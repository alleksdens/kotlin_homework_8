package ru.netology.interfaces

import ru.netology.data.SortTypes


interface CrudService<TNotes, TNoteComments> {
    fun add(item: TNotes): TNotes
    fun createComment(note: TNotes, item: TNoteComments): TNoteComments
    fun delete(note_id: Int): Boolean
    fun deleteComment(comment_id: Int): Boolean
    fun edit(
        note_id: Int,
        title: String = "",
        text: String = "",
        date: Int = 0,
        view_url: String? = null,
        can_comment: Int
    ): Boolean

    fun editComment(comment_id: Int, message: String): Boolean
    fun get(
        note_ids: List<Int> = emptyList<Int>(),
        offset: Int = 0,
        count: Int = -1,
        sort: SortTypes = SortTypes.DESC
    ): List<TNotes>

    fun getById(note_id: Int): TNotes
    fun getComments(
        note_id: Int,
        sort: SortTypes = SortTypes.DESC,
        offset: Int = 0,
        count: Int = -1
    ): List<TNoteComments>

    fun restoreComment(comment_id: Int): TNoteComments
}