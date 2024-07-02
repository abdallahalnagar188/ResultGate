package eramo.tahoon.data.remote.dto.products.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import eramo.tahoon.data.local.entity.AllImagesEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllImagesDto(
    @SerializedName("id") var imgId: String? = null,
    @SerializedName("product_id_fk") var productIdFk: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("created") var created: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("alt") var alt: String? = null
) : Parcelable{
    fun toAllImagesEntity(): AllImagesEntity {
        return AllImagesEntity(
            imgId = imgId,
            productIdFk = productIdFk,
            image = image,
            created = created,
            userId = userId,
            alt = alt
        )
    }
}