package eramo.tahoon.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eramo.tahoon.data.local.entity.AllImagesEntity
import eramo.tahoon.data.local.entity.CartDataEntity
import eramo.tahoon.data.local.entity.MyCartDataEntity
import eramo.tahoon.data.local.entity.MyFavouriteEntity

@Database(
    entities = [CartDataEntity::class, AllImagesEntity::class, MyCartDataEntity::class, MyFavouriteEntity::class],
    version = 11,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EramoDB : RoomDatabase() {
    abstract val dao: EramoDao
}