package com.nad.multiads.utils;

public interface AZAdsCallback {
    default void onAdRequestStarted() {}  // Saat mulai meminta iklan
    default void onAdLoaded() {}          // Saat iklan berhasil dimuat
    default void onAdDisplayed() {}       // Saat iklan ditampilkan ke pengguna
    default void onAdFailedToLoad() {}    // Saat gagal memuat iklan
    default void onAdFailedDisplay() {}    // Saat gagal memuat iklan
    default void onAdClicked() {}         // Saat pengguna mengklik iklan
    default void onAdClosed() {}          // Saat iklan ditutup oleh pengguna
    default void onAdCompleted() {}       // Saat pengguna menyelesaikan interaksi dengan iklan (rewarded ad)
}

