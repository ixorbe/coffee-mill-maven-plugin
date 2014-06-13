/*
 * Copyright 2013-2014 OW2 Nanoko Project
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

package org.nanoko.coffeemill.mojos.scripts.typescript;

import org.apache.commons.io.FileUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.nanoko.java.NPM;
import org.nanoko.maven.WatchingException;

import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;

import java.io.File;
import java.util.Collection;

/**
 * Compiles coffeescript files.
 */
public abstract class AbstractTypeScriptCompilerMojo extends AbstractCoffeeMillWatcherMojo {
    //TODO: set up
    public static final String TYPE_SCRIPT_NPM_NAME = "typescript";
    public static final String TYPE_SCRIPT_NPM_VERSION = "1.0.1";
    public static final String TYPE_SCRIPT_COMMAND = "tsc";

    /**
     * Where are TypeScript files.
     */
    @Parameter(defaultValue= "src/main/typescript", required = true, readonly = true)
    protected File typeScriptDir;

    /**
     * Where are TypeScript files implementing tests.
     */
    @Parameter(defaultValue="src/test/typescript", required = true, readonly = true)
    protected File typeScriptTestDir;

    /**
     * Enables / Disables the coffeescript test compilation.
     * Be aware that this property disables the compilation of test sources only.
     */
    @Parameter(defaultValue="false")
    protected boolean skipTypeScriptTestCompilation;

    protected File defaultOutputDirectory;

    protected NPM typescript;

    // Getters / Setters
    public File getDefaultOutputDirectory(){
        return this.defaultOutputDirectory;
    }

    public void setDefaultOutputDirectory(File outputDirectory){
        this.defaultOutputDirectory = outputDirectory;
        this.defaultOutputDirectory.mkdirs();
    }

    public File getTypeScriptDir() {
    	this.typeScriptDir.mkdirs();
        return this.typeScriptDir;
    }

    public void setTypeScriptDir(File coffeescriptDir){
    	this.typeScriptDir = coffeescriptDir;
    }

    public File getTypeScriptTestDir() {
        this.typeScriptTestDir.mkdirs();
        return this.typeScriptTestDir;
    }

    public void setTypeScriptTestDir(File typesScriptTestDir){
        this.typeScriptTestDir = typesScriptTestDir;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
    }

    public boolean accept(File file) {
        return false;
    }

    public boolean fileCreated(File file) throws WatchingException{
        return false;
    }

    public boolean fileUpdated(File file) throws WatchingException{
        return false;
    }

    public boolean fileDeleted(File file) throws WatchingException{
        return false;
    }

    protected void invokeTypeScriptCompiler(File input, File out) throws MojoExecutionException {
        try {
            int exit = typescript.execute(TYPE_SCRIPT_COMMAND, "--out", out.getAbsolutePath(), input.getAbsolutePath());
            getLog().debug("TypeScript compilation exits with " + exit + " status");
        } catch (MojoExecutionException e) { //NOSONAR
            throw new MojoExecutionException("Error during the compilation of " + input.getName() + " : " + e.getMessage());
        }
    }

    protected void invokeTypeScriptCompilerForDirectory(File dirInput, File dirOut) throws MojoExecutionException {
        if(dirInput.isDirectory() && dirOut.isDirectory()) {
            Collection<File> files = FileUtils.listFiles(dirInput, new String[]{"ts"}, true);
            for (File file : files) {
                if (file.isFile()) {
                    invokeTypeScriptCompiler(
                        file,
                        new File(
                            FSUtils.computeRelativeFile(file, dirInput, dirOut).getParentFile(),
                            typeScriptToJavaScriptExt(file.getName())
                        )
                    );
                }
            }
        }
    }

    protected String typeScriptToJavaScriptExt(String path) {
        return path.substring(0, path.length() - ".ts".length()) + ".js";
    }

    @SuppressWarnings("unused")
    private boolean isSkipped(){
    	return false;
    }
}
