package eramo.resultgate.data.remote.dto.products


import com.google.gson.annotations.SerializedName

data class ProductByIdResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: Data?
) {
    data class Data(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("fake_price")
        val fakePrice: Int?,
        @SerializedName("real_price")
        val realPrice: Int?,
        @SerializedName("category_id")
        val categoryId: Int?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("details")
        val details: String?,
        @SerializedName("summary")
        val summary: String?,
        @SerializedName("instructions")
        val instructions: String?,
        @SerializedName("features")
        val features: String?,
        @SerializedName("extras")
        val extras: String?,
        @SerializedName("material")
        val material: String?,
        @SerializedName("sku_number")
        val skuNumber: String?,
        @SerializedName("model_number")
        val modelNumber: String?,
        @SerializedName("primary_image")
        val primaryImage: String?,
        @SerializedName("to")
        val to: String?,
        @SerializedName("video")
        val video: String?,
        @SerializedName("limitation")
        val limitation: Int?,
        @SerializedName("shipping")
        val shipping: String?,
        @SerializedName("number_of_sales")
        val numberOfSales: Int?,
        @SerializedName("views")
        val views: Int?,
        @SerializedName("main_category_id")
        val mainCategoryId: Int?,
        @SerializedName("city_id")
        val cityId: Int?,
        @SerializedName("featured_product")
        val featuredProduct: String?,
        @SerializedName("stock")
        val stock: Int?,
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
        val category: Category?,
        @SerializedName("colors")
        val colors: List<Any?>?,
        @SerializedName("sizes")
        val sizes: List<Size?>?,
        @SerializedName("taxes")
        val taxes: List<Taxe?>?,
        @SerializedName("media")
        val media: List<Media?>?,
        @SerializedName("products_with")
        val productsWith: List<ProductsWith?>?,
        @SerializedName("main_category")
        val mainCategory: MainCategory?
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

        data class Size(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("name")
            val name: String?
        )

        data class Taxe(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("percentage")
            val percentage: Int?
        )

        data class Media(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("product_id")
            val productId: Int?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("image_url")
            val imageUrl: String?
        )

        data class ProductsWith(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("real_price")
            val realPrice: Int?,
            @SerializedName("primary_image")
            val primaryImage: String?,
            @SerializedName("primary_image_url")
            val primaryImageUrl: String?,
            @SerializedName("profit_percent")
            val profitPercent: Int?,
            @SerializedName("price_after_taxes")
            val priceAfterTaxes: String?,
            @SerializedName("average_rating")
            val averageRating: Int?,
            @SerializedName("taxes")
            val taxes: List<Taxe?>?
        ) {
            data class Taxe(
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
                val deletedAt: Any?
            )
        }

        data class MainCategory(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title_ar")
            val titleAr: String?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("image_url")
            val imageUrl: String?,
            @SerializedName("type")
            val type: String?
        )
    }
}