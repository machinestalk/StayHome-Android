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
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.listeners.OnChooseListener;

public class ConfirmationDialog  extends BottomSheetDialog implements View.OnClickListener {

    private AppCompatImageView mDialogImageView;
    private Button btnYes;
    private Button btnNo;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private Context context;
    private OnChooseListener onClickListener;
    private View mTopBarView;
    BottomSheetBehavior bottomSheetBehavior;
    @SuppressLint("StaticFieldLeak")
    private static ConfirmationDialog instance;

    public static ConfirmationDialog getInstance(@NonNull Context context) {
        return instance == null ? new ConfirmationDialog(context) : instance;
    }

    public void setListener(OnChooseListener OnClickDialogListener) {
        this.onClickListener = OnClickDialogListener;
    }


    public ConfirmationDialog(@NonNull Context context) {
        super(context, R.style.CustomAlertDialog);
        this.context = context;
        create();
    }

    public void setCanceledOnTouchOutside(boolean canceled) {
        super.setCanceledOnTouchOutside(canceled);
    }


    public void create() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.confirmation_dialog_layout, null);
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

        mDialogImageView = bottomSheetView.findViewById(R.id.ivAvatar);
        tvTitle = bottomSheetView.findViewById(R.id.view_confirmation_dialog_title);
        tvSubTitle = bottomSheetView.findViewById(R.id.view_confirmation_dialog_sub_title);
        btnYes = bottomSheetView.findViewById(R.id.view_confirmation_dialog_positive_button);
        btnNo = bottomSheetView.findViewById(R.id.view_confirmation_dialog_negative_button);
        mTopBarView = bottomSheetView.findViewById(R.id.view_confirmation_dialog_top_bar);

        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
        setCanceledOnTouchOutside(false);
    }

    public void setTOpBarInvisible(){
        mTopBarView.setVisibility(View.GONE);
    }

    public void setDialogImageView(@DrawableRes int resId) {
        mDialogImageView.setVisibility(View.VISIBLE);
        mDialogImageView.setImageResource(resId);
    }

    public void setTitleInvisible() {
        tvTitle.setVisibility(View.GONE);
    }

    public void setTvTitle(String tvTitle) {
        this.tvTitle.setText(tvTitle);
    }

    public void setTvSubTitle(String tvSubTitle) {
        this.tvSubTitle.setText(tvSubTitle);
    }

    public void setTextBtnYes(String text) {
        btnYes.setText(text);
    }

    public void setTextBtnNo(String text) {
        btnNo.setText(text);
    }

    public void setTextBtnNoVisibility(boolean visibility) {
        if(visibility)
        btnNo.setVisibility(View.VISIBLE);
        else
            btnNo.setVisibility(View.GONE);
    }

    public void setTextBtnYesVisibility(boolean visibility) {
        if(visibility)
            btnYes.setVisibility(View.VISIBLE);
        else
            btnYes.setVisibility(View.GONE);
    }



    public void setNegativeButtonBackgroundDrawable(Drawable drawable){
        btnNo.setBackground(drawable);
    }

    public void sePositiveButtonBackgroundDrawable(Drawable drawable){
        btnYes.setBackground(drawable);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_confirmation_dialog_positive_button:
                onClickListener.onAccept();
                break;
            case R.id.view_confirmation_dialog_negative_button:
                onClickListener.onRefuse();
                break;
        }
    }

}