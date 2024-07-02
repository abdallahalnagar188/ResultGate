package eramo.tahoon.domain.model

import com.google.gson.annotations.SerializedName

data class CartProductModel(

    @SerializedName("product")
    val productList: List<ProductList?>?,
    @SerializedName("total_taxes")
    val totalTaxes: String?,
    @SerializedName("total_price")
    val totalPrice: String?,
    @SerializedName("sub_total")
    val subTotal: String?

) {
    data class ProductList(
        @SerializedName("product_cart_id")
        val productCartId: Int?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("quantity")
        val quantity: Int?,
        @SerializedName("color_id")
        val colorId: String?,
        @SerializedName("size_id")
        val sizeId: String?,
        @SerializedName("color")
        val color: String?,
        @SerializedName("size")
        val size: String?,
        @SerializedName("image")
        val image: String?,
        @SerializedName("price")
        var price: Float?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("product_category")
        val productCategory: String?,
        @SerializedName("model_number")
        val modelNumber: String?,
        @SerializedName("limitation")
        val limitation: Int?,
        @SerializedName("extras")
        val extras: List<Extra?>?,
        @SerializedName("has_extra")
        val hasExtra: String?  ,
        @SerializedName("vendor_name")
        val vendorName: String?
    ) {
        data class Extra(
            @SerializedName("cost")
            val cost: String?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("quantity")
            val quantity: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("total_cost")
            val totalCost: Int?
        )
    }
}