package eramo.resultgate.domain.validation

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import eramo.resultgate.R
import eramo.resultgate.util.state.UiText
import eramo.resultgate.util.state.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateNotZero @Inject constructor(@ApplicationContext val context: Context) {
    operator fun invoke(id: Int, name: UiText): ValidationResult {
        if (id == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.select_a_s, name.asString(context))
            )
        }

        return ValidationResult(successful = true)
    }
}