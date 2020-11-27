/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.paging.mediator

import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.twidere.services.microblog.model.IStatus
import com.twidere.twiderex.db.AppDatabase
import com.twidere.twiderex.db.mapper.toDbTimeline
import com.twidere.twiderex.db.model.DbPagingTimeline.Companion.toPagingDbTimeline
import com.twidere.twiderex.db.model.DbPagingTimelineWithStatus
import com.twidere.twiderex.db.model.TimelineType
import com.twidere.twiderex.db.model.saveToDb
import com.twidere.twiderex.model.MicroBlogKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

@OptIn(ExperimentalPagingApi::class)
abstract class PagingWithGapMediator(
    accountKey: MicroBlogKey,
    database: AppDatabase,
) : PagingMediator(accountKey = accountKey, database = database) {
    private var loadCount = 0
    protected open val skipInitialLoad = true
    val loadingBetween = MutableLiveData(listOf<MicroBlogKey>())

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DbPagingTimelineWithStatus>
    ): MediatorResult {
        val maxStatusKey = when (loadType) {
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                lastItem.status.status.data.statusKey
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.REFRESH -> {
                null
            }
        }
        val sinceStatueKey = when (loadType) {
            LoadType.APPEND -> {
                null
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.REFRESH -> {
                withContext(Dispatchers.IO) {
                    database.pagingTimelineDao()
                        .getLatest(pagingKey, accountKey)?.status?.status?.data?.statusKey
                }
            }
        }
        if (skipInitialLoad && loadCount == 0 && loadType == LoadType.REFRESH && sinceStatueKey != null) {
            loadCount++
            return MediatorResult.Success(
                endOfPaginationReached = false
            )
        }
        return loadBetween(
            pageSize = state.config.pageSize,
            maxStatusKey = maxStatusKey,
            sinceStatueKey = sinceStatueKey
        ).let {
            // TODO: workaround for https://issuetracker.google.com/issues/173435602
            if (loadType == LoadType.REFRESH) {
                MediatorResult.Error(Error())
            } else {
                it
            }
        }
    }

    suspend fun loadBetween(
        pageSize: Int,
        maxStatusKey: MicroBlogKey? = null,
        sinceStatueKey: MicroBlogKey? = null,
    ): MediatorResult {
        if (maxStatusKey != null && sinceStatueKey != null) {
            loadingBetween.postValue((loadingBetween.value ?: listOf()) + maxStatusKey)
        }
        try {
            val max_id = withContext(Dispatchers.IO) {
                maxStatusKey?.let { database.statusDao().findWithStatusId(it)?.statusId }
            }
            val since_id = withContext(Dispatchers.IO) {
                sinceStatueKey?.let { database.statusDao().findWithStatusId(it)?.statusId }
            }
            val result = loadBetweenImpl(pageSize, max_id = max_id, since_id = since_id).map {
                it.toDbTimeline(accountKey, TimelineType.Custom).toPagingDbTimeline(pagingKey)
            }
            database.withTransaction {
                if (maxStatusKey != null) {
                    database.pagingTimelineDao().findWithStatusKey(maxStatusKey, accountKey)?.let {
                        it.isGap = false
                        database.pagingTimelineDao().insertAll(listOf(it))
                    }
                }
                if (sinceStatueKey != null) {
                    result.lastOrNull()?.timeline?.isGap = result.size >= pageSize - 1
                }
                result.saveToDb(database)
            }
            return MediatorResult.Success(
                endOfPaginationReached = result.isEmpty()
            )
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        } catch (e: SocketTimeoutException) {
            return MediatorResult.Error(e)
        } finally {
            if (maxStatusKey != null && sinceStatueKey != null) {
                loadingBetween.postValue((loadingBetween.value ?: listOf()) - maxStatusKey)
            }
        }
    }

    protected abstract suspend fun loadBetweenImpl(
        pageSize: Int,
        max_id: String? = null,
        since_id: String? = null,
    ): List<IStatus>
}
