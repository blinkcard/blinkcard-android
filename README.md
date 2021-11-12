# _BlinkCard_ SDK for Android

[![Build Status](https://travis-ci.org/blinkcard/blinkcard-android.svg?branch=master)](https://travis-ci.org/blinkcard/blinkcard-android)

_BlinkCard_ SDK for Android is SDK that enables you to perform scans of various credit or payment cards in your app. You can simply integrate the SDK into your app by following the instructions below.

Using _BlinkCard_ in your app requires a valid license. You can obtain a trial license by registering to [Microblink dashboard](https://microblink.com/login). After registering, you will be able to generate a license for your app. License is bound to [package name](https://developer.android.com/studio/build/application-id.html) of your app, so please make sure you enter the correct package name when asked.

See below for more information about how to integrate _BlinkCard_ SDK into your app and also check latest [Release notes](Release%20notes.md).

You can start by watching our [step-by-step tutorial](https://vimeo.com/542539564/c0f92f9cbf), in which you’ll find out how to make BlinkCard SDK a part of your Android app.

To see _BlinkCard_ in action, check our [demo app](https://play.google.com/store/apps/details?id=com.microblink.showcase.playstore).

# Table of contents

* [Android _BlinkCard_ integration instructions](#intro)
* [Quick Start](#quickStart)
    * [Quick start with the sample app](#quickDemo)
    * [SDK integration](#androidStudioIntegration)
* [Device requirements](#supportCheck)
* [_BlinkCard_ SDK integration levels](#uiCustomizations)
    * [Built-in activities (`UISettings`)](#runBuiltinActivity)
    * [Built-in fragment (`RecognizerRunnerFragment`)](#recognizerRunnerFragment)
    * [Custom UX with `RecognizerRunnerView`](#recognizerRunnerView)
    * [Direct API](#directAPI)
        * [Using Direct API for recognition of Android Bitmaps and custom camera frames](#directAPI_images)
        * [Using Direct API for `String` recognition (parsing)](#directAPI_strings)
        * [Understanding DirectAPI's state machine](#directAPIStateMachine)
        * [Using Direct API while RecognizerRunnerView is active](#directAPIWithRecognizer)
        * [Using Direct API with combined recognizers ](#directAPI_combined_recognizers)
* [Available activities and overlays](#builtInUIComponents)
    * [`BlinkCardUISettings` and `BlinkCardOverlayController`](#blinkcardUiComponent)
    * [Translation and localization](#translation)
* [Handling processing events with `RecognizerRunner` and `RecognizerRunnerView`](#processingEvents)
* [`Recognizer` concept and `RecognizerBundle`](#availableRecognizers)
    * [The `Recognizer` concept](#recognizerConcept)
    * [`RecognizerBundle`](#recognizerBundle)
        * [Passing `Recognizer` objects between activities](#intentOptimization)
* [List of available recognizers](#recognizerList)
    * [Frame Grabber Recognizer](#frameGrabberRecognizer)
    * [Success Frame Grabber Recognizer](#successFrameGrabberRecognizer)
    * [BlinkCard recognizers](#blinkcard_recognizers)
        * [BlinkCard recognizer](#blink_card_recognizer)
        * [LegacyBlinkCardRecognizer (deprecated)](#legacy_blink_card_recognizer)
        * [LegacyBlinkCardEliteRecognizer (deprecated)](#legacy_blink_card_elite_recognizer)
* [Embedding _BlinkCard_ inside another SDK](#embedAAR)
* [Processor architecture considerations](#archConsider)
    * [Reducing the final size of your app](#reduceSize)
        * [Consequences of removing processor architecture](#archConsequences)
    * [Combining _BlinkCard_ with other native libraries](#combineNativeLibraries)
* [Troubleshooting](#troubleshoot)
* [FAQ and known issues](#faq)
* [Additional info](#info)
    * [BlinkCard SDK size](#size_report)
    * [API reference](#api_reference)
    * [Contact](#contact)

# <a name="intro"></a> Android _BlinkCard_ integration instructions

The package contains Android Archive (AAR) that contains everything you need to use _BlinkCard_ library. Besides AAR, package also contains a sample project that contains following modules:

- _BlinkCard-SimpleIntegration_ demonstrates quick and simple integration of _BlinkCard_ library.
 
The source code of all sample apps is given to you to show you how to perform integration of _BlinkCard_ SDK into your app. You can use this source code and all resources as you wish. You can use sample apps as a basis for creating your own app, or you can copy/paste the code and/or resources from sample apps into your app and use them as you wish without even asking us for permission.
 
_BlinkCard_ is supported on Android SDK version 16 (Android 4.1) or later.

The list of all provided scan activities can be found in the [Built-in activities and overlays](#builtInUIComponents) section.

You can also create your own scanning UI - you just need to embed `RecognizerRunnerView` into your activity and pass activity's lifecycle events to it and it will control the camera and recognition process. For more information, see [Embedding `RecognizerRunnerView` into custom scan activity](#recognizerRunnerView).

# <a name="quickStart"></a> Quick Start

## <a name="quickDemo"></a> Quick start with the sample app

1. Open Android Studio.
2. In Quick Start dialog choose _Import project (Eclipse ADT, Gradle, etc.)_.
3. In File dialog select _BlinkCardSample_ folder.
4. Wait for the project to load. If Android studio asks you to reload project on startup, select `Yes`.


## <a name="androidStudioIntegration"></a> SDK integration
#### Adding _BlinkCard_ dependency

In your `build.gradle`, add _BlinkCard_ maven repository to repositories list

```
repositories {
    maven { url 'https://maven.microblink.com' }
}
```

Add _BlinkCard_ as a dependency and make sure `transitive` is set to true

```
dependencies {
    implementation('com.microblink:blinkcard:2.5.0@aar') {
        transitive = true
    }
}
```

#### Importing Javadoc

Android studio 3.0 should automatically import javadoc from maven dependency. If that doesn't happen, you can do that manually by following these steps:

1. In Android Studio project sidebar, ensure [project view is enabled](https://developer.android.com/sdk/installing/studio-androidview.html)
2. Expand `External Libraries` entry (usually this is the last entry in project view)
3. Locate `blinkcard-2.5.0` entry, right click on it and select `Library Properties...`
4. A `Library Properties` pop-up window will appear
5. Click the second `+` button in bottom left corner of the window (the one that contains `+` with little globe)
6. Window for defining documentation URL will appear
7. Enter following address: `https://blinkcard.github.io/blinkcard-android/`
8. Click `OK`


#### Performing your first scan
1. First you'll need to create an account at [Microblink dashboard](https://microblink.com/login) where you can generate a free trial license key for your app. License is bound to [package name](http://tools.android.com/tech-docs/new-build-system/applicationid-vs-packagename) of your app, so please make sure you enter the correct package name when asked. 

    Download your licence file and put it in your application's _assets_ folder. Make sure to set the license key before using any other classes from the SDK, otherwise you will get a runtime exception. 
    
    We recommend that you extend [Android Application class](https://developer.android.com/reference/android/app/Application.html) and set the license in [onCreate callback](https://developer.android.com/reference/android/app/Application.html#onCreate()) like this:

    ```java
    public class MyApplication extends Application {
        @Override
        public void onCreate() {
            MicroblinkSDK.setLicenseFile("path/to/license/file/within/assets/dir", this);
        }
    }
    ```

2. In your main activity, create recognizer objects that will perform image recognition, configure them and put them into [RecognizerBundle object](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/RecognizerBundle.html). You can see more information about available recognizers and `RecognizerBundle` [here](#availableRecognizers). 

	For example, to scan Payment / Debit card, configure your recognizer like this:

    ```java
    public class MyActivity extends Activity {
        private BlinkCardRecognizer mRecognizer;
        private RecognizerBundle mRecognizerBundle;
        
        @Override
        protected void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            
            // setup views, as you would normally do in onCreate callback
            
            // create BlinkCardRecognizer
            mRecognizer = new BlinkCardRecognizer();
            
            // bundle recognizers into RecognizerBundle
            mRecognizerBundle = new RecognizerBundle(mRecognizer);
        }
    }
    ```

3. Start recognition process by creating `BlinkCardUISettings` and calling [`ActivityRunner.startActivityForResult`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/uisettings/ActivityRunner.html#startActivityForResult-android.app.Activity-int-com.microblink.uisettings.UISettings-):
	
	```java
	// method within MyActivity from previous step
	public void startScanning() {
        // Settings for BlinkCardActivity
        BlinkCardUISettings settings = new BlinkCardUISettings(mRecognizerBundle);
        
        // tweak settings as you wish
        
        // Start activity
        ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, settings);
	}
	```
	
4. `onActivityResult` will be called in your activity after scanning is finished, here you can get the scanning results.

	```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                // load the data into all recognizers bundled within your RecognizerBundle
                mRecognizerBundle.loadFromIntent(data);
                
                // now every recognizer object that was bundled within RecognizerBundle
                // has been updated with results obtained during scanning session
                
                // you can get the result by invoking getResult on recognizer
                BlinkCardRecognizer.Result result = mRecognizer.getResult();
                if (result.getResultState() == Recognizer.Result.State.Valid) {
                    // result is valid, you can use it however you wish
                }
            }
        }
    }
	```
	
	For more information about available recognizers and `RecognizerBundle`, see [RecognizerBundle and available recognizers](#availableRecognizers).

# <a name="supportCheck"></a> Device requirements

### Android Version

_BlinkCard_ requires Android API level **16** or newer. For best performance and compatibility, we recommend at least Android 5.0.

### Camera

Camera video preview resolution also matters. In order to perform successful scans, camera preview resolution must be at least 720p. Note that camera preview resolution is not the same as video recording resolution.

### Processor architecture

_BlinkCard_ is distributed with **ARMv7**, **ARM64**, **x86** and **x86_64** native library binaries.

_BlinkCard_ is a native library, written in C++ and available for multiple platforms. Because of this, _BlinkCard_ cannot work on devices with obscure hardware architectures. We have compiled _BlinkCard_ native code only for the most popular Android [ABIs](https://en.wikipedia.org/wiki/Application_binary_interface).

Even before setting the license key, you should check if the _BlinkCard_ is supported on the current device (see next section: *Compatibility check*). Attempting to call any method from the SDK that relies on native code, such as license check, on a device with unsupported CPU architecture will crash your app.

If you are combining _BlinkCard_ library with other libraries that contain native code into your application, make sure you match the architectures of all native libraries.

For example, if a third party library has got only ARMv7 and ARM64 versions, you must use exactly ARMv7 and ARM64 versions of _BlinkCard_ with that library, but not x86. Using these architectures will crash your app at the initialization step because JVM will try to load all its native dependencies in the same preferred architecture and will fail with `UnsatisfiedLinkError`. 

For more information, see [Processor architecture considerations](#archConsider) section.

### Compatibility check

Here's how you can check whether the _BlinkCard_ is supported on the device:
	
```java
// check if BlinkCard is supported on the device,
RecognizerCompatibilityStatus status = RecognizerCompatibility.getRecognizerCompatibilityStatus(this);
if (status == RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED) {
    Toast.makeText(this, "BlinkCard is supported!", Toast.LENGTH_LONG).show();
} else if (status == RecognizerCompatibilityStatus.NO_CAMERA) {
    Toast.makeText(this, "BlinkCard is supported only via Direct API!", Toast.LENGTH_LONG).show();
} else if (status == RecognizerCompatibilityStatus.PROCESSOR_ARCHITECTURE_NOT_SUPPORTED) {
    Toast.makeText(this, "BlinkCard is not supported on current processor architecture!", Toast.LENGTH_LONG).show();
} else {
	Toast.makeText(this, "BlinkCard is not supported! Reason: " + status.name(), Toast.LENGTH_LONG).show();
}
```

Some recognizers require camera with autofocus. If you try using them on a device that doesn't support autofocus, you will get an error. To prevent that, you can check whether a recognizer requires autofocus by calling its [requiresAutofocus](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/Recognizer.html#requiresAutofocus--) method.

If you already have an array of recognizers, you can easily filter out recognizers that require autofocus from array using the following code snippet:

```java
Recognizer[] recArray = ...;
if(!RecognizerCompatibility.cameraHasAutofocus(CameraType.CAMERA_BACKFACE, this)) {
	recArray = RecognizerUtils.filterOutRecognizersThatRequireAutofocus(recArray);
}
```
# <a name="uiCustomizations"></a> _BlinkCard_ SDK integration levels

You can integrate _BlinkCard_ into your app in four different ways, depending on your use case and customisation needs:

1. Built-in activities (`UISettings`) - SDK handles everything and you just need to start our built-in activity and handle result, customisation options are limited
2. Built-in fragment (`RecognizerRunnerFragment`) - reuse scanning UX from our built-in activities in your own activity
3. Custom UX (`RecognizerRunnerView`) - SDK handles camera management while you have to implement completely custom scanning UX
4. Direct Api (`RecognizerRunner`) - SKD only handles recognition while you have to provide it with the images, either from camera or from a file

## <a name="runBuiltinActivity"></a> Built-in activities (`UISettings`)

`UISettings` is a class that contains all the necessary settings for SDK's built-in scan activities. It configures scanning activity behaviour, strings, icons and other UI elements. 
As shown in the first scan example, you should use [`ActivityRunner `](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/uisettings/ActivityRunner.html) to start the scan activity configured by `UISettings`.

We provide multiple `UISettings` classes specialised for different scanning scenarios. Each `UISettings` object has properties which can be changed via appropriate setter methods. For example, you can customise camera settings with `setCameraSettings` metod. 

All available `UISettings` classes are listed [here](#builtInUIComponents).

## <a name="recognizerRunnerFragment"></a> Built-in fragment (`RecognizerRunnerFragment`)

If you want to reuse our built-in activity UX inside your own activity, use [`RecognizerRunnerFragment`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/fragment/RecognizerRunnerFragment.html). Activity that will host `RecognizerRunnerFragment` must implement [`ScanningOverlayBinder`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/fragment/RecognizerRunnerFragment.ScanningOverlayBinder.html) interface. Attempting to add `RecognizerRunnerFragment` to activity that does not implement that interface will result in `ClassCastException`.

The `ScanningOverlayBinder` is responsible for returning `non-null` implementation of [`ScanningOverlay`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/fragment/overlay/ScanningOverlay.html) - class that will manage UI on top of `RecognizerRunnerFragment`. It is not recommended to create your own `ScanningOverlay` implementation, use one of our implementations listed [here](#builtInUIComponents) instead.

Here is the minimum example for activity that hosts the `RecognizerRunnerFragment`:

```java
public class MyActivity extends AppCompatActivity implements RecognizerRunnerFragment.ScanningOverlayBinder {
    private BlinkCardRecognizer mRecognizer;
    private RecognizerBundle mRecognizerBundle;
    private BlinkCardOverlayController mScanOverlay;
    private RecognizerRunnerFragment mRecognizerRunnerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate();
        setContentView(R.layout.activity_my_activity);
        mScanOverlay = createOverlay();
        if (null == savedInstanceState) {
            // create fragment transaction to replace R.id.recognizer_runner_view_container with RecognizerRunnerFragment
            mRecognizerRunnerFragment = new RecognizerRunnerFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.recognizer_runner_view_container, mRecognizerRunnerFragment);
            fragmentTransaction.commit();
        } else {
            // obtain reference to fragment restored by Android within super.onCreate() call
            mRecognizerRunnerFragment = (RecognizerRunnerFragment) getSupportFragmentManager().findFragmentById(R.id.recognizer_runner_view_container);
        }
    }

    @Override
    @NonNull
    public ScanningOverlay getScanningOverlay() {
        return mScanOverlay;
    }

    private BlinkCardOverlayController createOverlay() {
        // create BlinkCardRecognizer
        mRecognizer = new BlinkCardRecognizer();

        // bundle recognizers into RecognizerBundle
        mRecognizerBundle = new RecognizerBundle(mRecognizer);

        BlinkCardUISettings settings = new BlinkCardUISettings(mRecognizerBundle);

        return settings.createOverlayController(this, mScanResultListener);
    }

    private final ScanResultListener mScanResultListener = new ScanResultListener() {
        @Override
        public void onScanningDone(@NonNull RecognitionSuccessType recognitionSuccessType) {
            // pause scanning to prevent new results while fragment is being removed
            mRecognizerRunnerFragment.getRecognizerRunnerView().pauseScanning();

            // now you can remove the RecognizerRunnerFragment with new fragment transaction
            // and use result within mRecognizer safely without the need for making a copy of it

            // if not paused, as soon as this method ends, RecognizerRunnerFragments continues
            // scanning. Note that this can happen even if you created fragment transaction for
            // removal of RecognizerRunnerFragment - in the time between end of this method
            // and beginning of execution of the transaction. So to ensure result within mRecognizer
            // does not get mutated, ensure calling pauseScanning() as shown above.
        }
        @Override
        public void onUnrecoverableError(@NonNull Throwable throwable) {
        }
    };
    
}
```

Please refer to sample apps provided with the SDK for more detailed example and make sure your host activity's orientation is set to `nosensor` or has configuration changing enabled (i.e. is not restarted when configuration change happens). For more information, check [scan orientation section](#scanOrientation).

## <a name="recognizerRunnerView"></a> Custom UX with `RecognizerRunnerView`
This section discusses how to embed [RecognizerRunnerView](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/RecognizerRunnerView.html) into your scan activity and perform scan.

1. First make sure that `RecognizerRunnerView` is a member field in your activity. This is required because you will need to pass all activity's lifecycle events to `RecognizerRunnerView`.
2. It is recommended to keep your scan activity in one orientation, such as `portrait` or `landscape`. Setting `sensor` as scan activity's orientation will trigger full restart of activity whenever device orientation changes. This will provide very poor user experience because both camera and _BlinkCard_ native library will have to be restarted every time. There are measures against this behaviour that are discussed [later](#scanOrientation).
3. In your activity's `onCreate` method, create a new `RecognizerRunnerView`, set [RecognizerBundle](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/RecognizerBundle.html) containing recognizers that will be used by the view, define [CameraEventsListener](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/CameraEventsListener.html) that will handle mandatory camera events, define [ScanResultListener](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/ScanResultListener.html) that will receive call when recognition has been completed and then call its `create` method. After that, add your views that should be layouted on top of camera view.
4. Pass in your activity's lifecycle using `setLifecycle` method to enable automatic handling of lifeceycle events.

Here is the minimum example of integration of `RecognizerRunnerView` as the only view in your activity:

```java
public class MyScanActivity extends AppCompatActivity {
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 42;
    private RecognizerRunnerView mRecognizerRunnerView;
    private BlinkCardRecognizer mRecognizer;
    private RecognizerBundle mRecognizerBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create BlinkCardRecognizer
        mRecognizer = new BlinkCardRecognizer();

        // bundle recognizers into RecognizerBundle
        mRecognizerBundle = new RecognizerBundle(mRecognizer);
        // create RecognizerRunnerView
        mRecognizerRunnerView = new RecognizerRunnerView(this);
        
        // set lifecycle to automatically call recognizer runner view lifecycle methods
        mRecognizerRunnerView.setLifecycle(getLifecycle());

        // associate RecognizerBundle with RecognizerRunnerView
        mRecognizerRunnerView.setRecognizerBundle(mRecognizerBundle);

        // scan result listener will be notified when scanning is complete
        mRecognizerRunnerView.setScanResultListener(mScanResultListener);
        // camera events listener will be notified about camera lifecycle and errors
        mRecognizerRunnerView.setCameraEventsListener(mCameraEventsListener);

        setContentView(mRecognizerRunnerView);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // changeConfiguration is not handled by lifecycle events so call it manually
        mRecognizerRunnerView.changeConfiguration(newConfig);
    }

    private final CameraEventsListener mCameraEventsListener = new CameraEventsListener() {
        @Override
        public void onCameraPreviewStarted() {
            // this method is from CameraEventsListener and will be called when camera preview starts
        }

        @Override
        public void onCameraPreviewStopped() {
            // this method is from CameraEventsListener and will be called when camera preview stops
        }

        @Override
        public void onError(Throwable exc) {
            /**
             * This method is from CameraEventsListener and will be called when
             * opening of camera resulted in exception or recognition process
             * encountered an error. The error details will be given in exc
             * parameter.
             */
        }

        @Override
        @TargetApi(23)
        public void onCameraPermissionDenied() {
            /**
             * Called in Android 6.0 and newer if camera permission is not given
             * by user. You should request permission from user to access camera.
             */
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_REQUEST_CODE);
            /**
             * Please note that user might have not given permission to use
             * camera. In that case, you have to explain to user that without
             * camera permissions scanning will not work.
             * For more information about requesting permissions at runtime, check
             * this article:
             * https://developer.android.com/training/permissions/requesting.html
             */
        }

        @Override
        public void onAutofocusFailed() {
            /**
             * This method is from CameraEventsListener will be called when camera focusing has failed.
             * Camera manager usually tries different focusing strategies and this method is called when all
             * those strategies fail to indicate that either object on which camera is being focused is too
             * close or ambient light conditions are poor.
             */
        }

        @Override
        public void onAutofocusStarted(Rect[] areas) {
            /**
             * This method is from CameraEventsListener and will be called when camera focusing has started.
             * You can utilize this method to draw focusing animation on UI.
             * Areas parameter is array of rectangles where focus is being measured.
             * It can be null on devices that do not support fine-grained camera control.
             */
        }

        @Override
        public void onAutofocusStopped(Rect[] areas) {
            /**
             * This method is from CameraEventsListener and will be called when camera focusing has stopped.
             * You can utilize this method to remove focusing animation on UI.
             * Areas parameter is array of rectangles where focus is being measured.
             * It can be null on devices that do not support fine-grained camera control.
             */
        }
    };
    
    private final ScanResultListener mScanResultListener = new ScanResultListener() {
        @Override
        public void onScanningDone(@NonNull RecognitionSuccessType recognitionSuccessType) {
            // this method is from ScanResultListener and will be called when scanning completes
            // you can obtain scanning result by calling getResult on each
            // recognizer that you bundled into RecognizerBundle.
            // for example:

            BlinkCardRecognizer.Result result = mRecognizer.getResult();
            if (result.getResultState() == Recognizer.Result.State.Valid) {
                // result is valid, you can use it however you wish
            }

            // Note that mRecognizer is stateful object and that as soon as
            // scanning either resumes or its state is reset
            // the result object within mRecognizer will be changed. If you
            // need to create a immutable copy of the result, you can do that
            // by calling clone() on it, for example:

            BlinkCardRecognizer.Result immutableCopy = result.clone();

            // After this method ends, scanning will be resumed and recognition
            // state will be retained. If you want to prevent that, then
            // you should call:
            mRecognizerRunnerView.resetRecognitionState();
            // Note that reseting recognition state will clear internal result
            // objects of all recognizers that are bundled in RecognizerBundle
            // associated with RecognizerRunnerView.

            // If you want to pause scanning to prevent receiving recognition
            // results or mutating result, you should call:
            mRecognizerRunnerView.pauseScanning();
            // if scanning is paused at the end of this method, it is guaranteed
            // that result within mRecognizer will not be mutated, therefore you
            // can avoid creating a copy as described above

            // After scanning is paused, you will have to resume it with:
            mRecognizerRunnerView.resumeScanning(true);
            // boolean in resumeScanning method indicates whether recognition
            // state should be automatically reset when resuming scanning - this
            // includes clearing result of mRecognizer
        }
    };  
    
}
```

#### <a name="scanOrientation"></a> Scan activity's orientation

If activity's `screenOrientation` property in `AndroidManifest.xml` is set to `sensor`, `fullSensor` or similar, activity will be restarted every time device changes orientation from portrait to landscape and vice versa. While restarting activity, its `onPause`, `onStop` and `onDestroy` methods will be called and then new activity will be created anew. This is a potential problem for scan activity because in its lifecycle it controls both camera and native library - restarting the activity will trigger both restart of the camera and native library. This is a problem because changing orientation from landscape to portrait and vice versa will be very slow, thus degrading a user experience. **We do not recommend such setting.**

For that matter, we recommend setting your scan activity to either `portrait` or `landscape` mode and handle device orientation changes manually. To help you with this, `RecognizerRunnerView` supports adding child views to it that will be rotated regardless of activity's `screenOrientation`. You add a view you wish to be rotated (such as view that contains buttons, status messages, etc.) to `RecognizerRunnerView` with [addChildView](#{javadocUrl}(com/microblink/blinkcard/view/CameraViewGroup.html#addChildView-android.view.View-boolean-)) method. The second parameter of the method is a boolean that defines whether the view you are adding will be rotated with device. To define allowed orientations, implement [OrientationAllowedListener](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/OrientationAllowedListener.html) interface and add it to `RecognizerRunnerView` with method `setOrientationAllowedListener`. **This is the recommended way of rotating camera overlay.**

However, if you really want to set `screenOrientation` property to `sensor` or similar and want Android to handle orientation changes of your scan activity, then we recommend to set `configChanges` property of your activity to `orientation|screenSize`. This will tell Android not to restart your activity when device orientation changes. Instead, activity's `onConfigurationChanged` method will be called so that activity can be notified of the configuration change. In your implementation of this method, you should call `changeConfiguration` method of `RecognizerView` so it can adapt its camera surface and child views to new configuration.
## <a name="directAPI"></a> Direct API

This section will describe how to use direct API to recognize android Bitmaps without the need for camera. You can use direct API anywhere from your application, not just from activities.

Image recognition performance highly depends on the quality of the input images. When our camera management is used (scanning from a camera), we do our best to get camera frames with the best possible quality for the used device. On the other hand, when Direct API is used, you need to provide high-quality images without blur and glare for successful recognition.

### <a name="directAPI_images"></a> Using Direct API for recognition of Android Bitmaps and custom camera frames

1. First, you need to obtain reference to [RecognizerRunner singleton](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html) using [getSingletonInstance](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#getSingletonInstance--).
2. Second, you need to [initialize the recognizer runner](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#initialize-android.content.Context-com.microblink.entities.recognizers.RecognizerBundle-com.microblink.directApi.DirectApiErrorListener-).
3. After initialization, you can use singleton to [process Android bitmaps](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#recognizeBitmap-android.graphics.Bitmap-com.microblink.hardware.orientation.Orientation-com.microblink.geometry.Rectangle-com.microblink.view.recognition.ScanResultListener-) or [images](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#recognizeImage-com.microblink.image.Image-com.microblink.view.recognition.ScanResultListener-) that are [built from custom camera frames](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/image/ImageBuilder.html#buildImageFromCamera1NV21Frame-byte:A-int-int-com.microblink.hardware.orientation.Orientation-com.microblink.geometry.Rectangle-). Currently, it is not possible to process multiple images in parallel.
4. When you want to delete all cached data from multiple recognitions, for example when you want to scan other document and/or restart scanning, you need to [reset the recognition state](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#resetRecognitionState--).
5. Do not forget to [terminate](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#terminate--) the recognizer runner singleton after usage (it is a shared resource).

Here is the minimum example of usage of direct API for recognizing android Bitmap:

```java
public class DirectAPIActivity extends Activity {
    private RecognizerRunner mRecognizerRunner;
    private BlinkCardRecognizer mRecognizer;
    private RecognizerBundle mRecognizerBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate();
        // initialize your activity here
        // create BlinkCardRecognizer
        mRecognizer = new BlinkCardRecognizer();

        // bundle recognizers into RecognizerBundle
        mRecognizerBundle = new RecognizerBundle(mRecognizer);

        try {
            mRecognizerRunner = RecognizerRunner.getSingletonInstance();
        } catch (FeatureNotSupportedException e) {
            Toast.makeText(this, "Feature not supported! Reason: " + e.getReason().getDescription(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mRecognizerRunner.initialize(this, mRecognizerBundle, new DirectApiErrorListener() {
            @Override
            public void onRecognizerError(Throwable t) {
                Toast.makeText(DirectAPIActivity.this, "There was an error in initialization of Recognizer: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // start recognition
        Bitmap bitmap = BitmapFactory.decodeFile("/path/to/some/file.jpg");
        mRecognizerRunner.recognizeBitmap(bitmap, Orientation.ORIENTATION_LANDSCAPE_RIGHT, mScanResultListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecognizerRunner.terminate();
    }

    private final ScanResultListener mScanResultListener = new ScanResultListener() {
        @Override
        public void onScanningDone(@NonNull RecognitionSuccessType recognitionSuccessType) {
            // this method is from ScanResultListener and will be called
            // when scanning completes
            // you can obtain scanning result by calling getResult on each
            // recognizer that you bundled into RecognizerBundle.
            // for example:

            BlinkCardRecognizer.Result result = mRecognizer.getResult();
            if (result.getResultState() == Recognizer.Result.State.Valid) {
                // result is valid, you can use it however you wish
            }
        }
    };

}
```

[ScanResultListener.onScanningDone](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/ScanResultListener.html#onScanningDone-RecognitionSuccessType-) method is called for each input image that you send to the recognition. You can call `RecognizerRunner.recognize*` method multiple times with different images of the same document for better reading accuracy until you get a successful result in the listener's `onScanningDone` method. This is useful when you are using your own or third-party camera management.

### <a name="directAPI_strings"></a> Using Direct API for `String` recognition (parsing)

Some recognizers support recognition from `String`. They can be used through Direct API to parse given `String` and return data just like when they are used on an input image. When recognition is performed on `String`, there is no need for the OCR. Input `String` is used in the same way as the OCR output is used when image is being recognized. 

Recognition from `String` can be performed in the same way as recognition from image, described in the [previous section](#directAPI_images). 

The only difference is that one of the [RecognizerRunner singleton](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html) methods for recognition from string should be called:

- [recognizeString](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#recognizeString-java.lang.String-com.microblink.view.recognition.ScanResultListener-)
- [recognizeStringWithRecognizers](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#recognizeStringWithRecognizers-java.lang.String-com.microblink.view.recognition.ScanResultListener-com.microblink.entities.recognizers.RecognizerBundle-)


### <a name="directAPIStateMachine"></a> Understanding DirectAPI's state machine

Direct API's `RecognizerRunner` singleton is a state machine that can be in one of 3 states: `OFFLINE`, `READY` and `WORKING`.

- When you obtain the reference to `RecognizerRunner` singleton, it will be in `OFFLINE` state. 
- You can initialize `RecognizerRunner` by calling [initialize](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#initialize-android.content.Context-com.microblink.entities.recognizers.RecognizerBundle-com.microblink.directApi.DirectApiErrorListener-) method. If you call `initialize` method while `RecognizerRunner` is not in `OFFLINE` state, you will get `IllegalStateException`.
- After successful initialization, `RecognizerRunner` will move to `READY` state. Now you can call any of the `recognize*` methods.
- When starting recognition with any of the `recognize*` methods, `RecognizerRunner` will move to `WORKING` state. If you attempt to call these methods while `RecognizerRunner` is not in `READY` state, you will get `IllegalStateException`
- Recognition is performed on background thread so it is safe to call all `RecognizerRunner's` methods from UI thread
- When recognition is finished, `RecognizerRunner` first moves back to `READY` state and then calls the [onScanningDone](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/ScanResultListener.html#onScanningDone-RecognitionSuccessType-) method of the provided [`ScanResultListener`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/ScanResultListener.html). 
- Please note that `ScanResultListener`'s [`onScanningDone`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/ScanResultListener.html#onScanningDone-RecognitionSuccessType-) method will be called on background processing thread, so make sure you do not perform UI operations in this callback. Also note that until the `onScanningDone` method completes, `RecognizerRunner` will not perform recognition of another image or string, even if any of the `recognize*` methods have been called just after transitioning to `READY` state. This is to ensure that results of the recognizers bundled within `RecognizerBundle` associated with `RecognizerRunner` are not modified while possibly being used within `onScanningDone` method.
- By calling [`terminate`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#terminate--) method, `RecognizerRunner` singleton will release all its internal resources. Note that even after calling `terminate` you might receive `onScanningDone` event if there was work in progress when `terminate` was called.
- `terminate` method can be called from any `RecognizerRunner` singleton's state
- You can observe `RecognizerRunner` singleton's state with method [`getCurrentState`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#getCurrentState--)

### <a name="directAPIWithRecognizer"></a> Using Direct API while RecognizerRunnerView is active
Both [RecognizerRunnerView](#recognizerRunnerView) and `RecognizerRunner` use the same internal singleton that manages native code. This singleton handles initialization and termination of native library and propagating recognizers to native library. It is possible to use `RecognizerRunnerView` and `RecognizerRunner` together, as internal singleton will make sure correct synchronization and correct recognition settings are used. If you run into problems while using `RecognizerRunner` in combination with `RecognizerRunnerView`, [let us know](http://help.microblink.com)!


### <a name="directAPI_combined_recognizers"></a> Using Direct API with combined recognizers 

When you are using combined recognizer and images of both document sides are required, you need to call `RecognizerRunner.recognize*` multiple times. Call it first with the images of the first side of the document, until it is read, and then with the images of the second side. The combined recognizer automatically switches to second side scanning, after it has successfully read the first side. To be notified when the first side scanning is completed, you have to set the [FirstSideRecognitionCallback](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/metadata/recognition/FirstSideRecognitionCallback.html) through [MetadataCallbacks](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/metadata/MetadataCallbacks.html). If you don't need that information, e.g. when you have only one image for each document side, don't set the `FirstSideRecognitionCallback` and check the [RecognitionSuccessType](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/recognition/RecognitionSuccessType.html) in [ScanResultListener.onScanningDone](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/ScanResultListener.html#onScanningDone-RecognitionSuccessType-), after the second side image has been processed.

# <a name="builtInUIComponents"></a> Available activities and overlays
## <a name="blinkcardUiComponent"></a> `BlinkCardUISettings` and `BlinkCardOverlayController`

[`BlinkCardOverlayController`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/fragment/overlay/blinkcard/BlinkCardOverlayController.html) is an overlay best suited for scanning payment cards. It can be used for other card documents like ID cards, passports, driver's licenses, etc. This overlay also supports **combined recognizers**, because it manages scanning of multiple document sides in the single camera opening and guides the user through the scanning process.

To launch a built-in activity that uses `BlinkCardOverlayController` use [`BlinkCardUISettings`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/uisettings/BlinkCardUISettings.html).

### Scan overlay theming
<p align="center" >
  <img src="https://raw.githubusercontent.com/wiki/blinkcard/blinkcard-android/images/scan_screen_customisation.png" alt="BlinkID SDK">
</p>

To customise overlay, provide your custom style resource via [`BlinkCardUISettings.setOverlayViewStyle()`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/uisettings/BlinkCardUISettings.html#setOverlayViewStyle-int-) method or via [`ScanLineOverlayView `](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/fragment/overlay/blinkcard/scanlineui/ScanLineOverlayView.html) constructor. You can customise elements labeled on the screenshot above by providing the following attributes in your style:

**exit**

* `mb_exitScanDrawable` - icon drawable

**torch**

* `mb_torchOnDrawable` - icon drawable that is shown when the torch is enabled
* `mb_torchOffDrawable` - icon drawable that is show when the torch is disabled

**instructions text**

* `mb_instructionsTextAppearance` - style that will be used as `android:textAppearance`

**glare warning**

* `mb_glareWarningTextAppearance` - style that will be used as TextAppearance
* `mb_glareWarningBackgroundDrawable` - drawable used for background
* note that you can disable this element by using [`BlinkCardUISettings.setShowGlareWarning(false)`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/uisettings/BlinkCardUISettings.html#setShowGlareWarning-boolean-)

### Edit results screen
SDK also provides an activity that allows users to edit scanned results and input data that wasn't scanned. Note that this activity works only with `BlinkCardRecognizer`.

If you are using `BlinkCardUISettings`, enable edit screen by calling `BlinkCardUISettings.setEditScreenEnabled(true)`, otherwise, launch it by building an intent with `BlinkCardEditActivity.buildIntent()` and save scanned results to the intent by using `RecognizerBundle.saveToIntent(intent)`.  

If edit screen is enabled, in your `onActivityResult(int requestCode, int resultCode, Intent data)` method, intent will contain the original scanned results (`RecognizerBundle.loadFromIntent(data)`) and also user-edited fields (`BlinkCardEditResultBundle.createFromIntent(data)`).

Edit results activity can be customised in several ways:

* to configure which fields should be displayed use `BlinkCardUISettings.setEditScreenFieldConfiguration()`
* set your custom theme with `BlinkCardUISettings.setEditScreenTheme()` method
* change default strings by using `BlinkCardUISettings.setEditScreenStrings()`

### Edit screen theming

<p align="center" >
  <img src="https://raw.githubusercontent.com/wiki/blinkcard/blinkcard-android/images/edit_screen_customisation.png" alt="BlinkID SDK">
</p>

To customise edit results activity, provide your custom theme resource via [`BlinkCardUISettings.setEditScreenTheme()`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/uisettings/BlinkCardUISettings.html#setEditScreenTheme-int-). Your custom theme can either:

1. extend our default theme `MB_theme_blink_card_edit_screen` and override just specific attributes
2. extend any of the AppCompat themes and define all attributes listed below

Our default theme extends `Theme.AppCompat.Light` so if you want to use a dark theme you'll need to go with option number 2.

**toolbar**

* `mb_blinkcardEditToolbarTheme` - style that will be used as toolbar theme
* `mb_blinkcardEditToolbarBackground` - toolbar background color

**label**

* `mb_blinkcardEditLabelTextAppearance` - style that will be used as `android:textAppearance`
* `mb_blinkcardEditLabelTextColor` - text color used when the field is not in focus or in error state
* `mb_blinkcardEditErrorColor` - text color used in case of an error
* `colorAccent` - text color used when the field is focused

**value**

* `mb_blinkcardEditValueTextAppearance` - style that will be used as `android:textAppearance`
* `mb_blinkcardEditValueTextColor` - color for inputed value text
* `mb_blinkcardEditValueHintColor` - color for hint text

**divider**

* `mb_glareWarningTextAppearance` - style that will be used as `android:textAppearance`
* `mb_blinkcardEditDividerColor` - background color used when the field is not in focus or in error state
* `mb_blinkcardEditErrorColor` - background color used when the field is in error state
* `colorAccent` - background color used when the field is focused

**error**

* `mb_blinkcardEditErrorTextAppearance` - style that will be used as `android:textAppearance`
* `mb_blinkcardEditErrorColor ` - text color

**confirm button**

* `mb_blinkcardEditConfirmButtonStyle` - button style


## <a name="translation"></a> Translation and localization

Strings used within built-in activities and overlays can be localized to any language. If you are using `RecognizerRunnerView` ([see this chapter for more information](#recognizerRunnerView)) in your custom scan activity or fragment, you should handle localization as in any other Android app. `RecognizerRunnerView` does not use strings nor drawables, it only uses assets from `assets/microblink` folder. Those assets must not be touched as they are required for recognition to work correctly.

However, if you use our built-in activities or overlays, they will use resources packed within `LibBlinkCard.aar` to display strings and images on top of the camera view. We have already prepared strings for several languages which you can use out of the box. You can also [modify those strings](#stringChanging), or you can [add your own language](#addLanguage).

To use a language, you have to enable it from the code:
		
* To use a certain language, on application startup, before opening any UI component from the SDK, you should call method `LanguageUtils.setLanguageAndCountry(language, country, context)`. For example, you can set language to Croatian like this:
	
	```java
	// define BlinkCard language
	LanguageUtils.setLanguageAndCountry("hr", "", this);
	```

#### <a name="addLanguage"></a> Adding new language

_BlinkCard_ can easily be translated to other languages. The `res` folder in `LibBlinkCard.aar` archive has folder `values` which contains `strings.xml` - this file contains english strings. In order to make e.g. croatian translation, create a folder `values-hr` in your project and put the copy of `strings.xml` inside it (you might need to extract `LibBlinkCard.aar` archive to access those files). Then, open that file and translate the strings from English into Croatian.

#### <a name="stringChanging"></a> Changing strings in the existing language
	
To modify an existing string, the best approach would be to:

1. Choose a language you want to modify. For example Croatian ('hr').
2. Find `strings.xml` in folder `res/values-hr` of the `LibBlinkCard.aar` archive
3. Choose a string key which you want to change. For example: ```<string name="MBBack">Back</string>```
4. In your project create a file `strings.xml` in the folder `res/values-hr`, if it doesn't already exist
5. Create an entry in the file with the value for the string which you want. For example: ```<string name="MBBack">Natrag</string>```
6. Repeat for all the string you wish to change

# <a name="processingEvents"></a> Handling processing events with `RecognizerRunner` and `RecognizerRunnerView`

Processing events, also known as _Metadata callbacks_ are purely intended for giving processing feedback on UI or to capture some debug information during development of your app using _BlinkCard_ SDK. For that reason, built-in activities and fragments handle those events internally. If you need to handle those events yourself, you need to use either [RecognizerRunnerView](#recognizerRunnerView) or [RecognizerRunner](#directAPI).

Callbacks for all events are bundled into the [MetadataCallbacks](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/metadata/MetadataCallbacks.html) object. Both [RecognizerRunner](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html#setMetadataCallbacks-com.microblink.metadata.MetadataCallbacks-) and [RecognizerRunnerView](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/RecognizerRunnerView.html#setMetadataCallbacks-com.microblink.metadata.MetadataCallbacks-) have methods which allow you to set all your callbacks.

We suggest that you check for more information about available callbacks and events to which you can handle in the [javadoc for MetadataCallbacks class](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/metadata/MetadataCallbacks.html).

Please note that both those methods need to pass information about available callbacks to the native code and for efficiency reasons this is done at the time `setMetadataCallbacks` method is called and **not every time** when change occurs within the `MetadataCallbacks` object. This means that if you, for example, set `QuadDetectionCallback` to `MetadataCallbacks` **after** you already called `setMetadataCallbacks` method, the `QuadDetectionCallback` will not be registered with the native code and you will not receive its events.

Similarly, if you, for example, remove the `QuadDetectionCallback` from `MetadataCallbacks` object **after** you already called `setMetadataCallbacks` method, your app will crash with `NullPointerException` when our processing code attempts to invoke the method on removed callback (which is now set to `null`). We **deliberately** do not perform `null` check here because of two reasons:

- it is inefficient
- having `null` callback, while still being registered to native code is illegal state of your program and it should therefore crash

**Remember**, each time you make some changes to `MetadataCallbacks` object, you need to apply those changes to to your `RecognizerRunner` or `RecognizerRunnerView` by calling its `setMetadataCallbacks` method.

# <a name="availableRecognizers"></a> `Recognizer` concept and `RecognizerBundle`

This section will first describe [what is a `Recognizer`](#recognizerConcept) and how it should be used to perform recognition of the images, videos and camera stream. Next, [we will describe how `RecognizerBundle`](#recognizerBundle) can be used to tweak the recognition procedure and to transfer `Recognizer` objects between activities.

[RecognizerBundle](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/RecognizerBundle.html) is an object which wraps the [Recognizers](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/Recognizer.html) and defines settings about how recognition should be performed. Besides that, `RecognizerBundle` makes it possible to transfer `Recognizer` objects between different activities, which is required when using built-in activities to perform scanning, as described in first scan section, but is also handy when you need to pass `Recognizer` objects between your activities.

List of all available `Recognizer` objects, with a brief description of each `Recognizer`, its purpose and recommendations how it should be used to get best performance and user experience, can be found [here](#recognizerList) .

## <a name="recognizerConcept"></a> The `Recognizer` concept

The [Recognizer](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/Recognizer.html) is the basic unit of processing within the _BlinkCard_ SDK. Its main purpose is to process the image and extract meaningful information from it. As you will see [later](#recognizerList), the _BlinkCard_ SDK has lots of different `Recognizer` objects that have various purposes.

Each `Recognizer` has a `Result` object, which contains the data that was extracted from the image. The `Result` object is a member of corresponding `Recognizer` object and its lifetime is bound to the lifetime of its parent `Recognizer` object. If you need your `Result` object to outlive its parent `Recognizer` object, you must make a copy of it by calling its method [`clone()`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/Entity.Result.html#clone--).

Every `Recognizer` is a stateful object, that can be in two states: _idle state_ and _working state_. While in _idle state_, you can tweak `Recognizer` object's properties via its getters and setters. After you bundle it into a `RecognizerBundle` and use either [RecognizerRunner](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/directApi/RecognizerRunner.html) or [RecognizerRunnerView](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/RecognizerRunnerView.html) to _run_ the processing with all `Recognizer` objects bundled within `RecognizerBundle`, it will change to _working state_ where the `Recognizer` object is being used for processing. While being in _working state_, you cannot tweak `Recognizer` object's properties. If you need to, you have to create a copy of the `Recognizer` object by calling its [`clone()`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/Entity.html#clone--), then tweak that copy, bundle it into a new `RecognizerBundle` and use [`reconfigureRecognizers`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/RecognizerRunnerView.html#reconfigureRecognizers-com.microblink.entities.recognizers.RecognizerBundle-) to ensure new bundle gets used on processing thread.

While `Recognizer` object works, it changes its internal state and its result. The `Recognizer` object's `Result` always starts in [Empty state](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/Recognizer.Result.State.html#Empty). When corresponding `Recognizer` object performs the recognition of given image, its `Result` can either stay in `Empty` state (in case `Recognizer` failed to perform recognition), move to [Uncertain state](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/Recognizer.Result.State.html#Uncertain) (in case `Recognizer` performed the recognition, but not all mandatory information was extracted), move to [StageValid state](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/Recognizer.Result.State.html#StageValid) (in case `Recognizer` successfully scanned one part/side of the document and there are more fields to extract) or move to [Valid state](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/Recognizer.Result.State.html#Valid) (in case `Recognizer` performed recognition and all mandatory information was successfully extracted from the image).

As soon as one `Recognizer` object's `Result` within `RecognizerBundle` given to `RecognizerRunner` or `RecognizerRunnerView` changes to `Valid` state, the [`onScanningDone`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/ScanResultListener.html#onScanningDone-RecognitionSuccessType-) callback will be invoked on same thread that performs the background processing and you will have the opportunity to inspect each of your `Recognizer` objects' `Results` to see which one has moved to `Valid` state.

As already stated in [section about `RecognizerRunnerView`](#recognizerRunnerView), as soon as `onScanningDone` method ends, the `RecognizerRunnerView` will continue processing new camera frames with same `Recognizer` objects, unless [paused](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/RecognizerRunnerView.html#pauseScanning--). Continuation of processing or [resetting recognition](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/view/recognition/RecognizerRunnerView.html#resetRecognitionState--) will modify or reset all `Recognizer` objects's `Results`. When using built-in activities, as soon as `onScanningDone` is invoked, built-in activity pauses the `RecognizerRunnerView` and starts finishing the activity, while saving the `RecognizerBundle` with active `Recognizer` objects into `Intent` so they can be transferred back to the calling activities.


## <a name="recognizerBundle"></a> `RecognizerBundle`

The [RecognizerBundle](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/RecognizerBundle.html) is wrapper around [Recognizers](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/Recognizer.html) objects that can be used to transfer `Recognizer` objects between activities and to give `Recognizer` objects to `RecognizerRunner` or `RecognizerRunnerView` for processing.

The `RecognizerBundle` is always [constructed with array](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/RecognizerBundle.html#RecognizerBundle-com.microblink.entities.recognizers.Recognizer:A-) of `Recognizer` objects that need to be prepared for recognition (i.e. their properties must be tweaked already). The _varargs_ constructor makes it easier to pass `Recognizer` objects to it, without the need of creating a temporary array.

The `RecognizerBundle` manages a chain of `Recognizer` objects within the recognition process. When a new image arrives, it is processed by the first `Recognizer` in chain, then by the second and so on, iterating until a `Recognizer` object's `Result` changes its state to `Valid` or all of the `Recognizer` objects in chain were invoked (none getting a `Valid` result state). If you want to invoke all `Recognizers` in the chain, regardless of whether some `Recognizer` object's `Result` in chain has changed its state to `Valid` or not, you can [allow returning of multiple results on a single image](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/RecognizerBundle.html#setAllowMultipleScanResultsOnSingleImage-boolean-).

You cannot change the order of the `Recognizer` objects within the chain - no matter the order in which you give `Recognizer` objects to `RecognizerBundle`, they are internally ordered in a way that provides best possible performance and accuracy. Also, in order for _BlinkCard_ SDK to be able to order `Recognizer` objects in recognition chain in the best way possible, it is not allowed to have multiple instances of `Recognizer` objects of the same type within the chain. Attempting to do so will crash your application.

### <a name="intentOptimization"></a> Passing `Recognizer` objects between activities

Besides managing the chain of `Recognizer` objects, `RecognizerBundle` also manages transferring bundled `Recognizer` objects between different activities within your app. Although each `Recognizer` object, and each its `Result` object implements [Parcelable interface](https://developer.android.com/reference/android/os/Parcelable.html), it is not so straightforward to put those objects into [Intent](https://developer.android.com/reference/android/content/Intent.html) and pass them around between your activities and services for two main reasons:

- `Result` object is tied to its `Recognizer` object, which manages lifetime of the native `Result` object.
- `Result` object often contains large data blocks, such as images, which cannot be transferred via `Intent` because of [Android's Intent transaction data limit](https://developer.android.com/reference/android/os/TransactionTooLargeException.html).

Although the first problem can be easily worked around by making a [copy](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/Entity.Result.html#clone--) of the `Result` and transfer it independently, the second problem is much tougher to cope with. This is where, `RecognizerBundle's` methods [saveToIntent](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/intent/IntentTransferableBundle.html#saveToIntent-android.content.Intent-) and [loadFromIntent](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/intent/IntentTransferableBundle.html#loadFromIntent-android.content.Intent-) come to help, as they ensure the safe passing of `Recognizer` objects bundled within `RecognizerBundle` between activities according to policy defined with method [`setIntentDataTransferMode`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/MicroblinkSDK.html#setIntentDataTransferMode-com.microblink.intent.IntentDataTransferMode-):

- if set to [`STANDARD`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/intent/IntentDataTransferMode.html#STANDARD), the `Recognizer` objects will be passed via `Intent` using normal _Intent transaction mechanism_, which is limited by [Android's Intent transaction data limit](https://developer.android.com/reference/android/os/TransactionTooLargeException.html). This is same as manually putting `Recognizer` objects into `Intent` and is OK as long as you do not use `Recognizer` objects that produce images or other large objects in their `Results`.
- if set to [`OPTIMISED`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/intent/IntentDataTransferMode.html#OPTIMISED), the `Recognizer` objects will be passed via internal singleton object and no serialization will take place. This means that there is no limit to the size of data that is being passed. This is also the fastest transfer method, but it has a serious drawback - if Android kills your app to save memory for other apps and then later restarts it and redelivers `Intent` that should contain `Recognizer` objects, the internal singleton that should contain saved `Recognizer` objects will be empty and data that was being sent will be lost. You can easily provoke that condition by choosing _No background processes_ under _Limit background processes_ in your device's _Developer options_, and then switch from your app to another app and then back to your app.
- if set to [`PERSISTED_OPTIMISED`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/intent/IntentDataTransferMode.html#PERSISTED_OPTIMISED), the `Recognizer` objects will be passed via internal singleton object (just like in `OPTIMISED` mode) and will additionaly be serialized into a file in your application's private folder. In case Android restarts your app and internal singleton is empty after re-delivery of the `Intent`, the data will be loaded from file and nothing will be lost. The files will be automatically cleaned up when data reading takes place. Just like `OPTIMISED`, this mode does not have limit to the size of data that is being passed and does not have a drawback that `OPTIMISED` mode has, but some users might be concerned about files to which data is being written. 
    - These files **will** contain end-user's private data, such as image of the object that was scanned and the extracted data. Also these files **may** remain saved in your application's private folder until the next successful reading of data from the file. 
    - If your app gets restarted multiple times, only after first restart will reading succeed and will delete the file after reading. If multiple restarts take place, you must implement [`onSaveInstanceState`](https://developer.android.com/reference/android/app/Activity.html#onSaveInstanceState(android.os.Bundle)) and save bundle back to file by calling its [`saveState`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/RecognizerBundle.html#saveState--) method. Also, after saving state, you should ensure that you [clear saved state](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/RecognizerBundle.html#clearSavedState--) in your [`onResume`](https://developer.android.com/reference/android/app/Activity.html#onResume()), as [`onCreate`](https://developer.android.com/reference/android/app/Activity.html#onCreate(android.os.Bundle)) may not be called if activity is not restarted, while `onSaveInstanceState` may be called as soon as your activity goes to background (before `onStop`), even though activity may not be killed at later time. 
    - If saving data to file in private storage is a concern to you, you should use either `OPTIMISED` mode to transfer large data and image between activities or create your own mechanism for data transfer. Note that your application's private folder is only accessible by your application and your application alone, unless the end-user's device is rooted.

# <a name="recognizerList"></a> List of available recognizers

This section will give a list of all `Recognizer` objects that are available within _BlinkCard_ SDK, their purpose and recommendations how they should be used to get best performance and user experience.

## <a name="frameGrabberRecognizer"></a> Frame Grabber Recognizer

The [`FrameGrabberRecognizer`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/framegrabber/FrameGrabberRecognizer.html) is the simplest recognizer in _BlinkCard_ SDK, as it does not perform any processing on the given image, instead it just returns that image back to its [`FrameCallback`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/framegrabber/FrameCallback.html). Its [Result](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/framegrabber/FrameGrabberRecognizer.Result.html) never changes state from [Empty](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/Recognizer.Result.State.html#Empty).

This recognizer is best for easy capturing of camera frames with [`RecognizerRunnerView`](#recognizerRunnerView). Note that [`Image`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/image/Image.html) sent to [`onFrameAvailable`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/framegrabber/FrameCallback.html#onFrameAvailable-com.microblink.image.Image-boolean-double-) are temporary and their internal buffers all valid only until the `onFrameAvailable` method is executing - as soon as method ends, all internal buffers of `Image` object are disposed. If you need to store `Image` object for later use, you must create a copy of it by calling [`clone`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/image/Image.html#clone--).

Also note that [`FrameCallback`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/framegrabber/FrameCallback.html) interface extends [Parcelable interface](https://developer.android.com/reference/android/os/Parcelable.html), which means that when implementing `FrameCallback` interface, you must also implement `Parcelable` interface. 

This is especially important if you plan to transfer `FrameGrabberRecognizer` between activities - in that case, keep in mind that the instance of your object may not be the same as the instance on which `onFrameAvailable` method gets called - the instance that receives `onFrameAvailable` calls is the one that is created within activity that is performing the scan.

## <a name="successFrameGrabberRecognizer"></a> Success Frame Grabber Recognizer

The [`SuccessFrameGrabberRecognizer`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/successframe/SuccessFrameGrabberRecognizer.html) is a special `Recognizer` that wraps some other `Recognizer` and impersonates it while processing the image. However, when the `Recognizer` being impersonated changes its `Result` into `Valid` state, the `SuccessFrameGrabberRecognizer` captures the image and saves it into its own `Result` object.

Since `SuccessFrameGrabberRecognizer` impersonates its slave `Recognizer` object, it is not possible to give both concrete `Recognizer` object and `SuccessFrameGrabberRecognizer` that wraps it to same `RecognizerBundle` - doing so will have the same result as if you have given two instances of same `Recognizer` type to the `RecognizerBundle` - it will crash your application.

This recognizer is best for use cases when you need to capture the exact image that was being processed by some other `Recognizer` object at the time its `Result` became `Valid`. When that happens, `SuccessFrameGrabber's` [`Result`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/successframe/SuccessFrameGrabberRecognizer.Result.html) will also become `Valid` and will contain described image. That image can then be retrieved with [`getSuccessFrame()`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/successframe/SuccessFrameGrabberRecognizer.Result.html#getSuccessFrame--) method.

## <a name="blinkcard_recognizers"></a> BlinkCard recognizers

BlinkCard recognizers work best with the [`BlinkCardActivity`](#blinkcardUiComponent), which has UI best suited for credit card scanning. 

### <a name="blink_card_recognizer"></a> BlinkCard recognizer
The [`BlinkCardRecognizer`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/blinkcard/BlinkCardRecognizer.html) extracts the **card number** (PAN), **expiry date**, **owner** information (name or company title), **IBAN**, and **CVV**, from a large range of different card layouts. 

`BlinkCardRecognizer` is a Combined recognizer, which means it's designed for scanning **both sides of a card**. However, if all required data is found on the first side, we do not wait for second side scanning. We can return the result early. A set of required fields is defined through the recognizer's settings.

"Front side" and "back side" are terms more suited to ID scanning. We start the scanning process with the **side containing the card number**. This makes the UX easier for users with cards where all data is on the back side.

### <a name="legacy_blink_card_recognizer"></a> LegacyBlinkCardRecognizer (deprecated)
The [`LegacyBlinkCardRecognizer`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/blinkcard/legacy/LegacyBlinkCardRecognizer.html) scans back side of Payment / Debit card after scanning the front side and combines data from both sides.

### <a name="legacy_blink_card_elite_recognizer"></a> LegacyBlinkCardEliteRecognizer (deprecated)
The [`LegacyBlinkCardEliteRecognizer`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/recognizers/blinkcard/legacy/LegacyBlinkCardEliteRecognizer.html) scans back side of elite Payment / Debit card after scanning the front side and combines data from both sides.
# <a name="embedAAR"></a> Embedding _BlinkCard_ inside another SDK
	
You need to ensure that the final app gets all resources required by _BlinkCard_. At the time of writing this documentation, [Android does not have support for combining multiple AAR libraries into single fat AAR](https://stackoverflow.com/questions/20700581/android-studio-how-to-package-single-aar-from-multiple-library-projects/20715155#20715155). The problem is that resource merging is done while building application, not while building AAR, so application must be aware of all its dependencies. **There is no official Android way of "hiding" third party AAR within your AAR.**

This problem is usually solved with transitive Maven dependencies, i.e. when publishing your AAR to Maven you specify dependencies of your AAR so they are automatically referenced by app using your AAR. Besides this, there are also several other approaches you can try:

- you can ask your clients to reference _BlinkCard_ in their app when integrating your SDK
- since the problem lies in resource merging part you can try avoiding this step by ensuring your library will not use any component from _BlinkCard_ that uses resources (i.e. built-in activities, fragments and views, except `RecognizerRunnerView`). You can perform [custom UI integration](#recognizerRunnerView) while taking care that all resources (strings, layouts, images, ...) used are solely from your AAR, not from _BlinkCard_. Then, in your AAR you should not reference `LibBlinkCard.aar` as gradle dependency, instead you should unzip it and copy its assets to your AAR’s assets folder, its `classes.jar` to your AAR’s lib folder (which should be referenced by gradle as jar dependency) and contents of its jni folder to your AAR’s src/main/jniLibs folder.
- Another approach is to use [3rd party unofficial gradle script](https://github.com/adwiv/android-fat-aar) that aim to combine multiple AARs into single fat AAR. Use this script at your own risk and report issues to [its developers](https://github.com/adwiv/android-fat-aar/issues) - we do not offer support for using that script.
- There is also a [3rd party unofficial gradle plugin](https://github.com/Vigi0303/fat-aar-plugin) which aims to do the same, but is more up to date with latest updates to Android gradle plugin. Use this plugin at your own risk and report all issues with using to [its developers](https://github.com/Vigi0303/fat-aar-plugin/issues) - we do not offer support for using that plugin.

# <a name="archConsider"></a> Processor architecture considerations

_BlinkCard_ is distributed with **ARMv7**, **ARM64**, **x86** and **x86_64** native library binaries.

**ARMv7** architecture gives the ability to take advantage of hardware accelerated floating point operations and SIMD processing with [NEON](http://www.arm.com/products/processors/technologies/neon.php). This gives _BlinkCard_ a huge performance boost on devices that have ARMv7 processors. Most new devices (all since 2012.) have ARMv7 processor so it makes little sense not to take advantage of performance boosts that those processors can give. Also note that some devices with ARMv7 processors do not support NEON and VFPv4 instruction sets, most popular being those based on [NVIDIA Tegra 2](https://en.wikipedia.org/wiki/Tegra#Tegra_2), [ARM Cortex A9](https://en.wikipedia.org/wiki/ARM_Cortex-A9) and older. Since these devices are old by today's standard, _BlinkCard_ does not support them. For the same reason, _BlinkCard_ does not support devices with ARMv5 (`armeabi`) architecture.

**ARM64** is the new processor architecture that most new devices use. ARM64 processors are very powerful and also have the possibility to take advantage of new NEON64 SIMD instruction set to quickly process multiple pixels with a single instruction.

**x86** and **x86_64** architectures are used on very few devices today, most of them are manufactured before 2015, like [Asus Zenfone 4](http://www.gsmarena.com/asus_zenfone_4-5951.php) and they take about 1% of all devices, according to the Device catalog on Google Play Console. Some x86 and x86_64 devices have ARM emulator, but running the _BlinkCard_ on the emulator will give a huge performance penalty.

There are some issues to be considered:

- ARMv7 build of the native library cannot be run on devices that do not have ARMv7 compatible processor
- ARMv7 processors do not understand x86 instruction set
- x86 processors understand neither ARM64 nor ARMv7 instruction sets
- some x86 android devices ship with the builtin [ARM emulator](http://commonsware.com/blog/2013/11/21/libhoudini-what-it-means-for-developers.html) - such devices are able to run ARM binaries but with a performance penalty. There is also a risk that the builtin ARM emulator will not understand some specific ARM instruction and will crash.
- ARM64 processors understand ARMv7 instruction set, but ARMv7 processors do not understand ARM64 instructions. 
    - <a name="64bitNotice"></a> **NOTE:** as of the year 2018, some android devices that ship with ARM64 processors do not have full compatibility with ARMv7. This is mostly due to incorrect configuration of Android's 32-bit subsystem by the vendor, however Google decided that as of August 2019 all apps on PlayStore that contain native code need to have native support for 64-bit processors (this includes ARM64 and x86_64) - this is in anticipation of future Android devices that will support 64-bit code **only**, i.e. that will have ARM64 processors that do not understand ARMv7 instruction set.
- if ARM64 processor executes ARMv7 code, it does not take advantage of modern NEON64 SIMD operations and does not take advantage of 64-bit registers it has - it runs in emulation mode
- x86_64 processors understand x86 instruction set, but x86 processors do not understand x86_64 instruction set
- if x86_64 processor executes x86 code, it does not take advantage of 64-bit registers and use two instructions instead of one for 64-bit operations

`LibBlinkCard.aar` archive contains ARMv7, ARM64, x86 and x86_64 builds of the native library. By default, when you integrate _BlinkCard_ into your app, your app will contain native builds for all these processor architectures. Thus, _BlinkCard_ will work on ARMv7, ARM64, x86 and x86_64 devices and will use ARMv7 features on ARMv7 devices and ARM64 features on ARM64 devices. However, the size of your application will be rather large.

## <a name="reduceSize"></a> Reducing the final size of your app

We recommend that you distribute your app using [App Bundle](https://developer.android.com/platform/technology/app-bundle). This will defer apk generation to Google Play, allowing it to generate minimal APK for each specific device that downloads your app, including only required processor architecture support.

### Using APK splits

If you are unable to use App Bundle, you can create multiple flavors of your app - one flavor for each architecture. With gradle and Android studio this is very easy - just add the following code to `build.gradle` file of your app:

```
android {
  ...
  splits {
    abi {
      enable true
      reset()
      include 'x86', 'armeabi-v7a', 'arm64-v8a', 'x86_64'
      universalApk true
    }
  }
}
```

With that build instructions, gradle will build four different APK files for your app. Each APK will contain only native library for one processor architecture and one APK will contain all architectures. In order for Google Play to accept multiple APKs of the same app, you need to ensure that each APK has different version code. This can easily be done by defining a version code prefix that is dependent on architecture and adding real version code number to it in following gradle script:

```
// map for the version code
def abiVersionCodes = ['armeabi-v7a':1, 'arm64-v8a':2, 'x86':3, 'x86_64':4]

import com.android.build.OutputFile

android.applicationVariants.all { variant ->
    // assign different version code for each output
    variant.outputs.each { output ->
        def filter = output.getFilter(OutputFile.ABI)
        if(filter != null) {
            output.versionCodeOverride = abiVersionCodes.get(output.getFilter(OutputFile.ABI)) * 1000000 + android.defaultConfig.versionCode
        }
    }
}
```

For more information about creating APK splits with gradle, check [this article from Google](https://developer.android.com/studio/build/configure-apk-splits.html#configure-abi-split).

After generating multiple APK's, you need to upload them to Google Play. For tutorial and rules about uploading multiple APK's to Google Play, please read the [official Google article about multiple APKs](https://developer.android.com/google/play/publishing/multiple-apks.html).

### Removing processor architecture support

If you won't be distributing your app via Google Play or for some other reasons want to have single APK of smaller size, you can completely remove support for certain CPU architecture from your APK. **This is not recommended due to [consequences](#archConsequences)**.

To keep only some CPU architectures, for example `armeabi-v7a` and `arm64-v8a`, add the following statement to your `android` block inside `build.gradle`:

```
android {
    ...
    ndk {
        // Tells Gradle to package the following ABIs into your application
        abiFilters 'armeabi-v7a', 'arm64-v8a'
    }
}
```

This will remove other architecture builds for **all** native libraries used by the application.

To remove support for a certain CPU architecture only for _BlinkCard_, add the following statement to your `android` block inside `build.gradle`:

```
android {
	...
	packagingOptions {
		exclude 'lib/<ABI>/libBlinkCard.so'
	}
}
```

where `<ABI>` represents the CPU architecture you want to remove:

- to remove ARMv7 support, use `exclude 'lib/armeabi-v7a/libBlinkCard.so'`
- to remove x86 support, use `exclude 'lib/x86/libBlinkCard.so'`
- to remove ARM64 support, use `exclude 'lib/arm64-v8a/libBlinkCard.so'`
    - **NOTE**: this is **not recommended**. See [this notice](#64bitNotice).
- to remove x86_64 support, use `exclude 'lib/x86_64/libBlinkCard.so'`

You can also remove multiple processor architectures by specifying `exclude` directive multiple times. Just bear in mind that removing processor architecture will have side effects on performance and stability of your app. Please read [this](#archConsequences) for more information.

### <a name="archConsequences"></a> Consequences of removing processor architecture

- Google decided that as of August 2019 all apps on Google Play that contain native code need to have native support for 64-bit processors (this includes ARM64 and x86_64). This means that you cannot upload application to Google Play Console that supports only 32-bit ABI and does not support corresponding 64-bit ABI.

- By removing ARMv7 support, _BlinkCard_ will not work on devices that have ARMv7 processors. 
- By removing ARM64 support, _BlinkCard_ will not use ARM64 features on ARM64 device
    - also, some future devices may ship with ARM64 processors that will not support ARMv7 instruction set. Please see [this note](#64bitNotice) for more information.
- By removing x86 support, _BlinkCard_ will not work on devices that have x86 processor, except in situations when devices have ARM emulator - in that case, _BlinkCard_ will work, but will be slow and possibly unstable
- By removing x86_64 support, _BlinkCard_ will not use 64-bit optimizations on x86_64 processor, but if x86 support is not removed, _BlinkCard_ should work


## <a name="combineNativeLibraries"></a> Combining _BlinkCard_ with other native libraries

If you are combining _BlinkCard_ library with other libraries that contain native code into your application, make sure you match the architectures of all native libraries. For example, if third party library has got only ARMv7 and x86 versions, you must use exactly ARMv7 and x86 versions of _BlinkCard_ with that library, but not ARM64. Using these architectures will crash your app at initialization step because JVM will try to load all its native dependencies in same preferred architecture and will fail with `UnsatisfiedLinkError`.
# <a name="troubleshoot"></a> Troubleshooting

### Integration difficulties

In case of problems with SDK integration, first make sure that you have followed [integration instructions](#androidStudioIntegration). If you're still having problems, please contact us at [help.microblink.com](http://help.microblink.com).

### Licensing issues

If you are getting "invalid license key" error or having other license-related problems (e.g. some feature is not enabled that should be or there is a watermark on top of camera), first check the ADB logcat. All license-related problems are logged to error log so it is easy to determine what went wrong.

When you have to determine what is the license-relate problem or you simply do not understand the log, you should contact us [help.microblink.com](http://help.microblink.com). When contacting us, please make sure you provide following information:

* exact package name of your app (from your `AndroidManifest.xml` and/or your `build.gradle` file)
* license that is causing problems
* please stress out that you are reporting problem related to Android version of _BlinkCard_ SDK
* if unsure about the problem, you should also provide excerpt from ADB logcat containing license error

**Keep in mind:** Versions 2.0.0 and above require an internet connection to work under our new License Management Program.

We’re only asking you to do this so we can validate your trial license key. Data extraction still happens offline, on the device itself.
Once the validation is complete, you can continue using the SDK in offline mode (or over a private network) until the next check. 

### Other problems

If you are having problems with scanning certain items, undesired behaviour on specific device(s), crashes inside _BlinkCard_ or anything unmentioned, please do as follows:

* enable logging to get the ability to see what is library doing. To enable logging, put this line in your application:

	```java
	com.microblink.blinkcard.util.Log.setLogLevel(com.microblink.blinkcard.util.Log.LogLevel.LOG_VERBOSE);
	```

	After this line, library will display as much information about its work as possible. Please save the entire log of scanning session to a file that you will send to us. It is important to send the entire log, not just the part where crash occurred, because crashes are sometimes caused by unexpected behaviour in the early stage of the library initialization.
	
* Contact us at [help.microblink.com](http://help.microblink.com) describing your problem and provide following information:
	* log file obtained in previous step
	* high resolution scan/photo of the item that you are trying to scan
	* information about device that you are using - we need exact model name of the device. You can obtain that information with any app like [this one](https://play.google.com/store/apps/details?id=ru.andr7e.deviceinfohw)
	* please stress out that you are reporting problem related to Android version of _BlinkCard_ SDK


# <a name="faq"></a> FAQ and known issues
#### <a name="featureNotSupportedByLicenseKey"></a> After switching from trial to production license I get `InvalidLicenseKeyException` when I construct specific `Recognizer` object

Each license key contains information about which features are allowed to use and which are not. This exception indicates that your production license does not allow using of specific `Recognizer` object. You should contact [support](http://help.microblink.com) to check if provided license is OK and that it really contains all features that you have purchased.

#### <a name="invalidLicenseKey"></a> I get `InvalidLicenseKeyException` with trial license key

Whenever you construct any `Recognizer` object or any other object that derives from [`Entity`](https://blinkcard.github.io/blinkcard-android/com/microblink/blinkcard/entities/Entity.html), a check whether license allows using that object will be performed. If license is not set prior constructing that object, you will get `InvalidLicenseKeyException`. We recommend setting license as early as possible in your app, ideally in `onCreate` callback of your [Application singleton](https://developer.android.com/reference/android/app/Application.html).

#### <a name="missingResources"></a> When my app starts, I get exception telling me that some resource/class cannot be found or I get `ClassNotFoundException`

This usually happens when you perform integration into [Eclipse project](#eclipseIntegration) and you forget to add resources or native libraries into the project. You must alway take care that same versions of both resources, assets, java library and native libraries are used in combination. Combining different versions of resources, assets, java and native libraries will trigger crash in SDK. This problem can also occur when you have performed improper integration of _BlinkCard_ SDK into your SDK. Please read how to [embed _BlinkCard_ inside another SDK](#embedAAR).

#### <a name="unsatisfiedLinkError"></a> When my app starts, I get `UnsatisfiedLinkError`

This error happens when JVM fails to load some native method from native library If performing integration [into Android studio](quickIntegration) and this error happens, make sure that you have correctly combined _BlinkCard_ SDK with [third party SDKs that contain native code](#combineNativeLibraries). If this error also happens in our integration sample apps, then it may indicate a bug in the SDK that is manifested on specific device. Please report that to our [support team](http://help.microblink.com).

#### <a name="lateMetadata1"></a> I've added my callback to `MetadataCallbacks` object, but it is not being called

Make sure that after adding your callback to `MetadataCallbacks` you have applied changes to `RecognizerRunnerView` or `RecognizerRunner` as described in [this section](#processingEventsImportantNote).

#### <a name="lateMetadata2"></a> I've removed my callback to `MetadataCallbacks` object, and now app is crashing with `NullPointerException`

Make sure that after removing your callback from `MetadataCallbacks` you have applied changes to `RecognizerRunnerView` or `RecognizerRunner` as described in [this section](#processingEventsImportantNote).

#### <a name="statefulRecognizer"></a> In my `onScanningDone` callback I have the result inside my `Recognizer`, but when scanning activity finishes, the result is gone

This usually happens when using `RecognizerRunnerView` and forgetting to pause the `RecognizerRunnerView` in your `onScanningDone` callback. Then, as soon as `onScanningDone` happens, the result is mutated or reset by additional processing that `Recognizer` performs in the time between end of your `onScanningDone` callback and actual finishing of the scanning activity. For more information about statefulness of the `Recognizer` objects, check [this section](#recognizerConcept).

#### <a name="transactionTooLarge"></a> I am using built-in activity to perform scanning and after scanning finishes, my app crashes with `IllegalStateException` stating `Data cannot be saved to intent because its size exceeds intent limit`.

This usually happens when you use `Recognizer` that produces image or similar large object inside its `Result` and that object exceeds the Android intent transaction limit. You should enable different intent data transfer mode. For more information about this, [check this section](#intentOptimization). Also, instead of using built-in activity, you can use [`RecognizerRunnerFragment` with built-in scanning overlay](#recognizerRunnerFragment).

#### <a name="transactionTooLarge2"></a> After scanning finishes, my app freezes

This usually happens when you attempt to transfer standalone `Result` that contains images or similar large objects via Intent and the size of the object exceeds Android intent transaction limit. Depending on the device, you will get either [TransactionTooLargeException](https://developer.android.com/reference/android/os/TransactionTooLargeException.html), a simple message `BINDER TRANSACTION FAILED` in log and your app will freeze or your app will get into restart loop. We recommend that you use `RecognizerBundle` and its API for sending `Recognizer` objects via Intent in a more safe manner ([check this section](#intentOptimization) for more information). However, if you really need to transfer standalone `Result` object (e.g. `Result` object obtained by cloning `Result` object owned by specific `Recognizer` object), you need to do that using global variables or singletons within your application. Sending large objects via Intent is not supported by Android.

#### <a name="directApiBadPerformance"></a> Scanning with a camera works better than a recognition of images by using the `Direct API`

When automatic scanning of camera frames with our camera management is used (provided camera overlays or direct usage of `RecognizerRunnerView`), we use a stream of video frames and send multiple images to the recognition to boost reading accuracy. Also, we perform frame quality analysis and combine scanning results from multiple camera frames. On the other hand, when you are using the Direct API with a single image per document side, we cannot combine multiple images. We do our best to extract as much information as possible from that image. In some cases, when the quality of the input image is not good enough, for example, when the image is blurred or when glare is present, we are not able to successfully read the document.

#### <a name="networkRequiredError"></a> I am getting a ‘Network required’ error when I'm on a private network

Online trial licenses require a public network access for validation purposes. See [Licensing issues](#licensing-issues).

#### <a name="ocrResultForbidden"></a> `onOcrResult()` method in my `OcrCallback` is never invoked and all `Result` objects always return `null` in their OCR result getters

In order to be able to obtain raw OCR result, which contains locations of each character, its value and its alternatives, you need to have a license that allows that. By default, licenses do not allow exposing raw OCR results in public API. If you really need that, please [contact us](https://help.microblink.com) and explain your use case.
# <a name="info"></a> Additional info

## <a name="size_report"></a> BlinkCard SDK size
You can find BlinkCard SDK size report for all supported ABIs [here](https://github.com/blinkcard/blinkcard-android/blob/master/size-report/sdk_size_report.md).

## <a name="api_reference"></a> API reference
Complete API reference can be found in [Javadoc](https://blinkcard.github.io/blinkcard-android).

## <a name="contact"></a> Contact
For any other questions, feel free to contact us at [help.microblink.com](http://help.microblink.com).

