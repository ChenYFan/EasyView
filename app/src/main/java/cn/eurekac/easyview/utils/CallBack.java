package cn.eurekac.easyview.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CallBack {
    Object ownobj;
    Method execute;
    public CallBack(Object o, Method method) {
        ownobj = o;
        execute = method;
    }
    public Object invoke(Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return execute.invoke(ownobj, args);
    }
}