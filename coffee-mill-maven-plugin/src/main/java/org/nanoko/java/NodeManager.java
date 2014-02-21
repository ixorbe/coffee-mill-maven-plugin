package org.nanoko.java;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

import org.apache.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * A class managing node and npm
 * Must be sure that .npmrc file does not set the prefix.
 */
public class NodeManager {

    private static final String NODE_DIST = "http://nodejs.org/dist/v";
    private static final String NPM_DIST = "http://nodejs.org/dist/npm/npm-";
    private static String NODE_VERSION = "0.10.24";
    private static String NPM_VERSION = "1.3.23";    

    private static NodeManager singleton= null;

    private static Log log = new org.apache.commons.logging.impl.SimpleLog("default");       

    private final File nodeDirectory;
    private final File npmDirectory;
    private final File nodeModulesDirectory;
    private final File nodeLibDirectory;
    
    private static File userHomeNodeFile = new File(System.getProperty("user.home") + "/.node/" + NODE_VERSION);

    private File nodeExecutable;


    public static Log getLog() {
        return log;
    }

    public static void setLog(Log log) {
        NodeManager.log = log;
    }
    
    public static NodeManager getInstance(){
        return getSingleton(userHomeNodeFile);
    }

    public static NodeManager getInstance(File nodeDirectory){
        return getSingleton(nodeDirectory);
    }

    public static NodeManager getInstance(String NODE_VER, String NPM_VER){
        setVersion(NODE_VER,NPM_VER);
        return getSingleton(userHomeNodeFile);
    }

    public static NodeManager getInstance(String NODE_VER, String NPM_VER,File nodeDirectory){
        setVersion(NODE_VER,NPM_VER);
        return getSingleton(nodeDirectory);
    }

    public static void setVersion(String NODE_VER, String NPM_VER) {
        NODE_VERSION = NODE_VER;
        NPM_VERSION = NPM_VER;
    }

    private static NodeManager getSingleton(File nodeDirectory) {
        if(singleton == null)
            singleton = new NodeManager(nodeDirectory);
        return singleton;
    }


    public NodeManager( File nodeDirectory) {
        this.nodeDirectory = nodeDirectory;
        this.npmDirectory = new File(nodeDirectory, "lib/node_modules/npm/");

        if (!nodeDirectory.exists()) {
            nodeDirectory.mkdirs();
        }

        if (ExecUtils.isWindows()) {
            this.nodeExecutable = new File(nodeDirectory + "/bin", "node.exe");
            nodeLibDirectory = nodeExecutable.getParentFile();
            nodeLibDirectory.mkdirs();
        } else {
            this.nodeExecutable = new File(nodeDirectory + "/bin", "node");
            File nodePrefix = nodeExecutable.getParentFile().getParentFile();
            nodeLibDirectory = new File(nodePrefix, "lib");
            nodeLibDirectory.mkdirs();
        }

        nodeModulesDirectory = new File(nodeLibDirectory, "node_modules");
    }

    /**
     * Installs node in ~/.wisdom/node/$version.
     * The installation process is the following:
     * <ol>
     * <li>download node</li>
     * <li>expand node to the right location</li>
     * <li>download npm</li>
     * <li>expand npm</li>
     * </ol>
     * <p/>
     * Node and npm installation are divided to avoid facing npm corruption on node package.
     *
     * @throws java.io.IOException
     */
    public void installIfNotInstalled() throws IOException {
        if (!nodeExecutable.isFile()) {
            downloadAndInstallNode();
            downloadAndInstallNPM();
        } else {
            log.debug("Node executable : " + nodeExecutable.getAbsolutePath());
        }
    }

    public File getNodeExecutable() {
        return nodeExecutable;
    }

    public File getNodeModulesDirectory() {
        return nodeModulesDirectory;
    }

    private void downloadAndInstallNPM() throws IOException {
        URL url = new URL(NPM_DIST + NPM_VERSION + ".zip");
        File tmp = File.createTempFile("npm", ".zip");

        log.debug("Downloading npm-" + NPM_VERSION + " from " + url.toExternalForm());
        FileUtils.copyURLToFile(url, tmp);
        log.debug("npm downloaded - " + tmp.length() + " bytes");

        final ZipUnArchiver ua = new ZipUnArchiver();
        ua.enableLogging(new PlexusLoggerWrapper(NodeManager.log));
        ua.setOverwrite(true);
        ua.setSourceFile(tmp);
        ua.setDestDirectory(nodeLibDirectory);
        log.debug("Unzipping npm");
        try {
            ua.extract();
        } catch (ArchiverException e) {
            log.error("Cannot unzip NPM", e);
            throw new IOException(e);
        }

    }

    private void downloadAndInstallNode() throws IOException {
        URL url;
        String path;
        String suffixName;
        if (ExecUtils.isWindows()) {
            if (ExecUtils.is64bit()) {
                url = new URL(NODE_DIST + NODE_VERSION + "/x64/node.exe");
            } else {
                url = new URL(NODE_DIST + NODE_VERSION + "/node.exe");
            }
            // Manage download for windows.
            log.debug("Downloading nodejs from " + url.toExternalForm());

            // Create the bin directory
            File bin = new File(nodeDirectory, "bin");
            bin.mkdirs();

            FileUtils.copyURLToFile(url, nodeExecutable);
            log.debug(nodeExecutable.getAbsolutePath() + " was downloaded from " + url.toExternalForm());
            // Try to set the file executable.
            nodeExecutable.setExecutable(true);

            return;
        } else if (ExecUtils.isMac()) {
            if (!ExecUtils.is64bit()) {
                suffixName = "-darwin-x86";                
            } else {
                suffixName = "-darwin-x64";  
            }            
        } else if (ExecUtils.isLinux()) {
            if (!ExecUtils.is64bit()) {
                suffixName = "-linux-x86";                
            } else {
                suffixName = "-linux-x64";
            }            
        } else {
            throw new UnsupportedOperationException("Operating system `" + System.getProperty("os.name") + "` not " +
                    "supported");
        }
        path = "node-v" + NODE_VERSION + suffixName;
        url = new URL(NODE_DIST + NODE_VERSION + "/node-v" + NODE_VERSION + suffixName +".tar.gz");

        File tmp = File.createTempFile("nodejs", ".tar.gz");
        log.debug("Downloading nodejs-" + NODE_VERSION + " from " + url.toExternalForm());
        FileUtils.copyURLToFile(url, tmp);
        log.debug("nodejs downloaded - " + tmp.length() + " bytes");

        File tmpDir = Files.createTempDir();
        tmpDir.mkdirs();

        final TarGZipUnArchiver ua = new TarGZipUnArchiver();
        ua.enableLogging(new PlexusLoggerWrapper(NodeManager.log));
        ua.setSourceFile(tmp);
        ua.setDestDirectory(tmpDir);

        log.debug("Expanding nodejs");
        try {
            ua.extract();
        } catch (Exception e) {
            log.error("Cannot unzip node.js ", e);
            throw new IOException(e);
        }

        // Move files
        File test = new File(tmpDir, path);
        if (!test.isDirectory()) {
            throw new IllegalStateException("Cannot find expanded directory " + test.getAbsolutePath());
        }

        FileUtils.copyDirectory(test, nodeDirectory);

        // Check node executable
        if (!nodeExecutable.isFile()) {
            throw new IllegalStateException("Node executable not found after installation");
        } else {
            nodeExecutable.setExecutable(true);
        }

        // Delete the installed npm if any
        if (npmDirectory.isDirectory()) {
            FileUtils.deleteDirectory(npmDirectory);
        }

    }

}
