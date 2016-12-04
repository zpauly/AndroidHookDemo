package com.zpauly.ams_pms_hook;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Created by zpauly on 2016/12/3.
 */

public class AMSHookHelper {
    public static void hookAMS() {
        try {
            Class<?> activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField = activityManagerNative.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);

            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Class<?> IAMSClass = Class.forName("android.app.IActivityManager");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object originalAMS = mInstanceField.get(gDefault);
            Object newAMS = Proxy.newProxyInstance(originalAMS.getClass().getClassLoader(),
                    new Class[]{IAMSClass},
                    new IActivityManagerHandler(originalAMS));
            mInstanceField.set(gDefault, newAMS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
