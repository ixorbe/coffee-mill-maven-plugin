package org.nanoko.coffeemill.mojos.scripts.coffee;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.nanoko.java.NPM;

import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;


import java.io.File;
import java.util.Collection;


import static org.nanoko.java.NPM.npm;

/**
 * Compiles coffeescript files.
 */
public class AbstractCoffeeScriptCompilerMojo extends AbstractCoffeeMillMojo {

    public static final String COFFEE_SCRIPT_NPM_NAME = "coffee-script";
    public static final String COFFEE_SCRIPT_NPM_VERSION = "1.6.3";
    public static final String COFFEE_SCRIPT_COMMAND = "coffee";
    
    /**
     * Where are CoffeeScript files.
     */
    @Parameter(defaultValue= "src/main/coffee", required = true, readonly = true)
    protected File coffeeScriptDir;
    
    
    private NPM coffee;
    
    // Getters / Setters
    public File getCoffeeScriptDir() {
    	this.coffeeScriptDir.mkdirs();
        return this.coffeeScriptDir;
    }
    
    public void setCoffeeScriptDir(File coffeescriptDir){
    	this.coffeeScriptDir = coffeescriptDir;
    }

    
    public void execute() throws MojoExecutionException {    	
    	if(isSkipped()) { 
    		return; 
    	}
    	
    	if (!this.coffeeScriptDir.isDirectory()){
        	getLog().warn("/!\\ CoffeeScript compilation skipped - " + coffeeScriptDir.getAbsolutePath() + " does not exist !");
        	return;
        }
    	
    	coffee = npm(new MavenLoggerWrapper(this.getLog()), COFFEE_SCRIPT_NPM_NAME, COFFEE_SCRIPT_NPM_VERSION);

        getLog().info("Get CoffeeScript files from " + this.coffeeScriptDir.getAbsolutePath());
        invokeCoffeeScriptCompilerForDirectory(this.coffeeScriptDir, getWorkDirectory());

    }


    protected void invokeCoffeeScriptCompiler(File input, File out) throws MojoExecutionException {
        int exit = coffee.execute(COFFEE_SCRIPT_COMMAND, "--compile",/* "--map",*/ "--output", out.getAbsolutePath(), input.getAbsolutePath());
        getLog().debug("CoffeeScript compilation exits with " + exit + " status");
    }
    
    protected void invokeCoffeeScriptCompilerForDirectory(File dirInput, File dirOut) throws MojoExecutionException {
        if(dirInput.isDirectory() && dirOut.isDirectory()) {
            int exit = coffee.execute(COFFEE_SCRIPT_COMMAND, "--compile",/* "--map",*/ "--output", dirOut.getAbsolutePath(), dirInput.getAbsolutePath());
            getLog().debug("CoffeeScript compilation exits with " + exit + " status");
        }
    }
    
    private boolean isSkipped(){
    	return false;
    }

}
