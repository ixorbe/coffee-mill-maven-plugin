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

package org.nanoko.coffeemill.mojos.stylesheets.sass;

import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.nanoko.coffeemill.mojos.stylesheets.sass.SassCompilerMojo;

import java.io.File;
import java.util.Collection;

/**
 * Test the SassCompilerMojo.
 */
public class SassCompilerMojoTest {
	
	private final File workDir = new File("target/test/SassCompilerMojoTest/www");
	private final File stylesDir = new File("src/test/resources/stylesheets");
	
	private SassCompilerMojo mojo;
	
	@Before
    public void prepareTestDirectory(){     
        this.mojo = new SassCompilerMojo();        
        this.mojo.setWorkDirectory(this.workDir);
        this.mojo.setStylesheetsDir(stylesDir);
    } 
	
    @Test
    public void testLessCompilation() throws MojoExecutionException{
        System.out.println("Should compile 1 Sass file");

        mojo.execute();

        Collection<File> files = FileUtils.listFiles(mojo.getWorkDirectory(), new String[]{"css"}, true);
        assertTrue(files.size()==1);
    }
    
	
    @Test
    public void testLessCompilationNoSources() throws MojoExecutionException{
        System.out.println("Should compile nothing");

        mojo.setStylesheetsDir(new File(stylesDir, "nowhere"));
        mojo.execute();

        Collection<File> files = FileUtils.listFiles(mojo.getWorkDirectory(), new String[]{"css"}, true);
        assertFalse(files.size() > 0);
    }
    
    @After 
	public void cleanWorkDirectory() {
        if (workDir.exists()){
        	FileUtils.deleteQuietly(workDir);
        }
    }
}
