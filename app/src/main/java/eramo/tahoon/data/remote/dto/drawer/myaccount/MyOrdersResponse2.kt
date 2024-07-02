package eramo.tahoon.data.remote.dto.drawer.myaccount


import androidx.core.net.ParseException
import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.products.orders.OrderModel
import eramo.tahoon.util.state.UiText

data class MyOrdersResponse2(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val `data`: List<Data?>?
) {
    data class Data(
        @SerializedName("order_nubmer")
        val orderNubmer: String?,
        @SerializedName("total_price")
        val totalPrice: String?,
        @SerializedName("date")
        val date: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("payment_type")
        val paymentType: String?,
        @SerializedName("customer_promo_code_value")
        val customerPromoCodeValue: Int?
    ){
        @Throws(ParseException::class)
        private fun removePriceComma(value: String): Double {
            val newValue = value.replace(",", "")
            return newValue.toDouble()
        }
        fun toOrderModel(): OrderModel {
            return OrderModel(
                orderId = orderNubmer.toString(),
                orderRkm =  "",
                orderFrom =  "",
                orderDate = date ?: "",
                orderDateAr = date ?: "",
                orderTime =  date ?: "",
                orderDateS = date ?: "",
                payTypeId =  "",
                payType = UiText.DynamicString(paymentType.toString()),
                clientIdFk ="",
                commission =  "",
                getWayPercent ="",
                allSum = "",
                allSumInstall = "",
                allSumExtras =  "",
                promoCode = UiText.DynamicString(""),
                promoCodePercent =  "",
                customerId =  "",
                customerName =  "",
                customerMob =  "",
                customerAddress =  "",
                customAddress = "",
                vendorIdFk =  "",
                status = UiText.DynamicString(status?:""),
                created =  "",
                updated = "",
                actionInProgressDate =  "",
                actionInProgressTime =  "",
                actionInProgressUser =  "",
                actionCompletedDate = "",
                actionCompletedTime =  "",
                actionCompletedUser =  "",
                actionCancelledDate =  "",
                actionCancelledTime =  "",
                actionCancelledUser =  "",
                actionCancelledFrom ="",
                token = "",
                deliveryDate =  "",
                installationDate = "",
                technicalIdFk =  "",
                driverIdFk =  "",
                comments =  "",
                commentsLastUser =  "",
                commentsLastUpdatedDate =  "",
                orderDetails = emptyList(),
                orderProductExtraModels =  emptyList(),
                allDiscountsFeesModels =  emptyList(),
                sumExtraFees =  0,
                sumDiscountsFees =  0,
                totalShipping =  0.0,
                totalTaxes =  0.0,
//                finalTotal = totalPrice?.toDouble() ?: 0.0
                finalTotal = removePriceComma(totalPrice!!)
            )
        }

    }
}