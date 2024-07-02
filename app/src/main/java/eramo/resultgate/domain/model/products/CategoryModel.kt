package eramo.resultgate.domain.model.products

data class CategoryModel(
    var manufacturerId: String = "",
    var title: String = "",
    var fromId: String = "",
    var image: String = "",
    var allProducts: List<ProductModel> = arrayListOf()
)