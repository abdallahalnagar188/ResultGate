package eramo.tahoon.data.local

import androidx.room.*
import eramo.tahoon.data.local.entity.CartDataEntity
import eramo.tahoon.data.local.entity.MyCartDataEntity
import eramo.tahoon.data.local.entity.MyFavouriteEntity

@Dao
interface EramoDao {

    // done
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(myCartDataEntity: MyCartDataEntity)

    // done
    @Query("SELECT EXISTS(SELECT * FROM MyCartDataEntity WHERE productId = :productId)")
    suspend fun isProductExist(productId: Int): Boolean

    //    // done
//    @Query("UPDATE MyCartDataEntity SET productQty = :productQuantity WHERE productId=:productId")
//    suspend fun updateQuantity(productId: Int,productQuantity:Int)
    // done
    @Query("UPDATE MyCartDataEntity SET productQty = productQty + :productQuantity  WHERE productId=:productId")
    suspend fun updateQuantityAdd(productId: Int,productQuantity:Int)
//    @Query("UPDATE MyCartDataEntity SET productQty = productQty + 1 WHERE productId=:productId")
//    suspend fun updateQuantityAdd(productId: Int)

//    @Query("UPDATE MyCartDataEntity SET productQty = productQty - 1 WHERE productId=:productId")
//    suspend fun updateQuantityMinus(productId: Int)

    @Query("SELECT * FROM CartDataEntity WHERE productIdFk=:productId")
    suspend fun getCartItemById(productId: String): CartDataEntity

    //    @Update(onConflict = OnConflictStrategy.REPLACE)
    @Query("UPDATE MyCartDataEntity SET productQty = :quantity WHERE productId=:productId")
    suspend fun updateCartItemQuantity(productId: Int, quantity: Int)

    @Query("DELETE FROM MyCartDataEntity WHERE productId = :mainId")
    suspend fun removeCartItemById(mainId: Int)

    @Query("SELECT * FROM MyCartDataEntity")
    suspend fun getCartList(): List<MyCartDataEntity>

    @Query("DELETE FROM MyCartDataEntity")
    suspend fun clearCartList()

    // Favourite
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(myFavouriteEntity: MyFavouriteEntity)

    @Query("DELETE FROM MyFavouriteEntity WHERE productId = :productId")
    suspend fun removeFromFavourite(productId: Int)

    @Query("SELECT EXISTS(SELECT * FROM MyFavouriteEntity WHERE productId = :productId)")
    suspend fun isProductFavourite(productId: Int): Boolean

    @Query("SELECT * FROM MyFavouriteEntity")
    suspend fun getFavouriteList(): List<MyFavouriteEntity>

    @Query("DELETE FROM MyFavouriteEntity")
    suspend fun clearFavouriteList()
}