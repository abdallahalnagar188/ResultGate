package eramo.tahoon.domain.repository

import eramo.tahoon.data.remote.dto.cart.CheckoutResponse
import eramo.tahoon.data.remote.dto.drawer.myaccount.CancelOrderResponse
import eramo.tahoon.data.remote.dto.drawer.myaccount.OrderDetailsResponse
import eramo.tahoon.data.remote.dto.products.orders.CustomerPromoCodes
import eramo.tahoon.domain.model.products.PaymentTypesModel
import eramo.tahoon.domain.model.products.orders.AllProductExtrasModel
import eramo.tahoon.domain.model.products.orders.OrderModel
import eramo.tahoon.util.state.Resource
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun customerPromoCodes(): Flow<Resource<List<CustomerPromoCodes>>>

    suspend fun productExtras(productId: String): Flow<Resource<List<AllProductExtrasModel>>>

    suspend fun saveOrderRequest(userAddress: String?,coupon: String?,payment_type: String?,payment_id:String?): Flow<Resource<CheckoutResponse>>

    suspend fun checkout(userAddress: String?,coupon: String?,payment_type: String?,payment_id:String?): Flow<Resource<CheckoutResponse>>

    suspend fun allMyOrders(): Flow<Resource<List<OrderModel>>>

    suspend fun getOrderById(orderId: String): Flow<Resource<List<OrderModel>>>

    suspend fun getOrderDetails(orderId: String,notificationId:String?): Flow<Resource<OrderDetailsResponse>>

    suspend fun cancelProductOrder(orderId: String): Flow<Resource<CancelOrderResponse>>

    suspend fun paymentTypes(): Flow<Resource<List<PaymentTypesModel>>>

}