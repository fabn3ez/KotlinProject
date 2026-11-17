package com.group5.track.worker

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PropertyValidatorTest {

    @Test
    fun validKenyanPhoneNumbers_areAccepted() {
        val valid = listOf("0712345678", "+254712345678", "254712345678", "0722123456")
        valid.forEach { assertThat(PropertyValidator.isValidPhone(it)).isTrue() }
    }

    @Test
    fun invalidPhoneNumbers_areRejected() {
        val invalid = listOf("0612345678", "071234", "07123456789", "abc", "")
        invalid.forEach { assertThat(PropertyValidator.isValidPhone(it)).isFalse() }
    }

    @Test
    fun negativeOrZeroPrice_isRejected() {
        assertThat(PropertyValidator.isValidPrice(-5000.0)).isFalse()
        assertThat(PropertyValidator.isValidPrice(0.0)).isFalse()
    }

    @Test
    fun positivePrice_isAccepted() {
        assertThat(PropertyValidator.isValidPrice(25_000.0)).isTrue()
    }

    @Test
    fun zeroOrNegativeBedrooms_isRejected() {
        assertThat(PropertyValidator.isValidBedrooms(0)).isFalse()
        assertThat(PropertyValidator.isValidBedrooms(-1)).isFalse()
    }

    @Test
    fun blankTitleOrAddress_isRejected() {
        assertThat(PropertyValidator.isNotBlank("")).isFalse()
        assertThat(PropertyValidator.isNotBlank("   ")).isFalse()
        assertThat(PropertyValidator.isNotBlank("Kilimani")).isTrue()
    }
}
