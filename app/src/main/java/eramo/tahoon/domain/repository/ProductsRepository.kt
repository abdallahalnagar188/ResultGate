package eramo.tahoon.domain.repository

import androidx.paging.PagingData
import eramo.tahoon.data.local.entity.MyFavouriteEntity
import eramo.tahoon.data.remote.dto.NotificationDetailsResponse
import eramo.tahoon.data.remote.dto.NotificationResponse
import eramo.tahoon.data.remote.dto.home.HomeBestCategoriesResponse
import eramo.tahoon.data.remote.dto.home.HomeBootomSectionsResponse
import eramo.tahoon.data.remote.dto.home.HomeCategoriesResponse
import eramo.tahoon.data.remote.dto.home.HomeCounterResponse
import eramo.tahoon.data.remote.dto.home.HomePageSliderResponse
import eramo.tahoon.data.remote.dto.products.AddItemsListToWishListResponse
import eramo.tahoon.data.remote.dto.products.FavouriteResponse
import eramo.tahoon.data.remote.dto.products.ProductDetailsResponse
import eramo.tahoon.data.remote.dto.products.search.PriceResponse
import eramo.tahoon.domain.model.FilterCategoryModel
import eramo.tahoon.domain.model.OffersModel
import eramo.tahoon.domain.model.ResultModel
import eramo.tahoon.domain.model.SortSearchResultObject
import eramo.tahoon.domain.model.home.HomeBrandsModel
import eramo.tahoon.domain.model.products.AdsModel
import eramo.tahoon.domain.model.products.CategoryModel
import eramo.tahoon.domain.model.products.MyProductModel
import eramo.tahoon.domain.model.products.ProductModel
import eramo.tahoon.domain.model.products.ShopProductModel
import eramo.tahoon.domain.model.request.SearchRequest
import eramo.tahoon.util.state.Resource
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