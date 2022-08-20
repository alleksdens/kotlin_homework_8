package ru.netology.service

import ru.netology.data.*
import ru.netology.interfaces.CrudService

object NotesService : CrudService<Notes, NoteComments> {
    private val notesList = mutableListOf<Notes>()
    private val noteCommentsList = mutableListOf<NoteComments>()

    override fun add(item: Notes): Notes {
        notesList.add(item.copy(id = notesList.size + 1))
        println("✔ Добавлена заметка ${notesList.last()} \n")
        return notesList.last()
    }

    @Throws(TryingAddToRemovedNote::class, CommentsAreForbidden::class)
    override fun createComment(note: Notes, item: NoteComments): NoteComments {
        if (note.removed) throw TryingAddToRemovedNote("✖ Ошибка: Попытка добавить комментарий к удаленной заметке! \n")
        if (note.can_comment == 0) throw CommentsAreForbidden("✖ Ошибка: Комментарии к данной заметке отключены! \n")

        noteCommentsList.add(item.copy(id = noteCommentsList.size + 1, note_id = note.id))
        note.comments += 1
        println("✔ К заметке id = ${note.id} добавлен комментарий ${noteCommentsList.last()} \n")
        return noteCommentsList.last()
    }

    @Throws(NoteNotFound::class)
    override fun delete(note_id: Int): Boolean {
        println("Удаление заметки id = $note_id и всех комментариев к ней... \n")
        val notesList = notesList.filter { it.id == note_id && !it.removed }

        if (notesList.isEmpty()) throw NoteNotFound("✖ Такой заметки не существует либо она была удалена ранее!")

        val removedNote = notesList[0]
            .also { it.removed = true }

        noteCommentsList.filter {
            it.note_id == note_id
        }
            .forEach {
                it.removed = true
                removedNote.comments -= 1
                println("✔ Удален комментарий к заметке id = $note_id: $it \n")
            }

        println("✔ Удалена заметка $removedNote \n")
        return true
    }

    @Throws(CommentNotFound::class)
    override fun deleteComment(comment_id: Int): Boolean {
        println("Удаление коментария id = $comment_id \n")
        val commentList = noteCommentsList.filter { it.id == comment_id && !it.removed }
        if (commentList.isEmpty()) {
            throw CommentNotFound("✖ Такого комментария не существует либо он был удален ранее! \n")
        }
        val removedComment = commentList.get(0)

        val parentNoteList = notesList.filter { it.id == removedComment.note_id && !it.removed }

        if (parentNoteList.isEmpty()) {
            println("✖ Невозможно удалить комментарий, т.к. он принадлежит удаленной заметке! \n")
            return false
        }

        val parentNote = parentNoteList.get(0).also { it.comments -= 1 }
        removedComment.removed = true
        println("✔ Удален комментарий $removedComment. Оставшихся комментариев к заметке id = ${removedComment.note_id}: ${parentNote.comments} \n")
        return true
    }

    @Throws(NoteNotFound::class)
    override fun edit(
        note_id: Int,
        title: String,
        text: String,
        date: Int,
        view_url: String?,
        can_comment: Int
    ): Boolean {
        println("Редактирование заметки id = $note_id... \n")
        val notesList = notesList.filter { it.id == note_id && !it.removed }

        if (notesList.isEmpty()) {
            throw NoteNotFound("✖ Такой заметки не существует либо она была удалена ранее! \n")
        }

        val editNote = notesList[0]
            .also {
                it.title = if (title == "") it.title else title
                it.text = if (text == "") it.text else text
                it.date = if (date == 0) it.date else date
                it.view_url = view_url ?: it.view_url
                it.can_comment = can_comment
            }

        println("✔ Заметка успешно отредактирована: $editNote\n")
        return true
    }

    @Throws(CommentNotFound::class)
    override fun editComment(comment_id: Int, message: String): Boolean {
        println("Редактирование комментария id = $comment_id... \n")
        val commentList = noteCommentsList.filter { it.id == comment_id && !it.removed }
        if (commentList.isEmpty()) {
            throw CommentNotFound("✖ Такого комментария не существует либо он был удален ранее! \n")
        }
        val editComment = commentList[0].also { it.message = message }
        println("✔ Комментарий успешно отредактирован: $editComment\n")
        return true
    }

    override fun get(note_ids: List<Int>, offset: Int, count: Int, sort: SortTypes): List<Notes> {
        val foundNotesList =
            if (note_ids.isNotEmpty()) notesList.filter { it.id in note_ids && !it.removed }
                .toList() else notesList.filter { !it.removed }.toList()

        val totalCount = if (count < 0) foundNotesList.size else count
        val countRec = if (foundNotesList.size >= offset + totalCount) offset + totalCount else foundNotesList.size
        val subList = foundNotesList.subList(offset, countRec)
        if (sort.id == 0) return subList.sortedByDescending { it.date }
        return subList.sortedBy { it.date }
    }

    @Throws(NoteNotFound::class)
    override fun getById(note_id: Int): Notes {
        val foundNoteList = notesList.filter { it.id == note_id && !it.removed }
        if (foundNoteList.isEmpty()) throw NoteNotFound("✖ Заметка с id $note_id не найдена!\n")
        return foundNoteList[0]
    }

    override fun getComments(note_id: Int, sort: SortTypes, offset: Int, count: Int): List<NoteComments> {
        if (notesList.none { it.id == note_id && !it.removed }) return emptyList<NoteComments>() //возращаем пустой список, т.к. у удаленной заметки все комментарии помечены на удаление

        val foundNoteCommentsList = noteCommentsList.filter { it.note_id == note_id && !it.removed }.toList()

        val totalCount = if (count < 0) foundNoteCommentsList.size else count
        val countRec =
            if (foundNoteCommentsList.size >= offset + totalCount) offset + totalCount else foundNoteCommentsList.size
        val subList = foundNoteCommentsList.subList(offset, countRec)
        if (sort.id == 0) return subList.sortedByDescending { it.date }
        return subList.sortedBy { it.date }
    }

    @Throws(CommentNotFound::class, NoteNotFound::class)
    override fun restoreComment(comment_id: Int): NoteComments {
        val foundNoteCommentsList = noteCommentsList.filter { it.id == comment_id && it.removed }
        if (foundNoteCommentsList.isEmpty()) throw CommentNotFound("✖ Невозможно восстановить комментарий: комментария с id $comment_id не существует!\n")
        if (notesList.none { it.id == foundNoteCommentsList[0].note_id && !it.removed }) throw NoteNotFound("✖ Невозможно восстановить комментарий: содержащая его заметка удалена!")
        return foundNoteCommentsList[0].also { it.removed = false }
    }

    fun clearAllNotes() {
        notesList.clear()
    }

    fun clearAllComments() {
        noteCommentsList.clear()
    }

}