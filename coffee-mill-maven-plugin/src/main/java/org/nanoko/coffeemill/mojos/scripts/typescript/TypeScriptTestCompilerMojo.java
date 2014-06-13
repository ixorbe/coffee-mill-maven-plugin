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

import static org.nanoko.java.NPM.npm;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.maven.WatchingException;

/**
 * Compiles TypeScript files.
 * TypeScript files are generally in the <tt>src/test/typescript</tt> directory. It can be configured using the
 * <tt>typeScriptTestDir</tt> parameter.
 * If the directory does not exist, the compilation is skipped.
 *
 */
@Mojo(name = "test-compile-typescript", threadSafe = false,
requiresDependencyResolution = ResolutionScope.COMPILE,
requiresProject = true,
defaultPhase = LifecyclePhase.TEST_COMPILE)
public class TypeScriptTestCompilerMojo extends AbstractTypeScriptCompilerMojo {

    public void execute() throws MojoExecutionException {
        if(isSkipped()) {
            return;
        }

        if(this.getDefaultOutputDirectory()==null){
            this.setDefaultOutputDirectory(this.getWorkTestDirectory());
        }

        if (!this.typeScriptTestDir.isDirectory()){
            getLog().warn("/!\\ TypeScript Test compilation skipped - " + typeScriptTestDir.getAbsolutePath() + " does not exist !");
            return;
        }

        typescript = npm(new MavenLoggerWrapper(this.getLog()), TYPE_SCRIPT_NPM_NAME, TYPE_SCRIPT_NPM_VERSION);

        getLog().info("Get TypeScript Test files from " + this.typeScriptTestDir.getAbsolutePath());
        invokeTypeScriptCompilerForDirectory(this.typeScriptTestDir, defaultOutputDirectory);

    }

    @Override
    public boolean accept(File file) {
        return !isSkipped()
            && file.getParent().contains( this.typeScriptTestDir.getAbsolutePath() )
            && FSUtils.hasExtension(file, "ts");
    }

    @Override
    public boolean fileCreated(File file) throws WatchingException {
        compile(file);
        return true;
    }

    @Override
    public boolean fileUpdated(File file) throws WatchingException {
        if(fileDeleted(file)) {
            return this.fileCreated(file);
        } else {
            return false;
        }
    }

    @Override
    public boolean fileDeleted(File file) {
        File out = FSUtils.computeRelativeFile(file, this.getTypeScriptTestDir(), getWorkTestDirectory());
        File newName = new File( out.getAbsolutePath().substring(0, out.getAbsolutePath().length() - ".ts".length()) + ".js" );
        if(newName.exists()){
            FileUtils.deleteQuietly(newName);
        }
        return true;
    }

    private void compile(File file) throws WatchingException {
        if (file == null) {
            return;
        }

        File out = FSUtils.computeRelativeFile(file, this.getTypeScriptDir(), getWorkTestDirectory());
        getLog().info("Compiling TypeScript " + file.getAbsolutePath() + " to " + getWorkTestDirectory().getAbsolutePath());

        try {
            invokeTypeScriptCompiler(
                file,
                new File(
                    out.getParentFile(),
                    typeScriptToJavaScriptExt(out.getName())
                )
            );
        } catch (MojoExecutionException e) { //NOSONAR
            throw new WatchingException("Error during the compilation of " + file.getName() + " : " + e.getMessage());
        }
    }

    private boolean isSkipped(){
    	if (skipTypeScriptTestCompilation) {
            getLog().info("\033[31m TypeScript Test Compilation skipped \033[0m");
            return true;
        } else {
        	return false;
        }
    }
}