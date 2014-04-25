package org.nanoko.coffeemill.mojos.stylesheets.css;


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
import java.io.IOException;


import static org.nanoko.java.NPM.npm;

/**
 * Minifying CSS files.
 */
@Mojo(name = "minify-stylesheets", threadSafe = false,
requiresDependencyResolution = ResolutionScope.COMPILE,
requiresProject = true,
defaultPhase = LifecyclePhase.PACKAGE)
public class CssMinifierMojo extends AbstractCoffeeMillWatcherMojo {

    public static final String CLEANCSS_NPM_NAME = "clean-css";
    public static final String CLEANCSS_NPM_VERSION = "2.1.4";

    private NPM cleancss;


    public void execute() throws MojoExecutionException {    	
        if(isSkipped()) { 
            return; 
        }

        cleancss = npm(new MavenLoggerWrapper(this.getLog()), CLEANCSS_NPM_NAME, CLEANCSS_NPM_VERSION);
        try {
            compile();
        } catch (WatchingException e) {
            throw new MojoExecutionException("Error during execute() on CssMinifierMojo : cannot compile", e);
        }
    }

    public boolean accept(File file) {
        return !isSkipped() && FSUtils.hasExtension(file, getStylesheetsextensions());
    }

    public boolean fileCreated(File file) throws WatchingException {
        compile();
        return true;
    }

    public boolean fileUpdated(File file) throws WatchingException {
        compile();
        return true;
    }

    public boolean fileDeleted(File file) throws WatchingException{
        compile();
        return true;
    }


    private boolean compile() throws WatchingException { 
        if(this.project!=null) {
            this.setDefaultOutputFilename(this.project.getArtifactId()+"-"+this.project.getVersion());
        }

        boolean res = minify(this.getDefaultOutputFilename());
        //boolean res2 = minify(this.inputFilename+"-all");
        //return res || res2;
        return res;
    }

    private boolean minify( String baseName) throws WatchingException {
        // check if input is valid
        //File input = new File( this.getBuildDirectory(), baseName+".css");
        File input = new File( this.getWorkDirectory(), baseName+".css");
        if(!input.exists()) {
            return false;
        }

        // if output exist, delete it
        //File output = new File( this.getBuildDirectory(),baseName+"-min.css");
        File output = new File( this.getWorkDirectory(),baseName+"-min.css");
        if(output.exists()) {
            FileUtils.deleteQuietly(output);
        }

        getLog().info("Minifying " + input.getAbsolutePath() + " to " + output.getAbsolutePath());        
        try {
            int exit = cleancss.execute("cleancss", "-o",  output.getAbsolutePath(),input.getAbsolutePath());
            getLog().debug("Js minification execution exiting with " + exit + " status");
        } catch (MojoExecutionException e) {
            throw new WatchingException("Error during the minification of " + input.getName(), e);
        }

        if (!output.isFile()) {
            throw new WatchingException("Error during the minification of " + input.getAbsoluteFile() + " check log");
        }else {
            /*if(projectHelper != null && !baseName.contains("all") ){
                projectHelper.attachArtifact(project, "css", "min", output);
            }*/
            if(projectHelper != null && project!=null){
                try {
                    File artifact = new File(getTargetDirectory(), project.getBuild().getFinalName() + "-min.css");
                    getLog().info("Copying " + output.getAbsolutePath() + " to the " + artifact.getAbsolutePath());
                    FileUtils.copyFile(output, artifact, true);
                    
                    projectHelper.attachArtifact(project, artifact, "min");
                } catch (IOException e) {
                    this.getLog().error("Error while attaching js to project",e);
                }
            }
        }
        return true;
    }

    private boolean isSkipped(){
        if (skipCssMinification || skipCssAggregation || skipCssCompilation) {
            getLog().info("\033[31m CSS Minification skipped \033[0m");
            return true;
        } else {
            return false;
        }
    }

}
