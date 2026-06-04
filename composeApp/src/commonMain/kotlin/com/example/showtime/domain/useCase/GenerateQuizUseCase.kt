package com.example.showtime.domain.useCase

import com.example.showtime.domain.model.QuizQuestion
import com.example.showtime.domain.model.movie.ImageConfig
import com.example.showtime.domain.repository.MovieRepository
import com.example.showtime.infrastructure.api.login.ImageUrlRoute
import com.example.showtime.infrastructure.room.dao.MovieDao
import com.example.showtime.infrastructure.room.entity.MovieWithRelations
import com.example.showtime.presentation.quiz.main.QuestionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class GenerateQuizUseCase(
    private val movieDao: MovieDao,
    private val movieRepository: MovieRepository
) {
    suspend fun execute(): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val questions = mutableListOf<QuizQuestion>()
        val usedMovieIds = mutableSetOf<String>()
        val usedImages = mutableSetOf<String>()

        val rawMovies = movieDao.getQuizReadyMovies()
        if (rawMovies.size < 10) return@withContext emptyList()

        val config = try {
            movieRepository.getImageConfig().first().getOrNull()
        } catch (e: Exception) {
            null
        } ?: return@withContext emptyList()

        val typeCounts = mutableMapOf(
            QuestionType.MOVIE to 0,
            QuestionType.YEAR to 0,
            QuestionType.ACTOR to 0
        )
        val maxAttempts = 120
        var attempts = 0

        while (questions.size < 10 && attempts < maxAttempts) {
            attempts++
            val movie = rawMovies.random()
            if (usedMovieIds.contains(movie.movie.imdbId)) continue

            val types = listOf(QuestionType.MOVIE, QuestionType.YEAR, QuestionType.ACTOR)
                .filter { (typeCounts[it] ?: 0) < 4 }
                .shuffled()

            for (type in types) {
                val question = tryGenerateQuestion(movie, type, rawMovies, config)
                if (question != null && !usedImages.contains(question.imageUrl)) {
                    questions.add(question)
                    usedMovieIds.add(movie.movie.imdbId)
                    usedImages.add(question.imageUrl)
                    typeCounts[type] = (typeCounts[type] ?: 0) + 1
                    break
                }
            }
        }

        return@withContext questions
    }

    private suspend fun tryGenerateQuestion(
        target: MovieWithRelations,
        type: QuestionType,
        allMovies: List<MovieWithRelations>,
        config: ImageConfig
    ): QuizQuestion? {
        val imageUrl = getImageUrl(target, config, type) ?: return null

        return when (type) {
            QuestionType.MOVIE -> {
                val correctAnswer = target.movie.title
                val falseOptions = allMovies
                    .filter { it.movie.imdbId != target.movie.imdbId }
                    .map { it.movie.title }
                    .filter { it != correctAnswer }
                    .distinct()
                    .shuffled()
                    .take(3)

                if (falseOptions.size < 3) return null

                QuizQuestion(
                    type = type,
                    imageUrl = imageUrl,
                    movieTitle = target.movie.title,
                    options = (falseOptions + correctAnswer).shuffled(),
                    correctAnswer = correctAnswer
                )
            }

            QuestionType.YEAR -> {
                val correctYear = target.movie.year ?: return null
                val offsets = listOf(-10, -5, -3, -2, -1, 1, 2, 3, 5, 10).shuffled()
                val falseOptions = offsets.take(3).map { (correctYear + it).toString() }

                QuizQuestion(
                    type = type,
                    imageUrl = imageUrl,
                    movieTitle = target.movie.title,
                    options = (falseOptions + correctYear.toString()).shuffled(),
                    correctAnswer = correctYear.toString()
                )
            }

            QuestionType.ACTOR -> {
                var cast = movieDao.getMovieCastOnce(target.movie.imdbId)
                if (cast.isEmpty()) {
                    try {
                        movieRepository.getMovieCast(target.movie.imdbId, 1, 10).first()
                        cast = movieDao.getMovieCastOnce(target.movie.imdbId)
                    } catch (e: Exception) {
                    }
                }

                val actors = cast.filter {
                    it.department == "Acting" || it.professions?.contains("actor", true) == true
                }
                if (actors.isEmpty()) return null

                val correctAnswer = actors.take(3).random().name

                val otherCastNames = allMovies.filter { it.movie.imdbId != target.movie.imdbId }
                    .flatMap { movieDao.getMovieCastOnce(it.movie.imdbId) }
                    .map { it.name }
                    .filter { it != correctAnswer }
                    .toMutableSet()

                if (otherCastNames.size < 3) {
                    allMovies
                        .filter { it.movie.imdbId != target.movie.imdbId }
                        .shuffled()
                        .take(12)
                        .forEach { movie ->
                            try {
                                movieRepository.getMovieCast(movie.movie.imdbId, 1, 10).first()
                                movieDao.getMovieCastOnce(movie.movie.imdbId)
                                    .map { it.name }
                                    .filter { it != correctAnswer }
                                    .forEach { otherCastNames.add(it) }
                            } catch (e: Exception) {
                            }
                        }
                }

                val allOtherCast = otherCastNames.shuffled()

                if (allOtherCast.size < 3) return null

                QuizQuestion(
                    type = type,
                    imageUrl = imageUrl,
                    movieTitle = target.movie.title,
                    options = (allOtherCast.take(3) + correctAnswer).shuffled(),
                    correctAnswer = correctAnswer
                )
            }
        }
    }

    private fun getImageUrl(target: MovieWithRelations, config: ImageConfig, type: QuestionType): String? {
        if (type == QuestionType.MOVIE) {
            return target.movie.backdropPath?.let { ImageUrlRoute.backdrop(config, it, "w780") }
                ?: target.movie.posterPath?.let { ImageUrlRoute.poster(config, it, "w342") }
        }
        return target.movie.posterPath?.let { ImageUrlRoute.poster(config, it, "w342") }
            ?: target.movie.backdropPath?.let { ImageUrlRoute.backdrop(config, it, "w780") }
    }
}
