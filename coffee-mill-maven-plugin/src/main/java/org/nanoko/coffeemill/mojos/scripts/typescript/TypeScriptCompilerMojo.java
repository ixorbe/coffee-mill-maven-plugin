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

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.maven.WatchingException;

import java.io.File;

/**
 * Compiles TypeScript files.
 */
@Mojo(name = "compile-typescript", threadSafe = false,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true,
        defaultPhase = LifecyclePhase.COMPILE)
public class TypeScriptCompilerMojo extends AbstractTypeScriptCompilerMojo {
	
	public void execute() throws MojoExecutionException {
        if (isSkipped()) {
            return;
        }

        if( this.getDefaultOutputDirectory() == null) {
            this.setDefaultOutputDirectory(this.getWorkDirectory());
        }

        if (!this.typeScriptDir.isDirectory()){
            getLog().warn("/!\\ TypeScript compilation skipped - " + typeScriptDir.getAbsolutePath() + " does not exist !");
            return;
        }

        typescript = npm(new MavenLoggerWrapper(this.getLog()), TYPE_SCRIPT_NPM_NAME, TYPE_SCRIPT_NPM_VERSION);

        getLog().info("Get TypeScript files from " + this.typeScriptDir.getAbsolutePath());
        invokeTypeScriptCompilerForDirectory(this.typeScriptDir, defaultOutputDirectory);

    }

	@Override
    public boolean accept(File file) {
        return !isSkipped()
        	&& file.getParent().contains( this.typeScriptDir.getAbsolutePath() )
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
        File out = FSUtils.computeRelativeFile(file, this.getTypeScriptDir(), getWorkDirectory());
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

        File out = FSUtils.computeRelativeFile(file, this.getTypeScriptDir(), getWorkDirectory());
        getLog().info("Compiling TypeScript " + file.getAbsolutePath() + " to " + getWorkDirectory().getAbsolutePath());

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
    	if (skipJsCompilation) {
            getLog().info("\033[31m TypeScript Compilation skipped \033[0m");
            return true;
        } else {
        	return false;
        }
    }
}
