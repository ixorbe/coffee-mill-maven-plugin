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
 * The mojo packaging the wisdom application.
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
            throw new MojoExecutionException("Cannot build wisdom application", e);
        }
    }

    private void createApplicationDistribution() throws IOException {
        File distFile = new File(this.buildDirectory, this.project.getArtifactId() + "-" + this.project
                .getVersion() + ".zip");
        ZipArchiver archiver = new ZipArchiver();
        archiver.enableLogging(new PlexusLoggerWrapper(new MavenLoggerWrapper(this.getLog())));
        archiver.addDirectory(getBuildDirectory());
        archiver.setDestFile(distFile);
        archiver.createArchive();

        projectHelper.attachArtifact(project, "zip", distFile);
    }

}
