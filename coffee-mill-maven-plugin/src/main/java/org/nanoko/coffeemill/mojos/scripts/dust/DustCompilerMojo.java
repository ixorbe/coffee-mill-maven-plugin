package org.nanoko.coffeemill.mojos.scripts.dust;

import static org.nanoko.java.NPM.npm;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.java.NPM;
import org.nanoko.maven.WatchingException;


import java.io.File;
import java.util.Collection;

/**
 * Compiles Dust files.
 */
@Mojo(name = "compile-dust", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class DustCompilerMojo extends  AbstractCoffeeMillWatcherMojo {

    public static final String DUST_NPM_NAME = "dust-compiler";
    public static final String DUST_NPM_VERSION = "0.3.7";

    private NPM dust;


    public void execute() throws MojoExecutionException {       
        if(isSkipped()) { 
            return; 
        }

        dust = npm(new MavenLoggerWrapper(this.getLog()), DUST_NPM_NAME, DUST_NPM_VERSION);
        if ( getJavaScriptDir().isDirectory()) {
            getLog().info("Compiling dust files from " + getJavaScriptDir().getAbsolutePath());
            Collection<File> files = FileUtils.listFiles(getJavaScriptDir(), new String[]{"dust"}, true);
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        compile(file);
                    } catch (WatchingException e) {
                        throw new MojoExecutionException("Error during execute() on DustCompilerMojo : cannot compile", e);
                    }                    
                }
            }
        }        
    }


    public boolean accept(File file) {
        return !isSkipped()
                && file.getParent().contains( getJavaScriptDir().getAbsolutePath() )
                && FSUtils.hasExtension(file, "dust");
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


    private File getOutputJSFile(File input) {
        String jsFileName = input.getName().substring(0, input.getName().length() - ".dust".length()) + ".js";
        String path = input.getParentFile().getAbsolutePath().substring(getJavaScriptDir().getAbsolutePath().length());
        return new File(this.getWorkDirectory(), path + "/" + jsFileName);
    }

    private void compile(File file) throws WatchingException {
        File out = getOutputJSFile(file);
        getLog().info("Compiling " + file.getAbsolutePath() + " to " + out.getAbsolutePath());
        int exit = dust.execute("dust-compiler", "-s", file.getAbsolutePath(),"-d", out.getAbsolutePath());
        getLog().debug("Dust-compiler execution exiting with " + exit + " status");

        if (!out.isFile()) {
            throw new WatchingException("Error during the compilation of " + file.getAbsoluteFile() + "; check log");
        }
    }

    private boolean isSkipped(){
        if (skipJsCompilation) {
            getLog().info("\033[31m DUST Compilation skipped \033[37m");
            return true;
        } else {
            return false;
        }
    }

}
