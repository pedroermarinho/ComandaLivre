package io.github.pedroermarinho.comandalivreapi.shared.core.domain.usecases.version

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities.VersionEntity
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.forms.version.VersionForm
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.repositories.VersionRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.EntityId
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class CreateVersionUseCase(
    private val repository: VersionRepository,
) {
    private val log = KotlinLogging.logger {}

    fun execute(form: VersionForm): Result<EntityId> =
        repository
            .save(
                VersionEntity.createNew(
                    version = form.version,
                    platform = form.platform.value,
                ),
            ).onSuccess { version ->
                log.info { "Versão do app criada com sucesso: ${version.publicId}, ${form.version} - ${form.platform}" }
            }
}
