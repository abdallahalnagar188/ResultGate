package eramo.tahoon.domain.model.products

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShopProductModel(

    @SerializedName("id")
    val id: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("fake_price")
    val fakePrice: Double?,
    @SerializedName("real_price")
    val realPrice: Double?,
    @SerializedName("category_id")
    val categoryId: Int?,
    @SerializedName("to")
    val to: String?,
    @SerializedName("primary_image")
    val primaryImage: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("ofer")
    val ofer: Int?,
    @SerializedName("is_fav")
    val isFav: Int?,
    @SerializedName("new")
    val new: Int?,
    @SerializedName("primary_image_url")
    val primaryImageUrl: String?,
    @SerializedName("profit_percent")
    val profitPercent: Int?,
    @SerializedName("price_after_taxes")
    val priceAfterTaxes: String?,
    @SerializedName("average_rating")
    val averageRating: Int?,
    @SerializedName("category")
    val category: CategoryShopProductModel?,
//    @SerializedName("taxes")
//    val taxes: List<TaxeShopProductModel>?
):Parcelable{

    @Parcelize
    data class CategoryShopProductModel(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("image")
        val image: String?,
        @SerializedName("image_url")
        val imageUrl: String?,
        @SerializedName("type")
        val type: String?
    ):Parcelable

    @Parcelize
    data class TaxeShopProductModel(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("title_ar")
        val titleAr: String?,
        @SerializedName("title_en")
        val titleEn: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("percentage")
        val percentage: Int?,
        @SerializedName("admin_id")
        val adminId: Int?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("deleted_at")
        val deletedAt: String?
    ):Parcelable
}