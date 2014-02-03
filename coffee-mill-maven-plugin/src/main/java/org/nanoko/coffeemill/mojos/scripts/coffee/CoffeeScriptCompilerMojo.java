package org.nanoko.coffeemill.mojos.scripts.coffee;

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


import static org.nanoko.java.NPM.npm;

/**
 * Compiles coffeescript files.
 */
@Mojo(name = "compile-coffeescript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class CoffeeScriptCompilerMojo extends AbstractCoffeeMillWatcherMojo {

    public static final String COFFEE_SCRIPT_NPM_NAME = "coffee-script";
    public static final String COFFEE_SCRIPT_NPM_VERSION = "1.6.3";
    public static final String COFFEE_SCRIPT_COMMAND = "coffee";
    
    private File sourcesDir;
    private File destinationDir;
    
    private NPM coffee;

    public void execute() throws MojoExecutionException {
    	
    	this.sourcesDir = getCoffeeScriptDir();
    	this.destinationDir = getWorkDirectory();

        coffee = npm(new MavenLoggerWrapper(this.getLog()), COFFEE_SCRIPT_NPM_NAME, COFFEE_SCRIPT_NPM_VERSION);

        if (sourcesDir.isDirectory()) {
            getLog().info("Compiling CoffeeScript files from " + sourcesDir.getAbsolutePath());
            invokeCoffeeScriptCompiler(sourcesDir, destinationDir);
        }

    }

    public boolean accept(File file) {
        return
                FSUtils.isInDirectory(file, this.sourcesDir) && FSUtils.hasExtension(file, "coffee");
    }

    private File getOutputJSFile(File input) {

        if (!input.getAbsolutePath().startsWith(this.sourcesDir.getAbsolutePath()))
        	return null;        

        String jsFileName = input.getName().substring(0, input.getName().length() - ".coffee".length()) + ".js";
        String path = input.getParentFile().getAbsolutePath().substring(this.sourcesDir.getAbsolutePath().length());
        return new File(this.destinationDir, path + "/" + jsFileName);
    }

    private void compile(File file) throws WatchingException {
        if (file == null) {
            return;
        }
        File out = getOutputJSFile(file);
        getLog().info("Compiling CoffeeScript " + file.getAbsolutePath() + " to " + out.getAbsolutePath());

        try {
            invokeCoffeeScriptCompiler(file, out.getParentFile());
        } catch (MojoExecutionException e) { //NOSONAR
            throw new WatchingException("Error during the compilation of " + file.getName() + " : " + e.getMessage());
        }
    }

    private void invokeCoffeeScriptCompiler(File input, File out) throws MojoExecutionException {
        int exit = coffee.execute(COFFEE_SCRIPT_COMMAND, "--compile",/* "--map",*/ "--output", out.getAbsolutePath(),
                input.getAbsolutePath());
        getLog().debug("CoffeeScript compilation exits with " + exit + " status");
    }


    public boolean fileCreated(File file) throws WatchingException {
        compile(file);
        return true;
    }


    public boolean fileUpdated(File file) throws WatchingException {
        compile(file);
        return true;
    }

    public boolean fileDeleted(File file) {
        File theFile = getOutputJSFile(file);
        FileUtils.deleteQuietly(theFile);
        return true;
    }

}
