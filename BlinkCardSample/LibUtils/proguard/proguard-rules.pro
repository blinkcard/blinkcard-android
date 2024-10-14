-renamesourcefileattribute line

# HelpActivity is public
-keep public class com.microblink.blinkcard.help.** {
    *;
}

# ResultActivity is public
-keep public class com.microblink.blinkcard.result.ResultActivity {
    *;
}

# ResultActivityBeta is public
-keep public class com.microblink.blinkcard.result.ResultActivityBeta {
    *;
}

# StatisticsActivity is public
-keep public class com.microblink.blinkcard.statistics.StatisticsActivity {
    *;
}

# DefaultImageListener is public
-keep public class com.microblink.blinkcard.result.DefaultImageListener {
    *;
}

-keep class com.microblink.blinkcard.fullscreen.** {
    *;
}

-keep class com.microblink.blinkcard.customcamera.** {
    *;
}

-keep class com.microblink.blinkcard.util.templating.** {
    public *;
}

-keep class com.microblink.blinkcard.detector.DetectorActivity {
    *;
}

-keep class com.microblink.blinkcard.result.extract.RecognitionResultExtractorFactory {
    public *;
}

-keep interface com.microblink.blinkcard.result.extract.IBaseRecognitionResultExtractor {
    public *;
}

-keep class com.microblink.blinkcard.result.extract.RecognitionResultEntry {
    public *;
}

-keep class com.microblink.blinkcard.util.BlinkOcrConfigurator {
    public *;
}

-keep class com.microblink.blinkcard.menu.BaseMenuActivity {
    *;
}

-keep class com.microblink.blinkcard.menu.MenuListItem {
    *;
}

# new gradle plugin does not do that, and causes app to violently crash
-keep class **.R$* {
    *;
}