package eramo.tahoon.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MyFavouriteEntity(
    @PrimaryKey(autoGenerate = true) var dbId: Int? = null,
    var productId: Int? = null,
    var productName: String? = null,
    var categoryName: String? = null,
    var imageUrl: String? = null,
    var modelNumber: String? = null,
    var price: Float? = null,
    var fakePrice: Float? = null,
    var isNew: Int? = null,
    var profitPercent: Int? = null,
)