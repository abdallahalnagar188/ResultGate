package eramo.tahoon.domain.model.request

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("user_name") var userName: String? = null,
    @SerializedName("user_phone") var userPhone: String? = null,
    @SerializedName("pay_type") var payType: String? = null,
    @SerializedName("user_address") var userAddress: String? = null,
    @SerializedName("promo_code") var promoCode: Int? = null,
    @SerializedName("token") var token: String? = null,
    @SerializedName("orderItemList") var orderItemList: List<OrderItemList>? = arrayListOf(),
    @SerializedName("transaction_id") var transaction_id: String? = null,
//    @SerializedName("orderExtraList") var orderExtraList: List<OrderExtraList>? = arrayListOf()
)

data class OrderItemList(
    @SerializedName("product_id") var productId: Int? = null,
    @SerializedName("product_qty") var productQty: Int? = null,
    @SerializedName("product_price") var productPrice: Float? = null,
    @SerializedName("product_size_fk") var productSize: String? = null,
    @SerializedName("product_color_fk") var productColor: String? = null,
)

data class OrderExtraList(
    @SerializedName("extra_id") var extraId: String? = null,
    @SerializedName("extra_quantity") var extraQuantity: String? = null,
    @SerializedName("extra_price") var extraPrice: String? = null
)