package com.zpauly.service_hook;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zpauly on 2016/12/4.
 */

public class ServiceManager {
    private static ServiceManager instance;

    private ServiceManager() {

    }

    public static ServiceManager getInstance() {
        if (instance == null) {
            synchronized (ServiceManager.class) {
                if (instance == null) {
                    instance = new ServiceManager();
                }
            }
        }
        return instance;
    }

    private Map<String, Service> mServiceMap = new HashMap<>();

    private Map<ComponentName, ServiceInfo> mServiceInfoMap = new HashMap<>();

    public ServiceInfo selectPluginServiceInfo(Intent intent) {
        for (ComponentName name : mServiceInfoMap.keySet()) {
            if (intent.getComponent().getClassName().equals(name)) {
                return mServiceInfoMap.get(name);
            }
        }
        return null;
    }

    public void onStop(Intent targetIntent) {
        ServiceInfo serviceInfo = selectPluginServiceInfo(targetIntent);

        mServiceMap.get(serviceInfo.name).onDestroy();
        mServiceMap.remove(serviceInfo.name);
    }

    public void onStart(Intent proxyIntent, int startId) {
        Intent targetIntent = proxyIntent.getParcelableExtra(HookHelper.TARGET_SERVICE);
        ServiceInfo serviceInfo = selectPluginServiceInfo(targetIntent);

        if (serviceInfo == null) {
            try {
                Service service = createService();
                service.onStart(targetIntent, startId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (!mServiceMap.containsKey(serviceInfo.name)) {
            proxyCreateService(serviceInfo);
        }

        Service service = mServiceMap.get(serviceInfo.name);
        service.onStart(targetIntent, startId);
    }

    public void proxyCreateService(ServiceInfo serviceInfo) {
        serviceInfo.applicationInfo.packageName = "com.zpauly.service_hook";

        try {
            Service service = createService();

            mServiceMap.put(serviceInfo.name, service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Service createService() throws Exception {
        IBinder token = new Binder();

        Class<?> createServiceDataClass = Class.forName("android.app.ActivityThread$CreateServiceData");
        Constructor<?> constructor = createServiceDataClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object createServiceData = constructor.newInstance();

        Field tokenField = createServiceDataClass.getDeclaredField("token");
        tokenField.setAccessible(true);
        tokenField.set(createServiceData, token);

        Field infoField = createServiceDataClass.getDeclaredField("info");
        infoField.setAccessible(true);

        Class<?> compatibilityClass  = Class.forName("android.content.res.CompatibilityInfo");
        Field defaultCompatibilityField = compatibilityClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
        defaultCompatibilityField.setAccessible(true);
        Object defaultCompatibility = defaultCompatibilityField.get(null);
        Field compatInfoField = createServiceDataClass.getDeclaredField("compatInfo");
        compatInfoField.setAccessible(true);
        compatInfoField.set(createServiceData, defaultCompatibility);

        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);

        Method handleCreateServiceMethod = activityThreadClass.getDeclaredMethod("handleCreateService", createServiceDataClass);
        handleCreateServiceMethod.setAccessible(true);
        handleCreateServiceMethod.invoke(currentActivityThread, createServiceData);

        Field mServicesField = activityThreadClass.getDeclaredField("mServices");
        mServicesField.setAccessible(true);
        Map<?, ?> mServices = (Map<?, ?>) mServicesField.get(currentActivityThread);
        Service service = (Service) mServices.get(token);
        mServices.remove(token);
        return service;
    }
}
