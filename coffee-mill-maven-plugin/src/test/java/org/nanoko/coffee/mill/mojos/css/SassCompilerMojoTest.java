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
import org.junit.Test;

import org.nanoko.coffeemill.mojos.stylesheets.sass.SassCompilerMojo;

import java.io.File;
import java.util.Collection;

/**
 * Test the SassCompilerMojo.
 */
public class SassCompilerMojoTest {
	
	private final File workDir = new File("target/test/sass/compilation/tmp/");
	private final File stylesDir = new File("src/test/resources/stylesheets");
	
	@Before 
	public void cleanWorkDirectory() {
        if (workDir.exists())
        	FileUtils.deleteQuietly(workDir);
    }
	
	
    @Test
    public void testLessCompilation() {
        System.out.println("Should compile 1 Sass file");

        SassCompilerMojo mojo = new SassCompilerMojo();
        mojo.setStylesheetsDir(stylesDir);
        mojo.setWorkDirectory(workDir);
        try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Collection<File> files = FileUtils.listFiles(mojo.getWorkDirectory(), new String[]{"css"}, true);
        assertTrue(files.size()==1);
    }
    
	
    @Test
    public void testLessCompilationNoSources() {
        System.out.println("Should compile nothing");

        SassCompilerMojo mojo = new SassCompilerMojo();
        mojo.setStylesheetsDir(new File("nowhere"));
        mojo.setWorkDirectory(workDir);
        try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Collection<File> files = FileUtils.listFiles(mojo.getWorkDirectory(), new String[]{"css"}, true);
        assertFalse(files.size() > 0);
    }
}
