package io.github.pedroermarinho.comandalivreapi.prumodigital.core.domain.forms.project

import java.math.BigDecimal
import java.time.LocalDate

data class ProjectCreateForm(
    val name: String,
    val code: String,
    val clientName: String? = null,
    val budget: BigDecimal? = null,
    val startDatePlanned: LocalDate? = null,
    val endDatePlanned: LocalDate? = null,
    val companyId: Int,
    val addressId: Int? = null,
    val plannedStartDate: LocalDate? = null,
    val plannedEndDate: LocalDate? = null,
    val actualStartDate: LocalDate? = null,
    val actualEndDate: LocalDate? = null,
    val projectStatusId: Int,
    val description: String? = null,
)
