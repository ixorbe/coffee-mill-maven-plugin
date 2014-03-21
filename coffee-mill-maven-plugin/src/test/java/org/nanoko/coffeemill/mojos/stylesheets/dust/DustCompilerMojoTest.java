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

package org.nanoko.coffeemill.mojos.stylesheets.dust;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nanoko.coffeemill.mojos.scripts.dust.DustCompilerMojo;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

public class DustCompilerMojoTest {

    private final File jsSourceTestDir = new File("src/test/resources/js");
    private final File workDir = new File("target/test/DustCompilerMojoTest/www");
    private DustCompilerMojo mojo;
    
    @Before
    public void prepareTestDirectory(){     
        this.mojo = new DustCompilerMojo();        
        this.mojo.setWorkDirectory(this.workDir);
        this.mojo.setJavaScriptDir(jsSourceTestDir);
    }
    
    @Test
    public void testDustCompilation() throws MojoExecutionException, MojoFailureException {
        mojo.execute();
        File result = new File(mojo.getWorkDirectory(), "sample/templates/mytemplate.js");

        assertThat(result.isFile()).isTrue();

        //check the compiled template name is set to the file name
        //i.e mytemplate is this test
        try {
            assertThat(FileUtils.readFileToString(result).startsWith("(function(){dust.register(\"mytemplate\"")).isTrue();
        } catch (IOException e) {
            //we already have check that the file does exist
        }
    }

    @Test
    public void testWhenJavaScriptDirectoryDoesNotExist() throws MojoExecutionException, MojoFailureException {
        mojo.setJavaScriptDir(new File("src/test/resources/does_not_exist"));
        mojo.execute();
    }
    
    @After
    public void cleanTestDirectory()  {
        //clean output
        if (workDir.exists()){
            FileUtils.deleteQuietly(workDir);
        }
    }
    
}
