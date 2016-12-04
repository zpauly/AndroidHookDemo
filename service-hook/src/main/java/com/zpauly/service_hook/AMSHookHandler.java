package com.zpauly.service_hook;

import android.content.ComponentName;
import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zpauly on 2016/12/4.
 */

public class AMSHookHandler implements InvocationHandler {
    private Object base;

    public AMSHookHandler(Object base) {
        this.base = base;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("startService".equals(method.getName())) {
            int i = 0;
            Intent intent;
            for (; i < objects.length; i ++) {
                if (objects[i] instanceof Intent) {
                    break;
                }
            }
            intent = (Intent) objects[i];
            Intent newIntent = new Intent();
            String package_name = "com.zpauly.service_hook";
            ComponentName componentName = new ComponentName(package_name, ProxyService.class.getCanonicalName());
            newIntent.setComponent(componentName);
            newIntent.putExtra(HookHelper.TARGET_SERVICE, intent);
            objects[i] = newIntent;
        }
        return method.invoke(base, objects);
    }
}
