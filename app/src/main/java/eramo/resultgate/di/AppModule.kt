package eramo.resultgate.di

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eramo.resultgate.data.local.Converters
import eramo.resultgate.data.local.EramoDB
import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.data.repository.*
import eramo.resultgate.domain.repository.*
import eramo.resultgate.util.MyInterceptor
import eramo.resultgate.util.parser.GsonParser
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(okHttpClient: OkHttpClient): EramoApi =
        Retrofit.Builder()
            .baseUrl(EramoApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
            .create(EramoApi::class.java)


    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor {
            Log.e("api", it)
        }
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return loggingInterceptor
    }
    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor)
//            .addInterceptor(OptionalHeaderInterceptor(UserUtil.getCityFiltrationId()))
            .addInterceptor(MyInterceptor())
//            .addInterceptor(CurlLoggerInterceptor("CURL"))
            .connectTimeout(1000, TimeUnit.SECONDS)
            .writeTimeout(1000, TimeUnit.SECONDS)
            .readTimeout(1000, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideEramoDB(app: Application): EramoDB {
        return Room.databaseBuilder(app, EramoDB::class.java, "EramoDB")
            .addTypeConverter(Converters(GsonParser(Gson())))
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(eramoApi: EramoApi): AuthRepository {
        return AuthRepositoryImpl(eramoApi)
    }

    @Provides
    @Singleton
    fun provideDrawerRepository(eramoApi: EramoApi): DrawerRepository {
        return DrawerRepositoryImpl(eramoApi)
    }

    @Provides
    @Singleton
    fun provideProductsRepository(eramoApi: EramoApi,db: EramoDB): ProductsRepository {
        return ProductsRepositoryImpl(eramoApi,db.dao)
    }

    @Provides
    @Singleton
    fun provideRequestRepository(eramoApi: EramoApi): RequestRepository {
        return RequestRepositoryImpl(eramoApi)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(eramoApi: EramoApi): OrderRepository {
        return OrderRepositoryImpl(eramoApi)
    }

    @Provides
    @Singleton
    fun provideCartRepository(eramoApi: EramoApi, db: EramoDB,@ApplicationContext context:Context): CartRepository {
        return CartRepositoryImpl(eramoApi, db.dao,context )
    }

}