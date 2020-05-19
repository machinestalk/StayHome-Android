package com.machinestalk.stayhome.views.base;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableString;

import androidx.annotation.StringRes;

import com.machinestalk.android.interfaces.Controller;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.activities.base.BaseActivity;
import com.machinestalk.stayhome.config.AppConfig;
import com.machinestalk.stayhome.dialogs.base.BaseDialog;
import com.machinestalk.stayhome.utils.CustomTypefaceSpan;

import java.util.Locale;

/**
 * Created on 12/20/16.
 */

public abstract class BaseView extends com.machinestalk.android.views.BaseView {

    public BaseView( Controller controller ) {
        super( controller );
    }

    @Override
    protected void setTitle( CharSequence title ) {
        SpannableString spannableString = new SpannableString( title );
        spannableString.setSpan( new CustomTypefaceSpan( controller.getBaseActivity(), getString( R.string.font_semi_bold ) ),
                0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

        super.setTitle( spannableString );
    }

    public void showDialog( BaseDialog baseDialog ) {
        ( ( BaseActivity ) getBaseActivity() ).showDialog( baseDialog );
    }

    @Override
    public String getString( @StringRes int resID ) {
        if ( getBaseActivity() == null ) {
            return "";
        }
        Configuration overrideConfiguration = getBaseActivity().getResources().getConfiguration();
        overrideConfiguration.setLocale( new Locale( AppConfig.getInstance().getLanguage() ) );
        Context   context   = ConnectedCar.getInstance().createConfigurationContext( overrideConfiguration );
        Resources resources = context.getResources();
        return resources.getString( resID );
    }
    public String getEnString( @StringRes int resID ) {
        if ( getBaseActivity() == null ) {
            return "";
        }
        Configuration overrideConfiguration = getBaseActivity().getResources().getConfiguration();
        overrideConfiguration.setLocale( new Locale("en") );
        Context   context   = ConnectedCar.getInstance().createConfigurationContext( overrideConfiguration );
        Resources resources = context.getResources();
        return resources.getString( resID );
    }
    public String getArString( @StringRes int resID ) {
        if ( getBaseActivity() == null ) {
            return "";
        }
        Configuration overrideConfiguration = getBaseActivity().getResources().getConfiguration();
        overrideConfiguration.setLocale( new Locale("ar") );
        Context   context   = ConnectedCar.getInstance().createConfigurationContext( overrideConfiguration );
        Resources resources = context.getResources();
        return resources.getString( resID );
    }
}
