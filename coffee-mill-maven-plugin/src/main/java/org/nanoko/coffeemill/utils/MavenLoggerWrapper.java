package org.nanoko.coffeemill.utils;

import org.apache.maven.plugin.logging.Log;


/**
 * Wrap the Maven logger within a Basic Logger.
 */
public class MavenLoggerWrapper implements org.apache.commons.logging.Log {
    private Log logger;

    /**
     * Get reference to a Maven Logger
     * @param log Maven Logger
     */
    public MavenLoggerWrapper(Log log) {
        this.logger = log;
    }
    
    @Override
	public void debug(Object arg0) {
		this.logger.debug((CharSequence)arg0);
	}
	
    @Override
	public void debug(Object arg0, Throwable arg1) {
		this.logger.debug((CharSequence)arg0, arg1);
	}

    @Override
	public void error(Object arg0) {
		this.logger.error((CharSequence)arg0);
	}

    @Override
	public void error(Object arg0, Throwable arg1) {
		this.logger.error((CharSequence)arg0, arg1);
	}

    @Override
	public void fatal(Object arg0) {
		this.logger.error((CharSequence)arg0);
	}

    @Override
	public void fatal(Object arg0, Throwable arg1) {
		this.logger.error((CharSequence)arg0, arg1);
	}

    @Override
	public void info(Object arg0) {
		this.logger.info((CharSequence)arg0);
	}

    @Override
	public void info(Object arg0, Throwable arg1) {
		this.logger.info((CharSequence)arg0, arg1);
	}

    @Override
	public boolean isDebugEnabled() {
		return false;
	}

    @Override
	public boolean isErrorEnabled() {
		return false;
	}

    @Override
	public boolean isFatalEnabled() {
		return false;
	}

    @Override
	public boolean isInfoEnabled() {
		return false;
	}

    @Override
	public boolean isTraceEnabled() {
		return false;
	}

    @Override
	public boolean isWarnEnabled() {
		return false;
	}

    @Override
	public void trace(Object arg0) {
		this.logger.debug((CharSequence)arg0);
	}

    @Override
	public void trace(Object arg0, Throwable arg1) {
		this.logger.debug((CharSequence)arg0, arg1);
	}

    @Override
	public void warn(Object arg0) {
		this.logger.warn((CharSequence)arg0);
	}

    @Override
	public void warn(Object arg0, Throwable arg1) {
		this.logger.warn((CharSequence)arg0, arg1);
	}
	
}

