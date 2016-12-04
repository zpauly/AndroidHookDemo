package com.zpauly.service_hook;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by zpauly on 2016/12/4.
 */

public class TargetService extends Service {
    private static final String TAG = TargetService.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "target service create");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
