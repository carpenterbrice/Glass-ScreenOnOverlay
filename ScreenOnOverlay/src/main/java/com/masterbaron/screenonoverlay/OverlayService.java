package com.masterbaron.screenonoverlay;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Van Etten on 12/8/13.
 */
public class OverlayService extends Service {
    String TAG = ConfigActivity.class.getSimpleName();

    private WindowManager windowManager;
    private View overlayView;
    private WindowManager.LayoutParams params;
    private Animation animation;
    private boolean overlayShown = false;

    private View detailsView;
    private TextView dateView;
    private TextView batteryView;

    private BroadcastReceiver screenReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        overlayView = inflater.inflate(R.layout.main, null);
        detailsView = overlayView.findViewById(R.id.details);
        detailsView.setBackgroundColor(Color.argb(175, 0, 0, 0));
        dateView = (TextView) detailsView.findViewById(R.id.textDate);
        batteryView = (TextView) detailsView.findViewById(R.id.textBattery);

        animation = getAnimator();
        params = buildOverlayLayout();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenReceiver = getScreenReceiver(), filter);

        showThenHide(this);
    }

    @Override
    public void onDestroy() {
        hideOverlay();
        unregisterReceiver(screenReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public BroadcastReceiver getScreenReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "BroadcastReceiver.onReceive(ACTION_SCREEN_ON)");
                showThenHide(context);
            }
        };
    }

    private void showThenHide(Context context) {
        Calendar cal = Calendar.getInstance();
        dateView.setText(DateUtils.formatDateTime(context, cal.getTimeInMillis(), DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE));

        Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        double level = batteryIntent.getIntExtra("level", -1);
        double scale = batteryIntent.getIntExtra("scale", -1);
        int vol = 0;
        if (level > 0 && scale > 0) {
            vol = (int)(level / scale * 100);
        }
        batteryView.setText( context.getString(R.string.battery, vol));

        showOverlay();
        detailsView.startAnimation(animation);
    }

    private Animation getAnimator() {
        Animation upAndOut = AnimationUtils.loadAnimation(this, R.anim.up_and_out);
        upAndOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "onAnimationEnd()");
                hideOverlay();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return upAndOut;
    }

    public static class BootReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context, OverlayService.class));
        }
    }

    private void hideOverlay() {
        if ( overlayShown ) {
            overlayShown = false;
            windowManager.removeView(overlayView);
        }
    }

    private void showOverlay() {
        if ( !overlayShown ) {
            overlayShown = true;
            windowManager.addView(overlayView, params);
        }
    }

    private WindowManager.LayoutParams buildOverlayLayout() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 0, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL ;

        return layoutParams;
    }
}