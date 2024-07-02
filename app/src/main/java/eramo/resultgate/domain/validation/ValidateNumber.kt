package eramo.resultgate.domain.validation

import eramo.resultgate.R
import eramo.resultgate.util.state.UiText
import eramo.resultgate.util.state.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateNumber @Inject constructor() {
    operator fun invoke(number: String): ValidationResult {
        if (number.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.can_not_be_empty)
            )
        }

        val containsLetter = number.any { it.isLetter() }
        if (containsLetter) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.can_not_contains_a_letter)
            )
        }

        return ValidationResult(successful = true)
    }
}