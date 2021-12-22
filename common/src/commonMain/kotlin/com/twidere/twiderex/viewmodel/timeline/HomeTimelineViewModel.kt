/*
 *  Twidere X
 *
 *  Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>
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
package com.twidere.twiderex.viewmodel.timeline

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.twidere.services.microblog.TimelineService
import com.twidere.twiderex.db.CacheDatabase
import com.twidere.twiderex.extensions.asStateIn
import com.twidere.twiderex.paging.mediator.timeline.HomeTimelineMediator
import com.twidere.twiderex.repository.AccountRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import moe.tlaster.precompose.viewmodel.viewModelScope

class HomeTimelineViewModel(
    dataStore: DataStore<Preferences>,
    database: CacheDatabase,
    private val accountRepository: AccountRepository,
) : TimelineViewModel(dataStore) {
    private val account by lazy {
        accountRepository.activeAccount.mapNotNull { it }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val pagingMediator by lazy {
        account.mapNotNull { it }.mapLatest {
            HomeTimelineMediator(
                it.service as TimelineService,
                it.accountKey,
                database,
            )
        }.asStateIn(viewModelScope, null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val savedStateKey by lazy {
        account.mapNotNull { it }.mapLatest {
            "${it.accountKey}_home"
        }.asStateIn(viewModelScope, null)
    }
}
