package org.nanoko.coffeemill.mojos.scripts.coffee;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.coffeemill.utils.FSUtils;

import org.nanoko.maven.Watcher;
import org.nanoko.maven.WatchingException;
import org.nanoko.maven.pipeline.Watchers;

import java.io.File;

/**
 * Compiles coffeescript files.
 */
@Mojo(name = "compile-coffeescript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class CoffeeScriptCompilerMojo extends AbstractCoffeeScriptCompilerMojo implements Watcher {
	   
	 public void setSession(MavenSession session) {
	     this.session = session;
	     Watchers.add(session, this);
	 }
	
	 public void removeFromWatching() {
	     Watchers.remove(session, this);
	 }
    
    public boolean accept(File file) {
        return !isSkipped() 
        	//&& FSUtils.isInDirectory(file.getName(), this.coffeeScriptDir) 
        	&& file.getParent().contains( this.coffeeScriptDir.getAbsolutePath() )
        	&& FSUtils.hasExtension(file, "coffee");
    }
    
    public boolean fileCreated(File file) throws WatchingException {
        compile(file);
        return true;
    }


    public boolean fileUpdated(File file) throws WatchingException {
        compile(file);
        return true;
    }

    public boolean fileDeleted(File file) {
    	File deleted = new File(this.getWorkDirectory().getAbsolutePath(), FilenameUtils.getBaseName(file.getName()) + ".js");
        if (deleted.isFile()){
        	getLog().info("deleted File : "+deleted.getName());    	
        	FileUtils.deleteQuietly(deleted); 
        }
        return true;
    }

    
    private void compile(File file) throws WatchingException {
        if (file == null) { return; }
        
        File out = FSUtils.computeRelativeFile(file, this.getCoffeeScriptDir(), getWorkDirectory());
        getLog().info("Compiling CoffeeScript " + file.getAbsolutePath() + " to " + getWorkDirectory().getAbsolutePath());

        try {
            invokeCoffeeScriptCompiler(file, out);
        } catch (MojoExecutionException e) { //NOSONAR
            throw new WatchingException("Error during the compilation of " + file.getName() + " : " + e.getMessage());
        }
    }
    
    private boolean isSkipped(){
    	if (skipJsCompilation) {
            getLog().info("\033[31m CoffeeScript Compilation skipped \033[37m");
            return true;
        } else {
        	return false;
        }
    }

}
