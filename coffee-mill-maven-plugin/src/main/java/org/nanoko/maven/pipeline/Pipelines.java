package org.nanoko.maven.pipeline;

import java.io.File;
import org.apache.maven.execution.MavenSession;

import org.apache.commons.logging.Log;

/**
 * Pipeline bootstrap
 */
public class Pipelines {
    
    private Pipelines(){
    }

    public static Pipeline watchers(MavenSession session, Log customLog, File baseDir) {
        return new Pipeline(customLog, baseDir, Watchers.all(session));
    }
}
