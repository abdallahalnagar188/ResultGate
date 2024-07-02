package eramo.resultgate.presentation.viewmodel.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.remote.dto.drawer.MyAppInfoResponse
import eramo.resultgate.domain.usecase.drawer.GetAppInfoUseCase
import eramo.resultgate.util.Constants
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AboutUsViewModel @Inject constructor(
    private val getAppInfoUseCase: GetAppInfoUseCase
) : ViewModel() {

    private val _getAppInfoState = MutableStateFlow<UiState<MyAppInfoResponse>>(UiState.Empty())
    val getAppInfoState: StateFlow<UiState<MyAppInfoResponse>> = _getAppInfoState

    private var getAppInfoJob: Job? = null

    init {
        getAppInfo()
    }

    fun cancelRequest() = getAppInfoJob?.cancel()

    private fun getAppInfo() {
        getAppInfoJob?.cancel()
        getAppInfoJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                getAppInfoUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _getAppInfoState.value = UiState.Success(it)
                            } ?: run { _getAppInfoState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _getAppInfoState.value = UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _getAppInfoState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}