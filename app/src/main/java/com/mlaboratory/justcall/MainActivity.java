package com.mlaboratory.justcall;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button mBtnStartDial;
    private Button mBtnStopDial;
    private EditText mEdtTxtPhoneNumber;
    private EditText mEdtTxtDialTimes;

    private Dialer mDialer;
    private Intent mIntent;

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnStartDial = (Button) findViewById(R.id.btnStartDial);
        mBtnStopDial = (Button) findViewById(R.id.btnStopDial);
        mEdtTxtPhoneNumber = (EditText) findViewById(R.id.edtTxtPhoneNumber);
        mEdtTxtDialTimes = (EditText) findViewById(R.id.edtTxtDialTimes);
        mIntent = new Intent(this, RedialService.class);

        mBtnStartDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStartDialOnClick(v);
            }
        });
        mBtnStopDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStopDialOnClick(v);
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopService(mIntent);
        super.onDestroy();
    }

    public void btnStartDialOnClick(View v) {
        if (mEdtTxtPhoneNumber.getText().length() <= 0) {
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mEdtTxtDialTimes.getText().length() <= 0) {
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mDialer == null) {
            mDialer = new Dialer(this);
        }
        mDialer.setPhoneNumber(mEdtTxtPhoneNumber.getText().toString());

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CALL_PHONE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(R.string.alert_dialog_request_permission_msg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else {
            mBtnStartDial.setEnabled(false);
            mDialer.dial();

            MyPhoneStateListener.setCounter(0);

            RedialService.setHandler(mCodeHandler);
            mIntent.putExtra("PhoneNumber", mEdtTxtPhoneNumber.getText().toString());
            mIntent.putExtra("DialTimes", mEdtTxtDialTimes.getText().toString());

            stopService(mIntent);
            startService(mIntent);
        }
    }

    public void btnStopDialOnClick(View v) {
        stopService(mIntent);
        initBtnStartDial();
    }

    public void initBtnStartDial() {
        mBtnStartDial.setEnabled(true);
        mBtnStartDial.setText(R.string.button_start_dial);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mDialer.dial();
                    mBtnStartDial.setEnabled(false);

                    RedialService.setHandler(mCodeHandler);
                    mIntent.putExtra("PhoneNumber", mEdtTxtPhoneNumber.getText().toString());
                    mIntent.putExtra("DialTimes", mEdtTxtDialTimes.getText().toString());

                    stopService(mIntent);
                    startService(mIntent);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "CALL_PHONE permission denied.", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler mCodeHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MyCountDownTimer.IN_RUNNING) {// 正在倒计时
                mBtnStartDial.setText(getString(R.string.button_redial_count_down, msg.obj.toString()));
            } else if (msg.what == MyCountDownTimer.END_RUNNING) {// 完成倒计时
                mBtnStartDial.setText(getString(R.string.button_redialing));
                mDialer.dial();
            } else if (msg.what == MyCountDownTimer.FINAL_STATE) {
                initBtnStartDial();
            }
        }
    };
}
