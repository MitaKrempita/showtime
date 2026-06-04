package com.example.showtime.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.showtime.MovieRoute
import com.example.showtime.ProfileRoute
import com.example.showtime.QuizStartRoute
import com.example.showtime.WatchlistRoute

enum class NavTab {
    MOVIES, GALLERY, QUIZ, PROFILE
}

@Composable
fun NavBar(
    currentTab: NavTab,
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.DarkGray)
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (currentTab != NavTab.MOVIES) {
                navController.navigate(MovieRoute) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            }
        }) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(38.dp)
            )
        }

        IconButton(onClick = {
            if (currentTab != NavTab.GALLERY) {
                 navController.navigate(WatchlistRoute)
            }
        }) {
            Icon(
                imageVector = Icons.Default.BrowseGallery,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(38.dp)
            )
        }

        IconButton(onClick = {
            if (currentTab != NavTab.QUIZ) {
                 navController.navigate(QuizStartRoute)
            }
        }) {
            Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = null,
                tint =Color.Gray,
                modifier = Modifier.size(38.dp)
            )
        }

        IconButton(onClick = {
            if (currentTab != NavTab.PROFILE) {
                 navController.navigate(ProfileRoute)
            }
        }) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = null,
                tint =Color.Gray,
                modifier = Modifier.size(38.dp)
            )
        }
    }
}