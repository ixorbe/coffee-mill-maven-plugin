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

import org.nanoko.coffeemill.mojos.stylesheets.less.LessCompilerMojo;

import java.io.File;
import java.util.Collection;

/**
 * Test the LessCompilerMojo.
 */
public class LessCompilerMojoTest {
	
	private final File workDir = new File("target/test/less/compilation/tmp/");
	private final File stylesDir = new File("src/test/resources/stylesheets");
	
	@Before 
	public void cleanWorkDirectory() {
        if (workDir.exists())
        	FileUtils.deleteQuietly(workDir);
    }
	
	
    @Test
    public void testLessCompilation() {
        System.out.println("Should compile three less files");

        LessCompilerMojo mojo = new LessCompilerMojo();
        mojo.stylesheetsDir = stylesDir;
        mojo.workDir = workDir;
        mojo.workDir.mkdirs();
        try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Collection<File> files = FileUtils.listFiles(mojo.getWorkDirectory(), new String[]{"css"}, true);
        assertTrue(files.size()==3);
    }
    
	
    @Test
    public void testLessCompilationNoSources() {
        System.out.println("Should compile nothing");

        LessCompilerMojo mojo = new LessCompilerMojo();
        mojo.stylesheetsDir = new File("nowhere");
        mojo.workDir = workDir;
        mojo.workDir.mkdirs();
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
