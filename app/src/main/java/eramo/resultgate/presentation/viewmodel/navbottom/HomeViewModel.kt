package eramo.resultgate.presentation.viewmodel.navbottom

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eramo.resultgate.data.local.entity.MyFavouriteEntity
import eramo.resultgate.data.remote.dto.alldevices.AllDevicesResponse
import eramo.resultgate.data.remote.dto.alldevices.DataX
import eramo.resultgate.data.remote.dto.general.Member
import eramo.resultgate.data.remote.dto.home.HomeBestCategoriesResponse
import eramo.resultgate.data.remote.dto.home.HomeBootomSectionsResponse
import eramo.resultgate.data.remote.dto.home.HomeCategoriesResponse
import eramo.resultgate.data.remote.dto.home.HomeCounterResponse
import eramo.resultgate.data.remote.dto.home.HomePageSliderResponse
import eramo.resultgate.data.remote.dto.products.AddItemsListToWishListResponse
import eramo.resultgate.data.remote.dto.products.FavouriteResponse
import eramo.resultgate.data.remote.dto.products.orders.CartCountResponse
import eramo.resultgate.data.repository.AllDevicesRepoImpl
import eramo.resultgate.domain.model.CartProductModel
import eramo.resultgate.domain.model.OffersModel
import eramo.resultgate.domain.model.ResultModel
import eramo.resultgate.domain.model.home.CounterValueObject
import eramo.resultgate.domain.model.home.HomeBrandsModel
import eramo.resultgate.domain.model.products.CategoryModel
import eramo.resultgate.domain.model.products.MyProductModel
import eramo.resultgate.domain.repository.CartRepository
import eramo.resultgate.domain.repository.ProductsRepository
import eramo.resultgate.domain.usecase.drawer.UpdateFirebaseDeviceTokenUseCase
import eramo.resultgate.domain.usecase.drawer.myaccount.GetProfileUseCase
import eramo.resultgate.domain.usecase.product.AddFavouriteUseCase
import eramo.resultgate.domain.usecase.product.GetAllDevices
import eramo.resultgate.domain.usecase.product.HomeDealsByUserIdUseCase
import eramo.resultgate.domain.usecase.product.HomeFeaturedByUserIdUseCase
import eramo.resultgate.domain.usecase.product.HomeGetCategoriesUseCase
import eramo.resultgate.domain.usecase.product.HomeGetFeaturedProductsUseCase
import eramo.resultgate.domain.usecase.product.HomeOffersUseCase
import eramo.resultgate.domain.usecase.product.HomeProductsByUserIdUseCase
import eramo.resultgate.domain.usecase.product.HomeProductsManufacturerByUserIdUseCase
import eramo.resultgate.domain.usecase.product.HomeTopSliderUseCase
import eramo.resultgate.domain.usecase.product.RemoveFavouriteUseCase
import eramo.resultgate.util.Constants
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiState
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
class HomeViewModel @Inject constructor(
    private val homeDealsByUserIdUseCase: HomeDealsByUserIdUseCase,
    private val homeProductsByUserIdUseCase: HomeProductsByUserIdUseCase,
    private val homeFeaturedByUserIdUseCase: HomeFeaturedByUserIdUseCase,
    private val homeGetFeaturedProductsUseCase: HomeGetFeaturedProductsUseCase,
    private val homeProductsManufacturerByUserIdUseCase: HomeProductsManufacturerByUserIdUseCase,
    private val homeGetCategoriesUseCase: HomeGetCategoriesUseCase,
    private val addFavouriteUseCase: AddFavouriteUseCase,
    private val removeFavouriteUseCase: RemoveFavouriteUseCase,
    private val updateFirebaseDeviceTokenUseCase: UpdateFirebaseDeviceTokenUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val homeTopSliderUseCase: HomeTopSliderUseCase,
    private val homeOffersUseCase: HomeOffersUseCase,
    private val productsRepository: ProductsRepository,
    private val cartRepository: CartRepository,
    private val allDevicesUseCase:GetAllDevices,
) : ViewModel() {

    private val _latestDealsState = MutableStateFlow<UiState<List<MyProductModel>>>(UiState.Empty())
    val latestDealsState: StateFlow<UiState<List<MyProductModel>>> = _latestDealsState

    private val _mostViewedState = MutableStateFlow<UiState<List<MyProductModel>>>(UiState.Empty())
    val mostViewedState: StateFlow<UiState<List<MyProductModel>>> = _mostViewedState

    private val _bestCategoriesState = MutableStateFlow<UiState<HomeBestCategoriesResponse>>(UiState.Empty())
    val bestCategoriesState: StateFlow<UiState<HomeBestCategoriesResponse>> = _bestCategoriesState

    private val _bottomSectionsState = MutableStateFlow<UiState<HomeBootomSectionsResponse>>(UiState.Empty())
    val bottomSectionsState: StateFlow<UiState<HomeBootomSectionsResponse>> = _bottomSectionsState

    private val _mostSaleState = MutableStateFlow<UiState<List<MyProductModel>>>(UiState.Empty())
    val mostSaleState: StateFlow<UiState<List<MyProductModel>>> = _mostSaleState

    private val _homeCounterState = MutableStateFlow<UiState<HomeCounterResponse>>(UiState.Empty())
    val homeCounterState: StateFlow<UiState<HomeCounterResponse>> = _homeCounterState

    private val _homeCounterValueState = MutableStateFlow<CounterValueObject>(CounterValueObject(0, 0, 0, 0))
    val homeCounterValueState: StateFlow<CounterValueObject> = _homeCounterValueState

    private val _latestProductsState = MutableStateFlow<UiState<List<MyProductModel>>>(UiState.Empty())
    val latestProductsState: StateFlow<UiState<List<MyProductModel>>> = _latestProductsState

    //    private val _featuredProductsState = MutableStateFlow<UiState<FeaturedProductsResponse>>(UiState.Empty())
//    val featuredProductsState: StateFlow<UiState<FeaturedProductsResponse>> = _featuredProductsState
    private val _featuredProductsState = MutableStateFlow<UiState<List<MyProductModel>>>(UiState.Empty())
    val featuredProductsState: StateFlow<UiState<List<MyProductModel>>> = _featuredProductsState

    private val _categoriesState =
        MutableStateFlow<UiState<HomeCategoriesResponse>>(UiState.Empty())
    val categoriesState: StateFlow<UiState<HomeCategoriesResponse>> =
        _categoriesState

    private val _brandsState = MutableStateFlow<UiState<List<HomeBrandsModel>>>(UiState.Empty())
    val brandsState: StateFlow<UiState<List<HomeBrandsModel>>> = _brandsState

    private val _allProductsManufacturerState =
        MutableStateFlow<UiState<List<CategoryModel>>>(UiState.Empty())
    val allProductsManufacturerState: StateFlow<UiState<List<CategoryModel>>> =
        _allProductsManufacturerState

    private val _addFavouriteState = MutableSharedFlow<UiState<ResultModel>>()
    val addFavouriteState: SharedFlow<UiState<ResultModel>> = _addFavouriteState

    private val _removeFavouriteState = MutableSharedFlow<UiState<ResultModel>>()
    val removeFavouriteState: SharedFlow<UiState<ResultModel>> = _removeFavouriteState

    private val _addRemoveItemWishlistState = MutableSharedFlow<UiState<FavouriteResponse>>()
    val addRemoveItemWishlistState: SharedFlow<UiState<FavouriteResponse>> = _addRemoveItemWishlistState

    private val _getProfileState = MutableStateFlow<UiState<Member>>(UiState.Empty())
    val getProfileState: StateFlow<UiState<Member>> = _getProfileState

    private val _firebaseTokenState = MutableStateFlow<UiState<ResultModel>>(UiState.Empty())
    val firebaseTokenState: StateFlow<UiState<ResultModel>> = _firebaseTokenState

    private val _homeTopSliderState = MutableStateFlow<UiState<HomePageSliderResponse>>(UiState.Empty())
    val homeTopSliderState: StateFlow<UiState<HomePageSliderResponse>> = _homeTopSliderState

    private val _homeOffersState = MutableStateFlow<UiState<List<OffersModel>>>(UiState.Empty())
    val homeOffersState: StateFlow<UiState<List<OffersModel>>> = _homeOffersState

    private val _allDevices: MutableStateFlow<AllDevicesResponse?> = MutableStateFlow(null)
    val allDevices: StateFlow<AllDevicesResponse?> get() = _allDevices

    private val _cartCountState = MutableStateFlow<UiState<CartCountResponse>>(UiState.Empty())
    val cartCountState: StateFlow<UiState<CartCountResponse>> = _cartCountState

    private val _cartDataDtoState = MutableStateFlow<UiState<CartProductModel>>(UiState.Empty())
    val cartDataModelState: StateFlow<UiState<CartProductModel>> = _cartDataDtoState

    private val _addToCartState = MutableSharedFlow<UiState<ResultModel>>()
    val addToCartState: SharedFlow<UiState<ResultModel>> = _addToCartState

    private val _clearCartDBState = MutableSharedFlow<UiState<ResultModel>>()
    val clearCartDBState: SharedFlow<UiState<ResultModel>> = _clearCartDBState

    private val _addRemoveItemWishlistStateDB = MutableSharedFlow<UiState<ResultModel>>()
    val addRemoveItemWishlistStateDB: SharedFlow<UiState<ResultModel>> = _addRemoveItemWishlistStateDB

    private val _clearFavouriteListDBState = MutableSharedFlow<UiState<ResultModel>>()
    val clearFavouriteListDBState: SharedFlow<UiState<ResultModel>> = _clearFavouriteListDBState

    private val _getFavouriteListDBState = MutableStateFlow<UiState<List<MyFavouriteEntity>>>(UiState.Empty())
    val getFavouriteListDBState: StateFlow<UiState<List<MyFavouriteEntity>>> = _getFavouriteListDBState

    private val _addItemsListToWishlistState = MutableStateFlow<UiState<AddItemsListToWishListResponse>>(UiState.Empty())
    val addItemsListToWishlistState: StateFlow<UiState<AddItemsListToWishListResponse>> = _addItemsListToWishlistState

    private val _notificationCount = MutableStateFlow<Int>(0)
    val notificationCount: StateFlow<Int> get() = _notificationCount

    private var latestDealsJob: Job? = null
    private var homeCounterJob: Job? = null
    private var allProductsJob: Job? = null
    private var brandsJob: Job? = null
    private var allFeaturedJob: Job? = null
    private var allProductsManufacturerJob: Job? = null
    private var addFavouriteJob: Job? = null
    private var removeFavouriteJob: Job? = null
    private var addRemoveItemWishlistJob: Job? = null
    private var getProfileJob: Job? = null
    private var firebaseTokenJob: Job? = null
    private var homeAdsJob: Job? = null
    private var homeOffersJob: Job? = null
    private var cartCountJob: Job? = null
    private var cartDataJob: Job? = null
    private var addToCartJob: Job? = null
    private var clearCartDBJob: Job? = null
    private var addRemoveItemWishlistJobDB: Job? = null
    private var getFavouriteListDBJob: Job? = null
    private var addItemsListToWishlistJob: Job? = null
    private var clearFavouriteListDBJob: Job? = null
    private var notificationCountJob: Job? = null
    private var mostViewedJob: Job? = null
    private var mostSaleJob: Job? = null
    private var bottomSectionsJob: Job? = null
    private var bestCategoriesJob: Job? = null


    fun getAllDevices() {
        viewModelScope.launch {
            try {
                _allDevices.value = allDevicesUseCase()
                Log.e("success", _allDevices.value.toString())

            } catch (e: Exception) {
                Log.e("failed", e.message.toString())
            }
        }
    }

    fun cancelRequest() {
        latestDealsJob?.cancel()
        homeCounterJob?.cancel()
        allProductsJob?.cancel()
        allFeaturedJob?.cancel()
        allProductsManufacturerJob?.cancel()
        addFavouriteJob?.cancel()
        removeFavouriteJob?.cancel()
        addRemoveItemWishlistJob?.cancel()
        getProfileJob?.cancel()
        firebaseTokenJob?.cancel()
        homeAdsJob?.cancel()
        homeOffersJob?.cancel()
        cartCountJob?.cancel()
        cartDataJob?.cancel()
        addToCartJob?.cancel()
        clearCartDBJob?.cancel()
        addRemoveItemWishlistJobDB?.cancel()
        getFavouriteListDBJob?.cancel()
        addItemsListToWishlistJob?.cancel()
        clearFavouriteListDBJob?.cancel()
        notificationCountJob?.cancel()
        mostViewedJob?.cancel()
        mostSaleJob?.cancel()
        bottomSectionsJob?.cancel()
        bestCategoriesJob?.cancel()
    }

    fun latestDeals() {
        latestDealsJob?.cancel()
        latestDealsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                homeDealsByUserIdUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _latestDealsState.value = UiState.Success(data)
                            } ?: run { _latestDealsState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _latestDealsState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _latestDealsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun mostViewed() {
        mostViewedJob?.cancel()
        mostViewedJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.homeMostViewedProducts().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _mostViewedState.value = UiState.Success(data)
                            } ?: run { _mostViewedState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _mostViewedState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _mostViewedState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun bestCategories() {
        bestCategoriesJob?.cancel()
        bestCategoriesJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.homeBestCategories().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _bestCategoriesState.value = UiState.Success(data)
                            } ?: run { _bestCategoriesState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _bestCategoriesState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _bestCategoriesState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun bottomSections() {
        bottomSectionsJob?.cancel()
        bottomSectionsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.homeGetBottomSections().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _bottomSectionsState.value = UiState.Success(data)
                            } ?: run { _bottomSectionsState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _bottomSectionsState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _bottomSectionsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun mostSale() {
        mostSaleJob?.cancel()
        mostSaleJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.homeSaleViewedProducts().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _mostSaleState.value = UiState.Success(data)
                            } ?: run { _mostSaleState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _mostSaleState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _mostSaleState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    private fun getHomeCounter() {
        homeCounterJob?.cancel()
        homeCounterJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                homeDealsByUserIdUseCase.getHomeCounter().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                if ((data.data?.days == 0) && (data.data?.hours == 0) && (data.data?.minutes == 0) && (data.data?.seconds == 0)){
                                    _homeCounterState.value = UiState.Empty()
                                }
                                _homeCounterState.value = UiState.Success(data)

                                setupCounterDown(
                                    periodToMs(
                                        data.data?.days!!,
                                        data.data.hours!!,
                                        data.data.minutes!!,
                                        data.data.seconds!!
                                    )
                                )

                            } ?: run { _homeCounterState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _homeCounterState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _homeCounterState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    private fun setupCounterDown(totalMillis: Long) {
//        endDate = simpleDateFormat.parse(endTime)
//        val totalMillis = endDate.time - Date().time

        val timer = object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds: Long = millisUntilFinished / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24

                _homeCounterValueState.value = CounterValueObject(seconds, minutes, hours, days)
            }

            override fun onFinish() {
                _homeCounterValueState.value = CounterValueObject(0, 0, 0, 0)
            }
        }
        timer.start()

    }

    private fun periodToMs(days: Int, hours: Int, minutes: Int, seconds: Int): Long {
        val d: Long = (days).toLong() * (86400000)
        val h: Long = hours.toLong() * 60 * 60 * 1000
        val m: Long = minutes.toLong() * 60 * 1000
        val s: Long = seconds.toLong() * 1000

        return (d + h + m + s)
    }

    fun getLatestProducts() {
        allProductsJob?.cancel()
        allProductsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                homeProductsByUserIdUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _latestProductsState.value = UiState.Success(data)
                            } ?: run { _latestProductsState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _latestProductsState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _latestProductsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun getFeaturedProducts() {
        allFeaturedJob?.cancel()
        allFeaturedJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                homeGetFeaturedProductsUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _featuredProductsState.value = UiState.Success(data)
                            } ?: run { _featuredProductsState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _featuredProductsState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _featuredProductsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun getHomeCategories(brandId:String?) {
        allProductsManufacturerJob?.cancel()
        allProductsManufacturerJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                homeGetCategoriesUseCase(brandId?:"-1").collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _categoriesState.value = UiState.Success(data)
                            } ?: run { _categoriesState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _categoriesState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _categoriesState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun getHomeBrands() {
        brandsJob?.cancel()
        brandsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.homeGetBrands().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _brandsState.value = UiState.Success(data)
                            } ?: run { _brandsState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _brandsState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _brandsState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun allProductsManufacturer() {
        allProductsManufacturerJob?.cancel()
        allProductsManufacturerJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                homeProductsManufacturerByUserIdUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { data ->
                                _allProductsManufacturerState.value = UiState.Success(data)
                            } ?: run { _categoriesState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _allProductsManufacturerState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _allProductsManufacturerState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun addFavourite(property_id: String) {
        addFavouriteJob?.cancel()
        addFavouriteJob = viewModelScope.launch {
            withContext(coroutineContext) {
                addFavouriteUseCase(property_id).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _addFavouriteState.emit(UiState.Success(it))
                            } ?: run { _addFavouriteState.emit(UiState.Empty()) }
                        }

                        is Resource.Error -> {
                            _addFavouriteState.emit(
                                UiState.Error(result.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _addFavouriteState.emit(UiState.Loading())
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

    fun addRemoveItemWishlist(productId: String, refresh: String) {
        addRemoveItemWishlistJob?.cancel()
        addRemoveItemWishlistJob = viewModelScope.launch {
            withContext(coroutineContext) {
                addFavouriteUseCase.addRemoveItemWishlist(productId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _addRemoveItemWishlistState.emit(UiState.Success(it))
                            } ?: run { _addRemoveItemWishlistState.emit(UiState.Empty()) }

//                            when (refresh) {
//                                "latest products" -> getLatestProducts()
//                                "latest deals" -> latestDeals()
//                                "featured products" -> getFeaturedProducts()
//                                "most viewed products" -> mostViewed()
//                                "most sale products" -> mostSale()
//                            }

                            getLatestProducts()
                            latestDeals()
                            getFeaturedProducts()
                            mostViewed()
                            bottomSections()
                            mostSale()
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

    fun getProfile() {
        getProfileJob?.cancel()
        getProfileJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                getProfileUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _getProfileState.value = UiState.Success(it)
                            } ?: run { _getProfileState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _getProfileState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _getProfileState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun updateFirebaseToken(deviceToken: String) {
        firebaseTokenJob?.cancel()
        firebaseTokenJob = viewModelScope.launch {
            withContext(coroutineContext) {
                updateFirebaseDeviceTokenUseCase(deviceToken).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _firebaseTokenState.value = UiState.Success(it)
                            } ?: run { _firebaseTokenState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _firebaseTokenState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _firebaseTokenState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun getHomeTopSlider() {
        homeAdsJob?.cancel()
        homeAdsJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                homeTopSliderUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _homeTopSliderState.value = UiState.Success(it)
                            } ?: run { _homeTopSliderState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _homeTopSliderState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _homeTopSliderState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun getHomeOffers() {
        homeOffersJob?.cancel()
        homeOffersJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                homeOffersUseCase().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _homeOffersState.value = UiState.Success(it)
                            } ?: run { _homeOffersState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _homeOffersState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _homeOffersState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun getCartCount() {
        cartCountJob?.cancel()
        cartCountJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                val result = //if (UserUtil.isUserLogin()) cartRepository.getCartCountApi() else return@withContext
                    if (UserUtil.isUserLogin())
                        cartRepository.getCartCountApi()
                    else
                        cartRepository.getCartCountDB()

                result.collect { it ->
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let {
                                _cartCountState.value = UiState.Success(it)
                            } ?: run { _cartCountState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _cartCountState.value = UiState.Error(it.message!!)
                        }

                        is Resource.Loading -> {
                            _cartCountState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun getCartDataDB() {
        cartDataJob?.cancel()
        cartDataJob = viewModelScope.launch {
            withContext(coroutineContext) {
                val result = cartRepository.getCartDataDB()

                result.collect {
                    when (it) {
                        is Resource.Success -> {
                            _cartDataDtoState.value = UiState.Success(it.data)
                            getCartCount()
                        }

                        is Resource.Error -> {
                            _cartDataDtoState.value = UiState.Error(it.message!!)
                        }

                        is Resource.Loading -> {
                            _cartDataDtoState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun addToCart(cart: String) {
        addToCartJob = viewModelScope.launch {
            withContext(coroutineContext) {
                val result = cartRepository.addToCartApi(cart)

                result.collect { it ->
                    when (it) {
                        is Resource.Success -> {
                            it.data?.let {
                                val resultModel = ResultModel(200, "Success")
                                _addToCartState.emit(UiState.Success(resultModel))
                            } ?: run { _addToCartState.emit(UiState.Empty()) }

                            getCartCount()
                        }

                        is Resource.Error -> {
                            _addToCartState.emit(
                                UiState.Error(it.message!!)
                            )
                        }

                        is Resource.Loading -> {
                            _addToCartState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    fun clearCartDB() {
        clearCartDBJob?.cancel()
        clearCartDBJob = viewModelScope.launch {
            withContext(coroutineContext) {
                val result = cartRepository.removeAllCartDB()

                result.collect {
                    when (it) {
                        is Resource.Success -> {
                            _clearCartDBState.emit(UiState.Success(it.data))
                            getCartCount()
                        }

                        is Resource.Error -> {
                            _clearCartDBState.emit(UiState.Error(it.message!!))
                        }

                        is Resource.Loading -> {
                            _clearCartDBState.emit(UiState.Loading())
                        }
                    }
                }
            }
        }
    }

    fun addItemsListToWishlist(ids: String) {
        addItemsListToWishlistJob?.cancel()
        addItemsListToWishlistJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.addItemsListToWishList(ids).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _addItemsListToWishlistState.value = UiState.Success(it)
                            } ?: run { _addItemsListToWishlistState.value = UiState.Empty() }
                        }

                        is Resource.Error -> {
                            _addItemsListToWishlistState.value =
                                UiState.Error(result.message!!)
                        }

                        is Resource.Loading -> {
                            _addItemsListToWishlistState.value = UiState.Loading()
                        }
                    }
                }
            }
        }
    }

    fun clearFavouriteListDB() {
        clearFavouriteListDBJob?.cancel()
        clearFavouriteListDBJob = viewModelScope.launch {
            delay(Constants.ANIMATION_DELAY)
            withContext(coroutineContext) {
                productsRepository.clearFavouriteList().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _clearFavouriteListDBState.emit(UiState.Success(it))
                            } ?: run { _clearFavouriteListDBState.emit(UiState.Empty()) }
                        }

                        is Resource.Error -> {
                            _clearFavouriteListDBState.emit(UiState.Error(result.message!!))
                        }

                        is Resource.Loading -> {
                            _clearFavouriteListDBState.emit(UiState.Loading())
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

    init {
        getHomeCounter()
    }
}