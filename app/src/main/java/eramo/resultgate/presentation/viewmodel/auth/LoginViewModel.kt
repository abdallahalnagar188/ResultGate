package eramo.resultgate.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.local.EramoDB
import eramo.resultgate.data.local.entity.MyCartDataEntity
import eramo.resultgate.domain.model.auth.LoginModel
import eramo.resultgate.domain.repository.CartRepository
import eramo.resultgate.domain.usecase.auth.LoginUseCase
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val repository: CartRepository,
    private val dao: EramoDB,

) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<LoginModel>>(UiState.Empty())
    val loginState: StateFlow<UiState<LoginModel>> = _loginState

    private var loginJob: Job? = null
    fun cancelRequest() {
        loginJob?.cancel()

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
    suspend fun getCartList():List<MyCartDataEntity>{
        val orderList = dao.dao.getCartList()
        return orderList
    }

    suspend fun switchLocalCartToRemote(cart:String) = withContext(viewModelScope.coroutineContext) {
        repository.switchLocalCartToRemote(cart)

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