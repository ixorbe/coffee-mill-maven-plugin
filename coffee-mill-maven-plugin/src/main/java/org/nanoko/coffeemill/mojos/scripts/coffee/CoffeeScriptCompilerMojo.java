package org.nanoko.coffeemill.mojos.scripts.coffee;

import static org.nanoko.java.NPM.npm;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.maven.WatchingException;

import java.io.File;

/**
 * Compiles coffeescript files.
 */
@Mojo(name = "compile-coffeescript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class CoffeeScriptCompilerMojo extends AbstractCoffeeScriptCompilerMojo {
	 
	 public void execute() throws MojoExecutionException {     
        if(isSkipped()) { 
            return; 
        }
        
        if(this.getDefaultOutputDirectory()==null){            
            this.setDefaultOutputDirectory(this.getWorkDirectory());
        }
        
        if (!this.coffeeScriptDir.isDirectory()){
            getLog().warn("/!\\ CoffeeScript compilation skipped - " + coffeeScriptDir.getAbsolutePath() + " does not exist !");
            return;
        }
        
        coffee = npm(new MavenLoggerWrapper(this.getLog()), COFFEE_SCRIPT_NPM_NAME, COFFEE_SCRIPT_NPM_VERSION);

        getLog().info("Get CoffeeScript files from " + this.coffeeScriptDir.getAbsolutePath());
        invokeCoffeeScriptCompilerForDirectory(this.coffeeScriptDir, defaultOutputDirectory);

    }
    
	 @Override
    public boolean accept(File file) {
        return !isSkipped()
        	&& file.getParent().contains( this.coffeeScriptDir.getAbsolutePath() )
        	&& FSUtils.hasExtension(file, "coffee");
    }
    
	@Override
    public boolean fileCreated(File file) throws WatchingException {
        compile(file);
        return true;
    }

	@Override
    public boolean fileUpdated(File file) throws WatchingException {
	    if(fileDeleted(file)) {
            return this.fileCreated(file);
        } else {
            return false;
        }
    }

	@Override
    public boolean fileDeleted(File file) {
        File out = FSUtils.computeRelativeFile(file, this.getCoffeeScriptDir(), getWorkDirectory());
        File newName = new File( out.getAbsolutePath().substring(0, out.getAbsolutePath().length() - ".coffee".length()) + ".js" );
        if(newName.exists()){
            FileUtils.deleteQuietly(newName);
        }
        return true;
    }

    
    private void compile(File file) throws WatchingException {
        if (file == null) { 
            return; 
        }
        
        File out = FSUtils.computeRelativeFile(file, this.getCoffeeScriptDir(), getWorkDirectory());
        getLog().info("Compiling CoffeeScript " + file.getAbsolutePath() + " to " + getWorkDirectory().getAbsolutePath());
        
        try {
            invokeCoffeeScriptCompiler(file, out.getParentFile());
        } catch (MojoExecutionException e) { //NOSONAR
            throw new WatchingException("Error during the compilation of " + file.getName() + " : " + e.getMessage());
        }
    }
    
    private boolean isSkipped(){
    	if (skipJsCompilation) {
            getLog().info("\033[31m CoffeeScript Compilation skipped \033[0m");
            return true;
        } else {
        	return false;
        }
    }

}
