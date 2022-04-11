package ru.netology

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import ru.netology.exception.NoteNotFoundException
import ru.netology.getComment.GetComment

class NoteServiceTest {

    @Before
    fun reset() {
        NoteService.reset()
    }

    @Test
    fun add() {
        val note0 = NoteService.add()
        val note1 = NoteService.add()
        val note2 = NoteService.add()
        val expectedNote0Id = 0
        val expectedNote1Id = 1
        val expectedNote2Id = 2
        val expectedNotesSize = 3

        assertEquals(expectedNote0Id, note0)
        assertEquals(expectedNote1Id, note1)
        assertEquals(expectedNote2Id, note2)
        assertTrue(NoteService.noteList.contains(expectedNote0Id))
        assertTrue(NoteService.noteList.contains(expectedNote1Id))
        assertTrue(NoteService.noteList.contains(expectedNote2Id))
        assertEquals(expectedNotesSize, NoteService.notes.size)

    }

    @Test
    fun createComment() {
        val note0 = NoteService.add()
        val note1 = NoteService.add()
        val note2 = NoteService.add()
        val comment0toNote0 = NoteService.createComment(0)
        val comment1toNote1 = NoteService.createComment(1)
        val comment2toNote1 = NoteService.createComment(1)
        val comment3toNote2 = NoteService.createComment(2)
        val comment4toNote2 = NoteService.createComment(2)
        val comment5toNote2 = NoteService.createComment(2)
        val actualCountOfCommentsToNote2 = NoteService.notes[2].comments
        val expectedCountOfCommentsToNote2 = 3
        val expectedComment0Id = 0
        val expectedComment5Id = 5

        assertEquals(expectedComment0Id, comment0toNote0)
        assertEquals(expectedComment5Id, comment5toNote2)
        assertEquals(expectedCountOfCommentsToNote2, actualCountOfCommentsToNote2)
        assertTrue(NoteService.commentList.contains(comment4toNote2))
    }

    @Test(expected = NoteNotFoundException::class)
    fun createCommentThrowException() {
        val comment0toNote0 = NoteService.createComment(0)
    }

    @Test(expected = NoteNotFoundException::class)
    fun createCommentThrowException2() {
        val comment0toNote0 = NoteService.createComment(0, 111, 111)
    }

    @Test
    fun deleteTrue() {
        val note0 = NoteService.add()
        val note1 = NoteService.add()
        val note2 = NoteService.add()
        val comment0toNote0 = NoteService.createComment(0)
        val comment1toNote1 = NoteService.createComment(1)
        val comment2toNote1 = NoteService.createComment(1)
        val comment3toNote2 = NoteService.createComment(2)
        val comment4toNote2 = NoteService.createComment(2)
        val comment5toNote2 = NoteService.createComment(2)
        val note1ResultOfDelete = NoteService.delete(1)
        assertTrue(NoteService.noteList.contains(note0))
        assertTrue(NoteService.noteList.contains(note2))
        assertFalse(NoteService.noteList.contains(note1))
        assertFalse(NoteService.commentList.contains(comment1toNote1))
        assertFalse(NoteService.commentList.contains(comment2toNote1))
        assertTrue(NoteService.commentList.contains(comment3toNote2))
        assertTrue(note1ResultOfDelete)
    }

    @Test
    fun deleteFalse() {
        val note0 = NoteService.add()
        val note1 = NoteService.add()
        val note2 = NoteService.add()
        val note2ResultOfDelete = NoteService.delete(2)
        val note2SecondResultOfDelete = NoteService.delete(2)
        assertTrue(note2ResultOfDelete)
        assertFalse(note2SecondResultOfDelete)
    }

    @Test
    fun deleteComment() {
        NoteService.userId = 111
        val note0 = NoteService.add()
        val comment0toNote0 = NoteService.createComment(0, ownerId = 444)
        NoteService.userId = 222
        val note1 = NoteService.add()
        val comment1toNote1 = NoteService.createComment(1, ownerId = 222)
        val comment2toNote1 = NoteService.createComment(1, ownerId = 777)
        NoteService.userId = 333
        val note2 = NoteService.add()
        val comment3toNote2 = NoteService.createComment(2, ownerId = 111)
        val comment4toNote2 = NoteService.createComment(2, ownerId = 333)
        val comment5toNote2 = NoteService.createComment(2, ownerId = 222)
        val comment6toNote2 = NoteService.createComment(2, ownerId = 333)
        val comment7toNote2 = NoteService.createComment(2, ownerId = 222)
        NoteService.delete(1)
        val resultOfDeleteComment1 = NoteService.deleteComment(1, 222)
        val resultOfDeleteComment4 = NoteService.deleteComment(4, 222)
        val resultOfDeleteComment5 = NoteService.deleteComment(5, 333) //удаляет владелец записи 2
        val resultOfDeleteComment7 = NoteService.deleteComment(7, 222) //удаляет владелец комментария 7

        assertFalse(resultOfDeleteComment1) //запись 1 комментария была удалена
        assertFalse(resultOfDeleteComment4) //222 не является владельцем записи или комментария
        assertTrue(resultOfDeleteComment5) //комментарий удаляется владелцем записи 2
        assertTrue(resultOfDeleteComment7) //комментарий удаляется владельцем комментария
    }

    @Test
    fun edit() {
        val note0 = NoteService.add(text = "текст", title = "заголовок")
        val note1 = NoteService.add()
        val note1Edited = NoteService.edit(1, "новый заголовок", text = "новый текст")
        val resultOfDeleteNote0 = NoteService.delete(0)
        val note0Edited = NoteService.edit(0, "новый заголовок", text = "новый текст")
        val expectedTitle = "новый заголовок"
        val expectedText = "новый текст"

        assertEquals(expectedTitle, NoteService.notes[note1].title)
        assertEquals(expectedText, NoteService.notes[note1].text)
        assertTrue(note1Edited)
        assertNotEquals(expectedTitle, NoteService.notes[note0].title)
        assertNotEquals(expectedText, NoteService.notes[note0].text)
        assertFalse(note0Edited)
    }

    @Test
    fun editComment() {
        NoteService.userId = 111
        val note0 = NoteService.add()
        val comment0toNote0 = NoteService.createComment(0, ownerId = 444)
        NoteService.userId = 222
        val note1 = NoteService.add()
        val comment1toNote1 = NoteService.createComment(1, ownerId = 222)
        val comment2toNote1 = NoteService.createComment(1, ownerId = 777, message = "комментарий")
        NoteService.userId = 333
        val note2 = NoteService.add()
        val comment3toNote2 = NoteService.createComment(2, ownerId = 111)
        val comment4toNote2 = NoteService.createComment(2, ownerId = 333, message = "комментарий")
        val comment5toNote2 = NoteService.createComment(2, ownerId = 222)
        val comment6toNote2 = NoteService.createComment(2, ownerId = 333)
        val comment7toNote2 = NoteService.createComment(2, ownerId = 222)
        NoteService.delete(1)
        val resultOfEditComment2 = NoteService.editComment(2, 777, message = "измененный комментарий")
        val resultOfEditComment4 = NoteService.editComment(4, 333, message = "измененный комментарий")
        val resultOfEditComment7 = NoteService.editComment(7, 333, message = "измененный комментарий")
        val expectedMessage = "измененный комментарий"

        assertFalse(resultOfEditComment2)
        assertTrue(resultOfEditComment4)
        assertNotEquals(expectedMessage, NoteService.comments[4])
        assertFalse(resultOfEditComment7)
    }

    @Test
    fun get() {
        NoteService.userId = 111
        val note0 = NoteService.add()
        val note1 = NoteService.add()
        NoteService.userId = 222
        val note2 = NoteService.add()
        NoteService.userId = 333
        val note3 = NoteService.add()
        val note4 = NoteService.add()
        NoteService.userId = 111
        val note5 = NoteService.add()
        val note6 = NoteService.add()
        NoteService.userId = 222
        val note7 = NoteService.add()
        NoteService.delete(2)
        val countOfNotesByUser111 = NoteService.get(noteIds = listOf(0, 1, 2, 3, 4, 5, 6, 7), 111).size
        val listOfNotesByUser111 = NoteService.get(noteIds = listOf(0, 1, 2, 3, 4, 5, 6, 7), 111)
        val listOfNotesByUser111Sorted = NoteService.get(noteIds = listOf(0, 1, 2, 3, 4, 5, 6, 7), 111, sort = 1)
        val countOfNotesByUser222 = NoteService.get(noteIds = listOf(0, 2, 3, 7), 222).size
        val countOfNotesByUser111Count2 = NoteService.get(noteIds = listOf(0, 1, 2, 3, 4, 5, 6, 7), 111, count = 2).size

        val expectedCountOfNotesByUser111 = 4
        val expectedCountOfNotesByUser111Count2 = 2
        val expectedCountOfNotesByUser222 = 1

        assertEquals(expectedCountOfNotesByUser111, countOfNotesByUser111)
        assertEquals(countOfNotesByUser111Count2, expectedCountOfNotesByUser111Count2)
        assertEquals(expectedCountOfNotesByUser222, countOfNotesByUser222)
        assertEquals(listOfNotesByUser111[0], listOfNotesByUser111Sorted[3])

    }

    @Test
    fun getById() {
        NoteService.userId = 111
        val note0 = NoteService.add()
        val note1 = NoteService.add()
        NoteService.userId = 222
        val note2 = NoteService.add()
        NoteService.userId = 333
        val note3 = NoteService.add()
        NoteService.delete(2)
        val getNote1ById = NoteService.getById(1, 111)
        val getNote2ById = NoteService.getById(2, 222)
        val getNote3ById = NoteService.getById(3, 222)
        val expectedListOfGetNote1ById = listOf<Note>(NoteService.notes[1])
        val expectedListOfGetNote2ById = listOf<Note>()
        val expectedListOfGetNote3ById = listOf<Note>()
        assertEquals(expectedListOfGetNote1ById, getNote1ById)
        assertEquals(expectedListOfGetNote2ById, getNote2ById)
        assertEquals(expectedListOfGetNote3ById, getNote3ById)
    }

    @Test
    fun getComments() {
        NoteService.userId = 111
        val note0 = NoteService.add()
        val comment0toNote0 = NoteService.createComment(0, ownerId = 444)
        NoteService.userId = 222
        val note1 = NoteService.add()
        val comment1toNote1 = NoteService.createComment(1, ownerId = 222)
        val comment2toNote1 = NoteService.createComment(1, ownerId = 777)
        NoteService.userId = 333
        val note2 = NoteService.add()
        val comment3toNote2 = NoteService.createComment(2, ownerId = 111)
        val comment4toNote2 = NoteService.createComment(2, ownerId = 333)
        val comment5toNote2 = NoteService.createComment(2, ownerId = 222)
        val comment6toNote2 = NoteService.createComment(2, ownerId = 333)
        val comment7toNote2 = NoteService.createComment(2, ownerId = 222)
        val getCommentsNote2Count3 = NoteService.getComments(2, count = 3)
        NoteService.delete(1)
        NoteService.deleteComment(4, 333)
        NoteService.deleteComment(6, 333)
        val getCommentsNote1 = NoteService.getComments(1)
        val getCommentsNote2Size = NoteService.getComments(2).size
        val getCommentsNote2 = NoteService.getComments(2)
        val getCommentsNote2Sorted = NoteService.getComments(2, sort = 1)
        val expectedGetCommentsNote1 = arrayOf<GetComment>()
        val expectedGetCommentsNote2Size = 3
        val getCommentsNote2Count3Size = 3

        assertEquals(expectedGetCommentsNote1.size, getCommentsNote1.size)
        assertEquals(expectedGetCommentsNote2Size, getCommentsNote2Size)
        assertEquals(getCommentsNote2[0], getCommentsNote2Sorted[2])
        assertTrue(getCommentsNote2Count3.size == getCommentsNote2Count3Size)

    }

    @Test
    fun restoreComment() {
        NoteService.userId = 111
        val note0 = NoteService.add()
        val comment0toNote0 = NoteService.createComment(0, ownerId = 444)
        NoteService.userId = 222
        val note1 = NoteService.add()
        val comment1toNote1 = NoteService.createComment(1, ownerId = 222)
        val comment2toNote1 = NoteService.createComment(1, ownerId = 777)
        NoteService.userId = 333
        val note2 = NoteService.add()
        val comment3toNote2 = NoteService.createComment(2, ownerId = 111)
        val comment4toNote2 = NoteService.createComment(2, ownerId = 333)
        val comment5toNote2 = NoteService.createComment(2, ownerId = 222)
        val comment6toNote2 = NoteService.createComment(2, ownerId = 333)
        val comment7toNote2 = NoteService.createComment(2, ownerId = 222)
        NoteService.delete(1)
        NoteService.deleteComment(4, 333)
        NoteService.deleteComment(6, 333)
        val restoreComment1 = NoteService.restoreComment(1)
        val restoreComment3 = NoteService.restoreComment(3)
        val restoreComment4 = NoteService.restoreComment(4)

        assertFalse(restoreComment1)
        assertFalse(restoreComment3)
        assertTrue(restoreComment4)

    }
}