# AZMultiAds

Modular Ads Manager untuk Android yang mendukung **AdMob**, **AppLovin**, dan **Facebook Audience Network (FAN)** secara fleksibel dan efisien.

[![](https://jitpack.io/v/Arifinazis/azmultiads.svg)](https://jitpack.io/#Arifinazis/azmultiads)

---

## ✨ Fitur Utama

- ✅ Mendukung semua jenis iklan utama:
  - Banner
  - Interstitial
  - Rewarded
  - Native (Normal & Fullscreen)
- 🔁 Fallback otomatis ke jaringan cadangan jika primary gagal
- ⚙️ Modularisasi melalui `AZAdsConfig` & `AZAdsType`
- 📥 Sistem preload antrian Native Ads dengan `LinkedBlockingQueue` & `AtomicBoolean`
- 🚀 Thread-safe & memori aman (tidak bocor)
- ⏱️ Dukungan delay internal via `AZAdsRunUtils.runWithDelay()`
- 📦 Cocok digunakan di `Activity`, `Fragment`, `Adapter`, atau `Custom View`

---

## 📦 Integrasi

Tambahkan repository JitPack ke `settings.gradle` (Project level):

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Tambahkan dependency di `build.gradle` (App level):

```gradle
dependencies {
    implementation 'com.github.Arifinazis:azmultiads:1.0.0'
}
```

---

## 🧩 Cara Penggunaan

### 1. Inisialisasi Konfigurasi Iklan

```java
AZAds.getInstance(context).initAdConfigs(
    bannerConfig,
    interConfig,
    rewardConfig,
    nativeNormalConfig,
    nativeFullConfig
);
```

### 2. Preload Native Ads

```java
AZAds.getInstance(context).preloadNative(
    context,
    nativeConfig,
    isPrimary,
    isFullscreen
);
```

### 3. Menampilkan Native Ads

```java
AZAds.getInstance(context).showNativeAd(
    context,
    nadNativeView,
    isPrimary,
    isFullscreen,
    new AZAdsCallback() {
        @Override
        public void onAdLoaded() {
            // Ads Loaded
        }

        @Override
        public void onAdFailed(String error) {
            // Handle error
        }
    }
);
```

### 4. Menampilkan Interstitial

```java
AZAds.getInstance(context).showInterstitialAd(
    activity,
    interstitialConfig,
    isPrimary,
    isResume,
    new AZAdsCallback() {
        @Override
        public void onAdLoaded() {
            // Ad Shown
        }

        @Override
        public void onAdFailed(String error) {
            // Handle error
        }
    }
);
```

### 5. Menampilkan Rewarded

```java
AZAds.getInstance(context).showReward(
    activity,
    rewardConfig,
    isPrimary,
    new AZAdsCallback() {
        @Override
        public void onAdLoaded() {
            // Ad Shown
        }

        @Override
        public void onAdFailed(String error) {
            // Handle error
        }
    }
);
```

### 6. Menampilkan Banner

```java
AZAds.getInstance(context).showBanner(
    context,
    bannerConfig,
    nadBannerView,
    isPrimary
);
```

### 7. Utility Tambahan

```java
int queueSize = AZAds.getInstance(context).getNativeFullScreenSize();
View loadingView = AZAds.getInstance(context).getLoadingScreen();
AZAds.getInstance(context).destroy(); // Optional
```

---

## 🧾 License

```
MIT License

Copyright (c) 2025 Arifinazis

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
