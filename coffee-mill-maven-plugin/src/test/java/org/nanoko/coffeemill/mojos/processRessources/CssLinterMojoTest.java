package org.nanoko.coffeemill.mojos.processRessources;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;


import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.nanoko.coffeemill.mojos.stylesheets.css.CssLinterMojo;
import org.nanoko.maven.WatchingException;


public class CssLinterMojoTest {

	private final File src_cssFile = new File("src/test/resources/stylesheets/stuff.css");
	
	private final File testDir = new File("target/test/CssLinterMojoTest/");

	private CssLinterMojo mojo;
	private TestableLoggerWrapper  mylog;
	
	
	@Before
	public void prepareTestDirectory(){  	
    	this.mojo = new CssLinterMojo();
    	this.mojo.setWorkDirectory(testDir);
    	this.mojo.setBuildDirectory(testDir);
    	mylog = new TestableLoggerWrapper(this.mojo.getLog());
    	this.mojo.defaultLogger = mylog;
    }
	
	
	@Test
    public void testJavaScriptMinification() throws MojoExecutionException, MojoFailureException, WatchingException {  
    	System.out.println("\n ==> Should find no error in file");  
    	try {
			FileUtils.copyFileToDirectory(this.src_cssFile, testDir);
		} catch (IOException e) { e.printStackTrace(); } 
    	this.mojo.execute();
    	System.out.println(mylog.historyLogs);
    	//assertTrue( ((String) mylog.historyLogs.toArray()[2]).contains("is OK"));
    }
	
	@After
	public void cleanTestDirectory() {
		if(this.mojo.getBuildDirectory().exists())
			FileUtils.deleteQuietly(this.mojo.getBuildDirectory());
	}
	
}
