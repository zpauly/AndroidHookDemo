package com.zpauly.binderhook;

import android.os.IBinder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by zpauly on 2016/12/3.
 */

public class HookHelper {
    public static final String CLIPBOARD_SERVICE = "clipboard";
    public static final String SERVICE_MANAGER_NAME = "android.os.ServiceManager";

    public static void hookClipboardService() throws Exception {
        Class<?> serviceManager = Class.forName(SERVICE_MANAGER_NAME);
        Method getService = serviceManager.getDeclaredMethod("getService", String.class);
        getService.setAccessible(true);

        IBinder original = (IBinder) getService.invoke(null, CLIPBOARD_SERVICE);
        IBinder hooked = (IBinder) Proxy.newProxyInstance(serviceManager.getClassLoader(),
                new Class[] { IBinder.class },
                new BinderProxyHookHandler(original));

        Field cachedField = serviceManager.getDeclaredField("sCache");
        cachedField.setAccessible(true);
        Map<String, IBinder> cache = (Map<String, IBinder>) cachedField.get(null);
        cache.put(CLIPBOARD_SERVICE, hooked);
    }
}
