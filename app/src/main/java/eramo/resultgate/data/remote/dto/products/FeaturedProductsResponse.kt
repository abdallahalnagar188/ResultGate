package eramo.resultgate.data.remote.dto.products


import androidx.core.net.ParseException
import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.products.MyProductModel

data class FeaturedProductsResponse(
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
        @SerializedName("category_id")
        val categoryId: Int?,
        @SerializedName("primary_image")
        val primaryImage: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("ofer")
        val ofer: Int?,
        @SerializedName("to")
        val to: String?,
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
//        val category: Category?
        val category: List<Category?>?
    ) {
        data class Category(
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
        )

        fun toMyProductModel():MyProductModel{
            return MyProductModel(
                id,title,removePriceComma(fakePrice!!),removePriceComma(realPrice!!),categoryId,primaryImage,createdAt,ofer,to,isFav,new,primaryImageUrl,profitPercent,priceAfterTaxes,averageRating,
                MyProductModel.Category(
                    category?.get(0)?.id, category?.get(0)?.title, category?.get(0)?.image, category?.get(0)?.imageUrl, category?.get(0)?.type
                )
            )
        }

        @Throws(ParseException::class)
        private fun removePriceComma(value: String): Double {
            val newValue = value.replace(",", "")
            return newValue.toDouble()
        }
    }
}