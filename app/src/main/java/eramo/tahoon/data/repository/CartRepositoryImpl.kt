package eramo.tahoon.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import eramo.tahoon.R
import eramo.tahoon.data.local.EramoDao
import eramo.tahoon.data.local.entity.MyCartDataEntity
import eramo.tahoon.data.remote.EramoApi
import eramo.tahoon.data.remote.dto.cart.AddToCartResponse
import eramo.tahoon.data.remote.dto.cart.CheckProductStockResponse
import eramo.tahoon.data.remote.dto.cart.CheckPromoCodeResponse
import eramo.tahoon.data.remote.dto.cart.RemoveCartItemResponse
import eramo.tahoon.data.remote.dto.cart.UpdateCartQuantityResponse
import eramo.tahoon.data.remote.dto.products.orders.CartCountResponse
import eramo.tahoon.domain.model.CartProductModel
import eramo.tahoon.domain.model.ResultModel
import eramo.tahoon.domain.repository.CartRepository
import eramo.tahoon.util.Constants.API_SUCCESS
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.UserUtil
import eramo.tahoon.util.state.ApiState
import eramo.tahoon.util.state.Resource
import eramo.tahoon.util.state.UiText
import eramo.tahoon.util.toResultFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CartRepositoryImpl(
    private val eramoApi: EramoApi,
    private val dao: EramoDao,
   @ApplicationContext private val context: Context
) : CartRepository {

    override suspend fun addToCartDB(
        myCartDataEntity: MyCartDataEntity,
    ): Flow<Resource<ResultModel>> {
        return flow {
            try {
                val cart = dao.getCartList()
                emit(Resource.Loading())
                if (dao.isProductExist(myCartDataEntity.productId!!)) {
//                    dao.updateQuantity(myCartDataEntity.productId!!,myCartDataEntity.productQty!!)
                    dao.updateQuantityAdd(myCartDataEntity.productId!!,myCartDataEntity.productQty!!)
                } else if(cart.isEmpty()){
                    dao.insertCartItem(myCartDataEntity)
                } else if(cart[0].vendorId == myCartDataEntity.vendorId){
                    dao.insertCartItem(myCartDataEntity)

                }else{
                    if (LocalHelperUtil.isEnglish()){
                        emit(Resource.Error(UiText.DynamicString(context.getString(R.string.you_must_buy_from_the_same_vendor))))
                    }else{
                        emit(Resource.Error(UiText.DynamicString("الرجاء الشراء من نفس التاجر")))
                    }
//                    emit(Resource.Error(UiText.DynamicString(context.getString(R.string.you_cant_buy_more_than_limit))))
                    return@flow
                }


                //context.getString(R.string.you_must_buy_from_the_same_vendor
//                    if (dao.isProductExist(productModel.productId))
//                    dao.updateQuantity(productModel.productId)
//                else
//                    dao.insertCartItem(
//                        CartDataEntity(
//                            productIdFk = productModel.productId,
//                            productName = productModel.productName,
//                            mainCat = productModel.mainCat,
//                            status = productModel.statusText,
//                            modelNumber = productModel.modelNumber,
//                            productQty = product_qty.toInt(),
//                            productPrice = productModel.displayPrice,
//                            withInstallation = Constants.TEXT_NO,
//                            manufacturerEnName = productModel.manufacturerName,
//                            modelEnName = productModel.modelName,
//                            powerEnName = productModel.powerName,
//                            inStock = productModel.inStockValue,
//                            availableAmount = productModel.availableAmount,
//                            shippingPrice = productModel.shipping,
//                            installation_cost = productModel.installationCost,
//                            allImageEntity = productModel.allImageDtos.map { it.toAllImagesEntity() },
//                            quantityPrice = productModel.displayPrice,
//                            productSizeId = productSize,
//                            productColorId = productColor
//                        )
//                    )
                emit(Resource.Success(ResultModel(1, "Success")))
            } catch (e: Exception) {
                emit(Resource.Error(UiText.DynamicString(e.message!!)))
            }
        }
    }
    override suspend fun switchLocalCartToRemote(cart: String): Flow<Resource<ResultModel>> {

        return flow {
            emit(Resource.Loading())
            val result = toResultFlow {
                eramoApi.addToCart(
                    if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null, cart
                )
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> {
                        dao.clearCartList()
                        val resultModel = ResultModel(200, "Success")
                        emit(Resource.Success(resultModel))
                    }
                }
            }
        }
    }
    override suspend fun addToCartApi(
        cart: String
    ): Flow<Resource<AddToCartResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.addToCart(
                    if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null, cart
                )
            }
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

    override suspend fun getCartDataDB(): Flow<Resource<CartProductModel>> {
        return flow {
            try {
                emit(Resource.Loading())
                val list = dao.getCartList()
                val productList = mutableListOf<CartProductModel.ProductList>()
                var totalPrice = 0.0f
                for (i in list) {
                    productList.add(
                        CartProductModel.ProductList(
                            -1,
                            i.productId,
                            i.productQty,
                            i.colorId.toString(),
                            i.sizeId.toString(),
                            "",
                            "",
                            i.imageUrl,
                            i.price,
                            i.productName,
                            i.categoryName,
                            i.modelNumber,i.limitation, emptyList(),"",""
                        )
                    )
                    totalPrice += (i.productQty!!.toFloat() * i.price!!)
                }


                val output = CartProductModel(productList, "", totalPrice.toString(), "")
//                val list = dao.getCartList().map { it.toCartDataModel() }
//                val map = HashMap<String, Any>()
//                map["list"] = list
//                map["taxes"] = "0.0"
//                map["shipping"] = "0.0"
//                map["total"] = "0.0"
                emit(Resource.Success(output))
            } catch (e: Exception) {
                emit(Resource.Error(UiText.DynamicString(e.message!!)))
            }
        }
    }

    override suspend fun getCartDataApi(): Flow<Resource<CartProductModel>> {
        return flow {
            val result = toResultFlow { eramoApi.getCartData(if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val data = apiState.data?.data?.toCartProductModel()

//                        val map = HashMap<String, Any>()
//                        if (apiState.data?.cartDataDtos.isNullOrEmpty()) {
//                            map["list"] = ArrayList<CartDataModel>(emptyList())
//                        } else {
//                            val list = apiState.data?.cartDataDtos?.map { it.toCartDataModel() }
//                            val taxes = apiState.data?.total_taxes ?: 0.0
//                            val shipping = apiState.data?.total_shipping ?: 0.0
//                            val total = apiState.data?.total_price ?: 0.0
//                            map["list"] = list as List<CartDataModel>
//                            map["taxes"] = taxes.toString()
//                            map["shipping"] = shipping.toString()
//                            map["total"] = total.toString()
//                        }
                        emit(Resource.Success(data))
                    }
                }
            }
        }
    }

    override suspend fun checkProductStock(
        productId: String, quantity: String, sizeId: String, colorId: String
    ): Flow<Resource<CheckProductStockResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.checkProductStock(productId, quantity, sizeId, colorId) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val data = apiState.data

                        if (data?.status == 200) {
                            emit(Resource.Success(data))
                        } else {
                            emit(Resource.Error(UiText.DynamicString(data?.message ?: "Error")))
                        }

                    }
                }
            }
        }
    }

    override suspend fun updateCartItemQuantityDB(
        cartData: MyCartDataEntity
    ): Flow<Resource<ResultModel>> {
        return flow {
            try {
                emit(Resource.Loading())
                dao.updateCartItemQuantity(cartData.productId!!, cartData.productQty!!)
                emit(Resource.Success(ResultModel(1, "Success")))
            } catch (e: Exception) {
                emit(Resource.Error(UiText.DynamicString(e.message!!)))
            }
        }
    }

    override suspend fun updateCartItemApi(
        productCartId: String,
        quantity: String
    ): Flow<Resource<UpdateCartQuantityResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.updateCartItem(
                    "Bearer ${UserUtil.getUserToken()}", productCartId, quantity
                )
            }
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

    override suspend fun removeCartItemDB(main_id: String): Flow<Resource<ResultModel>> {
        return flow {
            try {
                emit(Resource.Loading())
                dao.removeCartItemById(main_id.toInt())
                emit(Resource.Success(ResultModel(1, "Success")))
            } catch (e: Exception) {
                emit(Resource.Error(UiText.DynamicString(e.message!!)))
            }
        }
    }

    override suspend fun removeCartItemApi(id: String): Flow<Resource<RemoveCartItemResponse>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.removeCartItem(
                        "Bearer ${UserUtil.getUserToken()}", id
                    )
                }
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

    override suspend fun removeAllCartDB(): Flow<Resource<ResultModel>> {
        return flow {
            try {
                emit(Resource.Loading())
                dao.clearCartList()
                emit(Resource.Success(ResultModel(1, "Success")))
            } catch (e: Exception) {
                emit(Resource.Error(UiText.DynamicString(e.message!!)))
            }
        }
    }

    override suspend fun removeAllCartApi(): Flow<Resource<ResultModel>> {
        return flow {
            val result = toResultFlow { eramoApi.removeAllCart(UserUtil.getUserId()) }
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

    override suspend fun getCartCountDB(): Flow<Resource<CartCountResponse>> {
        return flow {
            try {
                emit(Resource.Loading())
                var cartCount = 0
                dao.getCartList().map { cartCount += it.productQty!! }
                emit(Resource.Success(CartCountResponse(count = cartCount)))
            } catch (e: Exception) {
                emit(Resource.Error(UiText.DynamicString(e.message!!)))
            }
        }
    }

    override suspend fun getCartCountApi(): Flow<Resource<CartCountResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.getCartCount(if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null) }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> emit(Resource.Success(it.data))
                }
            }
        }
    }

    override suspend fun checkPromoCode(promoCode: String): Flow<Resource<CheckPromoCodeResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.checkPromoCode(
                    if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,
                    promoCode
                )
            }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> emit(Resource.Success(it.data))
                }
            }
        }
    }


}