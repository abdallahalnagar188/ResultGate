package eramo.resultgate.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import eramo.resultgate.data.local.EramoDao
import eramo.resultgate.data.local.entity.MyFavouriteEntity
import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.data.remote.dto.NotificationDetailsResponse
import eramo.resultgate.data.remote.dto.NotificationResponse
import eramo.resultgate.data.remote.dto.home.HomeBestCategoriesResponse
import eramo.resultgate.data.remote.dto.home.HomeBootomSectionsResponse
import eramo.resultgate.data.remote.dto.home.HomeCategoriesResponse
import eramo.resultgate.data.remote.dto.home.HomeCounterResponse
import eramo.resultgate.data.remote.dto.home.HomePageSliderResponse
import eramo.resultgate.data.remote.dto.products.AddItemsListToWishListResponse
import eramo.resultgate.data.remote.dto.products.FavouriteResponse
import eramo.resultgate.data.remote.dto.products.ProductDetailsResponse
import eramo.resultgate.data.remote.dto.products.search.PriceResponse
import eramo.resultgate.data.remote.paging.PagingFilterSubCategoryProducts
import eramo.resultgate.data.remote.paging.PagingHomeSearch
import eramo.resultgate.data.remote.paging.PagingManufacturers
import eramo.resultgate.data.remote.paging.PagingProducts
import eramo.resultgate.data.remote.paging.PagingProductsByCategory
import eramo.resultgate.data.remote.paging.PagingProductsFilterBySubCat
import eramo.resultgate.data.remote.paging.SortSearchResult
import eramo.resultgate.domain.model.FilterCategoryModel
import eramo.resultgate.domain.model.OffersModel
import eramo.resultgate.domain.model.ResultModel
import eramo.resultgate.domain.model.SortSearchResultObject
import eramo.resultgate.domain.model.home.HomeBrandsModel
import eramo.resultgate.domain.model.products.AdsModel
import eramo.resultgate.domain.model.products.CategoryModel
import eramo.resultgate.domain.model.products.MyProductModel
import eramo.resultgate.domain.model.products.ProductModel
import eramo.resultgate.domain.model.products.ShopProductModel
import eramo.resultgate.domain.model.request.SearchRequest
import eramo.resultgate.domain.repository.ProductsRepository
import eramo.resultgate.util.Constants.API_SUCCESS
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.pagingConfig
import eramo.resultgate.util.state.ApiState
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiText
import eramo.resultgate.util.toResultFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductsRepositoryImpl(
    private val eramoApi: EramoApi, private val dao: EramoDao
) : ProductsRepository {

    override suspend fun getUserNotifications(): Flow<Resource<NotificationResponse>> {
        return flow {
//            val result = toResultFlow { eramoApi.getUserNotifications(UserUtil.getUserId()) }
            val result = toResultFlow { eramoApi.getUserNotifications("Bearer ${UserUtil.getUserToken()}") }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> emit(Resource.Success(apiState.data))
                }
            }
        }
    }

    override suspend fun getNotificationDetails(notificationId: String): Flow<Resource<NotificationDetailsResponse>> {
        return flow {
//            val result = toResultFlow { eramoApi.getUserNotifications(UserUtil.getUserId()) }
            val result =
                toResultFlow { eramoApi.getNotificationDetails("Bearer ${UserUtil.getUserToken()}", notificationId) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> emit(Resource.Success(apiState.data))
                }
            }
        }
    }

    override suspend fun getProductById(productId: String): Flow<Resource<ProductDetailsResponse>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.getProductById(
                        if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                        productId
                    )
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val model = apiState.data
                        emit(Resource.Success(model))
                    }
                }
            }
        }
    }

    override suspend fun homeProductsByUserId(): Flow<Resource<List<MyProductModel>>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.homeGetLatestProducts(if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null)
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.data?.map { it!!.toMyProductModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun homeMostViewedProducts(): Flow<Resource<List<MyProductModel>>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.homeGetMostViewedProducts(if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null)
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.data?.map { it!!.toMyProductModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun homeBestCategories(): Flow<Resource<HomeBestCategoriesResponse>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.homeGetBestCategories(if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null)
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun homeGetBottomSections(): Flow<Resource<HomeBootomSectionsResponse>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.homeGetBottomSections(if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null)
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun homeSaleViewedProducts(): Flow<Resource<List<MyProductModel>>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.homeGetMostSaleProducts(if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null)
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.data?.map { it!!.toMyProductModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

//    override suspend fun homeFeaturedByUserId(): Flow<Resource<List<ProductModel>>> {
//        return flow {
//            val result =
//                toResultFlow {
//                    eramoApi.allProductsByUserId(
////                        "1",
////                        "4",
////                        UserUtil.getUserId(),
////                        Constants.TEXT_YES
//                    )
//                }
//            result.collect { apiState ->
//                when (apiState) {
//                    is ApiState.Loading -> emit(Resource.Loading())
//                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
//                    is ApiState.Success -> {
//                        val list = apiState.data?.allProducts?.map { it.toProductModel() }
//                        emit(Resource.Success(list))
//                    }
//                }
//            }
//        }
//    }

    override suspend fun homeGetFeaturedProducts(): Flow<Resource<List<MyProductModel>>> {
//    override suspend fun homeGetFeaturedProducts(): Flow<Resource<FeaturedProductsResponse>> {
        return flow {
            val result =
                toResultFlow {
                    val token = if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null
//                    eramoApi.homeGetFeaturedProducts("Bearer ${UserUtil.getUserToken()}")
                    eramoApi.homeGetFeaturedProducts(token)
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> {
                        emit(Resource.Error(apiState.message!!))
                    }

                    is ApiState.Success -> {
                        val list = apiState.data?.data?.map { it!!.toMyProductModel() }
//                        val list = apiState.data?.featuredProducts?.map { it.toProductModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun allProductsByUserId(): Flow<PagingData<ShopProductModel>> {
        return Pager(
            config = pagingConfig(),
            pagingSourceFactory = { PagingProducts(eramoApi) }
        ).flow
    }

    override suspend fun filterSubCategoryProducts(
        subCategoryId: String,
        type: String,
        value: String,
        max: String,
        min: String
    ): Flow<PagingData<ShopProductModel>> {
        return Pager(
            config = pagingConfig(),
            pagingSourceFactory = { PagingFilterSubCategoryProducts(eramoApi,subCategoryId,type, value, max, min) }
        ).flow
    }

    override suspend fun allCategorizationByUserId(catId: String,brandId: String): Flow<PagingData<ShopProductModel>> {
        return Pager(
            config = pagingConfig(),
            pagingSourceFactory = { PagingProductsByCategory(eramoApi, catId, brandId = brandId) }
        ).flow
    }

    override suspend fun homeProductsManufacturersByUserId(): Flow<Resource<List<CategoryModel>>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.allProductsManufacturersByUserId(
                        "1",
                        "100",
                        UserUtil.getUserId()
                    )
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.all_cats?.map { it.toCategoryModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun homeGetCategories(brandId:String?): Flow<Resource<HomeCategoriesResponse>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.homeCategories(
                        if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                        brandId
                    )
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> {
                        emit(Resource.Error(apiState.message!!))
                    }

                    is ApiState.Success -> {
//                            val list = apiState.data?.all_main_cats?.map { it.toCategoryModel() }
                        val list = apiState.data
                        emit(Resource.Success(list))

                    }
                }
            }
        }
    }

    override suspend fun homeGetSubCategories(): Flow<Resource<HomeCategoriesResponse>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.homeSubCategories(
                        if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null
                    )
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> {
                        emit(Resource.Error(apiState.message!!))
                    }

                    is ApiState.Success -> {
//                            val list = apiState.data?.all_main_cats?.map { it.toCategoryModel() }
                        val list = apiState.data
                        emit(Resource.Success(list))

                    }
                }
            }
        }
    }

    override suspend fun getProductsByBrand(brandId:String): Flow<Resource<List<ShopProductModel>>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.getBrandProducts(
                        if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                        brandId
                    )
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> {
                        emit(Resource.Error(apiState.message!!))
                    }

                    is ApiState.Success -> {
                        val list = apiState.data?.data?.products!!.map { it!!.toShopProductModel() }
                        emit(Resource.Success(list))

                    }
                }
            }
        }
    }

    override suspend fun homeGetBrands(): Flow<Resource<List<HomeBrandsModel>>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.homeGetBrands(
                        if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null
                    )
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> {
                        emit(Resource.Error(apiState.message!!))
                    }

                    is ApiState.Success -> {
                        val list = apiState.data?.map { it.toHomeBrandsModel() }
                        emit(Resource.Success(list))

                    }
                }
            }
        }
    }

    override suspend fun allProductsManufacturersByUserId(): Flow<PagingData<CategoryModel>> {
        return Pager(
            config = pagingConfig(),
            pagingSourceFactory = { PagingManufacturers(eramoApi) }
        ).flow
    }

    override suspend fun homeDealsByUserId(): Flow<Resource<List<MyProductModel>>> {
        return flow {
            val result =
                toResultFlow { eramoApi.latestDealsByUserId(if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.data?.map { it!!.toMyProductModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun getHomeCounter(): Flow<Resource<HomeCounterResponse>> {
        return flow {
            val result =
                toResultFlow { eramoApi.getHomeCounter() }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data
//                        val list = apiState.data?.allProducts?.map { it.toProductModel() }
//                        val endTime = apiState.data?.latestDeals
//                        val map = HashMap<String, Any>()
//                        map["list"] = list as List<ProductModel>
//                        map["endTime"] = endTime as String
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun latestDealsByUserId(): Flow<PagingData<ProductModel>> {
        TODO("Not yet implemented")
    }

//    override suspend fun latestDealsByUserId(): Flow<PagingData<ProductModel>> {
//        return Pager(
//            config = pagingConfig(),
//            pagingSourceFactory = { PagingDeals(eramoApi) }
//        ).flow
//    }

    override suspend fun addFavourite(property_id: String): Flow<Resource<ResultModel>> {
        return flow {
            val result =
                toResultFlow { eramoApi.addFavourite(UserUtil.getUserId(), property_id) }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> {
                        val model = it.data?.toResultModel()
                        if (it.data?.success == 1) emit(Resource.Success(model))
                        else emit(Resource.Error(UiText.DynamicString(model?.message ?: "Error")))
                    }
                }
            }
        }
    }

    override suspend fun removeFavourite(property_id: String): Flow<Resource<ResultModel>> {
        return flow {
            val result =
                toResultFlow { eramoApi.removeFavourite(UserUtil.getUserId(), property_id) }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> {
                        val model = it.data?.toResultModel()
                        if (it.data?.success == 1) emit(Resource.Success(model))
                        else emit(Resource.Error(UiText.DynamicString(model?.message ?: "Error")))
                    }
                }
            }
        }
    }

    override suspend fun addRemoveItemWishlist(productsId: String): Flow<Resource<FavouriteResponse>> {
        return flow {
            val result =
                toResultFlow { eramoApi.addRemoveItemWishlist("Bearer ${UserUtil.getUserToken()}", productsId) }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> {
                        val model = it.data
                        if (it.data?.status == API_SUCCESS) emit(Resource.Success(model))
                        else emit(Resource.Error(UiText.DynamicString(model?.message ?: "Error")))
                    }
                }
            }
        }
    }

    override suspend fun addItemsListToWishList(ids: String): Flow<Resource<AddItemsListToWishListResponse>> {
        return flow {
            val result =
                toResultFlow { eramoApi.addItemsListToWishlist("Bearer ${UserUtil.getUserToken()}", ids) }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> {
                        val model = it.data
                        if (it.data?.status == API_SUCCESS) emit(Resource.Success(model))
                        else emit(Resource.Error(UiText.DynamicString(model?.message ?: "Error")))
                    }
                }
            }
        }
    }

    override suspend fun userFavListByUserId(): Flow<Resource<List<ProductModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.userFavListByUserId(UserUtil.getUserId()) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        apiState.data?.allFavList?.let { favList ->
                            val list = favList.map { it.toProductModel() }
                            emit(Resource.Success(list))
                        } ?: emit(Resource.Success(emptyList()))
                    }
                }
            }
        }
    }

    override suspend fun myWishList(): Flow<Resource<List<MyProductModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.myWishList("Bearer ${UserUtil.getUserToken()}") }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        apiState.data?.let { favList ->
                            val list = favList.data?.map { it!!.toMyProductModel() }
                            emit(Resource.Success(list))
                        } ?: emit(Resource.Success(emptyList()))
//                        apiState.data?.allFavList?.let { favList ->
//                            val list = favList.map { it.toProductModel() }
//                            emit(Resource.Success(list))
//                        } ?: emit(Resource.Success(emptyList()))
                    }
                }
            }
        }
    }

//    override suspend fun productFilter(searchRequest: SearchRequest): Flow<PagingData<ShopProductModel>> {
//        return flow {
//            val result =
//                toResultFlow { eramoApi.productFilter(UserUtil.getUserId(), searchRequest) }
//            result.collect { apiState ->
//                when (apiState) {
//                    is ApiState.Loading -> emit(Resource.Loading())
//                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
//                    is ApiState.Success -> {
//                        apiState.data?.dataFounded?.let { founded ->
//                            val list = founded.map { it.toProductModel() }
//                            emit(Resource.Success(list))
//                        } ?: emit(Resource.Error(UiText.StringResource(R.string.no_results_found)))
//                    }
//                }
//            }
//        }
//    }

    override suspend fun productFilter(searchRequest: SearchRequest): Flow<PagingData<ShopProductModel>> {
        return Pager(
            config = pagingConfig(),
            pagingSourceFactory = { PagingProductsFilterBySubCat(eramoApi, searchRequest) }
        ).flow
    }

    override suspend fun productSearch(searchKey: String): Flow<PagingData<ShopProductModel>> {
        return Pager(
            config = pagingConfig(),
            pagingSourceFactory = { PagingHomeSearch(eramoApi, searchKey) }
        ).flow
    }

    override suspend fun sortSearchResult(
        obj: SortSearchResultObject,
        categoryId: String?
    ): Flow<PagingData<ShopProductModel>> {
        return Pager(
            config = pagingConfig(),
            pagingSourceFactory = { SortSearchResult(eramoApi, obj, categoryId) }
        ).flow
    }

    override suspend fun maxProductPrice(): Flow<Resource<PriceResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.maxProductPrice() }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> emit(Resource.Success(apiState.data))
                }
            }
        }
    }

    override suspend fun minProductPrice(): Flow<Resource<PriceResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.minProductPrice() }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> emit(Resource.Success(apiState.data))
                }
            }
        }
    }

    override suspend fun getFilterCategories(): Flow<Resource<List<FilterCategoryModel>>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.getFilterCategories(
                        "1",
                        "100",
                        UserUtil.getUserId()
                    )
                }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.allMainCats?.map { it.toFilterCategoriesModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun homeAds(): Flow<Resource<List<AdsModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.homeAds() }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
//                        val list = apiState.data?.map { it.toAdsModel() }
                        val list = apiState.data?.homepageSliders?.map { it.toAdsModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun homeTopSlider(): Flow<Resource<HomePageSliderResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.homeTopSlider() }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
//                        val list = apiState.data?.map { it.toAdsModel() }
//                        val list = apiState.data?.homepageSliders?.map { it.toAdsModel() }
                        val list = apiState.data
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun homeOffers(): Flow<Resource<List<OffersModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.allSpecialOffers() }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
//                        val list = apiState.data?.partners?.map { it.toOffersModel() }
                        val list = apiState.data?.data?.map { it!!.toOffersModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    //--------------------------- Favourite DB ---------------------------//
    override suspend fun addRemoveFavouriteDB(myFavouriteEntity: MyFavouriteEntity): Flow<Resource<ResultModel>> {
        return flow {
            try {
                emit(Resource.Loading())
                if (dao.isProductFavourite(myFavouriteEntity.productId!!)) {
                    dao.removeFromFavourite(myFavouriteEntity.productId!!)
                } else {
                    dao.insertFavourite(myFavouriteEntity)
                }

                emit(Resource.Success(ResultModel(1, "Success")))
            } catch (e: Exception) {
                emit(Resource.Error(UiText.DynamicString(e.message!!)))
            }
        }
    }

    override suspend fun getFavouriteListDB(): Flow<Resource<List<MyFavouriteEntity>>> {
        return flow {
            try {
                emit(Resource.Loading())

                val result = dao.getFavouriteList()
                emit(Resource.Success(result))

            } catch (e: Exception) {
                emit(Resource.Error(UiText.DynamicString(e.message!!)))
            }
        }
    }

    override suspend fun clearFavouriteList(): Flow<Resource<ResultModel>> {
        return flow {
            try {
                emit(Resource.Loading())
                dao.clearFavouriteList()
                emit(Resource.Success(ResultModel(1, "Success")))
            } catch (e: Exception) {
                emit(Resource.Error(UiText.DynamicString(e.message!!)))
            }
        }
    }

}