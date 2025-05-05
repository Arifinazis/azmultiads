package com.nad.multiads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.nad.multiads.face.OnAZAdCompleteListener;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class AZAppOpenAdAppLovin {

    private static final String LOG_TAG = "AZAppOpenAdAppLovin";
    private final AtomicBoolean isLoadingAd = new AtomicBoolean(false);
    private final AtomicBoolean isShowingAd = new AtomicBoolean(false);

    public AZAppOpenAdAppLovin() {
    }

    public void loadAd(Context context, String maxAppOpenAdUnitId) {
        if (isLoadingAd.get() || isAdAvailable()) {
            return;
        }
        isLoadingAd.set(true);
    }

    public boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long loadTime = 0;
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable() {
        return false;
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId) {
        showAdIfAvailable(activity, appOpenAdUnitId, () -> {
        });
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId, @NonNull OnAZAdCompleteListener onAZAdCompleteListener) {
        if (isShowingAd.get()) {
            Log.d(LOG_TAG, "The app open ad is already showing.");
            return;
        }

        if (!isAdAvailable()) {
            Log.d(LOG_TAG, "The app open ad is not ready yet.");
            onAZAdCompleteListener.onFinish();
            loadAd(activity, appOpenAdUnitId);
            return;
        }

        Log.d(LOG_TAG, "Will show ad.");

        isShowingAd.set(true);
    }
    public boolean isShowingAd() {
        return isShowingAd.get();
    }
}