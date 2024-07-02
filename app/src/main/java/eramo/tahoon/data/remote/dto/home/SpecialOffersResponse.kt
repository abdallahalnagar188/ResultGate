package eramo.tahoon.data.remote.dto.home


import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.OffersModel

data class SpecialOffersResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("message")
    val message: String?
) {
    data class Data(
        @SerializedName("image")
        val image: String?,
        @SerializedName("link")
        val link: String?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("ref_id")
        val refId: Int?,
        @SerializedName("image_url")
        val imageUrl: String?
    ){
        fun toOffersModel(): OffersModel{
            return OffersModel(
                refId.toString(),"",imageUrl?:"https://eramostore.com/uploads/user_front/navbar/202304031009202303221206e-RAMO-Store-logo-1.png",link?:"https://eramostore.com",type?:""
            )
        }
    }
}