package com.example.otplogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.edit_mobile_verify)
    EditText edit_mobile_verify;
    @BindView(R.id.tv_you_no)
    TextView tv_you_no;

    String number="", otp, mPhoneNumber,last_Ten_digits="";
    GoogleApiClient mGoogleApiClient;
    private int RESOLVE_HINT = 2,otpvisit,booktestnext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);


        //set google api client for hint request
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        // get mobile number from phone
        getHintPhoneNumber();
    }

    private void getHintPhoneNumber() {
        HintRequest hintRequest =
                new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
        PendingIntent mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest);
        try {
            startIntentSenderForResult(mIntent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result if we want hint number
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    String aa = credential.getId();
                    if(aa.length()>10)
                        last_Ten_digits=aa.substring(aa.length()-10);
                    // credential.getId();  <-- will need to process phone number string
//                    edit_mobile_verify.setText(credential.getId());
                    edit_mobile_verify.setText(last_Ten_digits);
                }

            }
        }
    }

    @OnClick(R.id.btn_create_otp)
    public void btnCreateOtp(){

        number = edit_mobile_verify.getText().toString().trim();

        if(number.equalsIgnoreCase("")){
            edit_mobile_verify.setError("Please enter mobile number !!");
            edit_mobile_verify.requestFocus();
        }else if(number.length()!=10){
            edit_mobile_verify.setError("Please enter 10 digit number !!");
            edit_mobile_verify.requestFocus();
        }else {
            //===== create sms link ===========
            createOtp(number);
        }
    }


    private void createOtp(String mobileNo) {
        String val = "" + ((int) (Math.random() * 9000) + 1000);
        otp = val;

        Intent intent = new Intent(this, ConfirmOtpActivity.class);
        intent.putExtra("MOBILE", number);
        intent.putExtra("OTP", otp);
        startActivity(intent);
        finish();


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}