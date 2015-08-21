package com.mty.groupfuel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import java.util.List;

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

    public static AlertDialog.Builder createErrorAlert(List<String> list, String title, Context context) {
        return createErrorAlert(catString(list), title, context);
    }

    public static AlertDialog.Builder createErrorAlert(List<String> list, Context context) {
        return createErrorAlert(catString(list), context);
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

    private static String catString(List<String> list) {
        String result = "";
        for (String string : list) {
            result += string;
            result += "\n";
        }
        return result;
    }
}
