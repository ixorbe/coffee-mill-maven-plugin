package org.nanoko.coffeemill.mojos.packaging;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import org.nanoko.java.PlexusLoggerWrapper;


import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;


import java.io.File;
import java.io.IOException;

/**
 * The mojo packaging the application.
 */
@Mojo(name = "build-zip", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class PackagerMojo extends AbstractCoffeeMillMojo  {
	
    public String outputFileName = "./release.zip";
    
    @Parameter(defaultValue="false")
	protected boolean skipZipPackaging;
    
    public void execute() throws MojoExecutionException {
    	if(isSkipped())
    		return;
        try {
            createApplicationDistribution();
        } catch (Exception e) {
            throw new MojoExecutionException("Cannot build application package", e);
        }
    }

    private void createApplicationDistribution() throws IOException {
    	this.getLog().info("createApplicationDistribution");
    	if( this.project != null )
    		this.outputFileName =  this.project.getArtifactId() + "-" + this.project.getVersion() + ".zip";
    	this.getLog().info("outputFileName="+outputFileName);
    	this.getLog().info("buildDirectory="+getTargetDirectory());
    	File distFile = new File(this.getTargetDirectory(), this.outputFileName);
        ZipArchiver archiver = new ZipArchiver();
        archiver.enableLogging(new PlexusLoggerWrapper(new MavenLoggerWrapper(this.getLog())));
        archiver.addDirectory(getBuildDirectory());
        archiver.setDestFile(distFile);
        archiver.createArchive();
        this.getLog().info("getDirectory="+getBuildDirectory());
        projectHelper.attachArtifact(project, "zip", distFile);
    }
    
    private boolean isSkipped(){
    	if ( skipZipPackaging ) {
            getLog().info("\033[31m Project zip packaging skipped \033[37m");
            return true;
        }
    	else return false;
    }

}
