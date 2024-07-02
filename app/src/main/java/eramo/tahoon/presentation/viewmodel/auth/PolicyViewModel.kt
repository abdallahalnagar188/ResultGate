package eramo.tahoon.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.drawer.PolicyInfoDto
import eramo.tahoon.domain.usecase.drawer.GetAppPolicyUseCase
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PolicyViewModel @Inject constructor(
    private val getAppPolicyUseCase: GetAppPolicyUseCase
) : ViewModel() {

    //    private val _getAppPolicyState = MutableStateFlow<UiState<List<PolicyInfoModel>>>(UiState.Empty())
    private val _getAppPolicyState = MutableStateFlow<UiState<List<PolicyInfoDto>>>(UiState.Empty())
    val getAppPolicyState: StateFlow<UiState<List<PolicyInfoDto>>> = _getAppPolicyState
//    val getAppPolicyState: StateFlow<UiState<List<PolicyInfoModel>>> = _getAppPolicyState

    private var getAppPolicyJob: Job? = null

    init {
        getAppPolicyApp()
    }

    fun cancelRequest() = getAppPolicyJob?.cancel()

    private fun getAppPolicyApp() {
        getAppPolicyJob?.cancel()
        getAppPolicyJob = viewModelScope.launch {
            withContext(coroutineContext) {
                getAppPolicyUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _getAppPolicyState.value = UiState.Success(it)
                            } ?: run { _getAppPolicyState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _getAppPolicyState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _getAppPolicyState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}