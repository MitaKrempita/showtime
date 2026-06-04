package com.example.showtime.presentation.mainScreen

enum class MovieSortOption(
    val label: String,
    val apiValue: String
) {
    Rating("Rating", "imdb_rating"),
    Year("Year", "year"),
    Title("Title", "title"),
    Popularity("Popularity", "popularity");
}