package eramo.resultgate.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.remote.dto.auth.ValidateForgetPasswordResponse
import eramo.resultgate.domain.usecase.auth.ForgotPassUseCase
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordStepTwoViewModel @Inject constructor(
    private val forgotPassUseCase: ForgotPassUseCase
) : ViewModel() {

    private val _codeValidationState = MutableStateFlow<UiState<ValidateForgetPasswordResponse>>(UiState.Empty())
    val codeValidationState: StateFlow<UiState<ValidateForgetPasswordResponse>> = _codeValidationState

    private var codeValidationJob: Job? = null

    fun cancelRequest() = codeValidationJob?.cancel()

    fun validateCode(code: String, email: String) {
        codeValidationJob?.cancel()
        codeValidationJob = viewModelScope.launch {
            withContext(coroutineContext) {
                forgotPassUseCase.validateCode(code, email).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _codeValidationState.value =
                                    UiState.Success(data = result.data)
                            } ?: run { _codeValidationState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _codeValidationState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _codeValidationState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}