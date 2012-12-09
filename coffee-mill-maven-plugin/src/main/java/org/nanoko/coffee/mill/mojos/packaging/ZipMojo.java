package org.nanoko.coffee.mill.mojos.packaging;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.nanoko.coffee.mill.mojos.AbstractCoffeeMillMojo;

import java.io.File;

/**
 * Build a zip file including the all assets, generated files and dependencies.
 * @goal build-zip
 */
public class ZipMojo extends AbstractCoffeeMillMojo {

    /**
     * The Zip archiver.
     * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="zip"
     */
    protected ZipArchiver zipArchiver;

    /**
     * @parameter default-value="false"
     */
    public boolean skipArchiveCreation;

    /**
     * @parameter default-value="true"
     */
    public boolean attachArchive;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipArchiveCreation) {
            getLog().info("Archive creation skipped");
            return;
        }
        String finalName = project.getBuild().getFinalName() + ".zip";
        File output = new File(buildDirectory, finalName );
        try {
            if (getWorkDirectory().isDirectory()) {
                zipArchiver.addDirectory( getWorkDirectory(), "" );
            }
            if (getLibDirectory().isDirectory()) {
                zipArchiver.addDirectory( getLibDirectory(), "" );
            }
            zipArchiver.setDestFile( output );
            zipArchiver.createArchive();

            if (attachArchive) {
                if (project.getFile() == null) {
                    project.setFile(output);
                } else {
                    projectHelper.attachArtifact(project, "zip", "dist", output);
                }
            }

        } catch( Exception e ) {
            throw new MojoExecutionException( "Could not zip", e );
        }

    }
}