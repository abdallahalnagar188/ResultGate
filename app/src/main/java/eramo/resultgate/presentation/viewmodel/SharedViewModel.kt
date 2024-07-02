package eramo.resultgate.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.local.entity.MyFavouriteEntity
import eramo.resultgate.data.remote.dto.UpdateFcmTokenResponse
import eramo.resultgate.data.remote.dto.general.Member
import eramo.resultgate.domain.model.ResultModel
import eramo.resultgate.domain.model.request.CheckoutModel
import eramo.resultgate.domain.model.request.OrderExtraList
import eramo.resultgate.domain.model.request.OrderItemList
import eramo.resultgate.domain.model.request.OrderRequest
import eramo.resultgate.domain.repository.AuthRepository
import eramo.resultgate.domain.repository.CartRepository
import eramo.resultgate.domain.repository.ProductsRepository
import eramo.resultgate.util.Constants
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    val openDrawer = MutableLiveData<Boolean>()
    val profileData = MutableLiveData<Member?>()
    val cartCount = MutableLiveData<Int>()
    val notificationsCount = MutableLiveData<Int>()

    // Private channel of type Resource
    private val _filterByCityEvent = Channel<Boolean>()

    // Receiving channel as a flow
    val filterByCityEvent = _filterByCityEvent.receiveAsFlow()

    private val _notificationCount = MutableStateFlow<Int>(0)
    val notificationCount: StateFlow<Int> get() = _notificationCount

    private val _updateFcmTokenState = MutableStateFlow<UiState<UpdateFcmTokenResponse>>(UiState.Empty())
    val updateFcmTokenState: StateFlow<UiState<UpdateFcmTokenResponse>> = _updateFcmTokenState

    private val _getFavouriteListDBState = MutableStateFlow<UiState<List<MyFavouriteEntity>>>(UiState.Empty())
    val getFavouriteListDBState: StateFlow<UiState<List<MyFavouriteEntity>>> = _getFavouriteListDBState

    private val _addRemoveItemWishlistStateDB = MutableSharedFlow<UiState<ResultModel>>()
    val addRemoveItemWishlistStateDB: SharedFlow<UiState<ResultModel>> = _addRemoveItemWishlistStateDB


    //____________________________________________________________________________________________//
    // order

    var orderItemList = ArrayList<OrderItemList>()
    var orderExtraList = ArrayList<OrderExtraList>()
    var paymentType = ""
    var orderPromoCode = 0

    private var notificationCountJob: Job? = null
    private var updateFcmTokenJob: Job? = null
    private var cartCountJob: Job? = null
    private var addRemoveItemWishlistJobDB: Job? = null
    private var getFavouriteListDBJob: Job? = null

    fun cancelRequest() {
        notificationCountJob?.cancel()
        updateFcmTokenJob?.cancel()
        cartCountJob?.cancel()
        addRemoveItemWishlistJobDB?.cancel()
        getFavouriteListDBJob?.cancel()
    }

    fun getNotificationCount() {
        notificationCountJob?.cancel()
        notificationCountJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.getUserNotifications().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                val unseenList = data.data!!.filter { it?.seen == 0 }
                                _notificationCount.value = unseenList.size
//                                _notificationCount.value = 0
                            } ?: run { _notificationCount.value = 0 }
                        }

                        is Resource.Error -> {
                            _notificationCount.value = 0
                        }

                        is Resource.Loading -> {
                            _notificationCount.value = 0
                        }
                    }
                }
            }
        }
    }

    fun updateFcmToken(fcmToken: String) {
        notificationCountJob?.cancel()
        notificationCountJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                authRepository.updateFcmToken(fcmToken).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _updateFcmTokenState.value = UiState.Success(data)
                                Log.e("firebaseToken", "updated")

                            } ?: run { _updateFcmTokenState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _updateFcmTokenState.value = UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _updateFcmTokenState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    var checkoutModel: CheckoutModel? = null
    fun clearCheckoutModel() {
        checkoutModel = null
    }

    fun getOrderRequestInstance(): OrderRequest {
        return OrderRequest(
            userId = UserUtil.getUserId(),
            userName = UserUtil.getUserName(),
            userPhone = UserUtil.getUserPhone(),
            payType = paymentType,
            token = UserUtil.getUserToken(),
            promoCode = orderPromoCode,
            orderItemList = orderItemList,
//            orderExtraList = orderExtraList,
        )
    }

    fun resetOrder() {
        orderExtraList.clear()
        orderItemList.clear()
        paymentType = ""
        orderPromoCode = 0
    }

    fun getCartCount() {
        cartCountJob?.cancel()
        cartCountJob = viewModelScope.launch {
            withContext(coroutineContext) {
                val result = if (UserUtil.isUserLogin())
                    cartRepository.getCartCountApi()
                else
                    cartRepository.getCartCountDB()

                result.collect { it ->
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let {

                                cartCount.value = it.count?: 0
                            } ?: run {  cartCount.value = 0 }
                        }

                        is Resource.Error -> {
                            cartCount.value = 0
                        }

                        is Resource.Loading -> {
                            cartCount.value = 0
                        }
                    }
                }
            }
        }
    }

    fun addRemoveItemWishlistDB(myFavouriteEntity: MyFavouriteEntity) {
        addRemoveItemWishlistJobDB?.cancel()
        addRemoveItemWishlistJobDB = viewModelScope.launch {
            withContext(coroutineContext) {
                productsRepository.addRemoveFavouriteDB(myFavouriteEntity).collect { result ->

                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _addRemoveItemWishlistStateDB.emit(UiState.Success(it))
                            } ?: run { _addRemoveItemWishlistStateDB.emit(UiState.Empty()) }
                        }

                        is Resource.Error -> {
                            _addRemoveItemWishlistStateDB.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _addRemoveItemWishlistStateDB.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    fun getFavouriteListDB() {
        getFavouriteListDBJob?.cancel()
        getFavouriteListDBJob = viewModelScope.launch {
            withContext(coroutineContext) {
                productsRepository.getFavouriteListDB().collect { result ->

                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _getFavouriteListDBState.emit(UiState.Success(it))
                            } ?: run { _getFavouriteListDBState.emit(UiState.Empty()) }
                        }

                        is Resource.Error -> {
                            _getFavouriteListDBState.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _getFavouriteListDBState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    fun triggerFilterByCityEvent() = viewModelScope.launch {
        _filterByCityEvent.send(true)
    }
}