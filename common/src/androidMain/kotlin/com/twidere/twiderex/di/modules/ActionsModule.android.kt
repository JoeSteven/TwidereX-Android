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
package com.twidere.twiderex.di.modules

import com.twidere.twiderex.action.ComposeAction
import com.twidere.twiderex.action.DirectMessageAction
import com.twidere.twiderex.action.DraftAction
import com.twidere.twiderex.action.MediaAction
import com.twidere.twiderex.action.StatusActions
import org.koin.dsl.module

actual val actionModule = module {
    single { ComposeAction(get(), get()) }
    single { DirectMessageAction(get()) }
    single { DraftAction(get(), get()) }
    single { MediaAction(get(), get(), get()) }
    single { StatusActions(get()) }
}
