package com.example.showtime.presentation.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.showtime.domain.model.movie.ImageConfig
import com.example.showtime.domain.model.movie.MovieListItem
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.showtime.infrastructure.api.login.ImageUrlRoute
import com.example.showtime.presentation.PrefetchImages
import com.example.showtime.presentation.cachedImageRequest
import com.example.showtime.presentation.NavBar
import com.example.showtime.presentation.NavTab

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainScreen(
    state: MainScreenUiState,
    onAction: (MainScreenIntent) -> Unit,
    onOpenFilters: () -> Unit,
    onOpenMovie: (String) -> Unit
) {
    PrefetchImages(
        state.movies.map { movie ->
            state.imageConfig?.let { ImageUrlRoute.poster(it, movie.posterPath, "w185") }
        }
    )

    Column(
        modifier = Modifier.background(state.onPrimary).fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = state.primary, shape = RectangleShape)
                .statusBarsPadding()
                .clip(RoundedCornerShape(percent = 100))
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                tint = state.contentTextColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Premiere",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
                color = state.contentTextColor,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold
            )
            IconToggleButton(
                checked = !state.isDarkMode,
                onCheckedChange = { onAction(MainScreenIntent.ToggleTheme) }
            ) {
                Icon(
                    imageVector = if (!state.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = null,
                    tint = state.contentTextColor
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onOpenFilters,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = state.secondary,
                    contentColor = state.onSecondary
                )
            ) {
                val badge = if (state.activeFilterCount > 0) " (${state.activeFilterCount})" else ""
                Text("Filter$badge")
            }

            Spacer(modifier = Modifier.weight(1f))

            Box {
                Button(
                    onClick = { onAction(MainScreenIntent.ToggleSortPicker) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.secondary,
                        contentColor = colorScheme.onSecondary
                    )
                ) {
                    Text("Sort: ${state.sortOption.label}")
                }
                DropdownMenu(
                    expanded = state.isSortPickerOpen,
                    onDismissRequest = { onAction(MainScreenIntent.ToggleSortPicker) }
                ) {
                    MovieSortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = { onAction(MainScreenIntent.ChangeSort(option)) }
                        )
                    }
                }
            }

            Box {
                Button(
                    onClick = { onAction(MainScreenIntent.ToggleSortOrderPicker) },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.secondary,
                        contentColor = colorScheme.onSecondary
                    )
                ) {
                    Icon(
                        imageVector = if (state.isSortOrderPickerOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = state.isSortOrderPickerOpen,
                    onDismissRequest = { onAction(MainScreenIntent.ToggleSortOrderPicker) }
                ) {
                    SortOrder.entries.forEach { order ->
                        DropdownMenuItem(
                            text = { Text(order.label) },
                            onClick = { onAction(MainScreenIntent.ChangeSortOrder(order)) }
                        )
                    }
                }
            }
        }

        Text(
            text = "${state.totalMovies} movies",
            color = state.contentTextColor,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            when {
                state.isLoading -> items(6) {
                    LoadingMovieCard(
                        cardColor = state.primary,
                        indicatorColor = state.secondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                state.errorMessage != null -> item {
                    Text(
                        text = state.errorMessage,
                        color = state.contentTextColor,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                state.isEmpty -> item {
                    Text(
                        text = "No movies found",
                        color = state.contentTextColor,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> items(state.movies) { movie ->
                    MovieCard(
                        movie = movie,
                        imageConfig = state.imageConfig,
                        textColor = state.contentTextColor,

                        mutedTextColor = state.mutedTextColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        onClick = { onOpenMovie(movie.imdbId) }
                    )
                }
            }
            if (state.canLoadMore || state.isLoadingMore) {
                item {
                    LaunchedEffect(state.currentPage, state.canLoadMore) {
                        if (state.canLoadMore) {
                            onAction(MainScreenIntent.LoadNextPage)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = state.secondary
                        )
                    }
                }
            }
        }
        NavBar(currentTab = NavTab.MOVIES)
    }
}

@Composable
private fun LoadingMovieCard(
    cardColor: Color,
    indicatorColor: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .size(width = 92.dp, height = 138.dp)
                    .background(indicatorColor.copy(alpha = 0.18f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = indicatorColor
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SkeletonLine(color = indicatorColor, widthFraction = 0.9f)
                SkeletonLine(color = indicatorColor, widthFraction = 0.35f)
                SkeletonLine(color = indicatorColor, widthFraction = 0.6f)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SkeletonChip(color = indicatorColor)
                    SkeletonChip(color = indicatorColor)
                    SkeletonChip(color = indicatorColor)
                }
            }
        }
    }
}

@Composable
private fun SkeletonLine(color: Color, widthFraction: Float) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(14.dp)
            .background(color.copy(alpha = 0.20f), RoundedCornerShape(14.dp))
    )
}

@Composable
private fun SkeletonChip(color: Color) {
    Spacer(
        modifier = Modifier
            .size(width = 58.dp, height = 24.dp)
            .background(color.copy(alpha = 0.18f), RoundedCornerShape(14.dp))
    )
}
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MovieCard(
    movie: MovieListItem,
    imageConfig: ImageConfig?,
    textColor: Color,

    mutedTextColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.primary),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            AsyncImage(
                model = cachedImageRequest(imageConfig?.let { ImageUrlRoute.poster(it, movie.posterPath, "w185") }),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 92.dp, height = 138.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorScheme.secondary.copy(alpha = 0.18f))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(movie.title, color = textColor, fontWeight = FontWeight.Bold)
                Text(movie.year?.toString() ?: "Unknown year", color = mutedTextColor)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(255, 202, 40, 255),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${movie.imdbRating ?: "-"} IMDb", color = textColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${movie.imdbVotes ?: 0} votes", color = mutedTextColor)
                }
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    movie.genres.forEach { genre ->
                        GenreChip(genre.name)
                    }
                }
            }
        }
    }
}

@Composable
private fun GenreChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = colorScheme.secondary.copy(alpha = 0.85f),
        contentColor = colorScheme.onSecondary
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
