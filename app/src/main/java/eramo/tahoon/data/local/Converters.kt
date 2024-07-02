package eramo.tahoon.data.local

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import eramo.tahoon.data.local.entity.AllImagesEntity
import eramo.tahoon.data.local.entity.CartDataEntity
import eramo.tahoon.util.parser.JsonParser

@ProvidedTypeConverter
class Converters(private val jsonParser: JsonParser) {

    @TypeConverter
    fun toCartListJson(carDataList: List<CartDataEntity>): String {
        return jsonParser.toJson(
            carDataList,
            object : TypeToken<ArrayList<CartDataEntity>>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun fromCartListJson(json: String): List<CartDataEntity> {
        return jsonParser.fromJson<ArrayList<CartDataEntity>>(
            json,
            object : TypeToken<ArrayList<CartDataEntity>>() {}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun toAllImagesJson(allImagesList: List<AllImagesEntity>): String {
        return jsonParser.toJson(
            allImagesList,
            object : TypeToken<ArrayList<AllImagesEntity>>() {}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun fromAllImagesJson(json: String): List<AllImagesEntity> {
        return jsonParser.fromJson<ArrayList<AllImagesEntity>>(
            json,
            object : TypeToken<ArrayList<AllImagesEntity>>() {}.type
        ) ?: emptyList()
    }

}