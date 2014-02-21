package org.nanoko.maven.pipeline;

import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.logging.Log;
import org.nanoko.maven.Watcher;
import org.nanoko.maven.WatchingException;
import org.nanoko.maven.utils.DefensiveThreadFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Pipeline.
 */
public class Pipeline {

    private List<Watcher> watchers = new ArrayList<>();
    private final Log log;
    private FileAlterationMonitor watcher;
    private final File baseDir;

    private final static String WATCHING_EXCEPTION_MESSAGE = "Watching exception: %s (check log for more details)";

    public Pipeline(Log customLog, File baseDir) {
        this.log = customLog;
        this.baseDir = baseDir;
    }

    public Pipeline(Log customLog, File baseDir, List<? extends Watcher> list) {
        this(customLog, baseDir);
        this.log.info("Initializing watch mode with " + list);
        watchers = new ArrayList<>();
        for (Object o : list) {
            watchers.add(new WatcherDelegate(o));
        }
    }

    public void shutdown() {
        try {
            watcher.stop();
        } catch (Exception e) { //NOSONAR
            // ignore it.
        }
    }

    public Pipeline remove(Watcher watcher) {
        watchers.remove(watcher);
        return this;
    }

    public Pipeline watch() {
        watcher = new FileAlterationMonitor(2000);
        watcher.setThreadFactory(new DefensiveThreadFactory("wisdom-pipeline-watcher", this.log));
        FileAlterationObserver srcObserver = new FileAlterationObserver(new File(baseDir, "src/main"), TrueFileFilter.INSTANCE);
        PipelineWatcher listener = new PipelineWatcher(this);
        srcObserver.addListener(listener);
        watcher.addObserver(srcObserver);
        try {
        	this.log.info("Start watching " + baseDir.getAbsolutePath());
            watcher.start();
        } catch (Exception e) {
        	this.log.error("Cannot start the watcher", e);
        }
        return this;
    }

    public void onFileCreate(File file) {
    	this.log.info("");
    	this.log.info("The watcher has detected a new file: " + file.getAbsolutePath());
    	this.log.info("");
        for (Watcher watcher : watchers) {
            if (watcher.accept(file)) {
                // This flag will be set to false if the processing must be interrupted.
                boolean continueProcessing;
                try {
                    continueProcessing = watcher.fileCreated(file);
                } catch (WatchingException e) { //NOSONAR
                	this.log.error(String.format(WATCHING_EXCEPTION_MESSAGE + e.getMessage()));
                    continueProcessing = false;
                }
                if (!continueProcessing) {
                    break;
                }
            }
        }
        this.log.info("");
        this.log.info("");
    }

    public void onFileChange(File file) {
    	this.log.info("");
    	this.log.info("The watcher has detected a change in " + file.getAbsolutePath());
    	this.log.info("");
        for (Watcher watcher : watchers) {
            if (watcher.accept(file)) {
                // This flag will be set to false if the processing must be interrupted.
                boolean continueProcessing;
                try {
                    continueProcessing = watcher.fileUpdated(file);
                } catch (WatchingException e) { //NOSONAR
                	this.log.error(String.format(WATCHING_EXCEPTION_MESSAGE + e.getMessage()));
                    continueProcessing = false;
                }
                if (!continueProcessing) {
                    break;
                }
            }
        }
        this.log.info("");
        this.log.info("");
    }

    public void onFileDelete(File file) {
    	this.log.info("");
    	this.log.info("The watcher has detected a deleted file: " + file.getAbsolutePath());
    	this.log.info("");
        for (Watcher watcher : watchers) {
            if (watcher.accept(file)) {
                // This flag will be set to false if the processing must be interrupted.
                boolean continueProcessing;
                try {
                    continueProcessing = watcher.fileDeleted(file);
                } catch (WatchingException e) { //NOSONAR
                	this.log.error(String.format(WATCHING_EXCEPTION_MESSAGE + e.getMessage()));
                    continueProcessing = false;
                }
                if (!continueProcessing) {
                    break;
                }
            }
        }
        this.log.info("");
        this.log.info("");
    }
}
