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
package com.twidere.twiderex.initializer

import android.content.Context
import androidx.startup.Initializer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.twidere.twiderex.worker.dm.DirectMessageFetchWorker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DirectMessageInitializerHolder

private const val DirectMessageWorkName = "twiderex_direct_message"

class DirectMessageInitializer : Initializer<DirectMessageInitializerHolder>, KoinComponent {
    private val workManager: WorkManager by inject()

    override fun create(context: Context): DirectMessageInitializerHolder {
        workManager.enqueueUniquePeriodicWork(
            DirectMessageWorkName,
            ExistingPeriodicWorkPolicy.KEEP,
            DirectMessageFetchWorker.createRepeatableWorker()
        )
        return DirectMessageInitializerHolder()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}
