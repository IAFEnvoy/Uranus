package com.iafenvoy.uranus.object.entity.pathfinding.raycoms;
/*
    All of this code is used with permission from Raycoms, one of the developers of the minecolonies project.
 */

import com.iafenvoy.uranus.Uranus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;


/**
 * Static class the handles all the Pathfinding.
 */
public final class Pathfinding {
    private static final BlockingQueue<Runnable> jobQueue = new LinkedBlockingDeque<>();
    private static ThreadPoolExecutor executor;

    private Pathfinding() {
        //Hides default constructor.
    }

    /**
     * Creates a new thread pool for pathfinding jobs
     *
     * @return the threadpool executor.
     */
    public static ThreadPoolExecutor getExecutor() {
        if (executor == null)
            executor = new ThreadPoolExecutor(1, PathfindingConstants.pathfindingThreads, 10, TimeUnit.SECONDS, jobQueue, new UranusThreadFactory());
        return executor;
    }

    /**
     * Ice and Fire specific thread factory.
     */
    public static class UranusThreadFactory implements ThreadFactory {
        /**
         * Ongoing thread IDs.
         */
        public static int id;

        @Override
        public Thread newThread(final @NotNull Runnable runnable) throws RuntimeException {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            final Thread thread = new Thread(runnable, "Uranus Pathfinding Worker #" + (id++));
            thread.setDaemon(true);
            thread.setPriority(Thread.MAX_PRIORITY);
            if (thread.getContextClassLoader() != classLoader) {
                Uranus.LOGGER.info("Corrected CCL of new Uranus Pathfinding Thread, was: {}", thread.getContextClassLoader().toString());
                thread.setContextClassLoader(classLoader);
            }
            thread.setUncaughtExceptionHandler((thread1, throwable) -> Uranus.LOGGER.error("Uranus Pathfinding Thread errored! ", throwable));
            return thread;
        }
    }
}
