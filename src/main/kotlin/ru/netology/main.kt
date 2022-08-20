package ru.netology

import ru.netology.data.*
import ru.netology.service.NotesService

fun main(args: Array<String>) {

    // Создадим несколько заметок
    val noteOne = NotesService.add(Notes(title = "Test title", text = "Test text", date = 20220606, can_comment = 0))
    val noteTwo = NotesService.add(Notes(title = "Test title 2", text = "Test text 2", date = 20220605))
    val noteThree = NotesService.add(Notes(title = "Test title 3", text = "Test text 3", date = 20220606))

    //Создадим комментарий к второй заметке
    val commentOneNoteTwo =
        NotesService.createComment(noteTwo, NoteComments(message = "Comment for note #2", date = 20220606))

    //Создадим 3 комментария к тертьей заметке
    val commentOneNoteThree =
        NotesService.createComment(noteThree, NoteComments(message = "First comment for note #3", date = 20220606))
    val commentTwoNoteThree =
        NotesService.createComment(noteThree, NoteComments(message = "Second comment for note #3", date = 20220606))
    val commentThreeNoteThree =
        NotesService.createComment(noteThree, NoteComments(message = "Third comment for note #3", date = 20220606))

    //Попробуем удалить несуществующую заметку
    try {
        NotesService.delete(5)
    } catch (e: NoteNotFound) {
        println(e.message)
    }

    //Удалим заметку #3
    NotesService.delete(noteThree.id)

    //попробуем создать комментарий к ранее удаленной заметке (noteThree)
    try {
        val createErrorComment =
            NotesService.createComment(noteThree, NoteComments(message = "Error comment", date = 20220606))
    } catch (e: TryingAddToRemovedNote) {
        println(e.message)
    }

    //Отредактируем существующую заметку и включим комментарии для нее
    NotesService.edit(note_id = noteOne.id, title = "After editing title", text = "After editing text", can_comment = 1)

    //Попробуем отредактировать несуществующую заметку
    try {
        NotesService.edit(note_id = 23, title = "Error editing title", text = "Error editing text", can_comment = 1)
    } catch (e: NoteNotFound) {
        println(e.message)
    }

    //Добавим комментарии к заметке (noteOne) после включения комментариев в ней
    val commentOneNoteOne =
        NotesService.createComment(noteOne, NoteComments(message = "First comment for note #1", date = 20220606))
    val commentTwoNoteOne =
        NotesService.createComment(noteOne, NoteComments(message = "Second comment for note #1", date = 20220605))

    //Попробуем добавить комментарий к заметке с отключенными комментариями (noteOne)
    try {
        val createErrorComment =
            NotesService.createComment(noteOne, NoteComments(message = "Error comment", date = 20220606))
    } catch (e: CommentsAreForbidden) {
        println(e.message)
    }

    //Попробуем удалить ранее удаленную заметку (#3)
    try {
        NotesService.delete(noteThree.id)
    } catch (e: NoteNotFound) {
        println(e.message)
    }

    //Попробуем удалить комментарий (commentOneNoteThree) удаленной ранее заметки (noteThree)
    try {
        NotesService.deleteComment(commentOneNoteThree.id)
    } catch (e: CommentNotFound) {
        println(e.message)
    }

    //Удалим существующий комментарий (commentOneNoteTwo) существующей заметки (noteTwo)
    NotesService.deleteComment(commentOneNoteTwo.id)

    //Попробуем удалить ранее удаленный комментарий (commentOneNoteTwo) существующей заметки (noteTwo)
    try {
        NotesService.deleteComment(commentOneNoteTwo.id)
    } catch (e: CommentNotFound) {
        println(e.message)
    }
    //Отредактируем существующий комментарий (commentOneNoteOne) существующей заметки (noteOne)
    NotesService.editComment(commentOneNoteOne.id, "After editing comment text")

    //Попробуем отредактировать удаленный ранее комментарий (commentOneNoteTwo) существующей заметки (noteTwo)
    try {
        NotesService.editComment(commentOneNoteTwo.id, "Error")
    } catch (e: CommentNotFound) {
        println(e.message)
    }
    //Получим список всех не удаленных заметок с сортировкой по дате создания в порядке возрастания
    println(
        "Получен список заметок (сортировка - дата  ${SortTypes.ASC.text}):\n${
            NotesService.get(
                offset = 0,
                sort = SortTypes.ASC
            )
        }\n"
    )

    //Получим существующую заметку (noteOne) по id
    println("Найдена заметка:\n${NotesService.getById(noteOne.id)}\n")

    //Попробуем получить удаленную ранее заметку (noteThree)
    try {
        NotesService.getById(noteThree.id)
    } catch (e: NoteNotFound) {
        print(e.message)
    }

    //Получим список всех неудаленных комментариев существующей заметки с сортировкой по дате создания в порядке убывания
    println(
        "Получен список комментариев к заметке id = ${noteOne.id} (сортировка - дата  ${SortTypes.DESC.text}):\n${
            NotesService.getComments(
                note_id = noteOne.id,
                offset = 0
            )
        }\n"
    )

    //Получим список всех неудаленных комментариев удаленной заметки с сортировкой по дате создания в порядке убывания
    val commentFoundList = NotesService.getComments(note_id = noteThree.id, offset = 0)
    if (commentFoundList.isEmpty()) println("[Комментарии не найдены]")
    else println(
        "Получен список комментариев к заметке id = ${noteThree.id} (сортировка - дата  ${SortTypes.DESC.text}):\n$commentFoundList\n"
    )

    //Попробуем восстановить ранее удаленный комментарий удаленной заметки
    try {
        NotesService.restoreComment(commentTwoNoteThree.id)
    } catch (e: NoteNotFound) {
        println(e.message)
    } catch (e: CommentNotFound) {
        println(e.message)
    }

    //Восстановим ранее удаленный комментарий существующей заметки
    println("Восстановлен комментарий: ${NotesService.restoreComment(commentOneNoteTwo.id)}")
}