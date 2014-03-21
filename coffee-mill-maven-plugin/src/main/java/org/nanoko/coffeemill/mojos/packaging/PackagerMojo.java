package org.nanoko.coffeemill.mojos.packaging;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import org.nanoko.java.PlexusLoggerWrapper;


import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;


import java.io.File;
import java.io.IOException;

/**
 * The mojo packaging the Project as a distribution-ready ZIP file
 */
@Mojo(name = "build-zip", threadSafe = false,
requiresDependencyResolution = ResolutionScope.COMPILE,
requiresProject = true,
defaultPhase = LifecyclePhase.PACKAGE)
public class PackagerMojo extends AbstractCoffeeMillMojo  {

    public void execute() throws MojoExecutionException {

        try {
            createApplicationDistribution();
        } catch (Exception e) {
            throw new MojoExecutionException("Cannot build application package", e);
        }
    }

    private void createApplicationDistribution() throws IOException {
        this.getLog().info("createApplicationDistribution");
        if( this.project != null ){
            this.setDefaultOutputFilename(this.project.getArtifactId() + "-" + this.project.getVersion() );
        }

        File distFile = new File(this.getTargetDirectory(), this.getDefaultOutputFilename()+ ".zip");
        ZipArchiver archiver = new ZipArchiver();
        archiver.enableLogging(new PlexusLoggerWrapper(new MavenLoggerWrapper(this.getLog())));
        if (getWorkDirectory().isDirectory()) {
            archiver.addDirectory( getWorkDirectory(), "" );
        }
        if (getLibDirectory().isDirectory()) {
            archiver.addDirectory( getLibDirectory(), "" );
        }
        archiver.setDestFile(distFile);
        archiver.createArchive();
        this.getLog().info("getDirectory="+getWorkDirectory());
        if(projectHelper != null ){
            projectHelper.attachArtifact(project,"zip", "dist", distFile);
        }
    }



}
