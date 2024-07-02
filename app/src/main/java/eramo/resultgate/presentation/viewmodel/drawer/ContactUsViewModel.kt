package eramo.resultgate.presentation.viewmodel.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.remote.dto.drawer.ContactUsResponse
import eramo.resultgate.data.remote.dto.drawer.ContactUsSendMessageResponse
import eramo.resultgate.domain.usecase.drawer.ContactMsgUseCase
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
class ContactUsViewModel @Inject constructor(
    private val getAppInfoUseCase: GetAppInfoUseCase,
    private val contactMsgUseCase: ContactMsgUseCase
) : ViewModel() {

    private val _getContactUsAppInfoState = MutableStateFlow<UiState<ContactUsResponse>>(UiState.Empty())
    val getAppInfoState: StateFlow<UiState<ContactUsResponse>> = _getContactUsAppInfoState

    private val _contactMsgState = MutableStateFlow<UiState<ContactUsSendMessageResponse>>(UiState.Empty())
    val contactMsgState: StateFlow<UiState<ContactUsSendMessageResponse>> = _contactMsgState

    private var getAppInfoJob: Job? = null

    init {
        getContactUsAppInfo()
    }

    fun cancelRequest() = getAppInfoJob?.cancel()

    private fun getContactUsAppInfo() {
        getAppInfoJob?.cancel()
        getAppInfoJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                getAppInfoUseCase.getContactUsAppInfo().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _getContactUsAppInfoState.value = UiState.Success(it)
                            } ?: run { _getContactUsAppInfoState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _getContactUsAppInfoState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _getContactUsAppInfoState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun contactMsg(
        name: String,
        email: String,
        phone: String,
        subject: String,
        message: String,
        iam_not_robot: String
    ) {
        getAppInfoJob?.cancel()
        getAppInfoJob = viewModelScope.launch {
            withContext(coroutineContext) {
                contactMsgUseCase(name, email, phone, subject, message, iam_not_robot).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _contactMsgState.value = UiState.Success(it)
                            } ?: run { _contactMsgState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _contactMsgState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _contactMsgState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}