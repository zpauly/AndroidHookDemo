package com.zpauly.classloader_hook;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zpauly on 2016/12/4.
 */

public class HookHelper {
    public static void classLoaderHook(ClassLoader classLoader, List<File> apkFileList, File outDexFile) {
        try {
            Field pathListField = findField(classLoader, "pathList");
            Object dexPathList = pathListField.get(classLoader);
            Method makeDexElementsMethod = classLoader.getClass().getDeclaredMethod("makeDexElements", new Class[]{ArrayList.class, File.class});
            makeDexElementsMethod.setAccessible(true);
            Object[] additionElements = (Object[]) makeDexElementsMethod.invoke(dexPathList, apkFileList, outDexFile);
            Field dexElementsField = findField(pathListField, "dexElements");
            dexElementsField.setAccessible(true);
            Object[] originalElements = (Object[]) dexElementsField.get(pathListField);
            Object[] newElements = (Object[]) Array.newInstance(originalElements.getClass().getComponentType(),
                    originalElements.length + additionElements.length);
            System.arraycopy(originalElements, 0, newElements, 0, originalElements.length);
            System.arraycopy(additionElements, 0, newElements, originalElements.length, originalElements.length + additionElements.length);
            dexElementsField.set(pathListField, newElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Field findField(Object instance, String name) {
        Class<?> clazz = instance.getClass();

        while (clazz != null) {
            try {
                Field e = clazz.getDeclaredField(name);
                e.setAccessible(true);
                return e;
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
