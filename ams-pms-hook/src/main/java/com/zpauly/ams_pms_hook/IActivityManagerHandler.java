package com.zpauly.ams_pms_hook;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zpauly on 2016/12/3.
 */

public class IActivityManagerHandler implements InvocationHandler {
    public static final String TAG = IActivityManagerHandler.class.getName();

    private Object base;

    public IActivityManagerHandler(Object base) {
        this.base = base;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("startActivity".equals(method.getName())) {
            Log.i(TAG, "hook before start activity");
        }
        return method.invoke(base, objects);
    }
}
