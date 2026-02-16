package io.github.pedroermarinho.comandalivre.domain.usecases.order

import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.enums.OrderStatusEnum
import io.github.pedroermarinho.comandalivreapi.comandalivre.core.domain.forms.CancelForm
import io.github.pedroermarinho.shared.annotations.UseCase
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@UseCase
class CancelOrderUseCase(
    private val changeStatusOrderUseCase: ChangeStatusOrderUseCase,
) {
    fun execute(
        orderId: UUID,
        form: CancelForm,
    ): Result<Unit> =
        runCatching {
            changeStatusOrderUseCase
                .execute(
                    publicId = orderId,
                    status = OrderStatusEnum.ITEM_CANCELED.value,
                    reason = form.reason,
                ).getOrThrow()
        }
}
