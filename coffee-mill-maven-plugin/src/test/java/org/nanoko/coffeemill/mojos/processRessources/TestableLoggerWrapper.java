package org.nanoko.coffeemill.mojos.processRessources;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.maven.plugin.logging.Log;


/**
 * Wrap the Maven logger within a Basic Logger.
 */
public class TestableLoggerWrapper implements org.apache.commons.logging.Log {
    private final Log log;
    public Collection<String> historyLogs;

    public TestableLoggerWrapper(Log log) {
        this.log = log;
        this.historyLogs = new LinkedList<String>();
    }


	public void debug(Object arg0) {
		this.historyLogs.add((String)arg0);
		this.log.debug((CharSequence)arg0);
	}
	
	
	public void debug(Object arg0, Throwable arg1) {
		this.historyLogs.add((String)arg0);
		this.log.debug((CharSequence)arg0, arg1);
	}


	public void error(Object arg0) {
		this.historyLogs.add((String)arg0);
		this.log.error((CharSequence)arg0);
	}


	public void error(Object arg0, Throwable arg1) {
		this.historyLogs.add((String)arg0);
		this.log.error((CharSequence)arg0, arg1);
	}


	public void fatal(Object arg0) {
		this.historyLogs.add((String)arg0);
		this.log.error((CharSequence)arg0);
	}


	public void fatal(Object arg0, Throwable arg1) {
		this.historyLogs.add((String)arg0);
		this.log.error((CharSequence)arg0, arg1);
	}


	public void info(Object arg0) {
		this.historyLogs.add((String)arg0);
		this.log.info((CharSequence)arg0);
	}


	public void info(Object arg0, Throwable arg1) {
		this.historyLogs.add((String)arg0);
		this.log.info((CharSequence)arg0, arg1);
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
		this.historyLogs.add((String)arg0);
		this.log.debug((CharSequence)arg0);
	}


	public void trace(Object arg0, Throwable arg1) {
		this.historyLogs.add((String)arg0);
		this.log.debug((CharSequence)arg0, arg1);
	}


	public void warn(Object arg0) {
		this.historyLogs.add((String)arg0);
		this.log.warn((CharSequence)arg0);
	}


	public void warn(Object arg0, Throwable arg1) {
		this.historyLogs.add((String)arg0);
		this.log.warn((CharSequence)arg0, arg1);
	}
	
}

