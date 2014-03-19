/*
 * Copyright 2013-2014 OW2 Nanoko Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanoko.coffeemill.mojos.scripts.coffee;

import static org.nanoko.java.NPM.npm;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.maven.WatchingException;

/**
 * Compiles CoffeeScript files.
 * CoffeeScript files are generally in the <tt>src/test/coffee</tt> directory. It can be configured using the
 * <tt>coffeeScriptTestDir</tt> parameter.
 * If the directory does not exist, the compilation is skipped.
 *
 */
@Mojo(name = "test-compile-coffeescript", threadSafe = false,
requiresDependencyResolution = ResolutionScope.COMPILE,
requiresProject = true,
defaultPhase = LifecyclePhase.TEST_COMPILE)
public class CoffeeScriptTestCompilerMojo extends AbstractCoffeeScriptCompilerMojo {
    
	
    
    public void execute() throws MojoExecutionException {     
        if(isSkipped()) { 
            return; 
        }
        
        if(this.getDefaultOutputDirectory()==null){            
            this.setDefaultOutputDirectory(this.getWorkTestDirectory());
        }
        
        if (!this.coffeeScriptTestDir.isDirectory()){
            getLog().warn("/!\\ CoffeeScript Test compilation skipped - " + coffeeScriptTestDir.getAbsolutePath() + " does not exist !");
            return;
        }
        
        coffee = npm(new MavenLoggerWrapper(this.getLog()), COFFEE_SCRIPT_NPM_NAME, COFFEE_SCRIPT_NPM_VERSION);

        getLog().info("Get CoffeeScript Test files from " + this.coffeeScriptTestDir.getAbsolutePath());
        invokeCoffeeScriptCompilerForDirectory(this.coffeeScriptTestDir, defaultOutputDirectory);

    }
    
    @Override
    public boolean accept(File file) {
        return !isSkipped()
            && file.getParent().contains( this.coffeeScriptTestDir.getAbsolutePath() )
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
        File out = FSUtils.computeRelativeFile(file, this.getCoffeeScriptTestDir(), getWorkTestDirectory());
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
        
        File out = FSUtils.computeRelativeFile(file, this.getCoffeeScriptDir(), getWorkTestDirectory());
        getLog().info("Compiling CoffeeScript " + file.getAbsolutePath() + " to " + getWorkTestDirectory().getAbsolutePath());
        
        try {
            invokeCoffeeScriptCompiler(file, out.getParentFile());
        } catch (MojoExecutionException e) { //NOSONAR
            throw new WatchingException("Error during the compilation of " + file.getName() + " : " + e.getMessage());
        }
    }
    
    private boolean isSkipped(){
    	if (skipCoffeeScriptTestCompilation) {
            getLog().info("\033[31m CoffeeScript Test Compilation skipped \033[37m");
            return true;
        } else {
        	return false;
        }
    }

}