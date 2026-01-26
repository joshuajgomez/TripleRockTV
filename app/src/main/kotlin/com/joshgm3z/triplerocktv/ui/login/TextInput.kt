package com.joshgm3z.triplerocktv.ui.login

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.triplerocktv.ui.common.TvPreview
import com.joshgm3z.triplerocktv.ui.theme.TripleRockTVTheme

@Composable
fun TextInput(
    modifier: Modifier = Modifier,
    text: String = "https://",
    label: String = "Server URL",
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onTextChange: (String) -> Unit = {}
) {
    OutlinedTextField(
        label = { Text(label) },
        value = text,
        enabled = enabled,
        singleLine = true,
        onValueChange = { onTextChange(it) },
        modifier = modifier,
        isError = isError,
        keyboardOptions = keyboardOptions,
        supportingText = {
            if (isError) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = colorScheme.error,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        "Please enter $label",
                        color = colorScheme.error,
                        fontSize = 15.sp
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colorScheme.onBackground,
            unfocusedTextColor = colorScheme.onBackground,
            focusedBorderColor = colorScheme.primary,
            errorTextColor = colorScheme.onBackground,
            disabledTextColor = colorScheme.onBackground.copy(alpha = 0.5f),
            disabledBorderColor = colorScheme.onBackground.copy(alpha = 0.3f),
        )
    )
}

@Composable
@TvPreview
fun TextInputPreview() {
    TripleRockTVTheme {
        TextInput()
    }
}

@Composable
@TvPreview
fun TextInputPreview_Error() {
    TripleRockTVTheme {
        TextInput(isError = true)
    }
}

@Composable
@TvPreview
fun TextInputPreview_Disabled() {
    TripleRockTVTheme {
        TextInput(enabled = false)
    }
}