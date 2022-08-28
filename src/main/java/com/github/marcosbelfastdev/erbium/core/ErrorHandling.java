package com.github.marcosbelfastdev.erbium.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ErrorHandling {

    public static void end(Class<?> exceptionClass, Throwable exception, String message) throws Throwable {
        printMessages(exception, message);
        throw initClass(exceptionClass);
    }

    public static void end(Class<?> exceptionClass, String message) throws Throwable {
        printMessage(message);
        throw initClass(exceptionClass);
    }

    public static void end(Class<?> exceptionClass) throws Throwable {
        throw initClass(exceptionClass);
    }

    public static void alert(Class<?> exceptionClass) {
        Throwable throwable = initClass(exceptionClass);
        printMessage(throwable.getMessage());
    }

    private static void printMessages(Throwable exception, String message) {
        System.out.println(exception.getMessage());
        System.out.println(message);
    }

    private static void printMessages(Exception exception) {
        System.out.println(exception.getMessage());
    }

    private static void printMessage(String message) {
        System.out.println(message);
    }

    private static Throwable initClass(Class<?> clazz) {
        Object object ;
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        try {
            object = constructors[0].newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return (Throwable) object;
    }

}
