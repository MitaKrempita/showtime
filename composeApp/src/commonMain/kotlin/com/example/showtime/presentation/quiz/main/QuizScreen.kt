package com.example.showtime.presentation.quiz.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.showtime.presentation.LocalNavController
import com.example.showtime.presentation.ShowtimeBackHandler

@Composable
fun QuizScreen(
    state: QuizState,
    onAction: (QuizIntent) -> Unit
) {
    val navController = LocalNavController.current
    val isAnswered = state.selectedAnswer != null
    var showAbandonDialog by rememberSaveable { mutableStateOf(false) }

    ShowtimeBackHandler(enabled = !state.isFinished) {
        showAbandonDialog = true
    }

    if (showAbandonDialog) {
        AlertDialog(
            onDismissRequest = { showAbandonDialog = false },
            title = { Text("Abandon quiz?") },
            text = { Text("Your progress will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAbandonDialog = false
                        onAction(QuizIntent.AbandonQuiz)
                        navController.popBackStack()
                    }
                ) {
                    Text("Abandon")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAbandonDialog = false }) {
                    Text("Continue")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(15, 15, 15, 255))
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(180, 0, 0, 255)
            )
        } else if (state.isFinished) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage,
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Back")
                    }
                    return@Column
                }
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color(76, 175, 80, 255)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Quiz Completed!",
                    fontSize = 28.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Score: ${String.format("%.2f", state.finalScore ?: 0f)}/100",
                    fontSize = 32.sp,
                    color = Color(180, 0, 0, 255),
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Correct: ${state.correctCount}  |  Incorrect: ${state.incorrectCount}",
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = "Time used: ${state.timeUsed} seconds",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    enabled = !state.isSavingResult,
                    onClick = { navController.popBackStack() }
                ) {
                    Text(if (state.isSavingResult) "Saving..." else "Done")
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${state.currentQuestionIndex + 1}/10",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${state.timerSeconds}s",
                        fontSize = 18.sp,
                        color = if (state.timerSeconds <= 10) Color.Red else Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { showAbandonDialog = true }) {
                        Text("Abandon", color = Color.White)
                    }
                }

                LinearProgressIndicator(
                    progress = { state.timerSeconds / 60f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50)),
                    color = if (state.timerSeconds <= 10) Color.Red else Color(180, 0, 0, 255),
                    trackColor = Color(48, 49, 56, 255)
                )

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(48, 49, 56, 255))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = state.movieImageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))
                        )

                        if (state.questionType != QuestionType.MOVIE) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.75f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = state.movieTitle,
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Text(
                    text = when (state.questionType) {
                        QuestionType.MOVIE -> "Guess the Movie Title"
                        QuestionType.YEAR -> "In which year was this movie released?"
                        QuestionType.ACTOR -> "Who is one of the lead actors in this movie?"
                    },
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    state.options.forEach { option ->
                        val cardColor = when {
                            !isAnswered -> Color(48, 49, 56, 255)
                            option == state.correctAnswer -> Color(76, 175, 80, 255)
                            option == state.selectedAnswer -> Color(244, 67, 54, 255)
                            else -> Color(48, 49, 56, 255).copy(alpha = 0.5f)
                        }

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable(enabled = !isAnswered) {
                                    onAction(QuizIntent.SelectAnswer(option))
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardColor)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
