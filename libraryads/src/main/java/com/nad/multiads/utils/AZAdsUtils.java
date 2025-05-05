package com.nad.multiads.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookMediationAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.nad.multiads.gdpr.MultiConsentManager;

public class AZAdsUtils {

    /**
     * Membuat Bundle untuk Non-Personalized Ads
     */
    public static Bundle createNonPersonalizedAdsBundle() {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        return extras;
    }

    public static AdRequest getAdsRequest(Context context,AZAdsType adsType,AZAdsConfig adsConfig) {
        AdRequest.Builder builder = new AdRequest.Builder();

        // Create admobExtras bundle
        Bundle admobExtras = new Bundle();
        // GDPR/CCPA Consent Handling
        MultiConsentManager multiConsentManager = new MultiConsentManager(context);
        if (multiConsentManager.isNonPersonalizedAds()) {
            admobExtras.putAll(createNonPersonalizedAdsBundle());
        }

        builder.addNetworkExtrasBundle(AdMobAdapter.class, admobExtras);

        // Optional keywords targeting
        if (adsConfig.getAdKeywords() != null && !adsConfig.getAdKeywords().trim().isEmpty()) {
            builder.addKeyword(adsConfig.getAdKeywords());
        }

        // Mediation FAN Binding handling
        if (adsType == AZAdsType.ADMOB_BINDING_FAN) {
            Bundle fanExtras = new Bundle();
            fanExtras.putString("native", "true");
            builder.addNetworkExtrasBundle(FacebookMediationAdapter.class, fanExtras);
        }

        return builder.build();
    }
    public static AdRequest createAdaptiveAdRequest(Context context, AZAdsType adsType, String adKeywords,int type) {
        AdRequest.Builder builder = new AdRequest.Builder();

        // Create admobExtras bundle
        Bundle admobExtras = new Bundle();

        // Handle collapsible
        if (type == 2) {
            admobExtras.putString("collapsible", "bottom");
        } else if (type == 3) {
            admobExtras.putString("collapsible", "top");
        }


        // GDPR/CCPA Consent Handling
        MultiConsentManager multiConsentManager = new MultiConsentManager(context);
        if (multiConsentManager.isNonPersonalizedAds()) {
            admobExtras.putAll(createNonPersonalizedAdsBundle());
        }

        builder.addNetworkExtrasBundle(AdMobAdapter.class, admobExtras);

        // Optional keywords targeting
        if (adKeywords != null && !adKeywords.trim().isEmpty()) {
            builder.addKeyword(adKeywords.trim());
        }

//        // Mediation FAN Binding handling
        if (adsType == AZAdsType.ADMOB_BINDING_FAN) {
            Bundle fanExtras = new Bundle();
            fanExtras.putString("native_banner", "true");
            builder.addNetworkExtrasBundle(FacebookMediationAdapter.class, fanExtras);
        }

        return builder.build();
    }
    public static AdSize getAdSize(Context context, int type) {
        if (!(context instanceof Activity)) {
            return AdSize.FLUID;
        }

        Activity activity = (Activity) context;

        int screenWidthDp = getScreenWidthDp(activity);

        if (screenWidthDp <= 0) {
            return AdSize.BANNER; // fallback kalau gagal ambil ukuran
        }

        switch (type) {
            case 1:
                return AdSize.getInlineAdaptiveBannerAdSize(screenWidthDp, 60);
            case 4:
                return AdSize.getInlineAdaptiveBannerAdSize(screenWidthDp, 120);
            case 5:
                return AdSize.getInlineAdaptiveBannerAdSize(screenWidthDp, 360);
            case 6:
            case 7:
                return AdSize.getInlineAdaptiveBannerAdSize(screenWidthDp, 480);
            default:
                return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, screenWidthDp);
        }
    }

    private static int getScreenWidthDp(Activity activity) {
        try {
            WindowManager windowManager = activity.getSystemService(WindowManager.class);
            if (windowManager == null) {
                return 0;
            }

            int widthPixels;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11 ke atas gunakan WindowMetrics
                WindowMetrics windowMetrics = windowManager.getCurrentWindowMetrics();
                Rect bounds = windowMetrics.getBounds();
                WindowInsets insets = windowMetrics.getWindowInsets();
                int insetHorizontal = insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars()).left
                        + insets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars()).right;
                widthPixels = bounds.width() - insetHorizontal;
            } else {
                // Android 10 ke bawah
                @SuppressWarnings("deprecation")
                DisplayMetrics metrics = new DisplayMetrics();
                @SuppressWarnings("deprecation")
                android.view.Display display = activity.getWindowManager().getDefaultDisplay();
                @SuppressWarnings("deprecation")
                int width = getDisplayMetricsWidth(display, metrics);
                widthPixels = width;
            }

            float density = activity.getResources().getDisplayMetrics().density;
            return (int) (widthPixels / density);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @SuppressWarnings("deprecation")
    private static int getDisplayMetricsWidth(android.view.Display display, DisplayMetrics metrics) {
        display.getMetrics(metrics);
        return metrics.widthPixels;
    }
    /**
     * Mendapatkan ukuran AdSize Adaptive Banner
     */
    public static AdSize getAdaptiveAdSize(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        int adWidth = (int) (displayMetrics.widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    /**
     * Konversi dp ke px
     */
    public static int dpToPx(Context context, float dp) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null");
        }
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
    public static void setLog(String tag, String s) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(s)) {
            Log.d(tag, s);
        }
    }
}
