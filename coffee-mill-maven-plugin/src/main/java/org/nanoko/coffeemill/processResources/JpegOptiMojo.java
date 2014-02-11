/*
 * Copyright 2013 OW2 Nanoko Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanoko.coffeemill.processResources;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.maven.WatchingException;
//import org.nanoko.coffeemill.utils.ExecUtils;
//import org.nanoko.coffeemill.utils.OptionsHelper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * A processor optimizing JPEG files using JpegTran.
 * JpegTran must be installed on the system and available from the path.
 * -> to install : http://saralinux.blogspot.fr/2013/12/installing-jpegtran-on-mac-or-unixlinux.html
 */
@Mojo(name = "optimize-jpeg", threadSafe = false,
requiresDependencyResolution = ResolutionScope.COMPILE,
requiresProject = true,
defaultPhase = LifecyclePhase.COMPILE)
public class JpegOptiMojo extends AbstractCoffeeMillWatcherMojo {

    /**
     * The JpegTran executable file name without extension.
     * This field is not final for testing purpose.
     */
    public static String EXECUTABLE_NAME = "jpegtran";

    /**
     * The JpegTran executable.
     */
    private File jpegTranExec;

    /**
     * Enables verbose mode.
     */
    private boolean verbose;

    public void execute() throws MojoExecutionException {

        jpegTranExec = FSUtils.findExecutableInPath(EXECUTABLE_NAME);

        if (jpegTranExec == null) {
            getLog().error("Cannot optimize JPEG files - jpegtran not installed.");
            return;
        } else {
            getLog().info("Invoking jpegtran : " + jpegTranExec.getAbsolutePath());
            try {
				processAll();
			} catch (WatchingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        //OptionsHelper.getBoolean(options, "verbose", false);
    }
    
    public boolean accept(File file) {
        return jpegTranExec != null
                && FSUtils.isInDirectory(file.getName(), getWorkDirectory())
                && (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg"));
    }
    

    /**
     * Iterates over project resources and optimize all JPEG files.
     * @throws WatchingException 
     *
     * @throws org.nanoko.coffee.mill.processors.Processor.ProcessorException
     */
    public void processAll() throws MojoExecutionException, WatchingException {
        if (jpegTranExec == null) {
            return;
        }
        if(!new File(getBuildDirectory().getAbsoluteFile(), "resources").exists())
        	return;
        
        Iterator<File> files = FileUtils.iterateFiles(new File(getBuildDirectory().getAbsoluteFile(), "resources"), new String[]{"jpg", "jpeg"}, true);
        while (files.hasNext()) {
            File file = files.next();
            try {
				optimize(file);
			} catch (WatchingException e) {
				throw new WatchingException("error during Jpeg optimization of " + file.getName() + " : " + e.getMessage());
			}
        }
    }

    

    

    private void optimize(File file) throws WatchingException {
        File dir = file.getParentFile();

        // Build command line
        CommandLine cmdLine = CommandLine.parse(jpegTranExec.getAbsolutePath());

        if (verbose) {
            cmdLine.addArgument("-verbose");
        }

        cmdLine.addArgument("-copy");
        cmdLine.addArgument("none");

        cmdLine.addArgument("-optimize");

        cmdLine.addArgument("-outfile");
        cmdLine.addArgument("__out.jpeg");

        cmdLine.addArgument(file.getName());

        DefaultExecutor executor = new DefaultExecutor();

        executor.setWorkingDirectory(dir);
        executor.setExitValue(0);
        try {
            getLog().info("Executing " + cmdLine.toString());
            executor.execute(cmdLine);

            // Overwrite the original file
            File out = new File(dir, "__out.jpeg");
            if (out.exists()) {
                FileUtils.copyFile(out, file);
                FileUtils.deleteQuietly(out);
            } else {
                throw new IOException("Output file not found : " + out.getAbsolutePath());
            }

            getLog().info(file.getName() + " optimized");
        } catch (IOException e) {
            throw new WatchingException("Error during JPG optimization of " + file.getAbsolutePath(), e);
        }
    }
    
    
    public boolean fileCreated(File file) throws WatchingException {
        optimize(file);
        return true;
    }

    public boolean fileUpdated(File file) throws WatchingException {
        optimize(file);
        return true;
    }
    
    public boolean fileDeleted(File file) throws WatchingException {

        return true;
    }
}