package com.group5.track.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Tests the validator the app actually uses (domain.PropertyValidator)
class DomainPropertyValidatorTest {

    private fun validate(
        title: String = "Kilimani 2BR",
        address: String = "Kilimani, Nairobi",
        price: String = "25000",
        bedrooms: String = "2",
        phone: String = "0712345678"
    ) = PropertyValidator.validatePropertyInput(title, address, price, bedrooms, phone)

    @Test
    fun validInput_passes() {
        assertThat(validate().isValid).isTrue()
    }

    @Test
    fun blankTitleAndAddress_areRejected() {
        val result = validate(title = " ", address = "")
        assertThat(result.isValid).isFalse()
        assertThat(result.titleError).isNotNull()
        assertThat(result.addressError).isNotNull()
    }

    @Test
    fun zeroNegativeOrNonNumericPrice_isRejected() {
        listOf("0", "-5000", "abc", "").forEach {
            assertThat(validate(price = it).priceError).isNotNull()
        }
    }

    @Test
    fun zeroOrNonNumericBedrooms_isRejected() {
        listOf("0", "-1", "two", "").forEach {
            assertThat(validate(bedrooms = it).bedroomsError).isNotNull()
        }
    }

    @Test
    fun invalidKenyanPhone_isRejected() {
        listOf("0612345678", "071234", "abc", "").forEach {
            assertThat(validate(phone = it).phoneError).isNotNull()
        }
    }

    @Test
    fun validKenyanPhoneFormats_areAccepted() {
        listOf("0712345678", "+254712345678", "254712345678", "0722123456").forEach {
            assertThat(validate(phone = it).phoneError).isNull()
        }
    }
}
