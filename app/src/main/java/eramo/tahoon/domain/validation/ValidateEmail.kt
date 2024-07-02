package eramo.tahoon.domain.validation

import android.util.Patterns
import eramo.tahoon.R
import eramo.tahoon.util.state.UiText
import eramo.tahoon.util.state.ValidationResult
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