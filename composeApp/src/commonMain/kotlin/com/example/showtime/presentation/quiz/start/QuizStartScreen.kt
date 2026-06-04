package com.example.showtime.presentation.quiz.start

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.showtime.presentation.NavBar
import com.example.showtime.presentation.NavTab

@Composable
fun QuizStartScreen(
    state: QuizStartState,
    onAction: (QuizStartIntent) -> Unit,
    onStartQuiz: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(15, 15, 15, 255))
    ) {
        Column(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 100.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MilitaryTech,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color(180, 0, 0, 255)
                )
                Text(
                    text = "Quiz",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Card(
                modifier = Modifier
                    .padding(top = 30.dp, bottom = 10.dp)
                    .height(250.dp)
                    .fillMaxWidth(0.85f),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(48, 49, 56, 255))
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(180, 0, 0, 255)
                        )
                    } else if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage,
                            color = Color.Red,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(state.leaderBoardData) { entry ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${entry.rank}. ${entry.username}",
                                        fontSize = 18.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${entry.score} pts",
                                        fontSize = 18.sp,
                                        color = Color(180, 0, 0, 255),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(48, 49, 56, 255))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFFF3F304)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Personal Best: ${state.personalBest} pts (${state.gamesPlayed} played)",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            if (!state.canStartQuiz && !state.isLoading) {
                Text(
                    text = "Browse the catalog first to populate your quiz pool",
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = onStartQuiz,
                enabled = state.canStartQuiz && !state.isLoading,
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Start Quiz")
            }
        }

        NavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            currentTab = NavTab.QUIZ
        )
    }
}