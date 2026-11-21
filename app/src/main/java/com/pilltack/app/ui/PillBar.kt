package com.pilltack.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.tanh

data class BarTab(val key: String, val route: String, val label: String, val icon: ImageVector)

/**
 * Floating dark pill navigation bar (ColorOS style).
 * - A highlight capsule slides to the selected tab with a springy overshoot.
 * - Touch the bar and it squishes; drag it and it stretches with rubber-band resistance, then wobbles back.
 * - Dragging sideways scrubs across the tabs; the one under your finger is selected on release.
 */
@Composable
fun PillBar(tabs: List<BarTab>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    val scope = rememberCoroutineScope()
    val dx = remember { Animatable(0f) }
    val dy = remember { Animatable(0f) }
    val press = remember { Animatable(0f) }
    var scrub by remember { mutableStateOf<Int?>(null) }
    val sel by rememberUpdatedState(selectedIndex)
    val select by rememberUpdatedState(onSelect)
    val shown = scrub ?: selectedIndex
    val n = tabs.size

    // The gesture lives on a wrapper that does NOT move, so the finger position stays stable while the bar stretches.
    Box(Modifier.fillMaxWidth().pointerInput(n) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            scope.launch { press.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 500f)) }
            var rawX = 0f
            var rawY = 0f
            var dragging = false
            val padPx = 16.dp.toPx()
            val lim = 28f
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val ch = event.changes.firstOrNull { it.id == down.id } ?: break
                if (!ch.pressed) break
                val d = ch.positionChange()
                rawX += d.x
                rawY += d.y
                if (!dragging && (abs(rawX) > viewConfiguration.touchSlop || abs(rawY) > viewConfiguration.touchSlop)) dragging = true
                if (dragging) {
                    ch.consume()
                    val ox = lim * tanh(rawX / lim)                 // rubber band: gets harder the further you pull
                    val oy = lim * 0.6f * tanh(rawY / (lim * 0.6f))
                    scope.launch { dx.snapTo(ox); dy.snapTo(oy) }
                    val w = size.width - 2 * padPx
                    scrub = (((ch.position.x - padPx) / w) * n).toInt().coerceIn(0, n - 1)
                }
            }
            scope.launch { press.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = 450f)) }
            scope.launch { dx.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 260f)) }   // gentle snap back
            scope.launch { dy.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = 260f)) }
            val target = scrub
            scrub = null
            if (dragging && target != null && target != sel) select(target)
        }
    }) {
        Box(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            BoxWithConstraints(
                Modifier.fillMaxWidth().height(72.dp)
                    .graphicsLayer {
                        translationX = dx.value
                        translationY = dy.value
                        val stretch = abs(dx.value) / 840f
                        scaleX = 1f + stretch + press.value * 0.008f
                        scaleY = 1f - stretch * 0.5f - press.value * 0.01f
                    }
                    .shadow(14.dp, RoundedCornerShape(36.dp))
                    .clip(RoundedCornerShape(36.dp))
                    .background(PT.Pill)
            ) {
                val itemW = maxWidth / n
                val x by animateDpAsState(itemW * shown, spring(dampingRatio = 0.7f, stiffness = 380f), label = "indicator")
                Box(Modifier.offset(x = x).width(itemW).fillMaxHeight().padding(6.dp)
                    .clip(RoundedCornerShape(30.dp)).background(if (PT.dark) Color.White.copy(alpha = 0.14f) else PT.Brand.copy(alpha = 0.12f)))
                Row(Modifier.fillMaxSize()) {
                    tabs.forEachIndexed { i, t ->
                        PillItem(t, i == shown, Modifier.weight(1f).fillMaxHeight()) { select(i) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PillItem(tab: BarTab, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val bounce = remember { Animatable(1f) }
    LaunchedEffect(selected) {
        if (selected) { bounce.snapTo(0.9f); bounce.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 420f)) }
    }
    val on = if (PT.dark) Color.White else PT.Brand
    val off = if (PT.dark) Color.White.copy(alpha = 0.68f) else PT.Grey
    val tint by animateColorAsState(if (selected) on else off, tween(200), label = "tabTint")
    Column(
        modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Icon(tab.icon, tab.label, tint = tint, modifier = Modifier.size(26.dp).graphicsLayer { scaleX = bounce.value; scaleY = bounce.value })
        Spacer(Modifier.height(3.dp))
        Txt(tab.label, T.c2, tint, maxLines = 1)
    }
}
