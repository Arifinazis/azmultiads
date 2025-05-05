package com.nad.multiads.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nad.multiads.R;

public class AdsFrameLayout extends FrameLayout {
    private float cornerRadius = 0f;
    private float topLeftRadius;
    private float topRightRadius;
    private float bottomLeftRadius;
    private float bottomRightRadius;
    private float elevationDp = 0f;

    private final Path clipPath = new Path();
    private boolean pathInvalid = true;
    private final RectF rectF = new RectF();

    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float strokeWidth = 0f;
    private int strokeColor = Color.TRANSPARENT;

    private boolean shouldClipChildren = true; // <-- tambahan baru

    public AdsFrameLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public AdsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AdsFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        strokePaint.setStyle(Paint.Style.STROKE);

        if (attrs != null) {
            try (TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NadAds, 0, 0)) {
                setWillNotDraw(a.getBoolean(R.styleable.NadAds_adsWillNotDraw, false));

                cornerRadius = a.getDimension(R.styleable.NadAds_adsCornerRadius, 0f);

                topLeftRadius = a.getDimension(R.styleable.NadAds_adsTopLeftCornerRadius, cornerRadius);
                topRightRadius = a.getDimension(R.styleable.NadAds_adsTopRightCornerRadius, cornerRadius);
                bottomLeftRadius = a.getDimension(R.styleable.NadAds_adsBottomLeftCornerRadius, cornerRadius);
                bottomRightRadius = a.getDimension(R.styleable.NadAds_adsBottomRightCornerRadius, cornerRadius);

                strokeWidth = a.getDimension(R.styleable.NadAds_adsStrokeWidth, 0f);
                strokeColor = a.getColor(R.styleable.NadAds_adsStrokeColor, Color.TRANSPARENT);
                strokePaint.setStrokeWidth(strokeWidth);
                strokePaint.setColor(strokeColor);

                elevationDp = a.getDimension(R.styleable.NadAds_adsElevationDp, 0f);
                setShadowElevation(elevationDp, Color.GRAY);

                shouldClipChildren = a.getBoolean(R.styleable.NadAds_adsClipChildren, true); // baca atribut baru
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        pathInvalid = true;
    }

    private void updateClipPath() {
        rectF.set(
                strokeWidth / 2f,
                strokeWidth / 2f,
                getWidth() - strokeWidth / 2f,
                getHeight() - strokeWidth / 2f
        );
        clipPath.reset();
        clipPath.addRoundRect(rectF, new float[]{
                topLeftRadius, topLeftRadius,
                topRightRadius, topRightRadius,
                bottomRightRadius, bottomRightRadius,
                bottomLeftRadius, bottomLeftRadius
        }, Path.Direction.CW);
        pathInvalid = false;
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        if (getWidth() == 0 || getHeight() == 0) {
            super.dispatchDraw(canvas);
            return;
        }

        if (pathInvalid) {
            updateClipPath();
        }

        int save = canvas.save();

        if (shouldClipChildren) { // <-- hanya clip kalau user mau
            canvas.clipPath(clipPath);
        }

        super.dispatchDraw(canvas);

        if (strokeWidth > 0f && strokeColor != Color.TRANSPARENT) {
            canvas.drawPath(clipPath, strokePaint);
        }

        canvas.restoreToCount(save);
    }

    public void setStrokeWidth(float width, @ColorInt int color) {
        strokeWidth = width;
        strokeColor = color;
        strokePaint.setStrokeWidth(width);
        strokePaint.setColor(color);
        pathInvalid = true;
        invalidate();
    }

    public void setShadowElevation(float elevation, @ColorInt int shadowColor) {
        this.elevationDp = elevation;
        setElevation(elevation);
        setOutlineProvider(new CustomOutlineProvider());
        setClipToOutline(true);
        invalidate();
    }

    private class CustomOutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            if (view.getWidth() > 0 && view.getHeight() > 0) {
                float maxRadius = Math.max(Math.max(topLeftRadius, topRightRadius),
                        Math.max(bottomLeftRadius, bottomRightRadius));
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), maxRadius);
            }
        }
    }

    // ==== Public setters =====
    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        this.topLeftRadius = radius;
        this.topRightRadius = radius;
        this.bottomLeftRadius = radius;
        this.bottomRightRadius = radius;
        pathInvalid = true;
        invalidate();
    }

    public void setTopLeftCornerRadius(float radius) {
        this.topLeftRadius = radius;
        pathInvalid = true;
        invalidate();
    }

    public void setTopRightCornerRadius(float radius) {
        this.topRightRadius = radius;
        pathInvalid = true;
        invalidate();
    }

    public void setBottomLeftCornerRadius(float radius) {
        this.bottomLeftRadius = radius;
        pathInvalid = true;
        invalidate();
    }

    public void setBottomRightCornerRadius(float radius) {
        this.bottomRightRadius = radius;
        pathInvalid = true;
        invalidate();
    }

    public void setClipChildrenEnabled(boolean enabled) { // public setter tambahan
        shouldClipChildren = enabled;
        invalidate();
    }
}
