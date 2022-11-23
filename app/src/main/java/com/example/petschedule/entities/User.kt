package com.example.petschedule.entities

data class User(
    var login: String,
    var password: String,
    var name : String,
    var token: String) {
}