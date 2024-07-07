package eramo.resultgate.data.remote

import eramo.resultgate.data.remote.dto.NotificationDetailsResponse
import eramo.resultgate.data.remote.dto.NotificationResponse
import eramo.resultgate.data.remote.dto.SendQueryResponse
import eramo.resultgate.data.remote.dto.UpdateFcmTokenResponse
import eramo.resultgate.data.remote.dto.auth.*
import eramo.resultgate.data.remote.dto.auth.forget.GiveMeEmailResponse
import eramo.resultgate.data.remote.dto.cart.AddToCartResponse
import eramo.resultgate.data.remote.dto.cart.CheckProductStockResponse
import eramo.resultgate.data.remote.dto.cart.CheckPromoCodeResponse
import eramo.resultgate.data.remote.dto.cart.CheckoutResponse
import eramo.resultgate.data.remote.dto.cart.MyCartResponse
import eramo.resultgate.data.remote.dto.cart.RemoveCartItemResponse
import eramo.resultgate.data.remote.dto.cart.UpdateCartQuantityResponse
import eramo.resultgate.data.remote.dto.drawer.ContactUsResponse
import eramo.resultgate.data.remote.dto.drawer.ContactUsSendMessageResponse
import eramo.resultgate.data.remote.dto.drawer.MyAppInfoResponse
import eramo.resultgate.data.remote.dto.drawer.MyWishListResponse
import eramo.resultgate.data.remote.dto.drawer.PolicyInfoDto
import eramo.resultgate.data.remote.dto.drawer.myaccount.AllRequestsResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.CancelOrderResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.MyAccountResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.MyOrdersResponse2
import eramo.resultgate.data.remote.dto.drawer.myaccount.MyQueriesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.OrderDetailsResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.QueryDetailsResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.SuspendAccountResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.AddToMyAddressesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.DeleteFromMyAddressesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.GetMyAddressesResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.myaddresses.UpdateAddressResponse
import eramo.resultgate.data.remote.dto.general.ResultDto
import eramo.resultgate.data.remote.dto.home.HomeBestCategoriesResponse
import eramo.resultgate.data.remote.dto.home.HomeBootomSectionsResponse
import eramo.resultgate.data.remote.dto.home.HomeBrandsResponse
import eramo.resultgate.data.remote.dto.home.HomeCategoriesResponse
import eramo.resultgate.data.remote.dto.home.HomeCounterResponse
import eramo.resultgate.data.remote.dto.home.HomePageSliderResponse
import eramo.resultgate.data.remote.dto.home.HomeSearchResponse
import eramo.resultgate.data.remote.dto.home.SpecialOffersResponse
import eramo.resultgate.data.remote.dto.products.*
import eramo.resultgate.data.remote.dto.products.orders.*
import eramo.resultgate.data.remote.dto.products.search.PriceResponse
import eramo.resultgate.data.remote.dto.teams.AllTeamsResponse
import eramo.resultgate.data.remote.dto.teams.deleteteam.ExitTeamResponse
import eramo.resultgate.data.remote.dto.teams.jointeam.JoinTeamResponse
import eramo.resultgate.data.remote.dto.teams.myteam.MyTeamResponse
import eramo.resultgate.data.remote.dto.teams.teamdetails.TeamDetailsResponse
import eramo.resultgate.domain.model.AuthApiBodySend
import eramo.resultgate.domain.model.AuthApiResponseModel
import eramo.resultgate.domain.model.checkout.CardPaymentKeyBodySendModel
import eramo.resultgate.domain.model.checkout.CardPaymentResponseModel
import eramo.resultgate.domain.model.checkout.OrderRegisterBodySend
import eramo.resultgate.domain.model.checkout.OrderRegisterResonseModel
import eramo.resultgate.domain.model.request.OrderRequest
import eramo.resultgate.util.UserUtil
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface EramoApi {
    companion object {

        //        const val BASE_URL = "https://takeefy.com/api/"
        const val BASE_URL = "https://multivendor.eramostore.com/dashboard/api/"
        const val BASE_URL_Payment = "https://accept.paymobsolutions.com/api/"
        const val IMAGE_URL_GENERAL = "https://newstore.eramoerp.com/uploads/products_images/"
        const val DEEPLINK_URL = "https://zayedjewellery.com/"
        const val IMAGE_URL_SPECIAL_OFFERS = "https://www.eramoerp.com/uploads/special_offers/"
        const val ADS_SLIDER_IMAGE_URL = "https://www.eramostore.com/"
    }

    //____________________________________________________________________________________________//
    // Auth

    //    @GET("SplashScreens")
//    suspend fun onBoardingScreens(): Response<OnBoardingDto>
    @GET("splash")
    suspend fun onBoardingScreens(): Response<MyOnBoardingResponse>

    @Multipart
    @POST("signup")
    suspend fun register(
        @Part("first_name") firstName: RequestBody?,
        @Part("last_name") lastName: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("password") password: RequestBody?,
        @Part("confirm_password") passwordConfirmation: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("address_type") addressType: RequestBody?,
        @Part("birth_date") birthDate: RequestBody?,
        @Part("gender") gender: RequestBody?,
        @Part("sign_from") signFrom: RequestBody?,
        @Part("country_id") countryId: RequestBody?,
        @Part("city_id") cityId: RequestBody?,
        @Part("region_id") regionId: RequestBody?,
        @Part("subregion_id") subRegionId: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<SignUpResponse>

    //    @FormUrlEncoded
    @POST("suspened-user")
    suspend fun suspendAccount(
        @Header("Authorization") userToken: String?,
    ): Response<SuspendAccountResponse>


    @FormUrlEncoded
    @POST("signin")
    suspend fun loginApp(
        @Field("phone") user_phone: String?,
        @Field("password") user_pass: String?,
        @Field("fcm_token") fcmToken: String?
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("fcm-token")
    suspend fun updateFcmToken(
        @Header("Authorization") token: String?,
        @Field("fcm_token") fcmToken: String?,
    ): Response<UpdateFcmTokenResponse>

//    @FormUrlEncoded
//    @POST("ForgetPass")
//    suspend fun forgetPass(@Field("user_email") user_email: String): Response<ResultDto>

    @FormUrlEncoded
    @POST("give-me-email")
    suspend fun giveMeEmail(@Field("email") email: String): Response<GiveMeEmailResponse>

    @FormUrlEncoded
    @POST("check-forget-code")
    suspend fun validateForgetPasswordCode(
        @Field("code") code: String,
        @Field("email") email: String
    ): Response<ValidateForgetPasswordResponse>

    @FormUrlEncoded
    @POST("change-password")
    suspend fun changePassword(
        @Field("new_password") password: String,
        @Field("confirm_password") confirmPassword: String,
        @Field("email") email: String
    ): Response<ValidateForgetPasswordResponse>

//    @FormUrlEncoded
//    @POST("update_pass")
//    suspend fun updatePass(
//        @Field("user_id") user_id: String,
//        @Field("current_pass") current_pass: String,
//        @Field("user_pass") user_pass: String
//    ): Response<ResultDto>

    @FormUrlEncoded
    @POST("update-password")
    suspend fun updatePass(
        @Header("Authorization") token: String,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_password") confirmPassword: String
    ): Response<UpdatePasswordResponse>

    //    @GET("countries-api")
    @GET("countries")
    suspend fun allCountries(): Response<AllCountriesResponse>

    @GET("get-city/{country_id}")
    suspend fun allCities(@Path("country_id") countryId: String): Response<AllCitiesResponse>

    @GET("get-region/{cityId}")
    suspend fun allRegions(@Path("cityId") cityId: String): Response<AllRegionsResponse>

    @GET("get-subregion/{regionId}")
    suspend fun allSubRegions(@Path("regionId") regionId: String): Response<AllSubRegionsResponse>

    @FormUrlEncoded
    @POST("verification-Resend")
    suspend fun sendVerifyMail(
        @Field("email") email: String
    ): Response<SendVerifyMailResponse>

    @FormUrlEncoded
    @POST("cheek-verification")
    suspend fun checkVerifyMailCode(
        @Field("email") email: String,
        @Field("code") code: String
    ): Response<CheckVerifyMailCodeResponse>

    //____________________________________________________________________________________________//
    // Drawer

    @FormUrlEncoded
    @POST("updateDeviceToken")
    suspend fun updateFirebaseDeviceToken(
        @Field("user_id") user_id: String,
        @Field("device_token") deviceToken: String
    ): Response<ResultDto>

//    @FormUrlEncoded
//    @POST("getProfile")
//    suspend fun getProfile(@Field("user_id") user_id: String): Response<Member>

    @GET("my-account")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<MyAccountResponse>

//    @Multipart
//    @POST("edit_profile")
//    suspend fun editProfile(
//        @Part("user_id") user_id: RequestBody?,
//        @Part("user_pass") user_pass: RequestBody?,
//        @Part("user_name") user_name: RequestBody?,
//        @Part("address") address: RequestBody?,
//        @Part("country_id") countryId: RequestBody?,
//        @Part("city_id") cityId: RequestBody?,
//        @Part("region_id") regionId: RequestBody?,
//        @Part("user_email") user_email: RequestBody?,
//        @Part("user_phone") user_phone: RequestBody?,
//        @Part m_image: MultipartBody.Part?
//    ): Response<EditProfileResponse>

    @Multipart
    @POST("my-account")
    suspend fun editProfile(
        @Header("Authorization") token: String,
        @Part("first_name") firstName: RequestBody?,
        @Part("last_name") lastName: RequestBody?,
        @Part("birth_date") birthDate: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<UpdateInformationResponse>

    //---------------------------- Addresses ----------------------------//
    @GET("my-addresses")
    suspend fun getMyAddresses(
        @Header("Authorization") userToken: String?
    ): Response<GetMyAddressesResponse>

    @FormUrlEncoded
    @POST("add-address")
    suspend fun addToMyAddresses(
        @Header("Authorization") userToken: String?,
        @Field("address_type") addressType: String,
        @Field("address") address: String,
        @Field("country_id") countryId: String,
        @Field("city_id") cityId: String,
        @Field("region_id") regionId: String,
        @Field("subregion_id") subRegionId: String
    ): Response<AddToMyAddressesResponse>

    @FormUrlEncoded
    @POST("delete-address")
    suspend fun deleteFromMyAddresses(
        @Header("Authorization") userToken: String?,
        @Field("address_id") addressId: String,
    ): Response<DeleteFromMyAddressesResponse>

    @FormUrlEncoded
    @POST("update-address")
    suspend fun updateAddress(
        @Header("Authorization") userToken: String?,
        @Field("address_id") addressId: String,
        @Field("address_type") addressType: String,
        @Field("address") address: String,
        @Field("country_id") countryId: String,
        @Field("city_id") cityId: String,
        @Field("region_id") regionId: String,
        @Field("subregion_id") subRegionId: String
    ): Response<UpdateAddressResponse>
    //---------------------------- Addresses ----------------------------//


    @GET("about-us")
    suspend fun getAppInfo(): Response<MyAppInfoResponse>

    @GET("contact-us")
    suspend fun getContactUsAppInfo(): Response<ContactUsResponse>

    //Old one
//    @GET("getAppPolicy")
//    suspend fun getAppPolicy(): Response<PolicyInfoResponse>
    @GET("terms-and-conditions")
    suspend fun getAppPolicy(
//        @Header("lang") lang: String = if (LocalHelperUtil.isEnglish()) Constants.API_HEADER_LANG_EN else Constants.API_HEADER_LANG_AR
    ): Response<ArrayList<PolicyInfoDto>>

//    @FormUrlEncoded
//    @POST("contact_msg")
//    suspend fun contactMsg(
//        @Field("user_id") user_id: String,
//        @Field("user_name") user_name: String,
//        @Field("user_email") user_email: String,
//        @Field("user_phone") user_phone: String,
//        @Field("subject") subject: String,
//        @Field("details") details: String
//    ): Response<ResultDto>

    @FormUrlEncoded
    @POST("send-message")
    suspend fun contactMsg(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("subject") subject: String,
        @Field("message") message: String,
        @Field("iam_not_robot") iam_not_robot: String
    ): Response<ContactUsSendMessageResponse>

    //____________________________________________________________________________________________//
    // Products

    //    @GET("getUserNotifications/{userId}")
//    suspend fun getUserNotifications(@Path("userId") userId: String): Response<List<NotificationDto>>
    @GET("notifications")
    suspend fun getUserNotifications(
        @Header("Authorization") token: String
    ): Response<NotificationResponse>

    @GET("notifications/{notificationId}")
    suspend fun getNotificationDetails(
        @Header("Authorization") token: String,
        @Path("notificationId") notificationId: String
    ): Response<NotificationDetailsResponse>

    @GET("all-products")
    suspend fun getShopProducts(
        @Header("Authorization") userToken: String?,
        @Query("page") page: String
    ): Response<ShopProductsResponse>

    @GET("all-products/{subCategoryId}")
    suspend fun filterSubCategoryProducts(
        @Header("Authorization") userToken: String?,
        @Path("subCategoryId") subCategoryId: String,
        @Query("type") type: String?,
        @Query("value") value: String?,
        @Query("max") max: String,
        @Query("min") min: String,
        @Query("page") page: String,
    ): Response<ShopProductsResponse>

    @GET("specific-product/{id}")
    suspend fun getProductById(
        @Header("Authorization") userToken: String?,
        @Path("id") productId: String
    ): Response<ProductDetailsResponse>
//    ): Response<ProductByIdResponse>


    @GET("latest-products")
    suspend fun homeGetLatestProducts(
        @Header("Authorization") userToken: String?
//        ,@Query("city_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
        ,@Query("region_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
    ): Response<LatestProductsResponse>

    @GET("most-views")
    suspend fun homeGetMostViewedProducts(
        @Header("Authorization") userToken: String?
//        ,@Query("city_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
        ,@Query("region_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
    ): Response<HomeMostViewedProductsResponse>

    @GET("best-categories")
    suspend fun homeGetBestCategories(
        @Header("Authorization") userToken: String?
//        ,@Query("city_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
        ,@Query("region_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
    ): Response<HomeBestCategoriesResponse>

    @GET("home-categorys")
    suspend fun homeGetBottomSections(
        @Header("Authorization") userToken: String?
//        ,@Query("city_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
        ,@Query("region_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
    ): Response<HomeBootomSectionsResponse>

    @GET("best-selling")
    suspend fun homeGetMostSaleProducts(
        @Header("Authorization") userToken: String?
//        ,@Query("city_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
        ,@Query("region_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
    ): Response<HomeMostSaleProductsResponse>

//    @GET("all-products")
//    suspend fun allProductsByUserId(
////        @Query("page") page: String,
////        @Query("perpage") perpage: String,
////        @Query("us/er_id") user_id: String,
////        @Query("featured") featured: String,
//    ): Response<AllProductsResponse>

    @GET("featured-products")
    suspend fun homeGetFeaturedProducts(
        @Header("Authorization") token: String?
//        ,@Query("city_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
        ,@Query("region_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
    ): Response<FeaturedProductsResponse>

    @GET("Allproducts_one_cats")
    suspend fun allCategorizationByUserIdOld(
        @Query("page") page: String,
        @Query("perpage") perpage: String,
        @Query("cat_id") cat_id: String,
        @Query("user_id") user_id: String
    ): Response<CategoriesResponse>

    @GET("all-products/{categoryId}")
    suspend fun allCategorizationByUserId(
        @Header("Authorization") token: String?,
        @Path("categoryId") categoryId: String,
        @Query("vendor_id") brandId: String,
        @Query("page") page: String
    ): Response<ProductByCategoryResponse>

    @GET("Allproducts_manufacturers")
    suspend fun allProductsManufacturersByUserId(
        @Query("page") page: String,
        @Query("perpage") perpage: String,
        @Query("user_id") user_id: String
    ): Response<AllCategoriesResponse>

    @GET("categories")
//    @GET("sub_categories")
    suspend fun homeCategories(
        @Header("Authorization") token: String?,
        @Query("brand_id") brandId: String?,
        @Query("region_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null

    ): Response<HomeCategoriesResponse>

    @GET("categories")
    suspend fun homeSubCategories(
        @Header("Authorization") token: String?,
    ): Response<HomeCategoriesResponse>

    @GET("get-brands")
    suspend fun homeGetBrands(
        @Header("Authorization") token: String?,
//        @Query("city_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
        @Query("region_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
    ): Response<HomeBrandsResponse>

    @GET("specific-brand/{brandId}")
    suspend fun getBrandProducts(
        @Header("Authorization") token: String?,
        @Path("brandId") brandId: String
    ): Response<BrandProductsResponse>

    @GET("latest-deals")
    suspend fun latestDealsByUserId(
        @Header("Authorization") token: String?
//        ,@Query("city_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
        ,@Query("region_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
    ): Response<LatestDealsResponse>

    @GET("Home-counter")
    suspend fun getHomeCounter(
        @Query("region_id") regionId: String? = if(UserUtil.getRegionId()!="-1") UserUtil.getCityFiltrationId()else null
    ): Response<HomeCounterResponse>

    @FormUrlEncoded
    @POST("add_favourite")
    suspend fun addFavourite(
        @Field("user_id") user_id: String?,
        @Field("product_id_fk") property_id: String?
    ): Response<ResultDto>

    @FormUrlEncoded
    @POST("remove_favourite")
    suspend fun removeFavourite(
        @Field("user_id") user_id: String?,
        @Field("product_id_fk") property_id: String?
    ): Response<ResultDto>

    @FormUrlEncoded
    @POST("add-remove-item-wishlist")
    suspend fun addRemoveItemWishlist(
        @Header("Authorization") userToken: String?,
        @Field("product_id") productId: String?
    ): Response<FavouriteResponse>

    @FormUrlEncoded
    @POST("add-list-wishlist")
    suspend fun addItemsListToWishlist(
        @Header("Authorization") userToken: String?,
        @Field("ids") ids: String?
    ): Response<AddItemsListToWishListResponse>

    @GET("UserFavList/{userId}")
    suspend fun userFavListByUserId(@Path("userId") userId: String): Response<AllFavListResponse>

    @GET("my-wishlist")
    suspend fun myWishList(@Header("Authorization") userToken: String): Response<MyWishListResponse>

//    @POST("product_search/{userId}")
//    suspend fun productFilter(
//        @Path("userId") userId: String,
//        @Body searchRequest: SearchRequest
//    ): Response<SearchResponse>

    @FormUrlEncoded
    @POST("all-products")
    suspend fun productsFilterBySubCat(
        @Header("Authorization") userToken: String?,
        @Field("category_id") categoryIds: String?,
        @Field("min_price") minPrice: String,
        @Field("max_price") maxPrice: String,
        @Field("page") page: String
    ): Response<ShopProductsResponse>
//    ): Response<ProductsFilterBySubCatResponse>

//    @FormUrlEncoded
//    @POST("search_product_title/{userId}")
//    suspend fun productSearch(
//        @Path("userId") userId: String,
//        @Field("title") title: String
//    ): Response<SearchResponse>

//    @GET("home-page-search")
//    suspend fun productSearch(
//        @Query("key") productName: String,
//    ): Response<MyHomePageSearchResponse2>


    @FormUrlEncoded
    @POST("join-team")
    suspend fun joinTeam(
        @Header("Authorization") userToken: String?,
        @Field("team_id") teamId: Int,
        @Field("grams") grams: Int
    ): Response<JoinTeamResponse>

    @GET("teams/{teamId}")
    suspend fun teamDetails(
        @Header("Authorization") userToken: String?,
        @Path("teamId") teamId: Int
    ): Response<TeamDetailsResponse>

    @DELETE("team/delete-request/{teamId}")
    suspend fun deleteTeam(
        @Header("Authorization") userToken: String?,
        @Path("teamId") teamId: Int
    ): Response<ExitTeamResponse>


    @FormUrlEncoded
    @POST("home-page-search")
    suspend fun productSearch(
        @Header("Authorization") userToken: String?,
        @Field("term") term: String,
        @Field("page") page: String
    ): Response<HomeSearchResponse>

    @GET("all-teams")
    suspend fun getALlTeams(
        @Header("Authorization") userToken: String?,
        @Query("page") page: String,
    ): Response<AllTeamsResponse>

    @GET("my-teams")
    suspend fun getMyTeams(
        @Header("Authorization") userToken: String?,
    ): Response<MyTeamResponse>

    @FormUrlEncoded
    @POST("home-page-search")
    suspend fun sortSearchResult(
        @Header("Authorization") userToken: String?,
        @Field("term") term: String,
        @Field("type") type: String?,
        @Field("value") value: String?,
        @Field("min") min: String,
        @Field("max") max: String,
        @Field("page") page: String,
        @Field("category_id") categoryId: String?
    ): Response<HomeSearchResponse>

    @GET("MaxProductPrice")
    suspend fun maxProductPrice(): Response<PriceResponse>

    @GET("MinProductPrice")
    suspend fun minProductPrice(): Response<PriceResponse>

    @GET("getCategories")
    suspend fun getFilterCategories(
        @Query("page") page: String,
        @Query("perpage") perpage: String,
        @Query("user_id") user_id: String
    ): Response<FilterCategoriesResponse>

    //    @GET("home_ads")
//    suspend fun homeAds(): Response<List<AdsDto>>
    @GET("homepage-sliders")
    suspend fun homeAds(): Response<MyAdsDto>

    @GET("homepage-sliders")
    suspend fun homeTopSlider(
//        @Query("city_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
        @Query("region_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
    ): Response<HomePageSliderResponse>

    @GET("AllSpecialOffers")
    suspend fun allSpecialOffers(
//        @Query("city_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
        @Query("region_id") cityFiltrationId: String? = if (UserUtil.getCityFiltrationId() != "-1") UserUtil.getCityFiltrationId() else null
    ): Response<SpecialOffersResponse>

    //____________________________________________________________________________________________//
    // Request

//    @FormUrlEncoded
//    @POST("Question_Request")
//    suspend fun questionsRequest(
//        @Field("user_id") user_id: String,
//        @Field("user_name") user_name: String,
//        @Field("user_email") user_email: String,
//        @Field("user_phone") user_phone: String,
//        @Field("message") message: String
//    ): Response<ResultDto>

//    @FormUrlEncoded
//    @POST("send-query")
//    suspend fun questionsRequest(
//        @Field("fullname") fullname: String,
//        @Field("email") email: String,
//        @Field("phone") phone: String,
//        @Field("message") message: String,
//        @Header("Authorization") token: String
//    ): Response<SendQueryResponse>


//    @Multipart
//    @POST("send-query")
//    suspend fun questionsRequest(
//        @Header("Authorization") token: String?,
//        @Part("user_id") userId: RequestBody?,
//        @Part("subject") subject: RequestBody?,
//        @Part("message") message: RequestBody?,
////        @Part("attachment") attachment: MultipartBody.Part?
//    ): Response<SendQueryResponse>

    @FormUrlEncoded
    @POST("send-query")
    suspend fun questionsRequest(
        @Header("Authorization") token: String?,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("email") email: String,
//        @Field("service") service: String,
//        @Field("air_condition_number") airConditioningCount: String,
        @Field("message") message: String
    ): Response<SendQueryResponse>

//    @GET("My_new_Requests/{userId}")
//    suspend fun newRequests(@Path("userId") userId: String): Response<AllRequestsResponse>

    @GET("myQueries")
    suspend fun myQueries(
        @Header("Authorization") token: String
    ): Response<MyQueriesResponse>

    @FormUrlEncoded
    @POST("query-details")
    suspend fun queryDetails(
        @Header("Authorization") token: String,
        @Field("query_id") queryId: String
    ): Response<QueryDetailsResponse>

    @GET("My_replied_Requests/{userId}")
    suspend fun repliedRequests(@Path("userId") userId: String): Response<AllRequestsResponse>

    @GET("My_cancelled_Requests/{userId}")
    suspend fun cancelledRequests(@Path("userId") userId: String): Response<AllRequestsResponse>

    //____________________________________________________________________________________________//
    // Orders

    @GET("customer_promocodes/{userId}")
    suspend fun customerPromoCodes(@Path("userId") userId: String): Response<CustomerPromoCodesResponse>

    @GET("productExtras/{productId}")
    suspend fun productExtras(@Path("productId") productId: String): Response<ExtrasProductResponse>
    @FormUrlEncoded
    @POST("checkout")
    suspend fun saveOrderRequest(
        @Header("Authorization") token: String?,
        @Field("user_address") userAddress: String?,//id
        @Field("coupon") coupon: String?,
        @Field("payment_type") payment_type: String?,
        @Field("order_from") from: String? ="ANDROID",
        @Field("payment_id") payment_id:String?
    ): Response<CheckoutResponse>

    @FormUrlEncoded
    @POST("checkout")
    suspend fun checkout(
        @Header("Authorization") token: String?,
        @Field("user_address") userAddress: String?,
        @Field("coupon") coupon: String?,
        @Field("payment_type") payment_type: String?,
        @Field("order_from") from: String? ="ANDROID",
        @Field("payment_id") payment_id:String?
    ): Response<CheckoutResponse>
    @FormUrlEncoded
    @POST("check-promocode")
    suspend fun checkPromoCode(
        @Header("Authorization") userToken: String?,
        @Field("coupon") promoCode: String
    ): Response<CheckPromoCodeResponse>
//    @GET("all_my_orders/{userId}")
//    suspend fun allMyOrders(@Path("userId") userId: String): Response<AllOrderResponse>

    @GET("my-orders")
    suspend fun allMyOrders(
        @Header("Authorization") token: String
    ): Response<MyOrdersResponse2>

    @GET("get_order_by_id/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: String): Response<AllOrderResponse>

    @GET("order-details/{orderId}")
    suspend fun getOrderDetails(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Query("notification_id") notificationId: String?
    ): Response<OrderDetailsResponse>

//    @FormUrlEncoded
//    @POST("cancel_product_order")
//    suspend fun cancelProductOrder(
//        @Field("user_id") user_id: String,
//        @Field("order_id") order_id: String
//    ): Response<ResultDto>

    @FormUrlEncoded
    @POST("order-cancel")
    suspend fun cancelProductOrder(
        @Header("Authorization") token: String?,
        @Field("order_number") orderId: String
    ): Response<CancelOrderResponse>

    @GET("PaymentTypes")
    suspend fun paymentTypes(): Response<PaymentTypesResponse>

    //____________________________________________________________________________________________//
    // Cart

//    @FormUrlEncoded
//    @POST("add_cart")
//    suspend fun addToCart(
//        @Field("user_id") user_id: String,
//        @Field("product_id_fk") product_id: String,
//        @Field("product_qty") product_qty: String,
//        @Field("product_price") product_price: String,
//        @Field("product_size_fk") productSize: String,
//        @Field("product_color_fk") productColor: String
//    ): Response<ResultDto>

    @FormUrlEncoded
    @POST("add-to-cart")
    suspend fun addToCart(
        @Header("Authorization") userToken: String?,
        @Field("cart") cart: String
    ): Response<AddToCartResponse>

    @POST("AddTocart")
    suspend fun addListToCart(@Body orderRequest: OrderRequest): Response<ResultDto>

//    @GET("GetCartData/{userId}")
//    suspend fun getCartData(@Path("userId") userId: String): Response<CartDataResponse>

    @GET("my-cart")
    suspend fun getCartData(
        @Header("Authorization") userToken: String?,
    ): Response<MyCartResponse>

    @FormUrlEncoded
    @POST("check-product-stock")
    suspend fun checkProductStock(
        @Field("product_id") productId: String,
        @Field("quantity") quantity: String,
        @Field("size_id") sizeId: String,
        @Field("color_id") colorId: String,
    ): Response<CheckProductStockResponse>

//    @FormUrlEncoded
//    @POST("update_cart")
//    suspend fun updateCartItem(
//        @Field("user_id") user_id: String,
//        @Field("main_id") main_id: String,
//        @Field("product_id_fk") product_id: String,
//        @Field("product_qty") product_qty: String,
//        @Field("product_price") product_price: String,
//        @Field("product_size_fk") productSize: String,
//        @Field("product_color_fk") productColor: String
//    ): Response<ResultDto>

    @FormUrlEncoded
    @POST("cart-update-quantity")
    suspend fun updateCartItem(
        @Header("Authorization") userToken: String?,
        @Field("product_cart_id") productCartId: String,
        @Field("quantity") quantity: String,
    ): Response<UpdateCartQuantityResponse>

//    @FormUrlEncoded
//    @POST("remove_from_cart")
//    suspend fun removeCartItem(
//        @Field("user_id") user_id: String,
//        @Field("main_id") main_id: String
//    ): Response<ResultDto>

    @FormUrlEncoded
    @POST("delete-item-from-my-cart")
    suspend fun removeCartItem(
        @Header("Authorization") userToken: String?,
        @Field("item_id") id: String
    ): Response<RemoveCartItemResponse>

    @FormUrlEncoded
    @POST("remove_all_cart")
    suspend fun removeAllCart(@Field("user_id") user_id: String): Response<ResultDto>

//    @GET("CountCart/{userId}")
//    suspend fun getCartCount(@Path("userId") userId: String): Response<CartCountResponse>

    @GET("cart-count")
    suspend fun getCartCount(@Header("Authorization") userToken: String?): Response<CartCountResponse>
    //online payment
    @POST
    suspend fun getAuthToken3(
        @Url url: String,
        @Body body: AuthApiBodySend
    ): Response<AuthApiResponseModel>

    @POST
    suspend fun orderRegister(
        @Url url: String,
        @Body body: OrderRegisterBodySend
    ):Response<OrderRegisterResonseModel>

    @POST
    suspend fun cardPaymentKey(
        @Url url: String,
        @Body body: CardPaymentKeyBodySendModel
    ):Response<CardPaymentResponseModel>

}