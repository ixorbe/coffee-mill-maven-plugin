package org.nanoko.java;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;

import org.apache.commons.logging.Log;

/**
 * Wrap the Maven logger within a Plexus Logger.
 */
public class PlexusLoggerWrapper extends AbstractLogger {

    private Log log;

    public PlexusLoggerWrapper(Log customLog) {
        super(Logger.LEVEL_INFO,"NODE");
        this.log = customLog;
    }

    @Override
    public void debug(String message, Throwable throwable) {
        if (throwable == null) {
            this.log.debug(message);
        } else {
            this.log.debug(message, throwable);
        }
    }

    @Override
    public void info(String message, Throwable throwable) {
        if (throwable == null) {
            this.log.info(message);
        } else {
            this.log.info(message, throwable);
        }
    }

    @Override
    public void warn(String message, Throwable throwable) {
        if (throwable == null) {
            this.log.warn(message);
        } else {
            this.log.warn(message, throwable);
        }
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (throwable == null) {
            this.log.error(message);
        } else {
            this.log.error(message, throwable);
        }
    }

    @Override
    public void fatalError(String message, Throwable throwable) {
        this.log.error(message, throwable);
    }

    @Override
    public Logger getChildLogger(String name) {
        return this;
    }
}
