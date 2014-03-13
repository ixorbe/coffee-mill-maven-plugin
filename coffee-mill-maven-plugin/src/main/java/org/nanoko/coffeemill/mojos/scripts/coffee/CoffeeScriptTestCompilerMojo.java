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

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

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
public class CoffeeScriptTestCompilerMojo extends AbstractCoffeeScriptCompilerMojo {
    
	/**
     * Enables / Disables the coffeescript test compilation.
     * Be aware that this property disables the compilation of test sources only.
     */
    @Parameter(defaultValue="false")
    protected boolean skipCoffeeScriptTestCompilation;
    
    /**
     * Where are CoffeeScript files implementing tests.
     */
    @Parameter(defaultValue="src/test/coffee", required = true, readonly = true)
    protected File coffeeScriptTestDir;
    

    public void execute() throws MojoExecutionException {
    	this.setCoffeeScriptDir(coffeeScriptTestDir);
    	this.setDefaultOutputDirectory(getWorkTestDirectory());
    	super.execute();
    }
    
    @SuppressWarnings("unused")
	private boolean isSkipped(){
    	if (skipCoffeeScriptTestCompilation) {
            getLog().info("\033[31m CoffeeScript Test Compilation skipped \033[37m");
            return true;
        } else {
        	return false;
        }
    }

}