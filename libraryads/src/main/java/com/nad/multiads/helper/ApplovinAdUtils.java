package com.nad.multiads.helper;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.nad.multiads.R;
import com.nad.multiads.utils.AZAdsConfig;
import com.nad.multiads.view.NadNativeView;

public class ApplovinAdUtils {

    /**
     * Bind AppLovin native ad with diff check (for fullscreen usage).
     */
    public static void bindWithDiff(@NonNull NadNativeView nativeView,
                                    @Nullable ApplovinNativeAdWrapperFull wrapperFull,
                                    @Nullable Runnable onSuccess) {

        FrameLayout container = nativeView.getApplovinNativeContainer();
        if (container == null || wrapperFull == null) return;

        MaxAd currentAd = nativeView.getLoadedApplovinAd();


        if (currentAd != null && !wrapperFull.getMaxAd().equals(currentAd)) {
            if (nativeView.getApplovinAdLoader() != null) {
                try {
                    nativeView.getApplovinAdLoader().destroy(currentAd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!wrapperFull.getMaxAd().equals(currentAd)) {
            nativeView.setLoadedApplovinAd(wrapperFull.getMaxAd());
            container.removeAllViews();
            container.addView(wrapperFull.getAdView());
            container.setVisibility(View.VISIBLE);
            nativeView.setVisibility(View.VISIBLE);
            if (onSuccess != null) onSuccess.run();
        }

    }

    /**
     * Bind AppLovin native ad directly for normal usage (no callback).
     */
    public static void bindNativeAd(@NonNull NadNativeView nativeView,
                                    @NonNull ApplovinNativeAdWrapper wrapper) {

        MaxAd newAd = wrapper.getMaxAd();
        MaxAd currentAd = nativeView.getLoadedApplovinAd();

        if (newAd.equals(currentAd)) return;

        if (currentAd != null && nativeView.getApplovinAdLoader() != null) {
            try {
                nativeView.getApplovinAdLoader().destroy(currentAd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        nativeView.setApplovinAdLoader(wrapper.getAdLoader());
        nativeView.setLoadedApplovinAd(newAd);

        FrameLayout container = nativeView.getApplovinNativeContainer();
        if (container != null && wrapper.getAdView() != null) {
            container.removeAllViews();
            container.addView(wrapper.getAdView());
            container.setVisibility(View.VISIBLE);
            nativeView.setVisibility(View.VISIBLE);
            nativeView.getShimmerLoader().stopShimmer();

        }

        nativeView.getShimmerLoader().setVisibility(View.GONE);

    }

    public static void applyTheme(Activity activity, MaxNativeAdView maxNativeAdView, AZAdsConfig adsConfig) {
        int primaryColor = adsConfig.isDark()
                ? (adsConfig.getColorDark(activity) != 0 ? adsConfig.getColorLight(activity) : ContextCompat.getColor(activity, R.color.gnt_default_light))
                : (adsConfig.getColorLight(activity) != 0 ? adsConfig.getColorLight(activity) : ContextCompat.getColor(activity, R.color.gnt_default_dark));


        TextView titleTextView = maxNativeAdView.findViewById(R.id.title_text_view);
        TextView bodyTextView = maxNativeAdView.findViewById(R.id.body_text_view);
        TextView advertiserTextView = maxNativeAdView.findViewById(R.id.advertiser_textView);

        if (titleTextView != null) titleTextView.setTextColor(primaryColor);
        if (advertiserTextView != null) advertiserTextView.setTextColor(primaryColor);
        if (bodyTextView != null) bodyTextView.setTextColor(primaryColor);


    }
}
