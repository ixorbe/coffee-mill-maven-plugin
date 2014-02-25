package org.nanoko.coffeemill.mojos.processresources;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.maven.WatchingException;
import org.nanoko.maven.pipeline.Pipeline;
import org.nanoko.maven.pipeline.Pipelines;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;


import java.io.IOException;

/**
 * Watch mode Mojo
 */
@Mojo(name = "watch", threadSafe = false,
requiresDependencyResolution = ResolutionScope.COMPILE,
requiresProject = true
        )
@Execute(phase = LifecyclePhase.PACKAGE)
public class WatchmodeMojo extends AbstractCoffeeMillMojo {

    @Parameter(defaultValue="true")
    protected boolean watchRunServer;

    @Parameter(defaultValue="8234")
    protected int watchJettyServerPort;   

    //The Jetty Server
    private Server server;

    private Pipeline pipeline; 


    public void execute() throws MojoExecutionException {

        try {
            init();
        } catch (WatchingException e) {
            throw new MojoExecutionException("Cannot init watchers on WatchmodeMojo", e);
        }

        if (watchRunServer) {
            try {
                server = new Server(watchJettyServerPort);
                addHandlersToServer();
                startServer();
            } catch (Exception e) {
                throw new MojoExecutionException("Cannot run the jetty server", e);
            } 
        } else {
            try {
                // Pretty long
                Thread.sleep(1000000000); 
            } catch (InterruptedException e) { 
                throw new MojoExecutionException("InterruptedException", e);
            }
        }

        pipeline.shutdown();
    }

    private void init() throws WatchingException {
        // Expand if needed.
        pipeline = Pipelines.watchers(session, new MavenLoggerWrapper(getLog()),basedir).watch();
    }


    private void addHandlersToServer() {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
        try {
            resourceHandler.setResourceBase(this.getWorkDirectory().getCanonicalPath());
        } catch (IOException e) {
            this.getLog().error(e.getMessage(), e);
        }
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
        server.setHandler(handlers);
    }    

    private void startServer() throws WatchingException  {        
        try {
            server.start();
            server.join();
        } catch ( Exception e) {
            // TODO Auto-generated catch block
            throw new WatchingException("Error during start server on watchmode.", e);
        }
    }

}