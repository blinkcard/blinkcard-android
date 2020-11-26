# Release notes

## 2.0.0

### **BlinkCard v2 Release Announcement**

We're proud to announce our AI-driven BlinkCard v2.0! Extract the **card number** (PAN), **expiry date**, **owner** information (name or company title), **IBAN**, and **CVV**, from a large range of different card layouts.

As of this version, BlinkCard SDK is fully compatible with other Microblink SDKs, which means that you can use it with other Microblink SDKs in the same application.

- `BlinkCardRecognizer` is a Combined recognizer, which means it's designed for scanning **both sides of a card**. However, if all required data is found on the first side, we do not wait for second side scanning. We can return the result early. A set of required fields is defined through the recognizer's settings.

- "Front side" and "back side" are terms more suited to ID scanning. We start the scanning process with the **side containing the card number**. This makes the UX easier for users with cards where all data is on the back side.

- Available `BlinkCardRecognizer` [**settings**](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/blinkcard/BlinkCardRecognizer.html):
	- You can toggle mandatory **extraction** of all fields except the PAN.
	- You can enable the **blur filter**. When blur filtering is enabled, blurred frames are discarded. Otherwise, we process the blurred frames but set the blur indicator result member.
	- You can define required **padding** around the detected document. This ensures some empty space exists between the document and the edge of the frame.

- [BlinkCardRecognizer.Result](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/blinkcard/BlinkCardRecognizer.Result.html) structure:
    - Contains:
        - The card issuer
        - PAN
        - PAN prefix
        - Expiry date
        - Owner information
        - IBAN
        - CVV
        - Cropped document images
        - Blur indicators for both sides
        - Processing status
	- **Processing status** can be one of:
		- Success - if the process ended successfully and data is valid
		- DetectionFailed - if detection of the document failed
		- ImagePreprocessingFailed - if preprocessing of the image failed
		- StabilityTestFailed - if inconsistent results were detected between different video frames (when video processing, we require at least two frames with consistent data, for image processing this isn't applicable)
		- ScanningWrongSide - if the first side presented in the scanning process does not contain the PAN, or when the user failed to present the second side
		- FieldIdentificationFailed - if we detected a field, but we're unable to parse it (possible glare issues, or a finger covering the field)
		- ImageReturnFailed - failed to return requested images
		- UnsupportedCard - this card layout is currently unsupported.
- We've expanded the set of possible recognizer states with **StageValid**. This state is set when first side scanning completes with valid data, and second side scanning is required.


### New features:

- We added a new BlinkCard screen that allows users to edit `BlinkCardRecognizer` scan results:
    - This screen allows users to edit scanned data and input data that wasn't scanned.
    - Enable it by calling `BlinkCardUISettings.setEditScreenEnabled(true)`.
    - Configure which fields should be displayed on this screen by using `BlinkCardUISettings.setEditScreenFieldConfiguration()` method.
    - Set your custom theme with `BlinkCardUISettings.setEditScreenTheme()` method.
    - Change default strings by using `BlinkCardUISettings.setEditScreenStrings()`.
    - To get user-edited fields, in your `onActivityResult(int requestCode, int resultCode, Intent data)` method call `BlinkCardEditResultBundle.createFromIntent(data)`.
    - This feature is available only for `BlinkCardRecognizer`.
    - If you are using a custom UI, you can launch edit screen by building intent with the following method `BlinkCardEditActivity.buildIntent()`.

- We updated the default BlinkCard scanning screen (`BlinkCardOverlayController`):
    - Instructions on how to reduce glare will be displayed when the user enables flashlight, you can disable it with `BlinkCardUISettings.setShowGlareWarning(false)`.
    - If the edit screen is enabled, a new button will show up after 5 seconds of unsuccessful scanning to allow the user to go directly to the edit screen.

### Other improvements:

- We have translated complete SDK to following languages: **Arabic(UAE)**, **Chinese simplified**, **Chinese traditional**, **Croatian**, **Czech**, **Dutch**, **English**, **Filipino**, **French**, **German**, **Hewrew**, **Hungarian**, **Indonesian**, **Italian**, **Malay**, **Portuguese**, **Romanian**, **Slovak**, **Spanish**, **Slovenian**, **Thai** and **Vietnamese**.
- We have improved recognition timeout logic when using `BlinkCardRecognizer`.
    - When a credit card has multiple sides to scan, the timeout timer for the second side starts after the second side of the card has been detected. Previously, it has been started immediately after the first side has been scanned.
    - Timeout duration can be configured by using `RecognizerBundle.setNumMsBeforeTimeout`

### Major API changes:

- To ensure compatibility with other Microblink SDKs, we have repackaged all classes. We have renamed the root package `com.microblink` to `com.microblink.blinkcard`, which is unique to `BlinkCard` SDK.
- `ScanResultListener` interface now has an additional method called when the scanning cannot continue because of an unrecoverable error. You have to implement the `onUnrecoverableError` method.
If you’re using built-in activities, when `onActivityResult` is called with `RESULT_CANCELED` result code, the exception will be available via `ActivityRunner.EXTRA_SCAN_EXCEPTION` intent extra. If the user canceled the scan, the exception would be `null`.

### Minor API changes:

- We have renamed old `BlinkCardRecognizer` and `BlinkCardEliteRecognizer` recognizers to `LegacyBlinkCardRecognizer` and `LegacyBlinkCardEliteRecognizer`. They are now deprecated.
- We removed `RecognizerRunnerView` custom attributes: `mb_initialOrientation` and `mb_aspectMode`. Use `RecognizerRunnerView.setInitialOrientation` and `RecognizerRunnerView.setAspectMode` to configure the attributes in the code.
- We renamed `RecogitionMode` to `RecognitionDebugMode` in `RecognizerBundle`.


### Bug fixes:

- We fixed race conditions in camera management, which in some cases caused that the camera was unable to resume after it had been paused.
- We fixed crash when using Direct API on high resolution `com.microblink.image.Image` from `HighResImageWrapper`


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
