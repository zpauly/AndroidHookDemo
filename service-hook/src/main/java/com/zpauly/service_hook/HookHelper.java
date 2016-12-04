package com.zpauly.service_hook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zpauly on 2016/12/4.
 */

public class HookHelper {
    public static final String TARGET_SERVICE = "TARGET_SERVICE";

    public static void hook() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);

            Class<?> activityManageNativeClass = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField = activityManageNativeClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);

            Object gDefault = gDefaultField.get(null);

            Class<?> singleton = Class.forName("android.util.Singleton");
            Field mInstanceField = singleton.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);

            Object original = mInstanceField.get(gDefault);

            Class<?> IActivityManager = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(currentActivityThread.getClass().getClassLoader(),
                    new Class[]{ IActivityManager },
                    new AMSHookHandler(original));
            mInstanceField.set(gDefault, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
