package ru.netology.getComment

data class GetComment(
    val id: Int,
    val uid: Int,//идентификатор автора комментария;
    val nid: Int,
    val oid: Int,//идентификатор владельца заметки;
    val date: Long,
    val message: String,
    val replyTo: Int? = null
) {
}