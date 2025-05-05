package com.nad.multiads.face;

public class NativeAdLoadCallbackWrapper implements NativeAdLoadCallback {

    private final Runnable onSuccess;
    private final Runnable onFailed;

    public NativeAdLoadCallbackWrapper(Runnable onSuccess, Runnable onFailed) {
        this.onSuccess = onSuccess;
        this.onFailed = onFailed;
    }

    @Override
    public void onResult(boolean success) {
        if (success && onSuccess != null) {
            onSuccess.run();
        } else if (!success && onFailed != null) {
            onFailed.run();
        }
    }

    public Runnable getOnSuccess() {
        return onSuccess;
    }

    public Runnable getOnFailed() {
        return onFailed;
    }
}
