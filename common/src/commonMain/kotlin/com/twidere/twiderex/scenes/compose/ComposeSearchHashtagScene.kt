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
package com.twidere.twiderex.scenes.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.AppBarNavigationButton
import com.twidere.twiderex.component.foundation.InAppNotificationScaffold
import com.twidere.twiderex.component.foundation.TextInput
import com.twidere.twiderex.component.lazy.loadState
import com.twidere.twiderex.component.stringResource
import com.twidere.twiderex.di.ext.getViewModel
import com.twidere.twiderex.extensions.observeAsState
import com.twidere.twiderex.ui.LocalNavController
import com.twidere.twiderex.ui.TwidereScene
import com.twidere.twiderex.viewmodel.compose.MastodonComposeSearchHashtagViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComposeSearchHashtagScene() {
    val navController = LocalNavController.current
    val viewModel: MastodonComposeSearchHashtagViewModel = getViewModel()
    val text by viewModel.text.observeAsState(initial = "")
    val source = viewModel.source.collectAsLazyPagingItems()
    TwidereScene {
        InAppNotificationScaffold(
            topBar = {
                AppBar(
                    title = {
                        ProvideTextStyle(value = MaterialTheme.typography.body1) {
                            TextInput(
                                value = text,
                                onValueChange = {
                                    viewModel.text.value = it
                                },
                                maxLines = 1,
                                placeholder = {
                                    Text(text = stringResource(res = com.twidere.twiderex.MR.strings.scene_compose_hashtag_search_search_placeholder))
                                },
                                autoFocus = true,
                                alignment = Alignment.CenterStart,
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        navController.goBackWith("#$text")
                                    }
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        AppBarNavigationButton()
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                navController.goBackWith("#$text")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = stringResource(
                                    res = com.twidere.twiderex.MR.strings.accessibility_common_done
                                )
                            )
                        }
                    },
                )
            }
        ) {
            LazyColumn {
                loadState(source.loadState.refresh)
                items(source) {
                    it?.name?.let { name ->
                        ListItem(
                            modifier = Modifier
                                .clickable {
                                    navController.goBackWith("#$name")
                                }
                        ) {
                            Text(text = name)
                        }
                    }
                }
            }
        }
    }
}
