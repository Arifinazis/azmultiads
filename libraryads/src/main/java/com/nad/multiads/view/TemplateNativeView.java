package com.nad.multiads.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.nativead.AdChoicesView;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.nad.multiads.R;
import com.nad.multiads.utils.AZAdsConfig;


public class TemplateNativeView extends FrameLayout {

    private NativeAd nativeAd;
    private NativeAdView nativeAdView;
    private ConstraintLayout background;
    private TextView primaryView;
    private TextView tertiaryView;
    private Button callToActionView;
    private ImageView iconView;
    private MediaView mediaView;
    private AdChoicesView adChoicesView;
    private int mTemplateType = R.layout.gnt_medium_template;

    public TemplateNativeView(Context context) {
        super(context);
        initView(context, null);
    }

    public TemplateNativeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TemplateNativeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attributeSet) {
        try (TypedArray ta = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0)) {
            this.mTemplateType = ta.getResourceId(R.styleable.TemplateView_gnt_template_type, R.layout.gnt_medium_template);
            LayoutInflater.from(context).inflate(this.mTemplateType, this);
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        nativeAdView = findViewById(R.id.native_ad_view);
        primaryView = findViewById(R.id.primary);
        tertiaryView = findViewById(R.id.body);
        callToActionView = findViewById(R.id.cta);
        iconView = findViewById(R.id.icon);
        mediaView = findViewById(R.id.media_view);
        background = findViewById(R.id.background);
        adChoicesView = findViewById(R.id.ad_choice_view);
    }


    public void destroyNativeAd() {
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
        }
    }

    public NativeAd getNativeAd() {
        return nativeAd;
    }
    public void setNativeAd(NativeAd nativeAd, @NonNull AZAdsConfig config) {
        this.nativeAd = nativeAd;
        if (nativeAd == null) {
            return;
        }
        String headline = truncateString(nativeAd.getHeadline(), 26);
        String body = truncateString(nativeAd.getBody(), 91);
        String callToAction = nativeAd.getCallToAction();
        NativeAd.Image icon = nativeAd.getIcon();
        this.nativeAdView.setCallToActionView(this.callToActionView);
        this.nativeAdView.setAdChoicesView(this.adChoicesView);
        this.nativeAdView.setHeadlineView(this.primaryView);
        this.nativeAdView.setMediaView(this.mediaView);
        this.primaryView.setText(headline);
        this.callToActionView.setText(callToAction);
        if (icon != null) {
            this.iconView.setVisibility(View.VISIBLE);
            this.iconView.setImageDrawable(icon.getDrawable());
        } else {
            this.iconView.setVisibility(View.GONE);
        }
        TextView textView = this.tertiaryView;
        if (textView != null) {
            textView.setText(body);
            this.nativeAdView.setBodyView(this.tertiaryView);
        }
        this.nativeAdView.setNativeAd(nativeAd);
        setColorsAndBackground(config);
    }

    private String truncateString(@Nullable String text, int maxLength) {
        if (TextUtils.isEmpty(text)) return "";
        if (text.length() <= maxLength) return text;
        String truncated = text.substring(0, maxLength);
        int lastSpace = truncated.lastIndexOf(' ');
        if (lastSpace > 0) {
            truncated = truncated.substring(0, lastSpace);
        }
        return truncated + "...";
    }

    private void setColorsAndBackground(@Nullable AZAdsConfig adsConfig) {
        if (adsConfig == null) {
            Log.w("TemplateNativeView", "MultiAdsConfig is null, skipping theme application");
            return;
        }
        int primaryColor = adsConfig.isDark()
                ? (adsConfig.getColorDark(getContext()) != 0 ? adsConfig.getColorLight(getContext()) : ContextCompat.getColor(getContext(), R.color.gnt_default_light))
                : (adsConfig.getColorLight(getContext()) != 0 ? adsConfig.getColorLight(getContext()) : ContextCompat.getColor(getContext(), R.color.gnt_default_dark));

        primaryView.setTextColor(primaryColor);
        tertiaryView.setTextColor(primaryColor);
        callToActionView.setTextColor(primaryColor);

        if (adsConfig.isDark()) {
            background.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gnt_default_dark));
        }
    }
}