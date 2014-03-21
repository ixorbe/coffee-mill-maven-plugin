package org.nanoko.coffeemill.mojos.packaging;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PackageAssetsMojoTest {
	
	
	// normally 'assetsDirectory' for packaging is 'workDirectory" to copy optimized .jpeg, .png & .html files
	// but need to call 'copyAssets' and 'OptiJpeg', 'OptiPng' and 'HtmlCompressor' mojo before
	// so we just test the copy feature
	private final File assetsSourceTestDir = new File("src/test/resources/assets");
	private final File buildDir = new File("target/test/PackageAssetsMojoTest/www-release");
	private PackageAssetsMojo mojo;
	
	@Before
	public void prepareTestDirectory(){
    	this.mojo = new PackageAssetsMojo();
    	this.mojo.setWorkDirectory(assetsSourceTestDir);
    	this.mojo.setBuildDirectory(this.buildDir);
    	
    }
	
    @Test
    public void testJavaScriptAggregation() throws MojoExecutionException, MojoFailureException {
    	System.out.println("\n ==> Should copy all assets files (except .js and .css files from main assets directory).");
    	
    	this.mojo.execute();  
    	
    	assertTrue(this.mojo.getTargetDirectory().list().length > 0);
    	assertFalse(new File(this.mojo.getBuildDirectory(), "shouldNotBeCopied.js").exists());
    	assertFalse(new File(this.mojo.getBuildDirectory(), "shouldNotBeCopied.css").exists());
    	assertTrue(new File(this.mojo.getBuildDirectory().getAbsolutePath()+"/lemonde/le-monde_files/require.js").exists());
    	
    }
    
    @After
	public void cleanTestDirectory() {
		if(this.mojo.getBuildDirectory().exists()){
			FileUtils.deleteQuietly(this.mojo.getBuildDirectory());
		}
	}
    
}
