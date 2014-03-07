package org.nanoko.coffeemill.mojos.stylesheets.less;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.maven.WatchingException;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.java.NPM;


import java.io.File;
import java.util.Collection;

import static org.nanoko.java.NPM.npm;

/**
 * Compiles LESS files.
 */
@Mojo(name = "compile-less", threadSafe = false,
requiresDependencyResolution = ResolutionScope.COMPILE,
requiresProject = true,
defaultPhase = LifecyclePhase.COMPILE)
public class LessCompilerMojo extends AbstractCoffeeMillWatcherMojo {

    public static final String LESS_NPM_NAME = "less";
    public static final String LESS_NPM_VERSION = "1.7.0";

    private NPM less;


    public void execute() throws MojoExecutionException {    	
        if(isSkipped()) { 
            return; 
        }

        less = npm(new MavenLoggerWrapper(this.getLog()), LESS_NPM_NAME, LESS_NPM_VERSION);
        if ( getStylesheetsDir().isDirectory()) {
            getLog().info("Compiling less files from " + getStylesheetsDir().getAbsolutePath());
            Collection<File> files = FileUtils.listFiles(getStylesheetsDir(), new String[]{"less"}, true);
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        compile(file);
                    } catch (WatchingException e) {
                        throw new MojoExecutionException("Error during execute() on LessCompilerMojo : cannot compile", e);
                    }                    
                }
            }
        }        
    }


    public boolean accept(File file) {
        return !isSkipped()
                && file.getParent().contains( getStylesheetsDir().getAbsolutePath() )
                && FSUtils.hasExtension(file, "less");
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
        File out = FSUtils.computeRelativeFile(file, this.getStylesheetsDir(), getWorkDirectory());
        File newName = new File( out.getAbsolutePath().substring(0, out.getAbsolutePath().length() - ".less".length()) + ".css" );
        if(newName.exists()){
            FileUtils.deleteQuietly(newName);
        }
        return true;
    }

    private void compile(File file) throws WatchingException {
        File out = FSUtils.computeRelativeFile(file, this.getStylesheetsDir(), getWorkDirectory());
        String newName = out.getAbsolutePath().substring(0, out.getAbsolutePath().length() - ".less".length()) + ".css";
        
        getLog().info("Compiling " + file.getAbsolutePath() + " to " + newName);
        int exit = less.execute("lessc", file.getAbsolutePath(), newName);
        getLog().debug("Less execution exiting with " + exit + " status");

        if (!new File(newName).exists()) {
            throw new WatchingException("Error during the compilation of " + file.getAbsoluteFile() + " check log");
        }
    }

    private boolean isSkipped(){
        if (skipCssCompilation) {
            getLog().info("\033[31m LESS Compilation skipped \033[37m");
            return true;
        } else {
            return false;
        }
    }

}
