package com.nad.multiads.helper;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.nad.multiads.AZAds;
import com.nad.multiads.utils.AZAdsCallback;
import com.nad.multiads.utils.AZAdsConfig;
import com.nad.multiads.utils.AZAdsConstant;
import com.nad.multiads.utils.AZAdsRunUtils;
import com.nad.multiads.view.AZAdLoadingScreen;

public class ShowInterAppMaxListener implements MaxAdListener {
    private final AZAds azAds;
    private final Activity activity;
    private final AZAdsConfig adsConfig;
    private final AZAdsCallback azAdsCallback;
    private final AZAdLoadingScreen loadingScreen;
    private final boolean isPrimaryAds;
    private final boolean isResume;

    public ShowInterAppMaxListener(AZAds azAds, Activity activity, AZAdsConfig adsConfig,
                                   AZAdsCallback azAdsCallback, AZAdLoadingScreen loadingScreen,
                                   boolean isPrimaryAds, boolean isResume) {
        this.azAds = azAds;
        this.activity = activity;
        this.adsConfig = adsConfig;
        this.azAdsCallback = azAdsCallback;
        this.loadingScreen = loadingScreen;
        this.isPrimaryAds = isPrimaryAds;
        this.isResume = isResume;
    }

    @Override
    public void onAdLoaded(@NonNull MaxAd maxAd) {
        // No-op
    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd maxAd) {
        if (!isResume) {
            AZAdsRunUtils.runWithDelay(() -> {
                if (activity.isFinishing()) return;
                if (loadingScreen != null && loadingScreen.isAdded()) {
                    loadingScreen.dismissAllowingStateLoss();
                }
            }, 800L);
        }
        azAdsCallback.onAdDisplayed();
    }

    @Override
    public void onAdHidden(@NonNull MaxAd maxAd) {
        azAds.mMaxinterstitialAd = null;

        if (isResume) {
            AZAdsConstant.lastShowAdResume = System.currentTimeMillis();
        } else {
            AZAdsConstant.lastShowAdFull = System.currentTimeMillis();
        }

        azAds.loadInter(adsConfig, true, azAdsCallback);
        azAdsCallback.onAdClosed();
    }

    @Override
    public void onAdClicked(@NonNull MaxAd maxAd) {
        // No-op
    }

    @Override
    public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
        azAds.mMaxinterstitialAd = null;

        if (isPrimaryAds) {
            azAds.showInterstitialAd(activity, adsConfig, false, isResume, azAdsCallback);
        } else {
            if (loadingScreen != null) {
                loadingScreen.dismissAllowingStateLoss();
            }
            azAdsCallback.onAdFailedDisplay();
        }
    }

    @Override
    public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
        // No-op
    }
}
