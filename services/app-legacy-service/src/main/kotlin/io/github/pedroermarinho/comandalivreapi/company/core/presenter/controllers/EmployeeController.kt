package io.github.pedroermarinho.comandalivreapi.company.core.presenter.controllers

import io.github.pedroermarinho.comandalivreapi.company.core.domain.response.employee.EmployeeResponse
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.SearchEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.domain.usecases.employee.UpdateEmployeeUseCase
import io.github.pedroermarinho.comandalivreapi.company.core.infra.mappers.EmployeeMapper
import io.github.pedroermarinho.shared.dtos.page.PageDTO
import io.github.pedroermarinho.shared.dtos.page.PageableDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/company/employees")
@Tag(name = "Funcionários", description = "Gerenciamento de funcionários do sistema")
class EmployeeController(
    private val searchEmployeeUseCase: SearchEmployeeUseCase,
    private val updateEmployeeUseCase: UpdateEmployeeUseCase,
    private val employeeMapper: EmployeeMapper,
) {
    @Operation(summary = "Buscar cargos do usuário logado", description = "Buscar cargos do usuário logado")
    @GetMapping("/my-employees")
    fun getMyEmployees(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
    ): ResponseEntity<PageDTO<EmployeeResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchEmployeeUseCase.getMyEmployees(pageable).getOrThrow()
        return ResponseEntity.ok(result.map { employeeMapper.toResponse(it) })
    }

    @Operation(summary = "Buscar funcionário do usuário logado por ID do restaurante", description = "Buscar funcionário do usuário logado por ID do restaurante")
    @GetMapping("/by-company/{companyId}")
    fun getMyEmployeeByCompanyId(
        @PathVariable companyId: UUID,
    ): ResponseEntity<EmployeeResponse> {
        val result = searchEmployeeUseCase.getByCompanyId(companyId).getOrThrow()
        return ResponseEntity.ok(employeeMapper.toResponse(result))
    }

    @Operation(
        summary = "Verifica se o usuário tem relação com alguma empresa",
        description = "Verifica se o usuário possui vínculo empregatício ativo",
    )
    @GetMapping("/has-company")
    fun hasCompanyRelation(): ResponseEntity<Boolean> {
        val result = searchEmployeeUseCase.hasActiveCompanyRelation().getOrThrow()
        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "Buscar funcionários por ID do restaurante",
        description = "Buscar funcionários por ID do restaurante",
    )
    @GetMapping("/company/{companyId}")
    fun getEmployeesByCompany(
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
        @RequestParam(required = false) sort: List<String>?,
        @RequestParam(required = false) direction: String?,
        @PathVariable companyId: UUID,
    ): ResponseEntity<PageDTO<EmployeeResponse>> {
        val pageable =
            PageableDTO(
                sort = sort,
                direction = direction,
                pageNumber = pageNumber,
                pageSize = pageSize,
            )
        val result = searchEmployeeUseCase.getAll(pageable, companyId).getOrThrow()
        return ResponseEntity.ok(result.map { employeeMapper.toResponse(it) })
    }

    @Operation(summary = "Buscar funcionário por ID", description = "Buscar funcionário por ID")
    @GetMapping("/{id}")
    fun getEmployeeById(
        @PathVariable id: UUID,
    ): ResponseEntity<EmployeeResponse> {
        val result = searchEmployeeUseCase.getById(id).getOrThrow()
        return ResponseEntity.ok(employeeMapper.toResponse(result))
    }

    @Operation(summary = "Alterar status do funcionário", description = "Alterar status do funcionário")
    @PatchMapping("/{id}/change-status/{status}")
    fun changeStatusEmployee(
        @PathVariable id: UUID,
        @PathVariable status: Boolean,
    ): ResponseEntity<Unit> {
        val result = updateEmployeeUseCase.changeStatus(id, status).getOrThrow()
        return ResponseEntity.ok(result)
    }
}
