package com.nad.multiads;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.nad.multiads.face.OnAZAdCompleteListener;
import com.nad.multiads.utils.AZAdsUtils;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class AZAppOpenAdMob {

    private static final String LOG_TAG = "AppOpenAdmob";
    private AppOpenAd appOpenAd = null;
    private final AtomicBoolean isLoadingAd = new AtomicBoolean(false);
    private final AtomicBoolean isShowingAd = new AtomicBoolean(false);
    private long loadTime = 0;

    public AZAppOpenAdMob() {
    }

    public void loadAd(Context context, String adMobAppOpenAdUnitId) {
        if (isLoadingAd.get() || isAdAvailable()) {
            return;
        }

        isLoadingAd.set(true);
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(context, adMobAppOpenAdUnitId, request, new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                appOpenAd = ad;
                isLoadingAd.set(false);
                loadTime = (new Date()).getTime();
                AZAdsUtils.setLog(LOG_TAG, "onAdLoaded.");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                isLoadingAd.set(false);
                AZAdsUtils.setLog(LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
            }
        });
    }

    public boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId) {
        showAdIfAvailable(activity, appOpenAdUnitId, () -> {
        });
    }

    public void showAdIfAvailable(@NonNull final Activity activity, String appOpenAdUnitId, @NonNull OnAZAdCompleteListener onAZAdCompleteListener) {
        if (isShowingAd.get()) {
            onAZAdCompleteListener.onFinish();
            AZAdsUtils.setLog(LOG_TAG, "The app open ad is already showing.");
            return;
        }

        if (!isAdAvailable()) {
            AZAdsUtils.setLog(LOG_TAG, "Iklan terbuka aplikasi belum siap.");
            onAZAdCompleteListener.onFinish();
            loadAd(activity, appOpenAdUnitId);
            return;
        }

        AZAdsUtils.setLog(LOG_TAG, "Will show ad.");

        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                appOpenAd = null;
                isShowingAd.set(true);
                AZAdsUtils.setLog(LOG_TAG, "onAdDismissedFullScreenContent.");
                onAZAdCompleteListener.onFinish();
                loadAd(activity, appOpenAdUnitId);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                appOpenAd = null;
                isShowingAd.set(false);
                AZAdsUtils.setLog(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                onAZAdCompleteListener.onFinish();
                loadAd(activity, appOpenAdUnitId);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                AZAdsUtils.setLog(LOG_TAG, "onAdShowedFullScreenContent.");
            }
        });

        isShowingAd.set(true);
        appOpenAd.show(activity);
    }
    public boolean isShowingAd() {
        return isShowingAd.get();
    }
}

