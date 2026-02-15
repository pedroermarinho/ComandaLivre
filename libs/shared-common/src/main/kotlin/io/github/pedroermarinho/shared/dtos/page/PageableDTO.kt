package io.github.pedroermarinho.shared.dtos.page

data class PageableDTO(
    val pageNumber: Int = 0,
    val pageSize: Int = 10,
    val search: String? = null,
    val sort: List<String>? = null,
    val direction: String? = "asc",
) {
    val offset: Long
        get() = pageNumber * pageSize.toLong()
}
