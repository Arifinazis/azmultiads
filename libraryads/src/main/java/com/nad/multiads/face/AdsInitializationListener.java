package com.nad.multiads.face;


public interface AdsInitializationListener {
    void onInitializationSuccess();
    void onInitializationFailed(String errorMessage);
}