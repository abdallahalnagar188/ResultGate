package eramo.tahoon.data.remote.dto.auth


import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.auth.CitiesModel
import eramo.tahoon.domain.model.auth.CitiesWithRegionsModel
import eramo.tahoon.domain.model.auth.RegionsInCitiesModel

data class AllCitiesResponse(
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
        @SerializedName("title_en")
        val titleEn: String?,
        @SerializedName("title_ar")
        val titleAr: String?,
        @SerializedName("country_id")
        val countryId: Int?,
        @SerializedName("regions")
        val regions: List<RegionList>?
    ) {

        data class RegionList(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("title_en")
            val titleEn: String?,
            @SerializedName("title_ar")
            val titleAr: String?
        ) {
            fun toRegionsInCitiesModel(): RegionsInCitiesModel {
                return RegionsInCitiesModel(id ?: -1, title ?: "", titleEn ?: "", titleAr ?: "")
            }
        }


        fun toCitiesModel(): CitiesModel {
            return CitiesModel(id ?: -1, title ?: "", titleEn ?: "", titleAr ?: "", countryId ?: -1)
        }

        fun toCitiesWithRegionsModel(): CitiesWithRegionsModel {
            return CitiesWithRegionsModel(id ?: -1, title ?: "", titleEn ?: "", titleAr ?: "", countryId ?: -1,
                regions!!.map { it!!.toRegionsInCitiesModel() }
            )
        }
    }
}