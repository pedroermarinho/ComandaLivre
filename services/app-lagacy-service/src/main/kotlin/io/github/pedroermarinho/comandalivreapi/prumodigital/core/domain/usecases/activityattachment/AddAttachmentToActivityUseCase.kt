package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.activityattachment

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.ActivityAttachmentForm
import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.ActivityAttachmentRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional

@Transactional
@UseCase
class AddAttachmentToActivityUseCase(
    private val activityAttachmentRepository: ActivityAttachmentRepository,
) {
    fun execute(form: ActivityAttachmentForm): Result<Unit> =
        runCatching {
            activityAttachmentRepository.create(form).getOrThrow()
        }
}
