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
package com.twidere.twiderex.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.twidere.twiderex.jobs.common.ShareMediaJob

class ShareMediaWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val shareMediaJob: ShareMediaJob,
) : CoroutineWorker(context, workerParams) {

    companion object {
        fun create(
            target: Uri,
        ) = OneTimeWorkRequestBuilder<ShareMediaWorker>()
            .setInputData(
                Data.Builder()
                    .putString("target", target.toString())
                    .build()
            )
            .build()
    }

    override suspend fun doWork(): Result {
        val target = inputData.getString("target") ?: return Result.failure()
        return try {
            shareMediaJob.execute(
                target
            )
            Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
