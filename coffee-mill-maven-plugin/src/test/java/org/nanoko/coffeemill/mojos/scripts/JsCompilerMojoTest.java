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

package org.nanoko.coffeemill.mojos.scripts;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.scripts.js.JsCompilerMojo;

/**
 * Test the JavaScriptCompilerMojo.
 */
public class JsCompilerMojoTest {
	
	private final File jsSourceTestDir = new File("src/test/resources/js");
	private final String testDir = "target/test/JSCompilerMojoTest";
	private final File workDir = new File(testDir, "tmp");
	private JsCompilerMojo mojo;
	
	@Before
	public void prepareTestDirectory(){  	
    	this.mojo = new JsCompilerMojo();        
    	this.mojo.setWorkDirectory(this.workDir);
    	this.mojo.setJavaScriptDir(jsSourceTestDir);
    }
	
	
    @Test
    public void testJavaScriptCompilation() throws MojoExecutionException, MojoFailureException {  
    	System.out.println("\n ==> Should copy 2 files \"test.js\", \"test2.js\" from "+this.jsSourceTestDir+" to "+this.workDir);     
    	this.mojo.execute();    	

    	//assertTrue(new File(this.mojo.getWorkDirectory(), "test.js").exists());
    	//assertTrue(new File(this.mojo.getWorkDirectory(), "test2.js").exists());
    }
    
    
    @Test
    public void testSkippedJavaScriptCompilationBecauseOfEmptyFolder() throws MojoExecutionException, MojoFailureException {    	    	
    	System.out.println("\n ==> Should copy nothing : Empty Folder.");
    	this.mojo.setJavaScriptDir(new File(jsSourceTestDir, "empty"));
    	this.mojo.execute();
    	
    	//assertTrue( this.mojo.getWorkDirectory().isDirectory()
    		//	&& this.mojo.getWorkDirectory().list().length==0 );
    }
    
    
    @After
	public void cleanTestDirectory() {
		if(this.mojo.getWorkDirectory().exists())
			FileUtils.deleteQuietly(this.mojo.getWorkDirectory());
	}

}
