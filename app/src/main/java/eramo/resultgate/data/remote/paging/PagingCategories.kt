package eramo.resultgate.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import eramo.resultgate.data.remote.EramoApi
import eramo.resultgate.domain.model.products.ProductModel
import eramo.resultgate.util.Constants
import retrofit2.HttpException
import java.io.IOException

class PagingCategories(
    private val eramoApi: EramoApi,
    private val catId: String
) : PagingSource<Int, ProductModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductModel> {
        val page = params.key ?: Constants.PAGING_START_INDEX

        return try {
            val result = eramoApi.allCategorizationByUserIdOld(
                page.toString(),
                Constants.PAGING_PER_PAGE.toString(),
                catId,
                "1"
            ).body()!!.allCatDtos.map { it.toAllCatsModel() }[0].allProducts

            LoadResult.Page(
                data = result,
                prevKey = if (page == Constants.PAGING_START_INDEX) null else page - 1,
                nextKey = if (result.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}