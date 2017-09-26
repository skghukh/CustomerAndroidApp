package com.rodafleets.rodacustomer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class RequestConfirmationDetails extends AppCompatActivity {

    private TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_confirmation_details);
        initComponents();
    }

    private void initComponents() {
        timer = (TextView) findViewById(R.id.timer);
        if (null != timer) {
            addCountdownToTimer();
        }

    }

    private void addCountdownToTimer() {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timer.setText("done!");
            }
        }.start();

    }

}
