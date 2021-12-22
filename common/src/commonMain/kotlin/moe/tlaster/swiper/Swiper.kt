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
package moe.tlaster.swiper

import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Swiper(
    modifier: Modifier = Modifier,
    state: SwiperState,
    orientation: Orientation = Orientation.Vertical,
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    BoxWithConstraints(
        modifier = modifier,
    ) {
        LaunchedEffect(constraints.maxHeight) {
            state.maxHeight = constraints.maxHeight
        }
        Layout(
            modifier = Modifier.draggable(
                orientation = orientation,
                enabled = enabled && !state.dismissed,
                reverseDirection = reverseDirection,
                onDragStopped = { velocity ->
                    scope.launch {
                        state.fling(velocity)
                    }
                },
                onDragStarted = {
                    state.onStart.invoke()
                },
                state = DraggableState { dy ->
                    scope.launch {
                        with(state) {
                            snap(offset + dy)
                        }
                    }
                },
            ),
            content = content,
        ) { measurables, constraints ->
            layout(constraints.maxWidth, constraints.maxHeight) {
                val offset = state.offset
                val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
                measurables
                    .map {
                        it.measure(childConstraints)
                    }
                    .forEach { placeable ->
                        // TODO: current this centers each page. We should investigate reading
                        //  gravity modifiers on the child, or maybe as a param to Swiper.
                        val xCenterOffset = (constraints.maxWidth - placeable.width) / 2
                        val yCenterOffset = (constraints.maxHeight - placeable.height) / 2
                        placeable.place(
                            x = xCenterOffset,
                            y = yCenterOffset + offset.roundToInt(),
                        )
                    }
            }
        }
    }
}
