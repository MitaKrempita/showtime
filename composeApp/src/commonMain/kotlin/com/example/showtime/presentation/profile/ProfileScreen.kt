package com.example.showtime.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.showtime.presentation.NavBar
import com.example.showtime.presentation.NavTab

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(15, 15, 15, 255))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 22.dp)
                .padding(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Profile",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            if (state.isLoading) {
                CircularProgressIndicator(color = Color(180, 0, 0, 255))
            }

            state.errorMessage?.let {
                Text(text = it, color = colorScheme.error)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(48, 49, 56, 255)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = state.user?.fullName ?: "Unknown user",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.user?.username?.let { "@$it" } ?: "",
                        color = Color.Gray
                    )
                }
            }

            ProfileRow("Best score", state.bestScore.toString())
            ProfileRow("Quiz played", state.gamesPlayed.toString())
            ProfileRow("Favorite movies", state.favoriteCount.toString())
            ProfileRow("Watchlist movies", state.watchlistCount.toString())

            Button(
                onClick = { onAction(ProfileIntent.Logout) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }

        NavBar(
            currentTab = NavTab.PROFILE,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ProfileRow(
    label: String,
    value: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(48, 49, 56, 255)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.White)
            Text(value, color = Color(180, 0, 0, 255), fontWeight = FontWeight.Bold)
        }
    }
}
