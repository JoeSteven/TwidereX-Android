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
package com.twidere.twiderex.viewmodel.user

import androidx.paging.PagingData
import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.mock.paging.collectDataForTest
import com.twidere.twiderex.mock.service.MockTimelineService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.ui.UiMedia
import com.twidere.twiderex.repository.TimelineRepository
import com.twidere.twiderex.viewmodel.AccountViewModelTestBase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class UserMediaTimelineViewModelTest : AccountViewModelTestBase() {
    override val mockService: MicroBlogService
        get() = MockTimelineService()

    @MockK
    private lateinit var repository: TimelineRepository

    private lateinit var viewModel: UserMediaTimelineViewModel

    override fun setUp() {
        super.setUp()
        every { repository.mediaTimeline(any(), any(), any()) }.returns(
            flowOf(
                PagingData.from(
                    (0..3).map {
                        mockk<UiMedia>() to mockk()
                    }
                )
            )
        )
        viewModel = UserMediaTimelineViewModel(
            repository,
            mockAccountRepository,
            MicroBlogKey.twitter("321")
        )
    }

    @Test
    fun source_any(): Unit = runBlocking {
        viewModel.source.firstOrNull().let {
            assertNotNull(it)
            assert(it.collectDataForTest().any())
        }
    }
}
