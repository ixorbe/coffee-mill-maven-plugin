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

package org.nanoko.coffee.mill.mojos.compile;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.scripts.coffee.CoffeeScriptCompilerMojo;

/**
 * Test the CoffeeScriptCompilerMojo.
 */
public class CoffeeScriptCompilerMojoTest {

	private final static String basedir = "target/test/CoffeeScriptCompilerMojoTest";
	private final static String coffeeScriptTestDir = "src/test/resources/coffee";
	
    @Test
    public void testCoffeeScriptCompilation() throws MojoExecutionException, MojoFailureException {  	
    	CoffeeScriptCompilerMojo mojo = new CoffeeScriptCompilerMojo();
    	mojo.coffeeScriptDir = new File(coffeeScriptTestDir);
    	
    	mojo.workDir = new File(basedir + "/compilation/www");
        mojo.workDir.mkdirs();
        
    	mojo.execute();
    }
    
    
    @Test
    public void testSkippedCoffeeScriptCompilationBecauseOfMissingFolder() throws MojoExecutionException, MojoFailureException {    	
    	CoffeeScriptCompilerMojo mojo = new CoffeeScriptCompilerMojo();
    	mojo.coffeeScriptDir = new File("coffeeScriptDirDoesNotExist");
        
    	mojo.execute();
    }
    
    @Test
    public void testSkippedCoffeeScriptCompilationBecauseOfEmptyFolder() throws MojoExecutionException, MojoFailureException {    	
    	CoffeeScriptCompilerMojo mojo = new CoffeeScriptCompilerMojo();
    	mojo.coffeeScriptDir = new File(coffeeScriptTestDir + "/empty");
        
    	mojo.workDir = new File(basedir + "/empty/www");
        mojo.workDir.mkdirs();
        
    	mojo.execute();
    }

}
