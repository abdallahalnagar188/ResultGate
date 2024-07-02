package eramo.resultgate.util


import com.google.gson.annotations.SerializedName

data class SSS(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("product")
        val product: List<Product?>?,
        @SerializedName("sub_total")
        val subTotal: String?,
        @SerializedName("total_extras")
        val totalExtras: String?,
        @SerializedName("total_price")
        val totalPrice: String?,
        @SerializedName("total_taxes")
        val totalTaxes: String?
    ) {
        data class Product(
            @SerializedName("color")
            val color: String?,
            @SerializedName("color_id")
            val colorId: Int?,
            @SerializedName("extras")
            val extras: List<Extra?>?,
            @SerializedName("id")
            val id: Int?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("model_number")
            val modelNumber: String?,
            @SerializedName("price")
            val price: String?,
            @SerializedName("product_cart_id")
            val productCartId: Int?,
            @SerializedName("product_category")
            val productCategory: String?,
            @SerializedName("quantity")
            val quantity: Int?,
            @SerializedName("size")
            val size: String?,
            @SerializedName("size_id")
            val sizeId: Int?,
            @SerializedName("title")
            val title: String?
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
}