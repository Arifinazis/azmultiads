package com.nad.multiads.face;

/**
 * Callback for Native ad loading events.
 */
@FunctionalInterface
public interface NativeAdLoadCallback {
    void onResult(boolean success);

    static NativeAdLoadCallback from(Runnable onSuccess, Runnable onFailed) {
        return success -> {
            if (success && onSuccess != null) {
                onSuccess.run();
            } else if (!success && onFailed != null) {
                onFailed.run();
            }
        };
    }
}
