package com.zpauly.binderhook;

import android.content.ClipData;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zpauly on 2016/12/3.
 */

public class BinderHookHandler implements InvocationHandler {
    private static final String TAG = BinderHookHandler.class.getName();

    private Object base;

    public BinderHookHandler(IBinder base, Class<?> stubClass) {
        try {
            Method asInterface = stubClass.getDeclaredMethod("asInterface", IBinder.class);
            //这里实际上是一个IClipInterface对象
            this.base = asInterface.invoke(null, base);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("getPrimaryClip".equals(method.getName())) {
            Log.i(TAG, "hook");
            return ClipData.newPlainText(null, "hooked");
        }

        if ("hasPrimaryClip".equals(method.getName())) {
            return true;
        }

        return method.invoke(base, objects);
    }
}
