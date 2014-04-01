package org.nanoko.coffeemill.mojos.scripts.js;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;

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
defaultPhase = LifecyclePhase.TEST_COMPILE)
public class JsTestCompilerMojo extends AbstractCoffeeMillWatcherMojo {

    public void execute() throws MojoExecutionException {    	
        if(isSkipped()) { 
            return; 
        }

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
            try {
                copy(file);
            } catch (WatchingException e) {
                throw new MojoExecutionException("Error during execute() on JsTestCompilerMojo : cannot copy", e);
            }    		
        }   
    }    
    
    public boolean accept(File file) {
        return !isSkipped()
                && file.getParent().contains( getJavaScriptTestDir().getAbsolutePath() )
                && FSUtils.hasExtension(file, "js");
    }

    public boolean fileCreated(File file) throws WatchingException {
        this.copy(file);
        return true;
    }

    public boolean fileUpdated(File file) throws WatchingException {
        if(fileDeleted(file)) {
            return this.fileCreated(file);
        } else {
            return false;
        }
    }

    public boolean fileDeleted(File file) {
        File deleted = FSUtils.computeRelativeFile(file, this.getJavaScriptTestDir(), getWorkTestDirectory());
        if (deleted.isFile()){
            getLog().info("Deleting File : "+file.getName());       
            FileUtils.deleteQuietly(deleted); 
        }
        return true;
    }

    private void copy(File f) throws WatchingException {
        getLog().info("Copy JavaScript files from " + this.getJavaScriptTestDir().getAbsolutePath());
        try {
            FileUtils.copyFileToDirectory(f, this.getWorkTestDirectory());
        } catch (IOException e) { 
            throw new WatchingException("Error during copy JavaScript files.", e); 
        }
    }    

    private boolean isSkipped(){
        if (skipJsTestCompilation) {
            getLog().info("\033[31m JS Test Compilation skipped \033[0m");
            return true;
        } else {
            return false;
        }
    }

}
