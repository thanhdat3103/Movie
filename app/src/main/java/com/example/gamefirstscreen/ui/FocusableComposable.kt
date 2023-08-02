package com.example.gamefirstscreen.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun FocusableComposable(
    modifier: Modifier = Modifier,
    onFocus: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable (BoxScope.(Boolean) -> Unit)
) {
    var focused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
      modifier = modifier
          .onFocusChanged {
              focused = it.isFocused
              if (focused) onFocus?.invoke()
          }
          .focusTarget()
          .run {
              onClick?.let {
                  clickable (
                      interactionSource = interactionSource,
                      indication = null,
                      onClick = it,
                      enabled = true
                  )
              } ?: this
          },
    ) {
        content(focused)
    }
}