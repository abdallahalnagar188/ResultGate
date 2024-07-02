package eramo.tahoon.data.remote.dto.auth


import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.auth.CountriesModel

data class AllCountriesResponse(
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
        @SerializedName("shortcut")
        val shortcut: String?,
        @SerializedName("logo")
        val image: String?
    ) {
        fun toCountriesModel(): CountriesModel {
            return CountriesModel(id ?: -1, title ?: "", shortcut ?: "", image ?: "")
        }
    }
}