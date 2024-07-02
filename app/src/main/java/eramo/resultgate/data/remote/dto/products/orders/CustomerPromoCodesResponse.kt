package eramo.resultgate.data.remote.dto.products.orders

import com.google.gson.annotations.SerializedName

data class CustomerPromoCodesResponse(
    @SerializedName("customer_promocodes") var customerPromocodes: ArrayList<CustomerPromoCodes>? = null
)

data class CustomerPromoCodes(
    @SerializedName("promo_id_fk") var promoIdFk: String? = null,
    @SerializedName("user_id_fk") var userIdFk: String? = null,
    @SerializedName("be_used") var beUsed: String? = null,
    @SerializedName("num_of_uses") var numOfUses: String? = null,
    @SerializedName("promo_code_name") var promoCodeName: String? = null,
    @SerializedName("promo_code_percent") var promoCodePercent: String? = null
)