package com.nad.multiads.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;

/**
 * Wrapper class for AppLovin native ads to safely store the loader, view, and ad instance.
 * Recommended for use in both NORMAL and FULLSCREEN native ad modes.
 */
public class ApplovinNativeAdWrapperFull {

    @NonNull
    private final MaxNativeAdLoader adLoader;

    @Nullable
    private final MaxNativeAdView adView;

    @NonNull
    private final MaxAd maxAd;

    private final long loadedTimestamp;

    public ApplovinNativeAdWrapperFull(@NonNull MaxNativeAdLoader adLoader,
                                       @Nullable MaxNativeAdView adView,
                                       @NonNull MaxAd maxAd) {
        this.adLoader = adLoader;
        this.adView = adView;
        this.maxAd = maxAd;
        this.loadedTimestamp = System.currentTimeMillis();
    }

    @NonNull
    public MaxNativeAdLoader getAdLoader() {
        return adLoader;
    }

    @Nullable
    public MaxNativeAdView getAdView() {
        return adView;
    }

    @NonNull
    public MaxAd getMaxAd() {
        return maxAd;
    }

    public long getLoadedTimestamp() {
        return loadedTimestamp;
    }

    /**
     * Release ad resources when this wrapper is no longer needed.
     */
    public void destroy() {
        try {
            adLoader.destroy(maxAd);
        } catch (Exception ignored) {}
    }
}
