package org.nanoko.coffeemill.mojos.packaging;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.packaging.PackagerMojo;

public class PackagerMojoTest {
	private final File srcDir = new File("src/test/resources/");
	private final File targetDir = new File("target/test/PackagerMojoTest");
	private PackagerMojo mojo;
	
	@Before
	public void prepareTestDirectory(){
	    this.mojo = new PackagerMojo();
        this.mojo.setWorkDirectory(srcDir);
        this.mojo.setLibDirectory(new File(""));
        this.mojo.setTargetDirectory(targetDir) ;
	}
	
	
    @Test
    public void testJavaScriptAggregation() throws MojoExecutionException, MojoFailureException {
    	this.mojo.setDefaultOutputFilename("release");
    	this.mojo.execute();    
    	
    	assertTrue(this.mojo.getTargetDirectory().list().length > 0);
    	assertTrue(new File(this.mojo.getTargetDirectory(), "release.zip").exists()); 	
    }
    
    
    @After
    public void cleanTestDirectory(){
        File zip = new File(this.mojo.getTargetDirectory(), "release.zip");
    	if(zip.exists()){
    		FileUtils.deleteQuietly(zip);
    	}
    }
    
}
