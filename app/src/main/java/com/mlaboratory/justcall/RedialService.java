package com.mlaboratory.justcall;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class RedialService extends Service {

    private MyPhoneStateListener mMyPhoneStateListener;
    private TelephonyManager mTelephonyManager;

    private static Handler mHandler;

    public static void setHandler(Handler mHandler) {
        RedialService.mHandler = mHandler;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String phoneNumber = "";
        String dialTimes = "0";

        Object object = intent.getExtras().get("PhoneNumber");
        if (object != null && object instanceof String) {
            phoneNumber = (String) object;
        }
        object = intent.getExtras().get("DialTimes");
        if (object != null && object instanceof String) {
            if (((String) object).length() > 0)
                dialTimes = (String) object;
        }

        mMyPhoneStateListener = new MyPhoneStateListener();
        Dialer dialer = new Dialer(this);
        dialer.setPhoneNumber(phoneNumber);
        mMyPhoneStateListener.setDialer(dialer);
        mMyPhoneStateListener.setDialTimes(Integer.valueOf(dialTimes));
        mMyPhoneStateListener.setHandler(mHandler);

        mTelephonyManager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTelephonyManager != null) {
            if (mMyPhoneStateListener != null) {
                mMyPhoneStateListener.cancelMyCountDownTimer();
                mTelephonyManager.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_NONE);
            }
            MyPhoneStateListener.setCounter(0);
        }
    }
}
