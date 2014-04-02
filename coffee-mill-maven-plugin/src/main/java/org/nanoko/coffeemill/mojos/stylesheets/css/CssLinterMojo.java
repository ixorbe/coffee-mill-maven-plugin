package org.nanoko.coffeemill.mojos.stylesheets.css;


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
 * Quality-Check CSS files.
 */
@Mojo(name = "lint-css", threadSafe = false,
requiresDependencyResolution = ResolutionScope.COMPILE,
requiresProject = true,
defaultPhase = LifecyclePhase.PACKAGE)
public class CssLinterMojo extends AbstractCoffeeMillMojo {

    public static final String PKG_NPM_NAME = "csslint";
    public static final String PKG_NPM_VERSION = "0.10.0";

    public static Log defaultLogger;

    private NPM lint;

    // Constructor
    public CssLinterMojo() {
        defaultLogger = new MavenLoggerWrapper(this.getLog());
    }    

    public void execute() throws MojoExecutionException {		
        if(isSkipped()) { 
            return; 
        }

        lint = npm(defaultLogger, PKG_NPM_NAME, PKG_NPM_VERSION);
        Collection<File> files = FileUtils.listFiles(this.getWorkDirectory(), new String[]{"css"}, false);
        for(File file : files) {
            try {
                compile(file);
            } catch (WatchingException e) {
                throw new MojoExecutionException("Error during execute() on CssLinterMojo : cannot compile", e);
            }
        }
    }

    private void compile(File file) throws WatchingException {
        String name = file.getName().substring(0, file.getName().lastIndexOf('.'))+".css";
        File input = new File( this.getWorkDirectory(), name);
        if(!input.exists()) {
            return;
        }

        getLog().info("Linting " + input.getAbsolutePath());
        try {            
            int exit = lint.execute("csslint", "--format=compact", input.getAbsolutePath());
            getLog().debug("Js minification execution exiting with " + exit + " status");
        } catch (MojoExecutionException e) {
            throw new WatchingException("Error during the compilation of " + file.getName(), e);
        }
    }

    private boolean isSkipped() {
        if (skipJsLint || skipJsCompilation) {
            getLog().info("\033[31m CSS Lint skipped \033[0m");
            return true;
        } else {
            return false;   	
        }
    }

}
