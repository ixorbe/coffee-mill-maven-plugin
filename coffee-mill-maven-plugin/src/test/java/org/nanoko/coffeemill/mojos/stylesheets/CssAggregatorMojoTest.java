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

package org.nanoko.coffeemill.mojos.stylesheets;

import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.stylesheets.css.CssAggregatorMojo;


import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Test the CssAggregatorMojo.
 */
public class CssAggregatorMojoTest {
	
	private final File testWorkDir = new File("target/test/css/aggregation/tmp/");
	private final File srcStyleDir = new File("src/test/resources/stylesheets/");
	private final File testBuildDir = new File("target/test/css/aggregation/www/");
	private final File libDir = new File(testWorkDir , "libs");
	
	
	@Before 
	public void cleanWorkDirectory()  {
        //clean output
        if (testBuildDir.exists())
        	FileUtils.deleteQuietly(testBuildDir);
        if (testWorkDir.exists())
        	FileUtils.deleteQuietly(testWorkDir);
        
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
    public void testCssCompilation() {
        System.out.println("It should aggregate");
        
        CssAggregatorMojo mojo = new CssAggregatorMojo();
        mojo.setWorkDirectory( testWorkDir );
        mojo.setBuildDirectory( testBuildDir );
        mojo.setLibDirectory(this.libDir);
        
        mojo.outputFileName = "test.aggregate";
        try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertTrue(new File(testBuildDir, "test.aggregate.css").exists());
    }
}
