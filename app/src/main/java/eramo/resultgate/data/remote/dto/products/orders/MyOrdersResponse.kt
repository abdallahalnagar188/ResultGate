package eramo.resultgate.data.remote.dto.products.orders


import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.products.orders.OrderModel
import eramo.resultgate.util.state.UiText

class MyOrdersResponse : ArrayList<MyOrdersResponse.MyOrdersResponseItem>(){
    data class MyOrdersResponseItem(
        @SerializedName("date")
        val date: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("total")
        val total: String?
    ){
        fun toOrderModel(): OrderModel {
            return OrderModel(
                orderId = id.toString(),
                orderRkm =  "",
                orderFrom =  "",
                orderDate = date ?: "",
                orderDateAr = "",
                orderTime =  "",
                orderDateS = "",
                payTypeId =  "",
                payType = UiText.DynamicString(""),
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
                finalTotal = total?.toInt()?.toDouble() ?: 0.0
            )
        }

    }
}