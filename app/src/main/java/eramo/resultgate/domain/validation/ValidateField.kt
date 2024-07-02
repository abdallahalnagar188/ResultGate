package eramo.resultgate.domain.validation

import eramo.resultgate.R
import eramo.resultgate.util.state.UiText
import eramo.resultgate.util.state.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateField @Inject constructor() {
    operator fun invoke(field: String): ValidationResult {
        if (field.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.can_not_be_empty)
            )
        }

        return ValidationResult(successful = true)
    }
}