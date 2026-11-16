package com.group5.track.domain

data class ValidationResult(
    val isValid: Boolean,
    val titleError: String? = null,
    val addressError: String? = null,
    val priceError: String? = null,
    val bedroomsError: String? = null,
    val phoneError: String? = null
)

object PropertyValidator {

    private val KENYAN_PHONE_REGEX = Regex(
        "^(?:254|\\+254|0)?(7(?:(?:[129][0-9])|(?:0[0-8])|(?:4[0-35-68])|(?:5[7-9])|(?:6[8-9]))[0-9]{6})$"
    )

    fun validatePropertyInput(
        title: String,
        address: String,
        priceInput: String,
        bedroomsInput: String,
        agentPhone: String
    ): ValidationResult {
        val titleError = if (title.isBlank()) "Property title is required" else null
        val addressError = if (address.isBlank()) "Property address is required" else null

        val price = priceInput.toDoubleOrNull()
        val priceError = when {
            priceInput.isBlank() -> "Monthly rent is required"
            price == null || price <= 0.0 -> "Enter a valid positive price"
            else -> null
        }

        val bedrooms = bedroomsInput.toIntOrNull()
        val bedroomsError = when {
            bedroomsInput.isBlank() -> "Bedroom count is required"
            bedrooms == null || bedrooms <= 0 -> "Bedrooms must be greater than 0"
            else -> null
        }

        val cleanPhone = agentPhone.replace("\\s".toRegex(), "")
        val phoneError = when {
            cleanPhone.isBlank() -> "Agent phone number is required"
            !KENYAN_PHONE_REGEX.matches(cleanPhone) -> "Enter a valid Kenyan phone number (e.g., 0712345678)"
            else -> null
        }

        val isValid = listOf(titleError, addressError, priceError, bedroomsError, phoneError).all { it == null }

        return ValidationResult(
            isValid = isValid,
            titleError = titleError,
            addressError = addressError,
            priceError = priceError,
            bedroomsError = bedroomsError,
            phoneError = phoneError
        )
    }
}
