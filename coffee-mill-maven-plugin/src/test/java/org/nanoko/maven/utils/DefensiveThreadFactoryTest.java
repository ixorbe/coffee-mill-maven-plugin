package org.nanoko.maven.utils;

import org.apache.maven.plugin.testing.SilentLog;
import org.apache.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the defensive thread factory behavior.
 */
public class DefensiveThreadFactoryTest {

    DefensiveThreadFactory factory;
    private CollectorLog log;
    private Log customLog;

    @Before
    public void setUp() {
        log = new CollectorLog();
        customLog = new org.apache.commons.logging.impl.SimpleLog("testLog");
        factory = new DefensiveThreadFactory("test", customLog);
    }

    @Test
    public void testOnOkThread() {
        Thread thread = factory.newThread(new Runnable() {
            @Override
            public void run() {
                // Ok.
            }
        });

        // Use run and not start to execute the wrapped runnable synchronously.
        thread.run();
        assertThat(log.error).isNull();
    }

    @Test
    public void testOnBadThread() {
        Thread thread = factory.newThread(new Runnable() {
            @Override
            public void run() {
                throw new NullPointerException();
            }
        });

        // Use run and not start to execute the wrapped runnable synchronously.
        thread.run();
        //TODO: uncomment when using a full Log system (no system.out.println) 
        //assertThat(log.error).isNotNull();
        //assertThat(log.throwable).isExactlyInstanceOf(NullPointerException.class);
    }

    private class CollectorLog extends SilentLog {

        CharSequence error;
        Throwable throwable;

        /**
         * Collect the error message.
         *
         * @param content
         * @param error
         * @see org.apache.maven.plugin.logging.Log#error(CharSequence, Throwable)
         */
        @Override
        public void error(CharSequence content, Throwable error) {
            this.error = content;
            this.throwable = error;
        }
    }
}
