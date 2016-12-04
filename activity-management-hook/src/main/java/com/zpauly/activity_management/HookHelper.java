package com.zpauly.activity_management;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zpauly on 2016/12/4.
 */

public class HookHelper {
    private static final String TAG = HookHelper.class.getName();

    public static void hookAMS() {
        try {
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);

            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Class<?> IActivityManagerClass = Class.forName("android.app.IActivityManager");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object originalInstance = mInstanceField.get(gDefault);
            Object proxyInstance = Proxy.newProxyInstance(singletonClass.getClassLoader(),
                    new Class[]{ IActivityManagerClass },
                    new AMSHookHandler(originalInstance));
            mInstanceField.set(gDefault, proxyInstance);


            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThreadMethod.setAccessible(true);
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            Field mHField = activityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Object mH = mHField.get(currentActivityThread);

            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            CustomCallback newCallback = new CustomCallback((Handler) mH);
            mCallbackField.set(mH, newCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
