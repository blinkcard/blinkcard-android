# Release notes

## 1.1.0

### Major API changes:

- SDK has been migrated to **AndroidX** dependencies - previous SDK dependency com.android.support:appcompat-v7 has been replaced with  **androidx.appcompat:appcompat**

### New features:

- `BlinkCardRecognizer` now extracts IBAN from the Payment / Debit card
- it is possible to set theme that will be used by activity, launched from the UISettings - use UISettings.setActivityTheme


### Improvements:

- improved camera performance on some Samsung devices

### Minor API changes:

- all scan activity classes are final now
- in `BlinkCardRecognizer` and `BlinkCardEliteRecognizer` results, `documentDataMatch` value is now returned as `DataMatchResult` enum with three possible values: `NotPerformed`,  `Failed` and `Success`
- new API for configuring camera options on `UISettings` -  use `UISettings.setCameraSettings`, which accepts object of `CameraSettings` type
- `BlinkCardOverlayController` does not use UI settings anymore, it uses `BlinkCardOverlaySettings`, you can create `BlinkCardOverlayController` from `BlinkCardUISettings` by calling method `uiSettings.createOverlayController`

### Bug fixes:

- fixed bug in `BlinkCardRecognizer`:
    - `anonymizeCvv` now works independently of any other anonymization setting
- fixed problems with aspect ratio of camera preview on Huawei Mate 10
- `BlinkCardActivity` correctly set volume to media instead of ring
- `BlinkCardActivity` now apply secure flag if enabled in ui settings

## 1.0.1

### Bug fixes:
- fixed scanning bug for devices with problematic camera resolution, which caused that SDK was unable to scan data, known affected devices were: `OnePlus 6T`, `OnePlus 7 Pro` and `Vivo V15`

## 1.0.0

- SDK for Android that enables you to perform scans of various credit or payment cards in your app
- for more information, see [documentation](https://github.com/blinkcard/blinkcard-android) and [sample applications](https://github.com/blinkcard/blinkcard-android/tree/master/BlinkCardSample)