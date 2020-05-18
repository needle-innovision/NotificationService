package com.fms.notification.service;

import org.joda.time.DateTime;

import java.sql.Timestamp;

public interface DateService {
    /**
     * @return current date at the moment of the call
     */
    DateTime now();

    Timestamp current();
}
