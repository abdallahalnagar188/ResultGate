package eramo.tahoon.data.remote.dto.products

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.products.AllCatsModel
import eramo.tahoon.util.LocalHelperUtil

data class CategoriesResponse(
    @SerializedName("all_cats") var allCatDtos: ArrayList<AllCatsDto> = arrayListOf()
)

data class AllCatsDto(
    @SerializedName("cat_id") var catId: String? = null,
    @SerializedName("title_ar") var titleAr: String? = null,
    @SerializedName("title_en") var titleEn: String? = null,
    @SerializedName("from_id") var fromId: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("all_products") var allProducts: List<AllProductsDto>? = null
) {
    fun toAllCatsModel(): AllCatsModel {
        return AllCatsModel(
            catId = catId ?: "",
            title = if (LocalHelperUtil.isEnglish()) titleEn ?: "" else titleAr ?: "",
            fromId = fromId ?: "",
            image = image ?: "",
            allProducts = allProducts?.let { list -> list.map { it.toProductModel() } }
                ?: emptyList()
        )
    }
}