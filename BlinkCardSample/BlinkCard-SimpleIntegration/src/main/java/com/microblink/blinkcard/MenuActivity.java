package com.microblink.blinkcard;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.microblink.BaseMenuActivity;
import com.microblink.MenuListItem;
import com.microblink.entities.recognizers.Recognizer;
import com.microblink.entities.recognizers.RecognizerBundle;
import com.microblink.entities.recognizers.blinkcard.BlinkCardRecognizer;
import com.microblink.result.activity.RecognizerBundleResultActivity;
import com.microblink.uisettings.ActivityRunner;
import com.microblink.uisettings.BlinkCardUISettings;
import com.microblink.util.RecognizerCompatibility;
import com.microblink.util.RecognizerCompatibilityStatus;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends BaseMenuActivity {

    public static final int MY_BLINK_CARD_REQUEST_CODE = 0x101;

    @Override
    protected String getTitleText() {
        return getString(R.string.app_name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // in case of problems with the SDK (crashes or ANRs, uncomment following line to enable
        // verbose logging that can help developers track down the problem)
        //Log.setLogLevel(Log.LogLevel.LOG_VERBOSE);

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // check if BlinkCard is supported on the device
        RecognizerCompatibilityStatus supportStatus = RecognizerCompatibility.getRecognizerCompatibilityStatus(this);
        if (supportStatus != RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED) {
            Toast.makeText(this, "BlinkCard is not supported! Reason: " + supportStatus.name(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected List<com.microblink.MenuListItem> createMenuListItems() {
        List<com.microblink.MenuListItem> items = new ArrayList<>();

        items.add(buildBlinkCardElement(true));
        items.add(buildBlinkCardElement(false));

        return items;
    }

    /**
     * This method is invoked after returning from scan activity. You can obtain
     * scan results here
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // onActivityResult is called whenever we are returned from activity started
        // with startActivityForResult. We need to check request code to determine
        // that we have really returned from BlinkCard activity.
        if (requestCode == MY_BLINK_CARD_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            startResultActivity(data);
        } else {
            // if BlinkCard activity did not return result, user has probably
            // pressed Back button and cancelled scanning
            Toast.makeText(this, "Scan cancelled!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startResultActivity(Intent data) {
        // set intent's component to ResultActivity and pass its contents
        // to ResultActivity. ResultActivity will show how to extract
        // data from result.
        data.setComponent(new ComponentName(getApplicationContext(), RecognizerBundleResultActivity.class));
        startActivity(data);
    }

    /**
     * Starts {@link com.microblink.activity.BlinkCardActivity} with given recognizer.
     * @param recognizer that will be used.
     */
    private void blinkCardRecognitionAction(Recognizer recognizer) {
        BlinkCardUISettings uiSettings = new BlinkCardUISettings(new RecognizerBundle(recognizer));
        uiSettings.setBeepSoundResourceID(R.raw.beep);

        ActivityRunner.startActivityForResult(this, MY_BLINK_CARD_REQUEST_CODE, uiSettings);
    }

    private MenuListItem buildBlinkCardElement(final boolean scanBothSides) {
        return new MenuListItem(
                scanBothSides ? "Scan both sides" : "Scan front side only",
                new Runnable() {
                    @Override
                    public void run() {
                        BlinkCardRecognizer blinkCard = new BlinkCardRecognizer();
                        if (!scanBothSides) {
                            blinkCard.setExtractCvv(false);
                        }
                        ImageSettings.enableAllImages(blinkCard);

                        blinkCardRecognitionAction(blinkCard);
                    }
                }
        );
    }
    
}
