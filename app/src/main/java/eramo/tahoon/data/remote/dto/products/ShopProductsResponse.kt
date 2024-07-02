package eramo.tahoon.data.remote.dto.products


import android.os.Parcelable
import androidx.core.net.ParseException
import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.products.ShopProductModel
import kotlinx.parcelize.Parcelize


@Parcelize
data class ShopProductsResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("max")
    val max: String?,
    @SerializedName("status")
    val status: Int?
) :Parcelable{

    @Parcelize
    data class Data(
        @SerializedName("data")
        val `data`: List<ProductDataList?>?,
        @SerializedName("links")
        val links: Links?,
        @SerializedName("meta")
        val meta: Meta?
    ):Parcelable {

        @Parcelize
        data class ProductDataList(
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
        ) :Parcelable{

            @Parcelize
            data class Category(
                @SerializedName("id")
                val id: Int?,
                @SerializedName("primary_image_url")
                val primaryImageUrl: String?,
                @SerializedName("title")
                val tile: String?
            ):Parcelable

            @Throws(ParseException::class)
            private fun removePriceComma(value: String): Double {
                val newValue = value.replace(",", "")
                return newValue.toDouble()
            }

            fun toShopProductModel(): ShopProductModel {
                return ShopProductModel(
                    id,
                    title,
                    removePriceComma(fakePrice?:"0.0"),
                    removePriceComma(realPrice?:"0.0"),
                    categoryId,
                    to,
                    primaryImageUrl,
                    createdAt,
                    ofer,
                    isFav,
                    new,
                    primaryImageUrl,
                    profitPercent,
                    priceAfterTaxes,
                    averageRating,
                    ShopProductModel.CategoryShopProductModel(category?.get(0)?.id, category?.get(0)?.tile, category?.get(0)?.primaryImageUrl, category?.get(0)?.primaryImageUrl,
                        ""
                    ),
                )
            }

        }

        @Parcelize
        data class Links(
            @SerializedName("first")
            val first: String?,
            @SerializedName("last")
            val last: String?,
//            @SerializedName("next")
//            val next: Any?,
//            @SerializedName("prev")
//            val prev: Any?
        ):Parcelable

        @Parcelize
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
        ):Parcelable {

            @Parcelize
            data class Link(
                @SerializedName("active")
                val active: Boolean?,
                @SerializedName("label")
                val label: String?,
                @SerializedName("url")
                val url: String?
            ):Parcelable
        }
    }
}

//
//@Parcelize
//data class ShopProductsResponse(
//    @SerializedName("status")
//    val status: Int?,
//    @SerializedName("data")
//    val `data`: Data?
//) : Parcelable {
//
//    @Parcelize
//    data class Data(
//        @SerializedName("current_page")
//        val currentPage: Int?,
//        @SerializedName("data")
//        val `data`: List<ProductDataList>?,
//        @SerializedName("first_page_url")
//        val firstPageUrl: String?,
//        @SerializedName("from")
//        val from: Int?,
//        @SerializedName("last_page")
//        val lastPage: Int?,
//        @SerializedName("last_page_url")
//        val lastPageUrl: String?,
//        @SerializedName("links")
//        val links: List<Link>?,
//        @SerializedName("path")
//        val path: String?,
//        @SerializedName("per_page")
//        val perPage: Int?,
//        @SerializedName("to")
//        val to: Int?,
//        @SerializedName("total")
//        val total: Int?
//    ) : Parcelable {
//
//        @Parcelize
//        data class ProductDataList(
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
//            @SerializedName("created_at")
//            val createdAt: String?,
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
//            val priceAfterTaxes: Int?,
//            @SerializedName("average_rating")
//            val averageRating: Int?,
//            @SerializedName("category")
//            val category: List<Category?>?,
////            @SerializedName("taxes")
////            val taxes: List<Taxe>?
//        ) : Parcelable {
//
//            @Parcelize
//            data class Category(
//                @SerializedName("id")
//                val id: Int?,
//                @SerializedName("title")
//                val title: String?,
//                @SerializedName("image")
//                val image: String,
//                @SerializedName("primary_image_url")
//                val imageUrl: String?,
//                @SerializedName("type")
//                val type: String?
//            ) : Parcelable
//
//            @Parcelize
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
//            ) : Parcelable {
//                fun toShopProductModelTaxe(): ShopProductModel.TaxeShopProductModel{
//                    return ShopProductModel.TaxeShopProductModel(
//                        id, titleAr, titleEn, status, percentage, adminId, createdAt, updatedAt, deletedAt
//                    )
//                }
//            }
//
//            @Throws(ParseException::class)
//            private fun removePriceComma(value: String): Double {
//                val newValue = value.replace(",", "")
//                return newValue.toDouble()
//            }
//
//            fun toShopProductModel(): ShopProductModel {
//                return ShopProductModel(
//                    id,
//                    title,
//                    removePriceComma(fakePrice?:"0.0"),
//                    removePriceComma(realPrice?:"0.0"),
//                    categoryId,
//                    to,
//                    primaryImage,
//                    createdAt,
//                    ofer,
//                    isFav,
//                    new,
//                    primaryImageUrl,
//                    profitPercent,
//                    priceAfterTaxes.toString(),
//                    averageRating,
//                    ShopProductModel.CategoryShopProductModel(category?.get(0)?.id, category?.get(0)?.title, category?.get(0)?.image, category?.get(0)?.imageUrl,
//                        category?.get(0)?.type
//                    ),
//                )
//            }
//        }
//
//        @Parcelize
//        data class Link(
//            @SerializedName("label")
//            val label: String,
//            @SerializedName("active")
//            val active: Boolean
//        ) : Parcelable
//    }
//}