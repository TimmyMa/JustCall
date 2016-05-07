package com.mlaboratory.justcall;

import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {

    private static final String LOG_TAG = MyPhoneStateListener.class.getSimpleName();

    private Dialer mDialer;
    private int mDialTimes = -1;
    private static int counter = 0;
    private static int delay = 5;

    private boolean mIsFirstDial = true;

    private static Handler mHandler;
    private static MyCountDownTimer mMycouMyCountDownTimer;

    public static void setCounter(int counter) {
        MyPhoneStateListener.counter = counter;
    }

    public static void setHandler(Handler mHandler) {
        MyPhoneStateListener.mHandler = mHandler;
    }

    public void setDialer(Dialer mDialer) {
        this.mDialer = mDialer;
    }

    public void setDialTimes(int dialTimes) {
        this.mDialTimes = dialTimes;
    }

    public static void setDelay(int delay) {
        MyPhoneStateListener.delay = delay;
    }


    public void cancelMyCountDownTimer() {
        if (mMycouMyCountDownTimer != null)
            mMycouMyCountDownTimer.cancel();
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                Log.v(LOG_TAG, "CALL_STATE_IDLE");
                if (counter >= mDialTimes && mDialTimes != -1) {
                    mHandler.obtainMessage(MyCountDownTimer.FINAL_STATE).sendToTarget();
                    return;
                }
                if (mDialer != null && !mIsFirstDial) {
                    mMycouMyCountDownTimer = new MyCountDownTimer(5000, 1000, mHandler);
                    mMycouMyCountDownTimer.start();
                }
                mIsFirstDial = false;
                if (mDialTimes != -1) {
                    counter++;
                }
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.v(LOG_TAG, "CALL_STATE_RINGING");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.v(LOG_TAG, "CALL_STATE_OFFHOOK");
                break;
            default:
                break;
        }
        super.onCallStateChanged(state, incomingNumber);
    }
}
