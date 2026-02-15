package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class DeleteTableUseCase(
    private val searchTableUseCase: SearchTableUseCase,
    private val tableRepository: TableRepository,
) {
    fun execute(id: UUID): Result<Unit> =
        runCatching {
            val table = searchTableUseCase.getEntityById(id).getOrThrow()
            tableRepository.save(table.delete())
        }
}
