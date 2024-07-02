package eramo.tahoon.data.remote.dto.products.orders

import com.google.gson.annotations.SerializedName
import eramo.tahoon.R
import eramo.tahoon.data.remote.dto.products.common.AllImagesDto
import eramo.tahoon.domain.model.products.orders.CartDataModel
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.state.UiText

data class CartDataResponse(
    @SerializedName("cart_data") var cartDataDtos: ArrayList<CartDataDto> = arrayListOf(),
    @SerializedName("total_shipping") var total_shipping: Double? = null,
    @SerializedName("total_taxes") var total_taxes: Double? = null,
    @SerializedName("total_price") var total_price: Double? = null,
    @SerializedName("total") var total: Double? = null,
)

data class CartDataDto(
    @SerializedName("id") var id: String? = null,
    @SerializedName("product_id_fk") var productIdFk: String? = null,
    @SerializedName("product_qty") var productQty: String? = null,
    @SerializedName("product_price") var productPrice: String? = null,
    @SerializedName("with_installation") var withInstallation: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("date_ar") var dateAr: String? = null,
    @SerializedName("date_s") var dateS: String? = null,
    @SerializedName("manufacturer_ar_name") var manufacturerArName: String? = null,
    @SerializedName("manufacturer_en_name") var manufacturerEnName: String? = null,
    @SerializedName("model_ar_name") var modelArName: String? = null,
    @SerializedName("model_en_name") var modelEnName: String? = null,
    @SerializedName("power_ar_name") var powerArName: String? = null,
    @SerializedName("power_en_name") var powerEnName: String? = null,
    @SerializedName("product_size_fk") var productSize: String? = null,
    @SerializedName("product_color_fk") var productColor: String? = null,
    @SerializedName("color_ar_name") var colorArName: String? = null,
    @SerializedName("color_en_name") var colorEnName: String? = null,
    @SerializedName("hex_code") var hexCode: String? = null,
    @SerializedName("size_ar_name") var sizeArName: String? = null,
    @SerializedName("size_en_name") var sizeEnName: String? = null,
    @SerializedName("in_stock") var inStock: String? = null,
    @SerializedName("available_amount") var availableAmount: String? = null,
    @SerializedName("status") var statusText: String? = null,
    @SerializedName("shipping_price") var shippingPrice: String? = null,
    @SerializedName("product_name_en") var productNameEn: String? = null,
    @SerializedName("product_name_ar") var productNameAr: String? = null,
    @SerializedName("modeel_rkm") var modelRkm: String? = null,
    @SerializedName("installation_cost") var installation_cost: String? = null,
    @SerializedName("main_cat_ar_name") var mainCatArName: String? = null,
    @SerializedName("main_cat_en_name") var mainCatEnName: String? = null,
    @SerializedName("sub_cat_ar_name") var subCatArName: String? = null,
    @SerializedName("sub_cat_en_name") var subCatEnName: String? = null,
    @SerializedName("all_images") var allImageDtos: List<AllImagesDto>? = null,
) {
    fun toCartDataModel(): CartDataModel {
        return CartDataModel(
            id = id,
            productIdFk = productIdFk,
            mainCat = if (LocalHelperUtil.isEnglish()) mainCatEnName else mainCatArName,
            productQty = productQty,
            productPrice = productPrice,
            userId = userId,
            dateAr = dateAr,
            dateS = dateS,
            manufacturerName = if (LocalHelperUtil.isEnglish()) manufacturerEnName else manufacturerArName,
            modelName = if (LocalHelperUtil.isEnglish()) modelEnName else modelArName,
            powerName = if (LocalHelperUtil.isEnglish()) powerEnName else powerArName,
            inStock = getStockText(),
            productSize = productSize,
            productColor = productColor,
            colorName = if (LocalHelperUtil.isEnglish()) colorEnName else colorArName,
            hexCode = hexCode,
            sizeName = if (LocalHelperUtil.isEnglish()) sizeEnName else sizeArName,
            availableAmount = availableAmount,
            statusText = getStatus(),
            shippingPrice = shippingPrice,
            productName = if (LocalHelperUtil.isEnglish())
                productNameEn else productNameAr,
            modelNumber = modelRkm,
            installation_cost = installation_cost,
            subCatName = if (LocalHelperUtil.isEnglish())
                subCatEnName else subCatArName,
            allImageDtos = allImageDtos,
        )
    }

    private fun getStatus(): String {
        statusText?.let {
            if (it == "active") return if (LocalHelperUtil.isEnglish()) it else "نشط"
            else return if (LocalHelperUtil.isEnglish()) it else "غير نشط"
        } ?: return if (LocalHelperUtil.isEnglish()) "inactive" else "غير نشط"
    }

    private fun getStockText(): UiText {
        return if (inStock.equals("yes"))
            UiText.StringResource(R.string.in_stock_value, availableAmount as String)
        else
            UiText.StringResource(R.string.out_of_stock)
    }
}

data class CartCountResponse(
    @SerializedName("status") var status: Int? = null,
    @SerializedName("count") var count: Int? = null
)