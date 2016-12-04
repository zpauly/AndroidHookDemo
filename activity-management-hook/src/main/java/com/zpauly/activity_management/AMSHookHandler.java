package com.zpauly.activity_management;

import android.content.ComponentName;
import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zpauly on 2016/12/4.
 */

public class AMSHookHandler implements InvocationHandler {
    private static final String TAG = AMSHookHandler.class.getName();

    public static final String SECOND_ACTIVITY = "SECOND_ACTIVITY";
    private static final String PACKAGE_NAME = "com.zpauly.activity_management";

    private Object base;

    public AMSHookHandler(Object base) {
        this.base = base;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("startActivity".equals(method.getName())) {
            Intent originalIntent = null;
            int i = 0;
            for (; i < objects.length; i++) {
                if (objects[i] instanceof Intent) {
                    originalIntent = (Intent) objects[i];
                    break;
                }
            }

            Intent newIntent = new Intent();
            newIntent.setComponent(new ComponentName(PACKAGE_NAME, SecondActivity.class.getCanonicalName()));
            newIntent.putExtra(SECOND_ACTIVITY, originalIntent);
            objects[i] = newIntent;
        }
        return method.invoke(base, objects);
    }
}
