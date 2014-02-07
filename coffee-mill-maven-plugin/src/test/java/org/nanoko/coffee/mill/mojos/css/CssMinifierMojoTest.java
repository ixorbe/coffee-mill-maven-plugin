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

package org.nanoko.coffee.mill.mojos.css;

import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.io.File;
import java.io.IOException;

import org.nanoko.coffeemill.mojos.stylesheets.css.CssMinifierMojo;




/**
 * Test the CssMinifierMojo.
 */
public class CssMinifierMojoTest {
	private final File srcStyleFile = new File("src/test/resources/stylesheets/stuff.css");
	private final File testBuildDir = new File("target/test/css/minification/www/");
	
	@Before 
	public void cleanWorkDirectory()  {
        //clean output
        if (testBuildDir.exists())
        	FileUtils.deleteQuietly(testBuildDir);
        
        try {
			FileUtils.copyFileToDirectory( srcStyleFile , testBuildDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	
    @Test
    public void testCssMinification() {
        System.out.println("It should minify the CSS");

        CssMinifierMojo mojo = new CssMinifierMojo();
        mojo.setBuildDirectory(testBuildDir);
        mojo.inputFilename = "stuff";
        try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertTrue(new File(testBuildDir, "stuff-min.css").exists());
    }
    
    @Test
    public void testCssMinificationWithNoSource() {
        System.out.println("It should abort the CSS minification");

        CssMinifierMojo mojo = new CssMinifierMojo();
        mojo.setBuildDirectory(testBuildDir);
        mojo.inputFilename = "nofile";
        try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertFalse(new File(testBuildDir, "nofile-min.css").exists());
    }
}
