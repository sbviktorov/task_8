package ru.netology.exception

class NoteNotFoundException(noteId: Int): IndexOutOfBoundsException("Note $noteId not found.") {
}