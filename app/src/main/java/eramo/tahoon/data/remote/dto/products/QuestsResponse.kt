package eramo.tahoon.data.remote.dto.products

import com.google.gson.annotations.SerializedName

data class QuestsResponse(
    @SerializedName("mokadam") var mokadam: String? = null,
    @SerializedName("remain") var remain: String? = null,
    @SerializedName("quest_for_6_month") var questFor6Month: String? = null,
    @SerializedName("quest_for_12_month") var questFor12Month: String? = null,
    @SerializedName("quest_for_15_month") var questFor15Month: String? = null,
    @SerializedName("message") var message: String? = null
)