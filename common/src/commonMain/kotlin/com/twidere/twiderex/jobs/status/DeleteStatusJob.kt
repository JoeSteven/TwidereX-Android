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
package com.twidere.twiderex.jobs.status

import com.twidere.services.microblog.StatusService
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.notification.InAppNotification
import com.twidere.twiderex.repository.AccountRepository
import com.twidere.twiderex.repository.StatusRepository
import com.twidere.twiderex.utils.notifyError

class DeleteStatusJob(
    private val accountRepository: AccountRepository,
    private val statusRepository: StatusRepository,
    private val inAppNotification: InAppNotification
) {
    suspend fun execute(
        accountKey: MicroBlogKey,
        statusKey: MicroBlogKey
    ) {
        val status = statusKey.let {
            statusRepository.loadFromCache(it, accountKey = accountKey)
        } ?: throw Error("Can't find any status matches:$statusKey")
        val service = accountRepository.findByAccountKey(accountKey)?.let {
            it.service as? StatusService
        } ?: throw Error()
        try {
            service.delete(status.statusId)
        } catch (e: Throwable) {
            inAppNotification.notifyError(e)
            throw e
        }
    }
}
