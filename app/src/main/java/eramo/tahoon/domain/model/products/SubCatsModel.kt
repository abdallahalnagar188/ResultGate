package eramo.tahoon.domain.model.products

import eramo.tahoon.data.remote.dto.products.AllProductsDto

data class SubCatsModel(
    var catId: String? = null,
    var title: String? = null,
    var fromId: String? = null,
    var allProducts: ArrayList<AllProductsDto> = arrayListOf()
)