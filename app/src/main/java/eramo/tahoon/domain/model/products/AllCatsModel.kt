package eramo.tahoon.domain.model.products

data class AllCatsModel(
    var catId: String,
    var title: String,
    var fromId: String,
    var image: String,
    var allProducts: List<ProductModel>
)