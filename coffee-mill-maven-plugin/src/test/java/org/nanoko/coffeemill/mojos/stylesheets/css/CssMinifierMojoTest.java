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

package org.nanoko.coffeemill.mojos.stylesheets.css;

import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.IOException;

import org.nanoko.coffeemill.mojos.stylesheets.css.CssMinifierMojo;

/**
 * Test the CssMinifierMojo.
 */
public class CssMinifierMojoTest {
	
	private final File srcStyleFile = new File("src/test/resources/stylesheets/stuff.css");
	private final String testDir = "target/test/CssMinifierMojoTest";
    private final File workDir = new File(testDir, "www");
    //private final File buildDir = new File(testDir, "www-release");
    
    private CssMinifierMojo mojo;
	
	@Before 
	public void prepareTestDirectory(){  
	    mojo = new CssMinifierMojo();
        mojo.setWorkDirectory(workDir);
        //mojo.setBuildDirectory(buildDir);
        try {
			//FileUtils.copyFileToDirectory( srcStyleFile , buildDir);
			FileUtils.copyFileToDirectory( srcStyleFile , workDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	
    @Test
    public void testCssMinification() throws MojoExecutionException {
        System.out.println("It should minify the CSS");
       
        mojo.inputFilename = "stuff";
		mojo.execute();
		
        assertTrue(new File(mojo.getWorkDirectory(), "stuff-min.css").exists());
    }
    
    @Test
    public void testCssMinificationWithNoSource() throws MojoExecutionException {
        System.out.println("It should abort the CSS minification");

        mojo.inputFilename = "nofile";
		mojo.execute();
		
        assertFalse(new File(mojo.getWorkDirectory(), "nofile-min.css").exists());
    }
    
    @After
	public void cleanTestDirectory()  {
        //clean output
        if (workDir.exists()){
        	FileUtils.deleteQuietly(workDir);
        }
        /*if (buildDir.exists()){
            FileUtils.deleteQuietly(buildDir);
        }*/
    }
}
