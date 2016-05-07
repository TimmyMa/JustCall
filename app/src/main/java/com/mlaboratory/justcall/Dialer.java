package com.mlaboratory.justcall;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Dialer {

    private Context mContext;
    private Intent mDialIntent;
    private String mPhoneNumber;

    public void setPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public Dialer (Context context) {
        mContext = context;
    }

    public void dial() {
        if (mDialIntent == null) {
            mDialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhoneNumber));
            mDialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(mDialIntent);

    }

}
