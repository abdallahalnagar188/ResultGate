package eramo.tahoon.data.remote.dto.products.orders

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.products.orders.AllProductExtrasModel
import eramo.tahoon.util.LocalHelperUtil

data class ExtrasProductResponse(
    @SerializedName("all_product_extras") var allProductExtraDtos: ArrayList<AllProductExtrasDto> = arrayListOf()
)

data class AllProductExtrasDto(
    @SerializedName("id") var id: String? = null,
    @SerializedName("product_id_fk") var productIdFk: String? = null,
    @SerializedName("extra_id_fk") var extraIdFk: String? = null,
    @SerializedName("extra_quantity") var extraQuantity: String? = null,
    @SerializedName("extra_price") var extraPrice: String? = null,
    @SerializedName("extra_cost") var extraCost: String? = null,
    @SerializedName("extra_en_name") var extraEnName: String? = null,
    @SerializedName("extra_ar_name") var extraArName: String? = null,
) {
    fun toAllProductExtrasModel(): AllProductExtrasModel {
        return AllProductExtrasModel(
            id = id ?: "",
            productIdFk = productIdFk ?: "",
            extraIdFk = extraIdFk ?: "",
            extraCost = extraCost ?: "",
            extraQuantity = extraQuantity ?: "",
            extraPrice = extraPrice ?: "",
            extraName = if (LocalHelperUtil.isEnglish()) extraEnName ?: "" else extraArName ?: ""
        )
    }
}