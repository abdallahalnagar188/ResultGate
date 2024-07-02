package eramo.resultgate.domain.model

import eramo.resultgate.domain.model.products.CategoryModel

data class FilterCategoryModel(
    var cat_id: String = "",
    var name: String = "",
    var sub_cats: List<CategoryModel> = arrayListOf()
)