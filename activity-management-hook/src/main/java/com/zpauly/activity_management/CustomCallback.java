package com.zpauly.activity_management;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by zpauly on 2016/12/4.
 */

public class CustomCallback implements Handler.Callback {
    private static final String TAG = CustomCallback.class.getName();

    private Handler base;

    public CustomCallback(Handler handler) {
        this.base = handler;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 100:
                Object obj = message.obj;
                try {
                    Field intentField = obj.getClass().getDeclaredField("intent");
                    intentField.setAccessible(true);
                    Intent originalTarget = (Intent) intentField.get(obj);
                    Intent targetIntent = originalTarget.getParcelableExtra(AMSHookHandler.SECOND_ACTIVITY);
                    originalTarget.setComponent(targetIntent.getComponent());
                    Log.i(TAG, originalTarget.getComponent().getClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        base.handleMessage(message);
        return true;
    }
}
