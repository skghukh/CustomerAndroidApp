package com.rodafleets.rodacustomer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RequestConfirmationDetails extends ParentActivity {

    private TextView timer;
    private ProgressBar progressBar;
    private ImageView bookingConfirmationImage;
    private CountDownTimer progressBarTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_confirmation_details);
        initComponents();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            showDriverDetails(intent);
        }
    };

    private void showDriverDetails(Intent intent) {
        final String driverName = intent.getStringExtra("driverName");
        final String requestId = intent.getStringExtra("requestId");
        final String bid = intent.getStringExtra("bid");
        final String amount = intent.getStringExtra("Amount");
        if (null != progressBarTimer) {
            progressBarTimer.cancel();
            timer.setVisibility(View.INVISIBLE);
        }
        progressBar.setProgress(45);
        bookingConfirmationImage.setVisibility(View.VISIBLE);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent data = new Intent();
                data.putExtra("driverName", driverName);
                data.putExtra("requestId", requestId);
                data.putExtra("bid", bid);
                data.putExtra("amount", amount);
                setResult(RESULT_OK, data);
                finish();
            }
        }, 100);
    }

    protected void initComponents() {
        super.initComponents();
        timer = (TextView) findViewById(R.id.timer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bookingConfirmationImage = (ImageView) findViewById(R.id.booking_confirmed_imageview);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Request_Accepted"));
        if (null != timer) {
            addCountdownToTimer();
        }
    }

    private void addCountdownToTimer() {
        progressBarTimer = new CountDownTimer(45000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished / 1000);
                progressBar.setProgress(45 - (int) (millisUntilFinished / 1000));
            }

            public void onFinish() {
                timer.setText("Oops! No Response");
            }
        }.start();

    }

}
