package packaging;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.junit.Test;
import org.nanoko.coffeemill.mojos.packaging.PackagerMojo;

public class PackagerMojoTest {
	private final File srcDir = new File("src/test/resources/");
	private final File buildDir = new File("target/test/packaging/");
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
    
}
