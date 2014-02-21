package org.nanoko.maven.pipeline;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nanoko.maven.Watcher;
import org.nanoko.maven.WatchingException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Check pipeline behavior.
 */
public class PipelineTest {

    public static final File FAKE = new File("target/fake-source");
    public static final File SOURCES = new File(FAKE, "src/main");

    Pipeline pipeline;
    private SpyWatcher textWatcher;
    private SpyWatcher mdWatcher;
    private Log log;

    @Before
    public void setUp() throws IOException {
    	this.log = new org.apache.commons.logging.impl.SimpleLog("testLogger");
        FileUtils.forceMkdir(SOURCES);
        textWatcher = new SpyWatcher(SOURCES, "txt");
        mdWatcher = new SpyWatcher(SOURCES, "md");
        pipeline = new Pipeline(this.log, FAKE, Arrays.asList(textWatcher, mdWatcher));
        pipeline.watch();
    }

    @After
    public void tearDown() {
        pipeline.shutdown();
        FileUtils.deleteQuietly(FAKE);
    }

    @Test
    public void testAdditionUpdateAndDeleteOfFile() throws IOException {
        File txt = new File(SOURCES, "touch.txt");
        txt.createNewFile();
        assertThat(txt.isFile()).isTrue();
        waitPullPeriod();
        assertThat(textWatcher.added).containsExactly("touch.txt");
        assertThat(mdWatcher.added).isEmpty();

        FileUtils.touch(txt);
        waitPullPeriod();
        assertThat(textWatcher.added).containsExactly("touch.txt");
        assertThat(textWatcher.updated).containsExactly("touch.txt");

        FileUtils.deleteQuietly(txt);
        waitPullPeriod();
        assertThat(textWatcher.added).containsExactly("touch.txt");
        assertThat(textWatcher.updated).containsExactly("touch.txt");
        assertThat(textWatcher.deleted).containsExactly("touch.txt");
    }

    @Test
    public void testAdditionUpdateAndDeleteOfSubFiles() throws IOException {
        File dir = new File(SOURCES, "foo");
        dir.mkdirs();
        File md = new File(SOURCES, "foo/touch.md");
        md.createNewFile();
        assertThat(md.isFile()).isTrue();
        waitPullPeriod();
        assertThat(mdWatcher.added).containsExactly("touch.md");

        FileUtils.touch(md);
        waitPullPeriod();
        assertThat(mdWatcher.added).containsExactly("touch.md");
        assertThat(mdWatcher.updated).containsExactly("touch.md");

        FileUtils.deleteQuietly(md);
        waitPullPeriod();
        assertThat(mdWatcher.added).containsExactly("touch.md");
        assertThat(mdWatcher.updated).containsExactly("touch.md");
        assertThat(mdWatcher.deleted).containsExactly("touch.md");
    }

    private void waitPullPeriod() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class SpyWatcher implements Watcher {

        private final String extension;
        private final File root;
        List<String> added = new ArrayList<>();
        List<String> updated = new ArrayList<>();
        List<String> deleted = new ArrayList<>();

        public SpyWatcher(File root, String extension) {
            this.extension = extension;
            this.root = root;
        }

        @Override
        public boolean accept(File file) {
        	boolean error = false;
        	try {
				error = (FilenameUtils.directoryContains(root.getCanonicalPath(), file.getCanonicalPath()) && file.getName().endsWith("." + extension))?true:false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           return error;
        }

        @Override
        public boolean fileCreated(File file) throws WatchingException {
            added.add(file.getName());
            return true;
        }

        @Override
        public boolean fileUpdated(File file) throws WatchingException {
            updated.add(file.getName());
            return true;
        }

        @Override
        public boolean fileDeleted(File file) throws WatchingException {
            deleted.add(file.getName());
            return true;
        }
    }
}
