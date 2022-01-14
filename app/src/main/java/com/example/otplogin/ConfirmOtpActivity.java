package com.example.otplogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmOtpActivity extends AppCompatActivity {

    private static final long START_TIME_IN_MILLIS = 60000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    //    @BindView(R.id.edit_confirm_otp) EditText edit_confirm_otp;
    @BindView(R.id.pinview_edit_confirm_otp)
    Pinview pinview_edit_confirm_otp;
    @BindView(R.id.tv_view_number)
    TextView tv_view_number;
    @BindView(R.id.tv_resend_sms) TextView tv_resend_sms;
    @BindView(R.id.tv_timer_time) TextView tv_timer_time;

    @BindView(R.id.view_otp)TextView view_otp;

    String number,otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_otp);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        number = intent.getStringExtra("MOBILE");
        otp = intent.getStringExtra("OTP");

        tv_view_number.setText("+91 - "+number);

        view_otp.setText(""+otp);

        reStartTime();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                // message is the fetching OTP
                pinview_edit_confirm_otp.setValue(message);//setText(message);
            }
        }
    };

    private void createOtp() {
        String val = "" + ((int) (Math.random() * 9000) + 1000);
        otp = val;
        view_otp.setText(""+otp);
    }

    @OnClick(R.id.tv_resend_sms)
    public void ResendOtp(){
//        pinview_edit_confirm_otp.setValue("");//setText(message);
        createOtp();
        reStartTime();
        tv_resend_sms.setVisibility(View.GONE);
    }

    private void reStartTime() {
        mTimeLeftInMillis=60000;
        //Timer
        /* CountDownTimer mCountDownTimer =*/
        new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                mTimeLeftInMillis = millisUntilFinished;
                int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
                int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

                tv_timer_time.setText(timeLeftFormatted+" Secs");
            }

            @Override
            public void onFinish() {
                tv_resend_sms.setVisibility(View.VISIBLE);
            }
        }.start();
    }


    @OnClick(R.id.btn_confirm_save_otp)
    public void confirmOtp(){
        String typedOtp = pinview_edit_confirm_otp.getValue();//getText().toString().trim();
        if (typedOtp.equalsIgnoreCase(otp)) {
//            showToast("OTP is matched !!");

            showToast("OTP matched !!");


        } else {
            showToast("OTP not matched. Try again !!");
        }
    }

    private void showToast(String str) {
        Toast.makeText(this, ""+str, Toast.LENGTH_SHORT).show();
    }
}