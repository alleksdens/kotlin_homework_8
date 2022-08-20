package ru.netology.data

class TryingAddToRemovedNote(msg: String) : RuntimeException(msg)
class CommentsAreForbidden(msg: String) : RuntimeException(msg)
class NoteNotFound(msg: String) : RuntimeException(msg)
class CommentNotFound(msg: String) : RuntimeException(msg)
