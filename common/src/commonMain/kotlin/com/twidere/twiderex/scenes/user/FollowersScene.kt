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
package com.twidere.twiderex.scenes.user

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.twidere.twiderex.component.UserListComponent
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.user.FollowersViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FollowersScene(
    userKey: MicroBlogKey,
) {
    val viewModel: FollowersViewModel = getViewModel {
        parametersOf(userKey)
    }
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    title = {
                        Text(stringResource(res = com.twidere.twiderex.MR.strings.scene_followers_title))
                    }
                )
            },
        ) {
            UserListComponent(viewModel)
        }
    }
}
