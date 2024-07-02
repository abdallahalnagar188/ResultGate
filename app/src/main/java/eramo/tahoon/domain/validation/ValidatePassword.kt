package eramo.tahoon.domain.validation

import eramo.tahoon.R
import eramo.tahoon.util.state.UiText
import eramo.tahoon.util.state.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidatePassword @Inject constructor() {
    operator fun invoke(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.txt_password_is_required)
            )
        }

        if (password.length < 8) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.must_be_8_characters_at_least)
            )
        }

//        val containsLetterAndDigit = password.any { it.isLetter() } && password.any { it.isDigit() }
//        if (!containsLetterAndDigit) {
//            return ValidationResult(
//                successful = false,
//                errorMessage = UiText.StringResource(R.string.must_contains_a_letter_a_digit_at_least)
//            )
//        }

        return ValidationResult(successful = true)
    }
}