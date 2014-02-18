package org.nanoko.coffeemill.mojos.scripts.js;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;

import org.nanoko.maven.WatchingException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Compiles JavaScript Test files.
 * <ul>
 * <ol>It compiles (checks) JavaScript files from src/test/js.</ol>
 * <ol>It copies these JavaScript files into work directory target/www-test.</ol>
 * </ul>
 * <p/>
 * This mojo makes the assumption that the files are already copied/generated to their destination directory,
 * when it is executed.
 */
@Mojo(name = "test-compile-javascript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class JsTestCompilerMojo extends AbstractCoffeeMillMojo {
	
	
    public void execute() throws MojoExecutionException, MojoFailureException {        
    	try {
    		if(isSkipped()) { return; }
    		
    		if (!this.getJavaScriptTestDir().isDirectory()) {
    			getLog().warn("JavaScript copy skipped - " + this.getJavaScriptTestDir().getAbsolutePath() + " does not exist !");
            	return;
    		}
    		
    		getLog().info("Get JavaScript files from " + this.getJavaScriptTestDir().getAbsolutePath());
        	Collection<File> files = FileUtils.listFiles(this.getJavaScriptTestDir(), new String[]{"js"}, true);
        	
        	if(files.isEmpty()){
    			getLog().warn("JavaScript sources directory "+this.getJavaScriptTestDir().getAbsolutePath()+" is empty !");
    			return;
    		}
        	
            for(File file : files) {
            	copy(file);    		
            }
            
    	} catch (WatchingException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }        
    }
    
    

    private void copy(File f) throws WatchingException {
    	getLog().info("Copy JavaScript files from " + this.getJavaScriptTestDir().getAbsolutePath());
    	try {
			FileUtils.copyFileToDirectory(f, this.getWorkTestDirectory());
		} catch (IOException e) { this.getLog().error(e); }
    }
    
    
    
    private boolean isSkipped(){
    	if (skipJsTestCompilation) {
            getLog().info("\033[31m JS Test Compilation skipped \033[37m");
            return true;
        } else {
        	return false;
        }
    }

}
