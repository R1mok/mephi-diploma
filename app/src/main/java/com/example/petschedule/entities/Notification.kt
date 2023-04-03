package com.example.petschedule.entities

import java.time.LocalTime

data class Notification (
    val groupName: String,
    val petName: String,
    val comment: String,
    val time: String
    )