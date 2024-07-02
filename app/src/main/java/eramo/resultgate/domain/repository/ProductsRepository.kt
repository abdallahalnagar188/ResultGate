package eramo.resultgate.domain.repository

import androidx.paging.PagingData
import eramo.resultgate.data.local.entity.MyFavouriteEntity
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
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {

    suspend fun getUserNotifications(): Flow<Resource<NotificationResponse>>

    suspend fun getNotificationDetails(notificationId: String): Flow<Resource<NotificationDetailsResponse>>

    suspend fun getProductById(productId: String): Flow<Resource<ProductDetailsResponse>>

    suspend fun homeProductsByUserId(): Flow<Resource<List<MyProductModel>>>

    suspend fun homeMostViewedProducts(): Flow<Resource<List<MyProductModel>>>

    suspend fun homeBestCategories(): Flow<Resource<HomeBestCategoriesResponse>>

    suspend fun homeGetBottomSections(): Flow<Resource<HomeBootomSectionsResponse>>

    suspend fun homeSaleViewedProducts(): Flow<Resource<List<MyProductModel>>>

//    suspend fun homeFeaturedByUserId(): Flow<Resource<List<ProductModel>>>

    suspend fun homeGetFeaturedProducts(): Flow<Resource<List<MyProductModel>>>
//    suspend fun homeGetFeaturedProducts(): Flow<Resource<FeaturedProductsResponse>>

    suspend fun allProductsByUserId(): Flow<PagingData<ShopProductModel>>

    suspend fun filterSubCategoryProducts(
        subCategoryId: String,
        type: String,
        value: String,
        max: String,
        min: String
    ): Flow<PagingData<ShopProductModel>>

    suspend fun allCategorizationByUserId(catId: String,brandId: String): Flow<PagingData<ShopProductModel>>

    suspend fun homeProductsManufacturersByUserId(): Flow<Resource<List<CategoryModel>>>

    suspend fun homeGetCategories(brandId:String?): Flow<Resource<HomeCategoriesResponse>>

    suspend fun homeGetSubCategories(): Flow<Resource<HomeCategoriesResponse>>

    suspend fun homeGetBrands(): Flow<Resource<List<HomeBrandsModel>>>

    suspend fun getProductsByBrand(brandId:String): Flow<Resource<List<ShopProductModel>>>

    suspend fun allProductsManufacturersByUserId(): Flow<PagingData<CategoryModel>>

    suspend fun homeDealsByUserId(): Flow<Resource<List<MyProductModel>>>

    suspend fun getHomeCounter(): Flow<Resource<HomeCounterResponse>>

    suspend fun latestDealsByUserId(): Flow<PagingData<ProductModel>>

    suspend fun addFavourite(property_id: String): Flow<Resource<ResultModel>>

    suspend fun removeFavourite(property_id: String): Flow<Resource<ResultModel>>

    suspend fun addRemoveItemWishlist(productsId: String): Flow<Resource<FavouriteResponse>>

    suspend fun addItemsListToWishList(ids: String): Flow<Resource<AddItemsListToWishListResponse>>

    suspend fun userFavListByUserId(): Flow<Resource<List<ProductModel>>>

    suspend fun myWishList(): Flow<Resource<List<MyProductModel>>>

    suspend fun productFilter(searchRequest: SearchRequest): Flow<PagingData<ShopProductModel>>

    suspend fun productSearch(searchKey: String): Flow<PagingData<ShopProductModel>>

    suspend fun sortSearchResult(obj: SortSearchResultObject, categoryId: String?): Flow<PagingData<ShopProductModel>>

    suspend fun maxProductPrice(): Flow<Resource<PriceResponse>>

    suspend fun minProductPrice(): Flow<Resource<PriceResponse>>

    suspend fun getFilterCategories(): Flow<Resource<List<FilterCategoryModel>>>

    suspend fun homeAds(): Flow<Resource<List<AdsModel>>>

    suspend fun homeTopSlider(): Flow<Resource<HomePageSliderResponse>>

    suspend fun homeOffers(): Flow<Resource<List<OffersModel>>>

    //--------------------------------------------- Favourite DB ---------------------------------------------//
    suspend fun addRemoveFavouriteDB(myFavouriteEntity: MyFavouriteEntity): Flow<Resource<ResultModel>>

    suspend fun getFavouriteListDB(): Flow<Resource<List<MyFavouriteEntity>>>

    suspend fun clearFavouriteList(): Flow<Resource<ResultModel>>
}