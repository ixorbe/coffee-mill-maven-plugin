package org.nanoko.coffeemill.mojos.reporting;


import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.java.NPM;

import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;

import org.nanoko.maven.WatchingException;

import java.io.File;


import static org.nanoko.java.NPM.npm;

/**
 * Create JsDoc for project.
 */
@Mojo(name = "documentation-javascript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.TEST,
        requiresProject = true,
        defaultPhase = LifecyclePhase.PACKAGE)
public class JsDocMojo extends AbstractCoffeeMillMojo {

    public static final String PKG_NPM_NAME = "jsdoc";
    public static final String PKG_NPM_VERSION = "3.3.0-alpha4";
    
    @Parameter(defaultValue="true")
	protected boolean skipJsDocumentation;
    
    private NPM jsdoc;
    
    
    public void execute() throws MojoExecutionException {    	
    	if(isSkipped()) { 
    		return; 
    	}
    	
    	jsdoc = npm(new MavenLoggerWrapper(this.getLog()), PKG_NPM_NAME, PKG_NPM_VERSION);
        try {
        	compile();
        } catch (WatchingException e) {
            throw new MojoExecutionException("Error during execute() on JsDocMojo : cannot compile", e);
        }
    }

    public boolean accept(File file) {
        return  !isSkipped() && FSUtils.hasExtension(file, getScriptextensions());
    }

    private void compile() throws WatchingException {
    	if(this.project!=null) {
    		this.setDefaultOutputFilename(this.project.getArtifactId()+"-"+this.project.getVersion());
    	}
    	
    	//File input = new File( this.getBuildDirectory(), getDefaultOutputFilename() +".js");
    	File input = new File( this.getWorkDirectory(), getDefaultOutputFilename() +".js");
    	if(!input.exists()) { 
    		return; 
    	}
    	
    	File output = new File( this.getTargetDirectory(),"jsdoc-report" ); 	
    	if(output.exists()) {
    		FileUtils.deleteQuietly(output);
    	}
    	
        getLog().info("Make Js Doc for " + input.getAbsolutePath() );        
        try {            
            int exit = jsdoc.execute("jsdoc", input.getAbsolutePath(), "-d",  output.getAbsolutePath() );
            getLog().debug("Js Doc generation execution exiting with " + exit + " status");
        } catch (MojoExecutionException e) {
            throw new WatchingException("Error during the compilation of " + input.getName(), e);
        }  
    }
    
    private boolean isSkipped(){
    	if ( skipJsDocumentation || skipJsCompilation) {
            getLog().info("\033[31m JS Documentation skipped \033[0m");
            return true;
        } else { 
        	return false;
        }
    }

}
