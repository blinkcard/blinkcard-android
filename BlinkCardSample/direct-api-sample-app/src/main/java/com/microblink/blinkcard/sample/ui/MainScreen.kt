package com.microblink.blinkcard.sample.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.microblink.blinkcard.sample.R
import com.microblink.blinkcard.sample.ui.theme.Cobalt
import com.microblink.blinkcard.sample.utils.MainViewModel

@Composable
fun MainScreenDirectApi(
    viewModel: MainViewModel,
    launchCardScanning: () -> Unit
) {
    val mainState by viewModel.mainState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = launchCardScanning
            ) {
                Text(text = stringResource(R.string.btn_launch))
            }
        }

        if (mainState.displayLoading) {
            LoadingDialog()
        }
    }
}

@Composable
private fun LoadingDialog() {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.Center)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp),
                color = Cobalt
            )
        }
    }
}
