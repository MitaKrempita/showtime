package com.example.showtime.presentation.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.showtime.domain.model.movie.MovieListItem
import com.example.showtime.infrastructure.api.login.ImageUrlRoute
import com.example.showtime.presentation.NavBar
import com.example.showtime.presentation.NavTab
import com.example.showtime.presentation.PrefetchImages
import com.example.showtime.presentation.cachedImageRequest

@Composable
fun MovieCollectionScreen(
    state: MovieCollectionState,
    onAction: (MovieCollectionIntent) -> Unit,
    onOpenMovie: (String) -> Unit
) {
    PrefetchImages(
        state.currentItems.map { movie ->
            state.imageConfig?.let { ImageUrlRoute.poster(it, movie.posterPath, "w185") }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(15, 15, 15, 255))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp)
        ) {
            Text(
                text = "Favorite / Watchlist",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)
            )

            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MovieCollectionType.entries.forEach { type ->
                    val selected = state.selectedType == type
                    Button(
                        onClick = { onAction(MovieCollectionIntent.SelectType(type)) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) Color(180, 0, 0, 255) else Color(48, 49, 56, 255),
                            contentColor = Color.White
                        )
                    ) {
                        Text(type.label)
                    }
                }
            }

            state.errorMessage?.let {
                Text(
                    text = it,
                    color = colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (state.currentItems.isEmpty() && !state.isLoading) {
                Text(
                    text = "No movies here.",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                items(state.currentItems) { movie ->
                    CollectionMovieCard(
                        movie = movie,
                        state = state,
                        onOpenMovie = onOpenMovie,
                        onRemove = { onAction(MovieCollectionIntent.RemoveMovie(movie.imdbId)) }
                    )
                }
            }
        }

        NavBar(
            currentTab = NavTab.GALLERY,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun CollectionMovieCard(
    movie: MovieListItem,
    state: MovieCollectionState,
    onOpenMovie: (String) -> Unit,
    onRemove: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenMovie(movie.imdbId) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(48, 49, 56, 255))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cachedImageRequest(state.imageConfig?.let { ImageUrlRoute.poster(it, movie.posterPath, "w185") }),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 74.dp, height = 110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(movie.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(movie.year?.toString() ?: "Unknown year", color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(255, 202, 40, 255),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${movie.imdbRating ?: "-"} IMDb", color = Color.White)
                }
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}
