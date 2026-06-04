package com.example.showtime.infrastructure.room.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MovieWithRelations(
    @Embedded val movie: MovieEntity,

    @Relation(
        parentColumn = "collectionId",
        entityColumn = "id"
    )
    val collection: CollectionEntity?,

    @Relation(
        parentColumn = "imdbId",
        entityColumn = "id",
        associateBy = Junction(
            value = MovieGenreEntity::class,
            parentColumn = "imdbId",
            entityColumn = "genreId"
        )
    )
    val genres: List<GenreEntity>,

    @Relation(
        parentColumn = "imdbId",
        entityColumn = "imdbId"
    )
    val favorite: FavoriteEntity? = null,

    @Relation(
        parentColumn = "imdbId",
        entityColumn = "imdbId"
    )
    val watchlist: WatchlistEntity? = null, //TODO: remove null after impl
) {
    val isFavorite: Boolean get() = favorite != null
    val isOnWatchlist: Boolean get() = watchlist != null
}
