package com.nad.multiads.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.nad.multiads.R;
import com.nad.multiads.utils.AZAdsConfig;
import com.nad.multiads.utils.AZNativeAdsStyle;

public class NadNativeView extends FrameLayout {

    private FrameLayout containerRoot;
    private ShimmerFrameLayout shimmerLoader;
    private TemplateNativeView admobNativeView;
    private FrameLayout applovinNativeContainer;
    private NativeAdLayout fanNativeAdContainer;
    private int currentTemplateResId = R.layout.view_native_ad_medium;
    private MaxAd loadedApplovinAd;
    private MaxNativeAdLoader applovinAdLoader;

    public NadNativeView(@NonNull Context context) {
        super(context);
        initialize(context, null);
    }

    public NadNativeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public NadNativeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(currentTemplateResId, this);
        bindViews();
    }

    private void bindViews() {
        shimmerLoader = findViewById(R.id.shimmer_view);
        containerRoot = findViewById(R.id.frameNative);
        admobNativeView = findViewById(R.id.admob_native_ad_container);
        fanNativeAdContainer = findViewById(R.id.fanNativeAdContainer);
        applovinNativeContainer = findViewById(R.id.applovinMaxContainer);
    }

    public void setNativeAdsStyle(AZNativeAdsStyle style) {
        switch (style) {
            case SMALL:
                updateTemplateLayout(R.layout.view_native_ad_small);
                break;
            case LARGE:
                updateTemplateLayout(R.layout.view_native_ad_large);
                break;
            case FULL:
                updateTemplateLayout(R.layout.view_native_ad_full);
                break;
            case MEDIUM:
            default:
                updateTemplateLayout(R.layout.view_native_ad_medium);
                break;
        }
    }

    private void updateTemplateLayout(int layoutResId) {
        this.currentTemplateResId = layoutResId;
        removeAllViews();
        LayoutInflater.from(getContext()).inflate(currentTemplateResId, this, true);
        bindViews();
    }


    public void destroyAllAds() {
        if (admobNativeView != null) {
            admobNativeView.destroyNativeAd();
        }
        if (applovinAdLoader != null && loadedApplovinAd != null) {
            try {
                applovinAdLoader.destroy(loadedApplovinAd);
            } catch (Exception e) {
                Log.e("NadNativeView", "Failed to destroy Applovin ad", e);
            }
            loadedApplovinAd = null;
            applovinAdLoader = null;
        }

    }

    public ShimmerFrameLayout getShimmerLoader() {
        return shimmerLoader;
    }

    public TemplateNativeView getAdmobNativeView() {
        return admobNativeView;
    }

    public FrameLayout getApplovinNativeContainer() {
        return applovinNativeContainer;
    }


    public NativeAdLayout getFanNativeLayout() {
        return fanNativeAdContainer;
    }

    public FrameLayout getContainerRoot() {
        return containerRoot;
    }

    public MaxNativeAdLoader getApplovinAdLoader() {
        return applovinAdLoader;
    }

    public void setApplovinAdLoader(MaxNativeAdLoader applovinAdLoader) {
        this.applovinAdLoader = applovinAdLoader;
    }

    public MaxAd getLoadedApplovinAd() {
        return loadedApplovinAd;
    }

    public void setLoadedApplovinAd(MaxAd loadedApplovinAd) {
        this.loadedApplovinAd = loadedApplovinAd;
    }



}
