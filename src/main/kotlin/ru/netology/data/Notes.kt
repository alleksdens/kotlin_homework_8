package ru.netology.data

data class Notes(
    val id: Int = 0,
    internal var title: String,
    internal var text: String,
    internal var date: Int,
    internal var comments: Int = 0,
    internal var read_comments: Int = 0,
    internal var view_url: String? = null,
    internal var can_comment: Int = 1,
    internal var removed: Boolean = false
)