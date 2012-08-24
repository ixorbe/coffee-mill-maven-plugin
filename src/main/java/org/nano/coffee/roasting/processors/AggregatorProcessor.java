package org.nano.coffee.roasting.processors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.nano.coffee.roasting.mojos.AbstractRoastingCoffeeMojo;
import org.nano.coffee.roasting.utils.OptionsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Common facet of aggregator.
 */
public abstract class AggregatorProcessor extends DefaultProcessor {


    private File output;
    private String extension;
    private List<String> names;

    public List<File> computeFileList(List<String> names, File workDir, File libDir, String extension,
                                      boolean failedOnMissingFile) throws ProcessorException {
        List<File> result = new ArrayList<File>();

        if (names == null || names.isEmpty()) {
            if (workDir.exists()) {
                result.addAll(FileUtils.listFiles(workDir, new String[]{extension}, true));
            } else {
                // Else we just skip.
                getLog().debug("Aggregation skipped - no files to aggregate");
                return result;
            }
        } else {
            if (!workDir.exists()) {
                throw new ProcessorException("Aggregation failed : " + workDir.getAbsolutePath() + " does not exist");
            }

            for (String name : names) {
                File file = resolveFile(name, workDir, libDir, extension);
                if (file == null) {
                    if (failedOnMissingFile) {
                        throw new ProcessorException("Aggregation failed : " + name + " file missing in " + workDir
                            .getAbsolutePath());
                    } else {
                        getLog().warn("Issue detected during aggregation : " + name + " missing");
                    }
                } else {
                    // The file exists.
                    result.add(file);
                }
            }
        }

        return result;
    }

    private File resolveFile(final String name, File workDir, File libDir, String extension) {
        // 1) Check for the file in the workDir with a direct name
        File file = new File(workDir, name);
        if (file.isFile()) { return file; }

        // 2) Try to append the extension
        file = new File(workDir, name + "." + extension);
        if (file.isFile()) { return file; }

        // 3) Search in the libDir as prefix
        if (libDir != null  && libDir.exists()) {
            File[] files = libDir.listFiles(new FilenameFilter() {
                public boolean accept(File file, String s) {
                    return s.startsWith(name);
                }
            });
            if (files.length > 0) { return files[0]; }
        }

        return null;
    }

    public void aggregate(List<File> files, File to) throws FileNotFoundException, ProcessorException {
        if (files.isEmpty()) {
            return;
        }

        getLog().info("Aggregating  " + files.size() + " files into " + to.getAbsolutePath());
        to.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(to);
        try {
            for (File file : files) {
                if (file.getPath().equals(to.getPath())) {
                    continue;
                }
                getLog().debug("Copying " + file.getAbsolutePath() + " to " + to.getName());
                FileInputStream in = new FileInputStream(file);
                try {
                    IOUtils.copy(in, out);
                    separator(out);
                } catch (IOException e) {
                    getLog().error("Aggregation failed : Cannot build aggregate file - " + e.getMessage());
                    throw new ProcessorException("Aggregation failed : cannot build aggregate file", e);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    @Override
    public void configure(AbstractRoastingCoffeeMojo mojo, Map<String, Object> options) {
        super.configure(mojo, options);
        this.extension = OptionsHelper.getString(options, "extension");
        this.output = OptionsHelper.getFile(options, "output");
        this.names = (List<String>) options.get("names");
    }

    @Override
    public boolean accept(File file) {
        return isFileContainedInDirectory(file, mojo.getWorkDirectory()) && file.isFile()  && file.getName().endsWith
                (extension);
    }

    public void aggregate() throws ProcessorException {
        try {
            List<File> files = computeFileList(names, mojo.getWorkDirectory(), mojo.getLibDirectory(), extension, true);
            aggregate(files, output);
        } catch (FileNotFoundException e) {
            throw new ProcessorException("Cannot build aggregate file " + output.getAbsolutePath(), e);
        }
    }

    @Override
    public void processAll() throws ProcessorException {
        if (mojo.getWorkDirectory().exists()) {
            aggregate();
        }
    }

    @Override
    public void fileCreated(File file) throws ProcessorException {
        aggregate();
    }

    @Override
    public void fileUpdated(File file) throws ProcessorException {
        aggregate();
    }

    @Override
    public void fileDeleted(File file) throws ProcessorException {
        aggregate();
    }

    public void tearDown() {
        // Nothing to do.
    }

    public abstract void separator(OutputStream out) throws IOException;

}
