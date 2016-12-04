package com.zpauly.binderhook;

import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zpauly on 2016/12/3.
 */

public class BinderProxyHookHandler implements InvocationHandler {
    public static final String TAG = BinderProxyHookHandler.class.getName();

    private IBinder base;

    private Class<?> stubClass;

    private Class<?> iinterfaceClass;

    public BinderProxyHookHandler(IBinder base) {
        this.base = base;
        try {
            this.stubClass = Class.forName("android.content.IClipboard$Stub");
            this.iinterfaceClass = Class.forName("android.content.IClipboard");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if ("queryLocalInterface".equals(method.getName())) {
            Log.i(TAG, "hook");
            return Proxy.newProxyInstance(o.getClass().getClassLoader(),
                    new Class[] {IBinder.class, IInterface.class, this.iinterfaceClass},
                    new BinderHookHandler(base, stubClass));
        }
        return method.invoke(base, objects);
    }
}
