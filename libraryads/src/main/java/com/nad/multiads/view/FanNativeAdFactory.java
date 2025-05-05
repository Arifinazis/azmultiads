package com.nad.multiads.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.nad.multiads.R;
import com.nad.multiads.utils.AZAdsConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class to inflate and bind Facebook FAN Native Ads.
 */
public class FanNativeAdFactory {

    public static void inflateAndBindNativeAd(Activity activity, NativeAd fanNativeAd, NativeAdLayout fanNativeAdLayout, AZAdsConfig adsConfig) {
        if (activity == null || fanNativeAd == null || fanNativeAdLayout == null) {
            return;
        }

        fanNativeAd.unregisterView();

        LayoutInflater inflater = LayoutInflater.from(activity);
        LinearLayout nativeAdView;
        switch (adsConfig.getNativeStyle()) {
            case SMALL:
                nativeAdView = (LinearLayout) inflater.inflate(R.layout.fan_native_small, fanNativeAdLayout, false);
                break;
            case LARGE:
                nativeAdView = (LinearLayout) inflater.inflate(R.layout.fan_native_large, fanNativeAdLayout, false);
                break;
            case FULL:
                nativeAdView = (LinearLayout) inflater.inflate(R.layout.fan_native_full, fanNativeAdLayout, false);
                break;
            case MEDIUM:
            default:
                nativeAdView = (LinearLayout) inflater.inflate(R.layout.fan_native_medium, fanNativeAdLayout, false);
                break;
        }

        fanNativeAdLayout.removeAllViews();
        fanNativeAdLayout.addView(nativeAdView);

        LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(activity, fanNativeAd, fanNativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
        MediaView nativeAdIcon = nativeAdView.findViewById(R.id.native_ad_icon);
        MediaView nativeAdMedia = nativeAdView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = nativeAdView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = nativeAdView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = nativeAdView.findViewById(R.id.native_ad_call_to_action);
        ConstraintLayout fanNativeBackground = nativeAdView.findViewById(R.id.background);
        int textColorPrimary = adsConfig.isDark() ? adsConfig.getColorDark(activity) : adsConfig.getColorLight(activity);
        nativeAdTitle.setTextColor(textColorPrimary);
        nativeAdSocialContext.setTextColor(textColorPrimary);
        sponsoredLabel.setTextColor(textColorPrimary);
        nativeAdBody.setTextColor(textColorPrimary);


        nativeAdTitle.setText(fanNativeAd.getAdvertiserName());
        nativeAdBody.setText(fanNativeAd.getAdBodyText());
        nativeAdSocialContext.setText(fanNativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(fanNativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(fanNativeAd.getAdCallToAction());
        sponsoredLabel.setText(fanNativeAd.getSponsoredTranslation());

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(sponsoredLabel);
        clickableViews.add(nativeAdIcon);
        clickableViews.add(nativeAdMedia);
        clickableViews.add(nativeAdBody);
        clickableViews.add(nativeAdSocialContext);
        clickableViews.add(nativeAdCallToAction);

        fanNativeAd.registerViewForInteraction(nativeAdView, nativeAdIcon, nativeAdMedia, clickableViews);
    }

}