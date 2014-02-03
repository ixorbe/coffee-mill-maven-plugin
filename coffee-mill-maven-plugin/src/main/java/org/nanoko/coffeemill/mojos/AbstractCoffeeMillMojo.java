package org.nanoko.coffeemill.mojos;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;


import org.nanoko.java.NodeManager;

import java.io.File;
import java.util.List;

/**
 * Common part.
 */
public abstract class AbstractCoffeeMillMojo extends AbstractMojo {

	
	// MAVEN
    /**
     * The maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required=true)
    public MavenProject project;
    
    /**
     * Maven ProjectHelper.
     */
    @Component
    public MavenProjectHelper projectHelper;
    
    /**
     * The current build session instance.
     */
    @Component
    public MavenSession session;
    
    /**
     * The plugin dependencies.
     */
    @Parameter(defaultValue = "${plugin.artifacts}")
    public List<Artifact> pluginDependencies;
    
    // DIRECTORY
    /**
     * The target directory of the compiler if fork is true.
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
    public File buildDirectory;
    
    /**
     * The directory to run the compiler from if fork is true.
     */
    @Parameter(defaultValue = "${basedir}", required = true, readonly = true)
    public File basedir;

    /**
     * Where are JavaScript files. 
     */
    @Parameter(defaultValue= "src/main/js", required = true, readonly = true)
    public File javaScriptDir;
    
    /**
     * Where are JavaScript files implementing tests.
     */
    @Parameter(defaultValue= "src/test/js", required = true, readonly = true)
    public File javaScriptTestDir;
  
    
    /**
     * Where are the assets.
     */
    @Parameter(defaultValue= "src/main/www", required = true, readonly = true)
    public File assetsDir;
    
    /**
     * Where are LESS, CSS and SASS/SCSS files.
     */
    @Parameter(defaultValue= "src/main/stylesheets", required = true, readonly = true)
    public File stylesheetsDir;

    /**
     * Where are the OUTPUT files written.
     */
    @Parameter(defaultValue= "target/www", required = true, readonly = true)
    public File workDir;

    /**
     * Where are the output test files written.
     */
    @Parameter(defaultValue= "target/www-test", required = true, readonly = true)
    public File workTestDir;

    /**
     * Where are the dependencies copies.
     */
    @Parameter(defaultValue= "target/libs", required = true, readonly = true)
    public File libDir;
    
    
    // NODE
    public NodeManager node =  NodeManager.getInstance();


    
    // METHODS
    public File getTarget() {
        return new File(project.getBuild().getDirectory());
    }

    public File getWorkDirectory() {
        workDir.mkdirs();
        return workDir;
    }

    public File getWorkTestDirectory() {
        workTestDir.mkdirs();
        return workTestDir;
    }

    public File getLibDirectory() {
        return libDir;
    }

    public File getJavaScriptDir() {
        return javaScriptDir;
    }

    public File getStylesheetsDir() {
        return stylesheetsDir;
    }    
   
    public NodeManager getNodeManager() {
        return node;
    }    
    
}
