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

package org.nanoko.coffeemill.mojos.stylesheets.css;

import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.After;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.stylesheets.css.CssCompilerMojo;

import java.io.File;
import java.util.Collection;

/**
 * Test the CssCompilerMojo.
 */
public class CssCompilerMojoTest {
	
	private final File workDir = new File("target/test/CssCompilerMojoTest/www");
	private final File stylesDir = new File("src/test/resources/stylesheets");
	
	
    @Test
    public void testCssCompilation() {
        System.out.println("Should compile two css files");

        CssCompilerMojo mojo = new CssCompilerMojo();
        mojo.setStylesheetsDir(stylesDir);
        mojo.setWorkDirectory(workDir);
        try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Collection<File> files = FileUtils.listFiles(mojo.getWorkDirectory(), new String[]{"css"}, true);
        assertTrue(files.size()==4);
    }
    
	
    @Test
    public void testCssCompilationNoSources() {
        System.out.println("Should compile nothing");

        CssCompilerMojo mojo = new CssCompilerMojo();
        mojo.setStylesheetsDir(new File(stylesDir, "nowhere"));
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
    
    @After
	public void cleanTestDirectory() {
        if (workDir.exists())
        	FileUtils.deleteQuietly(workDir);
    }
    
}
