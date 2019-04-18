package com.microblink;

import android.app.Application;

import com.microblink.intent.IntentDataTransferMode;

public final class BlinkCardSampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // obtain your licence at http://microblink.com/login or contact us at http://help.microblink.com
        MicroblinkSDK.setLicenseFile("com.microblink.blinkcard.mblic", this);

        // use optimised way for transferring RecognizerBundle between activities, while ensuring
        // data does not get lost when Android restarts the scanning activity
        MicroblinkSDK.setIntentDataTransferMode(IntentDataTransferMode.PERSISTED_OPTIMISED);
    }

}
