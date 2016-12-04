package com.zpauly.ams_pms_hook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zpauly on 2016/12/3.
 */

public class IPackageManagerHandler implements InvocationHandler {
    public static final String TAG = IPackageManagerHandler.class.getName();

    private Object base;

    public IPackageManagerHandler(Object base) {
        this.base = base;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return method.invoke(base, objects);
    }
}
