package com.microblink.blinkcard.sample;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.microblink.blinkcard.core.BlinkCardSdkSettings;
import com.microblink.blinkcard.ux.contract.BlinkCardScanActivitySettings;
import com.microblink.blinkcard.ux.contract.MbBlinkCardScan;
import com.microblink.blinkcard.ux.contract.ScanActivityResultStatus;

public class JavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_java);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String licenseKey = "YOUR_LICENSE_KEY";
        BlinkCardSdkSettings sdkSettings = new BlinkCardSdkSettings(licenseKey);
        BlinkCardScanActivitySettings activitySettings = new BlinkCardScanActivitySettings(sdkSettings);

        ActivityResultLauncher<BlinkCardScanActivitySettings> resultLauncher = registerForActivityResult(
                new MbBlinkCardScan(),
                result -> {
                    if (result.getStatus() == ScanActivityResultStatus.Scanned) {
                        // handle result.getResult() here
                    }
                }
        );

        findViewById(R.id.button).setOnClickListener(v -> resultLauncher.launch(activitySettings));
    }
}
