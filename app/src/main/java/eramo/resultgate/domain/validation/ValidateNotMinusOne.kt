package eramo.resultgate.domain.validation

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import eramo.resultgate.R
import eramo.resultgate.util.state.UiText
import eramo.resultgate.util.state.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ValidateNotMinusOne @Inject constructor(@ApplicationContext val context: Context) {
    operator fun invoke(id: Int): ValidationResult {
        if (id == -1) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.select)
            )
        }

        return ValidationResult(successful = true)
    }
}