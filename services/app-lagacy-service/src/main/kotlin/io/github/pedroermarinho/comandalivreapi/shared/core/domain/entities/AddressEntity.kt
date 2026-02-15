package io.github.pedroermarinho.comandalivreapi.shared.core.domain.entities

import io.github.pedroermarinho.comandalivreapi.shared.core.domain.valueobject.*
import java.util.UUID

data class AddressEntity(
    val id: EntityId,
    val street: Street,
    val number: String,
    val zipCode: ZipCode,
    val city: City,
    val state: State,
    val neighborhood: Neighborhood,
    val complement: String?,
    val audit: EntityAudit,
) {
    companion object {
        fun createNew(
            publicId: UUID? = null,
            street: String,
            number: String,
            zipCode: String,
            city: String,
            state: String,
            neighborhood: String,
            complement: String?,
        ): AddressEntity =
            AddressEntity(
                id = EntityId.createNew(publicId = publicId),
                street = Street(street),
                number = number,
                zipCode = ZipCode(zipCode),
                city = City(city),
                state = State(state),
                neighborhood = Neighborhood(neighborhood),
                complement = complement,
                audit = EntityAudit.createNew(),
            )
    }

    fun isNew(): Boolean = id.isNew()

    fun update(
        street: String,
        number: String,
        zipCode: String,
        city: String,
        state: String,
        neighborhood: String,
        complement: String?,
    ): AddressEntity =
        this.copy(
            street = Street(street),
            number = number,
            zipCode = ZipCode(zipCode),
            city = City(city),
            state = State(state),
            neighborhood = Neighborhood(neighborhood),
            complement = complement,
            audit = this.audit.update(),
        )

    fun delete() =
        this.copy(
            audit = this.audit.delete(),
        )
}
