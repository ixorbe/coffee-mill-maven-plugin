/*
 * Copyright 2013 OW2 Nanoko Project
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

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.java.NPM;
//import org.nanoko.coffee.mill.processors.CoffeeScriptCompilationProcessor;
//import org.nanoko.coffee.mill.processors.Processor;
//import org.nanoko.coffee.mill.utils.OptionsHelper;
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
defaultPhase = LifecyclePhase.TEST)
public class CoffeeScriptTestCompilerMojo extends AbstractCoffeeMillMojo {

    private static final String COFFEE_SCRIPT_ARTIFACTID = "coffeescript";
    public static final String COFFEE_SCRIPT_COMMAND = "coffee";
    
    /**
     * Enables / Disables the coffeescript compilation.
     * Be aware that this property disables the compilation on both main sources and test sources.
     */
    @Parameter(defaultValue="false")
    protected boolean skipCoffeeScriptCompilation;
    
    /**
     * Where are CoffeeScript files implementing tests.
     */
    @Parameter(defaultValue="src/test/coffee", required = true, readonly = true)
    private File coffeeScriptTestDir;
    
    
    private NPM coffee;
    
    
    /**
     * Enables / Disables the coffeescript test compilation.
     * Be aware that this property disables the compilation of test sources only.
     */
    @Parameter(defaultValue="false")
    protected boolean skipCoffeeScriptTestCompilation;
    

    public void execute() throws MojoExecutionException, MojoFailureException {
    	if (!isSkipped()) {
            return;
        }    	
    	
    	 if (!coffeeScriptTestDir.exists()) {
             return;
         }
    	
    	Collection<File> files = FileUtils.listFiles(this.coffeeScriptTestDir, new String[]{"coffee"}, true);
		
		if(files.isEmpty()){
			getLog().warn("/!\\ CoffeeScript sources directory " + this.coffeeScriptTestDir.getAbsolutePath() + " is empty !");
			return;
		}
		
		for(File file : files){
	        getLog().info("Compiling CoffeeScript " + file.getAbsolutePath() + " to " + getWorkDirectory().getAbsolutePath());
	
	        try {
	            invokeCoffeeScriptCompiler(file, getWorkDirectory());
	        } catch (MojoExecutionException e) { //NOSONAR
	            throw new MojoExecutionException("Error during the compilation of " + file.getName() + " : " + e.getMessage());
	        }
		}
    }
    
    
    private void invokeCoffeeScriptCompiler(File input, File out) throws MojoExecutionException {
        int exit = coffee.execute(COFFEE_SCRIPT_COMMAND, "--compile",/* "--map",*/ "--output", out.getAbsolutePath(),
                input.getAbsolutePath());
        getLog().debug("CoffeeScript compilation exits with " + exit + " status");
    }
    
    
    private boolean isSkipped(){
    	if (skipCoffeeScriptTestCompilation) {
            getLog().info("\033[31m CoffeeScript Test Compilation skipped \033[37m");
            return true;
        }
    	else return false;
    }

}