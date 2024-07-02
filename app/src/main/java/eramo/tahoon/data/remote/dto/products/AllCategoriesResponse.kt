package eramo.tahoon.data.remote.dto.products

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.products.CategoryModel
import eramo.tahoon.util.LocalHelperUtil

data class AllCategoriesResponse(
    @SerializedName("all_cats") var all_cats: ArrayList<AllCategories> = arrayListOf()
)

data class AllCategories(
    @SerializedName("cat_id") var manufacturerId: String? = null,
    @SerializedName("title_ar") var titleAr: String? = null,
    @SerializedName("title_en") var titleEn: String? = null,
    @SerializedName("from_id") var fromId: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("all_products") var allProducts: ArrayList<AllProductsDto>? = null
) {
    fun toCategoryModel(): CategoryModel {
        return CategoryModel(
            manufacturerId = manufacturerId ?: "",
            title = if (LocalHelperUtil.isEnglish()) titleEn ?: "" else titleAr ?: "",
            fromId = fromId ?: "",
            image = image ?: "",
            allProducts = allProducts?.map { it.toProductModel() } ?: emptyList()
        )
    }
}