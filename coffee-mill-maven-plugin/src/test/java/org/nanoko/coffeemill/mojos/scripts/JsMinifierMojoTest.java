package org.nanoko.coffeemill.mojos.scripts;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.scripts.js.JsMinifierMojo;

public class JsMinifierMojoTest {

	private final File jsFileToMinify = new File("src/test/resources/js/h-ubu.js");
	private final File workdir = new File("target/test/JsMinifierMojoTest/www");
	private JsMinifierMojo mojo;
	
	@Before
	public void prepareTestDirectory(){  	
    	this.mojo = new JsMinifierMojo();
    	this.mojo.setWorkDirectory(this.workdir);
    	
    	
    	if(this.jsFileToMinify.exists())
			try {
				FileUtils.copyFileToDirectory(this.jsFileToMinify, this.workdir);
			} catch (IOException e) { e.printStackTrace(); } 
    }
	
	//TODO: use Mockito to test if "this.project" exist (on JsMinifierMojo : compile() )
	@Test
    public void testJavaScriptMinification() throws MojoExecutionException, MojoFailureException {  
    	System.out.println("\n ==> Should minify file \"h-ubu.js\" from "+this.workdir);
    	mojo.inputFilename = "h-ubu";
    	this.mojo.execute();    	

    	assertTrue(new File(this.mojo.getWorkDirectory(), "h-ubu-min.js").exists());
    }
	
	
	@After
	public void cleanTestDirectory() {
		if(workdir.exists())
			FileUtils.deleteQuietly(workdir);
	}
	
}
