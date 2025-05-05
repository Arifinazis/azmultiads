package com.nad.multiads.helper;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.nativead.NativeAd;
import com.nad.multiads.utils.AZAdsConfig;
import com.nad.multiads.view.NadNativeView;
import com.nad.multiads.view.TemplateNativeView;

import java.util.Objects;

import kotlin.jvm.internal.Intrinsics;

public class AdmobAdUtils {
    public static void bindWithDiff(@NonNull NadNativeView nativeView,
                                    @Nullable NativeAd newAd,
                                    @NonNull AZAdsConfig config,
                                    @Nullable Runnable onSuccess) {

        TemplateNativeView templateView = nativeView.getAdmobNativeView();
        if (templateView == null || newAd == null) return;

        NativeAd currentAd = templateView.getNativeAd();

        if (currentAd != null) {
            if (!newAd.equals(currentAd)) {
                currentAd.destroy(); // Clean up old ad
                templateView.setNativeAd(newAd, config);
                FrameLayout container = nativeView.getContainerRoot();
                if (container != null) {
                    container.removeAllViews();
                    container.addView(templateView);
                }
                if (onSuccess != null) onSuccess.run();
            }
        } else {
            templateView.setNativeAd(newAd, config);
            FrameLayout container = nativeView.getContainerRoot();
            if (container != null) {
                container.removeAllViews();
                container.addView(templateView);
            }
            if (onSuccess != null) onSuccess.run();
        }

        templateView.setVisibility(View.VISIBLE);
        nativeView.setVisibility(View.VISIBLE);
    }
    public static void bindNativeAd(@NonNull NadNativeView nativeView,
                                    @NonNull NativeAd newAd,
                                    @NonNull AZAdsConfig config) {

        TemplateNativeView templateView = nativeView.getAdmobNativeView();
        if (templateView == null) return;

        NativeAd currentAd = templateView.getNativeAd();
        boolean isDifferent = !newAd.equals(currentAd);

        if (isDifferent) {
            if (currentAd != null) {
                currentAd.destroy(); // Clean up old ad
            }

            templateView.setNativeAd(newAd, config);

            FrameLayout container = nativeView.getContainerRoot();
            if (container != null) {
                container.removeAllViews();
                container.addView(templateView);
                container.setVisibility(View.VISIBLE);
            }

            templateView.setVisibility(View.VISIBLE);
            nativeView.setVisibility(View.VISIBLE);
        }
    }

}
