package com.machinestalk.stayhome.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.machinestalk.android.utilities.PreferenceUtility;
import com.machinestalk.stayhome.R;

public class TermsConditionActivity extends AppCompatActivity {


    Button bt_agree;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_terms_condition);
        initViews();
        setListener();
        setupToolbar();
    }

    private void setupToolbar() {
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(this.getResources().getString(R.string.Gen_Land_lbl_terms_config));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initViews() {

        bt_agree = findViewById(R.id.bt_agree) ;
        toolbar = findViewById(R.id.toolbar);

    }

    private void setListener() {

        bt_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtility.getInstance(getApplicationContext()).putBoolean(PreferenceUtility.IS_LOGGED, true);
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                setResult(RESULT_OK);
                startActivity(intent);
                finish();
            }
        });

    }




    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

}
