package com.ordersmanagement.crm.utils;

import org.slf4j.Logger;

public final class LoggerUtils {

    public static void logUserAction(Logger logger, String message) {
        logger.info(AuthUtils.LOGGED_EMPLOYEE.getName() + " " + message);
    }
}
