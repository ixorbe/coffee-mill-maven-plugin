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
import org.nanoko.coffeemill.mojos.stylesheets.css.CssAggregatorMojo;
import org.openqa.selenium.internal.seleniumemulation.GetBodyText;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Test the CssAggregatorMojo.
 */
public class CssAggregatorMojoTest {

	private final File srcStyleDir = new File("src/test/resources/stylesheets/");
	private final File testWorkDir = new File("target/test/CssAggregatorMojoTest/www");
	private final File testBuildDir = new File("target/test/CssAggregatorMojoTest/www-release");
	private final File libDir = new File(testWorkDir , "libs");
	
	
	@Before 
	public void prepareTestDirectory()  {        
        //copy new ressources
        Collection<File> files = FileUtils.listFiles(srcStyleDir, new String[]{"css"}, true);
        for(File f : files) {
        	try {
				FileUtils.copyFileToDirectory( f , testWorkDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
	
	
    @Test
    public void testCssCompilation() throws MojoExecutionException {
        System.out.println("==> It should aggregate");
        
        CssAggregatorMojo mojo = new CssAggregatorMojo();
        mojo.setWorkDirectory( testWorkDir );
        mojo.setBuildDirectory( testBuildDir );
        mojo.setLibDirectory(this.libDir);
        
        mojo.outputFileName = "test.aggregate";
        mojo.execute();
        
        assertTrue(new File(mojo.getWorkDirectory(), "test.aggregate.css").exists());
    }
    
    /*
    @After
    public void cleanTestDirectory(){
        //clean output
        if (testBuildDir.exists())
        	FileUtils.deleteQuietly(testBuildDir);
        if (testWorkDir.exists())
        	FileUtils.deleteQuietly(testWorkDir);
    }*/
}
