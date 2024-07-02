package eramo.tahoon.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.auth.ValidateForgetPasswordResponse
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
class ForgetPasswordStepThreeViewModel @Inject constructor(
    private val forgotPassUseCase: ForgotPassUseCase
) : ViewModel() {

    private val _newPasswordState = MutableStateFlow<UiState<ValidateForgetPasswordResponse>>(UiState.Empty())
    val newPasswordState: StateFlow<UiState<ValidateForgetPasswordResponse>> = _newPasswordState

    private var newPasswordJob: Job? = null

    fun cancelRequest() = newPasswordJob?.cancel()

    fun changePassword( password: String, rePassword: String, email: String) {
        newPasswordJob?.cancel()
        newPasswordJob = viewModelScope.launch {
            withContext(coroutineContext) {
                forgotPassUseCase.changePassword( password, rePassword, email).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _newPasswordState.value =
                                    UiState.Success(data = result.data)
                            } ?: run { _newPasswordState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _newPasswordState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _newPasswordState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}
