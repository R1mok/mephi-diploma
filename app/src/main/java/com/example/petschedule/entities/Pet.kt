package com.example.petschedule.entities

data class Pet(
    val id: String,
    val name: String,
    val petType: String,
    val petGender: String,
    val description: String?,
    val petParameters: MutableList<PetParameters>?
)
