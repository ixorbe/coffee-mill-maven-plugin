package org.nanoko.java;

import org.apache.commons.io.FileUtils;

import org.apache.commons.logging.Log;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.nanoko.java.NPM;
import org.nanoko.java.NodeManager;


import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Check the node manager and node management (especially installation)
 * Most of these tests are 'long' test and need to be launched explicitly.
 */
public class NodeManagerTest {

    File nodeDirectory;
    private NodeManager manager;
    private Log log = new org.apache.commons.logging.impl.SimpleLog("NodeManagerTest");


    @Before
    public void setUp() {
        nodeDirectory = new File(System.getProperty("user.home"));
        nodeDirectory.mkdirs();
        NodeManager.log = log;
        manager = NodeManager.getInstance(nodeDirectory);
    }

    @Test
    public void testInstallation() throws IOException {

        manager.installIfNotInstalled();
        System.out.println("getNodeModulesDirectory ::: "+manager.getNodeModulesDirectory().getAbsolutePath());
        System.out.println("getNodeExecutable ::: "+manager.getNodeExecutable().getAbsolutePath());

        assertThat(manager.getNodeExecutable()).isFile();
        assertThat(manager.getNodeModulesDirectory().getAbsolutePath()).startsWith(nodeDirectory.getAbsolutePath());
    }

    @Test
    public void testInstallationOfCoffeeScript() throws IOException, ParseException {
        manager.installIfNotInstalled();
        NPM npm = NPM.npm(log, "coffee-script", "1.6.3");
        assertThat(npm).isNotNull();
        assertThat(npm.findExecutable("coffee")).isFile();
    }

    @Test
    public void testReinstallation() throws IOException, ParseException {
        manager.installIfNotInstalled();

        NPM npm = NPM.npm(log, "coffee-script", "1.6.3");
        assertThat(npm).isNotNull();
        assertThat(npm.findExecutable("coffee")).isFile();

        NPM npm2 = NPM.npm(log, "coffee-script", "1.6.3");
        assertThat(npm).isEqualTo(npm2);
        assertThat(npm.hashCode()).isEqualTo(npm2.hashCode());
    }

    @Test
    public void testExecution() throws IOException, ParseException {
        manager.installIfNotInstalled();

        NPM npm = NPM.npm(log, "coffee-script", "1.6.3");
        File input = new File("target/test-classes/coffee");
        File output = new File("target/test/coffee");
        output.mkdirs();
        int exit = npm.execute("coffee", "--compile", "--map", "--output", output.getAbsolutePath(),
                input.getAbsolutePath());
        assertThat(exit).isEqualTo(0);
        assertThat(new File(output, "test.js")).isFile();
        assertThat(new File(output, "test.map")).isFile();
    }

}
