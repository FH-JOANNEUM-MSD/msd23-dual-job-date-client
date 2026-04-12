package fh.msd.jobdating.core.ui.components.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
expect fun PlatformDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
        dismissOnClickOutside = true,
        dismissOnBackPress = true
    ),
    content: @Composable () -> Unit
)