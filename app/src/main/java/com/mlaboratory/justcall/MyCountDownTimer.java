package com.mlaboratory.justcall;

import android.os.CountDownTimer;
import android.os.Handler;

public class MyCountDownTimer extends CountDownTimer{

    private static Handler mHandler;
    public static final int IN_RUNNING = 1001;
    public static final int END_RUNNING = 1002;
    public static final int FINAL_STATE = 1003;


    public MyCountDownTimer(long millisInFuture, long countDownInterval, Handler handler) {
        super(millisInFuture, countDownInterval);
        mHandler = handler;
    }

    @Override
    public void onFinish() {
        if (mHandler != null)
            mHandler.obtainMessage(END_RUNNING, R.string.button_redialing).sendToTarget();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (mHandler != null)
            mHandler.obtainMessage(IN_RUNNING,
                    (millisUntilFinished / 1000)).sendToTarget();
    }
}
