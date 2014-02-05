package org.nanoko.coffeemill.mojos.scripts.js;


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
@Mojo(name = "optimize-javascript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class JsOptimizerMojo extends AbstractCoffeeMillWatcherMojo {

    public static final String PKG_NPM_NAME = "jslint";
    public static final String PKG_NPM_VERSION = "0.2.10";
    
    private NPM lint;

    public void execute() throws MojoExecutionException {

    	lint = npm(new MavenLoggerWrapper(this.getLog()), PKG_NPM_NAME, PKG_NPM_VERSION);
        try {
        	Collection<File> files = FileUtils.listFiles(this.getWorkDirectory(), new String[]{"js"}, true);
            for(File file : files)
            	compile(file);

        } catch (WatchingException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public boolean accept(File file) {
        return  FSUtils.hasExtension(file, scriptExtensions);
    }

    public void compile(File f) throws WatchingException {
    	String name = f.getName().substring(0, f.getName().lastIndexOf('.'))+".js";
    	File input = new File( this.getBuildDirectory().getAbsolutePath(),name);
    	if(!input.exists())
    		return;

        getLog().info("Linting " + input.getAbsolutePath());
        int exit = lint.execute("jslint",input.getAbsolutePath());

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

}
