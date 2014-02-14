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

package org.nanoko.coffeemill.mojos.scripts;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.scripts.coffee.CoffeeScriptCompilerMojo;

/**
 * Test the CoffeeScriptCompilerMojo.
 */
public class CoffeeScriptCompilerMojoTest {
	
	private final File coffeescriptSourceTestDir = new File("src/test/resources/coffee");
	private final File workDir = new File("target/test/CoffeeScriptCompilerMojoTest/www");
	private CoffeeScriptCompilerMojo mojo;
	
	
	
	@Before
	public void prepareTestDirectory(){  	
    	this.mojo = new CoffeeScriptCompilerMojo();        
    	this.mojo.setWorkDirectory(this.workDir);
    	this.mojo.setCoffeeScriptDir(coffeescriptSourceTestDir);
    }
	
	
    @Test
    public void testCoffeeScriptCompilation() throws MojoExecutionException, MojoFailureException {
    	System.out.println("\n ==> Should compile 3 files \"FeedEntry.coffee\", \"FeedReader.coffee\", \"SyndicationService.coffee\" to "+this.workDir);       
    	mojo.execute();
    	
    	assertTrue(new File(this.mojo.getWorkDirectory(), "FeedEntry.js").exists());
    	assertTrue(new File(this.mojo.getWorkDirectory(), "FeedReader.js").exists());
    	assertTrue(new File(this.mojo.getWorkDirectory(), "SyndicationService.js").exists());
    }
    
        
    @Test
    public void testSkippedCoffeeScriptCompilationBecauseOfEmptyFolder() throws MojoExecutionException, MojoFailureException {
    	System.out.println("\n ==> Should compile nothing : Empty Folder.");
    	this.mojo.setCoffeeScriptDir(new File(coffeescriptSourceTestDir, "empty"));
    	mojo.execute();
    	
    	assertTrue( this.mojo.getWorkDirectory().isDirectory()
    			&& this.mojo.getWorkDirectory().list().length==0 );
    }
    
    
    @After
	public void cleanTestDirectory() {
		if(this.mojo.getWorkDirectory().exists())
			FileUtils.deleteQuietly(this.mojo.getWorkDirectory());
	}
    
    

}
