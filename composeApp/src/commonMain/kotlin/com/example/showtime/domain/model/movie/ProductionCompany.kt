package com.example.showtime.domain.model.movie

data class ProductionCompany (
    val id : Int,
    val name : String,
    val logoPath : String?,
    val originCountry : String?
){
}