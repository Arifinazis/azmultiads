package com.nad.multiads.view;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder;
import com.nad.multiads.R;
import com.nad.multiads.utils.AZAdsConfig;
import com.nad.multiads.utils.AZNativeAdsStyle;


/**
 * Factory class to create AppLovin MaxNativeAdView easily.
 */
public class AppLovinNativeAdFactory {

    private static final String TAG = "AppLovinNativeAdFactory";

    public static MaxNativeAdView create(Activity activity, AZAdsConfig adsConfig) {
        if (activity == null) {
            Log.e(TAG, "Activity is null! Cannot create MaxNativeAdView.");
            return null;
        }

        AZNativeAdsStyle nativeStyle = adsConfig.getNativeStyle();
        MaxNativeAdViewBinder binder;
        switch (nativeStyle) {
            case SMALL:
                binder = new MaxNativeAdViewBinder.Builder(R.layout.applovin_native_small)
                        .setTitleTextViewId(R.id.title_text_view)
                        .setBodyTextViewId(R.id.body_text_view)
                        .setAdvertiserTextViewId(R.id.advertiser_textView)
                        .setOptionsContentViewGroupId(R.id.ad_options_view)
                        .setCallToActionButtonId(R.id.cta_button)
                        .build();
                break;
            case LARGE:
                binder = new MaxNativeAdViewBinder.Builder(R.layout.applovin_native_large)
                        .setTitleTextViewId(R.id.title_text_view)
                        .setBodyTextViewId(R.id.body_text_view)
                        .setAdvertiserTextViewId(R.id.advertiser_textView)
                        .setIconImageViewId(R.id.icon_image_view)
                        .setMediaContentViewGroupId(R.id.media_view_container)
                        .setOptionsContentViewGroupId(R.id.ad_options_view)
                        .setCallToActionButtonId(R.id.cta_button)
                        .build();
                break;
            case FULL:
                binder = new MaxNativeAdViewBinder.Builder(R.layout.applovin_native_full)
                        .setTitleTextViewId(R.id.title_text_view)
                        .setBodyTextViewId(R.id.body_text_view)
                        .setAdvertiserTextViewId(R.id.advertiser_textView)
                        .setIconImageViewId(R.id.icon_image_view)
                        .setMediaContentViewGroupId(R.id.media_view_container)
                        .setOptionsContentViewGroupId(R.id.ad_options_view)
                        .setCallToActionButtonId(R.id.cta_button)
                        .build();
                break;
            case MEDIUM:
            default:
                binder = new MaxNativeAdViewBinder.Builder(R.layout.applovin_native_medium)
                        .setTitleTextViewId(R.id.title_text_view)
                        .setBodyTextViewId(R.id.body_text_view)
                        .setAdvertiserTextViewId(R.id.advertiser_textView)
                        .setIconImageViewId(R.id.icon_image_view)
                        .setMediaContentViewGroupId(R.id.media_view_container)
                        .setOptionsContentViewGroupId(R.id.ad_options_view)
                        .setCallToActionButtonId(R.id.cta_button)
                        .build();
                break;
        }
        return new MaxNativeAdView(binder, activity);
    }


}