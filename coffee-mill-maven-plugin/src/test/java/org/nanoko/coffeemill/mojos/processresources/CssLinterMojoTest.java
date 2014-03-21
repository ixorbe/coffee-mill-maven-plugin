package org.nanoko.coffeemill.mojos.processresources;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;


import org.apache.commons.io.FileUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.nanoko.coffeemill.mojos.stylesheets.css.CssLinterMojo;
import org.nanoko.maven.WatchingException;


public class CssLinterMojoTest {	
	
	private final File testDir = new File("target/test/CssLinterMojoTest/");

	private CssLinterMojo mojo;
	private TestableLoggerWrapper  mylog;
	
	
	@Before
	public void prepareTestDirectory(){  	
    	this.mojo = new CssLinterMojo();
    	this.mojo.setWorkDirectory(testDir);
    	this.mojo.setBuildDirectory(testDir);
    	mylog = new TestableLoggerWrapper(this.mojo.getLog());
    	CssLinterMojo.defaultLogger = mylog;
    }
	
	
	@Test
    public void testCssLintNoError() throws MojoExecutionException, MojoFailureException, WatchingException {  
    	System.out.println("\n ==> Should find no error in file");  
    	try {
			FileUtils.copyFileToDirectory(new File("src/test/resources/stylesheets/test_no_error.css"), testDir);
		} catch (IOException e) { e.printStackTrace(); } 
    	this.mojo.execute();
    	
    	boolean containsOK = false;        
        for(Object line : mylog.historyLogs.toArray()){
            if( ((String)line).contains("Lint Free!")){
                containsOK = true;
                break;
            }
        }
        assertTrue(containsOK);
    }
	
	@Test
    public void testCssError() throws MojoExecutionException, MojoFailureException, WatchingException {  
    	System.out.println("\n ==> Should find no error in file");  
    	try {
			FileUtils.copyFileToDirectory(new File("src/test/resources/stylesheets/test_warning.css"), testDir);
		} catch (IOException e) { e.printStackTrace(); } 
    	this.mojo.execute();
    	
    	boolean containsOK = false;        
        for(Object line : mylog.historyLogs.toArray()){
            if( ((String)line).contains("Lint Free!")){
                containsOK = true;
                break;
            }
        }
        assertFalse(containsOK);
    }
	
	@After
	public void cleanTestDirectory() {
		if(this.mojo.getBuildDirectory().exists()){
			FileUtils.deleteQuietly(this.mojo.getBuildDirectory());
		}
	}
	
}
