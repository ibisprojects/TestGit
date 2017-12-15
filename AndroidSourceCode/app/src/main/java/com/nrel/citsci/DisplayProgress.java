package com.nrel.citsci;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Manoj on 1/25/2015.
 */
public class DisplayProgress {

    private static ProgressDialog mProgressDialog;


    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeMessage(String message){
        try {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.setMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
