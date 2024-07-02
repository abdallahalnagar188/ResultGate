package eramo.resultgate.data.remote.dto.home


import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.products.ShopProductModel
import eramo.resultgate.util.removePriceComma

data class HomeSearchResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("data")
        val `data`: List<Data?>?,
        @SerializedName("links")
        val links: Links?,
        @SerializedName("meta")
        val meta: Meta?
    ) {
        data class Data(
            @SerializedName("average_rating")
            val averageRating: Int?,
            @SerializedName("category")
            val category: List<Category?>?,
            @SerializedName("category_id")
            val categoryId: Int?,
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("discount")
            val discount: String?,
            @SerializedName("fake_price")
            val fakePrice: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("is_fav")
            val isFav: Int?,
            @SerializedName("is_new")
            val isNew: Int?,
            @SerializedName("new")
            val new: Int?,
            @SerializedName("ofer")
            val ofer: Int?,
            @SerializedName("offer")
            val offer: Int?,
            @SerializedName("offer_end_at")
            val offerEndAt: String?,
            @SerializedName("price_after_taxes")
            val priceAfterTaxes: String?,
            @SerializedName("primary_image_url")
            val primaryImageUrl: String?,
            @SerializedName("profit_percent")
            val profitPercent: Int?,
            @SerializedName("real_price")
            val realPrice: String?,
            @SerializedName("subcategory_id")
            val subcategoryId: Int?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("to")
            val to: String?,
            @SerializedName("vendor_image")
            val vendorImage: String?,
            @SerializedName("vendor_name")
            val vendorName: String?
        ) {
            data class Category(
                @SerializedName("id")
                val id: Int?,
                @SerializedName("primary_image_url")
                val primaryImageUrl: String?,
                @SerializedName("title")
                val title: String?
            )

            fun toShopProductModel(): ShopProductModel {
                return ShopProductModel(
                    id,
                    title,
                    removePriceComma(fakePrice?:"0.0").toDouble(),
                    removePriceComma(realPrice?:"0.0").toDouble(),
                    categoryId,
                    to,
                    primaryImageUrl,
                    "",
                    ofer,
                    isFav,
                    new,
                    primaryImageUrl,
                    profitPercent,
                    priceAfterTaxes,
                    averageRating,
                    ShopProductModel.CategoryShopProductModel(category?.get(0)?.id, category?.get(0)?.title, category?.get(0)?.primaryImageUrl, category?.get(0)?.primaryImageUrl,
                        ""
                    ),
//                    taxes!!.map { it!!.toShopProductModelTaxe() }
                )
            }
        }

        data class Links(
            @SerializedName("first")
            val first: String?,
            @SerializedName("last")
            val last: String?,
            @SerializedName("next")
            val next: Any?,
            @SerializedName("prev")
            val prev: Any?
        )

        data class Meta(
            @SerializedName("current_page")
            val currentPage: Int?,
            @SerializedName("from")
            val from: Int?,
            @SerializedName("last_page")
            val lastPage: Int?,
            @SerializedName("links")
            val links: List<Link?>?,
            @SerializedName("path")
            val path: String?,
            @SerializedName("per_page")
            val perPage: Int?,
            @SerializedName("to")
            val to: Int?,
            @SerializedName("total")
            val total: Int?
        ) {
            data class Link(
                @SerializedName("active")
                val active: Boolean?,
                @SerializedName("label")
                val label: String?,
                @SerializedName("url")
                val url: String?
            )
        }
    }
}

//data class HomeSearchResponse(
//    @SerializedName("status")
//    val status: Int?,
//    @SerializedName("data")
//    val `data`: List<Data?>?,
//    @SerializedName("message")
//    val message: String?
//) {
//    data class Data(
//        @SerializedName("current_page")
//        val currentPage: Int?,
//        @SerializedName("data")
//        val `data`: List<Data?>?,
//        @SerializedName("first_page_url")
//        val firstPageUrl: String?,
//        @SerializedName("from")
//        val from: Int?,
//        @SerializedName("last_page")
//        val lastPage: Int?,
//        @SerializedName("last_page_url")
//        val lastPageUrl: String?,
//        @SerializedName("links")
//        val links: List<Link?>?,
//        @SerializedName("next_page_url")
//        val nextPageUrl: String?,
//        @SerializedName("path")
//        val path: String?,
//        @SerializedName("per_page")
//        val perPage: Int?,
//        @SerializedName("prev_page_url")
//        val prevPageUrl: String?,
//        @SerializedName("to")
//        val to: Int?,
//        @SerializedName("total")
//        val total: Int?
//    ) {
//        data class Data(
//            @SerializedName("id")
//            val id: Int?,
//            @SerializedName("title")
//            val title: String?,
//            @SerializedName("fake_price")
//            val fakePrice: String?,
//            @SerializedName("real_price")
//            val realPrice: String?,
//            @SerializedName("category_id")
//            val categoryId: Int?,
//            @SerializedName("to")
//            val to: String?,
//            @SerializedName("primary_image")
//            val primaryImage: String?,
//            @SerializedName("ofer")
//            val ofer: Int?,
//            @SerializedName("is_fav")
//            val isFav: Int?,
//            @SerializedName("new")
//            val new: Int?,
//            @SerializedName("primary_image_url")
//            val primaryImageUrl: String?,
//            @SerializedName("profit_percent")
//            val profitPercent: Int?,
//            @SerializedName("price_after_taxes")
//            val priceAfterTaxes: String?,
//            @SerializedName("average_rating")
//            val averageRating: Int?,
//            @SerializedName("category")
//            val category: Category?,
////            @SerializedName("taxes")
////            val taxes: List<Taxe?>?
//        ) {
//            data class Category(
//                @SerializedName("id")
//                val id: Int?,
//                @SerializedName("title")
//                val title: String?,
//                @SerializedName("image")
//                val image: String?,
//                @SerializedName("image_url")
//                val imageUrl: String?,
//                @SerializedName("type")
//                val type: String?
//            )
//
//            data class Taxe(
//                @SerializedName("id")
//                val id: Int?,
//                @SerializedName("title_ar")
//                val titleAr: String?,
//                @SerializedName("title_en")
//                val titleEn: String?,
//                @SerializedName("status")
//                val status: String?,
//                @SerializedName("percentage")
//                val percentage: Int?,
//                @SerializedName("admin_id")
//                val adminId: Int?,
//                @SerializedName("created_at")
//                val createdAt: String?,
//                @SerializedName("updated_at")
//                val updatedAt: String?,
//                @SerializedName("deleted_at")
//                val deletedAt: String?
//            ){
//                fun toShopProductModelTaxe(): ShopProductModel.TaxeShopProductModel{
//                    return ShopProductModel.TaxeShopProductModel(
//                        id, titleAr, titleEn, status, percentage, adminId, createdAt, updatedAt, deletedAt
//                    )
//                }
//            }
//
//            fun toShopProductModel(): ShopProductModel {
//                return ShopProductModel(
//                    id,
//                    title,
//                    removePriceComma(fakePrice?:"0.0").toDouble(),
//                    removePriceComma(realPrice?:"0.0").toDouble(),
//                    categoryId,
//                    to,
//                    primaryImage,
//                    "",
//                    ofer,
//                    isFav,
//                    new,
//                    primaryImageUrl,
//                    profitPercent,
//                    priceAfterTaxes,
//                    averageRating,
//                    ShopProductModel.CategoryShopProductModel(category?.id, category?.title, category?.image, category?.imageUrl,
//                        category?.type
//                    ),
////                    taxes!!.map { it!!.toShopProductModelTaxe() }
//                )
//            }
//
//        }
//
//        data class Link(
//            @SerializedName("url")
//            val url: String?,
//            @SerializedName("label")
//            val label: String?,
//            @SerializedName("active")
//            val active: Boolean?
//        )
//    }
//}