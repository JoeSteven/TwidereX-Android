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
package com.twidere.twiderex.component.lazy.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.twidere.twiderex.R
import com.twidere.twiderex.component.lazy.LazyColumn2
import com.twidere.twiderex.component.lazy.LazyPagingItems
import com.twidere.twiderex.component.lazy.items
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.component.status.UserName
import com.twidere.twiderex.component.status.UserScreenName
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.ui.standardPadding

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LazyUiUserList(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<UiUser>,
    state: LazyListState = rememberLazyListState(),
    key: ((index: Int) -> Any) = { items.peekOrNull(it)?.userKey?.hashCode() ?: it },
    onItemClicked: (UiUser) -> Unit = {},
    header: LazyListScope.() -> Unit = {},
) {
    LazyColumn2(
        modifier = modifier,
        state = state,
    ) {
        header.invoke(this)
        items(items, key = key) {
            (it ?: UiUser.placeHolder()).let {
                ListItem(
                    modifier = Modifier.clickable {
                        onItemClicked.invoke(it)
                    },
                    icon = {
                        UserAvatar(
                            user = it,
                        )
                    },
                    text = {
                        Row {
                            UserName(user = it)
                            Spacer(modifier = Modifier.width(standardPadding))
                            UserScreenName(user = it)
                        }
                    },
                    secondaryText = {
                        Row {
                            Text(
                                text = stringResource(
                                    id = R.string.common_controls_profile_dashboard_followers,
                                )
                            )
                            Spacer(modifier = Modifier.width(standardPadding))
                            Text(
                                text = it.followersCount.toString()
                            )
                        }
                    }
                )
            }
        }
        loadState(items.loadState.append) {
            items.retry()
        }
    }
}
