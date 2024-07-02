package eramo.resultgate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import eramo.resultgate.data.local.entity.AllImagesEntity
import eramo.resultgate.data.local.entity.CartDataEntity
import eramo.resultgate.data.local.entity.MyCartDataEntity
import eramo.resultgate.data.local.entity.MyFavouriteEntity

@Database(
    entities = [CartDataEntity::class, AllImagesEntity::class, MyCartDataEntity::class, MyFavouriteEntity::class],
    version = 11,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EramoDB : RoomDatabase() {
    abstract val dao: EramoDao
}