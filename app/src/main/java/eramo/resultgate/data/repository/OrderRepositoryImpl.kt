package eramo.resultgate.data.repository

import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.data.remote.dto.cart.CheckoutResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.CancelOrderResponse
import eramo.resultgate.data.remote.dto.drawer.myaccount.OrderDetailsResponse
import eramo.resultgate.data.remote.dto.products.orders.CustomerPromoCodes
import eramo.resultgate.domain.model.products.PaymentTypesModel
import eramo.resultgate.domain.model.products.orders.AllProductExtrasModel
import eramo.resultgate.domain.model.products.orders.OrderModel
import eramo.resultgate.domain.repository.OrderRepository
import eramo.resultgate.util.Constants
import eramo.resultgate.util.Constants.API_SUCCESS
import eramo.resultgate.util.UserUtil
import eramo.resultgate.util.state.ApiState
import eramo.resultgate.util.state.Resource
import eramo.resultgate.util.state.UiText
import eramo.resultgate.util.toResultFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderRepositoryImpl(private val eramoApi: EramoApi) : OrderRepository {

    override suspend fun customerPromoCodes(): Flow<Resource<List<CustomerPromoCodes>>> {
        return flow {
            val result = toResultFlow { eramoApi.customerPromoCodes(UserUtil.getUserId()) }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> emit(Resource.Success(it.data?.customerPromocodes))
                }
            }
        }
    }

    override suspend fun productExtras(productId: String): Flow<Resource<List<AllProductExtrasModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.productExtras(productId) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list =
                            apiState.data?.allProductExtraDtos?.map { it.toAllProductExtrasModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun saveOrderRequest(userAddress: String?, coupon: String?, payment_type: String?,payment_id:String?): Flow<Resource<CheckoutResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.checkout(
                    if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null, userAddress, coupon, payment_type,Constants.SIGN_UP_SIGN_FROM,payment_id
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

    override suspend fun checkout(userAddress: String?, coupon: String?, payment_type: String?,payment_id:String?): Flow<Resource<CheckoutResponse>> {
        return flow {
            val result = toResultFlow {
                eramoApi.checkout(
                    if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null, userAddress, coupon, payment_type,Constants.SIGN_UP_SIGN_FROM,payment_id
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

    override suspend fun allMyOrders(): Flow<Resource<List<OrderModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.allMyOrders("Bearer ${UserUtil.getUserToken()}") }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
//                        val list = apiState.data?.allOrders?.map { it.toOrderModel() }
                        val list = apiState.data?.data?.map { it!!.toOrderModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun getOrderById(orderId: String): Flow<Resource<List<OrderModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.getOrderById(orderId) }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.allOrders?.map { it.toOrderModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }

    override suspend fun getOrderDetails(orderId: String,notificationId:String?): Flow<Resource<OrderDetailsResponse>> {
        return flow {
            val result = toResultFlow { eramoApi.getOrderDetails("Bearer ${UserUtil.getUserToken()}", orderId,notificationId) }
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

    override suspend fun cancelProductOrder(orderId: String): Flow<Resource<CancelOrderResponse>> {
        return flow {
            val result =
                toResultFlow {
                    eramoApi.cancelProductOrder(  if (UserUtil.isUserLogin()) "Bearer ${UserUtil.getUserToken()}" else null,orderId)
                }
            result.collect {
                when (it) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(it.message!!))
                    is ApiState.Success -> {
                        val model = it.data
                        if (it.data?.status == API_SUCCESS) emit(Resource.Success(model))
                        else emit(
                            Resource.Error(
                                UiText.DynamicString(
                                    model?.message ?: "Error"
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun paymentTypes(): Flow<Resource<List<PaymentTypesModel>>> {
        return flow {
            val result = toResultFlow { eramoApi.paymentTypes() }
            result.collect { apiState ->
                when (apiState) {
                    is ApiState.Loading -> emit(Resource.Loading())
                    is ApiState.Error -> emit(Resource.Error(apiState.message!!))
                    is ApiState.Success -> {
                        val list = apiState.data?.paymentTypeDtos?.map { it.toPaymentTypesModel() }
                        emit(Resource.Success(list))
                    }
                }
            }
        }
    }
}