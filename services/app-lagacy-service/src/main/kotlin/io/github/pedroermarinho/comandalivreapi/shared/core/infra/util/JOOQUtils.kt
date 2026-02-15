package io.github.pedroermarinho.comandalivreapi.shared.core.infra.util

import com.google.common.base.CaseFormat
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageDTO
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.dtos.page.PageableDTO
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.SelectJoinStep
import org.jooq.SortField
import org.jooq.Table
import org.jooq.TableField
import org.springframework.data.domain.Sort
import kotlin.Result
import kotlin.collections.ArrayList
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.collections.emptyList
import kotlin.collections.firstOrNull
import kotlin.collections.map
import kotlin.getOrElse
import kotlin.map

fun getSortFields(
    sortSpecification: List<String>?,
    table: Table<*>,
    defaultDirection: String? = "asc",
): Result<Collection<SortField<*>>> {
    val direction =
        if (defaultDirection == null || defaultDirection.equals("asc", ignoreCase = true)) {
            Sort.Direction.ASC
        } else {
            Sort.Direction.DESC
        }

    val querySortFields: ArrayList<SortField<*>> = ArrayList()

    if (sortSpecification == null) {
        return Result.failure(IllegalArgumentException("Sort specification is null"))
    }

    for (order in sortSpecification) {
        val parts = order.split(",").map { it.trim() }
        val sortFieldName = parts[0]

        val formattedFieldName =
            CaseFormat.LOWER_CAMEL
                .converterTo(CaseFormat.LOWER_UNDERSCORE)
                .convert(sortFieldName)
                ?.uppercase()
                ?: return Result.failure(IllegalArgumentException("Field $sortFieldName not found in table ${table.name}"))

        // Obtém o TableField e converte para SortField com a direção especificada
        val sortField =
            getTableField(formattedFieldName, table)
                .map { tableField -> convertTableFieldToSortField(tableField, direction) }
                .getOrElse { return Result.failure(it) }

        querySortFields.add(sortField)
    }

    return Result.success(querySortFields)
}

fun getTableField(
    sortFieldName: String,
    table: Table<*>,
): Result<TableField<*, *>> {
    try {
        val property =
            table::class
                .members
                .firstOrNull { it.name == sortFieldName }
                ?: return Result.failure(IllegalArgumentException("Field $sortFieldName not found in table ${table.name}"))

        val tableField =
            property.call(table) as? TableField<*, *>
                ?: return Result.failure(IllegalArgumentException("Field $sortFieldName is not a TableField in table ${table.name}"))

        return Result.success(tableField)
    } catch (e: NoSuchFieldException) {
        return Result.failure(IllegalArgumentException("Field $sortFieldName not found in table ${table.name}"))
    } catch (e: IllegalAccessException) {
        return Result.failure(IllegalArgumentException("Field $sortFieldName not accessible in table ${table.name}"))
    }
}

fun convertTableFieldToSortField(
    tableField: TableField<*, *>,
    sortDirection: Sort.Direction,
): SortField<*> =
    if (sortDirection == Sort.Direction.ASC) {
        tableField.asc()
    } else {
        tableField.desc()
    }

fun <T> fetchPage(
    dsl: DSLContext,
    baseQuery: SelectJoinStep<Record>,
    condition: Condition,
    pageable: PageableDTO,
    orderBy: Collection<SortField<*>>? = null,
    mapper: (Record) -> T,
): PageDTO<T> {
    // Obtém o total de registros antes da paginação
    val totalElements = dsl.fetchCount(baseQuery.where(condition))

    // Se não houver elementos, retorna uma página vazia
    if (totalElements == 0) {
        return PageDTO(
            content = emptyList(),
            totalElements = 0,
            totalPages = 1,
            number = pageable.pageNumber,
            size = pageable.pageSize,
            numberOfElements = 0,
            hasPrevious = false,
            hasNext = false,
            first = true,
            last = true,
            search = pageable.search,
            sort = pageable.sort,
            direction = pageable.direction,
        )
    }

    // Executa a query paginada
    val records =
        baseQuery
            .where(condition)
            .orderBy(orderBy)
            .offset(pageable.pageNumber * pageable.pageSize)
            .limit(pageable.pageSize)
            .fetch()

    val content = records.map { mapper(it) }

    // Calcula corretamente o total de páginas
    val totalPages = kotlin.math.ceil(totalElements.toDouble() / pageable.pageSize).toInt()

    // Verifica se há próxima página corretamente
    val hasNext = (pageable.pageNumber + 1) * pageable.pageSize < totalElements
    val hasPrevious = pageable.pageNumber > 0
    val first = pageable.pageNumber == 0
    val last = !hasNext

    return PageDTO(
        content = content,
        totalElements = totalElements.toLong(),
        totalPages = totalPages,
        number = pageable.pageNumber,
        size = pageable.pageSize,
        numberOfElements = content.size,
        hasPrevious = hasPrevious,
        hasNext = hasNext,
        first = first,
        last = last,
        search = pageable.search,
        sort = pageable.sort,
        direction = pageable.direction,
    )
}
