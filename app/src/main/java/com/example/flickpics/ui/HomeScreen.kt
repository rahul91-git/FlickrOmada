package com.example.flickpics.ui

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.example.flickpics.R
import com.example.flickpics.models.PhotoUiModel
import com.example.flickpics.ui.viewmodel.PhotoViewModel

private const val GRID_CELLS = 3

@Composable
fun HomeScreen(viewModel: PhotoViewModel) {
    val state by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()

    var selectedPhoto by remember { mutableStateOf<PhotoUiModel?>(null) }

    // Fetch photos when the screen is first launched
    LaunchedEffect(Unit) {
        viewModel.fetchPhotos()
    }

    // Listen for scroll events to trigger pagination
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= state.photos.size - 5) {
                    // Trigger pagination when 5 items away from the end
                    viewModel.fetchPhotos()
                }
            }
    }

    Column {
        SearchBarComponent { query -> viewModel.searchPhotoByQuery(query) }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.search_bar_bottom_spacing)))

        if (state.isLoading && state.photos.isEmpty()) {
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
        } else {
            PhotoGrid(
                gridState = gridState,
                photos = state.photos,
                onClick = { selectedPhoto = it })
        }

        state.error?.let { error ->
            ErrorToastComponent(error)
        }

        selectedPhoto?.let { photo ->
            AlertDialog(
                onDismissRequest = { selectedPhoto = null },
                title = { Text(text = photo.title) },
                text = {
                    PhotoItemComponent(photo = photo, modifier = Modifier.fillMaxWidth())
                },
                confirmButton = {
                    TextButton(
                        onClick = { selectedPhoto = null }
                    ) {
                        Text(text = stringResource(R.string.close))
                    }
                }
            )
        }
    }
}

@Composable
private fun PhotoGrid(
    gridState: LazyGridState,
    photos: List<PhotoUiModel>,
    onClick: (PhotoUiModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(count = GRID_CELLS),
        state = gridState,
        contentPadding = PaddingValues(dimensionResource(R.dimen.grid_content_padding_all)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.image_padding_all)), // Vertical spacing between items
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.image_padding_all)),
        modifier = Modifier.fillMaxSize()
    ) {
        items(photos) { photo ->
            PhotoItemComponent(
                photo = photo,
                modifier = Modifier
                    .aspectRatio(1f) // This maintains a square aspect ratio
                    .border(dimensionResource(R.dimen.border_width), Color.Gray)
                    .clickable { onClick(photo) }
            )
        }
    }
}

@Composable
private fun ErrorToastComponent(error: String) =
    Toast.makeText(LocalContext.current, error, Toast.LENGTH_SHORT).show()


@Composable
private fun PhotoItemComponent(photo: PhotoUiModel, modifier: Modifier = Modifier) {
    AsyncImage(
        model = photo.imageUrl,
        contentDescription = null,
        modifier = modifier
    )
}


@Composable
fun SearchBarComponent(onSearch: (String?) -> Unit) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onSearch(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.search_bar_padding_all)),
        singleLine = true,
    )
}
