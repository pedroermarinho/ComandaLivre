package io.github.pedroermarinho.comandalivre.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.product.ProductCategoryResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product.SearchProductCategoryUseCase
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductCategoryMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/comandalivre/product-categories")
@Tag(name = "Categorias de Produtos", description = "Endpoints para gerenciamento de categorias de produtos do sistema.")
class ProductCategoryController(
    private val searchProductCategoryUseCase: SearchProductCategoryUseCase,
    private val productCategoryMapper: ProductCategoryMapper,
) {
    @Operation(
        summary = "Buscar categorias de produtos",
        description = "Retorna uma lista paginada de todas as categorias de produtos disponíveis.",
    )
    @GetMapping
    fun getAll(
        @Parameter(description = "Número da página (inicia em 0).", example = "0")
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @Parameter(description = "Tamanho da página.", example = "10")
        @RequestParam(defaultValue = "10") pageSize: Int,
        @Parameter(description = "Campos para ordenação (ex: name, key).")
        @RequestParam(required = false) sort: List<String>?,
        @Parameter(description = "Direção da ordenação (asc ou desc).")
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<ProductCategoryResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchProductCategoryUseCase.getAll(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { productCategoryMapper.toResponse(it) })
    }

    @Operation(
        summary = "Buscar todas as categorias de produtos",
        description = "Retorna uma lista de todas as categorias de produtos disponíveis.",
    )
    @GetMapping("/list")
    fun getAllList(): ResponseEntity<List<ProductCategoryResponse>> {
        val result = searchProductCategoryUseCase.getAll().getOrThrow()
        return ResponseEntity.ok(result.map { productCategoryMapper.toResponse(it) })
    }
}
