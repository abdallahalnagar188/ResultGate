package eramo.tahoon.data.remote.dto.home


import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.home.HomeBrandsModel

class HomeBrandsResponse : ArrayList<HomeBrandsResponse.HomeBrandsResponseItem>() {
    data class HomeBrandsResponseItem(
        @SerializedName("description")
        val description: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("logo")
        val logo: String?,
        @SerializedName("name")
        val name: String?
    ) {
        fun toHomeBrandsModel(): HomeBrandsModel {
            return HomeBrandsModel(
                id ?: -1, name ?: "--", logo ?: ""
            )
        }
    }
}