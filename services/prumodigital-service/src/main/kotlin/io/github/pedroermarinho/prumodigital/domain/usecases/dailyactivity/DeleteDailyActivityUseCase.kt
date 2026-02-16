package io.github.pedroermarinho.prumodigital.domain.usecases.dailyactivity

import io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.repositories.DailyActivityRepository
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@UseCase
class DeleteDailyActivityUseCase(
    private val dailyActivityRepository: DailyActivityRepository,
) {
    fun execute(id: UUID): Result<Unit> = dailyActivityRepository.delete(id)
}
