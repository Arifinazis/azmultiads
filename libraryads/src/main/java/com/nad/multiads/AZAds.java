package com.nad.multiads;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxAdViewConfiguration;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.nad.multiads.face.AdsInitializationListener;
import com.nad.multiads.face.NativeAdLoadCallbackWrapper;
import com.nad.multiads.helper.AdmobAdUtils;
import com.nad.multiads.helper.ApplovinAdUtils;
import com.nad.multiads.helper.ApplovinNativeAdWrapper;
import com.nad.multiads.helper.ApplovinNativeAdWrapperFull;
import com.nad.multiads.helper.ShowInterAdmobListener;
import com.nad.multiads.helper.ShowInterAppMaxListener;
import com.nad.multiads.helper.ShowRewardAdmobListener;
import com.nad.multiads.helper.ShowRewardAppMaxListener;
import com.nad.multiads.utils.AZAdsCallback;
import com.nad.multiads.utils.AZAdsConfig;
import com.nad.multiads.utils.AZAdsConstant;
import com.nad.multiads.utils.AZAdsRunUtils;
import com.nad.multiads.utils.AZAdsType;
import com.nad.multiads.utils.AZAdsUtils;
import com.nad.multiads.utils.AZNativeAdsStyle;
import com.nad.multiads.view.AZAdLoadingScreen;
import com.nad.multiads.view.AppLovinNativeAdFactory;
import com.nad.multiads.view.NadBannerView;
import com.nad.multiads.view.NadNativeView;


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AZAds: Modular Ads Manager untuk Android (AdMob, AppLovin, Facebook Audience Network)
 * --------------------------------------------------------------------------------------
 * Fitur Utama:
 * - Mendukung semua jenis iklan utama: Banner, Interstitial, Rewarded, dan Native (Normal & Fullscreen).
 * - Modularisasi melalui AZAdsConfig (konfigurasi per unit & jaringan) dan AZAdsType (jenis iklan).
 * - Mendukung fallback ke jaringan cadangan jika primary gagal.
 * - Dukungan preload queue (native ads) menggunakan thread-safe queue dan flag AtomicBoolean.
 * - Thread aman (thread-safe), singleton global, dan manajemen memori yang stabil.
 * - Dukungan delay menggunakan Handler bawaan (melalui AZAdsRunUtils.runWithDelay()).
 * Cara Penggunaan:
 * 1. Inisialisasi Konfigurasi Iklan:
 *    AZAds.getInstance(context).initAdConfigs(bannerConfig, interConfig, rewardConfig, nativeNormalConfig, nativeFullConfig);
 * 2. Preload Native Ads:
 *    AZAds.getInstance(context).preloadNative(context, config, isPrimary, isFullscreen);
 * 3. Menampilkan Native Ads (NORMAL atau FULLSCREEN):
 *    AZAds.getInstance(context).showNativeAd(context, nadNativeView, isPrimary, isFullscreen, callback);
 * 4. Menampilkan Interstitial:
 *    AZAds.getInstance(context).showInterstitialAd(activity, config, isPrimary, isResume, callback);
 * 5. Menampilkan Rewarded:
 *    AZAds.getInstance(context).showReward(activity, config, isPrimary, callback);
 * 6. Menampilkan Banner:
 *    AZAds.getInstance(context).showBanner(context, config, nadBannerView, isPrimary);
 * 7. Mendapatkan Jumlah Antrian Native Ads (Full):
 *    AZAds.getInstance(context).getNativeFullScreenSize();
 * 8. Mendapatkan UI Loading Screen Default:
 *    AZAds.getInstance(context).getLoadingScreen();
 * 9. Membersihkan Semua Queue atau Iklan Aktif:
 *    AZAds.getInstance(context).destroy(); // (opsional - bisa ditambahkan sendiri)
 * Catatan:
 * - Semua interaksi antar jaringan menggunakan fallback otomatis bila ad gagal ditampilkan.
 * - Aman digunakan di Activity, Fragment, Adapter, atau custom view.
 */

public class AZAds {
    // TAG Debugging
    private static final String TAG = "AZAds";
    private static final String TAG_INIT = "AZAds_Init";
    private static final String TAG_BANNER = "AZAds_banner_Normal";
    private static final String TAG_LOAD_INTER_PROCESS = "AZAds_load_inter_process";
    private static final String TAG_LOAD_INTER = "AZAds_load_inter";
    private static final String TAG_LOAD_INTER_RESUME = "AZAds_load_inter_resume";
    private static final String TAG_LOAD_REWARD = "AZAds_load_reward";
    private static final String TAG_LOAD_REWARD_PROCESS = "AZAds_load_reward_process";
    private static final String TAG_LOAD_REWARD_RESUME = "AZAds_load_reward_resume";
    private static final String TAG_PRELOAD_NATIVE = "AZAds_Preload_Normal";
    private static final String TAG_PRELOAD_NATIVE_FULL = "AZAds_Preload_Full";
    private static final String TAG_LOAD_NATIVE = "AZAds_Load_Normal";
    private static final String TAG_LOAD_NATIVE_FULL = "AZAds_Load_Full";
    private static final String TAG_SHOW_NATIVE = "AZAds_Show_Normal";
    private static final String TAG_SHOW_NATIVE_FULL = "AZAds_Show_Full";

    // Singleton & Context Management
    private static volatile AZAds instance;
    private final Context appContext;
    private static final long HANDLER_DELAY_MS = 2000;
    private final LinkedBlockingQueue<Object> mNativeAdsQueue = new LinkedBlockingQueue<>(100);
    private final LinkedBlockingQueue<Object> mNativeFullScreenQueue = new LinkedBlockingQueue<>(100);
    private final AtomicBoolean isLoadingNativeNormal = new AtomicBoolean(false);
    private final AtomicBoolean isLoadingNativeFull = new AtomicBoolean(false);

    private final AtomicBoolean isBannerAlready = new AtomicBoolean(false);
    private AZAdsConfig adsCBanner, adsCInter, adsCReward;
    private AZAdsConfig adsConfigNativeNormal, adsConfigNativeFull;

    private final AZAdLoadingScreen loadingScreen;
    private int countNativeNormal = 0;
    private int countNativeFull = 0;
    @Nullable
    public InterstitialAd mInterAdmob;
    @Nullable
    public MaxInterstitialAd mMaxinterstitialAd;
    private int retryAttempt;
    private final AtomicBoolean isLoadingInter = new AtomicBoolean(false);


    public com.google.android.gms.ads.rewarded.RewardedAd adMobRewardedAd;
    public MaxRewardedAd mMaxRewardedAd;
    private final AtomicBoolean isLoadingReward = new AtomicBoolean(false);

    private AZAds(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
        this.loadingScreen = new AZAdLoadingScreen();
    }


    public static AZAds getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (AZAds.class) {
                if (instance == null) {
                    instance = new AZAds(context);
                }
            }
        }
        return instance;
    }

    public void initAdConfigs(@Nullable AZAdsConfig banner, @Nullable AZAdsConfig inter, @Nullable AZAdsConfig reward,
                              @Nullable AZAdsConfig nativeNormal, @Nullable AZAdsConfig nativeFull) {
        this.adsCBanner = banner;
        this.adsCInter = inter;
        this.adsCReward = reward;
        this.adsConfigNativeNormal = nativeNormal;
        this.adsConfigNativeFull = nativeFull;
    }


    private void initAdsNetwork(@NonNull Context context, @NonNull AZAdsConfig adsConfig, boolean isPrimary) {
        if (!(context instanceof Activity)) {
            Log.w(TAG_INIT, "Context is not an Activity, skipping SDK init.");
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
                        Log.d(TAG_INIT, "All Ads SDK initialized successfully");
                    }

                    @Override
                    public void onInitializationFailed(String error) {
                        Log.e(TAG_INIT, "Initialization failed: " + error);
                    }
                })
                .build();

        if (isPrimary) {
            adsNetwork.initializePrimaryAdNetwork();
        } else {
            adsNetwork.initializeBackupAdNetwork();
        }
    }


    public AZAdLoadingScreen getLoadingScreen() {
        return loadingScreen;
    }


    public int getNativeFullScreenSize() {
        return mNativeFullScreenQueue.size();
    }

    private void startLoading(ShimmerFrameLayout shimmerLoader) {
        if (shimmerLoader != null) {
            shimmerLoader.setVisibility(View.VISIBLE);
            shimmerLoader.post(shimmerLoader::startShimmer);
        }
    }

    private void stopLoading(ShimmerFrameLayout shimmerLoader) {
        if (shimmerLoader != null) {
            try {
                shimmerLoader.stopShimmer();
            } catch (Exception e) {
                e.printStackTrace();
            }
            shimmerLoader.setVisibility(View.GONE);
        }
    }

    private void prepareNativeView(NadNativeView nativeView, AZNativeAdsStyle style) {
        if (nativeView == null || style == null) return;

        nativeView.setNativeAdsStyle(style);
        nativeView.setVisibility(View.VISIBLE);
        startLoading(nativeView.getShimmerLoader());
    }

    private void failedNative(NativeAdLoadCallbackWrapper callback, NadNativeView adFrame) {
        if (callback != null) callback.getOnFailed().run();
        stopLoading(adFrame.getShimmerLoader());
    }

    private void successNative(NativeAdLoadCallbackWrapper callback, NadNativeView adFrame) {
        if (callback != null) callback.getOnSuccess().run();
        stopLoading(adFrame.getShimmerLoader());
    }

    private boolean checkCappingTime(AZAdsConfig config) {
        long currentTimeMillis = System.currentTimeMillis() - AZAdsConstant.lastShowAdFull;
        return currentTimeMillis > config.getIntervalAdFull() && System.currentTimeMillis() - AZAdsConstant.lastShowAdResume > config.getIntervalAdResume() && System.currentTimeMillis() - AZAdsConstant.lastShowAdReward > config.getIntervalAdFull();
    }

    private boolean checkCappingTimeResume(AZAdsConfig config) {
        return System.currentTimeMillis() - AZAdsConstant.lastShowAdFull > config.getIntervalAdResume();
    }

    @Nullable
    public static Activity toActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }

        // Jika context dibungkus (misalnya ContextThemeWrapper), telusuri sampai ketemu Activity
        Context baseContext = context;
        while (baseContext instanceof android.content.ContextWrapper) {
            if (baseContext instanceof Activity) {
                return (Activity) baseContext;
            }
            baseContext = ((android.content.ContextWrapper) baseContext).getBaseContext();
        }

        return null;
    }

    //====================SHOW BANNER==========================================//
    public void showBanner(@NonNull Context context, @Nullable AZAdsConfig config, @NonNull NadBannerView nadBannerView, boolean isPrimary) {
        if (config != null) {
            adsCBanner = config;
        }
        initAdsNetwork(context, adsCBanner, isPrimary);
        String network = isPrimary ? adsCBanner.getPrimaryNetwork() : adsCBanner.getBackupNetwork();
        AZAdsType adsType = AZAdsType.fromNetworkName(network);
        if (adsType == null) {
            Log.e(TAG_BANNER, ">> GAGAL: Jaringan iklan tidak dikenal - " + network);
            isBannerAlready.set(false);
            return;
        }

        nadBannerView.setVisibility(View.VISIBLE);
        nadBannerView.showLoading();

        Log.d(TAG_BANNER, ">> Deteksi jaringan: " + adsType.name());
        switch (adsType) {
            case ADMOB:
                showBannerAdmob(context, nadBannerView, isPrimary);
                break;
            case FAN:
                showBannerFAN(context, nadBannerView, isPrimary);
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case FAN_BIDDING_APPLOVIN_MAX:
                showBannerApplovinMax(context, nadBannerView, isPrimary);
                break;
        }
    }

    private void showBannerAdmob(Context context, NadBannerView nadBannerView, boolean isPrimary) {
        String adUnitId = isPrimary ? adsCBanner.getPrimaryUnitId() : adsCBanner.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            nadBannerView.hideAd();
            return;
        }

        com.google.android.gms.ads.AdView admobBanner = new com.google.android.gms.ads.AdView(context);
        admobBanner.setAdSize(AZAdsUtils.getAdSize(context, adsCBanner.getBannerType()));
        admobBanner.setAdUnitId(adUnitId);
        nadBannerView.getAdContainer().removeAllViews();
        nadBannerView.getAdContainer().addView(admobBanner);


        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        Bundle collapsibleExtras = new Bundle();
        collapsibleExtras.putString("collapsible", "bottom");
        adRequestBuilder.addNetworkExtrasBundle(com.google.ads.mediation.admob.AdMobAdapter.class, collapsibleExtras);

        AZAdsType adsType = AZAdsType.fromNetworkName(isPrimary ? adsCBanner.getPrimaryNetwork() : adsCBanner.getBackupNetwork());
        AdRequest adRequest = AZAdsUtils.createAdaptiveAdRequest(context, adsType, adsCBanner.getAdKeywords(), adsCBanner.getBannerType());

        admobBanner.setAdListener(new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (isPrimary) {
                    showBanner(context, adsCBanner, nadBannerView, false);
                } else {
                    isBannerAlready.set(false);
                    nadBannerView.hideAd();
                }
            }

            @Override
            public void onAdLoaded() {
                isBannerAlready.set(true);
                nadBannerView.hideLoading();
                nadBannerView.getAdContainer().setVisibility(View.VISIBLE);
            }
        });
        admobBanner.loadAd(adRequest);

    }

    private void showBannerFAN(Context context, NadBannerView nadBannerView, boolean isPrimary) {
        nadBannerView.hideAd();
    }

    private void showBannerApplovinMax(Context context, NadBannerView nadBannerView, boolean isPrimary) {
        String adUnitId = isPrimary ? adsCBanner.getPrimaryUnitId() : adsCBanner.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            nadBannerView.hideAd();
            return;
        }
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize(context).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx(context, heightDp);
        MaxAdViewConfiguration config = MaxAdViewConfiguration.builder()
                .setAdaptiveType(MaxAdViewConfiguration.AdaptiveType.ANCHORED)
                .build();
        MaxAdView maxAdView = new MaxAdView(adUnitId, config);
        nadBannerView.getAdContainer().addView(maxAdView);
        maxAdView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(@NonNull MaxAd ad) {
            }

            @Override
            public void onAdCollapsed(@NonNull MaxAd ad) {
            }

            @Override
            public void onAdDisplayed(@NonNull MaxAd ad) {
            }

            @Override
            public void onAdHidden(@NonNull MaxAd ad) {
            }

            @Override
            public void onAdClicked(@NonNull MaxAd ad) {
            }

            @Override
            public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError error) {
                if (isPrimary) {
                    showBanner(context, adsCBanner, nadBannerView, false);
                } else {
                    isBannerAlready.set(false);
                    nadBannerView.hideAd();
                }
            }

            @Override
            public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
                if (!isPrimary) {
                    isBannerAlready.set(false);
                    nadBannerView.hideAd();
                }
            }

            @Override
            public void onAdLoaded(@NonNull MaxAd ad) {
                isBannerAlready.set(true);
                nadBannerView.hideLoading();
                nadBannerView.getAdContainer().setVisibility(View.VISIBLE);
            }
        });

        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        maxAdView.setBackgroundColor(
                adsCBanner.isDark() ? adsCBanner.getColorDark(context) : adsCBanner.getColorLight(context)
        );
        maxAdView.loadAd();
    }

    //====================LOAD INTER==========================================//
    public void loadInter(AZAdsConfig adsConfig, boolean isPrimary, AZAdsCallback callback) {
        if (adsConfig != null) {
            adsCInter = adsConfig;
        }
        if (isLoadingInter.get()) {
            return;
        }
        isLoadingInter.set(true);

        if (adsCInter == null) {
            isLoadingInter.set(false);  // Reset jika gagal
            callback.onAdFailedToLoad();
            Log.d(TAG_LOAD_INTER, ">> config null");
            return;
        }
        String network = isPrimary ? adsCInter.getPrimaryNetwork() : adsCInter.getBackupNetwork();
        AZAdsType adsType = AZAdsType.fromNetworkName(network);
        if (adsType == null) {
            Log.e(TAG_LOAD_INTER_PROCESS, ">> GAGAL: Jaringan iklan tidak dikenal - " + network);
            isLoadingInter.set(false);
            callback.onAdFailedToLoad();
            return;
        }
        initAdsNetwork(appContext, adsCInter, isPrimary);
        Log.d(TAG_LOAD_INTER_PROCESS, ">> Deteksi jaringan: " + adsType.name());
        switch (adsType) {
            case ADMOB:
                loadInterAdmob(adsCInter, isPrimary, callback, adsType);
                break;
            case FAN:
                callback.onAdFailedToLoad();
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case FAN_BIDDING_APPLOVIN_MAX:
                loadInterApplovin(adsCInter, isPrimary, callback, adsType);
                break;
        }
    }

    private void loadInterAdmob(AZAdsConfig adsConfig, boolean isPrimary, AZAdsCallback callback, AZAdsType adsType) {
        if (adsConfig == null) {
            callback.onAdFailedToLoad();
            return;
        }
        String adUnitId = isPrimary ? adsConfig.getPrimaryUnitId() : adsConfig.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingInter.set(false);
            callback.onAdFailedToLoad();
            return;
        }
        AdRequest adRequest = AZAdsUtils.getAdsRequest(appContext, adsType, adsConfig);

        InterstitialAd.load(appContext, adUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                isLoadingInter.set(false);
                mInterAdmob = null;
                if (isPrimary) {
                    loadInter(adsConfig, false, callback);
                } else {
                    callback.onAdFailedToLoad();
                }
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                isLoadingInter.set(false);
                mInterAdmob = interstitialAd;
                callback.onAdLoaded();
            }
        });

    }

    private void loadInterApplovin(AZAdsConfig adsConfig, boolean isPrimary, AZAdsCallback callback, AZAdsType adsType) {
        if (adsConfig == null) {
            callback.onAdFailedToLoad();
            return;
        }
        String adUnitId = isPrimary ? adsConfig.getPrimaryUnitId() : adsConfig.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingInter.set(false);
            callback.onAdFailedToLoad();
            return;
        }
        mMaxinterstitialAd = new MaxInterstitialAd(adUnitId);

        mMaxinterstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(@NonNull MaxAd maxAd) {
                isLoadingInter.set(false);
                retryAttempt = 0;
                callback.onAdLoaded();
            }

            @Override
            public void onAdDisplayed(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdHidden(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdClicked(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
                isLoadingInter.set(false);
                retryAttempt++;
                long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
                AZAdsRunUtils.runWithDelay(new Runnable() {
                    @Override
                    public void run() {
                        mMaxinterstitialAd.loadAd();
                    }
                },delayMillis);
               if (isPrimary) {
                    loadInter(adsConfig, false, callback);
                } else {
                    callback.onAdFailedToLoad();
                }
            }

            @Override
            public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {

            }
        });

        mMaxinterstitialAd.loadAd();
    }

    //====================SHOW INTER==========================================//
    public void showInterstitialAd(
            @NonNull Activity activity,
            @Nullable AZAdsConfig adsConfig, boolean isPrimary,boolean isResume, AZAdsCallback callback) {
        if (adsConfig != null){
            adsCInter = adsConfig;
        }
        if (adsCInter == null) {
            callback.onAdCompleted();
            Log.d(TAG_LOAD_INTER, ">> config null");
            return;
        }
        String network = isPrimary ? adsCInter.getPrimaryNetwork() : adsCInter.getBackupNetwork();
        AZAdsType adsType = AZAdsType.fromNetworkName(network);
        if (adsType == null) {
            Log.e(TAG_LOAD_INTER_PROCESS, ">> GAGAL: Jaringan iklan tidak dikenal - " + network);
            callback.onAdCompleted();
            return;
        }
        switch (adsType) {
            case ADMOB:
                showInterAdmob(activity, adsCInter, isPrimary,isResume, callback);
                break;
            case FAN:
                callback.onAdCompleted();
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case FAN_BIDDING_APPLOVIN_MAX:
                showInterApplovin(activity, adsCInter, isPrimary,isResume, callback);
                break;
        }
    }
    private void showInterAdmob(@NonNull Activity activity, @Nullable  AZAdsConfig adsConfig, boolean isPrimary,boolean isResume, AZAdsCallback callback) {
        if (adsConfig != null) {
            adsCInter = adsConfig;
        }
        if (adsCInter == null){
            callback.onAdCompleted();
            return;
        }

        if (isResume){
            if (mInterAdmob == null || (adsCInter.isEnableCapping() && !checkCappingTimeResume(adsCInter))) {
                loadInter(adsConfig, isPrimary, callback);
                callback.onAdCompleted();
                return;
            }
        }else{
            if (mInterAdmob == null || (adsCInter.isEnableCapping() && !checkCappingTime(adsCInter))) {
                if (adsCInter.isPreload()) {
                    loadInter(adsConfig, isPrimary, callback);
                }
                callback.onAdCompleted();
                return;
            }
            if (!activity.isFinishing() && !loadingScreen.isAdded() && !loadingScreen.isDetached()) {
                loadingScreen.showAllowingStateLoss(
                        ((AppCompatActivity) activity).getSupportFragmentManager(),
                        "showInterstitialAd"
                );
            }
        }


        if (mInterAdmob != null) {
            mInterAdmob.setFullScreenContentCallback(new ShowInterAdmobListener(this, activity, adsCInter, callback, loadingScreen, isPrimary, isResume));
        }else{
            callback.onAdCompleted();
            return;
        }
        if (isResume){
            if (activity.isFinishing() || mInterAdmob == null) {
                return;
            }
            mInterAdmob.show(activity);
        }else{
            AZAdsRunUtils.runWithDelay(new Runnable() {
                @Override
                public void run() {
                    if (activity.isFinishing() || mInterAdmob == null) {
                        return;
                    }
                    mInterAdmob.show(activity);
                }
            },1100L);
        }

    }
    private void showInterApplovin(@NonNull Activity activity, @Nullable  AZAdsConfig adsConfig, boolean isPrimary,boolean isResume, AZAdsCallback callback) {
        if (adsConfig != null) {
            adsCInter = adsConfig;
        }
        if (adsCInter == null){
            callback.onAdCompleted();
            return;
        }

        if (isResume){
            if (mMaxinterstitialAd == null || (adsCInter.isEnableCapping() && !checkCappingTimeResume(adsCInter))) {
                loadInter(adsConfig, isPrimary, callback);
                callback.onAdCompleted();
                return;
            }
        }else{
            if (mMaxinterstitialAd == null || (adsCInter.isEnableCapping() && !checkCappingTime(adsCInter))) {
                if (adsCInter.isPreload()) {
                    loadInter(adsConfig, isPrimary, callback);
                }
                callback.onAdCompleted();
                return;
            }
            if (!activity.isFinishing() && !loadingScreen.isAdded() && !loadingScreen.isDetached()) {
                loadingScreen.showAllowingStateLoss(
                        ((AppCompatActivity) activity).getSupportFragmentManager(),
                        "showInterstitialAd"
                );
            }
        }


        if (mMaxinterstitialAd != null) {
            mMaxinterstitialAd.setListener(new ShowInterAppMaxListener(this, activity, adsCInter, callback, loadingScreen, isPrimary, isResume));
        }
        if (isResume){
            if (activity.isFinishing() || mMaxinterstitialAd == null) {
                return;
            }
            if (mMaxinterstitialAd.isReady()){
                mMaxinterstitialAd.showAd(activity);
            }else{
                callback.onAdCompleted();
            }
        }else{
            AZAdsRunUtils.runWithDelay(new Runnable() {
                @Override
                public void run() {
                    if (activity.isFinishing() || mMaxinterstitialAd == null) {
                        return;
                    }
                    if (mMaxinterstitialAd.isReady()){
                        mMaxinterstitialAd.showAd(activity);
                    }else{
                        callback.onAdCompleted();
                    }
                }
            },1100L);
        }

    }



    //====================LOAD REWARD==========================================//
    public void loadReward(AZAdsConfig adsConfig, boolean isPrimary, AZAdsCallback callback) {
        if (adsConfig != null) {
            adsCReward = adsConfig;
        }
        if (isLoadingReward.get()) {
            return;
        }
        isLoadingReward.set(true);

        if (adsCReward == null) {
            isLoadingReward.set(false);  // Reset jika gagal
            callback.onAdFailedToLoad();
            Log.d(TAG_LOAD_INTER, ">> config null");
            return;
        }
        String network = isPrimary ? adsCReward.getPrimaryNetwork() : adsCReward.getBackupNetwork();
        AZAdsType adsType = AZAdsType.fromNetworkName(network);
        if (adsType == null) {
            Log.e(TAG_LOAD_REWARD, ">> GAGAL: Jaringan iklan tidak dikenal - " + network);
            isLoadingReward.set(false);
            callback.onAdFailedToLoad();
            return;
        }
        initAdsNetwork(appContext, adsCReward, isPrimary);
        Log.d(TAG_LOAD_REWARD_PROCESS, ">> Deteksi jaringan: " + adsType.name());
        switch (adsType) {
            case ADMOB:
                loadRewardAdmob(adsCReward, isPrimary, callback, adsType);
                break;
            case FAN:
                callback.onAdFailedToLoad();
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case FAN_BIDDING_APPLOVIN_MAX:
                loadRewardAppMax(adsCReward, isPrimary, callback, adsType);
                break;
        }
    }
    private void loadRewardAdmob(AZAdsConfig adsConfig, boolean isPrimary, AZAdsCallback callback, AZAdsType adsType) {
        if (adsConfig == null) {
            callback.onAdFailedToLoad();
            return;
        }
        String adUnitId = isPrimary ? adsConfig.getPrimaryUnitId() : adsConfig.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingReward.set(false);
            callback.onAdFailedToLoad();
            return;
        }
        AdRequest adRequest = AZAdsUtils.getAdsRequest(appContext, adsType, adsConfig);
        com.google.android.gms.ads.rewarded.RewardedAd.load(appContext, adUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                adMobRewardedAd = ad;
                isLoadingReward.set(false);
                callback.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d(TAG, loadAdError.toString());
                adMobRewardedAd = null;
                isLoadingInter.set(false);
                if (isPrimary) {
                    loadReward(adsConfig, false, callback);
                } else {
                    callback.onAdFailedToLoad();
                }
            }
        });
    }

    private void loadRewardAppMax(AZAdsConfig adsConfig, boolean isPrimary, AZAdsCallback callback, AZAdsType adsType) {
        if (adsConfig == null) {
            callback.onAdFailedToLoad();
            return;
        }
        String adUnitId = isPrimary ? adsConfig.getPrimaryUnitId() : adsConfig.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingReward.set(false);
            callback.onAdFailedToLoad();
            return;
        }
        mMaxRewardedAd = MaxRewardedAd.getInstance(adUnitId);
        mMaxRewardedAd.setListener(new MaxRewardedAdListener() {
            @Override
            public void onUserRewarded(@NonNull MaxAd maxAd, @NonNull MaxReward maxReward) {

            }

            @Override
            public void onAdLoaded(@NonNull MaxAd maxAd) {
                isLoadingReward.set(false);
                retryAttempt = 0;
                callback.onAdLoaded();
            }

            @Override
            public void onAdDisplayed(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdHidden(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdClicked(@NonNull MaxAd maxAd) {

            }

            @Override
            public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
                isLoadingReward.set(false);
                retryAttempt++;
                long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
                AZAdsRunUtils.runWithDelay(new Runnable() {
                    @Override
                    public void run() {
                        mMaxRewardedAd.loadAd();
                    }
                },delayMillis);
                if (isPrimary) {
                    loadReward(adsConfig, false, callback);
                } else {
                    callback.onAdFailedToLoad();
                }
            }

            @Override
            public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {

            }
        });

        mMaxRewardedAd.loadAd();
    }
    //====================SHOW REWARD==========================================//
    public void showReward(
            @NonNull Activity activity,
            @Nullable AZAdsConfig adsConfig, boolean isPrimary, AZAdsCallback callback) {
        if (adsConfig != null){
            adsCReward = adsConfig;
        }
        if (adsCReward == null) {
            callback.onAdCompleted();
            return;
        }
        String network = isPrimary ? adsCReward.getPrimaryNetwork() : adsCReward.getBackupNetwork();
        AZAdsType adsType = AZAdsType.fromNetworkName(network);
        if (adsType == null) {
            callback.onAdCompleted();
            return;
        }
        switch (adsType) {
            case ADMOB:
                showRewardAdmob(activity, adsCReward, isPrimary, callback);
                break;
            case FAN:
                callback.onAdCompleted();
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case FAN_BIDDING_APPLOVIN_MAX:
                showRewardAppMax(activity, adsCInter, isPrimary, callback);
                break;
        }
    }
    private void showRewardAdmob(@NonNull Activity activity, @Nullable  AZAdsConfig adsConfig, boolean isPrimary, AZAdsCallback callback) {
        if (adsConfig != null) {
            adsCReward = adsConfig;
        }
        if (adsCReward == null){
            callback.onAdCompleted();
            return;
        }

        if (adMobRewardedAd == null) {
            if (adsCReward.isPreload()) {
                loadInter(adsConfig, isPrimary, callback);
            }
            callback.onAdCompleted();
            return;
        }
        if (!activity.isFinishing() && !loadingScreen.isAdded() && !loadingScreen.isDetached()) {
            loadingScreen.showAllowingStateLoss(
                    ((AppCompatActivity) activity).getSupportFragmentManager(),
                    "showRewardAd"
            );
        }

        if (adMobRewardedAd != null) {
            adMobRewardedAd.setFullScreenContentCallback(new ShowRewardAdmobListener(this, activity, adsCReward, callback, loadingScreen, isPrimary));
        }else{
            callback.onAdCompleted();
            return;
        }
        AZAdsRunUtils.runWithDelay(new Runnable() {
            @Override
            public void run() {
                adMobRewardedAd.show(activity, rewardItem -> {
                    callback.onAdCompleted();
                });
            }
        },1100L);
    }
    private void showRewardAppMax(@NonNull Activity activity, @Nullable  AZAdsConfig adsConfig, boolean isPrimary, AZAdsCallback callback) {
        if (adsConfig != null) {
            adsCReward = adsConfig;
        }
        if (adsCReward == null){
            callback.onAdCompleted();
            return;
        }

        if (mMaxRewardedAd == null ) {
            if (adsCReward.isPreload()) {
                loadInter(adsConfig, isPrimary, callback);
            }
            callback.onAdCompleted();
            return;
        }
        if (!activity.isFinishing() && !loadingScreen.isAdded() && !loadingScreen.isDetached()) {
            loadingScreen.showAllowingStateLoss(
                    ((AppCompatActivity) activity).getSupportFragmentManager(),
                    "showInterstitialAd"
            );
        }

        if (mMaxRewardedAd != null) {
            mMaxRewardedAd.setListener(new ShowRewardAppMaxListener(this, activity, adsCInter, callback, loadingScreen, isPrimary));
        }
        AZAdsRunUtils.runWithDelay(new Runnable() {
            @Override
            public void run() {
                if (mMaxRewardedAd.isReady()){
                    mMaxRewardedAd.showAd(activity);
                }else{
                    callback.onAdCompleted();
                }
            }
        },1100L);
    }

    //====================NATIVE UTILITY==========================================//
    public void preloadNative(@NonNull Context context, @Nullable AZAdsConfig config, boolean isPrimary, boolean isFullscreen) {
        if (isFullscreen) {
            if (config != null) {
                adsConfigNativeFull = config;
            }
            preloadNativeFull(context, isPrimary);
        } else {
            if (config != null) {
                adsConfigNativeNormal = config;
            }
            preloadNativeNormal(context, isPrimary);
        }
    }

    public void showNativeAd(@NonNull Context context,
                             @NonNull NadNativeView nativeView,
                             boolean isPrimary,
                             boolean isFullscreen,
                             @Nullable NativeAdLoadCallbackWrapper callbackWrapper) {
        if (isFullscreen) {
            showNativeAdFull(context, nativeView, isPrimary, callbackWrapper);
        } else {
            showNativeAdNormal(context, nativeView, isPrimary, callbackWrapper);
        }
    }

    //====================PRELOAD NORMAL==========================================//
    private void preloadNativeNormal(@NonNull Context context, boolean isPrimary) {
        if (adsConfigNativeNormal == null) {
            Log.w(TAG_PRELOAD_NATIVE, ">> GAGAL: adsConfigNativeNormal NULL");
            isLoadingNativeNormal.set(false);
            return;
        }

        Log.d(TAG_PRELOAD_NATIVE, ">> MULAI preload NORMAL | isPrimary: " + isPrimary + " | queueSize: " + mNativeAdsQueue.size());

        isLoadingNativeNormal.set(true);
        if (mNativeAdsQueue.size() >= adsConfigNativeNormal.getMaxCountAds() || countNativeNormal >= adsConfigNativeNormal.getMaxCountAds()) {
            Log.d(TAG_PRELOAD_NATIVE, ">> LEWATI preload: queue sudah penuh");
            countNativeNormal = 0;
            return;
        }
        initAdsNetwork(context, adsConfigNativeNormal, isPrimary);
        AZAdsRunUtils.runInBackground(() -> {
            String network = isPrimary ? adsConfigNativeNormal.getPrimaryNetwork() : adsConfigNativeNormal.getBackupNetwork();
            AZAdsType adsType = AZAdsType.fromNetworkName(network);
            if (adsType == null) {
                Log.e(TAG_PRELOAD_NATIVE, ">> GAGAL: Jaringan iklan tidak dikenal - " + network);
                isLoadingNativeNormal.set(false);
                return;
            }

            Log.d(TAG_PRELOAD_NATIVE, ">> Deteksi jaringan: " + adsType.name());
            switch (adsType) {
                case ADMOB:
                    preloadNativeAdmob(context, isPrimary);
                    break;
                case FAN:
                    preloadNativeFan(context, isPrimary);
                    break;
                case APPLOVIN:
                case APPLOVIN_MAX:
                case FAN_BIDDING_APPLOVIN_MAX:
                    preloadNativeApplovin(context, isPrimary);
                    break;
            }
        });
    }

    private void preloadNativeAdmob(Context context, boolean isPrimary) {
        if (adsConfigNativeNormal == null) {
            isLoadingNativeNormal.set(false);
            return;
        }
        String adUnitId = isPrimary ? adsConfigNativeNormal.getPrimaryUnitId() : adsConfigNativeNormal.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingNativeNormal.set(false);
            return;
        }

        Log.d(TAG_PRELOAD_NATIVE, "ADMOB | NORMAL ");
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                .build();
        AdLoader adLoader = new AdLoader.Builder(context, adUnitId)
                .forNativeAd(nativeAd -> {
                    if (mNativeAdsQueue.size() < adsConfigNativeNormal.getMaxCountAds()) {
                        mNativeAdsQueue.add(nativeAd);
                        countNativeNormal++;
                    } else {
                        nativeAd.destroy();
                    }
                    isLoadingNativeNormal.set(false);
                    Log.d(TAG, "AdMob normal" + " loaded. Queue size: " + mNativeAdsQueue.size());
                })
                .withAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        isLoadingNativeNormal.set(false);
                        Log.e(TAG_PRELOAD_NATIVE, String.format(
                                "ADMOB Native %s failed: %s", "NORMAL",
                                adError.getMessage()
                        ));
                        if (isPrimary) {
                            preloadNativeNormal(context, false);
                        }
                    }
                })
                .withNativeAdOptions(adOptions)
                .build();

        AZAdsType adsType = AZAdsType.fromNetworkName(isPrimary ? adsConfigNativeNormal.getPrimaryNetwork() : adsConfigNativeNormal.getBackupNetwork());
        AdRequest adRequest = AZAdsUtils.getAdsRequest(context, adsType, adsConfigNativeNormal);
        adLoader.loadAd(adRequest);
    }

    private void preloadNativeFan(Context context, boolean isPrimary) {
        if (adsConfigNativeNormal == null) {
            isLoadingNativeNormal.set(false);
            return;
        }
        String adUnitId = isPrimary ? adsConfigNativeNormal.getPrimaryUnitId() : adsConfigNativeNormal.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingNativeNormal.set(false);
            return;
        }

        Log.d(TAG_PRELOAD_NATIVE, "FAN | NORMAL");

        com.facebook.ads.NativeAd fanNativeAd = new com.facebook.ads.NativeAd(context, adUnitId);

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // optional
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                isLoadingNativeNormal.set(false);
                Log.e(TAG_PRELOAD_NATIVE, String.format(
                        "FAN Native %s failed: %s", "NORMAL",
                        adError.getErrorMessage()
                ));

                if (isPrimary) {
                    preloadNativeNormal(context, false);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (mNativeAdsQueue.size() < adsConfigNativeNormal.getMaxCountAds()) {
                    mNativeAdsQueue.add(ad);
                    countNativeNormal++;
                } else {
                    ad.destroy();
                }
                isLoadingNativeNormal.set(false);
                Log.d(TAG, "FAN normal" + " loaded. Queue size: " + mNativeAdsQueue.size());
            }

            @Override
            public void onAdClicked(Ad ad) {
                // optional
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // optional
            }
        };

        com.facebook.ads.NativeAd.NativeLoadAdConfig loadAdConfig =
                fanNativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build();
        fanNativeAd.loadAd(loadAdConfig);
    }

    private void preloadNativeApplovin(Context context, boolean isPrimary) {
        if (adsConfigNativeNormal == null) {
            isLoadingNativeNormal.set(false);
            return;
        }

        String adUnitId = isPrimary ? adsConfigNativeNormal.getPrimaryUnitId() : adsConfigNativeNormal.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingNativeNormal.set(false);
            return;
        }

        Log.d(TAG_PRELOAD_NATIVE, "APPLOVIN_MAX | NORMAL");

        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(adUnitId);

        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(@Nullable MaxNativeAdView maxNativeAdView, @NonNull MaxAd maxAd) {
                isLoadingNativeNormal.set(false);
                if (mNativeAdsQueue.size() < adsConfigNativeNormal.getMaxCountAds()) {
                    ApplovinNativeAdWrapper wrapper = new ApplovinNativeAdWrapper(nativeAdLoader, maxNativeAdView, maxAd);
                    mNativeAdsQueue.add(wrapper);
                    countNativeNormal++;
                    Log.d(TAG_PRELOAD_NATIVE, "AppLovin Normal loaded. Queue size: " + mNativeAdsQueue.size());
                } else {
                    nativeAdLoader.destroy(maxAd);
                }
            }

            @Override
            public void onNativeAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError error) {
                isLoadingNativeNormal.set(false);
                Log.e(TAG_PRELOAD_NATIVE, "AppLovin Native NORMAL failed: " + error.getMessage());
                if (isPrimary) {
                    preloadNativeApplovin(context, false);
                }
            }
        });

        nativeAdLoader.loadAd(AppLovinNativeAdFactory.create((Activity) context, adsConfigNativeNormal));
    }

    //====================PRELOAD FULL==========================================//
    private void preloadNativeFull(@NonNull Context context, boolean isPrimary) {
        if (adsConfigNativeFull == null) {
            Log.w(TAG_PRELOAD_NATIVE_FULL, ">> GAGAL: adsConfigNativeFull NULL");
            isLoadingNativeFull.set(false);
            return;
        }

        Log.d(TAG_PRELOAD_NATIVE_FULL, ">> MULAI preload FULL | isPrimary: " + isPrimary + " | queueSize: " + mNativeFullScreenQueue.size());

        isLoadingNativeFull.set(true);
        if (mNativeFullScreenQueue.size() >= adsConfigNativeFull.getMaxCountAds() || countNativeFull >= adsConfigNativeFull.getMaxCountAds()) {
            Log.d(TAG_PRELOAD_NATIVE_FULL, ">> LEWATI preload: queue sudah penuh");
            countNativeFull = 0;
            return;
        }
        initAdsNetwork(context, adsConfigNativeFull, isPrimary);
        AZAdsRunUtils.runInBackground(() -> {
            String network = isPrimary ? adsConfigNativeFull.getPrimaryNetwork() : adsConfigNativeFull.getBackupNetwork();
            AZAdsType adsType = AZAdsType.fromNetworkName(network);

            if (adsType == null) {
                Log.e(TAG_PRELOAD_NATIVE_FULL, ">> GAGAL: Jaringan iklan tidak dikenal - " + network);
                isLoadingNativeFull.set(false);
                return;
            }

            Log.d(TAG_PRELOAD_NATIVE_FULL, ">> Deteksi jaringan: " + adsType.name());

            switch (adsType) {
                case ADMOB:
                    preloadNativeAdmobFull(context, isPrimary);
                    break;
                case FAN:
                    preloadNativeFanFull(context, isPrimary);
                    break;
                case APPLOVIN:
                case APPLOVIN_MAX:
                case FAN_BIDDING_APPLOVIN_MAX:
                    preloadNativeApplovinFull(context, isPrimary);
                    break;
            }
        });
    }

    private void preloadNativeAdmobFull(Context context, boolean isPrimary) {
        if (adsConfigNativeFull == null) {
            isLoadingNativeFull.set(false);
            return;
        }
        String adUnitId = isPrimary ? adsConfigNativeFull.getPrimaryUnitId() : adsConfigNativeFull.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingNativeFull.set(false);
            return;
        }

        Log.d(TAG_PRELOAD_NATIVE_FULL, "ADMOB | FULL");
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT)
                .build();
        AdLoader adLoader = new AdLoader.Builder(context, adUnitId)
                .forNativeAd(nativeAd -> {
                    if (mNativeFullScreenQueue.size() < adsConfigNativeFull.getMaxCountAds()) {
                        mNativeFullScreenQueue.add(nativeAd);
                        countNativeFull++;
                    } else {
                        nativeAd.destroy();
                    }
                    isLoadingNativeFull.set(false);
                    Log.d(TAG, "AdMob Full" + " loaded. Queue size: " + mNativeFullScreenQueue.size());
                })
                .withAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        isLoadingNativeFull.set(false);
                        Log.e(TAG_PRELOAD_NATIVE_FULL, String.format(
                                "ADMOB Native %s failed: %s", "NORMAL",
                                adError.getMessage()
                        ));
                        if (isPrimary) {
                            preloadNativeFull(context, false);
                        }
                    }
                })
                .withNativeAdOptions(adOptions)
                .build();

        AZAdsType adsType = AZAdsType.fromNetworkName(isPrimary ? adsConfigNativeFull.getPrimaryNetwork() : adsConfigNativeFull.getBackupNetwork());
        AdRequest adRequest = AZAdsUtils.getAdsRequest(context, adsType, adsConfigNativeFull);
        adLoader.loadAd(adRequest);
    }

    private void preloadNativeFanFull(Context context, boolean isPrimary) {
        if (adsConfigNativeFull == null) {
            isLoadingNativeFull.set(false);
            return;
        }
        String adUnitId = isPrimary ? adsConfigNativeFull.getPrimaryUnitId() : adsConfigNativeFull.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingNativeFull.set(false);
            return;
        }

        Log.d(TAG_PRELOAD_NATIVE_FULL, "FAN | FULL");

        com.facebook.ads.NativeAd fanNativeAd = new com.facebook.ads.NativeAd(context, adUnitId);

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // optional
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                isLoadingNativeFull.set(false);
                Log.e(TAG_PRELOAD_NATIVE_FULL, String.format(
                        "FAN Native %s failed: %s", "NORMAL",
                        adError.getErrorMessage()
                ));

                if (isPrimary) {
                    preloadNativeFull(context, false);
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (mNativeFullScreenQueue.size() < adsConfigNativeFull.getMaxCountAds()) {
                    mNativeFullScreenQueue.add(ad);
                    countNativeFull++;
                } else {
                    ad.destroy();
                }
                isLoadingNativeFull.set(false);
                Log.d(TAG, "FAN Full" + " loaded. Queue size: " + mNativeFullScreenQueue.size());
            }

            @Override
            public void onAdClicked(Ad ad) {
                // optional
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // optional
            }
        };

        com.facebook.ads.NativeAd.NativeLoadAdConfig loadAdConfig =
                fanNativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build();
        fanNativeAd.loadAd(loadAdConfig);
    }

    private void preloadNativeApplovinFull(Context context, boolean isPrimary) {
        if (adsConfigNativeFull == null) {
            isLoadingNativeFull.set(false);
            return;
        }

        String adUnitId = isPrimary ? adsConfigNativeFull.getPrimaryUnitId() : adsConfigNativeFull.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingNativeFull.set(false);
            return;
        }

        Log.d(TAG_PRELOAD_NATIVE_FULL, "APPLOVIN_MAX | FULL");

        MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(adUnitId);
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(@Nullable MaxNativeAdView maxNativeAdView, @NonNull MaxAd maxAd) {
                isLoadingNativeFull.set(false);
                if (mNativeFullScreenQueue.size() < adsConfigNativeFull.getMaxCountAds()) {
                    ApplovinNativeAdWrapperFull wrapper = new ApplovinNativeAdWrapperFull(nativeAdLoader, maxNativeAdView, maxAd);
                    mNativeFullScreenQueue.add(wrapper);
                    countNativeFull++;
                    Log.d(TAG_PRELOAD_NATIVE_FULL, "AppLovin Full loaded. Queue size: " + mNativeFullScreenQueue.size());
                } else {
                    nativeAdLoader.destroy(maxAd);
                }
            }

            @Override
            public void onNativeAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError error) {
                isLoadingNativeFull.set(false);
                Log.e(TAG_PRELOAD_NATIVE_FULL, "AppLovin Native FULL failed: " + error.getMessage());
                if (isPrimary) {
                    preloadNativeApplovinFull(context, false);
                }
            }
        });

        MaxNativeAdView dummyView = AppLovinNativeAdFactory.create((Activity) context, adsConfigNativeFull);
        nativeAdLoader.loadAd(dummyView);
    }

    //====================SHOW NORMAL==========================================//
    private void showNativeAdNormal(@NonNull Context context, @NonNull NadNativeView nativeView, boolean isPrimary, @Nullable NativeAdLoadCallbackWrapper callbackWrapper) {
        Log.d(TAG_SHOW_NATIVE, ">> TRY SHOW | NORMAL | isPrimary: " + isPrimary + " | queueSize: " + mNativeAdsQueue.size());

        if (adsConfigNativeNormal == null) {
            Log.w(TAG_SHOW_NATIVE, ">> GAGAL: Konfigurasi adsConfigNativeNormal NULL");
            failedNative(callbackWrapper, nativeView);
            return;
        }

        AZAdsConfig config = adsConfigNativeNormal;
        prepareNativeView(nativeView, config.getNativeStyle());

        initAdsNetwork(context, config, isPrimary);
        Object pooledAd = mNativeAdsQueue.poll();
        if (pooledAd == null && config.isPreload()) {
            Log.w(TAG_SHOW_NATIVE, ">> Queue kosong, fallback ke loadNativeNormal()");
            preloadNativeNormal(context, isPrimary);  // refill lagi
            return;
        }

        if (pooledAd instanceof NativeAd) {
            Log.d(TAG_SHOW_NATIVE, ">> AdMob ad ditemukan di queue, akan di-bind");

            AZAdsRunUtils.runWithDelay(new Runnable() {
                @Override
                public void run() {
                    AdmobAdUtils.bindNativeAd(nativeView, (NativeAd) pooledAd, config);
                }
            }, 500);
            return;
        }

        if (pooledAd instanceof ApplovinNativeAdWrapper) {
            Log.d(TAG_SHOW_NATIVE, ">> AppLovin ad ditemukan di queue, akan di-bind");
            ApplovinNativeAdWrapper wrapper = (ApplovinNativeAdWrapper) pooledAd;
            if (wrapper.getAdView() == null) {
                Log.e(TAG_SHOW_NATIVE, ">> AppLovin adView NULL, gagal menampilkan");
                failedNative(callbackWrapper, nativeView);
                return;
            }

            AZAdsRunUtils.runWithDelay(new Runnable() {
                @Override
                public void run() {
                    nativeView.setApplovinAdLoader(wrapper.getAdLoader());
                    ApplovinAdUtils.bindNativeAd(nativeView, wrapper);
                    ApplovinAdUtils.applyTheme(toActivity(context), wrapper.getAdView(), adsConfigNativeNormal);
                }
            }, 500);

            return;
        }

        Log.w(TAG_SHOW_NATIVE, ">> Tidak ada ad valid dalam queue, loadNativeNormal() dipanggil manual");

        loadNativeNormal(context, nativeView, isPrimary, callbackWrapper);
    }

    //====================LOAD NATIVE NORMAL==========================================//
    private void loadNativeNormal(@NonNull Context context, NadNativeView nativeView, boolean isPrimary, @Nullable NativeAdLoadCallbackWrapper callbackWrapper) {
        AZAdsConfig config = adsConfigNativeNormal;
        if (config == null) {
            Log.w(TAG_LOAD_NATIVE, ">> GAGAL: Config native normal NULL");
            failedNative(callbackWrapper, nativeView);
            return;
        }

        Log.d(TAG_LOAD_NATIVE, ">> MULAI LOAD Native NORMAL | isPrimary: " + isPrimary);

        initAdsNetwork(context, config, isPrimary);
        String network = isPrimary ? config.getPrimaryNetwork() : config.getBackupNetwork();
        AZAdsType adsType = AZAdsType.fromNetworkName(network);

        if (adsType == null) {
            Log.e(TAG_LOAD_NATIVE, ">> GAGAL: Unknown ads type for network: " + network);
            failedNative(callbackWrapper, nativeView);
            return;
        }

        Log.d(TAG_LOAD_NATIVE, ">> Jaringan Detected: " + adsType.name());

        switch (adsType) {
            case ADMOB:
                loadNativeAdmob(context, nativeView, isPrimary, callbackWrapper);
                break;
            case FAN:
                loadNativeFan(context, nativeView, isPrimary, callbackWrapper);
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case FAN_BIDDING_APPLOVIN_MAX:
                loadNativeApplovin(context, nativeView, isPrimary, callbackWrapper);
                break;
        }
    }

    private void loadNativeAdmob(@NonNull Context context, @NonNull NadNativeView nativeView, boolean isPrimary, @Nullable NativeAdLoadCallbackWrapper callbackWrapper) {
        AZAdsConfig config = adsConfigNativeNormal;
        nativeView.getAdmobNativeView().setVisibility(View.GONE);
        FrameLayout rootContainer = nativeView.getContainerRoot();
        if (rootContainer == null || config == null) {
            failedNative(callbackWrapper, nativeView);
            return;
        }
        String adUnitId = isPrimary ? config.getPrimaryUnitId() : config.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingNativeNormal.set(false);
            failedNative(callbackWrapper, nativeView);
            return;
        }
        Log.d(TAG_LOAD_NATIVE, "ADMOB | NORMAL");
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                .build();
        AdLoader adLoader = new AdLoader.Builder(context, adUnitId)
                .forNativeAd(nativeAd -> {
                    Context viewContext = nativeView.getContext();
                    if (viewContext instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) viewContext;
                        if (activity.isFinishing() || activity.isDestroyed()) {
                            nativeAd.destroy();
                            return;
                        }
                    }

                    nativeView.getAdmobNativeView().setNativeAd(nativeAd, config);
                    rootContainer.setVisibility(View.VISIBLE);
                    successNative(callbackWrapper, nativeView);
                    Log.d(TAG_LOAD_NATIVE, "Admob Normal berhasil load");
                })
                .withAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        isLoadingNativeNormal.set(false);
                        Log.e(TAG_LOAD_NATIVE, "AdMob Native failed: " + adError.getMessage());
                        if (isPrimary) {
                            if (!mNativeAdsQueue.isEmpty()) {
                                showNativeAdNormal(context, nativeView, true, callbackWrapper);
                            } else {
                                loadNativeNormal(context, nativeView, false, callbackWrapper);
                            }
                        } else {
                            failedNative(callbackWrapper, nativeView);
                        }
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        isLoadingNativeNormal.set(false);
                        nativeView.getAdmobNativeView().setVisibility(View.VISIBLE);
                    }
                })
                .withNativeAdOptions(adOptions)
                .build();

        AZAdsType adsType = AZAdsType.fromNetworkName(isPrimary ? config.getPrimaryNetwork() : config.getBackupNetwork());
        AdRequest adRequest = AZAdsUtils.getAdsRequest(context, adsType, config);
        if (isLoadingNativeNormal.get()) {
            AZAdsRunUtils.runWithDelay(new Runnable() {
                @Override
                public void run() {
                    isLoadingNativeNormal.set(true);
                    adLoader.loadAd(adRequest);
                }
            }, 1000);
        } else {
            isLoadingNativeNormal.set(true);
            adLoader.loadAd(adRequest);
        }
    }

    private void loadNativeFan(@NonNull Context context, @NonNull NadNativeView nativeView, boolean isPrimary, @Nullable NativeAdLoadCallbackWrapper callbackWrapper) {
        failedNative(callbackWrapper, nativeView);
    }

    private void loadNativeApplovin(@NonNull Context context, @NonNull NadNativeView nativeView, boolean isPrimary, @Nullable NativeAdLoadCallbackWrapper callbackWrapper) {
        Activity activity = toActivity(context);
        if (activity == null) {
            failedNative(callbackWrapper, nativeView);
            return;
        }
        FrameLayout rootContainer = nativeView.getContainerRoot();
        if (adsConfigNativeNormal == null || rootContainer == null) {
            failedNative(callbackWrapper, nativeView);
            return;
        }

        String adUnitId = isPrimary ? adsConfigNativeNormal.getPrimaryUnitId() : adsConfigNativeNormal.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingNativeNormal.set(false);
            failedNative(callbackWrapper, nativeView);
            return;
        }

        Log.d(TAG_LOAD_NATIVE, "APPLOVIN MAX | NORMAL");
        MaxNativeAdLoader adLoader = new MaxNativeAdLoader(adUnitId);
        nativeView.setApplovinAdLoader(adLoader);
        adLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(@Nullable MaxNativeAdView adView, @NonNull MaxAd maxAd) {
                isLoadingNativeNormal.set(false);
                if (nativeView.getLoadedApplovinAd() != null && nativeView.getApplovinAdLoader() != null) {
                    nativeView.getApplovinAdLoader().destroy(nativeView.getLoadedApplovinAd());
                }
                nativeView.setLoadedApplovinAd(maxAd);
                nativeView.setLoadedApplovinAd(nativeView.getLoadedApplovinAd());
                nativeView.getApplovinNativeContainer().removeAllViews();
                nativeView.getApplovinNativeContainer().addView(adView);
                nativeView.getApplovinNativeContainer().setVisibility(View.VISIBLE);
                rootContainer.setVisibility(View.VISIBLE);
                successNative(callbackWrapper, nativeView);
                Log.d(TAG_LOAD_NATIVE, "AppLovin MAX NORMAL loaded");
            }

            @Override
            public void onNativeAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError error) {
                isLoadingNativeNormal.set(false);
                Log.e(TAG_LOAD_NATIVE, "AppLovin Max Native failed: " + error.getMessage());
                if (isPrimary) {
                    if (!mNativeAdsQueue.isEmpty()) {
                        showNativeAdNormal(context, nativeView, true, callbackWrapper);
                    } else {
                        loadNativeNormal(context, nativeView, false, callbackWrapper);
                    }
                } else {
                    failedNative(callbackWrapper, nativeView);
                }
            }
        });

        MaxNativeAdView maxNativeAdView = AppLovinNativeAdFactory.create(activity, adsConfigNativeNormal);
        if (isLoadingNativeNormal.get()) {
            AZAdsRunUtils.runWithDelay(new Runnable() {
                @Override
                public void run() {
                    isLoadingNativeNormal.set(true);
                    if (maxNativeAdView != null) {
                        ApplovinAdUtils.applyTheme(activity, maxNativeAdView, adsConfigNativeNormal);
                    }
                    adLoader.loadAd(maxNativeAdView);
                }
            }, 1000);
        } else {
            isLoadingNativeNormal.set(true);
            if (maxNativeAdView != null) {
                ApplovinAdUtils.applyTheme(activity, maxNativeAdView, adsConfigNativeNormal);
            }
            adLoader.loadAd(maxNativeAdView);
        }


    }

    //====================SHOW FULL==========================================//
    private void showNativeAdFull(@NonNull Context context, @NonNull NadNativeView nativeView, boolean isPrimary, @Nullable NativeAdLoadCallbackWrapper callbackWrapper) {
        Log.d(TAG_SHOW_NATIVE_FULL, ">> TRY SHOW | FULLSCREEN | isPrimary: " + isPrimary + " | queueSize: " + mNativeFullScreenQueue.size());

        if (adsConfigNativeFull == null) {
            Log.w(TAG_SHOW_NATIVE_FULL, ">> GAGAL: Konfigurasi adsConfigNativeFull NULL");
            failedNative(callbackWrapper, nativeView);
            return;
        }

        AZAdsConfig config = adsConfigNativeFull;
        prepareNativeView(nativeView, config.getNativeStyle());
        initAdsNetwork(context, config, isPrimary);
        Object pooledAd = mNativeFullScreenQueue.poll();
        if (pooledAd == null && config.isPreload()) {
            Log.w(TAG_SHOW_NATIVE_FULL, ">> Queue kosong, fallback ke loadNativeFull()");
            preloadNativeFull(context, isPrimary);
            return;
        }

        if (pooledAd instanceof NativeAd) {
            Log.d(TAG_SHOW_NATIVE_FULL, ">> AdMob ad ditemukan di queue, akan di-bind");
            AdmobAdUtils.bindWithDiff(nativeView, (NativeAd) pooledAd, config, () -> successNative(callbackWrapper, nativeView));
            return;
        }

        if (pooledAd instanceof com.facebook.ads.NativeAd) {
            Log.w(TAG_SHOW_NATIVE_FULL, ">> FAN ad belum diimplementasikan, skip");
            return;
        }

        if (pooledAd instanceof ApplovinNativeAdWrapperFull) {
            Log.d(TAG_SHOW_NATIVE_FULL, ">> AppLovin ad ditemukan di queue, akan di-bind");
            ApplovinNativeAdWrapperFull wrapper = (ApplovinNativeAdWrapperFull) pooledAd;
            if (wrapper.getAdView() == null) {
                Log.e(TAG_SHOW_NATIVE, ">> AppLovin adView NULL, gagal menampilkan");
                failedNative(callbackWrapper, nativeView);
                return;
            }
            nativeView.setApplovinAdLoader(wrapper.getAdLoader());
            ApplovinAdUtils.bindWithDiff(nativeView, wrapper, () -> successNative(callbackWrapper, nativeView));
            ApplovinAdUtils.applyTheme(toActivity(context), wrapper.getAdView(), adsConfigNativeFull);
            return;
        }

        Log.w(TAG_SHOW_NATIVE_FULL, ">> Tidak ada ad valid dalam queue, loadNativeFull() dipanggil manual");
        loadNativeFull(context, nativeView, isPrimary, callbackWrapper);
    }

    //====================LOAD NATIVE NORMAL==========================================//
    private void loadNativeFull(@NonNull Context context, NadNativeView nativeView, boolean isPrimary, @Nullable NativeAdLoadCallbackWrapper callbackWrapper) {
        AZAdsConfig config = adsConfigNativeFull;
        if (config == null) {
            Log.w(TAG_LOAD_NATIVE_FULL, ">> GAGAL: Config native full NULL");
            failedNative(callbackWrapper, nativeView);
            return;
        }

        Log.d(TAG_LOAD_NATIVE_FULL, ">> MULAI LOAD Native FULL | isPrimary: " + isPrimary);

        initAdsNetwork(context, config, isPrimary);
        String network = isPrimary ? config.getPrimaryNetwork() : config.getBackupNetwork();
        AZAdsType adsType = AZAdsType.fromNetworkName(network);

        if (adsType == null) {
            Log.e(TAG_LOAD_NATIVE_FULL, ">> GAGAL: Unknown ads type for network: " + network);
            failedNative(callbackWrapper, nativeView);
            return;
        }

        Log.d(TAG_LOAD_NATIVE_FULL, ">> Jaringan Detected: " + adsType.name());

        switch (adsType) {
            case ADMOB:
                loadNativeAdmobFull(context, nativeView, isPrimary, callbackWrapper);
                break;
            case FAN:
                loadNativeFanFull(context, nativeView, isPrimary, callbackWrapper);
                break;
            case APPLOVIN:
            case APPLOVIN_MAX:
            case FAN_BIDDING_APPLOVIN_MAX:
                loadNativeApplovinFull(context, nativeView, isPrimary, callbackWrapper);
                break;
        }
    }

    private void loadNativeAdmobFull(@NonNull Context context, @NonNull NadNativeView nativeView, boolean isPrimary, @Nullable NativeAdLoadCallbackWrapper callbackWrapper) {
        AZAdsConfig config = adsConfigNativeFull;
        nativeView.getAdmobNativeView().setVisibility(View.GONE);
        FrameLayout rootContainer = nativeView.getContainerRoot();
        if (rootContainer == null || config == null) {
            failedNative(callbackWrapper, nativeView);
            return;
        }
        String adUnitId = isPrimary ? config.getPrimaryUnitId() : config.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingNativeFull.set(false);
            failedNative(callbackWrapper, nativeView);
            return;
        }

        Log.d(TAG_LOAD_NATIVE_FULL, "ADMOB | FULL");
        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_PORTRAIT)
                .build();
        AdLoader adLoader = new AdLoader.Builder(context, adUnitId)
                .forNativeAd(nativeAd -> {
                    isLoadingNativeFull.set(false);
                    Context viewContext = nativeView.getContext();
                    if (viewContext instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) viewContext;
                        if (activity.isFinishing() || activity.isDestroyed()) {
                            nativeAd.destroy();
                            return;
                        }
                    }

                    nativeView.getAdmobNativeView().setNativeAd(nativeAd, config);
                    rootContainer.setVisibility(View.VISIBLE);
                    successNative(callbackWrapper, nativeView);
                    Log.d(TAG_LOAD_NATIVE_FULL, "Admob FULL berhasil load");

                })
                .withAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        isLoadingNativeFull.set(false);
                        Log.e(TAG_LOAD_NATIVE_FULL, "AdMob Native failed: " + adError.getMessage());
                        if (isPrimary) {
                            if (!mNativeFullScreenQueue.isEmpty()) {
                                showNativeAdFull(context, nativeView, true, callbackWrapper);
                            } else {
                                loadNativeFull(context, nativeView, false, callbackWrapper);
                            }
                        } else {
                            failedNative(callbackWrapper, nativeView);
                        }
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        isLoadingNativeFull.set(false);
                        nativeView.getAdmobNativeView().setVisibility(View.VISIBLE);
                    }
                })
                .withNativeAdOptions(adOptions)
                .build();

        AZAdsType adsType = AZAdsType.fromNetworkName(isPrimary ? config.getPrimaryNetwork() : config.getBackupNetwork());
        AdRequest adRequest = AZAdsUtils.getAdsRequest(context, adsType, config);
        if (isLoadingNativeFull.get()) {
            AZAdsRunUtils.runWithDelay(new Runnable() {
                @Override
                public void run() {
                    isLoadingNativeFull.set(true);
                    adLoader.loadAd(adRequest);
                }
            }, HANDLER_DELAY_MS);
        } else {
            isLoadingNativeFull.set(true);
            adLoader.loadAd(adRequest);
        }
    }

    private void loadNativeFanFull(@NonNull Context context, @NonNull NadNativeView nativeView, boolean isPrimary, @Nullable NativeAdLoadCallbackWrapper callbackWrapper) {
        failedNative(callbackWrapper, nativeView);
    }

    private void loadNativeApplovinFull(@NonNull Context context, @NonNull NadNativeView nativeView, boolean isPrimary, @Nullable NativeAdLoadCallbackWrapper callbackWrapper) {
        Activity activity = toActivity(context);
        if (activity == null) {
            failedNative(callbackWrapper, nativeView);
            return;
        }
        FrameLayout rootContainer = nativeView.getContainerRoot();
        AZAdsConfig config = adsConfigNativeFull;
        if (config == null || rootContainer == null) {
            failedNative(callbackWrapper, nativeView);
            return;
        }
        String adUnitId = isPrimary ? config.getPrimaryUnitId() : config.getBackupUnitId();
        if (adUnitId.isEmpty()) {
            isLoadingNativeFull.set(false);
            failedNative(callbackWrapper, nativeView);
            return;
        }

        Log.d(TAG_LOAD_NATIVE_FULL, "APPLOVIN MAX | FULL");
        MaxNativeAdLoader adLoader = new MaxNativeAdLoader(adUnitId);
        nativeView.setApplovinAdLoader(adLoader);
        adLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(@Nullable MaxNativeAdView adView, @NonNull MaxAd maxAd) {
                isLoadingNativeFull.set(false);
                if (nativeView.getLoadedApplovinAd() != null && nativeView.getApplovinAdLoader() != null) {
                    nativeView.getApplovinAdLoader().destroy(nativeView.getLoadedApplovinAd());
                }
                nativeView.setLoadedApplovinAd(maxAd);
                nativeView.setLoadedApplovinAd(nativeView.getLoadedApplovinAd());
                nativeView.getApplovinNativeContainer().removeAllViews();
                nativeView.getApplovinNativeContainer().addView(adView);
                nativeView.getApplovinNativeContainer().setVisibility(View.VISIBLE);
                rootContainer.setVisibility(View.VISIBLE);
                successNative(callbackWrapper, nativeView);
                Log.d(TAG_LOAD_NATIVE_FULL, "AppLovin MAX FULL loaded");
            }


            @Override
            public void onNativeAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError error) {
                isLoadingNativeFull.set(false);
                Log.e(TAG_LOAD_NATIVE_FULL, "AppLovin Max Native failed: " + error.getMessage());
                if (isPrimary) {
                    if (!mNativeFullScreenQueue.isEmpty()) {
                        showNativeAdFull(context, nativeView, true, callbackWrapper);
                    } else {
                        loadNativeFull(context, nativeView, false, callbackWrapper);
                    }
                } else {
                    failedNative(callbackWrapper, nativeView);
                }
            }
        });

        MaxNativeAdView maxNativeAdView = AppLovinNativeAdFactory.create(activity, config);


        if (isLoadingNativeFull.get()) {
            AZAdsRunUtils.runWithDelay(new Runnable() {
                @Override
                public void run() {
                    isLoadingNativeFull.set(true);
                    if (maxNativeAdView != null) {
                        ApplovinAdUtils.applyTheme(activity, maxNativeAdView, config);
                    }
                    adLoader.loadAd(maxNativeAdView);
                }
            }, HANDLER_DELAY_MS);
        } else {
            isLoadingNativeFull.set(true);
            if (maxNativeAdView != null) {
                ApplovinAdUtils.applyTheme(activity, maxNativeAdView, config);
            }
            adLoader.loadAd(maxNativeAdView);
        }

    }
}
