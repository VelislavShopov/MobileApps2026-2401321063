package com.example.padelconnect.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val id: String = "",
    val name: String = ""
)