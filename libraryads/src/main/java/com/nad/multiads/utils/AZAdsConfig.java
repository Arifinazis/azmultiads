package com.nad.multiads.utils;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.nad.multiads.R;

import java.util.concurrent.atomic.AtomicBoolean;

public final class AZAdsConfig {

    private final String primaryNetwork;
    private final String primaryAppId;
    private final String backupNetwork;
    private final String backupAppId;
    private final String primaryUnitId;
    private final String backupUnitId;
    private final AtomicBoolean isDark;
    private final AtomicBoolean preload;
    private final AtomicBoolean isDebug;
    private final AZNativeAdsStyle nativeStyle;
    private final AZLoadNativeType loadNativeType;
    private final String adKeywords;
    private final int maxCountAds;
    private final int colorLight;
    private final int colorDark;
    private final int bannerType;
    private final long intervalAdFull;
    private final long intervalAdResume;
    private final long intervalAdReward;
    private final boolean enableCapping;

    private AZAdsConfig(Builder builder) {
        this.primaryNetwork = builder.primaryNetwork;
        this.primaryAppId = builder.primaryAppId;
        this.backupNetwork = builder.backupNetwork;
        this.backupAppId = builder.backupAppId;
        this.primaryUnitId = builder.primaryUnitId;
        this.backupUnitId = builder.backupUnitId;
        this.isDark = new AtomicBoolean(builder.isDark);
        this.preload = new AtomicBoolean(builder.preload);
        this.isDebug = new AtomicBoolean(builder.isDebug);
        this.nativeStyle = builder.nativeStyle;
        this.loadNativeType = builder.loadNativeType;
        this.adKeywords = builder.adKeywords;
        this.maxCountAds = builder.maxCountAds;
        this.colorLight = builder.colorLight;
        this.colorDark = builder.colorDark;
        this.bannerType = builder.bannerType;
        this.intervalAdFull = builder.intervalAdFull;
        this.intervalAdResume = builder.intervalAdResume;
        this.intervalAdReward = builder.intervalAdReward;
        this.enableCapping = builder.enableCapping;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @NonNull public String getPrimaryNetwork() { return primaryNetwork; }
    @NonNull public String getPrimaryAppId() { return primaryAppId; }
    @NonNull public String getBackupNetwork() { return backupNetwork; }
    @NonNull public String getBackupAppId() { return backupAppId; }
    @NonNull public String getPrimaryUnitId() { return primaryUnitId; }
    @NonNull public String getBackupUnitId() { return backupUnitId; }
    public boolean isDark() { return isDark.get(); }
    public boolean isPreload() { return preload.get(); }
    public boolean isDebug() { return isDebug.get(); }
    @NonNull public AZNativeAdsStyle getNativeStyle() { return nativeStyle; }
    @NonNull public AZLoadNativeType getLoadNativeType() { return loadNativeType; }
    @Nullable public String getAdKeywords() { return adKeywords; }
    public int getMaxCountAds() { return maxCountAds; }

    @ColorInt
    public int getColorDark(Context context) {
        return (colorDark == 0) ? ContextCompat.getColor(context, R.color.gnt_default_light) : colorDark;
    }

    @ColorInt
    public int getColorLight(Context context) {
        return (colorLight == 0) ? ContextCompat.getColor(context, R.color.gnt_default_dark) : colorLight;
    }

    public int getBannerType() { return bannerType; }
    public long getIntervalAdFull() { return intervalAdFull; }
    public long getIntervalAdResume() { return intervalAdResume; }
    public long getIntervalAdReward() { return intervalAdReward; }
    public boolean isEnableCapping() { return enableCapping; }

    public static final class Builder {
        private String primaryNetwork = "";
        private String primaryAppId = "";
        private String backupNetwork = "";
        private String backupAppId = "";
        private String primaryUnitId = "";
        private String backupUnitId = "";
        private boolean isDark = false;
        private boolean preload = false;
        private boolean isDebug = false;
        private AZNativeAdsStyle nativeStyle = AZNativeAdsStyle.MEDIUM;
        private AZLoadNativeType loadNativeType = AZLoadNativeType.NORMAL;
        private String adKeywords = null;
        private int maxCountAds = 1;
        private int colorLight = 0xFFFFFFFF;
        private int colorDark = 0xFF000000;
        private int bannerType = 0;
        private long intervalAdFull = 15000;
        private long intervalAdResume = 15000;
        private long intervalAdReward = 15000;
        private boolean enableCapping = true;

        private Builder() {}

        @NonNull public Builder setPrimaryNetwork(@NonNull String val) { primaryNetwork = val; return this; }
        @NonNull public Builder setPrimaryAppId(@NonNull String val) { primaryAppId = val; return this; }
        @NonNull public Builder setBackupNetwork(@NonNull String val) { backupNetwork = val; return this; }
        @NonNull public Builder setBackupAppId(@NonNull String val) { backupAppId = val; return this; }
        @NonNull public Builder setPrimaryUnitId(@NonNull String val) { primaryUnitId = val; return this; }
        @NonNull public Builder setBackupUnitId(@NonNull String val) { backupUnitId = val; return this; }
        @NonNull public Builder setDarkMode(boolean val) { isDark = val; return this; }
        @NonNull public Builder setPreload(boolean val) { preload = val; return this; }
        @NonNull public Builder setDebug(boolean val) { isDebug = val; return this; }
        @NonNull public Builder setNativeStyle(@NonNull AZNativeAdsStyle val) { nativeStyle = val; return this; }
        @NonNull public Builder setLoadNativeType(@NonNull AZLoadNativeType val) { loadNativeType = val; return this; }
        @NonNull public Builder setAdKeywords(@Nullable String val) { adKeywords = val; return this; }
        @NonNull public Builder setMaxCountAds(int val) { maxCountAds = val; return this; }
        @NonNull public Builder setColorLight(@ColorInt int val) { colorLight = val; return this; }
        @NonNull public Builder setColorDark(@ColorInt int val) { colorDark = val; return this; }
        @NonNull public Builder setBannerType(int val) { bannerType = val; return this; }
        @NonNull public Builder setIntervalAdFull(long val) { intervalAdFull = val; return this; }
        @NonNull public Builder setIntervalAdResume(long val) { intervalAdResume = val; return this; }
        @NonNull public Builder setIntervalAdReward(long val) { intervalAdReward = val; return this; }
        @NonNull public Builder setEnableCapping(boolean val) { enableCapping = val; return this; }

        @NonNull public AZAdsConfig build() {
            return new AZAdsConfig(this);
        }
    }
}
