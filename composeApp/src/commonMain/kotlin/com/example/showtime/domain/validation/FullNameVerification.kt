package com.example.showtime.domain.validation

fun fullNameVerification(fullName : String) : ValidationResult
{
    val split = fullName.split(' ')
    return if(split.filter { vrednost -> vrednost.isNotEmpty() }.size>=2)
        ValidationResult.Success
    else ValidationResult.Error(message = "Full name is needed")

}