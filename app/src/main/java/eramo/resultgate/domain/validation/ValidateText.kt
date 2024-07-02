package eramo.resultgate.domain.validation

import eramo.resultgate.R
import eramo.resultgate.util.state.UiText
import eramo.resultgate.util.state.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateText @Inject constructor() {
    operator fun invoke(text: String): ValidationResult {
        if (text.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.can_not_be_empty)
            )
        }

        val containsDigit = text.any { it.isDigit() }
        if (containsDigit) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.Can_not_contains_a_number)
            )
        }

        return ValidationResult(successful = true)
    }
}