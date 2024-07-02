package eramo.tahoon.data.remote.dto.home


import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.products.MyProductModel
import eramo.tahoon.util.removePriceComma

data class HomeBootomSectionsResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: List<Data?>?
) {
    data class Data(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("products")
        val products: List<Product?>?,
        @SerializedName("image_url")
        val imageUrl: String?,
        @SerializedName("type")
        val type: String?
    ) {
        data class Product(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("fake_price")
            val fakePrice: String?,
            @SerializedName("discount")
            val discount: Int?,
            @SerializedName("real_price")
            val realPrice: String?,
            @SerializedName("to")
            val to: String?,
            @SerializedName("category_id")
            val categoryId: Int?,
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
            val averageRating: Int?
        ){
            fun toMyProductModel(): MyProductModel {
                return MyProductModel(
                    id,
                    title,
                    removePriceComma(fakePrice ?: "0.0").toDouble(),
                    removePriceComma(realPrice ?: "0.0").toDouble(),
                    categoryId,
                    primaryImage,
                    "",
                    ofer,
                    to,
                    isFav,
                    new,
                    primaryImageUrl,
                    profitPercent,
                    priceAfterTaxes,
                    averageRating,
                    MyProductModel.Category(-1, "", "", "", "")
                )
            }
        }


    }
}