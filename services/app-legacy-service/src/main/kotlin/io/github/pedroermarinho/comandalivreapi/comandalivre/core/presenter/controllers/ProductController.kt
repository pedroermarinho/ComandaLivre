package io.github.pedroermarinho.comandalivreapi.comandalivre.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.ProductModifierGroupForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.ProductModifierOptionForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.product.ProductCreateRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.request.product.ProductUpdateRequest
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.product.ProductResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.response.product.ProductWithModifiersResponse
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.product.*
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductMapper
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.infra.mappers.ProductWithModifiersMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/api/v1/comandalivre/products")
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos do sistema.")
class ProductController(
    private val searchProductUseCase: SearchProductUseCase,
    private val createProductUserCase: CreateProductUseCase,
    private val updateProductUserCase: UpdateProductUseCase,
    private val deleteProductUserCase: DeleteProductUseCase,
    private val updateProductImageUseCase: UpdateProductImageUseCase,
    private val manageProductModifiersUseCase: ManageProductModifiersUseCase,
    private val productMapper: ProductMapper,
    private val productWithModifiersMapper: ProductWithModifiersMapper,
) {
    @Operation(
        summary = "Buscar produtos",
        description = "Retorna uma lista paginada de produtos, com opção de filtro por empresa.",
    )
    @GetMapping
    fun getAll(
        @Parameter(description = "Número da página (inicia em 0).", example = "0")
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @Parameter(description = "Tamanho da página.", example = "10")
        @RequestParam(defaultValue = "10") pageSize: Int,
        @Parameter(description = "Campos para ordenação (ex: name, price).")
        @RequestParam(required = false) sort: List<String>?,
        @Parameter(description = "Direção da ordenação (asc ou desc).")
        @RequestParam(required = false) direction: String?,
        @Parameter(
            description = "ID público da empresa para filtrar os produtos.",
            required = true,
        ) @RequestParam(required = true) companyId: UUID,
    ): ResponseEntity<PageDTO<ProductResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )

        val result = searchProductUseCase.getByCompany(pageable, companyId).getOrThrow()
        return ResponseEntity.ok(result.map { productMapper.toResponse(it) })
    }

    @Operation(
        summary = "Buscar produto por ID",
        description = "Retorna os detalhes de um produto específico pelo seu ID público, incluindo modificadores.",
    )
    @GetMapping("/{id}")
    fun getById(
        @Parameter(description = "ID público do produto.", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<ProductWithModifiersResponse> {
        val result = searchProductUseCase.getByIdWithModifiers(id).getOrThrow()
        return ResponseEntity.ok(productWithModifiersMapper.toResponse(result))
    }

    @Operation(
        summary = "Criar produto",
        description = "Cria um novo produto no cardápio.",
    )
    @PostMapping
    @Transactional
    fun create(
        @Parameter(description = "Dados para criação do produto.", required = true)
        @Valid
        @RequestBody productCreateRequest: ProductCreateRequest,
        uriBuilder: UriComponentsBuilder,
    ): ResponseEntity<Unit> {
        val result = createProductUserCase.create(productCreateRequest).getOrThrow()
        val uri = uriBuilder.path("/api/v1/products/{publicId}").buildAndExpand(result.publicId).toUri()
        return ResponseEntity.created(uri).build()
    }

    @Operation(
        summary = "Atualizar produto",
        description = "Atualiza os dados de um produto existente.",
    )
    @PutMapping("/{id}")
    fun update(
        @Parameter(description = "ID público do produto a ser atualizado.", required = true)
        @PathVariable id: UUID,
        @Parameter(description = "Dados para atualização do produto.", required = true)
        @Valid
        @RequestBody productUpdateRequest: ProductUpdateRequest,
    ): ResponseEntity<Unit> {
        updateProductUserCase.update(id, productUpdateRequest).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Atualizar status do produto",
        description = "Altera o status de disponibilidade de um produto (disponível/indisponível).",
    )
    @PatchMapping("/{id}/status/{status}")
    fun updateStatus(
        @Parameter(description = "ID público do produto.", required = true)
        @PathVariable id: UUID,
        @Parameter(description = "Novo status de disponibilidade (true para disponível, false para indisponível).", required = true)
        @PathVariable status: Boolean,
    ): ResponseEntity<Unit> {
        updateProductUserCase.updateStatus(id, status).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Deletar produto",
        description = "Remove um produto do cardápio.",
    )
    @DeleteMapping("/{id}")
    fun delete(
        @Parameter(description = "ID público do produto a ser deletado.", required = true)
        @PathVariable id: UUID,
    ): ResponseEntity<Unit> {
        deleteProductUserCase.execute(id).getOrThrow()
        return ResponseEntity.noContent().build()
    }

    @Operation(
        summary = "Upload de imagem do produto",
        description = "Realiza o upload da imagem principal para um produto específico.",
    )
    @PatchMapping("/{id}/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateImage(
        @Parameter(description = "ID público do produto.", required = true)
        @PathVariable id: UUID,
        @Parameter(description = "Arquivo de imagem a ser enviado (multipart/form-data).", required = true)
        @RequestPart("imageFile") imageFile: MultipartFile,
    ): ResponseEntity<Unit> {
        updateProductImageUseCase.execute(id, imageFile).getOrThrow()
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "Criar grupo de modificadores",
        description = "Cria um novo grupo de modificadores para um produto.",
    )
    @PostMapping("/{productId}/modifier-groups")
    fun createModifierGroup(
        @Parameter(description = "ID público do produto.", required = true)
        @PathVariable productId: UUID,
        @Parameter(description = "Dados para criação do grupo de modificadores.", required = true)
        @Valid
        @RequestBody form: ProductModifierGroupForm,
        uriBuilder: UriComponentsBuilder,
    ): ResponseEntity<Unit> {
        val result = manageProductModifiersUseCase.createGroup(productId, form).getOrThrow()
        val uri = uriBuilder.path("/api/v1/comandalivre/products/modifier-groups/{publicId}").buildAndExpand(result.id.publicId).toUri()
        return ResponseEntity.created(uri).build()
    }

    @Operation(
        summary = "Criar opção de modificador",
        description = "Cria uma nova opção de modificador em um grupo.",
    )
    @PostMapping("/modifier-groups/{groupId}/options")
    fun createModifierOption(
        @Parameter(description = "ID público do grupo de modificadores.", required = true)
        @PathVariable groupId: UUID,
        @Parameter(description = "Dados para criação da opção de modificador.", required = true)
        @Valid
        @RequestBody form: ProductModifierOptionForm,
        uriBuilder: UriComponentsBuilder,
    ): ResponseEntity<Unit> {
        val result = manageProductModifiersUseCase.createOption(groupId, form).getOrThrow()
        val uri = uriBuilder.path("/api/v1/comandalivre/products/modifier-groups/options/{publicId}").buildAndExpand(result.id.publicId).toUri()
        return ResponseEntity.created(uri).build()
    }
}
