package eramo.tahoon.data.remote.dto.products


import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.products.orders.ProductExtrasModel

data class ProductDetailsResponse(
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("ac_main_type")
        val acMainType: String?,
        @SerializedName("ac_sub_type")
        val acSubType: String?,
        @SerializedName("average_rating")
        val averageRating: Int?,
        @SerializedName("category")
        val category:  List<Category?>?,
        @SerializedName("category_id")
        val categoryId: Int?,
        @SerializedName("city_id")
        val cityId: Int?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("details")
        val details: String?,
        @SerializedName("discount")
        val discount: Int?,
        @SerializedName("extras")
        val extras: String?,
        @SerializedName("extras_items")
        val extrasItems: List<ExtrasItem?>?,
        @SerializedName("fake_price")
        val fakePrice: String?,
        @SerializedName("featured_product")
        val featuredProduct: String?,
        @SerializedName("features")
        val features: String?,
        @SerializedName("hot_cold")
        val hotCold: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("at_cart")
        val at_cart: String?,
        @SerializedName("cart_qty")
        val cart_qty: Int?,
        @SerializedName("inStock_quantity")
        val inStockQuantity: Int?,
        @SerializedName("installation")
        val installation: Installation?,
        @SerializedName("instructions")
        val instructions: String?,
        @SerializedName("is_fav")
        val isFav: Int?,
        @SerializedName("limitation")
        val limitation: Int?,
        @SerializedName("main_category")
        val mainCategory: MainCategory?,
        @SerializedName("main_category_id")
        val mainCategoryId: Int?,
        @SerializedName("manufacturer")
        val manufacturer: Manufacturer?,
        @SerializedName("media")
        val media: List<Media?>?,
        @SerializedName("model")
        val model: Model?,
        @SerializedName("model_number")
        val modelNumber: String?,
        @SerializedName("vendor_name")
        val vendorName: String?,
        @SerializedName("vendor_id")
        val vendorId: String?,
        @SerializedName("new")
        val new: Int?,
        @SerializedName("ofer")
        val ofer: Int?,
        @SerializedName("power")
        val power: Power?,
        @SerializedName("price_after_taxes")
        val priceAfterTaxes: String?,
        @SerializedName("primary_image")
        val primaryImage: String?,
        @SerializedName("primary_image_url")
        val primaryImageUrl: String?,
        @SerializedName("products_with")
        val productsWith: List<ProductsWith?>?,
        @SerializedName("profit_percent")
        val profitPercent: Int?,
        @SerializedName("real_price")
        val realPrice: String?,
        @SerializedName("shipping")
        val shipping: String?,
        @SerializedName("slug")
        val slug: String?,
        @SerializedName("stock")
        val stock: Int?,
        @SerializedName("summary")
        val summary: String?,
        @SerializedName("taxes")
        val taxes: List<Any?>?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("to")
        val to: String?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("video")
        val video: String?,
        @SerializedName("views")
        val views: Int?,
        @SerializedName("colors")
        val colors: List<Color?>?,
        @SerializedName("sizes")
        val sizes: List<Size?>?,
        @SerializedName("stocks")
        val stocks: List<Stocks?>?

    ) {
        data class Stocks(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("size_id")
            val sizeId: Int?,
            @SerializedName("color_id")
            val colorId: Int?,
            @SerializedName("stock")
            val stock: Int?,
        )

        data class Category(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("image_url")
            val imageUrl: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("type")
            val type: String?
        )

        data class ExtrasItem(
            @SerializedName("cost")
            val cost: Int?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("name")
            val name: String?
        ) {
            fun toProductExtrasModel(): ProductExtrasModel {
                return ProductExtrasModel(
                    id ?: -1, cost ?: 0, name ?: ""
                )
            }
        }

        data class Installation(
            @SerializedName("cost")
            val cost: Double?,
            @SerializedName("name")
            val name: String?
        )

        data class MainCategory(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("image_url")
            val imageUrl: String?,
            @SerializedName("title_ar")
            val titleAr: String?,
            @SerializedName("type")
            val type: String?
        )

        data class Manufacturer(
            @SerializedName("description")
            val description: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("logo")
            val logo: String?,
            @SerializedName("name")
            val name: String?
        )

        data class Media(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("image_url")
            val imageUrl: String?,
            @SerializedName("product_id")
            val productId: Int?
        )

        data class Model(
            @SerializedName("name")
            val name: String?
        )

        data class Power(
            @SerializedName("coverage_area_from")
            val coverageAreaFrom: Double?,
            @SerializedName("coverage_area_to")
            val coverageAreaTo: Double?,
            @SerializedName("name")
            val name: String?
        )

        data class ProductsWith(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("name")
            val title: String?,
            @SerializedName("real_price")
            val realPrice: String?,
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
            @SerializedName("at_cart")
            val inCart: String?,
            @SerializedName("vendor_id")
            val vendorId: String?,
            @SerializedName("taxes")
            val taxes: List<Taxe?>?,
            @SerializedName("stocks")
            val stocks: List<Stocks?>?
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

            data class Stocks(
                @SerializedName("id")
                val id: Int?,
                @SerializedName("size_id")
                val sizeId: Int?,
                @SerializedName("color_id")
                val colorId: Int?,
                @SerializedName("stock")
                val stock: Int?,
            )
        }

        data class Color(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("hexa")
            val code: String?
        )

        data class Size(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("name")
            val name: String?
        )
    }
}