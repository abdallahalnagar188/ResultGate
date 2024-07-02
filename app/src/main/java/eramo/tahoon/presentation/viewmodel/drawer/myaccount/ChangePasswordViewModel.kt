package eramo.tahoon.presentation.viewmodel.drawer.myaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.auth.UpdatePasswordResponse
import eramo.tahoon.domain.usecase.auth.UpdatePassUseCase
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val updatePassUseCase: UpdatePassUseCase
) : ViewModel() {

    private val _updatePassState = MutableStateFlow<UiState<UpdatePasswordResponse>>(UiState.Empty())
    val updatePassState: StateFlow<UiState<UpdatePasswordResponse>> = _updatePassState

    private var updatePassJob: Job? = null

    fun cancelRequest() = updatePassJob?.cancel()

    fun updatePass(
        password: String,
        new_password: String,
        confirm_password: String
    ) {
        updatePassJob?.cancel()
        updatePassJob = viewModelScope.launch {
            withContext(coroutineContext) {
                updatePassUseCase(password, new_password, confirm_password).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _updatePassState.value = UiState.Success(it)
                            } ?: run { _updatePassState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _updatePassState.value = UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _updatePassState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}