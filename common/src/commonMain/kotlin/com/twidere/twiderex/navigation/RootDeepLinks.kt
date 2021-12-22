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
package com.twidere.twiderex.navigation

import com.twidere.route.processor.AppRoute
import com.twidere.twiderex.model.MicroBlogKey
import com.twidere.twiderex.model.enums.ComposeType

/**
 * if deeplink has the same parameters with route in Root.kt,
 * make it's name the same to route parameters in Root.kt too
 */
const val twidereXSchema = "twiderex"

@AppRoute(
    schema = twidereXSchema
)
interface RootDeepLinks {
    interface Twitter {
        fun User(screenName: String): String
        fun Status(statusId: String): String
    }

    interface Mastodon {
        fun Hashtag(keyword: String): String
    }

    fun User(userKey: MicroBlogKey): String
    fun Status(statusKey: MicroBlogKey): String
    fun Search(keyword: String): String
    val SignIn: String

    fun Draft(draftId: String): String
    fun Compose(composeType: ComposeType?, statusKey: MicroBlogKey?): String
    fun Conversation(conversationKey: MicroBlogKey): String

    interface Callback {
        interface SignIn {
            val Mastodon: String
            val Twitter: String
        }
    }
}
