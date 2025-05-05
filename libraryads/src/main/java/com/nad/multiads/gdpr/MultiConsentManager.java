// MultiConsentManager.java (FINAL)
package com.nad.multiads.gdpr;

import android.app.Activity;
import android.content.Context;

import com.facebook.ads.AdSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

public class MultiConsentManager {

    private final Context context;
    private final ConsentInformation consentInformation;

    public MultiConsentManager(Context context) {
        this.context = context.getApplicationContext();
        this.consentInformation = UserMessagingPlatform.getConsentInformation(this.context);
    }

    public boolean getCanRequestAds() {
        return consentInformation.canRequestAds();
    }

    public boolean isGDPRRequired() {
        return consentInformation.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }

    public boolean isGDPRNotRequired() {
        return consentInformation.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.NOT_REQUIRED;
    }

    public boolean isGDPRUnknown() {
        return consentInformation.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.UNKNOWN;
    }

    public boolean isNonPersonalizedAds() {
        return !consentInformation.canRequestAds();
    }

    public void gatherConsent(Activity activity, OnConsentCompleteListener listener) {
        if (activity == null || listener == null) return;

        ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();

        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () -> {
                    if (consentInformation.isConsentFormAvailable()) {
                        loadAndShowConsentForm(activity, listener);
                    } else {
                        applyNetworkConsentDefaults();
                        listener.onConsentCompleted(null);
                    }
                },
                formError -> listener.onConsentCompleted(convertFormError(formError))
        );
    }

    private void loadAndShowConsentForm(Activity activity, OnConsentCompleteListener listener) {
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                activity,
                formError -> {
                    applyNetworkConsentDefaults();
                    listener.onConsentCompleted(convertFormError(formError));
                }
        );
    }

    private void applyNetworkConsentDefaults() {
        boolean canRequestAds = consentInformation.canRequestAds();
        if (canRequestAds) {
            AdSettings.setDataProcessingOptions(new String[]{});
        } else {
            AdSettings.setDataProcessingOptions(new String[]{"LDU"}, 0, 0);
        }
    }

    private ConsentError convertFormError(FormError formError) {
        if (formError == null) {
            return null;
        }
        return new ConsentError(formError.getErrorCode(), formError.getMessage());
    }

    public interface OnConsentCompleteListener {
        void onConsentCompleted(ConsentError error);
    }
}