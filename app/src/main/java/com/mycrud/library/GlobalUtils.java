package com.mycrud.library;

import android.app.ProgressDialog;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GlobalUtils
{
    public static class GToast
    {
        private static Toast globalToast;

        public static void show(AppCompatActivity activity, String message)
        {
            if (globalToast != null)
                globalToast.cancel();

            globalToast = Toast.makeText(activity, message, android.widget.Toast.LENGTH_LONG);
            globalToast.show();
        }
    }

    public static class GProgressDialog
    {
        private static ProgressDialog globalProgress;

        public static void show(AppCompatActivity activity, String header, String message)
        {
            globalProgress = ProgressDialog.show(activity, header, message, false, false);
        }

        public static void dismiss()
        {
            globalProgress.dismiss();
        }
    }

    public static class GDevice
    {
        public static float scale;

        public static int getPixel(int dp)
        {
            return (int)(dp * scale + .5f);
        }
    }
}
