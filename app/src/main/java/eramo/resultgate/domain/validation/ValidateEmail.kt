package eramo.resultgate.domain.validation

import android.util.Patterns
import eramo.resultgate.R
import eramo.resultgate.util.state.UiText
import eramo.resultgate.util.state.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateEmail @Inject constructor() {
    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.txt_email_is_required)
            )
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.txt_please_enter_a_valid_email_address)
            )
        }

        return ValidationResult(successful = true)
    }
}