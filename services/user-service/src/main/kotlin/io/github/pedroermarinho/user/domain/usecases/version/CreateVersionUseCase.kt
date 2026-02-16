package io.github.pedroermarinho.user.domain.usecases.version

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.pedroermarinho.shared.annotations.UseCase
import io.github.pedroermarinho.user.domain.entities.VersionEntity
import io.github.pedroermarinho.user.domain.forms.version.VersionForm
import io.github.pedroermarinho.user.domain.repositories.VersionRepository
import io.github.pedroermarinho.shared.valueobject.EntityId
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
