package com.nad.multiads.helper;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.nad.multiads.AZAds;
import com.nad.multiads.utils.AZAdsCallback;
import com.nad.multiads.utils.AZAdsConfig;
import com.nad.multiads.view.AZAdLoadingScreen;

public class ShowRewardAdmobListener extends FullScreenContentCallback {
    private final AZAds azAds;
    private final Activity activity;
    private final AZAdsConfig adsConfig;
    private final AZAdsCallback azAdsCallback;
    private final AZAdLoadingScreen loadingScreen;
    private final boolean isPrimaryAds;

    public ShowRewardAdmobListener(AZAds azAds, Activity activity, AZAdsConfig adsConfig,
                                   AZAdsCallback azAdsCallback, AZAdLoadingScreen loadingScreen,
                                   boolean isPrimaryAds) {
        this.azAds = azAds;
        this.activity = activity;
        this.adsConfig = adsConfig;
        this.azAdsCallback = azAdsCallback;
        this.loadingScreen = loadingScreen;
        this.isPrimaryAds = isPrimaryAds;
    }

    @Override
    public void onAdDismissedFullScreenContent() {
        super.onAdDismissedFullScreenContent();

        // Hapus referensi rewarded ad setelah ditutup
        azAds.adMobRewardedAd = null;

        // Load kembali iklan untuk next use
        azAds.loadReward(adsConfig, true, azAdsCallback);

        azAdsCallback.onAdClosed();
    }

    @Override
    public void onAdShowedFullScreenContent() {
        super.onAdShowedFullScreenContent();

        if (activity.isFinishing()) return;

        if (loadingScreen != null && loadingScreen.isAdded()) {
            loadingScreen.dismissAllowingStateLoss();
        }

        azAdsCallback.onAdDisplayed();
    }

    @Override
    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
        super.onAdFailedToShowFullScreenContent(adError);

        if (isPrimaryAds) {
            azAds.showReward(activity, adsConfig, false, azAdsCallback);
        } else {
            if (loadingScreen != null && loadingScreen.isAdded()) {
                loadingScreen.dismissAllowingStateLoss();
            }
            azAdsCallback.onAdFailedDisplay();
        }
    }

    @Override
    public void onAdClicked() {
        super.onAdClicked();
    }

    @Override
    public void onAdImpression() {
        super.onAdImpression();
    }
}
