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

package org.nanoko.coffeemill.mojos.processResources;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.processResources.CopyAssetsMojo;
import org.nanoko.coffeemill.mojos.processResources.HtmlCompressorMojo;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class HtmlCompressorMojoTest {
	
	private final File assetsSourceTestDir = new File("src/test/resources/assets");
	private final File workDir = new File("target/test/HtmlCompressorMojo/www");
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
    	
    	mojo.htmlCompressionOptions = new HashMap<>();
    	mojo.htmlCompressionOptions.put("preserveLineBreak", "false");
    	mojo.htmlCompressionOptions.put("generateStatistics", "true");
    	mojo.htmlCompressionOptions.put("removeComments", "true");
    	mojo.htmlCompressionOptions.put("removeFormAttributes", "true");
    	mojo.htmlCompressionOptions.put("removeHttpProtocol", "true");
    	mojo.htmlCompressionOptions.put("removeHttpsProtocol", "true");
    	mojo.htmlCompressionOptions.put("removeInputAttributes", "true");
    	mojo.htmlCompressionOptions.put("removeIntertagSpaces", "true");
    	mojo.htmlCompressionOptions.put("removeJavaScriptProtocol", "true");
    	mojo.htmlCompressionOptions.put("removeLinkAttributes", "true");
    	mojo.htmlCompressionOptions.put("removeMultiSpaces", "true");
    	mojo.htmlCompressionOptions.put("removeQuotes", "true");
    	mojo.htmlCompressionOptions.put("removeScriptAttributes", "true");
    	mojo.htmlCompressionOptions.put("removeStyleAttributes", "true");
    	mojo.htmlCompressionOptions.put("simpleBooleanAttributes", "true");
    	mojo.htmlCompressionOptions.put("simpleDoctype", "true");

        File file = new File(mojo.getAssetsDir(), "lemonde/le-monde.html");
        long size = file.length();

        mojo.execute();

        file = new File(mojo.getWorkDirectory(), "lemonde/le-monde.html");
        long newSize = file.length();

        // Optimization, so the new size is smaller.
        assertTrue(newSize < size);
    }

}