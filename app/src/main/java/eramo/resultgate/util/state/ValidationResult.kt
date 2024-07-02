package eramo.resultgate.util.state

data class ValidationResult(
    val successful: Boolean = false,
    val errorMessage: UiText? = null
)
