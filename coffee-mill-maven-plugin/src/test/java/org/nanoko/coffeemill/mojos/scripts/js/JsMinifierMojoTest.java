package org.nanoko.coffeemill.mojos.scripts.js;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JsMinifierMojoTest {

	private final File jsFileToMinify = new File("src/test/resources/js/h-ubu.js");
	private final String testDir = "target/test/JsMinifierMojoTest/";
	private final File buildDir = new File(testDir + "/www");
	private JsMinifierMojo mojo;
	
	@Before
	public void prepareTestDirectory(){  	
    	this.mojo = new JsMinifierMojo();
    	this.mojo.setBuildDirectory(this.buildDir);
    	
    	
    	if(this.jsFileToMinify.exists())
			try {
				FileUtils.copyFileToDirectory(this.jsFileToMinify, this.buildDir);
			} catch (IOException e) { e.printStackTrace(); } 
    }
	
	@Test
    public void testJavaScriptMinification() throws MojoExecutionException, MojoFailureException {  
    	System.out.println("\n ==> Should minify file \"h-ubu.js\" from "+this.buildDir);  
    	this.mojo.inputFileName = "h-ubu";
    	this.mojo.execute();    	

    	assertTrue(new File(this.mojo.getBuildDirectory(), "h-ubu-min.js").exists());
    }
	
	@After
	public void cleanTestDirectory() {
		if(this.mojo.getBuildDirectory().exists())
			FileUtils.deleteQuietly(this.mojo.getBuildDirectory());
	}
	
}
