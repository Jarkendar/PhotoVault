package dev.jskrzypczak.photovault.feature.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.feature.settings.AccentColor

// TODO(etap-8+): move this palette to core/ui theme/Color.kt and apply via PhotoVaultTheme.
private val accentPalette: Map<AccentColor, Color> = mapOf(
    AccentColor.PURPLE to Color(0xFF6650A4),
    AccentColor.TEAL to Color(0xFF00897B),
    AccentColor.GREEN to Color(0xFF2E7D32),
)

@Composable
internal fun AccentColorPicker(
    selected: AccentColor,
    onSelect: (AccentColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        AccentColor.entries.forEachIndexed { index, accent ->
            if (index > 0) Spacer(Modifier.width(12.dp))
            val color = accentPalette[accent] ?: Color.Gray
            val isSelected = accent == selected
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .then(
                        if (isSelected) {
                            Modifier.border(3.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        } else Modifier
                    )
                    .clickable { onSelect(accent) },
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
