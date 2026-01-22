package com.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class logTest {
    //Initialize Log4j instance


    private static Logger getLogger() {
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        return LogManager.getLogger(className);
    }
    //Info Level Logs
    public static void info(String message) {
        getLogger().info(message);
    }

    public static void info(Object object) {
        getLogger().info(object);
    }

    //Warn Level Logs
    public static void warn(String message) {
        getLogger().warn(message);
    }

    public static void warn(Object object) {
        getLogger().warn(object);
    }

    //Error Level Logs
    public static void error(String message) {
        getLogger().error(message);
    }

    public static void error(Object object) {
        getLogger().error(object);
    }

    //Fatal Level Logs
    public static void fatal(String message) {
        getLogger().fatal(message);
    }

    //Debug Level Logs
    public static void debug(String message) {
        getLogger().debug(message);
    }

    public static void debug(Object object) {
        getLogger().debug(object);
    }
}