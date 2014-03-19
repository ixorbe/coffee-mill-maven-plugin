package org.nanoko.coffeemill.mojos.scripts.coffee;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.nanoko.java.NPM;
import org.nanoko.maven.WatchingException;

import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;

import java.io.File;

/**
 * Compiles coffeescript files.
 */
public abstract class AbstractCoffeeScriptCompilerMojo extends AbstractCoffeeMillWatcherMojo {

    public static final String COFFEE_SCRIPT_NPM_NAME = "coffee-script";
    public static final String COFFEE_SCRIPT_NPM_VERSION = "1.7.1";
    public static final String COFFEE_SCRIPT_COMMAND = "coffee";
    
    /**
     * Where are CoffeeScript files.
     */
    @Parameter(defaultValue= "src/main/coffee", required = true, readonly = true)
    protected File coffeeScriptDir;
    
    /**
     * Where are CoffeeScript files implementing tests.
     */
    @Parameter(defaultValue="src/test/coffee", required = true, readonly = true)
    protected File coffeeScriptTestDir;
    
    /**
     * Enables / Disables the coffeescript test compilation.
     * Be aware that this property disables the compilation of test sources only.
     */
    @Parameter(defaultValue="false")
    protected boolean skipCoffeeScriptTestCompilation;  
    
    protected File defaultOutputDirectory;
    
    protected NPM coffee;    
    
            
    // Getters / Setters
    public File getDefaultOutputDirectory(){
        return this.defaultOutputDirectory;
    }
    
    public void setDefaultOutputDirectory(File outputDirectory){
        this.defaultOutputDirectory = outputDirectory;
        this.defaultOutputDirectory.mkdirs();
    }
    
    public File getCoffeeScriptDir() {
    	this.coffeeScriptDir.mkdirs();
        return this.coffeeScriptDir;
    }
    
    public void setCoffeeScriptDir(File coffeescriptDir){
    	this.coffeeScriptDir = coffeescriptDir;
    }
    
    public File getCoffeeScriptTestDir() {
        this.coffeeScriptTestDir.mkdirs();
        return this.coffeeScriptTestDir;
    }
    
    public void setCoffeeScriptTestDir(File coffeescriptTestDir){
        this.coffeeScriptTestDir = coffeescriptTestDir;
    }
    
    
    public void execute() throws MojoExecutionException, MojoFailureException {    
    }    
    
    public boolean accept(File file) {
        return false;
    }
    
    public boolean fileCreated(File file) throws WatchingException{
        return false;
    }

    public boolean fileUpdated(File file) throws WatchingException{
        return false;
    }

    public boolean fileDeleted(File file) throws WatchingException{
        return false;
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
    
    @SuppressWarnings("unused")
    private boolean isSkipped(){
    	return false;
    }

    



}
