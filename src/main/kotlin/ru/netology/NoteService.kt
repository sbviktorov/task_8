package ru.netology

import ru.netology.exception.NoteNotFoundException
import ru.netology.getComment.GetComment
import java.lang.Thread.sleep

object NoteService {
    private var noteId: Int = 0
    private var commentId: Int = 0
    var notes = mutableListOf<Note>()
    var comments = mutableListOf<Comment>()
    var noteList = mutableListOf<Int>()
    var commentList = mutableListOf<Int>()
    var userId: Int = 111
    var notesByOwner = listOf<Note>()
    var noteById = listOf<Note>()


    fun add(
        title: String = "title of note $noteId",
        text: String = "text of note $noteId",
        privacyView: String? = null,
        privacyComment: String? = null
    ): Int {

        val newNote = notes.add(
            index = noteId,
            Note(
                id = noteId,
                ownerId = userId,
                title = title,
                text = text,
                comments = 0,
                readComments = 0,
                viewUrl = "URL of note $noteId"
            )
        )
        noteList.add(noteId)
        noteId++
        sleep(8) // sleep добавлено нарочно, что бы хоть немного менялось время при тестах
        return noteId - 1
    }

    fun createComment(
        noteId: Int, //идентификатор заметки. положительное число, обязательный параметр
        ownerId: Int = userId,
        replyTo: Int = if (noteList.contains(noteId)) {
            notes[noteId].ownerId
        } else {
            throw NoteNotFoundException(noteId)
        },
        message: String = "message  to note $noteId of comment $commentId.", //текст комментария.
        guid: String? = null
    ): Int {
        if (noteList.contains(noteId)) {
            notes[noteId].comments++
            val newComment = comments.add(
                index = commentId,
                Comment(
                    noteId = noteId,
                    commentId = commentId,
                    ownerId = ownerId,
                    replyTo = replyTo,
                    message = message
                )
            )
            commentList.add(commentId)
            commentId++
            sleep(7) // sleep добавлено нарочно, что бы менялось время при создании постов
            return commentId - 1
        } else {
            throw NoteNotFoundException(noteId)
        }
    }

    fun delete(noteId: Int): Boolean {
        if (noteList.contains(noteId)) {
            noteList.remove(noteId)
            for (i in 0 until comments.size) {
                if (comments[i].noteId == noteId) {
                    commentList.remove(comments[i].commentId)
                }
            }
            return true
        } else {
            println("Note $noteId is not available for deletion")
            return false
        }
    }

    fun deleteComment(commentId: Int, ownerId: Int): Boolean {
        //удалить коммент может либо владелец записи, к которой оставлен коммент, либо пользователь, оставивший коммент
        if ((commentList.contains(commentId)) && ((ownerId == notes[comments[commentId].noteId].ownerId) || (ownerId == comments[commentId].ownerId))) {
            commentList.remove(commentId)
            return true
        }

        println("You can\'t delete Comment $commentId")
        return false

    }

    fun edit(
        noteId: Int,
        title: String = "Edited ${notes[noteId].title}",
        text: String = "Edited ${notes[noteId].text}",
        privacyView: String? = null,
        privacyComment: String? = null
    ): Boolean {
        if (noteList.contains(noteId)) {
            notes[noteId] = notes[noteId].copy(title = title, text = text)
            return true
        }
        println("You can\'t edit Note $noteId")
        return false
    }

    fun editComment(
        commentId: Int,
        ownerId: Int = userId,
        message: String = "Edited ${comments[commentId].message}"
    ): Boolean {
        if (commentList.contains(commentId) && (ownerId == comments[commentId].ownerId)) {
            comments[commentId] = comments[commentId].copy(message = message)
            return true
        }
        println("You can\'t edit Comment $commentId")
        return false
    }

    fun get(
        noteIds: List<Int>,
        userId: Int,//идентификатор пользователя, информацию о заметках которого требуется получить. положительное число, по умолчанию идентификатор текущего пользователя
        offset: Int = 0,//смещение, необходимое для выборки определенного подмножества заметок. положительное число, по умолчанию 0
        count: Int = 20,//количество заметок, информацию о которых необходимо получить. положительное число, по умолчанию 20, максимальное значение 100
        sort: Int = 0//0 - по убыванию даты, 1 - по возрастанию даты
    ): List<Note> {
        notesByOwner = listOf<Note>()
        var i = 0
        while ((i < noteIds.size) && (i <= count) && (i <= 100)) {

            if ((notes[noteIds[i]].ownerId == userId) && noteList.contains(noteIds[i])) {
                notesByOwner += notes[noteIds[i]]
                println("Information about Note ${noteIds[i]} (author  $userId) added")
            } else {
                println("Information about Note ${noteIds[i]} (author  $userId) not found")
            }
            i++
        }

        if (sort == 0) {
            notesByOwner = notesByOwner.reversed()

        }
        return notesByOwner

    }

    fun getById(
        noteId: Int,
        ownerId: Int,
        need_wiki: Boolean = false
    ): List<Note> {
        noteById = listOf<Note>()
        return if (noteList.contains(noteId) && ownerId == notes[noteId].ownerId) {
            noteById += notes[noteId]

            noteById
        } else {
            println("Information about Note $noteId (author  $ownerId) not found")
            noteById
        }
    }

    fun getComments(
        noteId: Int,//положительное число, обязательный параметр
        ownerId: Int = userId,//положительное число, по умолчанию идентификатор текущего пользователя
        sort: Int = 0,// результатов (0 — по дате добавления в порядке возрастания, 1 — по дате добавления в порядке убывания).положительное число, по умолчанию 0
        offset: Int = 0,// смещение, необходимое для выборки определенного подмножества комментариев. положительное число, по умолчанию 0
        count: Int = 20
    ): Array<GetComment> {
        var getCommentsArray = arrayOf<GetComment>()
        if (noteList.contains(noteId)) {
            for (i in 0 until comments.size) {
                if (comments[i].noteId == noteId && commentList.contains(i)) {
                    getCommentsArray += GetComment(
                        id = i,
                        uid = comments[i].ownerId,
                        date = comments[i].date,
                        message = comments[i].message,
                        nid = comments[i].noteId,
                        oid = comments[i].replyTo
                    )
                    if (getCommentsArray.size == count) break
                }
            }

        } else {
            println("Information about Note $noteId not found")

        }
        if (sort == 1) {
            var getCommentsReversedArray = arrayOf<GetComment>()

            for (i in getCommentsArray.indices) {
                getCommentsReversedArray += getCommentsArray[getCommentsArray.size - 1 - i]
            }

            getCommentsArray = getCommentsReversedArray
        }
        return getCommentsArray
    }

    fun restoreComment(
        commentId: Int,
        ownerId: Int? = userId
    ): Boolean {
        return if (!noteList.contains(comments[commentId].noteId)) {
            println("Восстановление коммента удаленной записи невозможно")
            false
        } else if (commentList.contains(commentId)) {
            println("Восстановление неудаленного комментария невозможно")
            false
        } else {
            commentList.add(commentId)
            commentList.sort()
            println("комментарий $commentId восстановлен")
            true
        }

    }

    fun reset() {
        noteId = 0
        commentId = 0
        notes = mutableListOf<Note>()
        comments = mutableListOf<Comment>()
        noteList = mutableListOf<Int>()
        commentList = mutableListOf<Int>()
        userId = 111
        notesByOwner = listOf<Note>()
        noteById = listOf<Note>()

    }

}