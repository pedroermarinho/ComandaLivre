package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.usecases.dailyactivity

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyActivityRepository
import io.github.pedroermarinho.comandalivreapi.shared.core.domain.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class DeleteDailyActivityUseCase(
    private val dailyActivityRepository: DailyActivityRepository,
) {
    fun execute(id: UUID): Result<Unit> = dailyActivityRepository.delete(id)
}
