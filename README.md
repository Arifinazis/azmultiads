# AZMultiAds

Modular Ads Manager untuk Android yang mendukung **AdMob**, **AppLovin**, dan **Facebook Audience Network (FAN)** secara fleksibel dan efisien.

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

Tambahkan repository JitPack di `settings.gradle` project kamu:

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Lalu tambahkan dependency di `build.gradle` aplikasi:

```gradle
dependencies {
   implementation 'com.github.Arifinazis:azmultiads:1.0.0'
}
```
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
