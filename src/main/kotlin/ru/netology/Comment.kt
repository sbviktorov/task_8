package ru.netology

import java.util.*

data class Comment(
    val noteId: Int,
    val commentId: Int,
    val ownerId: Int,
    val replyTo: Int,
    val message: String,
    val date: Long = Date().time,
) {
}