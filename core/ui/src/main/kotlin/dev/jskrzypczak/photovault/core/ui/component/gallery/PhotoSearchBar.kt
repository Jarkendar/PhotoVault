package dev.jarkendar.photovault.core.ui.component.gallery

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jarkendar.photovault.core.ui.R
import dev.jarkendar.photovault.core.ui.preview.PhonePreview
import dev.jarkendar.photovault.core.ui.theme.PhotoVaultTheme

@Composable
fun PhotoSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.core_ui_search_hint)) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = stringResource(R.string.core_ui_filter),
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
        modifier = modifier.fillMaxWidth(),
    )
}

@PhonePreview
@Composable
private fun PhotoSearchBarEmptyPreview() {
    PhotoVaultTheme {
        PhotoSearchBar(
            query = "",
            onQueryChange = {},
            onFilterClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@PhonePreview
@Composable
private fun PhotoSearchBarFilledPreview() {
    PhotoVaultTheme {
        PhotoSearchBar(
            query = "zachód słońca",
            onQueryChange = {},
            onFilterClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
