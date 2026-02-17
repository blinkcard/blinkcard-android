plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.microblink.blinkcard.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.microblink.blinkcard.sample"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.activity)
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.blinkcard.ux)
}
