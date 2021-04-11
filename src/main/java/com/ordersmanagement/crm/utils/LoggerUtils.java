package com.ordersmanagement.crm.utils;

import org.slf4j.Logger;

public final class LoggerUtils {

    public static void logUserAction(Logger logger, String message) {
        if (AuthUtils.LOGGED_EMPLOYEE != null) {
            logger.info(AuthUtils.LOGGED_EMPLOYEE.getName() + " " + message);
        } else {
            logger.info(AuthUtils.LOGGED_USER.getFullName() + " " + message);
        }
    }
}
