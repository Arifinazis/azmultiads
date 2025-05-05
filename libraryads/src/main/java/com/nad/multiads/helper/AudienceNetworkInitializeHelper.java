package com.nad.multiads.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.AudienceNetworkAds.InitListener;
import com.facebook.ads.AudienceNetworkAds.InitResult;

public class AudienceNetworkInitializeHelper implements InitListener {

    @SuppressLint("HardwareIds")
    public static void initializeAd(Context context, boolean debug) {
        if (!AudienceNetworkAds.isInitialized(context)) {
            if (debug) {
                AdSettings.turnOnSDKDebugger(context);
                AdSettings.addTestDevice("ab5b1cc2-07a0-42cc-a584-c6a79c7eef88");
                AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE);
            }
            AudienceNetworkAds
                    .buildInitSettings(context)
                    .withInitListener(new AudienceNetworkInitializeHelper())
                    .initialize();
        }
    }

    @Override
    public void onInitialized(InitResult result) {
        Log.d(AudienceNetworkAds.TAG, result.getMessage());
    }
}