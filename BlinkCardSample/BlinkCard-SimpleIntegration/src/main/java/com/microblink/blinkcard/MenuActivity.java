package com.microblink.blinkcard;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.microblink.blinkcard.activity.edit.BlinkCardEditResultBundle;
import com.microblink.blinkcard.entities.recognizers.Recognizer;
import com.microblink.blinkcard.entities.recognizers.RecognizerBundle;
import com.microblink.blinkcard.entities.recognizers.blinkcard.BlinkCardRecognizer;
import com.microblink.blinkcard.menu.BaseMenuActivity;
import com.microblink.blinkcard.menu.MenuListItem;
import com.microblink.blinkcard.result.activity.RecognizerBundleResultActivity;
import com.microblink.blinkcard.uisettings.ActivityRunner;
import com.microblink.blinkcard.uisettings.BlinkCardUISettings;
import com.microblink.blinkcard.util.RecognizerCompatibility;
import com.microblink.blinkcard.util.RecognizerCompatibilityStatus;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class MenuActivity extends BaseMenuActivity {

    public static final int MY_BLINK_CARD_REQUEST_CODE = 0x101;
    public static final int MY_BLINK_CARD_WITH_EDIT_REQUEST_CODE = 0x102;

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
    protected List<MenuListItem> createMenuListItems() {
        List<MenuListItem> items = new ArrayList<>();

        items.add(buildBlinkCardElement(true));
        items.add(buildBlinkCardElement(false));
        items.add(buildBlinkCardWithEditElement());

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
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == MY_BLINK_CARD_REQUEST_CODE) {
                startResultActivity(data);
            } else if (requestCode == MY_BLINK_CARD_WITH_EDIT_REQUEST_CODE) {
                showBlinkCardEditedResults(data);
            }
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

    private void showBlinkCardEditedResults(final Intent data) {
        BlinkCardEditResultBundle resultBundle = BlinkCardEditResultBundle.createFromIntent(data);
        String resultString =
                resultBundle.cardNumber + "\n"
                + resultBundle.expiryDate + "\n"
                + resultBundle.cvv + "\n"
                + resultBundle.owner + "\n"
                + resultBundle.iban;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(resultString);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface anInterface, int i) {
                startResultActivity(data);
            }
        });
        builder.create().show();
    }

    /**
     * Starts {@link com.microblink.blinkcard.activity.BlinkCardActivity} with given recognizer.
     * @param recognizer that will be used.
     */
    private void blinkCardRecognitionAction(Recognizer recognizer, boolean enableEditing) {
        BlinkCardUISettings uiSettings = new BlinkCardUISettings(new RecognizerBundle(recognizer));
        uiSettings.setBeepSoundResourceID(R.raw.beep);

        int requestCode;
        if (enableEditing) {
            requestCode = MY_BLINK_CARD_WITH_EDIT_REQUEST_CODE;
            uiSettings.setEditScreenEnabled(true);
            // You can also configure edit fields
            // BlinkCardEditFieldConfiguration editConfiguration = new BlinkCardEditFieldConfiguration();
            // editConfiguration.shouldDisplayIban = true;
            // ...
            // uiSettings.setEditScreenFieldConfiguration(editConfiguration);
        } else {
            requestCode = MY_BLINK_CARD_REQUEST_CODE;
        }

        ActivityRunner.startActivityForResult(this, requestCode, uiSettings);
    }

    private MenuListItem buildBlinkCardElement(final boolean scanIbanAndCvv) {
        return new MenuListItem(
                scanIbanAndCvv ? "Scan all fields" : "Scan without IBAN and CVV",
                new Runnable() {
                    @Override
                    public void run() {
                        BlinkCardRecognizer blinkCard = new BlinkCardRecognizer();
                        if (!scanIbanAndCvv) {
                            blinkCard.setExtractCvv(false);
                            blinkCard.setExtractIban(false);
                        }
                        ImageSettings.enableAllImages(blinkCard);
                        blinkCardRecognitionAction(blinkCard, false);
                    }
                }
        );
    }

    private MenuListItem buildBlinkCardWithEditElement() {
        return new MenuListItem(
                "Scan card and edit fields",
                new Runnable() {
                    @Override
                    public void run() {
                        BlinkCardRecognizer blinkCard = new BlinkCardRecognizer();
                        ImageSettings.enableAllImages(blinkCard);
                        blinkCardRecognitionAction(blinkCard, true);
                    }
                }
        );
    }
    
}
