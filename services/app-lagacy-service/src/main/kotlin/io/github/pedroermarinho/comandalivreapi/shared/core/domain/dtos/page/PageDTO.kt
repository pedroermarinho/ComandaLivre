package io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Representação de uma página de dados")
data class PageDTO<T>(
    @param:Schema(description = "Conteúdo da página")
    val content: List<T>,
    @param:Schema(description = "Total de elementos encontrados")
    val totalElements: Long,
    @param:Schema(description = "Total de páginas")
    val totalPages: Int,
    @param:Schema(description = "Número da página atual (zero-indexed)")
    val number: Int,
    @param:Schema(description = "Tamanho da página")
    val size: Int,
    @param:Schema(description = "Número de elementos na página atual")
    val numberOfElements: Int,
    @param:Schema(description = "Indica se existe página anterior")
    val hasPrevious: Boolean,
    @param:Schema(description = "Indica se existe próxima página")
    val hasNext: Boolean,
    @param:Schema(description = "Indica se esta é a primeira página")
    val first: Boolean,
    @param:Schema(description = "Indica se esta é a última página")
    val last: Boolean,
    @param:Schema(description = "Query de pesquisa aplicada, se houver")
    val search: String? = null,
    @param:Schema(description = "Campo(s) de ordenação, se aplicável")
    val sort: List<String>? = null,
    @param:Schema(description = "Direção da ordenação (asc/desc), se aplicável")
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
