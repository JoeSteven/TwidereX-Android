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
package com.twidere.twiderex.viewmodel

import com.twidere.services.microblog.MicroBlogService
import com.twidere.twiderex.model.AccountDetails
import com.twidere.twiderex.model.AmUser
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.PlatformType
import com.twidere.twiderex.repository.AccountRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf

internal abstract class AccountViewModelTestBase : ViewModelTestBase() {

    @MockK
    protected lateinit var mockUser: AmUser
    @MockK
    protected lateinit var mockAccount: AccountDetails
    @MockK
    protected lateinit var mockAccountRepository: AccountRepository

    abstract val mockService: MicroBlogService

    override fun setUp() {
        super.setUp()
        with(mockUser) {
            every { userId }.returns("123")
        }
        with(mockAccount) {
            every { service }.returns(mockService)
            every { accountKey }.returns(MicroBlogKey.twitter("123"))
            every { type }.returns(PlatformType.Twitter)
            every { user }.returns(mockUser)
        }
        every { mockAccountRepository.activeAccount }.returns(flowOf(mockAccount))
    }
}
