package com.microblink.blinkcard.sample;

import static android.widget.Toast.LENGTH_SHORT;

import android.os.Bundle;
import android.widget.Toast;

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

        String licenseKey = "sRwCAB9jb20ubWljcm9ibGluay5ibGlua2NhcmQuc2FtcGxlAGxleUpEY21WaGRHVmtUMjRpT2pFM056RXpNelU1TnpNeE5ETXNJa055WldGMFpXUkdiM0lpT2lKa1pHUXdOalpsWmkwMU9ESXpMVFF3TURndE9UUTRNQzAxTkRVNFlqQXhZVFUyWWpnaWZRPT0YFrpq6KfwMg1/IisdQJgz1wolnIpfGXQMT43vZmSMfueSLyL5HKpkotfXDzdy6xxsIrsVpBq1OTWHoL1URjMWSzezzuTmP9qjklZrOdHGeeUolfXYwsXJTG38rCI=";
        BlinkCardSdkSettings sdkSettings = new BlinkCardSdkSettings(licenseKey);
        BlinkCardScanActivitySettings activitySettings = new BlinkCardScanActivitySettings(sdkSettings);

        ActivityResultLauncher<BlinkCardScanActivitySettings> resultLauncher = registerForActivityResult(
                new MbBlinkCardScan(),
                result -> {
                    // handle result here
                    if (result.getStatus() == ScanActivityResultStatus.Scanned
                            && result.getResult().getCardholderName() != null) {
                        Toast.makeText(this, result.getResult().getCardholderName(), LENGTH_SHORT).show();
                    }
                }
        );

        findViewById(R.id.button).setOnClickListener(v -> resultLauncher.launch(activitySettings));
    }
}
