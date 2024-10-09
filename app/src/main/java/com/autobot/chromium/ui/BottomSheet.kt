package com.autobot.chromium.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autobot.chromium.R
import com.autobot.chromium.theme.MyAppThemeColors

@Composable
fun BottomSheetContent(onBottomSheetOptionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .background(
                color = MyAppThemeColors.current.tertiaryDark,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 4.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // First Row of icons
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconColumn(imageVector = Icons.Outlined.Star, label = "Bookmarks", onOptionClick = {
                onBottomSheetOptionClick(it)})
            IconColumn(painter = painterResource(id = R.drawable.ic_download), label = "Download", onOptionClick = {
                onBottomSheetOptionClick(it)})
            IconColumn(painter = painterResource(id = R.drawable.ic_recent), label = "Recent", onOptionClick = {
                onBottomSheetOptionClick(it)})
            IconColumn(painter = painterResource(id = R.drawable.ic_settings), label = "Settings", onOptionClick = {
                onBottomSheetOptionClick(it)})
        }

        androidx.compose.material3.HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MyAppThemeColors.current.myText
        )
        // Second Row of icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconColumn(painter = painterResource(id = R.drawable.ic_exit), label = "Exit", onOptionClick = {
                onBottomSheetOptionClick(it)})
            IconColumn(painter = painterResource(id = R.drawable.ic_incognito), label = "Incognito", onOptionClick = {
                onBottomSheetOptionClick(it)})
            IconColumn(painter = painterResource(id = R.drawable.ic_share), label = "Share", onOptionClick = {
                onBottomSheetOptionClick(it)})
            IconColumn(painter = painterResource(id = R.drawable.ic_dark_mode), label = "Dark M", onOptionClick = {
                onBottomSheetOptionClick(it)})
        }

        // Third Row of icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconColumn(imageVector = Icons.Default.Close, label = "Close", onOptionClick = {
                onBottomSheetOptionClick(it)
            })
            IconColumn(painter = painterResource(id = R.drawable.ic_share), label = "Share", onOptionClick = {
                onBottomSheetOptionClick(it)})
            IconColumn(painter = painterResource(id = R.drawable.ic_help_feedback), label = "Help", onOptionClick = {
                onBottomSheetOptionClick(it)})
        }
    }
}

@Composable
fun IconColumn(
    imageVector: ImageVector? = null,
    painter: Painter? = null,
    label: String,
    modifier: Modifier = Modifier,
    onOptionClick: (String) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .padding(vertical = 4.dp
    )
        .clip(RoundedCornerShape(24.dp))
        .clickable { onOptionClick(label) }
        .wrapContentSize(Alignment.Center)
        .padding(8.dp)
    ) {
        val maxWidth = this.maxWidth

        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = label,
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )
            } else if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}
@Preview
@Composable
fun IconGrid() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconColumn(imageVector = Icons.Default.Home, label = "Home", onOptionClick = {
            it})
        IconColumn(painter = painterResource(id = R.drawable.ic_share), label = "Settings", onOptionClick = {
            it})
        IconColumn(imageVector = Icons.Default.Favorite, label = "Favorites", onOptionClick = {
            it})
    }
}