package eramo.tahoon.data.remote.dto.products

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.OffersModel

data class OffersResponse(
    @SerializedName("partners") var partners: ArrayList<OffersDto> = arrayListOf()
)

data class OffersDto(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("link") var link: String? = null,
    @SerializedName("created_at") var created_at: String? = null,
    @SerializedName("updated_at") var updated_at: String? = null
) {
    fun toOffersModel(): OffersModel {
        return OffersModel(
            id = id.toString(),
            name =  "",
            image = image ?: "",
            url = link ?: "",
            type =  ""
        )
    }
}

//data class OffersResponse(
//    @SerializedName("partners") var partners: ArrayList<OffersDto> = arrayListOf()
//)
//
//data class OffersDto(
//    @SerializedName("id") var id: String? = null,
//    @SerializedName("name") var name: String? = null,
//    @SerializedName("image") var image: String? = null,
//    @SerializedName("url") var url: String? = null,
//    @SerializedName("category") var category: String? = null
//) {
//    fun toOffersModel(): OffersModel {
//        return OffersModel(
//            id = id ?: "",
//            name = name ?: "",
//            image = image ?: "",
//            url = url ?: "",
//            category = category ?: ""
//        )
//    }
//}