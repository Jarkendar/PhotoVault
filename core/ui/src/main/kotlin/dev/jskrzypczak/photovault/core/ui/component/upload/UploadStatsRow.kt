package dev.jskrzypczak.photovault.core.ui.component.upload

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jskrzypczak.photovault.core.ui.preview.PhonePreview
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme

@Composable
fun UploadStatsRow(
    uploadingCount: Int,
    queuedCount: Int,
    doneCount: Int,
    uploadingLabel: String,
    queuedLabel: String,
    doneLabel: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        UploadStatCard(
            count = uploadingCount,
            label = uploadingLabel,
            modifier = Modifier.weight(1f),
        )
        UploadStatCard(
            count = queuedCount,
            label = queuedLabel,
            modifier = Modifier.weight(1f),
        )
        UploadStatCard(
            count = doneCount,
            label = doneLabel,
            countColor = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun UploadStatCard(
    count: Int,
    label: String,
    modifier: Modifier = Modifier,
    countColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = countColor,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PhonePreview
@Composable
private fun UploadStatsRowPreview() {
    PhotoVaultTheme {
        UploadStatsRow(
            uploadingCount = 2,
            queuedCount = 3,
            doneCount = 127,
            uploadingLabel = "Przesyłanie",
            queuedLabel = "W kolejce",
            doneLabel = "Gotowe",
        )
    }
}
