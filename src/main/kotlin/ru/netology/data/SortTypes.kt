package ru.netology.data

enum class SortTypes(
    val id: Int,
    val text: String
) {
    DESC(0, "По убыванию"),
    ASC(1, "По возрастанию")
}