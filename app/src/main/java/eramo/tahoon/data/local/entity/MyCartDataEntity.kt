package eramo.tahoon.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class MyCartDataEntity(
    @PrimaryKey  (autoGenerate = true) var dbId: Int? = null,
    var productId: Int? = null,
    var productName: String? = null,
    var categoryName: String? = null,
    var imageUrl: String? = null,
    var modelNumber: String? = null,
    var price: Float? = null,
    var limitation: Int? = null,
    var vendorId:String?=null,
    var vendorName:String?=null,
    var productQty: Int? = null,
    var sizeId: Int? = null,
    var colorId: Int? = null,

) {

//    fun toCartProductModel():CartProductModel{
//        return CartProductModel(
//            -1,productId,productQty, colorId.toString(), sizeId.toString(),"","",imageUrl,price,productName,categoryName,modelNumber,"",""

//        )
    }
