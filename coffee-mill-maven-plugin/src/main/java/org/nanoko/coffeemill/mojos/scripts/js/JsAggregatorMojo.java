package org.nanoko.coffeemill.mojos.scripts.js;

import org.apache.commons.io.FileUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.maven.WatchingException;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.FileAggregation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Aggregate Js files.
 */
@Mojo(name = "aggregate-javascript", threadSafe = false,
requiresDependencyResolution = ResolutionScope.TEST,
requiresProject = true,
defaultPhase = LifecyclePhase.PACKAGE)
public class JsAggregatorMojo extends AbstractCoffeeMillWatcherMojo {


    @Parameter(defaultValue="true")
    protected boolean failedOnMissingFile;


    public void execute() throws MojoExecutionException {
        if(isSkipped()) { 
            return; 
        }

        if (!this.getWorkDirectory().isDirectory()){
            getLog().warn("JavaScript aggregation skipped - " + this.getWorkDirectory() + " does not exist !");
            return;
        }   

        try {
            this.aggregate();            
        } catch (WatchingException e) {
            throw new MojoExecutionException("Error during execute() on JsAggregatorMojo : cannot aggregate", e);
        }
    }


    public boolean accept(File file) {
        return !isSkipped() && FSUtils.hasExtension(file, getScriptextensions());
    }

    public boolean fileCreated(File file) throws WatchingException {
        this.aggregate();
        return true;
    }

    public boolean fileUpdated(File file) throws WatchingException {
        this.aggregate();
        return true;
    }

    public boolean fileDeleted(File file) throws WatchingException {
        this.aggregate();
        return true;
    }


    private void aggregate() throws WatchingException {
        if(this.project!=null){
            this.setDefaultOutputFilename(this.project.getArtifactId()+"-"+this.project.getVersion());
        }

        //File output = new File( this.getBuildDirectory(), this.getDefaultOutputFilename() + ".js");
        File output = new File( this.getWorkDirectory(), this.getDefaultOutputFilename() + ".js");
        
        if(output.exists()) {
            FileUtils.deleteQuietly(output); 
        }

        // Classic Aggregation (app + ext. libs)
        if (this.getJsAggregationFiles() == null || this.getJsAggregationFiles().isEmpty()) {    		
            if(aggregateAppOnly(output)) {
                aggregateAppWithLibs(output);
            }    		
        } else { // Aggregation from pom.xml JsAggregationFiles list
            aggregateFromListFiles(output);        	
        }    	
    }


    private boolean aggregateFromListFiles(File output) throws WatchingException {
        Collection<File> files = new ArrayList<File>();

        for (String filename : this.getJsAggregationFiles()) {
            File file = FSUtils.resolveFile(filename, getWorkDirectory(), getLibDirectory(), "js");
            if (file == null) {
                if (failedOnMissingFile) {
                    throw new WatchingException("Aggregation failed : " + filename + " file missing in " + getWorkDirectory().getAbsolutePath());
                } else {
                    getLog().warn("Issue detected during aggregation : " + filename + " missing");
                }
            } else {
                // The file exists.
                files.add(file);
            }
        }

        joinFiles(output, files);
        if(output.exists() && project != null) {
            try {
                File artifact = new File(getTargetDirectory(), project.getBuild().getFinalName() + ".js");
                getLog().info("Copying " + output.getAbsolutePath() + " to the " + artifact.getAbsolutePath());
                FileUtils.copyFile(output, artifact, true);
                project.getArtifact().setFile(artifact);
            } catch (IOException e) {
                this.getLog().error("Error while attaching js to project",e);
            }
        }
        return true;
    }    

    private boolean aggregateAppOnly(File output) throws WatchingException {
        Collection<File> files = FileUtils.listFiles(this.getWorkDirectory(), new String[]{"js"}, true);
        if(files.isEmpty()){
            getLog().warn("JavaScript work directory "+this.getWorkDirectory().getAbsolutePath()+" is empty !");
            return false;
        }
        getLog().info("Aggregate Js files from " + this.getWorkDirectory().getAbsolutePath());

        joinFiles(output, files);
        if(output.exists()&& project != null) {
            try {
                File artifact = new File(getTargetDirectory(), project.getBuild().getFinalName() + ".js");
                getLog().info("Copying " + output.getAbsolutePath() + " to the " + artifact.getAbsolutePath());
                FileUtils.copyFile(output, artifact, true);
                project.getArtifact().setFile(artifact);
            } catch (IOException e) {
                this.getLog().error("Error while attaching js to project",e);
            }
        }
        return true;
    }

    private void aggregateAppWithLibs(File in) throws WatchingException{
        File output = new File(this.getBuildDirectory(),  this.getDefaultOutputFilename()+"-all.js");
        if(output.exists()){
            FileUtils.deleteQuietly(output);
        }

        Collection<File> files = FileUtils.listFiles(this.getLibDirectory(), new String[]{"js"}, true);

        if(files.isEmpty()){
            getLog().warn("JavaScript External libraries directory "+this.getLibDirectory().getAbsolutePath()+" is empty !");
            try {
                FileUtils.copyFile(in, output);
            } catch (IOException e) {
                throw new WatchingException("Error during copy file to build directory "+this.getBuildDirectory(), e);
            }
            return;
        }

        files.add(in);
        getLog().info("Aggregate Js files from libs directory " + this.getLibDirectory().getAbsolutePath());

        joinFiles(output, files);
    }    

    private void joinFiles(File output, Collection<File> files) throws WatchingException{
        try {
            FileAggregation.joinFiles( output, files);
        } catch (IOException e) {
            throw new WatchingException("Error during joinFiles", e);
        }

        if (!output.isFile()) {
            throw new WatchingException("Error during the Js aggregation check log");
        }
    }

    private boolean isSkipped(){
        if (skipJsAggregation || skipJsCompilation) {
            getLog().info("\033[31m JS Aggregation skipped \033[37m");
            return true;
        } else {
            return false;
        }
    }


}
