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
package org.nanoko.coffeemill.mojos.processResources;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.coffeemill.mojos.AbstractCoffeeMillMojo;

@Mojo( name = "resolve-dependencies", requiresDependencyResolution = ResolutionScope.TEST) 
public class ResolveAssetsDependenciesMojo extends AbstractCoffeeMillMojo {


	File outputDirectory=null;
    public void execute() throws MojoExecutionException, MojoFailureException {   
    	if(outputDirectory  == null){
    		outputDirectory = this.getLibDirectory();
    	}
    	
    	@SuppressWarnings("unchecked")
		Set<Artifact> dependencies = this.project.getArtifacts();
        Set<Artifact> keepers = new LinkedHashSet<Artifact>();
        
        // Only retrieve JS & CSS dependencies
        for(Artifact a : dependencies) {
        	if(a.getType().equals("js") || a.getType().equals("css")){
        		keepers.add(a);
        	} else {
        		getLog().warn(a.getFile().getName() + " dependency can't be resolved");
        	}
        }
        if(keepers.size()>0) {
        	copyDependencies(keepers);
        }
    }
    
    public void copyDependencies(Set<Artifact> artifacts){
    	if(!outputDirectory.exists()) {
    		outputDirectory.mkdirs();
    	}
    	
    	for( Artifact a : artifacts) {
	    	try {
				File f = a.getFile();
				File out  = new File(outputDirectory, a.getArtifactId()+"."+a.getType());
	    		getLog().info("	Copy " + f.getAbsolutePath() + " to " + outputDirectory);
				FileUtils.copyFile(f, out);
	    	} catch (IOException e) {}
    	}
    }
   
}
