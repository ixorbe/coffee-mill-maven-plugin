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
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.stylesheets.css.CssCompilerMojo;

import java.io.File;
import java.util.Collection;

/**
 * Test the CoffeeScriptCompilerMojo.
 */
public class CssCompilerMojoTest {
	
	@Before 
	public void cleanWorkDirectory() {
        File out = new File("target/test/css/compilation/tmp/");
        if (out.exists())
        	FileUtils.deleteQuietly(out);
    }
	
	@Ignore
    @Test
    public void testCssCompilation() {
        System.out.println("Should compile two css files");
        String basedir = "target/test/css/compilation/";
        CssCompilerMojo mojo = new CssCompilerMojo();
        mojo.stylesheetsDir = new File("src/test/resources/stylesheets");
        mojo.workDir = new File(basedir+"tmp/");
        mojo.workDir.mkdirs();
        try {
			mojo.execute();
		} catch (MojoExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Collection<File> files = FileUtils.listFiles(mojo.getWorkDirectory(), new String[]{"css"}, true);
        assertTrue(files.size()==2);
    }
    
	@Ignore
    @Test
    public void testCssCompilationNoSources() {
        System.out.println("Should compile nothing");
        String basedir = "target/test/css/compilation/";
        CssCompilerMojo mojo = new CssCompilerMojo();
        mojo.stylesheetsDir = new File("src/test/resources/js");
        mojo.workDir = new File(basedir+"tmp/");
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
