package com.machinestalk.android.service;

import android.content.Context;
import androidx.annotation.NonNull;

import com.machinestalk.android.R;
import com.machinestalk.android.service.converters.ServiceConverterFactory;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("FeatureEnvy")
public final class ServiceManager {

    private static ServiceManager instance;
    private ServiceProtocol serviceProtocol;
    private Retrofit retrofit;
    private Context context;


    private ServiceManager() {

    }

    public static ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    public void initialize(ServiceProtocol serviceProtocol, Context context, boolean forceInit) {

        this.context = context;
        this.serviceProtocol = serviceProtocol;
        validateServiceProtocol();

        if (retrofit != null && !forceInit) {
            return;
        }

        OkHttpClient okHttpClient = getOkHttpClient();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://uat.thingstalk.io/")
                .client(okHttpClient)
                .addCallAdapterFactory(new ServiceCallAdapterFactory())
                .addConverterFactory(new ServiceConverterFactory())
                .addConverterFactory( GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetrofit() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://ci.thingstalk.io/")
                    .addCallAdapterFactory(new ServiceCallAdapterFactory())
                    .addConverterFactory(new ServiceConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    @NonNull
    public OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient.Builder builder = okHttpClient.newBuilder();

        if(!serviceProtocol.isSafeClient()) {
            builder.sslSocketFactory(getUnsafeSslFactory()).hostnameVerifier(getHostNameVerifier());
        }

        okHttpClient = builder
                .addNetworkInterceptor(serviceProtocol)
                .connectTimeout(serviceProtocol.getConnectionTimeoutInSeconds(), TimeUnit.SECONDS)
                .readTimeout(serviceProtocol.getReadTimeoutInSeconds(), TimeUnit.SECONDS)
                .writeTimeout(serviceProtocol.getWriteTimeoutInSeconds(), TimeUnit.SECONDS).build();
        return okHttpClient;
    }

    private HostnameVerifier getHostNameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    private SSLSocketFactory getUnsafeSslFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getUnsafeTrustManagers(), new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception ex) {
        }
        return null;
    }

    private TrustManager[] getUnsafeTrustManagers() {
        return new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
            @Override
            public void checkServerTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }
            @Override
            public void checkClientTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }
        }};
    }




    public <S> S loadService(Class<S> serviceClass) {

        try {
            validateServiceProtocol();
        } catch (Exception ex) {
            return null;
        }

        return retrofit.create(serviceClass);
    }

    public ServiceProtocol getServiceProtocol() {
        return serviceProtocol;
    }


    private void validateServiceProtocol() {
        if (serviceProtocol != null) {
            return;

        }
        throw new IllegalArgumentException(context.getString(R.string.error_message_service_protocol));
    }
}
