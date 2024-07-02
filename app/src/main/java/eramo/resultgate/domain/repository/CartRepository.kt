package eramo.resultgate.domain.repository

import eramo.resultgate.data.local.entity.MyCartDataEntity
import eramo.resultgate.data.remote.dto.cart.AddToCartResponse
import eramo.resultgate.data.remote.dto.cart.CheckProductStockResponse
import eramo.resultgate.data.remote.dto.cart.CheckPromoCodeResponse
import eramo.resultgate.data.remote.dto.cart.RemoveCartItemResponse
import eramo.resultgate.data.remote.dto.cart.UpdateCartQuantityResponse
import eramo.resultgate.data.remote.dto.products.orders.CartCountResponse
import eramo.resultgate.domain.model.CartProductModel
import eramo.resultgate.domain.model.ResultModel
import eramo.resultgate.util.state.Resource
import kotlinx.coroutines.flow.Flow

interface CartRepository{

    suspend fun addToCartDB(
        myCartDataEntity: MyCartDataEntity
    ): Flow<Resource<ResultModel>>

    suspend fun addToCartApi(
        cart: String
    ): Flow<Resource<AddToCartResponse>>

    suspend fun getCartDataDB(): Flow<Resource<CartProductModel>>

    suspend fun getCartDataApi(): Flow<Resource<CartProductModel>>

    suspend fun checkProductStock(
        productId: String, quantity: String, sizeId: String, colorId: String
    ): Flow<Resource<CheckProductStockResponse>>

//    suspend fun updateCartItemDB(
//        main_id: String,
//        product_id: String,
//        product_qty: String,
//        product_price: String,
//        product_size: String,
//        product_color: String
//    ): Flow<Resource<ResultModel>>

    suspend fun updateCartItemQuantityDB(
        cartData: MyCartDataEntity
    ): Flow<Resource<ResultModel>>

    suspend fun updateCartItemApi(
        productCartId: String,
        quantity: String
    ): Flow<Resource<UpdateCartQuantityResponse>>

    suspend fun removeCartItemDB(main_id: String): Flow<Resource<ResultModel>>

    suspend fun removeCartItemApi(id: String): Flow<Resource<RemoveCartItemResponse>>

    suspend fun removeAllCartDB(): Flow<Resource<ResultModel>>

    suspend fun removeAllCartApi(): Flow<Resource<ResultModel>>

    suspend fun getCartCountDB(): Flow<Resource<CartCountResponse>>

    suspend fun getCartCountApi(): Flow<Resource<CartCountResponse>>

    suspend fun checkPromoCode(promoCode: String): Flow<Resource<CheckPromoCodeResponse>>

   suspend fun switchLocalCartToRemote(cart: String): Flow<Resource<ResultModel>>

}