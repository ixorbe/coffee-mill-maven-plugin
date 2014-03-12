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
     * Maven ProjectHelper.
     */
    @Component
    public MavenProjectHelper projectHelper;

    /**
     * The maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required=true)
    public MavenProject project;

    /**
     * The current build session instance.
     */
    @Component
    public MavenSession session;  

    // DEPENDENCIES
    /**
     * The plugin dependencies.
     */
    @Parameter(defaultValue = "${plugin.artifacts}")
    public List<Artifact> pluginDependencies;

    /**
     * The directory to run the compiler from if fork is true.
     */
    @Parameter(defaultValue = "${basedir}", required = true, readonly = true)
    public File basedir;    


    /**
     * NodeJs executable Handler 
     */
    private NodeManager node =  NodeManager.getInstance();


    // EXTENSIONS
    /**
     * Default extensions authorized for script files
     */
    private static final String[] SCRIPT_EXTENSIONS = {"js","coffee"};    

    /**
     * Default extensions autorized for stylesheet files
     */
    private static final String[] STYLESHEET_EXTENSIONS = {"css","less","scss"};


    // DIRECTORY
    /**
     * Default filename for output
     */
    private String defaultOutputFilename = "default-output-filename";

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
    private File assetsDir;

    /**
     * Where are LESS, CSS and SASS/SCSS files.
     */
    @Parameter(defaultValue= "src/main/stylesheets", required = true, readonly = true)
    private File stylesheetsDir;

    /**
     * The target directory of the compiler if fork is true.
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
    private File targetDirectory = new File("./target");

    /**
     * Where are the OUTPUT WIP files written.
     */
    @Parameter(defaultValue= "target/www", required = true, readonly = true)
    private File workDir;

    /**
     * Where are the OUTPUT BUILD files written.
     */
    @Parameter(defaultValue= "target/www-release", required = true, readonly = true)
    private File buildDir;

    /**
     * Where are the output test files written.
     */
    @Parameter(defaultValue= "target/www-test", required = true, readonly = true)
    private File workTestDir;

    /**
     * Where are the dependencies copies.
     */
    @Parameter(defaultValue= "target/libs", required = true, readonly = true)
    private File libDir;
    
    /**
     * Define ordered Js files list to aggregate
     */
    @Parameter
    private List<String> jsAggregationFiles;
    
    


    public List<String> getJsAggregationFiles() {
        return jsAggregationFiles;
    }

    // SKIP BOOLEAN
    /**
     * Enables / disables JsCompilation
     */
    @Parameter(defaultValue="false")
    protected boolean skipJsCompilation;

    /**
     * Enables / disables JsTestCompilation
     */
    @Parameter(defaultValue="false")
    protected boolean skipJsTestCompilation;

    /**
     * Enables / disables JsAggregation
     */
    @Parameter(defaultValue="false")
    protected boolean skipJsAggregation;

    /**
     * Enables / disables JsMinification
     */
    @Parameter(defaultValue="false")
    protected boolean skipJsMinification;

    /**
     * Enables / disables CssCompilation
     */
    @Parameter(defaultValue="false")
    protected boolean skipCssCompilation;

    /**
     * Enables / disables CssAggregation
     */
    @Parameter(defaultValue="false")
    protected boolean skipCssAggregation;

    /**
     * Enables / disables CssMinification
     */
    @Parameter(defaultValue="false")
    protected boolean skipCssMinification;

    /**
     * Enables / disables JsLint Optimizer
     */
    @Parameter(defaultValue="false")
    protected boolean skipJsLint;

    /**
     * Enables / disables Pictures Optimizer
     */
    @Parameter(defaultValue="false")
    protected boolean skipPicturesOptimization;



    /**
     * Return output Filename
     * @return filename
     */
    public String getDefaultOutputFilename() {
        return defaultOutputFilename;
    }

    /**
     * Assign output filename
     * @param defaultOutputFilename
     */
    public void setDefaultOutputFilename(String defaultOutputFilename) {
        this.defaultOutputFilename = defaultOutputFilename;
    }

    /**
     * Return the NodeJS handler
     * @return NodeJS
     */
    public NodeManager getNodeManager() {
        return this.node;
    }


    /**
     * Get Target directory (maven)
     * @return target directory
     */
    public File getTargetDirectory() {
        this.targetDirectory.mkdirs();
        return this.targetDirectory;
    }

    /**
     * Specify Target directory (maven)
     * @return target directory
     */
    public void setTargetDirectory(File target){
        this.targetDirectory = target;
    }    

    // WORK-DIR
    public File getWorkDirectory() {
        this.workDir.mkdirs();
        return this.workDir;
    }

    public void setWorkDirectory(File work){
        this.workDir = work;
    }

    // WORK-TEST-DIR
    public File getWorkTestDirectory() {
        this.workTestDir.mkdirs();
        return this.workTestDir;
    }

    // BUILD-DIR
    public File getBuildDirectory() {
        this.buildDir.mkdirs();
        return this.buildDir;
    }

    public void setBuildDirectory(File build){
        this.buildDir = build;
    }

    // JS-DIR
    public File getJavaScriptDir() {
        this.javaScriptDir.mkdirs();
        return this.javaScriptDir;
    }

    public void setJavaScriptDir(File javascript){
        this.javaScriptDir = javascript;
    }

    // JS-TEST-DIR
    public File getJavaScriptTestDir(){
        this.javaScriptTestDir.mkdirs();
        return this.javaScriptTestDir;
    }

    public void setJavascriptTestDir(File jsTestDir){
        this.javaScriptTestDir = jsTestDir;
    }

    // STYLESHEETS-DIR
    public File getStylesheetsDir() {
        this.stylesheetsDir.mkdirs();
        return this.stylesheetsDir;
    }

    public void setStylesheetsDir(File stylesheetsDir){
        this.stylesheetsDir = stylesheetsDir;
    }    

    // LIB-DIR
    public File getLibDirectory() {
        this.libDir.mkdirs();
        return this.libDir;
    }

    public void setLibDirectory(File libs) {
        this.libDir = libs;
    }

    // ASSETS-DIR
    public File getAssetsDir(){
        this.assetsDir.mkdirs();
        return this.assetsDir;
    }

    public void setAssetsDir(File assets){
        this.assetsDir = assets;
    }

    // EXTENSIONS
    public static String[] getScriptextensions() {
        return SCRIPT_EXTENSIONS;
    }

    public static String[] getStylesheetsextensions() {
        return STYLESHEET_EXTENSIONS;
    } 

}
