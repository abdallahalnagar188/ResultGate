package eramo.tahoon.domain.validation

import eramo.tahoon.R
import eramo.tahoon.util.state.UiText
import eramo.tahoon.util.state.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidatePhone @Inject constructor() {
    operator fun invoke(phone: String): ValidationResult {
        if (phone.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.txt_phone_is_required)
            )
        }

        if (phone.length < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.txt_please_enter_a_valid_phone_number)
            )
        }

        if (phone.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.must_not_contains_a_letter)
            )
        }

        if (phone.any { it.isWhitespace() }) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.must_not_contains_a_space)
            )
        }

        return ValidationResult(successful = true)
    }
}