package eramo.tahoon.data.remote.dto.auth


import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.auth.SubRegionsModel

data class AllSubRegionsResponse(
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("region_id")
        val regionId: Int?,
        @SerializedName("title")
        val title: String?
    ) {
        fun toSubRegionModel(): SubRegionsModel {
            return SubRegionsModel(
                id ?: -1, title ?: ""
            )
        }
    }
}