package com.machinestalk.stayhome.activities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.entities.BluetoothEntity;
import com.machinestalk.stayhome.utils.Util;
import com.minew.beaconplus.sdk.MTPeripheral;
import com.minew.beaconplus.sdk.enums.FrameType;
import com.minew.beaconplus.sdk.frames.AccFrame;
import com.minew.beaconplus.sdk.frames.IBeaconFrame;
import com.minew.beaconplus.sdk.frames.MinewFrame;
import com.minew.beaconplus.sdk.frames.TlmFrame;
import com.minew.beaconplus.sdk.frames.UrlFrame;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.minew.beaconplus.sdk.enums.FrameType.FrameAccSensor;
import static com.minew.beaconplus.sdk.enums.FrameType.FrameTLM;
import static com.minew.beaconplus.sdk.enums.FrameType.FrameURL;
import static com.minew.beaconplus.sdk.enums.FrameType.FrameiBeacon;


public class RecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MTPeripheral> mData;
    private String lastBleDetected;
    private Context mContext;
    boolean isIbeaconFrameDetected = false;
    boolean isOtherbeaconDetected = false;
    boolean isSensorBeaconDetected = false;
    boolean isTLMBeaconDetected = false;
    boolean isURLBeaconDetected = false;
    private String lastBRaceltDetected;



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        viewHolder = new ViewHolder(View.inflate(parent.getContext(), R.layout.item_beacon_recyclerview, null));
        return viewHolder;
    }

    public MTPeripheral getData(int position) {
        return mData.get(position);
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

        void onAddItemClick(View view, int position);
    }


    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).setDataAndUi(mData.get(position));

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
            ((ViewHolder) holder).mAddBracelet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onAddItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(Context context, List<MTPeripheral> data) {
//        clearList();
        if (mData!= null)
        Log.i("old list"+mData.size(),"");
        mData = data;
        this.mContext = context ;
        notifyDataSetChanged();
    }

    public void clearList (){
        if (mData != null) {
            mData.clear();
            notifyDataSetChanged();
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView data;
        AppCompatImageView mAddBracelet;

        public ViewHolder(View itemView) {
            super(itemView);
            data = itemView.findViewById(R.id.view_item_beacon_address);
            mAddBracelet = itemView.findViewById(R.id.view_item_beacon_add_bracelet);
        }

        public void setDataAndUi(MTPeripheral mtPeripheral) {

            String name = mtPeripheral.mMTFrameHandler.getName();
            String mac = mtPeripheral.mMTFrameHandler.getMac();
            int rssi = mtPeripheral.mMTFrameHandler.getRssi();
            int battery = mtPeripheral.mMTFrameHandler.getBattery();

            BluetoothEntity bluetoothEntity = new BluetoothEntity();
            bluetoothEntity.setAdresse(mac);
            bluetoothEntity.setRssi(String.valueOf(rssi));
            bluetoothEntity.setBattery(String.valueOf(battery));

            StringBuilder beaconData = new StringBuilder();
            if ( name != null ) {
                beaconData.append(String.format("Name: %s\n", name));
                bluetoothEntity.setName(name);
            }
            beaconData.append(String.format(Locale.ENGLISH, "MAC: %s\nRSSI: %d\n", mac, rssi));
            List<MinewFrame> advFrames = mtPeripheral.mMTFrameHandler.getAdvFrames();
            for (int i = 0; i < advFrames.size(); i++) {
                MinewFrame advFrame = advFrames.get(i);
                FrameType frameType = advFrame.getFrameType();
//                || FrameType.FrameDeviceInfo.getValue() == frameType
                if (i == 0) {
                    int txPower = advFrame.getAdvtxPower();
                    int radioTxPower = advFrame.getRadiotxPower();
                    double accuracy = calculateDistance(radioTxPower, rssi);
                    bluetoothEntity.setRx(String.valueOf(radioTxPower));
                    bluetoothEntity.setTx(String.valueOf(radioTxPower));
                    bluetoothEntity.setTxPower(String.valueOf(txPower));
                    beaconData.append(String.format(Locale.ENGLISH, "txPower: %d\n", txPower));
                    beaconData.append(String.format(Locale.ENGLISH, "radioTxPower: %d\n", radioTxPower));
                    beaconData.append(String.format(Locale.ENGLISH, "radioTxPower: %d\n", radioTxPower));
                    beaconData.append(String.format(Locale.ENGLISH, "battery: %d\n", battery));
                    beaconData.append(String.format(Locale.ENGLISH, "accuracy: %s\n", "" + ((float) accuracy)));
                }
                if (frameType == FrameiBeacon) {
                    beaconData.append("---- iBeacon ---- \n");
                    IBeaconFrame frame = (IBeaconFrame) advFrame;
                    beaconData.append("RSSI @ 1m: ").append(frame.getTxPower()).append("dBm\n");
                    beaconData.append("radioTxPower: ").append(frame.getRadiotxPower()).append("dBm\n");
                    beaconData.append("Major:").append(frame.getMajor()).append("\n");
                    beaconData.append("Minor:").append(frame.getMinor()).append("\n");
                    bluetoothEntity.setTx("" + frame.getRadiotxPower());
                    bluetoothEntity.setRx("" + frame.getRadiotxPower());
                    bluetoothEntity.setMinor("" + frame.getMinor());
                    bluetoothEntity.setMajor("" + frame.getMajor());
                    bluetoothEntity.setFrameName("FrameiBeacon");
                    isIbeaconFrameDetected = true ;

                    if ( !bluetoothEntity.getAdresse().equals(lastBRaceltDetected)) {
                        Util.showBluetoothList(mContext, bluetoothEntity);
                    }
                    lastBRaceltDetected = bluetoothEntity.getAdresse();

                } else if (frameType == FrameAccSensor) {
                    beaconData.append("---- AccSensor ----\n");
                    AccFrame frame = (AccFrame) advFrame;
                    beaconData.append("X: ").append(frame.getXAxis()).append("\n");
                    beaconData.append("Y: ").append(frame.getYAxis()).append("\n");
                    beaconData.append("Z: ").append(frame.getZAxis()).append("\n");
                    beaconData.append("radioTxPower: ").append(frame.getRadiotxPower()).append("dBm\n");
                    bluetoothEntity.setX("" + frame.getXAxis());
                    bluetoothEntity.setY("" + frame.getYAxis());
                    bluetoothEntity.setZ("" + frame.getZAxis());
                    bluetoothEntity.setFrameName("FrameAccSensor");
                    isSensorBeaconDetected = true ;

                    if ( !bluetoothEntity.getAdresse().equals(lastBRaceltDetected)) {
                        Util.showBluetoothList(mContext, bluetoothEntity);
                    }
                    lastBRaceltDetected = bluetoothEntity.getAdresse();


                } else if (frameType == FrameTLM) {
                    beaconData.append("---- TLM ----\n");
                    TlmFrame frame = (TlmFrame) advFrame;
                    beaconData.append("Temperature: ").append(frame.getTemperature()).append("\n");
                    beaconData.append("radioTxPower: ").append(frame.getRadiotxPower()).append("dBm\n");
                    bluetoothEntity.setRx("" + frame.getRadiotxPower());
                    bluetoothEntity.setTemperature("" + frame.getTemperature());
                    bluetoothEntity.setFrameName("FrameTLM");
                    isTLMBeaconDetected = true ;

                    if ( !bluetoothEntity.getAdresse().equals(lastBRaceltDetected)) {
                        Util.showBluetoothList(mContext, bluetoothEntity);
                    }
                    lastBRaceltDetected = bluetoothEntity.getAdresse();

                } else if (frameType == FrameURL) {
                    beaconData.append("---- URL ----\n");
                    UrlFrame frame = (UrlFrame) advFrame;
                    beaconData.append("RSSI @ 0m: ").append(frame.getTxPower()).append("dBm\n");
                    beaconData.append("URL: ").append(frame.getUrlString()).append("\n");
                    bluetoothEntity.setUrl("" + frame.getUrlString());
                    bluetoothEntity.setRssi("" + frame.getTxPower());
                    bluetoothEntity.setFrameName("FrameURL");
                    isURLBeaconDetected = true ;
                    if ( !bluetoothEntity.getAdresse().equals(lastBRaceltDetected)) {
                        Util.showBluetoothList(mContext, bluetoothEntity);
                    }
                    lastBRaceltDetected = bluetoothEntity.getAdresse();


                } else {
                    for (Map.Entry<String, String> entry : advFrame.getMap().entrySet()) {
                        if (entry.getKey().equals("NameSpace ID")) {
                            bluetoothEntity.setNamespace(String.valueOf(entry.getValue()));
                        } else if (entry.getKey().equals("Instance ID")) {
                            bluetoothEntity.setInstanceID(String.valueOf(entry.getValue()));
                        }
                        beaconData.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
                    }
                    bluetoothEntity.setFrameName("Bracelet Frame");
                    isOtherbeaconDetected = true ;

                    if ( !bluetoothEntity.getAdresse().equals(lastBRaceltDetected)) {
                        Util.showBluetoothList(mContext, bluetoothEntity);
                    }
                    lastBRaceltDetected = bluetoothEntity.getAdresse();

                }
                beaconData.append("---------\n");
                data.setText(mac);
            }

        }

    }


    double getDistance(int rssi, int txPower) {
        return Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
    }



     private double calculateDistance(float txPower, double rssi) {

        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi * 1.0 / txPower;

        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }



}
