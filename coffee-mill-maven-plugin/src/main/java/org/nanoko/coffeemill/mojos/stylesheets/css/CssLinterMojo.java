package org.nanoko.coffeemill.mojos.stylesheets.css;


import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.java.NPM;

import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;

import org.nanoko.maven.WatchingException;

import java.io.File;
import java.util.Collection;


import static org.nanoko.java.NPM.npm;

/**
 * Optimize Js files.
 */
@Mojo(name = "lint-css", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class CssLinterMojo extends AbstractCoffeeMillWatcherMojo {

    public static final String PKG_NPM_NAME = "csslint";
    public static final String PKG_NPM_VERSION = "0.10.0";
    
    private NPM lint;

    public void execute() throws MojoExecutionException {
		if(isSkipped())
    		return;
		
    	lint = npm(new MavenLoggerWrapper(this.getLog()), PKG_NPM_NAME, PKG_NPM_VERSION);
        try {
        	Collection<File> files = FileUtils.listFiles(this.getWorkDirectory(), new String[]{"css"}, false);
            for(File file : files)
            	compile(file);

        } catch (WatchingException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public boolean accept(File file) {
        return  !isSkipped() && FSUtils.hasExtension(file, scriptExtensions);
    }

    public void compile(File f) throws WatchingException {
    	String name = f.getName().substring(0, f.getName().lastIndexOf('.'))+".css";
    	File input = new File( this.getWorkDirectory().getAbsolutePath(), name);
    	if(!input.exists())
    		return;

        getLog().info("Linting " + input.getAbsolutePath());
        int exit = lint.execute("csslint", "--format=compact", input.getAbsolutePath());
    }


    public boolean fileCreated(File file) throws WatchingException {
        compile(file);
        return true;
    }


    public boolean fileUpdated(File file) throws WatchingException {
        compile(file);
        return true;
    }

    public boolean fileDeleted(File file) throws WatchingException{
        return true;
    }
    
    private boolean isSkipped() {
    	if (skipJsLint || skipJsCompilation) {
            getLog().info("\033[31m JS Lint Optimizer skipped \033[37m");
            return true;
        }
    	else return false;   	
    }

}
