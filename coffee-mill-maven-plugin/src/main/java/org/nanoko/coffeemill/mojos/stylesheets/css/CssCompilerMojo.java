package org.nanoko.coffeemill.mojos.stylesheets.css;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.maven.WatchingException;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;


/**
 * Compiles CSS files.
 */
@Mojo(name = "compile-css", threadSafe = false,
requiresDependencyResolution = ResolutionScope.TEST,
requiresProject = true,
defaultPhase = LifecyclePhase.PACKAGE)
public class CssCompilerMojo extends AbstractCoffeeMillWatcherMojo {

    public void execute() throws MojoExecutionException {    	
        if(isSkipped()) { 
            return; 
        }

        if ( getStylesheetsDir().isDirectory()) {
            Collection<File> files = FileUtils.listFiles(getStylesheetsDir(), new String[]{"css"}, true);
            for(File f: files) {
                try {
                    copy(f);
                } catch (WatchingException e) {
                    throw new MojoExecutionException("Error during execute() on CssCompilerMojo : cannot copy css files", e);
                }    			
            }
        }
    }

    public boolean accept(File file) {
        return !isSkipped()
                && file.getParent().contains( getStylesheetsDir().getAbsolutePath() )
                && FSUtils.hasExtension(file, "css");
    }

    public boolean fileCreated(File file) throws WatchingException {
        this.copy(file);
        return true;
    }

    public boolean fileUpdated(File file) throws WatchingException {
        if(fileDeleted(file)) {
            this.copy(file);
            return true;
        } else { 
            return false;
        }
    }

    public boolean fileDeleted(File file) throws WatchingException {        
        File deleted = FSUtils.computeRelativeFile(file, this.getStylesheetsDir(), getWorkDirectory());
        if (deleted.isFile()){
            getLog().info("deleting File : "+file.getName());    	
            FileUtils.deleteQuietly(deleted); 
        }
        return true;
    }


    private void copy(File f) throws WatchingException {
        getLog().info("Copy css files from " + getStylesheetsDir().getAbsolutePath());
        try {
            File out = FSUtils.computeRelativeFile(f, this.getStylesheetsDir(), getWorkDirectory());
            if (out.getParentFile() != null) {
                out.getParentFile().mkdirs();
                FileUtils.copyFileToDirectory(f, out.getParentFile());
            } else{ 
                getLog().error("Cannot copy file - parent directory not accessible for " + out);
            }
        } catch (IOException e) { 
            throw new WatchingException("Error during copy css files to workDirectory "+this.getWorkDirectory().getAbsolutePath(), e); 
        }
    }

    private boolean isSkipped(){
        if (skipCssCompilation) {
            getLog().info("\033[31m CSS Compilation skipped \033[0m");
            return true;
        } else {
            return false;
        }
    }

}
