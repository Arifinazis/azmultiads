package com.nad.multiads.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Utility class to run tasks in background or on UI thread,
 * and to start Activities with a delay.
 */
public final class AZAdsRunUtils {

    private static final int NUM_OF_THREADS = 20;
    private static Executor executor;
    private static Handler handler;

    private AZAdsRunUtils() {
        // Private constructor to prevent instantiation
    }

    static {
        createExecutor();
    }

    private static void createExecutor() {
        executor = Executors.newFixedThreadPool(NUM_OF_THREADS, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("Background Thread");
            return thread;
        });
    }

    /**
     * Runs a task in the background thread.
     *
     * @param runnable The task to run
     */
    public static void runInBackground(@NonNull Runnable runnable) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            runnable.run();
        } else {
            if (executor == null) {
                createExecutor();
            }
            executor.execute(runnable);
        }
    }

    /**
     * Runs a task on the UI thread.
     *
     * @param runnable The task to run
     */
    public static void runOnUI(@Nullable Runnable runnable) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        if (runnable != null) {
            handler.post(runnable);
        }
    }

    /**
     * Starts a new Activity with a delay.
     *
     * @param from                The current Activity
     * @param to                  The target Activity class
     * @param delayMillis         Delay time in milliseconds
     * @param shouldFinishCurrent Whether to finish the current Activity after starting the new one
     */
    public static void startActivityWithDelay(@NonNull final Activity from,
                                              @NonNull final Class<?> to,
                                              long delayMillis,
                                              boolean shouldFinishCurrent) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.postDelayed(() -> {
            Intent intent = new Intent(from, to);
            from.startActivity(intent);
            if (shouldFinishCurrent) {
                from.finish();
            }
        }, delayMillis);
    }

    /**
     * Starts a new Activity with a delay without finishing the current one.
     *
     * @param from        The current Activity
     * @param to          The target Activity class
     * @param delayMillis Delay time in milliseconds
     */
    public static void startActivityWithDelay(@NonNull final Activity from,
                                              @NonNull final Class<?> to,
                                              long delayMillis) {
        startActivityWithDelay(from, to, delayMillis, false);
    }
    /**
     * Starts a new Activity immediately without any delay.
     *
     * @param from                The current Activity
     * @param to                  The target Activity class
     * @param shouldFinishCurrent Whether to finish the current Activity after starting the new one
     */
    public static void startActivityNow(@NonNull final Activity from,
                                        @NonNull final Class<?> to,
                                        boolean shouldFinishCurrent) {
        runOnUI(() -> {
            Intent intent = new Intent(from, to);
            from.startActivity(intent);
            if (shouldFinishCurrent) {
                from.finish();
            }
        });
    }

    /**
     * Starts a new Activity immediately without finishing the current one.
     *
     * @param from The current Activity
     * @param to   The target Activity class
     */
    public static void startActivityNow(@NonNull final Activity from,
                                        @NonNull final Class<?> to) {
        startActivityNow(from, to, false);
    }
    /**
     * Runs a task with a delay on the UI thread.
     *
     * @param runnable     The task to run
     * @param delayMillis  The delay time in milliseconds
     */
    public static void runWithDelay(@NonNull Runnable runnable, long delayMillis) {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.postDelayed(runnable, delayMillis);
    }

}
