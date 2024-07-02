package eramo.tahoon.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.domain.model.auth.OnBoardingModel
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
class OnBoardingViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _onBoardingStateState = MutableStateFlow<UiState<List<OnBoardingModel>>>(UiState.Empty())
    val onBoardingStateStateDto: StateFlow<UiState<List<OnBoardingModel>>> = _onBoardingStateState

    private var onBoardingStateJob: Job? = null

    fun cancelRequest() = onBoardingStateJob?.cancel()

    fun getOnBoarding() {
        onBoardingStateJob?.cancel()
        onBoardingStateJob = viewModelScope.launch {
            withContext(coroutineContext) {
                repository.onBoardingScreens().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _onBoardingStateState.value =
                                    UiState.Success(data = result.data)
                            } ?: run { _onBoardingStateState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _onBoardingStateState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _onBoardingStateState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}