package com.autobot.chromium.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autobot.chromium.R

@Composable
fun SearchBarBrowser(
    onAddTab: () -> Unit,
    onMenuClick: () -> Unit,
    searchBarText: String,
    onTextChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var query by rememberSaveable { mutableStateOf(searchBarText) }
    var isActive by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                if (isActive && query.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth().padding(bottom = 4.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Color.Gray)
                        .wrapContentHeight().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                        LazyColumn {
                            items(suggestions.size) { index ->
                                ItemSuggestions(
                                    suggestionText = suggestions[index],
                                    onSuggestionClick = {
                                        onSuggestionClick(it)
                                        query = it
                                        isActive = false
                                        keyboardController?.hide()
                                        onSearch(it)
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth().background(Color.Gray)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        CustomSearchBar(
                            hint = "Search",
                            isEnabled = true,
                            onTextChange = { text ->
                                query = text
                                onTextChange(text)
                                isActive = text.isNotEmpty()
                            },
                            onSearchClicked = {
                                onSearch(query)
                                isActive = false
                                keyboardController?.hide()
                            },
                            onFocusChange = {
                                onFocusChange(it)
                            }
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = onAddTab) {
                            Icon(Icons.Default.Add, contentDescription = "Add New Tab")
                        }

                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                        }
                    }
                }
            }
        }

    }
}
@Composable
fun CustomSearchBar(
    hint: String,
    modifier: Modifier = Modifier,
    isEnabled: (Boolean) = true,
    height: Dp = 42.dp,
    elevation: Dp = 42.dp,
    cornerShape: Shape = RoundedCornerShape(8.dp),
    backgroundColor: Color = Color.White,
    onSearchClicked: () -> Unit = {},
    onTextChange: (String) -> Unit = {},
    onFocusChange: (Boolean) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue()) }

    Row(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .shadow(elevation = elevation, shape = cornerShape)
            .background(color = backgroundColor, shape = cornerShape)
            .clickable { onSearchClicked() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = modifier
                .weight(1f)
                .size(36.dp)
                .background(color = Color.Transparent, shape = CircleShape)
                .clickable {
                    if (text.text.isNotEmpty()) {
                        text = TextFieldValue(text = "")
                        onTextChange("")
                    }
                },
        ) {
            if (text.text.isNotEmpty()) {
                Icon(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = stringResource(R.string.project_id),
                    tint = MaterialTheme.colorScheme.primary,
                )
            } else {
                Icon(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = stringResource(R.string.project_id),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        BasicTextField(
            modifier = modifier
                .weight(5f)
                .fillMaxWidth().onFocusChanged {
                   onFocusChange(it.isFocused)
                }
                .padding(4.dp),
            value = text,
            onValueChange = {
                text = it
                onTextChange(it.text)
            },
            enabled = isEnabled,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            decorationBox = { innerTextField ->
                if (text.text.isEmpty()) {
                    Text(
                        text = hint,
                        color = Color.Gray.copy(alpha = 0.5f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                innerTextField()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = { onSearchClicked() }),
            singleLine = true
        )

    }
}

@Preview
@Composable
fun SearchBarBrowserPreview() {
    SearchBarBrowser(
        onAddTab = {},
        onMenuClick = {},
        searchBarText = "Search",
        onTextChange = {},
        onSearch = {},
        suggestions = listOf("Jetpack Compose", "Android", "Compose Tutorial"),
        onSuggestionClick = {},
        onFocusChange = {}
    )
}
@Composable
fun ItemSuggestions(
    suggestionText: String,
    onSuggestionClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSuggestionClick(suggestionText) } // Trigger the callback when clicked
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_recent),
            contentDescription = "Autobot Browser",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = suggestionText,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}