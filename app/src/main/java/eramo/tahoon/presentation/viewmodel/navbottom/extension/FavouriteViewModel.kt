package eramo.tahoon.presentation.viewmodel.navbottom.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.tahoon.data.local.entity.MyFavouriteEntity
import eramo.tahoon.data.remote.dto.products.FavouriteResponse
import eramo.tahoon.domain.model.ResultModel
import eramo.tahoon.domain.model.products.MyProductModel
import eramo.tahoon.domain.repository.ProductsRepository
import eramo.tahoon.domain.usecase.product.AddFavouriteUseCase
import eramo.tahoon.domain.usecase.product.RemoveFavouriteUseCase
import eramo.tahoon.domain.usecase.product.UserFavListByUserIdUseCase
import eramo.tahoon.util.Constants
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val userFavListByUserIdUseCase: UserFavListByUserIdUseCase,
    private val removeFavouriteUseCase: RemoveFavouriteUseCase,
    private val addFavouriteUseCase: AddFavouriteUseCase,
    private val productsRepository: ProductsRepository
    ) : ViewModel() {

    private val _userFavState = MutableStateFlow<UiState<List<MyProductModel>>>(UiState.Empty())
    val userFavState: StateFlow<UiState<List<MyProductModel>>> = _userFavState

    private val _addRemoveItemWishlistState = MutableSharedFlow<UiState<FavouriteResponse>>()
    val addRemoveItemWishlistState: SharedFlow<UiState<FavouriteResponse>> = _addRemoveItemWishlistState

    private val _removeFavouriteState = MutableSharedFlow<UiState<ResultModel>>()
    val removeFavouriteState: SharedFlow<UiState<ResultModel>> = _removeFavouriteState

    private val _getFavouriteListDBState = MutableStateFlow<UiState<List<MyFavouriteEntity>>>(UiState.Empty())
    val getFavouriteListDBState: StateFlow<UiState<List<MyFavouriteEntity>>> = _getFavouriteListDBState

    private val _addRemoveItemWishlistStateDB = MutableSharedFlow<UiState<ResultModel>>()
    val addRemoveItemWishlistStateDB: SharedFlow<UiState<ResultModel>> = _addRemoveItemWishlistStateDB

    private var userFavJob: Job? = null
    private var removeFavouriteJob: Job? = null
    private var addRemoveItemWishlistJob: Job? = null
    private var getFavouriteListDBJob: Job? = null
    private var addRemoveItemWishlistJobDB: Job? = null

    fun cancelRequest() {
        userFavJob?.cancel()
        addRemoveItemWishlistJob?.cancel()
        getFavouriteListDBJob?.cancel()
        addRemoveItemWishlistJobDB?.cancel()
    }

    fun userFav() {
        userFavJob?.cancel()
        userFavJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                userFavListByUserIdUseCase.myWishList().collect() { result ->
                    when (result) {
                        is Resource.Success -> {
                            _userFavState.value = UiState.Success(result.data)
                        }

                        is Resource.Error -> {
                            _userFavState.value = UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _userFavState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun removeFavourite(property_id: String) {
        removeFavouriteJob?.cancel()
        removeFavouriteJob = viewModelScope.launch {
            withContext(coroutineContext) {
                removeFavouriteUseCase(property_id).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _removeFavouriteState.emit(UiState.Success(it))
                            } ?: run { _removeFavouriteState.emit(UiState.Empty()) }
                        }

                        is Resource.Error -> {
                            _removeFavouriteState.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _removeFavouriteState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    fun addRemoveItemWishlist(productId: String) {
        addRemoveItemWishlistJob?.cancel()
        addRemoveItemWishlistJob = viewModelScope.launch {
            withContext(coroutineContext) {
                addFavouriteUseCase.addRemoveItemWishlist(productId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _addRemoveItemWishlistState.emit(UiState.Success(it))
                            } ?: run { _addRemoveItemWishlistState.emit(UiState.Empty()) }

                            userFav()
                        }

                        is Resource.Error -> {
                            _addRemoveItemWishlistState.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _addRemoveItemWishlistState.emit(UiState.Loading())
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
}