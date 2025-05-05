package com.nad.multiads.helper;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.nad.multiads.AZAds;
import com.nad.multiads.utils.AZAdsCallback;
import com.nad.multiads.utils.AZAdsConfig;
import com.nad.multiads.view.AZAdLoadingScreen;

public class ShowRewardAppMaxListener implements MaxRewardedAdListener {
    private final AZAds azAds;
    private final Activity activity;
    private final AZAdsConfig adsConfig;
    private final AZAdsCallback azAdsCallback;
    private final AZAdLoadingScreen loadingScreen;
    private final boolean isPrimaryAds;

    public ShowRewardAppMaxListener(AZAds azAds, Activity activity, AZAdsConfig adsConfig,
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
    public void onUserRewarded(@NonNull MaxAd maxAd, @NonNull MaxReward maxReward) {
        // Optional: implementasi reward bisa ditambahkan jika diperlukan
    }

    @Override
    public void onAdLoaded(@NonNull MaxAd maxAd) {
        // No-op, karena ini listener untuk show
    }

    @Override
    public void onAdDisplayed(@NonNull MaxAd maxAd) {
        if (activity.isFinishing()) return;

        if (loadingScreen != null && loadingScreen.isAdded()) {
            loadingScreen.dismissAllowingStateLoss();
        }

        azAdsCallback.onAdDisplayed();
    }

    @Override
    public void onAdHidden(@NonNull MaxAd maxAd) {
        // Hapus referensi untuk mencegah reuse
        azAds.mMaxRewardedAd = null;

        // Load ulang untuk persiapan pemakaian berikutnya
        azAds.loadReward(adsConfig, true, azAdsCallback);
        azAdsCallback.onAdClosed();
    }

    @Override
    public void onAdClicked(@NonNull MaxAd maxAd) {
        // Optional
    }

    @Override
    public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError maxError) {
        azAds.mMaxRewardedAd = null;

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
    public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
        // Optional handling
    }
}
