package com.example.showtime.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.showtime.domain.model.movie.ImageConfig
import com.example.showtime.domain.model.movie.Movie
import com.example.showtime.domain.model.movie.PersonSummary
import com.example.showtime.domain.model.movie.Video
import com.example.showtime.domain.model.movie.Image
import com.example.showtime.infrastructure.api.login.ImageUrlRoute
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.showtime.domain.repository.MovieRepository
import com.example.showtime.presentation.PrefetchImages
import com.example.showtime.presentation.cachedImageRequest
import org.koin.compose.koinInject

@Composable
fun MovieDetailsScreenRoute(
    movieId: String,
    onBack: () -> Unit
) {
    val repository = koinInject<MovieRepository>()
    val viewModel = viewModel(key = "movie-details-$movieId") {
        MovieDetailsViewModel(movieId, repository)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MovieDetailsScreen(
        uiState = uiState,
        onBack = onBack,
        onEvent = { viewModel.onEvent(it) }
    )
}

@Composable
fun MovieDetailsScreen(
    uiState: MovieDetailsUiState,
    onBack: () -> Unit,
    onEvent: (MovieDetailsEvent) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorScheme.secondary)
            }
        }

        uiState.errorMessage != null || uiState.movie == null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background)
                    .padding(24.dp)
                    .statusBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colorScheme.onBackground
                        )
                    }
                }
                Text(
                    text = uiState.errorMessage ?: "Movie not found",
                    color = colorScheme.onBackground
                )
                Button(onClick = { onEvent(MovieDetailsEvent.Retry) }) {
                    Text("Retry")
                }
            }
        }

        else -> {
            val movie = uiState.movie
            val imageConfig = uiState.imageConfig
            val backdropUrls = buildBackdropUrls(movie, imageConfig, uiState)
            PrefetchImages(backdropUrls + imageConfig?.let { ImageUrlRoute.poster(it, movie.posterPath, "w185") })
            val trailerUrl = uiState.trailer?.key?.let { "https://www.youtube.com/watch?v=$it" }
            val pagerState = rememberPagerState(pageCount = { backdropUrls.size.coerceAtLeast(1) })

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(364.dp)
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(285.dp)
                        ) { page ->
                            AsyncImage(
                                model = cachedImageRequest(backdropUrls.getOrNull(page)),
                                contentDescription = movie.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(colorScheme.primary.copy(alpha = 0.16f))
                            )
                        }

                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(12.dp)
                                .background(colorScheme.onPrimary.copy(alpha = 0.7f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colorScheme.onBackground
                            )
                        }

                        if (trailerUrl != null) {
                            FilledIconButton(
                                onClick = { uriHandler.openUri(trailerUrl) },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(64.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play trailer",
                                    modifier = Modifier.size(34.dp),
                                    tint = Color.Red
                                )
                            }
                        }

                        AsyncImage(
                            model = cachedImageRequest(imageConfig?.let { ImageUrlRoute.poster(it, movie.posterPath, "w185") }),
                            contentDescription = movie.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(width = 120.dp, height = 180.dp)
                                .align(Alignment.BottomStart)
                                .clip(RoundedCornerShape(18.dp))
                                .background(colorScheme.primary.copy(alpha = 0.18f))
                                .shadow(elevation = 20.dp, spotColor = Color.Black)
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 152.dp, end = 16.dp, bottom = 20.dp)
                        ) {
                            Text(
                                text = movie.title,
                                color = colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = buildMetadataLine(movie),
                                color = colorScheme.onBackground.copy(alpha = 0.72f)
                            )
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(255, 202, 40, 255)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "IMDb ${movie.imdbRating ?: "-"} (${movie.imdbVotes ?: 0} votes)",
                                color = colorScheme.onBackground
                            )
                        }

                        Text(
                            text = "TMDB ${movie.tmdbRating ?: "-"}",
                            color = colorScheme.onBackground
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onEvent(MovieDetailsEvent.ToggleFavorite) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (movie.isFavorite) Color(180, 0, 0, 255) else Color(48, 49, 56, 255),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = if (movie.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (movie.isFavorite) "Favorite" else "Add Favorite")
                            }

                            Button(
                                onClick = { onEvent(MovieDetailsEvent.ToggleWatchlist) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (movie.isOnWatchlist) Color(180, 0, 0, 255) else Color(48, 49, 56, 255),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WatchLater,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (movie.isOnWatchlist) "Watchlist" else "Add Watchlist")
                            }
                        }

                        uiState.actionMessage?.let {
                            Text(
                                text = it,
                                color = colorScheme.error
                            )
                        }

                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            movie.genres.forEach { genre ->
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = colorScheme.secondary.copy(alpha = 0.85f),
                                    contentColor = colorScheme.onSecondary
                                ) {
                                    Text(
                                        text = genre.name,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        if (!movie.overview.isNullOrBlank()) {
                            Text(
                                text = "Overview",
                                color = colorScheme.onBackground,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = movie.overview,
                                color = colorScheme.onBackground.copy(alpha = 0.72f)
                            )
                        }

                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailBadge("Budget", movie.budget?.let(::formatMoney) ?: "N/A")
                            DetailBadge("Revenue", movie.revenue?.let(::formatMoney) ?: "N/A")
                            DetailBadge("Language", movie.languageCode ?: "N/A")
                            DetailBadge("Popularity", movie.popularity?.toString() ?: "N/A")
                        }
                    }
                }

                if (backdropUrls.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Images",
                                color = colorScheme.onBackground,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                items(backdropUrls.take(5)) { imageUrl ->
                                    AsyncImage(
                                        model = cachedImageRequest(imageUrl),
                                        contentDescription = movie.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(width = 240.dp, height = 140.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(colorScheme.primary.copy(alpha = 0.12f))
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Cast",
                        color = colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                items(uiState.cast) { person ->
                    CastRow(
                        person = person,
                        imageConfig = imageConfig
                    )
                }
            }
        }
    }
}

@Composable
private fun CastRow(
    person: PersonSummary,
    imageConfig: ImageConfig?
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cachedImageRequest(imageConfig?.let { ImageUrlRoute.profile(it, person.profilePath, "w185") }),
                contentDescription = person.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(colorScheme.secondary.copy(alpha = 0.15f))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = person.name,
                    color = colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = person.department ?: person.professions ?: "Cast",
                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
                )
            }
        }
    }
}

@Composable
private fun DetailBadge(
    label: String,
    value: String
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = label,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
            )
            Text(
                text = value,
                color = colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun buildBackdropUrls(
    movie: Movie,
    imageConfig: ImageConfig?,
    uiState: MovieDetailsUiState
): List<String> {
    if (imageConfig == null) return emptyList()

    val fromGallery = uiState.backdrops.mapNotNull { image ->
        ImageUrlRoute.backdrop(imageConfig, image.filePath, "w780")
    }

    if (fromGallery.isNotEmpty()) return fromGallery

    return listOfNotNull(
        ImageUrlRoute.poster(imageConfig, movie.posterPath, "w185"),
        ImageUrlRoute.backdrop(imageConfig, movie.backdropPath, "w780")
    )
}

private fun buildMetadataLine(movie: Movie): String =
    listOfNotNull(
        movie.year?.toString(),
        movie.runtime?.let { "$it min" }
    ).joinToString(" - ")

private fun formatMoney(value: Long): String = "$" + value.toString()
