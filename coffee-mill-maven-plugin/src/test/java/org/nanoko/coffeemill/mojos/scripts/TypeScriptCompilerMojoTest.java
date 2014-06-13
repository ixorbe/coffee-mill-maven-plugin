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
import org.nanoko.coffeemill.mojos.scripts.typescript.TypeScriptCompilerMojo;

/**
 * Test the TypeScriptCompilerMojo.
 */
public class TypeScriptCompilerMojoTest {
	
	private final File typescriptSourceTestDir = new File("src/test/resources/typescript");
	private final File workDir = new File("target/test/TypeScriptCompilerMojoTest/www");
	private TypeScriptCompilerMojo mojo;

	@Before
	public void prepareTestDirectory(){  	
    	this.mojo = new TypeScriptCompilerMojo();
    	this.mojo.setWorkDirectory(this.workDir);
    	this.mojo.setTypeScriptDir(typescriptSourceTestDir);
    }

    @Test
    public void testTypeScriptCompilation() throws MojoExecutionException, MojoFailureException {
    	System.out.println("\n ==> Should compile 2 files \"test.ts\", \"Person.ts\" to " + this.workDir);
    	mojo.execute();
    	
    	assertTrue(new File(this.mojo.getWorkDirectory().getAbsolutePath() + "/interfaces", "Person.js").exists());
    	assertTrue(new File(this.mojo.getWorkDirectory(), "test.js").exists());
    }

    @Test
    public void testSkippedTypeScriptCompilationBecauseOfEmptyFolder() throws MojoExecutionException, MojoFailureException {
    	System.out.println("\n ==> Should compile nothing : Empty Folder.");
    	this.mojo.setTypeScriptDir(new File(typescriptSourceTestDir, "empty"));
    	mojo.execute();
    	
    	assertTrue( this.mojo.getWorkDirectory().isDirectory()
    			&& this.mojo.getWorkDirectory().list().length == 0);
    }

    @After
	public void cleanTestDirectory() {
		if(this.mojo.getWorkDirectory().exists()){
			FileUtils.deleteQuietly(this.mojo.getWorkDirectory());
		}
	}
}
