package com.zpauly.ams_pms_hook;

import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zpauly on 2016/12/3.
 */

public class PMSHookHelper {
    public static void hookPMS(Context context) {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object thread = currentActivityThread.invoke(null);

            Field sPackageManager = activityThread.getDeclaredField("sPackageManager");
            Object original = sPackageManager.get(thread);
            Class<?> IPMSClass = original.getClass();

            Object proxy = Proxy.newProxyInstance(thread.getClass().getClassLoader(),
                    new Class[]{IPMSClass},
                    new IPackageManagerHandler(original));

            sPackageManager.set(thread, proxy);

            Class<?> appliationClass = Class.forName("android.app.ApplicationPackageManager");
            Field mPm = appliationClass.getDeclaredField("mPM");
            mPm.setAccessible(true);
            mPm.set(context.getPackageManager(), proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
