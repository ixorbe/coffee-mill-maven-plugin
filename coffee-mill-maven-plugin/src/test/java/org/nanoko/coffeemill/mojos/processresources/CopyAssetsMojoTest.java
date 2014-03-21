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

package org.nanoko.coffeemill.mojos.processresources;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.processresources.CopyAssetsMojo;

/**
 * Test the CopyAssetsMojo.
 */
public class CopyAssetsMojoTest {
	
	private final File assetsSourceTestDir = new File("src/test/resources/assets");
	private final File workDir = new File("target/test/CopyAssetsMojoTest/www");
	private CopyAssetsMojo mojo;
	
	
	@Before
	public void prepareTestDirectory(){  	
    	this.mojo = new CopyAssetsMojo();        
    	this.mojo.setWorkDirectory(this.workDir);
    	this.mojo.setAssetsDir(assetsSourceTestDir);
    }
	
	
	@Test
    public void testAssetCopy() throws MojoExecutionException, MojoFailureException {
        mojo.execute();

        assertTrue(new File(mojo.getWorkDirectory(), "index.html").isFile());
        assertTrue(new File(mojo.getWorkDirectory(), "img").isDirectory());
        assertTrue(new File(mojo.getWorkDirectory(), "img/demo.png").exists());
    }

	
    @Test
    public void testIgnoredFileDuringCopy() throws MojoExecutionException, MojoFailureException {
        mojo.execute();

        assertFalse(new File(mojo.getWorkDirectory(), "shouldNotBeCopied.css").exists());
        assertFalse(new File(mojo.getWorkDirectory(), "shouldNotBeCopied.js").exists());
    }

    @Test
    public void testAssetCopyWhenAssetDoesNotExist() throws MojoExecutionException, MojoFailureException {
    	mojo.setAssetsDir(new File("src/test/resources/assets_donotexist"));
        mojo.execute();

        assertFalse(mojo.getWorkDirectory().list().length > 0 );
    }
    
    
    @After
	public void cleanTestDirectory() {
		if(this.mojo.getWorkDirectory().exists()){
			FileUtils.deleteQuietly(this.mojo.getWorkDirectory());
		}
	}

}
