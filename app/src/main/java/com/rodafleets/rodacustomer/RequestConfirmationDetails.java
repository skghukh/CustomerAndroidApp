package com.rodafleets.rodacustomer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RequestConfirmationDetails extends ParentActivity {

    private TextView timer;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_confirmation_details);
        initComponents();
    }

    protected void initComponents() {
        super.initComponents();
        timer = (TextView) findViewById(R.id.timer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (null != timer) {
            addCountdownToTimer();
        }

    }

    private void addCountdownToTimer() {
        new CountDownTimer(45000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished / 1000);
                progressBar.setProgress(45- (int)(millisUntilFinished / 1000));
            }

            public void onFinish() {
                timer.setText("done!");
            }
        }.start();

    }

}
