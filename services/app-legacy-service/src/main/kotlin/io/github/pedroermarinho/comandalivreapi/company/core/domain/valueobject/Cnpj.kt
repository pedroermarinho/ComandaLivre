package io.github.pedroermarinho.comandalivreapi.company.core.domain.valueobject

import io.github.pedroermarinho.shared.exceptions.BusinessLogicException

@JvmInline
value class Cnpj private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(rawCnpj: String): Cnpj {
            val normalizedCnpj = rawCnpj.replace(Regex("[^0-9]"), "")

            val digits = normalizedCnpj.filter(Char::isDigit)

            if (digits.length != 14) {
                throw BusinessLogicException("CNPJ deve conter 14 dígitos numéricos")
            }

            if (digits.all { it == digits[0] }) {
                throw BusinessLogicException("CNPJ $rawCnpj é inválido")
            }

            if (!isValidCnpj(digits)) {
                throw BusinessLogicException("CNPJ $rawCnpj é inválido")
            }

            return Cnpj(normalizedCnpj)
        }

        fun restore(persistedValue: String): Cnpj = Cnpj(persistedValue)

        private fun isValidCnpj(cnpj: String): Boolean {
            val weights1 = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
            val weights2 = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)

            fun calcDigit(
                weights: IntArray,
                digits: String,
            ): Char {
                val sum = digits.mapIndexed { i, c -> c.digitToInt() * weights[i] }.sum()
                val mod = sum % 11
                return if (mod < 2) '0' else (11 - mod).digitToChar()
            }

            val d1 = calcDigit(weights1, cnpj.substring(0, 12))
            val d2 = calcDigit(weights2, cnpj.substring(0, 12) + d1)

            return cnpj[12] == d1 && cnpj[13] == d2
        }
    }
}
