package eramo.tahoon.util.state

sealed class UiState<T>(val data: T? = null, val message: UiText? = null) {
    class Success<T>(data: T?) : UiState<T>(data)
    class Error<T>(message: UiText, data: T? = null) : UiState<T>(data, message)
    class Loading<T> : UiState<T>()
    class Empty<T> : UiState<T>()
}
