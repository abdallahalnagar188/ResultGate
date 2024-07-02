package eramo.tahoon.util.state

data class ValidationResult(
    val successful: Boolean = false,
    val errorMessage: UiText? = null
)
