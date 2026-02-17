package com.microblink.blinkcard.sample.result

import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.microblink.blinkcard.core.session.BlinkCardScanningResult
import com.microblink.blinkcard.sample.ui.theme.Cobalt
import com.microblink.blinkcard.sample.ui.theme.Cobalt400
import com.microblink.blinkcard.sample.ui.theme.Cobalt50
import com.microblink.blinkcard.sample.ui.theme.Cobalt800
import com.microblink.blinkcard.sample.ui.theme.DeepBlue
import com.microblink.blinkcard.sample.ui.theme.ErrorRed

@Composable
fun BlinkCardSampleResultScreen(
    result: BlinkCardScanningResult? = null,
    onNavigateUp: () -> Unit
) {
    result?.let { scanningResult ->
        val fullResult = buildList {
            add(SampleResult("Issuing network", value = scanningResult.issuingNetwork))
            add(SampleResult("Cardholder name", value = scanningResult.cardholderName))
            add(SampleResult("IBAN", value = scanningResult.iban))
            add(SampleResult("Overall liveness", value = scanningResult.overallCardLivenessResult.name))
            add(SampleResult("Card accounts count", value = scanningResult.cardAccounts.size.toString()))
        }
        val cardAccountTabs = scanningResult.cardAccounts.mapIndexed { index, account ->
            SampleResultTab(
                title = "Card account ${index + 1}",
                results = listOf(
                    SampleResult("Card number", value = account.cardNumber),
                    SampleResult("Card number valid", value = account.cardNumberValid.toString()),
                    SampleResult("Card number prefix", value = account.cardNumberPrefix),
                    SampleResult(
                        "Expiry date",
                        children = listOf(
                            SampleResult("Original string", value = account.expiryDate?.originalString),
                            SampleResult(
                                "Filled by domain knowledge",
                                value = account.expiryDate?.filledByDomainKnowledge?.toString()
                            ),
                            SampleResult(
                                "Successfully parsed",
                                value = account.expiryDate?.successfullyParsed?.toString()
                            )
                        )
                    ),
                    SampleResult("CVV", value = account.cvv),
                    SampleResult("Funding type", value = account.fundingType),
                    SampleResult("Card category", value = account.cardCategory),
                    SampleResult("Issuer", value = account.issuerName),
                    SampleResult("Issuer country", value = account.issuerCountry)
                )
            )
        }
        val imageTabItems = buildList {
            scanningResult.firstSideResult?.cardImage?.bitmap?.let {
                add(SampleResultImage("First side image", image = it))
            }
            scanningResult.secondSideResult?.cardImage?.bitmap?.let {
                add(SampleResultImage("Second side image", image = it))
            }
        }
        val resultTabs = buildList {
            add(SampleResultTab(title = "Full result", results = fullResult))
            addAll(cardAccountTabs)
            if (imageTabItems.isNotEmpty()) {
                add(SampleResultTab(title = "Images", results = imageTabItems))
            }
        }
        SampleResultScreen(
            resultTabs = resultTabs,
            onNavigateUp = onNavigateUp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SampleResultScreen(
    resultTabs: List<SampleResultTab>,
    onNavigateUp: () -> Unit
) {
    BackHandler(true, onNavigateUp)
    var activeTab by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("BlinkCard Sample", style = TestMenuTitleText)
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = Cobalt800,
                    titleContentColor = Color.White
                )
            )
        },
        content = { padding ->
            Surface(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding())
                    .fillMaxSize(),
                color = Cobalt50
            ) {
                Column {
                    FlowRow(
                        Modifier
                            .fillMaxWidth()
                            .background(White)
                    ) {
                        resultTabs.forEachIndexed { num, tab ->
                            tab.title?.let { title ->
                                Column(
                                    Modifier
                                        .padding(4.dp)
                                        .border(1.dp, DeepBlue, RoundedCornerShape(40.dp))
                                        .clip(RoundedCornerShape(40.dp))
                                        .background(if (activeTab == num) ErrorRed else White)
                                        .clickable { activeTab = num }
                                        .padding(10.dp)
                                ) {
                                    Text(title, color = Cobalt800, style = ResultValueText)
                                }
                            }
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Cobalt50)
                    ) {
                        resultTabs[activeTab].results.let { results ->
                            if (results.isNotEmpty()) {
                                items(items = results, itemContent = {
                                    SampleResultRow(
                                        node = it,
                                        level = 0,
                                        showDivider = true
                                    )
                                })
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SampleResultRow(
    node: Result,
    level: Int,
    showDivider: Boolean = true
) {
    var expanded by rememberSaveable {
        mutableStateOf(true)
    }
    Column(
        modifier = Modifier.animateContentSize(
            spring(
                stiffness = Spring.StiffnessLow,
                visibilityThreshold = IntSize.VisibilityThreshold,
                dampingRatio = Spring.DampingRatioNoBouncy
            )
        )
    ) {
        if (node is SampleResult) {
            SampleResultRowText(
                result = node,
                level = level,
                isExpanded = expanded,
                showDivider = showDivider,
                onClick = {
                    if (!node.children.isNullOrEmpty()) {
                        expanded = !expanded
                    }
                }
            )
            if (expanded) {
                node.children?.forEach {
                    SampleResultRow(
                        node = it,
                        level = level + 1
                    )
                }
            }
        } else if (node is SampleResultImage) {
            SampleResultRowImage(
                result = node,
                level = level,
                showDivider = showDivider
            )
        }
    }
}

@Composable
fun SampleResultRowText(
    result: SampleResult,
    level: Int,
    isExpanded: Boolean,
    showDivider: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .background(if (level == 0) Cobalt50 else Color.White)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .padding(start = (10 * level).dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(0.85f)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = result.title.toString().uppercase(),
                style = ResultTitleText,
                color = if (level == 0) Cobalt800 else if (level == 1) Cobalt else Cobalt400
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                result.value?.let {
                    Text(
                        text = it,
                        style = ResultValueText,
                        color = Black
                    )
                }
            }
        }
        result.children?.let {
            val chevronRotation by animateFloatAsState(
                targetValue = if (isExpanded) 90f else 0f,
                label = ""
            )
            Column(
                modifier = Modifier.weight(0.15f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 16.dp)
                        .rotate(chevronRotation),
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null
                )
            }
        }
    }
    if (showDivider) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = DeepBlue,
            thickness = if (level == 0) 2.dp else 1.dp
        )
    }
}

@Composable
fun SampleResultRowImage(
    result: SampleResultImage,
    level: Int,
    showDivider: Boolean = true
) {
    Row(
        modifier = Modifier
            .background(if (level == 0) Cobalt50 else Color.White)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .padding(start = (10 * level).dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = result.title.toString().uppercase(),
                style = ResultTitleText,
                color = if (level == 0) Cobalt800 else if (level == 1) Cobalt else Cobalt400
            )
            result.image?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = result.title,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    if (showDivider) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = DeepBlue,
            thickness = if (level == 0) 2.dp else 1.dp
        )
    }
}

sealed class Result {
    abstract val title: String?
    abstract val children: List<SampleResult>?
    abstract val description: String?
}

data class SampleResult(
    override val title: String?,
    override val children: List<SampleResult>? = null,
    override val description: String? = null,
    val value: String? = null
) : Result()

data class SampleResultImage(
    override val title: String?,
    override val children: List<SampleResult>? = null,
    override val description: String? = null,
    val image: Bitmap? = null
) : Result()

data class SampleResultTab(
    val title: String?,
    val results: List<Result>
)

val TestMenuTitleText = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 20.sp,
    lineHeight = 24.sp
)

val ResultTitleText = TextStyle(
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp
)

val ResultValueText = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = 20.sp,
    lineHeight = 24.sp
)
