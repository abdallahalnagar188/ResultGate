package eramo.resultgate.data.remote.dto.auth


import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.auth.RegionsModel

data class AllRegionsResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("message")
    val message: String?
) {
    data class Data(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("country_id")
        val countryId: Int?,
        @SerializedName("city_id")
        val cityId: Int?
    ) {
        fun toRegionsModel(): RegionsModel {
            return RegionsModel(id ?: -1, title ?: "", countryId ?: -1, cityId ?: -1)
        }
    }
}