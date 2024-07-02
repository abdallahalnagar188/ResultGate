package eramo.resultgate.data.remote.dto.products

import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.FilterCategoryModel
import eramo.resultgate.util.LocalHelperUtil

data class FilterCategoriesResponse(
    @SerializedName("all_main_cats") var allMainCats: ArrayList<FilterCategoriesDto> = arrayListOf()
)

data class FilterCategoriesDto(
    @SerializedName("cat_id") var cat_id: String? = null,
    @SerializedName("name_ar") var name_ar: String? = null,
    @SerializedName("name_en") var name_en: String? = null,
    @SerializedName("sub_cats") var sub_cats: ArrayList<AllCategories>? = null
) {
    fun toFilterCategoriesModel(): FilterCategoryModel {
        return FilterCategoryModel(
            cat_id = cat_id ?: "",
            name = if (LocalHelperUtil.isEnglish()) name_en ?: "" else name_ar ?: "",
            sub_cats = sub_cats?.map { it.toCategoryModel() } ?: emptyList()
        )
    }
}