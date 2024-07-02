package eramo.tahoon.data.remote.dto.products

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.products.CategoryModel

data class NewCategoriesResponse(
    @SerializedName("all_main_cats") val all_main_cats: List<AllMainCat>
) {
    data class AllMainCat(
        @SerializedName("id") val id: Int?,
        @SerializedName("image_url") val image_url: String?,
        @SerializedName("title") val title: String?,
        @SerializedName("type") val type: String?

    ) {
        fun toCategoryModel(): CategoryModel {
            return CategoryModel(
                manufacturerId = id.toString() ?: "",
                title =  title ?: "",
                fromId = "",
                image = image_url ?: "",
//                allProducts = all_main_cats?.map { it.toProductModel() } ?: emptyList()
                allProducts = emptyList()
            )
        }
    }
}
