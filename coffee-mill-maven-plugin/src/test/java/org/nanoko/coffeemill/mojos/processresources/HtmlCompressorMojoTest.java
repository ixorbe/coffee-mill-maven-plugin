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

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.processresources.CopyAssetsMojo;
import org.nanoko.coffeemill.mojos.processresources.HtmlCompressorMojo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class HtmlCompressorMojoTest {
	
	private final File assetsSourceTestDir = new File("src/test/resources/assets");
	private final File workDir = new File("target/test/HtmlCompressorMojoTest/www");
	private HtmlCompressorMojo mojo;
	
	public HtmlCompressorMojoTest() throws MojoExecutionException{
		CopyAssetsMojo copymojo = new CopyAssetsMojo();
		copymojo.setAssetsDir(assetsSourceTestDir);
		copymojo.setWorkDirectory(workDir);
		copymojo.execute();
	}	
	
	@Before
	public void prepareTestDirectory(){
    	this.mojo = new HtmlCompressorMojo();
    	this.mojo.setWorkDirectory(this.workDir);
    	this.mojo.setAssetsDir(assetsSourceTestDir);
    }
	

    @Test
    public void testHTMLCompression() throws MojoExecutionException, MojoFailureException {
    	System.out.println("\n ==> Should compress the html test file (smaller file size).");
    	
    	mojo.setSkipHtmlCompression(false);
    	
    	Map<String,String> options = new HashMap<>();
    	
    	options.put("preserveLineBreak", "false");
    	options.put("generateStatistics", "true");
    	options.put("removeComments", "true");
    	options.put("removeFormAttributes", "true");
    	options.put("removeHttpProtocol", "true");
    	options.put("removeHttpsProtocol", "true");
    	options.put("removeInputAttributes", "true");
    	options.put("removeIntertagSpaces", "true");
    	options.put("removeJavaScriptProtocol", "true");
    	options.put("removeLinkAttributes", "true");
    	options.put("removeMultiSpaces", "true");
    	options.put("removeQuotes", "true");
    	options.put("removeScriptAttributes", "true");
    	options.put("removeStyleAttributes", "true");
    	options.put("simpleBooleanAttributes", "true");
    	options.put("simpleDoctype", "true");
    	
    	mojo.setHtmlCompressionOptions(options); 

        File file = new File(mojo.getAssetsDir(), "lemonde/le-monde.html");
        long size = file.length();

        mojo.execute();

        file = new File(mojo.getWorkDirectory(), "lemonde/le-monde.html");
        long newSize = file.length();

        // Optimization, so the new size is smaller.
        assertTrue(newSize < size);
    }
    
    @After
	public void cleanTestDirectory() {
		if(this.mojo.getWorkDirectory().exists())
			FileUtils.deleteQuietly(this.mojo.getWorkDirectory());
	}

}