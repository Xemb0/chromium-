package com.autobot.chromium.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.autobot.chromium.database.TabData

@Composable
 fun TabItem(
    tabData: TabData, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier, onCloseClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color.LightGray else Color.Gray

    Row(
        modifier = modifier.padding(top = if(isSelected) 8.dp else 4.dp)
            .clip(
                if (isSelected) {
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp) // Rounded corners for the selected tab
                } else {
                    RoundedCornerShape(8.dp) // Full rounded corners for non-selected tab
                }
            )// Padding for the selected tab
            .clickable { onClick() }
            .background(backgroundColor)
            .padding(vertical = 8.dp, horizontal = 8.dp), // Padding inside the tab
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically // Align content vertically to center
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Close Tab",
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp)) // Adjust icon size to fit the tab nicely
                .size(24.dp)
        )

        Text(
            text = tabData.url,
            modifier = Modifier
                .weight(1f) // Give the text space to fill as much as possible
                .padding(end = 8.dp),
            maxLines = 1, // Limit to 1 line to avoid overflow
            overflow = TextOverflow.Ellipsis // Ellipsis if the text is too long
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Tab",
                modifier = Modifier
                    .clickable { onCloseClick() }
                    .clip(RoundedCornerShape(24.dp)) // Adjust icon size to fit the tab nicely
                    .size(24.dp)
                    .background(Color.Gray.copy(alpha = .3f))
            )
        }
    }
}
