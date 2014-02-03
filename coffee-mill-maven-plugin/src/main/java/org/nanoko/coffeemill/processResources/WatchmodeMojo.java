package org.nanoko.coffeemill.processResources;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.maven.WatchingException;
import org.nanoko.maven.pipeline.Pipeline;
import org.nanoko.maven.pipeline.Pipelines;



import java.io.IOException;

/**
 * Run Mojo
 */
@Mojo(name = "run", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true
        )
@Execute(phase = LifecyclePhase.PACKAGE)
public class WatchmodeMojo extends  AbstractCoffeeMillMojo {

    private Pipeline pipeline;

    public void execute() {
       
    	/*try {
            init();
        } catch (WatchingException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
*/
        

        pipeline.shutdown();
    }

    public void init() throws MojoExecutionException, WatchingException {
        // Expand if needed.
        

      //  pipeline = Pipelines.watchers(session, basedir, this).watch();
    }


}
