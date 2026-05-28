package dev.jskrzypczak.photovault.core.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light · Portrait", showBackground = true)
@Preview(name = "Dark · Portrait", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light · Landscape", showBackground = true, device = "spec:parent=pixel_6,orientation=landscape")
@Preview(name = "Dark · Landscape", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, device = "spec:parent=pixel_6,orientation=landscape")
annotation class PhonePreview
