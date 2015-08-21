package com.mty.groupfuel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class Alerter {

    public static AlertDialog.Builder createErrorAlert(String message, String title, Context context) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
    }

    public static AlertDialog.Builder createErrorAlert(String message, Context context) {
        return new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
    }

    public static AlertDialog.Builder createErrorAlert(Throwable throwable, Context context) {
        Log.e(context.getClass().getSimpleName(), throwable.getMessage(), throwable);
        return createErrorAlert(throwable.getMessage(), context);
    }
}
