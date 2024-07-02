package eramo.resultgate.data.remote.dto.drawer


import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.products.MyProductModel
import eramo.resultgate.util.removePriceComma

data class MyWishListResponse(
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
        @SerializedName("fake_price")
        val fakePrice: String?,
        @SerializedName("real_price")
        val realPrice: String?,
        @SerializedName("to")
        val to: String?,
        @SerializedName("primary_image")
        val primaryImage: String?,
        @SerializedName("taxes")
        val taxes: List<Any?>?,
        @SerializedName("ofer")
        val ofer: Int?,
        @SerializedName("primary_image_url")
        val primaryImageUrl: String?,
        @SerializedName("profit_percent")
        val profitPercent: Int?,
        @SerializedName("price_after_taxes")
        val priceAfterTaxes: String?,
        @SerializedName("average_rating")
        val averageRating: Int?,
        @SerializedName("category")
        val category: List<Category?>?
    ) {

        data class Category(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("primary_image_url")
            val primary_image_url: String?,
        )

        fun toMyProductModel(): MyProductModel {
            return MyProductModel(
                id,
                title,
                removePriceComma(fakePrice ?: "0.0").toDouble(),
                removePriceComma(realPrice ?: "0.0").toDouble(),
                -1,
                primaryImage,
                "",
                ofer,
                to,
                1,
                -1,
                primaryImageUrl,
                profitPercent,
                priceAfterTaxes,
                averageRating,
                MyProductModel.Category(
                    category?.get(0)?.id ?: -1,
                    category?.get(0)?.title ?: "",
                    category?.get(0)?.primary_image_url ?: "",
                    category?.get(0)?.primary_image_url ?: "",
                    ""
                )
            )
        }
    }
}