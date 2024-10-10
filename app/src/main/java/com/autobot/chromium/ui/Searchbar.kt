package com.autobot.chromium.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autobot.chromium.R

@Composable
fun SearchBarBrowser(
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    onReload: () -> Unit,
    onAddTab: () -> Unit,
    onMenuClick: () -> Unit,
    onSearch: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isActive by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Display suggestions when the search bar is active and has text.
        if (isActive && textFieldValue.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
            ) {
                items(suggestions.size) { index ->
                    ItemSuggestions(
                        suggestionText = suggestions[index],
                        onSuggestionClick = {
                            onSuggestionClick(it)
                            isActive = false
                            keyboardController?.hide()
                            onSearch(it)
                        }
                    )
                }
            }
        }

        // Search bar with icons for adding a tab and showing more options.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Make the search bar take up the remaining space.
            CustomSearchBar(
                textFieldValue = textFieldValue,
                onTextFieldValueChange = {
                    onTextFieldValueChange(it)
                    isActive = it.isNotEmpty()
                },
                onSearchClicked = {
                    onSearch(textFieldValue)
                    isActive = false
                    keyboardController?.hide()
                },
                onFocusChange = onFocusChange,
                modifier = Modifier.weight(1f) // Makes the search bar take up available space.
            )

            // Icon for adding a new tab.
            IconButton(onClick = onAddTab) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add New Tab",
                    tint = Color.White
                )
            }

            // Icon for more options.
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun CustomSearchBar(
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 42.dp,
    elevation: Dp = 4.dp,
    cornerShape: Shape = RoundedCornerShape(8.dp),
    backgroundColor: Color = Color.White,
) {
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .shadow(elevation = elevation, shape = cornerShape)
            .background(color = backgroundColor, shape = cornerShape)
            .padding(horizontal = 16.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                onFocusChange(focusState.isFocused)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = stringResource(R.string.project_id),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = textFieldValue,
            onValueChange = onTextFieldValueChange,
            modifier = modifier
                .onFocusChanged {
                    isFocused = it.isFocused
                    onFocusChange(it.isFocused)
                }
                .padding(horizontal = 0.dp, vertical = 12.dp)
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .fillMaxWidth(),
            textStyle = TextStyle(
                color = if (isFocused) Color.Black else Color.Gray.copy(alpha = 0.9f),
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked()
                    keyboardController?.hide()
                    isFocused = false
                }
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (textFieldValue.isEmpty() && !isFocused) {
                        Text(
                            text = "Search...",
                            style = TextStyle(color = Color.Gray.copy(alpha = 0.6f), fontSize = 16.sp)
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun ItemSuggestions(
    suggestionText: String,
    onSuggestionClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSuggestionClick(suggestionText) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_recent),
            contentDescription = "Autobot Browser",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = suggestionText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )
    }
}

@Preview
@Composable
fun SearchBarBrowserPreview() {
    SearchBarBrowser(
        textFieldValue = "https://stevdza-san.com",
        onTextFieldValueChange = {},
        onReload = {},
        onAddTab = {},
        onMenuClick = {},
        onSearch = {},
        suggestions = listOf("https://stevdza-san.com", "https://www.google.com"),
        onSuggestionClick = {},
        onFocusChange = {}
    )
}