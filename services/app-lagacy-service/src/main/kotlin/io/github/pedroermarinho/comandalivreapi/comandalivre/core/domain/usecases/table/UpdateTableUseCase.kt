package io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.usecases.table

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.table.TableUpdateForm
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.repositories.TableRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class UpdateTableUseCase(
    private val tableRepository: TableRepository,
    private val searchTableUseCase: SearchTableUseCase,
) {
    private val log = KotlinLogging.logger {}

    fun update(
        publicId: UUID,
        form: TableUpdateForm,
    ): Result<Unit> =
        runCatching {
            val table = searchTableUseCase.getEntityById(publicId).getOrThrow()
            if (table.name.value != form.name) {
                searchTableUseCase
                    .checkExistsByNameAndCompanyId(
                        name = form.name,
                        companyId = table.companyId.value,
                    ).getOrThrow()
            }
            tableRepository
                .save(
                    table.update(
                        name = form.name,
                        numPeople = form.numPeople,
                        description = form.description,
                    ),
                ).getOrThrow()
        }
}
