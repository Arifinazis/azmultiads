package com.nad.multiads.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nad.multiads.R;

public class AZAdLoadingScreen extends DialogFragment {

    private ConstraintLayout rootLayout;
    private TextView tvDes;

    private String descriptionText = null;
    private @ColorInt int backgroundColor = 0;
    private @DrawableRes int backgroundDrawableRes = 0;
    private boolean isShowing = false;

    public AZAdLoadingScreen() {
        setCancelable(false);
    }

    public static AZAdLoadingScreen newInstance() {
        return new AZAdLoadingScreen();
    }

    public AZAdLoadingScreen setDescription(@NonNull String text) {
        this.descriptionText = text;
        return this;
    }

    public AZAdLoadingScreen setBackgroundColor(@ColorInt int color) {
        this.backgroundColor = color;
        return this;
    }

    public AZAdLoadingScreen setBackgroundDrawable(@DrawableRes int resId) {
        this.backgroundDrawableRes = resId;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.view_loading_screen, container, false);
        rootLayout = view.findViewById(R.id.rootLayout);
        tvDes = view.findViewById(R.id.tv_des);
        applyCustomizations();
        return view;
    }

    private void applyCustomizations() {
        if (descriptionText != null) {
            tvDes.setText(descriptionText);
        }
        if (backgroundColor != 0) {
            rootLayout.setBackgroundColor(backgroundColor);
        }
        if (backgroundDrawableRes != 0) {
            rootLayout.setBackgroundResource(backgroundDrawableRes);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setGravity(Gravity.CENTER);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            View decorView = window.getDecorView();
            ViewCompat.setOnApplyWindowInsetsListener(decorView, (v, insets) -> insets);
        }
    }

    public void showAllowingStateLoss(@NonNull FragmentManager fm, @Nullable String tag) {
        if (!isShowing && !isAdded()) {
            isShowing = true;
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        }
    }

    @Override
    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        if (isAdded() || isShowing) return -1;
        isShowing = true;
        transaction.add(this, tag);
        return transaction.commitAllowingStateLoss();
    }

    @Override
    public void dismiss() {
        isShowing = false;
        super.dismiss();
    }
}
