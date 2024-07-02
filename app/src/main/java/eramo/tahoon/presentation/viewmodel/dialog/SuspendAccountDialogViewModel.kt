package eramo.tahoon.presentation.viewmodel.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.drawer.myaccount.SuspendAccountResponse
import eramo.tahoon.domain.repository.AuthRepository
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SuspendAccountDialogViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _suspendAccountState = MutableStateFlow<UiState<SuspendAccountResponse>>(UiState.Empty())
    val suspendAccountState: StateFlow<UiState<SuspendAccountResponse>> = _suspendAccountState

    private var suspendAccountJob: Job? = null

    fun cancelRequest() = suspendAccountJob?.cancel()

    fun suspendAccount() {
        suspendAccountJob?.cancel()
        suspendAccountJob = viewModelScope.launch {
            withContext(coroutineContext) {
                authRepository.suspendAccount().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _suspendAccountState.value = UiState.Success(it)
                            } ?: run { _suspendAccountState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _suspendAccountState.value = UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _suspendAccountState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}