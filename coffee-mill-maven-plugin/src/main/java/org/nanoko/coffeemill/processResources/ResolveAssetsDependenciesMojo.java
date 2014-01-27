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

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;

/**
 * @goal resolve-dependencies
 * @requiresDependencyResolution test
 *  */

public class ResolveAssetsDependenciesMojo extends AbstractCoffeeMillMojo {
    
	 /**
	 * The Maven Session Object
	 * @parameter expression="${project}"
	 * @readonly
	 */
	//@Parameter(defaultValue="${project}", readonly=true)
	 public MavenProject proj;
	 
	
    public void execute() throws MojoExecutionException, MojoFailureException {        

    	getLog().info("MERCI toto !");
        Set<Artifact> dependencies = proj.getArtifacts();
        getLog().info("DERIEN tata !");
 
        Set<Artifact> keepers = new LinkedHashSet<Artifact>();

        
        // Only retrieve JS & CSS dependencies
        for(Artifact a : dependencies) {
        	getLog().info(a.getFile().getAbsolutePath());
        	if(a.getType().equals("js") || a.getType().equals("css"))
        		keepers.add(a);
        	else getLog().warn(a.getFile().getName() + " dependency can't be resolved");
        }
        
        for( Artifact a : keepers)
        	copyDependencies(a, getWorkDirectory());

    }
    
    public void copyDependencies(Artifact a, File destDirectory){
    	try {
			File f = a.getFile();
    		getLog().info("	Copy " + f.getAbsolutePath() + " to " + destDirectory);
			FileUtils.copyFileToDirectory(f, destDirectory);
    	} catch (IOException e) {}
    }
   
}
