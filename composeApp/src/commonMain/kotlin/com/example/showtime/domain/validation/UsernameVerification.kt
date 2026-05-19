package com.example.showtime.domain.validation

fun usernameVerification(username: String) : ValidationResult
{
    if(username.length<3)
    {
        return ValidationResult.Error("Username must be at least 3 characters in length")
    }
    if(!username.matches("\\w+".toRegex()))
    {
       return ValidationResult.Error("Username must consist of only letters numbers and '_'")
    }
    return ValidationResult.Success
}
