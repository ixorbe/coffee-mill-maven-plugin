package org.nanoko.coffeemill.mojos;

import org.apache.maven.execution.MavenSession;

import org.nanoko.maven.pipeline.Watchers;
import org.nanoko.maven.Watcher;

/**
 * Common part.
 */
public abstract class AbstractCoffeeMillWatcherMojo extends AbstractCoffeeMillMojo implements Watcher {

    public void setSession(MavenSession session) {
        this.session = session;
        Watchers.add(session, this);
    }

    public void removeFromWatching() {
        Watchers.remove(session, this);
    }

}
