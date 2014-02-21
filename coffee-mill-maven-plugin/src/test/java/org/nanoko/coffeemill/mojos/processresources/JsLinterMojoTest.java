package org.nanoko.coffeemill.mojos.processresources;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.nanoko.coffeemill.mojos.scripts.js.JsLinterMojo;
import org.nanoko.maven.WatchingException;


public class JsLinterMojoTest {

	private final File src_jsFileError = new File("src/test/resources/js/js_linterror.js");
	private final File src_jsFileNoError = new File("src/test/resources/js/js_lintnoerror.js");
	
	private final File testDir = new File("target/test/JsLinterMojoTest/");

	private JsLinterMojo mojo;
	
	private TestableLoggerWrapper mylog;
	
	
	@Before
	public void prepareTestDirectory(){  	
    	this.mojo = new JsLinterMojo();
    	this.mojo.setWorkDirectory(testDir);
    	this.mojo.setBuildDirectory(testDir);
    	mylog = new TestableLoggerWrapper(this.mojo.getLog());
    	this.mojo.setDefaultLogger(mylog);
    }
	
	@Test
    public void testJsLintError() throws MojoExecutionException, MojoFailureException, WatchingException {  
    	System.out.println("\n ==> Should says errors in file");
    	
    	try {
    		FileUtils.copyFileToDirectory(this.src_jsFileError, testDir);
    	} catch (IOException e) { e.printStackTrace(); } 
    	
    	this.mojo.execute();
    	assertFalse( ((String) mylog.historyLogs.toArray()[2]).contains("is OK"));
    }
	
	@Test
    public void testJsLintNoErrror() throws MojoExecutionException, MojoFailureException, WatchingException {  
    	System.out.println("\n ==> Should find no error in file");  
    	try {
			FileUtils.copyFileToDirectory(this.src_jsFileNoError, testDir);
		} catch (IOException e) { e.printStackTrace(); } 
    	this.mojo.execute();
    	assertTrue( ((String) mylog.historyLogs.toArray()[2]).contains("is OK"));
    }
	
	@After
	public void cleanTestDirectory() {
		if(this.mojo.getBuildDirectory().exists())
			FileUtils.deleteQuietly(this.mojo.getBuildDirectory());
	}
	
}
