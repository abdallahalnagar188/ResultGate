package eramo.tahoon.data.remote.dto.products


import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.products.ShopProductModel

data class BrandProductsResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("description")
        val description: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("logo")
        val logo: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("products")
        val products: List<Product?>?
    ) {
        data class Product(
            @SerializedName("discount")
            val discount: String?,
            @SerializedName("fake_price")
            val fakePrice: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("is_fav")
            val isFav: Int?,
            @SerializedName("is_new")
            val isNew: Int?,
            @SerializedName("ofer")
            val ofer: Int?,
            @SerializedName("offer_end_at")
            val offerEndAt: String?,
            @SerializedName("real_price")
            val realPrice: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("category_id")
            val categoryId: Int?           ,
            @SerializedName("category_name")
            val categoryName: String?         ,
            @SerializedName("profit_percent")
            val profitPercent: Int?
        ){
            fun toShopProductModel(): ShopProductModel {
                return ShopProductModel(
                    id,title,fakePrice?.toDouble(),realPrice?.toDouble() ,categoryId,"",image,"",ofer,isFav,isNew,image,profitPercent,"",null,
                    ShopProductModel.CategoryShopProductModel(categoryId,categoryName,"","","")
                )
            }
        }

    }
}