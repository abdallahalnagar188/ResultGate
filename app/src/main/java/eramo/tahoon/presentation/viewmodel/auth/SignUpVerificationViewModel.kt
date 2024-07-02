package eramo.tahoon.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.remote.dto.auth.CheckVerifyMailCodeResponse
import eramo.tahoon.data.remote.dto.auth.SendVerifyMailResponse
import eramo.tahoon.domain.model.auth.LoginModel
import eramo.tahoon.domain.usecase.auth.LoginUseCase
import eramo.tahoon.domain.usecase.auth.VerifyAccountUseCase
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SignUpVerificationViewModel @Inject constructor(
    private val verifyAccountUseCase: VerifyAccountUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _sendVerifyMailState = MutableStateFlow<UiState<SendVerifyMailResponse>>(UiState.Empty())
    val sendVerifyMailState: StateFlow<UiState<SendVerifyMailResponse>> = _sendVerifyMailState

    private val _checkVerifyMailCodeState = MutableStateFlow<UiState<CheckVerifyMailCodeResponse>>(UiState.Empty())
    val checkVerifyMailCodeState: StateFlow<UiState<CheckVerifyMailCodeResponse>> = _checkVerifyMailCodeState

    private val _loginState = MutableStateFlow<UiState<LoginModel>>(UiState.Empty())
    val loginState: StateFlow<UiState<LoginModel>> = _loginState

    private var loginJob: Job? = null
    private var sendVerifyMailJob: Job? = null
    private var checkVerifyMailCodeJob: Job? = null

    fun cancelRequest() {
        sendVerifyMailJob?.cancel()
        checkVerifyMailCodeJob?.cancel()
    }

    fun sendVerifyMail(email: String) {
        sendVerifyMailJob?.cancel()
        sendVerifyMailJob = viewModelScope.launch {
            withContext(coroutineContext) {
                verifyAccountUseCase.sendVerifyMail(email).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _sendVerifyMailState.value =
                                    UiState.Success(data = result.data)
                            } ?: run { _sendVerifyMailState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _sendVerifyMailState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _sendVerifyMailState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun checkVerifyMailCode(code: String, userEmail: String) {
        checkVerifyMailCodeJob?.cancel()
        checkVerifyMailCodeJob = viewModelScope.launch {
            withContext(coroutineContext) {
                verifyAccountUseCase.checkVerifyMailCode(code, userEmail).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _checkVerifyMailCodeState.value =
                                    UiState.Success(data = result.data)
                            } ?: run { _checkVerifyMailCodeState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _checkVerifyMailCodeState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _checkVerifyMailCodeState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun loginApp(user_phone: String, user_pass: String, isRemember: Boolean) {
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            withContext(coroutineContext) {
                loginUseCase(user_phone, user_pass).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                if (result.data.member?.status == "1" && result.data.member?.verifiedStatus == 1){
                                    saveUserInfo(it, user_pass, isRemember)
                                }

                                _loginState.value = UiState.Success(it)
                            } ?: run { _loginState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _loginState.value = UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _loginState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    private fun saveUserInfo(body: LoginModel, password: String, isRemember: Boolean) {
        UserUtil.saveUserInfo(
            isRemember,
            password,
            body.member?.id.toString(),
            body.token.toString(),
            body.member?.name ?: "",
            body.member?.firstName ?: "",
            body.member?.lastName ?: "",
            body.member?.lang ?: "en",
            body.member?.email ?: "",
            body.member?.phone ?: "",
            body.member?.birthDate ?: "",
            body.member?.gender ?: "",
            body.member?.status ?: "",
            body.member?.country?.id.toString(),
            body.member?.country?.title.toString(),
            body.member?.city?.id.toString(),
            body.member?.city?.title.toString(),
            body.member?.region?.id.toString(),
            body.member?.region?.title.toString(),
            "",
            body.member?.imageUrl ?: ""
        )
    }
}