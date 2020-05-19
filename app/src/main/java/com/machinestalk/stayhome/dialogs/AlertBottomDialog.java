package com.machinestalk.stayhome.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.listeners.OnClickAlertListener;

public class AlertBottomDialog extends BottomSheetDialog implements View.OnClickListener {

    private ImageView ivAvatar;
    private Button btDone;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private Context context;
    private OnClickAlertListener onClickListener;
    BottomSheetBehavior bottomSheetBehavior;
    @SuppressLint("StaticFieldLeak")
    private static AlertBottomDialog instance;
    private View mTopBarView;
    private AppCompatImageView mSecondIconImageView;

    public static AlertBottomDialog getInstance(@NonNull Context context) {
        return instance == null ? new AlertBottomDialog(context) : instance;
    }

    public void setListener(OnClickAlertListener OnClickDialogListener) {
        this.onClickListener = OnClickDialogListener;
    }


    public AlertBottomDialog(@NonNull Context context) {
        super(context, R.style.CustomAlertDialog);
        this.context = context;
        create();
    }

    public void create() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.alert_dialog_layout, null);
        setContentView(bottomSheetView);
        bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        setWhiteNavigationBar(this);
        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // do something
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // do something
            }
        };


        ivAvatar = (ImageView) bottomSheetView.findViewById(R.id.ivAvatar);
        tvTitle = (TextView) bottomSheetView.findViewById(R.id.view_alert_dialog_title);
        tvSubTitle = (TextView) bottomSheetView.findViewById(R.id.view_alert_dialog_sub_title);
        btDone = (Button) bottomSheetView.findViewById(R.id.bt_done);
        mTopBarView = bottomSheetView.findViewById(R.id.view_alert_dialog_top_bar);
        mSecondIconImageView = bottomSheetView.findViewById(R.id.view_alert_dialog_second_icon);
        btDone.setOnClickListener(this);

        setCanceledOnTouchOutside(false);

    }

    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here
            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);
            Drawable[] layers = {dimDrawable, navigationBarDrawable};
            LayerDrawable windowBackground = new LayerDrawable(layers);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                windowBackground.setLayerInsetTop(1, metrics.heightPixels);
            }else{
                return;
            }
            window.setBackgroundDrawable(windowBackground);
        }
    }

    public void setTopBarInvisible(){
        mTopBarView.setVisibility(View.GONE);
    }


    public void setSecondIconVisible(int resource){
        mSecondIconImageView.setImageResource(resource);
        mSecondIconImageView.setVisibility(View.VISIBLE);
    }



    public void setCanceledOnTouchOutside(boolean canceled) {
        super.setCanceledOnTouchOutside(true);
    }
    public void setSpecificCanceledOnTouchOutside(boolean canceled) {
        super.setCanceledOnTouchOutside(canceled);
    }

    public void setIvAvatar(@DrawableRes int resId) {
        ivAvatar.setVisibility(View.VISIBLE);
        ivAvatar.setImageResource(resId);
    }

    public void setTvTitle(String tvTitle) {
        this.tvTitle.setVisibility(View.VISIBLE);
        this.tvTitle.setText(tvTitle);
    }

    public void setTvSubTitle(String tvSubTitle) {
        this.tvSubTitle.setVisibility(View.VISIBLE);
        this.tvSubTitle.setText(tvSubTitle);
    }

    public void setTitleColor(int color){
        this.tvTitle.setTextColor(ContextCompat.getColor(context, color));
    }

    public void setTextButton(String textButton) {
        btDone.setVisibility(View.GONE);
        btDone.setText(textButton);
    }

    public void setTextButtonVisibility(boolean visibility) {
        if (visibility)
        btDone.setVisibility(View.VISIBLE);
        else
            btDone.setVisibility(View.GONE);
    }
    public void setTextButtonInvisible() {

            btDone.setVisibility(View.INVISIBLE);
    }


    /**
     *
     * @param listener
     */
    public void setOnClickAlertListener(OnClickAlertListener listener){
        onClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_done:
                if (onClickListener != null) {
                    onClickListener.onAlertClick();
                }
                break;
        }
    }
}