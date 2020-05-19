package com.machinestalk.stayhome.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.machinestalk.android.viewholders.BaseViewHolder;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.components.TextView;
import com.machinestalk.stayhome.constants.AppConstants;
import com.minew.beaconplus.sdk.MTPeripheral;

/**
 * Created on 4/20/2017.
 */

public class BraceletViewHolder extends BaseViewHolder<MTPeripheral> {

    Context controller;
    MTPeripheral entity;
    private TextView data;
    AppCompatImageView mAddBracelet;
    MultiSelector multiSelector;
    private boolean mIsSelected = false;
    private String lastBRaceltDetected;
    boolean isIbeaconFrameDetected = false;
    boolean isOtherbeaconDetected = false;
    boolean isSensorBeaconDetected = false;
    boolean isTLMBeaconDetected = false;
    boolean isURLBeaconDetected = false;


    public BraceletViewHolder(Activity controller, MultiSelector multiSelector, View itemView) {
        super(itemView, multiSelector);
        this.controller = controller;
        this.multiSelector = multiSelector;
        data = itemView.findViewById(R.id.view_item_beacon_address);
        mAddBracelet = itemView.findViewById(R.id.view_item_beacon_add_bracelet);
        mAddBracelet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MTPeripheral mtPeripheral = entity;
                if (mtPeripheral != null) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(AppConstants.BRACELET_MAC_ADDRESS, mtPeripheral.mMTFrameHandler.getMac());
                    controller.setResult(Activity.RESULT_OK, returnIntent);
                    controller.finish();
                    // addMacAddress(mtPeripheral.mMTFrameHandler.getMac());
                }
            }
        });
    }

    @Override
    public void bind(MTPeripheral entity) {
        this.entity = entity;
        setDataAndUi(entity);
    }


    @Override
    public boolean onLongClick(View view) {

        return false;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);


    }

    public void setDataAndUi(MTPeripheral mtPeripheral) {

        String mac = mtPeripheral.mMTFrameHandler.getMac();
//        int rssi = mtPeripheral.mMTFrameHandler.getRssi();
//        int battery = mtPeripheral.mMTFrameHandler.getBattery();
//
//        BluetoothEntity bluetoothEntity = new BluetoothEntity();
//        bluetoothEntity.setAdresse(mac);
//        bluetoothEntity.setRssi(String.valueOf(rssi));
//        bluetoothEntity.setBattery(String.valueOf(battery));
//
//        StringBuilder beaconData = new StringBuilder();
//        if ( name != null ) {
//            beaconData.append(String.format("Name: %s\n", name));
//            bluetoothEntity.setName(name);
//        }
//        beaconData.append(String.format(Locale.ENGLISH, "MAC: %s\nRSSI: %d\n", mac, rssi));
//        List<MinewFrame> advFrames = mtPeripheral.mMTFrameHandler.getAdvFrames();
//        for (int i = 0; i < advFrames.size(); i++) {
//            MinewFrame advFrame = advFrames.get(i);
//            FrameType frameType = advFrame.getFrameType();
////                || FrameType.FrameDeviceInfo.getValue() == frameType
//            if (i == 0) {
//                int txPower = advFrame.getAdvtxPower();
//                int radioTxPower = advFrame.getRadiotxPower();
//                bluetoothEntity.setRx(String.valueOf(radioTxPower));
//                bluetoothEntity.setTx(String.valueOf(radioTxPower));
//                bluetoothEntity.setTxPower(String.valueOf(txPower));
//                beaconData.append(String.format(Locale.ENGLISH, "txPower: %d\n", txPower));
//                beaconData.append(String.format(Locale.ENGLISH, "radioTxPower: %d\n", radioTxPower));
//                beaconData.append(String.format(Locale.ENGLISH, "radioTxPower: %d\n", radioTxPower));
//                beaconData.append(String.format(Locale.ENGLISH, "battery: %d\n", battery));
//            }
//            if (frameType == FrameiBeacon) {
//                beaconData.append("---- iBeacon ---- \n");
//                IBeaconFrame frame = (IBeaconFrame) advFrame;
//                beaconData.append("RSSI @ 1m: ").append(frame.getTxPower()).append("dBm\n");
//                beaconData.append("radioTxPower: ").append(frame.getRadiotxPower()).append("dBm\n");
//                beaconData.append("Major:").append(frame.getMajor()).append("\n");
//                beaconData.append("Minor:").append(frame.getMinor()).append("\n");
//                bluetoothEntity.setTx("" + frame.getRadiotxPower());
//                bluetoothEntity.setRx("" + frame.getRadiotxPower());
//                bluetoothEntity.setMinor("" + frame.getMinor());
//                bluetoothEntity.setMajor("" + frame.getMajor());
//                bluetoothEntity.setFrameName("FrameiBeacon");
//                isIbeaconFrameDetected = true ;
//
//            } else if (frameType == FrameAccSensor) {
//                beaconData.append("---- AccSensor ----\n");
//                AccFrame frame = (AccFrame) advFrame;
//                beaconData.append("X: ").append(frame.getXAxis()).append("\n");
//                beaconData.append("Y: ").append(frame.getYAxis()).append("\n");
//                beaconData.append("Z: ").append(frame.getZAxis()).append("\n");
//                beaconData.append("radioTxPower: ").append(frame.getRadiotxPower()).append("dBm\n");
//                bluetoothEntity.setX("" + frame.getXAxis());
//                bluetoothEntity.setY("" + frame.getYAxis());
//                bluetoothEntity.setZ("" + frame.getZAxis());
//                bluetoothEntity.setFrameName("FrameAccSensor");
//                isSensorBeaconDetected = true ;
//
//            } else if (frameType == FrameTLM) {
//                beaconData.append("---- TLM ----\n");
//                TlmFrame frame = (TlmFrame) advFrame;
//                beaconData.append("Temperature: ").append(frame.getTemperature()).append("\n");
//                beaconData.append("radioTxPower: ").append(frame.getRadiotxPower()).append("dBm\n");
//                bluetoothEntity.setRx("" + frame.getRadiotxPower());
//                bluetoothEntity.setTemperature("" + frame.getTemperature());
//                bluetoothEntity.setFrameName("FrameTLM");
//                isTLMBeaconDetected = true ;
//
//            } else if (frameType == FrameURL) {
//                beaconData.append("---- URL ----\n");
//                UrlFrame frame = (UrlFrame) advFrame;
//                beaconData.append("RSSI @ 0m: ").append(frame.getTxPower()).append("dBm\n");
//                beaconData.append("URL: ").append(frame.getUrlString()).append("\n");
//                bluetoothEntity.setUrl("" + frame.getUrlString());
//                bluetoothEntity.setRssi("" + frame.getTxPower());
//                bluetoothEntity.setFrameName("FrameURL");
//                isURLBeaconDetected = true ;
//                if ( !bluetoothEntity.getAdresse().equals(lastBRaceltDetected)) {
//                    Util.showBluetoothList(controller, bluetoothEntity);
//                }
//                lastBRaceltDetected = bluetoothEntity.getAdresse();
//
//
//            } else {
//                for (Map.Entry<String, String> entry : advFrame.getMap().entrySet()) {
//                    if (entry.getKey().equals("NameSpace ID")) {
//                        bluetoothEntity.setNamespace(String.valueOf(entry.getValue()));
//                    } else if (entry.getKey().equals("Instance ID")) {
//                        bluetoothEntity.setInstanceID(String.valueOf(entry.getValue()));
//                    }
//                    beaconData.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
//                }
//                bluetoothEntity.setFrameName("Bracelet Frame");
//                isOtherbeaconDetected = true ;
//
//            }
//            beaconData.append("---------\n");
            data.setText(mac);
//        }

    }


}
