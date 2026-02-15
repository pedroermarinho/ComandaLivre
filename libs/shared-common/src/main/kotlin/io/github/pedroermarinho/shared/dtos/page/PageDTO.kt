package io.github.pedroermarinho.shared.dtos.page

data class PageDTO<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
    val numberOfElements: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val first: Boolean,
    val last: Boolean,
    val search: String? = null,
    val sort: List<String>? = null,
    val direction: String? = null,
) {
    fun <R> map(transform: (T) -> R): PageDTO<R> =
        PageDTO(
            content = content.map(transform),
            totalElements = totalElements,
            totalPages = totalPages,
            number = number,
            size = size,
            numberOfElements = numberOfElements,
            hasPrevious = hasPrevious,
            hasNext = hasNext,
            first = first,
            last = last,
            search = search,
            sort = sort,
            direction = direction,
        )
}
