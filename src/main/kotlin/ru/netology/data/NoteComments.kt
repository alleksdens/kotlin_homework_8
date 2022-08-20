package ru.netology.data

data class NoteComments(
    val id: Int = 0,
    val note_id: Int = 0,
    internal var date: Int,
    internal var message: String,
    internal var removed: Boolean = false
)