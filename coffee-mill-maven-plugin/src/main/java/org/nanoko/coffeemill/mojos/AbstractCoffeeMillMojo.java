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
    private File javaScriptDir;
    
    /**
     * Where are JavaScript files implementing tests.
     */
    @Parameter(defaultValue= "src/test/js", required = true, readonly = true)
    private File javaScriptTestDir;
  
    
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
     * Where are the OUTPUT WIP files written.
     */
    @Parameter(defaultValue= "target/tmp", required = true, readonly = true)
    private File workDir;
    
    /**
     * Where are the OUTPUT BUILD files written.
     */
    @Parameter(defaultValue= "target/www", required = true, readonly = true)
    private File buildDir;

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
    
    /**
     * Default extensions authorized for script files
     */
    public String[] scriptExtensions = {"js","coffee"};
    
    /**
     * Default extensions autorized for stylesheets files
     */
    public String[] stylesheetsExtensions = {"css","less","scss"};
    
    
    // NODE
    public NodeManager node =  NodeManager.getInstance();


    
    // METHODS
    public File getTarget() {
        return new File(project.getBuild().getDirectory());
    }

    // WORK-DIR
    public File getWorkDirectory() {
        this.workDir.mkdirs();
        return this.workDir;
    }
    
    public void setWorkDirectory(File workDir){
    	this.workDir = workDir;
    	this.workDir.mkdirs();
    }
    
    // BUILD-DIR
    public File getBuildDirectory() {
    	this.buildDir.mkdirs();
        return this.buildDir;
    }
    
    public void setBuildDirectory(File buildDir){
    	this.buildDir = buildDir;
    	this.buildDir.mkdirs();
    }

    // WORK-TEST-DIR
    public File getWorkTestDirectory() {
    	this.workTestDir.mkdirs();
        return this.workTestDir;
    }
    
    // JS-DIR
    public File getJavaScriptDir() {
    	this.javaScriptDir.mkdirs();
        return this.javaScriptDir;
    }
    
    public void setJavaScriptDir(File javascriptDir){
    	this.javaScriptDir = javascriptDir;
    	this.javaScriptDir.mkdirs();
    }
    
    // JS-TEST-DIR
    /*public File getJavaScriptTestDirectory(){
    	this.javaScriptTestDir.mkdirs();
    	return this.javaScriptTestDir;
    }
    
    public void setJavascriptTestDirectory(File jsTestDir){
    	this.javaScriptTestDir = jsTestDir;
    	this.javaScriptTestDir.mkdirs();
    }*/

    // LIB-DIR
    public File getLibDirectory() {
        return this.libDir;
    }
    
    // STYLESHEETS-DIR
    public File getStylesheetsDir() {
        return this.stylesheetsDir;
    }    
   
    public NodeManager getNodeManager() {
        return this.node;
    }    
    
}
