package com.nad.multiads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.nad.multiads.face.AdsInitializationListener;
import com.nad.multiads.face.OnAZAdCompleteListener;
import com.nad.multiads.utils.AZAdsConfig;
import com.nad.multiads.utils.AZAdsType;

import java.util.concurrent.atomic.AtomicBoolean;

public class AZAppOpenAdManager {
    private static final String LOG_TAG = "AZAppOpenAdManager";

    private final AZAppOpenAdMob admobAd;
    private final AZAppOpenAdAppLovin applovinAd;
    private final AZAdsConfig adsConfig;
    private final AtomicBoolean isPrimaryAds = new AtomicBoolean(true);
    private void initAdsNetwork(@NonNull Context context, @NonNull AZAdsConfig adsConfig, boolean isPrimary) {
        if (!(context instanceof Activity)) {
            return;
        }
        Activity activity = (Activity) context;

        AZAdsNetwork adsNetwork = new AZAdsNetwork.Builder(activity)
                .setPrimaryNetwork(adsConfig.getPrimaryNetwork())
                .setBackupNetwork(adsConfig.getBackupNetwork())
                .setPrimaryAppId(adsConfig.getPrimaryAppId())
                .setBackupAppId(adsConfig.getBackupAppId())
                .setDebug(BuildConfig.DEBUG)
                .setInitializationListener(new AdsInitializationListener() {
                    @Override
                    public void onInitializationSuccess() {

                    }

                    @Override
                    public void onInitializationFailed(String error) {
                    }
                })
                .build();

        if (isPrimary) {
            adsNetwork.initializePrimaryAdNetwork();
        } else {
            adsNetwork.initializeBackupAdNetwork();
        }
    }

    public AZAppOpenAdManager(@NonNull AZAdsConfig adsConfig) {
        this.admobAd = new AZAppOpenAdMob();
        this.applovinAd = new AZAppOpenAdAppLovin();
        this.adsConfig = adsConfig;
    }

    public void preload(@NonNull Context context, boolean isPrimary) {
        isPrimaryAds.set(isPrimary);
        String adUnitId = isPrimary ? adsConfig.getPrimaryUnitId() : adsConfig.getBackupUnitId();
        if (adUnitId.isEmpty()) return;

        String network = isPrimary ? adsConfig.getPrimaryNetwork() : adsConfig.getBackupNetwork();
        AZAdsType adsType = AZAdsType.fromNetworkName(network);
        if (adsType == null) return;
        // Inisialisasi network sebelum load
        initAdsNetwork(context, adsConfig, isPrimary);

        switch (adsType) {
            case ADMOB:
                admobAd.loadAd(context, adUnitId);
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case FAN_BIDDING_APPLOVIN_MAX:
                applovinAd.loadAd(context, adUnitId);
                break;
        }
    }

    public void showIfAvailable(@NonNull Activity activity, @NonNull OnAZAdCompleteListener callback) {
        String unitPrimary = adsConfig.getPrimaryUnitId();
        String unitBackup = adsConfig.getBackupUnitId();
        String netPrimary = adsConfig.getPrimaryNetwork();
        String netBackup = adsConfig.getBackupNetwork();

        AZAdsType typePrimary = AZAdsType.fromNetworkName(netPrimary);
        AZAdsType typeBackup = AZAdsType.fromNetworkName(netBackup);

        // Tampilkan primary jika tersedia
        if (typePrimary == AZAdsType.ADMOB && admobAd.isAdAvailable()) {
            admobAd.showAdIfAvailable(activity, unitPrimary, callback);
            return;
        } else if ((typePrimary == AZAdsType.APPLOVIN || typePrimary == AZAdsType.APPLOVIN_MAX || typePrimary == AZAdsType.FAN_BIDDING_APPLOVIN_MAX) &&
                applovinAd.isAdAvailable()) {
            applovinAd.showAdIfAvailable(activity, unitPrimary, callback);
            return;
        }

        // Fallback ke backup jika primary gagal
        if (typeBackup == AZAdsType.ADMOB && admobAd.isAdAvailable()) {
            admobAd.showAdIfAvailable(activity, unitBackup, callback);
        } else if ((typeBackup == AZAdsType.APPLOVIN || typeBackup == AZAdsType.APPLOVIN_MAX || typeBackup == AZAdsType.FAN_BIDDING_APPLOVIN_MAX) &&
                applovinAd.isAdAvailable()) {
            applovinAd.showAdIfAvailable(activity, unitBackup, callback);
        } else {
            // Tidak ada iklan tersedia
            Log.d(LOG_TAG, "No open ad available.");
            callback.onFinish();
        }
    }
    public boolean isShowingAd() {
        return admobAd.isShowingAd() || applovinAd.isShowingAd();
    }

}
