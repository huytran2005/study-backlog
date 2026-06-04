package com.example.brainnote.feature.auth.forgotpassword

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PasswordValidatorTest {

    private lateinit var validator: PasswordValidator

    @Before
    fun setup() {
        validator = PasswordValidator()
    }

    @Test
    fun validate_emptyPassword_returnsError() {
        val result = validator.validate("")
        assertFalse(result.isValid)
        assertEquals("Password cannot be empty.", result.errorMessage)
    }

    @Test
    fun validate_shortPassword_returnsError() {
        val result = validator.validate("A1a")
        assertFalse(result.isValid)
        assertEquals("At least 8 characters required.", result.errorMessage)
    }

    @Test
    fun validate_noUppercase_returnsError() {
        val result = validator.validate("password123")
        assertFalse(result.isValid)
        assertEquals("Must contain at least one uppercase letter.", result.errorMessage)
    }

    @Test
    fun validate_noLowercase_returnsError() {
        val result = validator.validate("PASSWORD123")
        assertFalse(result.isValid)
        assertEquals("Must contain at least one lowercase letter.", result.errorMessage)
    }

    @Test
    fun validate_noNumber_returnsError() {
        val result = validator.validate("PasswordStr")
        assertFalse(result.isValid)
        assertEquals("Must contain at least one number.", result.errorMessage)
    }

    @Test
    fun validate_validPassword_returnsTrue() {
        val result = validator.validate("Password123")
        assertTrue(result.isValid)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun passwordsMatch_matching_returnsTrue() {
        assertTrue(validator.passwordsMatch("Password123", "Password123"))
    }

    @Test
    fun passwordsMatch_mismatching_returnsFalse() {
        assertFalse(validator.passwordsMatch("Password123", "Password124"))
    }

    @Test
    fun calculateStrength_invalid_returnsWeak() {
        assertEquals(PasswordStrength.WEAK, validator.calculateStrength("pass"))
    }

    @Test
    fun calculateStrength_validButShortNoSpecial_returnsMedium() {
        assertEquals(PasswordStrength.MEDIUM, validator.calculateStrength("Password123"))
    }

    @Test
    fun calculateStrength_validAndLong_returnsStrong() {
        assertEquals(PasswordStrength.STRONG, validator.calculateStrength("LongPassword123"))
    }

    @Test
    fun calculateStrength_validAndSpecial_returnsStrong() {
        assertEquals(PasswordStrength.STRONG, validator.calculateStrength("Pass123!"))
    }
}

