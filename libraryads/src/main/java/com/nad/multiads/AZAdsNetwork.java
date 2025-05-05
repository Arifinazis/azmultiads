package com.nad.multiads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkInitializationConfiguration;
import com.google.android.gms.ads.MobileAds;
import com.nad.multiads.face.AdsInitializationListener;
import com.nad.multiads.helper.AudienceNetworkInitializeHelper;
import com.nad.multiads.utils.AZAdsType;

import java.lang.ref.WeakReference;

public class AZAdsNetwork {

    private static final String TAG = "NADAdsNetwork";

    private final WeakReference<Activity> activityRef;
    private final WeakReference<Context> contextRef;

    private final String primaryAppId;
    private final String backupAppId;
    private final String primaryNetwork;
    private final String backupNetwork;
    private final boolean debug;
    private final AdsInitializationListener initializationListener;

    private static AZAdsNetwork instance;

    private AZAdsNetwork(Builder builder) {
        this.activityRef = new WeakReference<>(builder.activity);
        this.contextRef = new WeakReference<>(builder.activity.getApplicationContext());
        this.primaryAppId = builder.primaryAppId;
        this.backupAppId = builder.backupAppId;
        this.primaryNetwork = builder.primaryNetwork;
        this.backupNetwork = builder.backupNetwork;
        this.debug = builder.debug;
        this.initializationListener = builder.listener;
    }

    public static void init(Builder builder) {
        if (instance == null) {
            instance = new AZAdsNetwork(builder);
        }
    }

    public static AZAdsNetwork getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AZAdsNetwork is not initialized. Call init() first.");
        }
        return instance;
    }

    @Nullable
    private Activity safeGetActivity() {
        return activityRef.get();
    }

    @Nullable
    private Context safeGetContext() {
        return contextRef.get();
    }

    public String getPrimaryAppId() {
        return primaryAppId != null ? primaryAppId : "";
    }

    public String getBackupAppId() {
        return backupAppId != null ? backupAppId : "";
    }

    public String getPrimaryNetwork() {
        return primaryNetwork != null ? primaryNetwork : "admob";
    }

    public String getBackupNetwork() {
        return backupNetwork != null ? backupNetwork : "applovin";
    }

    public boolean isDebug() {
        return debug;
    }

    // ===================
    // Builder Class
    // ===================
    public static class Builder {
        private final Activity activity;
        private String primaryAppId = "";
        private String backupAppId = "";
        private String primaryNetwork = "admob";
        private String backupNetwork = "applovin";
        private boolean debug = false;
       private AdsInitializationListener listener = null;

        public Builder(Activity activity) {
            if (activity == null) {
                throw new IllegalArgumentException("Activity cannot be null.");
            }
            this.activity = activity;
        }

        public Builder setPrimaryAppId(String primaryAppId) {
            this.primaryAppId = primaryAppId;
            return this;
        }

        public Builder setBackupAppId(String backupAppId) {
            this.backupAppId = backupAppId;
            return this;
        }

        public Builder setPrimaryNetwork(String primaryNetwork) {
            this.primaryNetwork = primaryNetwork;
            return this;
        }

        public Builder setBackupNetwork(String backupNetwork) {
            this.backupNetwork = backupNetwork;
            return this;
        }

        public Builder setDebug(boolean value) {
            this.debug = value;
            return this;
        }


        public Builder setInitializationListener(AdsInitializationListener listener) {
            this.listener = listener;
            return this;
        }

        public AZAdsNetwork build() {
            AZAdsNetwork.init(this);
            return AZAdsNetwork.getInstance();
        }
    }
    public void initializeAllNetworks() {
        initializePrimaryAdNetwork();
        initializeBackupAdNetwork();
    }

    // Untuk primary network
    public void initializePrimaryAdNetwork() {
        initializeAdNetwork(true);
    }

    // Untuk backup network
    public void initializeBackupAdNetwork() {
        initializeAdNetwork(false);
    }

    private void initializeAdNetwork(boolean isPrimary) {
        Activity activity = safeGetActivity();
        if (activity == null) {
            Log.w(TAG, "Activity is null during initialization, skipping...");
            if (initializationListener != null) {
                initializationListener.onInitializationFailed("Activity is null");
            }
            return;
        }

        String adNetwork = isPrimary ? getPrimaryNetwork() : getBackupNetwork();
        if (adNetwork == null || adNetwork.isEmpty()) {
            Log.e(TAG, "Ad network is null or empty, skipping initialization.");
            if (initializationListener != null) {
                initializationListener.onInitializationFailed("Ad network name is empty");
            }
            return;
        }

        AZAdsType adsType = AZAdsType.fromNetworkName(adNetwork);
        if (adsType == null) {
            Log.w(TAG, (isPrimary ? "Primary" : "Backup") + " Ad network not recognized: " + adNetwork);
            if (initializationListener != null) {
                initializationListener.onInitializationFailed("Ad network not recognized: " + adNetwork);
            }
            return;
        }

        switch (adsType) {
            case ADMOB:
                initializeAdMob();
                break;
            case FAN:
                initializeFacebookAudienceNetwork();
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case FAN_BIDDING_APPLOVIN_MAX:
                initApplovinMax(isPrimary);
                break;
            case APPLOVIN_DISCOVERY:
                initApplovinDiscovery(isPrimary);
                break;
        }

        Log.d(TAG, (isPrimary ? "Primary" : "Backup") + " Ad network initialized: " + adsType.logName());

        if (initializationListener != null) {
            initializationListener.onInitializationSuccess();
        }
    }

    // ====================
    // Init Network Methods
    // ====================

    private void initializeAdMob() {
        Activity activity = safeGetActivity();
        if (activity != null) {
            MobileAds.initialize(activity, initializationStatus -> {});
        }
    }

    private void initializeFacebookAudienceNetwork() {
        boolean debug = isDebug();
        Activity activity = safeGetActivity();
        if (activity != null) {
            if (debug) Log.d(TAG, "Initializing Facebook Audience Network...");
            AudienceNetworkInitializeHelper.initializeAd(activity, debug);
        }
    }

    private void initApplovinMax(boolean isPrimary) {
        Activity activity = safeGetActivity();
        if (activity == null) return;

        AppLovinSdk sdk = AppLovinSdk.getInstance(activity);
        String appId = isPrimary ? getPrimaryAppId() : getBackupAppId();

        if (sdk.isInitialized()) {
            Log.d(TAG, "AppLovin SDK already initialized.");
            return;
        }

        AppLovinSdkInitializationConfiguration initConfig = AppLovinSdkInitializationConfiguration.builder(appId)
                .setMediationProvider(AppLovinMediationProvider.MAX)
//                .setTestDeviceAdvertisingIds(List.of("ab5b1cc2-07a0-42cc-a584-c6a79c7eef88"))
                .build();
        sdk.initialize(initConfig, sdkConfig -> {
            Log.d(TAG, "AppLovin | Applovin Max initialized: " + sdkConfig.toString());
        });
        AudienceNetworkInitializeHelper.initializeAd(activity,isDebug());
    }
    private void initApplovinDiscovery(boolean isPrimary) {
        Activity activity = safeGetActivity();
        if (activity == null) return;

        AppLovinSdk sdk = AppLovinSdk.getInstance(activity);
        String appId = isPrimary ? getPrimaryAppId() : getBackupAppId();
        if (sdk.isInitialized()) {
            Log.d(TAG, "AppLovin SDK Discovery already initialized.");
            return;
        }
        AppLovinSdkInitializationConfiguration initConfig = AppLovinSdkInitializationConfiguration.builder(appId)
                .setMediationProvider(AppLovinMediationProvider.UNKNOWN)
//                .setTestDeviceAdvertisingIds(List.of("ab5b1cc2-07a0-42cc-a584-c6a79c7eef88"))
                .build();
        sdk.initialize(initConfig, sdkConfig -> {
            Log.d(TAG, "AppLovin Discovery initialized: " + sdkConfig.toString());
        });
    }
}
