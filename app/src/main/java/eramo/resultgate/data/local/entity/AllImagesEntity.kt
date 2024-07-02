package eramo.resultgate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import eramo.resultgate.data.remote.dto.products.common.AllImagesDto

@Entity
data class AllImagesEntity(
    var imgId: String? = null,
    var productIdFk: String? = null,
    var image: String? = null,
    var created: String? = null,
    var userId: String? = null,
    var alt: String? = null,
    @PrimaryKey val productImagesId: Int? = imgId?.toInt(),
){
    fun toAllImagesDto(): AllImagesDto {
        return AllImagesDto(
            imgId = imgId,
            productIdFk = productIdFk,
            image = image,
            created = created,
            userId = userId,
            alt = alt
        )
    }
}