package ru.netology

import java.util.*

data class Note(
    val id: Int,
    val ownerId: Int,
    val title: String,
    val text: String,
    val date: Long = Date().time,
    var comments: Int,
    val readComments: Int,
    val viewUrl: String
) {
}