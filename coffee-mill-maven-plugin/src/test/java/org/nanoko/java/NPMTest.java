package org.nanoko.java;


import org.junit.Test;
import org.nanoko.java.NPM;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Check the NPM behavior.
 */
public class NPMTest {

    @Test
    public void testExtractVersionFromPackageJson() {
        File coffeescript = new File("target/test-classes/package-json/coffeescript");
        String version = NPM.getVersionFromNPM(coffeescript);
        assertThat(version).isEqualTo("1.6.3");

        File less = new File("target/test-classes/package-json/less");
        version = NPM.getVersionFromNPM(less);
        assertThat(version).isEqualTo("1.5.0");

        File doesNotExist = new File("target/test-classes/package-json/nope");
        version = NPM.getVersionFromNPM(doesNotExist);
        assertThat(version).isEqualTo("0.0.0");
    }
}
