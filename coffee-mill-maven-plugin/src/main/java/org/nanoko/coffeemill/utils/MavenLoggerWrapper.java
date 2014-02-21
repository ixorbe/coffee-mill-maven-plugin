package org.nanoko.coffeemill.utils;

import org.apache.maven.plugin.logging.Log;


/**
 * Wrap the Maven logger within a Basic Logger.
 */
public class MavenLoggerWrapper implements org.apache.commons.logging.Log {
    private Log logger;

    public MavenLoggerWrapper(Log log) {
        this.logger = log;
    }

	public void debug(Object arg0) {
		this.logger.debug((CharSequence)arg0);
	}
	
	
	public void debug(Object arg0, Throwable arg1) {
		this.logger.debug((CharSequence)arg0, arg1);
	}


	public void error(Object arg0) {
		this.logger.error((CharSequence)arg0);
	}


	public void error(Object arg0, Throwable arg1) {
		this.logger.error((CharSequence)arg0, arg1);
	}


	public void fatal(Object arg0) {
		this.logger.error((CharSequence)arg0);
	}


	public void fatal(Object arg0, Throwable arg1) {
		this.logger.error((CharSequence)arg0, arg1);
	}


	public void info(Object arg0) {
		this.logger.info((CharSequence)arg0);
	}


	public void info(Object arg0, Throwable arg1) {
		this.logger.info((CharSequence)arg0, arg1);
	}


	public boolean isDebugEnabled() {
		return false;
	}


	public boolean isErrorEnabled() {
		return false;
	}


	public boolean isFatalEnabled() {
		return false;
	}


	public boolean isInfoEnabled() {
		return false;
	}


	public boolean isTraceEnabled() {
		return false;
	}


	public boolean isWarnEnabled() {
		return false;
	}


	public void trace(Object arg0) {
		this.logger.debug((CharSequence)arg0);
	}


	public void trace(Object arg0, Throwable arg1) {
		this.logger.debug((CharSequence)arg0, arg1);
	}


	public void warn(Object arg0) {
		this.logger.warn((CharSequence)arg0);
	}


	public void warn(Object arg0, Throwable arg1) {
		this.logger.warn((CharSequence)arg0, arg1);
	}
	
}

