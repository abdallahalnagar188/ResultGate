package eramo.resultgate.presentation.viewmodel.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.remote.dto.products.orders.CustomerPromoCodes
import eramo.resultgate.domain.repository.OrderRepository
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PromoCodeViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _promoCodeState = MutableStateFlow<UiState<List<CustomerPromoCodes>>>(UiState.Empty())
    val promoCodeState: StateFlow<UiState<List<CustomerPromoCodes>>> = _promoCodeState

    private var promoCodeJob: Job? = null

    fun cancelRequest() = promoCodeJob?.cancel()

    fun promoCode() {
        promoCodeJob?.cancel()
        promoCodeJob = viewModelScope.launch {
            withContext(coroutineContext) {
                orderRepository.customerPromoCodes().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _promoCodeState.value = UiState.Success(it)
                            } ?: run { _promoCodeState.value = UiState.Empty() }
                        }
                        is Resource.Error -> {
                            _promoCodeState.value = UiState.Error(result.message!!)
                        }
                        is Resource.Loading -> {
                            _promoCodeState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }
}