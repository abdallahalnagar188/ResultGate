package eramo.resultgate.data.remote.dto.drawer.myaccount


import com.google.gson.annotations.SerializedName

data class OrderDetailsResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val `data`: Data?
) {
    data class Data(
        @SerializedName("order_number")
        val orderNumber: String?,
        @SerializedName("total_price")
        val totalPrice: String?,
        @SerializedName("sub_total_price")
        val subTotalPrice: String?,
        @SerializedName("totel_product_price")
        val totelProductPrice: String?,
        @SerializedName("total_taxes_price")
        val totalTaxesPrice: String?,
        @SerializedName("shipping_fees")
        val shippingFees: String?,
        @SerializedName("payment_type")
        val paymentType: String?,
        @SerializedName("date")
        val date: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("descount")
        val descount: String?,
        @SerializedName("products")
        val products: List<Product?>?
    ) {
        data class Product(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("image")
            val image: String?,
            @SerializedName("price")
            val price: String?,
            @SerializedName("quantity")
            val quantity: String?,
//            @SerializedName("color_id")
//            val colorId: Int?,
            @SerializedName("color_name")
            val colorName: String?,
            @SerializedName("size_name")
            val sizeName: String?,
//            @SerializedName("size_id")
//            val sizeId: Int?,
            @SerializedName("category_name")
            val categoryName: String?,

            @SerializedName("extras")
            val extras: List<Extra?>?,

            @SerializedName("gram_kirat")
            val gramKirat: Int?,
            @SerializedName("gram_price")
            val gramPrice: String?,
            @SerializedName("weight")
            val weight: Double?
        ){
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