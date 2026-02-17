package com.microblink.blinkcard.sample.result

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.microblink.blinkcard.core.session.BlinkCardScanningResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlinkCardSampleResultScreen(
    result: BlinkCardScanningResult? = null,
    onNavigateUp: () -> Unit
) {
    BackHandler(enabled = true, onBack = onNavigateUp)
    if (result == null) {
        return
    }

    val resultItems = buildList {
        add(ResultItem("Issuing network", result.issuingNetwork))
        add(ResultItem("Cardholder name", result.cardholderName ?: "N/A"))
        add(ResultItem("IBAN", result.iban ?: "N/A"))
        add(ResultItem("Overall liveness", result.overallCardLivenessResult.name))

        result.cardAccounts.forEachIndexed { index, account ->
            add(ResultItem("Card account ${index + 1} number", account.cardNumber))
            add(ResultItem("Card account ${index + 1} number valid", account.cardNumberValid.toString()))
            add(ResultItem("Card account ${index + 1} prefix", account.cardNumberPrefix ?: "N/A"))
            add(ResultItem("Card account ${index + 1} expiry", account.expiryDate?.originalString ?: "N/A"))
            add(ResultItem("Card account ${index + 1} CVV", account.cvv ?: "N/A"))
            add(ResultItem("Card account ${index + 1} funding type", account.fundingType ?: "N/A"))
            add(ResultItem("Card account ${index + 1} card category", account.cardCategory ?: "N/A"))
            add(ResultItem("Card account ${index + 1} issuer", account.issuerName ?: "N/A"))
            add(ResultItem("Card account ${index + 1} issuer country", account.issuerCountry ?: "N/A"))
        }
    }

    val firstSideImage = result.firstSideResult?.cardImage?.bitmap
    val secondSideImage = result.secondSideResult?.cardImage?.bitmap

    Scaffold(
        topBar = { TopAppBar(title = { Text("BlinkCard Result") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(resultItems) { item ->
                ResultRow(item)
            }
            item {
                firstSideImage?.let { BitmapSection("First side image", it) }
                secondSideImage?.let { BitmapSection("Second side image", it) }
            }
        }
    }
}

@Composable
private fun ResultRow(item: ResultItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = item.value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    HorizontalDivider()
}

@Composable
private fun BitmapSection(title: String, bitmap: Bitmap) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = title,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private data class ResultItem(
    val title: String,
    val value: String
)
