package org.nanoko.coffeemill.mojos.scripts.js;


import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.java.NPM;

import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;

import org.nanoko.maven.WatchingException;

import java.io.File;
import java.util.Collection;


import static org.nanoko.java.NPM.npm;

/**
 * Quality Check Js files.
 */
@Mojo(name = "lint-javascript", threadSafe = false,
requiresDependencyResolution = ResolutionScope.COMPILE,
requiresProject = true,
defaultPhase = LifecyclePhase.PACKAGE)
public class JsLinterMojo extends AbstractCoffeeMillMojo {

    public static final String PKG_NPM_NAME = "jslint";
    public static final String PKG_NPM_VERSION = "0.2.11";

    private static Log defaultLogger;

    private NPM lint;


    // Constructor
    public JsLinterMojo() {
        defaultLogger = new MavenLoggerWrapper(this.getLog());
    }

    public Log getDefaultLogger() {
        return defaultLogger;
    }

    public static void setDefaultLogger(Log log) {
        defaultLogger = log;
    }

    public void execute() throws MojoExecutionException {

        if(isSkipped()) { 
            return; 
        }

        lint = npm(defaultLogger, PKG_NPM_NAME, PKG_NPM_VERSION);
        try {
            Collection<File> files = FileUtils.listFiles(this.getWorkDirectory(), new String[]{"js"}, false);
            for(File file : files) {
                compile(file);
            }

        } catch (WatchingException e) {
            throw new MojoExecutionException("Error during execute() on JsLinterMojo : cannot compile", e);
        }
    }

    private void compile(File f) throws WatchingException {
        String name = f.getName().substring(0, f.getName().lastIndexOf('.'))+".js";
        File input = new File( this.getWorkDirectory(), name);
        if(!input.exists()){
            return;
        }

        getLog().info("Linting " + input.getAbsolutePath());
        lint.execute(PKG_NPM_NAME, input.getAbsolutePath());
    }

    private boolean isSkipped() {
        if (skipJsLint || skipJsCompilation) {
            getLog().info("\033[31m JS Lint skipped \033[37m");
            return true;
        } else {
            return false;   	
        }
    }

}
