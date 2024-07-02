package eramo.tahoon.data.remote.dto.products.orders

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import eramo.tahoon.R
import eramo.tahoon.data.remote.dto.products.common.AllImagesDto
import eramo.tahoon.domain.model.products.orders.DiscountFeesModel
import eramo.tahoon.domain.model.products.orders.OrderDetailsModel
import eramo.tahoon.domain.model.products.orders.OrderModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.state.UiText
import kotlinx.parcelize.Parcelize

data class AllOrderResponse(
    @SerializedName("all_orders") var allOrders: ArrayList<AllOrders> = arrayListOf()
)

@Parcelize
data class OrderDetails(
    @SerializedName("id") var id: String? = null,
    @SerializedName("order_id_fk") var orderIdFk: String? = null,
    @SerializedName("product_id_fk") var productIdFk: String? = null,
    @SerializedName("quantity") var quantity: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("installation_price") var installationPrice: String? = null,
    @SerializedName("supplier_id_fk") var supplierIdFk: String? = null,
    @SerializedName("fan_sn") var fanSn: String? = null,
    @SerializedName("compressor_sn") var compressorSn: String? = null,
    @SerializedName("supplier_name") var supplierName: String? = null,
    @SerializedName("manufacturer_ar_name") var manufacturerArName: String? = null,
    @SerializedName("manufacturer_en_name") var manufacturerEnName: String? = null,
    @SerializedName("model_ar_name") var modelArName: String? = null,
    @SerializedName("model_en_name") var modelEnName: String? = null,
    @SerializedName("power_ar_name") var powerArName: String? = null,
    @SerializedName("power_en_name") var powerEnName: String? = null,
    @SerializedName("product_name_en") var productNameEn: String? = null,
    @SerializedName("product_name_ar") var productNameAr: String? = null,
    @SerializedName("modeel_rkm") var modelRkm: String? = null,
    @SerializedName("sku_rkm") var skuRkm: String? = null,
    @SerializedName("status") var statusText: String? = null,
    @SerializedName("main_cat_ar_name") var mainCatAr: String? = null,
    @SerializedName("main_cat_en_name") var mainCatEn: String? = null,
    @SerializedName("sub_cat_ar_name") var subCatAr: String? = null,
    @SerializedName("sub_cat_en_name") var subCatEn: String? = null,
    @SerializedName("shipping_price") var shippingPrice: String? = null,
    @SerializedName("taxes_price") var taxesPrice: String? = null,
    @SerializedName("all_images") var allImageDtos: List<AllImagesDto>? = null
) :Parcelable{
    fun toOrderDetailsModel(): OrderDetailsModel {
        return OrderDetailsModel(
            id ?: "",
            orderIdFk ?: "",
            productIdFk ?: "",
            quantity ?: "",
            price ?: "",
            installationPrice ?: "",
            supplierIdFk ?: "",
            fanSn ?: "",
            compressorSn ?: "",
            supplierName ?: "",
            if (LocalHelperUtil.isEnglish()) manufacturerEnName ?: "" else manufacturerArName ?: "",
            if (LocalHelperUtil.isEnglish()) modelEnName ?: "" else modelArName ?: "",
            if (LocalHelperUtil.isEnglish()) powerEnName ?: "" else powerArName ?: "",
            if (LocalHelperUtil.isEnglish()) productNameEn ?: "" else productNameAr ?: "",
            modelRkm ?: "",
            skuRkm ?: "",
            getStatus(),
            if (LocalHelperUtil.isEnglish()) mainCatEn ?: "" else mainCatAr ?: "",
            if (LocalHelperUtil.isEnglish()) subCatEn ?: "" else subCatAr ?: "",
            shippingPrice ?: "",
            taxesPrice ?: "",
            allImageDtos
        )
    }

    private fun getStatus(): String {
        statusText?.let {
            if (it == "active") return if (LocalHelperUtil.isEnglish()) it else "نشط"
            else return if (LocalHelperUtil.isEnglish()) it else "غير نشط"
        } ?: return if (LocalHelperUtil.isEnglish()) "inactive" else "غير نشط"
    }
}

data class AllDiscountsFees(
    @SerializedName("id") var id: String? = null,
    @SerializedName("order_id_fk") var orderIdFk: String? = null,
    @SerializedName("discount") var discount: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("added_date") var addedDate: String? = null,
    @SerializedName("added_time") var addedTime: String? = null,
    @SerializedName("notes") var notes: String? = null
) {
    fun toDiscountFeesModel(): DiscountFeesModel {
        return DiscountFeesModel(
            id ?: "",
            orderIdFk ?: "",
            discount ?: "",
            userId ?: "",
            addedDate ?: "",
            addedTime ?: "",
            notes ?: ""
        )
    }
}

data class AllOrders(
    @SerializedName("order_id") var orderId: String? = null,
    @SerializedName("order_rkm") var orderRkm: String? = null,
    @SerializedName("order_from") var orderFrom: String? = null,
    @SerializedName("order_date") var orderDate: String? = null,
    @SerializedName("order_date_ar") var orderDateAr: String? = null,
    @SerializedName("order_time") var orderTime: String? = null,
    @SerializedName("order_date_s") var orderDateS: String? = null,
    @SerializedName("pay_type_id") var payTypeId: String? = null,
    @SerializedName("pay_type") var payType: String? = null,
    @SerializedName("client_id_fk") var clientIdFk: String? = null,
    @SerializedName("commission") var commission: String? = null,
    @SerializedName("get_way_percent") var getWayPercent: String? = null,
    @SerializedName("all_sum") var allSum: String? = null,
    @SerializedName("all_sum_install") var allSumInstall: String? = null,
    @SerializedName("all_sum_extras") var allSumExtras: String? = null,
    @SerializedName("promo_code_fk") var promoCodeFk: String? = null,
    @SerializedName("promo_code_percent") var promoCodePercent: String? = null,
    @SerializedName("customer_id") var customerId: String? = null,
    @SerializedName("customer_name") var customerName: String? = null,
    @SerializedName("customer_mob") var customerMob: String? = null,
    @SerializedName("customer_address") var customerAddress: String? = null,
    @SerializedName("custom_address") var customAddress: String? = null,
    @SerializedName("vendor_id_fk") var vendorIdFk: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("created") var created: String? = null,
    @SerializedName("updated") var updated: String? = null,
    @SerializedName("action_inprogress_date") var actionInprogressDate: String? = null,
    @SerializedName("action_inprogress_time") var actionInprogressTime: String? = null,
    @SerializedName("action_inprogress_user") var actionInprogressUser: String? = null,
    @SerializedName("action_completed_date") var actionCompletedDate: String? = null,
    @SerializedName("action_completed_time") var actionCompletedTime: String? = null,
    @SerializedName("action_completed_user") var actionCompletedUser: String? = null,
    @SerializedName("action_cancelled_date") var actionCancelledDate: String? = null,
    @SerializedName("action_cancelled_time") var actionCancelledTime: String? = null,
    @SerializedName("action_cancelled_user") var actionCancelledUser: String? = null,
    @SerializedName("action_cancelled_from") var actionCancelledFrom: String? = null,
    @SerializedName("token") var token: String? = null,
    @SerializedName("delivery_date") var deliveryDate: String? = null,
    @SerializedName("installation_date") var installationDate: String? = null,
    @SerializedName("technical_id_fk") var technicalIdFk: String? = null,
    @SerializedName("driver_id_fk") var driverIdFk: String? = null,
    @SerializedName("comments") var comments: String? = null,
    @SerializedName("comments_last_user") var commentsLastUser: String? = null,
    @SerializedName("comments_last_updated_date") var commentsLastUpdatedDate: String? = null,
    @SerializedName("order_details") var orderDetails: ArrayList<OrderDetails>? = null,
    @SerializedName("order_product_extras") var orderProductExtraDtos: ArrayList<AllProductExtrasDto>? = null,
    @SerializedName("all_discounts_fees") var allDiscountsFees: ArrayList<AllDiscountsFees>? = null,
    @SerializedName("sum_extra_fees") var sumExtraFees: Int? = null,
    @SerializedName("sum_discounts_fees") var sumDiscountsFees: Int? = null,
    @SerializedName("total_shipping") var totalShipping: Double? = null,
    @SerializedName("total_taxes") var totalTaxes: Double? = null,
    @SerializedName("final_total") var finalTotal: Double? = null
) {
    fun toOrderModel(): OrderModel {
        return OrderModel(
            orderId = orderId ?: "",
            orderRkm = orderRkm ?: "",
            orderFrom = orderFrom ?: "",
            orderDate = orderDate ?: "",
            orderDateAr = orderDateAr ?: "",
            orderTime = orderTime ?: "",
            orderDateS = orderDateS ?: "",
            payTypeId = payTypeId ?: "",
            payType = getPayType(),
            clientIdFk = clientIdFk ?: "",
            commission = commission ?: "",
            getWayPercent = getWayPercent ?: "",
            allSum = allSum ?: "",
            allSumInstall = allSumInstall ?: "",
            allSumExtras = allSumExtras ?: "",
            promoCode = getPromoCode(),
            promoCodePercent = promoCodePercent ?: "",
            customerId = customerId ?: "",
            customerName = customerName ?: "",
            customerMob = customerMob ?: "",
            customerAddress = customerAddress ?: "",
            customAddress = customAddress ?: "",
            vendorIdFk = vendorIdFk ?: "",
            status = getStatus(),
            created = created ?: "",
            updated = updated ?: "",
            actionInProgressDate = actionInprogressDate ?: "",
            actionInProgressTime = actionInprogressTime ?: "",
            actionInProgressUser = actionInprogressUser ?: "",
            actionCompletedDate = actionCompletedDate ?: "",
            actionCompletedTime = actionCompletedTime ?: "",
            actionCompletedUser = actionCompletedUser ?: "",
            actionCancelledDate = actionCancelledDate ?: "",
            actionCancelledTime = actionCancelledTime ?: "",
            actionCancelledUser = actionCancelledUser ?: "",
            actionCancelledFrom = actionCancelledFrom ?: "",
            token = token ?: "",
            deliveryDate = deliveryDate ?: "",
            installationDate = installationDate ?: "",
            technicalIdFk = technicalIdFk ?: "",
            driverIdFk = driverIdFk ?: "",
            comments = comments ?: "",
            commentsLastUser = commentsLastUser ?: "",
            commentsLastUpdatedDate = commentsLastUpdatedDate ?: "",
            orderDetails = orderDetails?.map { it.toOrderDetailsModel() } ?: emptyList(),
            orderProductExtraModels =
            orderProductExtraDtos?.map { it.toAllProductExtrasModel() } ?: emptyList(),
            allDiscountsFeesModels = allDiscountsFees?.map { it.toDiscountFeesModel() }
                ?: emptyList(),
            sumExtraFees = sumExtraFees ?: 0,
            sumDiscountsFees = sumDiscountsFees ?: 0,
            totalShipping = totalShipping ?: 0.0,
            totalTaxes = totalTaxes ?: 0.0,
            finalTotal = finalTotal ?: 0.0
        )
    }

    @JvmName("getPayType1")
    private fun getPayType(): UiText {
        return if (payType.equals("cash")) UiText.StringResource(R.string.payment_cash)
        else UiText.DynamicString("")
    }


    private fun getPromoCode(): UiText {
        return if (promoCodeFk.equals("0")) UiText.StringResource(R.string.s_egp, "0")
        else {
            val allSum = allSum?.toFloat()!!
            val percent = promoCodePercent?.toFloat()!!
            val value = (allSum * (percent) / 100)
            UiText.StringResource(R.string.s_egp, value.toString())
        }
    }

    @JvmName("getStatus1")
    private fun getStatus(): UiText {
        return status?.let {
            when (it) {
                "neworder" -> UiText.StringResource(R.string.new_order)
                "inprogress" -> UiText.StringResource(R.string.in_progress)
                "completed" -> UiText.StringResource(R.string.completed)
                "cancelled" -> UiText.StringResource(R.string.cancelled)
                else -> UiText.DynamicString("")
            }
        } ?: UiText.DynamicString("")
    }
}