package com.example.petschedule.entities

data class Pet(
    val id: String,
    val name: String,
    var petType: String,
    var petGender: String,
    var description: String?,
    val petParameters: MutableList<PetParameters>?
)
