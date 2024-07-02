package eramo.tahoon.domain.model

import eramo.tahoon.domain.model.products.CategoryModel

data class FilterCategoryModel(
    var cat_id: String = "",
    var name: String = "",
    var sub_cats: List<CategoryModel> = arrayListOf()
)