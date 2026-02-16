package com.microblink.blinkcard.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.microblink.blinkcard.core.image.InputImage
import com.microblink.blinkcard.core.session.BlinkCardProcessResult
import com.microblink.blinkcard.core.session.BlinkCardSessionSettings
import com.microblink.blinkcard.core.session.InputImageSource
import com.microblink.blinkcard.core.settings.ScanningSettings
import com.microblink.blinkcard.sample.navigation.Destination
import com.microblink.blinkcard.sample.result.BlinkCardResultHolder
import com.microblink.blinkcard.sample.result.BlinkCardSampleResultScreen
import com.microblink.blinkcard.sample.ui.MainScreenDirectApi
import com.microblink.blinkcard.sample.ui.theme.BlinkCardTheme
import com.microblink.blinkcard.sample.utils.MainViewModel
import com.microblink.blinkcard.sample.utils.getBitmapFromAsset
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlinkCardTheme {
                MainNavHost(navController = rememberNavController())
            }
        }
    }

    @Composable
    fun MainNavHost(
        modifier: Modifier = Modifier,
        navController: NavHostController = rememberNavController(),
        startDestination: String = Destination.Main,
    ) {
        val context = LocalContext.current
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Destination.Main) {
                val mainState by viewModel.mainState.collectAsStateWithLifecycle()

                mainState.error?.let {
                    Toast.makeText(this@MainActivity, "Error: $it", Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }

                MainScreenDirectApi(
                    viewModel = viewModel,
                    launchCardScanning = {
                        viewModel.viewModelScope.launch {
                            viewModel.initializeLocalSdk(context)
                            viewModel.localSdk?.let { sdk ->
                                val session = sdk.createScanningSession(
                                    BlinkCardSessionSettings(
                                        inputImageSource = InputImageSource.Photo,
                                        scanningSettings = ScanningSettings()
                                    )
                                )

                                var result: Result<BlinkCardProcessResult>? = null
                                val front = getBitmapFromAsset(context, "test-images/front.jpg")
                                front?.let {
                                    result = session?.process(InputImage.createFromBitmap(front))
                                }

                                val back = getBitmapFromAsset(context, "test-images/back.jpg")
                                back?.let {
                                    result = session?.process(InputImage.createFromBitmap(back))
                                }

                                if (result != null && result!!.isSuccess) {
                                    viewModel.onDirectApiResultAvailable(session!!.getResult())
                                    navController.navigate(Destination.BlinkCardResult)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Direct API scan failed. Add test images to assets/test-images.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                )
            }

            composable(Destination.BlinkCardResult) {
                BlinkCardSampleResultScreen(
                    result = BlinkCardResultHolder.blinkCardResult,
                    onNavigateUp = {
                        viewModel.resetState()
                        navController.popBackStack(
                            route = Destination.Main,
                            inclusive = false
                        )
                    }
                )
            }
        }
    }
}
