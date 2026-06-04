package com.example.showtime.presentation.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.showtime.presentation.mainScreen.MovieFilters

@Composable
fun FilterScreen(
    state: FilterScreenUiState,
    onAction: (FilterScreenEvent) -> Unit,
    onBack: () -> Unit,
    onApply: (MovieFilters) -> Unit
) {
    val screenBackground = Color(15, 15, 15, 255)
    val screenText  = Color.White
    val secondaryColor = Color(180, 0, 0, 255)
    val onSecondaryColor =  Color.White
    val primaryColor =  Color(48, 49, 56, 255)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = screenText
                )
            }
            Text(
                text = "Filter Movies",
                color = screenText,
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedTextField(
            value = state.query,
            onValueChange = { onAction(FilterScreenEvent.UpdateQuery(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = screenText,
                unfocusedTextColor = screenText,
                focusedLabelColor = secondaryColor,
                unfocusedLabelColor = screenText.copy(alpha = 0.75f),
                focusedBorderColor = secondaryColor,
                unfocusedBorderColor = screenText.copy(alpha = 0.35f),
                cursorColor = secondaryColor
            )
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Genre", color = screenText, fontWeight = FontWeight.Medium)
            if (state.isLoadingGenres) {
                CircularProgressIndicator(color = secondaryColor)
            } else {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.selectedGenreId == null,
                        onClick = { onAction(FilterScreenEvent.UpdateGenre(null)) },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            labelColor = screenText,
                            selectedLabelColor = onSecondaryColor,
                            containerColor = primaryColor.copy(alpha = 0.14f),
                            selectedContainerColor = secondaryColor
                        )
                    )

                    state.genres.forEach { genre ->
                        FilterChip(
                            selected = genre.id == state.selectedGenreId,
                            onClick = { onAction(FilterScreenEvent.UpdateGenre(genre.id)) },
                            label = { Text(genre.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                labelColor = screenText,
                                selectedLabelColor = onSecondaryColor,
                                containerColor = primaryColor.copy(alpha = 0.14f),
                                selectedContainerColor = secondaryColor
                            )
                        )
                    }
                }
            }
            state.errorMessage?.let {
                Text(it, color = colorScheme.error)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.minYear,
                onValueChange = { onAction(FilterScreenEvent.UpdateMinYear(it.filter(Char::isDigit))) },
                modifier = Modifier.weight(1f),
                label = { Text("Min year") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = screenText,
                    unfocusedTextColor = screenText,
                    focusedLabelColor = secondaryColor,
                    unfocusedLabelColor = screenText.copy(alpha = 0.75f),
                    focusedBorderColor = secondaryColor,
                    unfocusedBorderColor = screenText.copy(alpha = 0.35f),
                    cursorColor = secondaryColor
                )
            )

            OutlinedTextField(
                value = state.maxYear,
                onValueChange = { onAction(FilterScreenEvent.UpdateMaxYear(it.filter(Char::isDigit))) },
                modifier = Modifier.weight(1f),
                label = { Text("Max year") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = screenText,
                    unfocusedTextColor = screenText,
                    focusedLabelColor = secondaryColor,
                    unfocusedLabelColor = screenText.copy(alpha = 0.75f),
                    focusedBorderColor = secondaryColor,
                    unfocusedBorderColor = screenText.copy(alpha = 0.35f),
                    cursorColor = secondaryColor
                )
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val formatRating = ((state.minRating * 10).toInt() / 10f).toString()
            Text(
                text = "Minimum rating: $formatRating",
                color = screenText,
                fontWeight = FontWeight.Medium
            )
            Slider(
                value = state.minRating,
                onValueChange = { onAction(FilterScreenEvent.UpdateMinRating(it)) },
                valueRange = 0f..10f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = secondaryColor,
                    activeTrackColor = secondaryColor,
                    inactiveTrackColor = screenText.copy(alpha = 0.24f)
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    onApply(
                        MovieFilters(
                            query = state.query,
                            genreId = state.selectedGenreId,
                            minYear = state.minYear.toIntOrNull(),
                            maxYear = state.maxYear.toIntOrNull(),
                            minRating = state.minRating
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Apply Filters")
            }

            Button(
                onClick = { onAction(FilterScreenEvent.ClearAll) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear All")
            }
        }

        Spacer(modifier = Modifier.width(1.dp))
    }
}