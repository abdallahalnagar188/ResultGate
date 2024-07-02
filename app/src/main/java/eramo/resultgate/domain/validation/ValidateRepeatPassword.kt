package eramo.resultgate.domain.validation

import eramo.resultgate.R
import eramo.resultgate.util.state.UiText
import eramo.resultgate.util.state.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateRepeatPassword @Inject constructor() {
    operator fun invoke(password: String,rePassword: String): ValidationResult {
        if (rePassword.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.can_not_be_empty)
            )
        }

        if (password != rePassword) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.txt_password_not_match)
            )
        }

        return ValidationResult(successful = true)
    }
}