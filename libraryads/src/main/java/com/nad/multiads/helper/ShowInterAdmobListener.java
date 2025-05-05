package com.nad.multiads.helper;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.nad.multiads.AZAds;
import com.nad.multiads.utils.AZAdsCallback;
import com.nad.multiads.utils.AZAdsConfig;
import com.nad.multiads.utils.AZAdsConstant;
import com.nad.multiads.utils.AZAdsRunUtils;
import com.nad.multiads.view.AZAdLoadingScreen;

public class ShowInterAdmobListener extends FullScreenContentCallback {

    private final AZAds azAds;
    private final Activity activity;
    private final AZAdsConfig adsConfig;
    private final AZAdsCallback azAdsCallback;
    private final AZAdLoadingScreen loadingScreen;
    private final boolean isPrimaryAds;
    private final boolean isResume;

    public ShowInterAdmobListener(AZAds azAds, Activity activity, AZAdsConfig adsConfig,
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
    public void onAdDismissedFullScreenContent() {
        super.onAdDismissedFullScreenContent();
        azAds.mInterAdmob = null; // pindahkan ke sini
        if (isResume) {
            AZAdsConstant.lastShowAdResume = System.currentTimeMillis();
        } else {
            AZAdsConstant.lastShowAdFull = System.currentTimeMillis();
        }
        azAds.loadInter(adsConfig, true, azAdsCallback);
        azAdsCallback.onAdClosed();
    }

    @Override
    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
        super.onAdFailedToShowFullScreenContent(adError);
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
    public void onAdShowedFullScreenContent() {
        super.onAdShowedFullScreenContent();
        // azAds.mInterAdmob = null; // dihapus dari sini
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
}
