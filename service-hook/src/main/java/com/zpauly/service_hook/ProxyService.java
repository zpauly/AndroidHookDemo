package com.zpauly.service_hook;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by zpauly on 2016/12/4.
 */

public class ProxyService extends Service {
    private static final String TAG = ProxyService.class.getName();

    @Override
    public void onCreate() {
        Log.i(TAG, "proxy service create");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "proxy service start");

        ServiceManager.getInstance().onStart(intent, startId);
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "proxy service destroy");

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
