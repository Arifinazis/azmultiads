package com.nad.multiads.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum AZAdsType implements Serializable {
    ADMOB("admob"),
    ADMOB_BINDING_FAN("admob_fan"),
    FAN("facebook"),
    APPLOVIN("applovin"),
    APPLOVIN_MAX("applovin_max"),
    FAN_BIDDING_APPLOVIN_MAX("fan_bidding_applovin_max"),
    APPLOVIN_DISCOVERY("applovin_discovery");

    private final String networkName;

    private static final Map<String, AZAdsType> lookupMap = new HashMap<>();

    static {
        for (AZAdsType type : AZAdsType.values()) {
            lookupMap.put(type.networkName.toLowerCase(), type);
        }
    }

    AZAdsType(String networkName) {
        this.networkName = networkName;
    }

    public String getNetworkName() {
        return networkName;
    }

    public static AZAdsType fromNetworkName(String networkName) {
        if (networkName == null) return null;
        return lookupMap.get(networkName.toLowerCase());
    }

    public boolean isApplovinNetwork() {
        return this == APPLOVIN || this == APPLOVIN_MAX;
    }

    public String logName() {
        switch (this) {
            case ADMOB:
                return "AdMob Ads";
            case FAN:
                return "Facebook Audience Network";
            case APPLOVIN:
                return "AppLovin Ads";
            case APPLOVIN_MAX:
                return "AppLovin MAX Ads";
            case FAN_BIDDING_APPLOVIN_MAX:
                return "FAN Binding APPLOVIN MAX ads";
            case APPLOVIN_DISCOVERY:
                return "Applovin Discovery";
            default:
                return "Unknown Ads Network";
        }
    }
}
