import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.netology.data.*
import ru.netology.service.NotesService

class NoteService {

    @Before
    fun initNotesService() {
        NotesService.clearAllNotes()
        NotesService.clearAllComments()
    }

    //Создадим заметку
    @Test
    fun addNote() {
        val result =
            NotesService.add(
                Notes(
                    title = "Test title",
                    text = "Test text", date = 20220606, can_comment = 0
                )
            )
        Assert.assertEquals(Notes::class, result::class)
    }

    //Создадим комментарий к заметке
    @Test
    fun createComment() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 1))
        val result =
            NotesService.createComment(
                note,
                NoteComments(message = "Comment for note", date = 20220606)
            )
        Assert.assertEquals(NoteComments::class, result::class)
    }

    //Попробуем удалить несуществующую заметку
    @Test(expected = NoteNotFound::class)
    fun deleteNoteError() {
        NotesService.delete(5)
    }

    //Удалим существующую заметку
    @Test
    fun deleteNote() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 0))

        val result = NotesService.delete(note.id)

        Assert.assertEquals(true, result)
    }

    //Попробуем создать комментарий к ранее удаленной заметке
    @Test(expected = TryingAddToRemovedNote::class)
    fun createCommentErrorTryingAddToRemovedNote() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 0))

        NotesService.delete(note.id)

        NotesService.createComment(note, NoteComments(message = "Error comment", date = 20220606))
    }

    //Попробуем добавить комментарий к заметке с отключенными комментариями
    @Test(expected = CommentsAreForbidden::class)
    fun createCommentErrorCommentsAreForbidden() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 0))

        NotesService.createComment(note, NoteComments(message = "Error comment", date = 20220606))
    }

    //Попробуем удалить ранее удаленную заметку
    @Test(expected = NoteNotFound::class)
    fun createCommentErrorNoteNotFound() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 0))

        NotesService.delete(note.id)
        NotesService.delete(note.id)
    }

    //Отредактируем существующую заметку
    @Test
    fun editNote() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 0))

        val result = NotesService.edit(
            note_id = note.id,
            title = "After editing title",
            text = "After editing text",
            can_comment = 1
        )

        Assert.assertEquals(true, result)
    }

    //Попробуем отредактировать несуществующую заметку
    @Test(expected = NoteNotFound::class)
    fun editNoteError() {
        NotesService.edit(note_id = 23, title = "Error editing title", text = "Error editing text", can_comment = 1)
    }

    //Попробуем удалить комментарий удаленной ранее заметки
    @Test(expected = CommentNotFound::class)
    fun deleteCommentError() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 1))

        val commentForRemove =
            NotesService.createComment(note, NoteComments(message = "Error comment", date = 20220606))
        NotesService.delete(note.id)

        NotesService.deleteComment(commentForRemove.id)
    }

    //Удалим существующий комментарий существующей заметки
    @Test
    fun deleteComment() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 1))

        val commentForRemove =
            NotesService.createComment(note, NoteComments(message = "Error comment", date = 20220606))

        val result = NotesService.deleteComment(commentForRemove.id)

        Assert.assertEquals(true, result)
    }

    //Попробуем удалить ранее удаленный комментарий существующей заметки
    @Test(expected = CommentNotFound::class)
    fun deleteCommentExistingNote() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 1))

        val commentForRemove =
            NotesService.createComment(note, NoteComments(message = "Error comment", date = 20220606))

        NotesService.deleteComment(commentForRemove.id)
        NotesService.deleteComment(commentForRemove.id)
    }

    //Отредактируем существующий комментарий существующей заметки
    @Test
    fun editComment() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 1))

        val comment = NotesService.createComment(note, NoteComments(message = "Error comment", date = 20220606))

        val result = NotesService.editComment(comment.id, "After editing comment text")

        Assert.assertEquals(true, result)
    }

    //Попробуем отредактировать удаленный ранее комментарий существующей заметки
    @Test(expected = CommentNotFound::class)
    fun editRemovedCommentExistingNote() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 1))

        val commentForRemove =
            NotesService.createComment(note, NoteComments(message = "Error comment", date = 20220606))

        NotesService.deleteComment(commentForRemove.id)

        NotesService.editComment(commentForRemove.id, "Error")
    }

    //Получим список всех не удаленных заметок с сортировкой по дате создания в порядке возрастания
    @Test
    fun getNotes() {

        NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 0))
        val result = NotesService.get(
            offset = 0,
            sort = SortTypes.ASC
        )

        Assert.assertNotEquals(0, result.size)
    }

    //Получим существующую заметку по id
    @Test
    fun getNotesFromId() {
        val note = NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 0))
        val result = NotesService.getById(note.id)

        Assert.assertEquals(Notes::class, result::class)
    }

    //Попробуем получить удаленную ранее заметку
    @Test(expected = NoteNotFound::class)
    fun getRemovedNote() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 0))

        NotesService.delete(note.id)
        NotesService.getById(note.id)
    }

    //Получим список всех неудаленных комментариев существующей заметки с сортировкой по дате создания в порядке убывания
    @Test
    fun getAllComments() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 1))

        NotesService.createComment(note, NoteComments(message = "Error comment", date = 20220606))

        val result = NotesService.getComments(
            note_id = note.id,
            offset = 0
        )

        Assert.assertNotEquals(0, result.size)
    }

    //Попробуем восстановить ранее удаленный комментарий удаленной заметки
    @Test(expected = NoteNotFound::class)
    fun restoreRemovedCommentRemovedNote() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 1))
        val commentForRemove =
            NotesService.createComment(note, NoteComments(message = "Error comment", date = 20220606))

        NotesService.deleteComment(commentForRemove.id)
        NotesService.delete(note.id)

        NotesService.restoreComment(commentForRemove.id)
    }

    //Восстановим ранее удаленный комментарий существующей заметки
    @Test
    fun restoreCommentExistingNote() {
        val note =
            NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 1))
        val commentForRemove =
            NotesService.createComment(note, NoteComments(message = "Error comment", date = 20220606))

        NotesService.deleteComment(commentForRemove.id)

        val result = NotesService.restoreComment(commentForRemove.id)

        Assert.assertEquals(NoteComments::class, result::class)
    }
}