package com.grayseal.bookshelf.widgets

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**

Composable function that creates a single item for the BookShelf Navigation Drawer.
The item can have an optional icon, label, and badge to display.
The item can also be selected or not, which changes its background color and text color.
When clicked, it calls the provided onClick lambda function.
* @param label The label of the item, represented by a composable function.
* @param selected Whether the item is selected or not.
* @param onClick The lambda function that is called when the item is clicked.
* @param modifier Additional modifier to apply to the item.
* @param icon The icon of the item, represented by a composable function. Optional.
* @param badge The badge of the item, represented by a composable function. Optional.
* @param colors The colors of the item, represented by a NavigationDrawerItemColors object.
* @param interactionSource The InteractionSource to be used for this item.
 */

@Composable
@ExperimentalMaterial3Api
fun BookShelfNavigationDrawerItem(
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    badge: (@Composable () -> Unit)? = null,
    colors: NavigationDrawerItemColors = NavigationDrawerItemDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier
            .height(56.0.dp)
            .fillMaxWidth(),
        color = Color.Transparent,
        interactionSource = interactionSource,
    ) {
        Row(
            Modifier.padding(start = 0.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                val iconColor = colors.iconColor(selected).value
                CompositionLocalProvider(LocalContentColor provides iconColor, content = icon)
                Spacer(Modifier.width(12.dp))
            }
            Box(Modifier.weight(1f)) {
                val labelColor = colors.textColor(selected).value
                CompositionLocalProvider(LocalContentColor provides labelColor, content = label)
            }
            if (badge != null) {
                Spacer(Modifier.width(12.dp))
                val badgeColor = colors.badgeColor(selected).value
                CompositionLocalProvider(LocalContentColor provides badgeColor, content = badge)
            }
        }
    }
}