package eramo.resultgate.presentation.viewmodel.navbottom.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.remote.dto.NotificationDetailsResponse
import eramo.resultgate.data.remote.dto.NotificationResponse
import eramo.resultgate.domain.repository.ProductsRepository
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
class NotificationViewModel @Inject constructor(
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _notificationsState = MutableStateFlow<UiState<NotificationResponse>>(UiState.Empty())
    val notificationsState: StateFlow<UiState<NotificationResponse>> = _notificationsState

    private val _notificationDetailsState = MutableStateFlow<UiState<NotificationDetailsResponse>>(UiState.Empty())
    val notificationDetailsState: StateFlow<UiState<NotificationDetailsResponse>> = _notificationDetailsState

    private var notificationsJob: Job? = null
    private var notificationDetailsJob: Job? = null

    fun cancelRequest() {
        notificationsJob?.cancel()
        notificationDetailsJob?.cancel()
    }

    fun getNotification() {
        notificationsJob?.cancel()
        notificationsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.getUserNotifications().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _notificationsState.value = UiState.Success(data)
                            } ?: run { _notificationsState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _notificationsState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _notificationsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun getNotificationDetails(notificationId:String) {
        notificationDetailsJob?.cancel()
        notificationDetailsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.getNotificationDetails(notificationId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _notificationDetailsState.value = UiState.Success(data)
                            } ?: run { _notificationDetailsState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _notificationDetailsState.value =
                                UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _notificationDetailsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}