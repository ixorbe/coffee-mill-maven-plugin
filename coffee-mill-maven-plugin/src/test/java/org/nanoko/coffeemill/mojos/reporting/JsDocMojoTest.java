package org.nanoko.coffeemill.mojos.reporting;


import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JsDocMojoTest {
	private final File jsSourceDir = new File("src/test/resources/jsdoc");
	private final File targetDir = new File("target/test/JsDocMojoTest");
	private JsDocMojo mojo;
	
	
	@Before
	public void prepareTestDirectory(){
		this.mojo = new JsDocMojo();
		this.mojo.setBuildDirectory(jsSourceDir);
		this.mojo.setTargetDirectory(targetDir);
	}
	
	
    @Test
    public void testJsDocGeneration() throws MojoExecutionException, MojoFailureException {
    	System.out.println("==> Should generate javadoc from "+jsSourceDir);
    	this.mojo.setDefaultOutputFilename("test-js-to-doc");
    	mojo.execute();
    	
    	File jsDocReport = new File(targetDir, "jsdoc-report");
    	assertTrue(jsDocReport.exists());
    	assertTrue(new File( jsDocReport, "index.html").exists());
    	assertTrue(new File( jsDocReport, "scripts").exists());
    	assertTrue(new File( jsDocReport, "test-js-to-doc.js.html").exists());
    }
    
    
    @After
    public void cleanTestDirectory(){
    	File jsDocReport = new File(targetDir, "jsdoc-report");
    	if(jsDocReport.exists()){
    		FileUtils.deleteQuietly(jsDocReport);
    	}
    }
    
}
