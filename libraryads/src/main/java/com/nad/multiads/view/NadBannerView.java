package com.nad.multiads.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.ads.MaxAdView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.nad.multiads.R;

public class NadBannerView extends FrameLayout {
    private ShimmerFrameLayout shimmerView;
    private FrameLayout adContainer;
    public NadBannerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public NadBannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NadBannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context) {
        inflate(context, R.layout.view_nad_banner, this);
        shimmerView = findViewById(R.id.shimmer_view);
        adContainer = findViewById(R.id.ad_container);
    }



    public void hideAd() {
        hideLoading();
        adContainer.removeAllViews();
        adContainer.setVisibility(View.GONE);
        setVisibility(View.GONE);
    }


    public void showLoading() {
        if (shimmerView != null && adContainer != null) {
            adContainer.removeAllViews();
            adContainer.setVisibility(View.GONE);
            shimmerView.setVisibility(View.VISIBLE);
            shimmerView.startShimmer();
        }
    }

    public void hideLoading() {
        if (shimmerView != null) {
            shimmerView.stopShimmer();
            shimmerView.setVisibility(View.GONE);
        }
    }

    public FrameLayout getAdContainer() {
        return adContainer;
    }
}
