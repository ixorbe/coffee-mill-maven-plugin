package org.nanoko.coffeemill.mojos.scripts.coffee;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
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
    
    /**
     * Where are CoffeeScript files.
     */
    @Parameter(defaultValue= "src/main/coffee", required = true, readonly = true)
    public File coffeeScriptDir;

    /**
     * Where are CoffeeScript files implementing tests.
     */
    @Parameter(defaultValue= "src/test/coffee", required = true, readonly = true)
    public File coffeeScriptTestDir;
            
    private NPM coffee;
    
    
    public File getCoffeeScriptDir() {
    	this.coffeeScriptDir.mkdirs();
        return this.coffeeScriptDir;
    }
    
    public void setCoffeeScriptDir(File coffeescriptDir){
    	this.coffeeScriptDir = coffeescriptDir;
    	this.coffeeScriptDir.mkdirs();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
    	if(isSkipped())
    		return;
    	
    	if (!this.coffeeScriptDir.isDirectory()){
        	getLog().warn("/!\\ CoffeeScript compilation skipped - " + coffeeScriptDir.getAbsolutePath() + " does not exist !");
        	return;
        }
    	
    	coffee = npm(new MavenLoggerWrapper(this.getLog()), COFFEE_SCRIPT_NPM_NAME, COFFEE_SCRIPT_NPM_VERSION);

        getLog().info("Get CoffeeScript files from " + this.coffeeScriptDir.getAbsolutePath());
		Collection<File> files = FileUtils.listFiles(this.coffeeScriptDir, new String[]{"coffee"}, true);
		
		if(files.isEmpty()){
			getLog().warn("/!\\ CoffeeScript sources directory " + this.coffeeScriptDir.getAbsolutePath() + " is empty !");
			return;
		}
			
		for(File file : files)
			invokeCoffeeScriptCompiler(file, getWorkDirectory());      	

    }

    public boolean accept(File file) {
        return !isSkipped() 
        	&& FSUtils.isInDirectory(file.getName(), this.coffeeScriptDir) 
        	&& FSUtils.hasExtension(file, "coffee");
    }

    private void compile(File file) throws WatchingException {
        if (file == null) {
            return;
        }
        //File out = new File(getWorkDirectory(), file.getName());
        getLog().info("Compiling CoffeeScript " + file.getAbsolutePath() + " to " + getWorkDirectory().getAbsolutePath());

        try {
            invokeCoffeeScriptCompiler(file, getWorkDirectory());
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
    	File deleted = new File(this.getWorkDirectory().getAbsolutePath(), file.getName());
        if (deleted.isFile()){
        	getLog().info("deleted File : "+file.getName());    	
        	FileUtils.deleteQuietly(deleted); 
        }
        return true;
    }
    
    private boolean isSkipped(){
    	if (skipJsCompilation) {
            getLog().info("\033[31m CoffeeScript Compilation skipped \033[37m");
            return true;
        }
    	else return false;
    }

}
