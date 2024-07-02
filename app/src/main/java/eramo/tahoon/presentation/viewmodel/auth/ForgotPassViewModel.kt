package eramo.tahoon.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.auth.forget.GiveMeEmailResponse
import eramo.tahoon.domain.usecase.auth.ForgotPassUseCase
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ForgotPassViewModel @Inject constructor(
    private val forgotPassUseCase: ForgotPassUseCase
) : ViewModel() {

    private val _forgotPassState = MutableStateFlow<UiState<GiveMeEmailResponse>>(UiState.Empty())
    val forgotPassState: StateFlow<UiState<GiveMeEmailResponse>> = _forgotPassState

    private var forgotPassJob: Job? = null

    fun cancelRequest() = forgotPassJob?.cancel()

    fun forgotPassApp(email: String) {
        forgotPassJob?.cancel()
        forgotPassJob = viewModelScope.launch {
            withContext(coroutineContext) {
                forgotPassUseCase(email).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _forgotPassState.value =
                                    UiState.Success(data = result.data)
                            } ?: run { _forgotPassState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _forgotPassState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _forgotPassState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}