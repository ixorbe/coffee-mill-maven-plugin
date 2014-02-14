package org.nanoko.coffeemill.mojos.packaging;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.junit.After;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.packaging.PackagerMojo;

public class PackagerMojoTest {
	private final File srcDir = new File("src/test/resources/");
	private final File buildDir = new File("target/test/PackagerMojoTest/www-release");
	private PackagerMojo mojo;
	
	
    @Test
    public void testJavaScriptAggregation() throws MojoExecutionException, MojoFailureException {
    	this.mojo = new PackagerMojo();
    	this.mojo.setBuildDirectory(srcDir);
    	this.mojo.setTargetDirectory(buildDir) ;
    	this.mojo.execute();    	
    	assertTrue(this.mojo.getTargetDirectory().list().length > 0);
    	assertTrue(new File(this.mojo.getTargetDirectory(), "release.zip").exists());
    	
    }
    
    @After
    public void cleanTestDirectory(){
    	if(buildDir.exists()){
    		FileUtils.deleteQuietly(buildDir);
    	}
    }
    
}
