@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.pilltack.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import com.pilltack.app.data.Repo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pilltack.app.data.Dose
import com.pilltack.app.data.Fmt
import com.pilltack.app.data.Medicine
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.format.TextStyle as DayStyle
import java.util.Locale

@Composable
fun Txt(text: String, style: TextStyle = T.b3, color: Color = PT.Ink, modifier: Modifier = Modifier,
        align: TextAlign? = null, maxLines: Int = Int.MAX_VALUE) =
    Text(text, modifier, color = color, style = style, textAlign = align, maxLines = maxLines)

val shapeNames = listOf("Capsule", "Caplet", "Round", "Oval", "Square", "Drop", "Bottle", "Hexagon")
val palette = listOf(0xFFF3F3F3, 0xFFBDBDBD, 0xFFF5E6B8, 0xFFF4B6B0, 0xFFF6E27A, 0xFF8FD18B,
    0xFF6EC1F5, 0xFF3D7BE0, 0xFFB8A4F0, 0xFFF29BD0, 0xFFE5383B, 0xFFF5A623).map { it.toInt() }

@Composable
fun PillGlyph(shape: Int, left: Color, right: Color, bg: Color, size: Dp = 52.dp, modifier: Modifier = Modifier) {
    Box(modifier.size(size).clip(RoundedCornerShape(size * 0.22f)).background(bg), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize(0.62f)) { drawPill(shape, left, right) }
    }
}

@Composable
fun MedGlyph(m: Medicine?, size: Dp = 52.dp) = PillGlyph(
    m?.shape ?: 0, Color(m?.colorLeft ?: 0xFFE5383B.toInt()), Color(m?.colorRight ?: 0xFFFFFFFF.toInt()),
    Color(m?.colorBg ?: 0xFFF5A623.toInt()), size
)

private fun DrawScope.drawPill(shape: Int, l: Color, r: Color) {
    val w = size.width; val h = size.height
    when (shape) {
        0 -> {
            val ch = h * 0.5f; val top = (h - ch) / 2
            val p = Path().apply { addRoundRect(RoundRect(0f, top, w, top + ch, CornerRadius(ch / 2))) }
            clipPath(p) {
                drawRect(l, Offset(0f, 0f), Size(w / 2, h))
                drawRect(r, Offset(w / 2, 0f), Size(w / 2, h))
            }
        }
        1 -> {
            val ch = h * 0.42f; val top = (h - ch) / 2
            drawRoundRect(l, Offset(0f, top), Size(w, ch), CornerRadius(ch * 0.3f))
            drawRect(r, Offset(w / 2 - 2f, top), Size(4f, ch))
        }
        2 -> { drawCircle(l, w * 0.45f); drawRect(r, Offset(w * 0.1f, h / 2 - 2f), Size(w * 0.8f, 4f)) }
        3 -> { drawOval(l, Offset(0f, h * 0.22f), Size(w, h * 0.56f)); drawRect(r, Offset(w / 2 - 2f, h * 0.3f), Size(4f, h * 0.4f)) }
        4 -> { drawRoundRect(l, Offset(w * 0.1f, h * 0.1f), Size(w * 0.8f, h * 0.8f), CornerRadius(w * 0.18f)); drawCircle(r, w * 0.12f) }
        5 -> {
            val p = Path().apply { moveTo(w / 2, h * 0.05f); lineTo(w * 0.8f, h * 0.55f); lineTo(w * 0.2f, h * 0.55f); close() }
            drawPath(p, l); drawCircle(l, w * 0.3f, Offset(w / 2, h * 0.65f)); drawCircle(r, w * 0.07f, Offset(w * 0.42f, h * 0.6f))
        }
        6 -> {
            drawRoundRect(l, Offset(w * 0.28f, h * 0.3f), Size(w * 0.44f, h * 0.65f), CornerRadius(w * 0.08f))
            drawRect(r, Offset(w * 0.34f, h * 0.06f), Size(w * 0.32f, h * 0.2f))
        }
        else -> {
            val p = Path().apply {
                moveTo(w * 0.25f, h * 0.1f); lineTo(w * 0.75f, h * 0.1f); lineTo(w, h / 2)
                lineTo(w * 0.75f, h * 0.9f); lineTo(w * 0.25f, h * 0.9f); lineTo(0f, h / 2); close()
            }
            drawPath(p, l); drawCircle(r, w * 0.12f)
        }
    }
}

@Composable
fun Logo(color: Color = PT.Blue) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        PillLogoMark(30.dp)
        Spacer(Modifier.width(4.dp))
        Txt("PillTrack", T.b1, color)
    }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val sc by animateFloatAsState(if (pressed) 0.96f else 1f, spring(stiffness = Spring.StiffnessMedium), label = "press")
    Button(onClick, modifier.fillMaxWidth().heightIn(min = 52.dp).graphicsLayer { scaleX = sc; scaleY = sc },
        enabled = enabled, shape = RoundedCornerShape(28.dp), interactionSource = src,
        colors = ButtonDefaults.buttonColors(containerColor = PT.Blue, contentColor = Color.White)) { Txt(text, T.b1, Color.White) }
}

@Composable
fun SoftButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val sc by animateFloatAsState(if (pressed) 0.96f else 1f, spring(stiffness = Spring.StiffnessMedium), label = "press")
    Button(onClick, modifier.fillMaxWidth().heightIn(min = 52.dp).graphicsLayer { scaleX = sc; scaleY = sc },
        shape = RoundedCornerShape(28.dp), interactionSource = src,
        colors = ButtonDefaults.buttonColors(containerColor = PT.Surface, contentColor = PT.Ink)) { Txt(text, T.b2) }
}

@Composable
fun Field(value: String, onChange: (String) -> Unit, hint: String, modifier: Modifier = Modifier,
          number: Boolean = false, singleLine: Boolean = true, minLines: Int = 1) {
    TextField(value, onChange, modifier.fillMaxWidth(), singleLine = singleLine, minLines = minLines,
        placeholder = { Txt(hint, T.b3, PT.Grey) }, textStyle = T.b3,
        keyboardOptions = KeyboardOptions(keyboardType = if (number) KeyboardType.Number else KeyboardType.Text),
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = PT.Surface, unfocusedContainerColor = PT.Surface,
            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, cursorColor = PT.Blue))
}

@Composable
fun Chip(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val bg by animateColorAsState(if (selected) PT.Blue else PT.Surface, tween(200), label = "chipBg")
    val fg by animateColorAsState(if (selected) Color.White else PT.Ink, tween(200), label = "chipFg")
    Box(modifier.heightIn(min = 48.dp).clip(RoundedCornerShape(24.dp)).background(bg)
        .clickable(onClick = onClick).padding(horizontal = 16.dp), contentAlignment = Alignment.Center) { Txt(text, T.c1, fg) }
}

@Composable
fun OptionRow(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 52.dp).clip(RoundedCornerShape(12.dp)).background(PT.Surface)
        .clickable(onClick = onClick).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Txt(text, T.b3, modifier = Modifier.weight(1f))
        if (selected) Icon(Icons.Filled.Check, null, tint = PT.Blue)
    }
}

@Composable
fun BackBar(onBack: (() -> Unit)?, onCancel: (() -> Unit)?, hPad: Dp = 16.dp) {
    Row(Modifier.fillMaxWidth().padding(horizontal = hPad, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (onBack != null) RoundIconButton(Icons.Filled.ChevronLeft, "Back", onBack)
        }
        LogoPill()
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            if (onCancel != null) Box(Modifier.heightIn(min = 46.dp).clip(RoundedCornerShape(23.dp)).background(PT.Surface)
                .clickable(onClick = onCancel).padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                Txt("Cancel", T.b2, PT.Link)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Txt(title, T.b1, modifier = Modifier.weight(1f))
        if (action != null) TextButton(onAction) { Txt(action, T.c1, PT.Link) }
    }
}

/** One row on the schedule: a stored Dose or a computed upcoming occurrence. */
data class Slot(val med: Medicine?, val at: Long, val dose: Dose?, val name: String, val detail: String) {
    val status: String get() = dose?.status ?: "SCHEDULED"
    val open: Boolean get() = status == "SCHEDULED" || status == "PENDING"
}

@Composable
fun StatusMark(status: String) {
    when (status) {
        "TAKEN" -> Icon(Icons.Filled.CheckCircle, "Taken", tint = PT.Green)
        "SKIPPED" -> Icon(Icons.Filled.SkipNext, "Skipped", tint = PT.Grey)
        "MISSED" -> Icon(Icons.Filled.Cancel, "Missed", tint = PT.Red)
        "PENDING" -> Icon(Icons.Filled.Alarm, "Due now", tint = PT.Link)
        else -> Icon(Icons.Filled.ChevronRight, null, tint = PT.Grey)
    }
}

@Composable
fun DoseCard(s: Slot, h24: Boolean, extra: String? = null, onClick: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(PT.Surface)
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        MedGlyph(s.med, 52.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row { Txt("Medicine Name: ", T.c1, PT.Grey); Txt(s.name, T.c1, maxLines = 1) }
            if (s.detail.isNotBlank()) Txt(s.detail, T.c1, PT.Grey)
            Row { Txt("Time: ", T.c1, PT.Grey); Txt(Fmt.time(s.at, h24), T.c1) }
            if (extra != null) Txt(extra, T.c2, PT.Grey)
        }
        StatusMark(s.status)
    }
}

/** Taken / Skip / Snooze (5, 10, 15, 30) for a slot. */
@Composable
fun DoseDialog(s: Slot, h24: Boolean, onTaken: () -> Unit, onSkip: () -> Unit, onSnooze: (Int) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {}, containerColor = PT.Dialog,
        title = { Txt(s.name, T.h2) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Txt("Scheduled ${Fmt.time(s.at, h24)}" + if (s.detail.isNotBlank()) " · ${s.detail}" else "", T.b3, PT.Grey)
                PrimaryButton("Taken", onClick = onTaken)
                SoftButton("Skip this dose", onClick = onSkip)
                if (s.status == "PENDING") {
                    Txt("Snooze for", T.c1, PT.Grey)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(5, 10, 15, 30).forEach { Chip("$it min", false) { onSnooze(it) } }
                    }
                }
            }
        })
}

@Composable
fun TimeDialog(initial: String, h24: Boolean, onOk: (String) -> Unit, onCancel: () -> Unit) {
    val t = LocalTime.parse(initial)
    val st = rememberTimePickerState(t.hour, t.minute, h24)
    AlertDialog(onDismissRequest = onCancel, containerColor = PT.Dialog,
        confirmButton = { TextButton({ onOk("%02d:%02d".format(st.hour, st.minute)) }) { Text("OK") } },
        dismissButton = { TextButton(onCancel) { Text("Cancel") } },
        text = { TimePicker(st) })
}

fun millisToDay(ms: Long): LocalDate = java.time.Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate()

@Composable
fun DatePickDialog(initialDay: Long, onOk: (Long) -> Unit, onCancel: () -> Unit) {
    val st = rememberDatePickerState(initialSelectedDateMillis = initialDay * 86_400_000L)
    DatePickerDialog(onDismissRequest = onCancel,
        confirmButton = { TextButton({ st.selectedDateMillis?.let { onOk(millisToDay(it).toEpochDay()) } ?: onCancel() }) { Text("OK") } },
        dismissButton = { TextButton(onCancel) { Text("Cancel") } }) { DatePicker(st) }
}

@Composable
fun DateRow(label: String, epochDay: Long, onPick: (Long) -> Unit) {
    var show by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().heightIn(min = 52.dp).clip(RoundedCornerShape(12.dp)).background(PT.Surface)
        .clickable { show = true }.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Txt(label, T.b3, PT.Grey, Modifier.weight(1f))
        Txt(Fmt.date(LocalDate.ofEpochDay(epochDay)), T.b2)
    }
    if (show) DatePickDialog(epochDay, { onPick(it); show = false }, { show = false })
}

/** Month grid that lets the user tick several calendar dates. */
@Composable
fun MultiDates(selected: MutableList<Long>) {
    var month by remember { mutableStateOf(YearMonth.now()) }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton({ month = month.minusMonths(1) }) { Icon(Icons.Filled.ChevronLeft, "Previous month") }
            Txt("${month.month.getDisplayName(DayStyle.FULL, Locale.getDefault())} ${month.year}", T.b1,
                modifier = Modifier.weight(1f), align = TextAlign.Center)
            IconButton({ month = month.plusMonths(1) }) { Icon(Icons.Filled.ChevronRight, "Next month") }
        }
        Row { listOf("M", "T", "W", "T", "F", "S", "S").forEach { Txt(it, T.c1, PT.Grey, Modifier.weight(1f), TextAlign.Center) } }
        val offset = month.atDay(1).dayOfWeek.value - 1
        val len = month.lengthOfMonth()
        for (r in 0 until (offset + len + 6) / 7) Row {
            for (c in 0..6) {
                val day = r * 7 + c - offset + 1
                if (day in 1..len) {
                    val e = month.atDay(day).toEpochDay()
                    val on = selected.contains(e)
                    Box(Modifier.weight(1f).aspectRatio(1f).padding(3.dp).clip(CircleShape)
                        .background(if (on) PT.Blue else Color.Transparent)
                        .clickable { if (on) selected.remove(e) else selected.add(e) }, contentAlignment = Alignment.Center) {
                        Txt("$day", T.c1, if (on) Color.White else PT.Ink)
                    }
                } else Spacer(Modifier.weight(1f))
            }
        }
        Txt("${selected.size} date(s) selected", T.c1, PT.Grey, Modifier.padding(top = 6.dp))
    }
}

/** Fade + slide-up when an item first appears. */
@Composable
fun Modifier.appear(index: Int = 0): Modifier {
    val a = remember { Animatable(0f) }
    LaunchedEffect(Unit) { delay(minOf(index, 8) * 45L); a.animateTo(1f, tween(380)) }
    return this.graphicsLayer { alpha = a.value; translationY = (1f - a.value) * 36f }
}

/**
 * The PillTrack logo mark: a tilted capsule (light blue top-right, light grey bottom-left, as in the original design),
 * drawn with gradients, a seam, gloss highlights and a ground shadow for a 3D look.
 */
@Composable
fun PillLogoMark(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension
        val cx = s / 2f
        val cy = s * 0.47f
        for (i in 0..3) {
            drawOval(Color.Black.copy(alpha = 0.06f), Offset(s * (0.22f + i * 0.02f), s * (0.86f + i * 0.004f)),
                Size(s * (0.56f - i * 0.04f), s * (0.09f - i * 0.012f)))
        }
        rotate(45f, Offset(cx, cy)) {
            val cw = s * 0.38f
            val ch = s * 0.9f
            val left = cx - cw / 2f
            val top = cy - ch / 2f
            val body = Path().apply { addRoundRect(RoundRect(left, top, left + cw, top + ch, CornerRadius(cw / 2f))) }
            clipPath(body) {
                val blue = Brush.horizontalGradient(listOf(Color(0xFFA9CEFB), Color(0xFF6FA8F5), Color(0xFF3B78D8)), left, left + cw)
                val grey = Brush.horizontalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFE3E6EA), Color(0xFF9FA8B5)), left, left + cw)
                drawRect(blue, Offset(left, top), Size(cw, ch / 2f))
                drawRect(grey, Offset(left, cy), Size(cw, ch / 2f))
                drawRect(Color.Black.copy(alpha = 0.18f), Offset(left, cy - s * 0.006f), Size(cw, s * 0.012f))
                drawRoundRect(Color.White.copy(alpha = 0.55f), Offset(left + cw * 0.16f, top + cw * 0.35f),
                    Size(cw * 0.16f, ch * 0.36f), CornerRadius(cw * 0.08f))
                drawRoundRect(Color.White.copy(alpha = 0.7f), Offset(left + cw * 0.16f, cy + cw * 0.25f),
                    Size(cw * 0.16f, ch * 0.28f), CornerRadius(cw * 0.08f))
            }
        }
    }
}

/** Change a dose to Taken / Skipped / Missed, at any time (also days later). */
@Composable
fun EditStatusDialog(s: Slot, h24: Boolean, onSave: (String, Long) -> Unit, onDismiss: () -> Unit) {
    val zone = java.time.ZoneId.systemDefault()
    var status by remember { mutableStateOf(if (s.open) "TAKEN" else s.status) }
    val base = s.dose?.actualAt?.takeIf { it > 0 } ?: s.at
    var takenAt by remember { mutableLongStateOf(base) }
    var pickTime by remember { mutableStateOf(false) }
    val day = java.time.Instant.ofEpochMilli(s.at).atZone(zone).toLocalDate()
    AlertDialog(onDismissRequest = onDismiss, containerColor = PT.Dialog,
        title = { Txt(s.name, T.h2) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Txt("Scheduled ${Fmt.date(day)}, ${Fmt.time(s.at, h24)}", T.b3, PT.Grey)
                Txt("Change status", T.b2)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip("Taken", status == "TAKEN") { status = "TAKEN" }
                    Chip("Skipped", status == "SKIPPED") { status = "SKIPPED" }
                    Chip("Missed", status == "MISSED") { status = "MISSED" }
                }
                if (status == "TAKEN") {
                    Row(Modifier.fillMaxWidth().heightIn(min = 52.dp).clip(RoundedCornerShape(12.dp)).background(PT.Surface)
                        .clickable { pickTime = true }.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Txt("Taken at", T.b3, PT.Grey, Modifier.weight(1f))
                        Txt(Fmt.time(takenAt, h24), T.b2)
                    }
                }
            }
        },
        confirmButton = { TextButton({ onSave(status, takenAt) }) { Text("Save") } },
        dismissButton = { TextButton(onDismiss) { Text("Cancel") } })
    if (pickTime) {
        val t = java.time.Instant.ofEpochMilli(takenAt).atZone(zone)
        TimeDialog("%02d:%02d".format(t.hour, t.minute), h24, { hm ->
            takenAt = day.atTime(LocalTime.parse(hm)).atZone(zone).toInstant().toEpochMilli(); pickTime = false
        }, { pickTime = false })
    }
}

@Composable
fun EditStatusHost(slot: Slot?, repo: Repo, h24: Boolean, onClose: () -> Unit) {
    val scope = rememberCoroutineScope()
    if (slot != null) EditStatusDialog(slot, h24, { st, at ->
        onClose()
        scope.launch {
            val d = slot.dose
            if (d != null) repo.setDoseStatus(d.id, st, at) else slot.med?.let { repo.setSlotStatus(it, slot.at, st, at) }
        }
    }, onClose)
}

@Composable
fun LogoPill() {
    Row(Modifier.clip(RoundedCornerShape(24.dp)).background(PT.Surface).padding(start = 10.dp, end = 16.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically) {
        PillLogoMark(30.dp)
        Spacer(Modifier.width(6.dp))
        Txt("PillTrack", T.b1, PT.Blue)
    }
}

/** Round soft button that springs when pressed. Optional red badge dot. */
@Composable
fun RoundIconButton(icon: ImageVector, desc: String, onClick: () -> Unit, badge: Boolean = false) {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val sc by animateFloatAsState(if (pressed) 0.88f else 1f, spring(dampingRatio = 0.45f, stiffness = 500f), label = "roundBtn")
    Box(Modifier.size(46.dp).graphicsLayer { scaleX = sc; scaleY = sc }.clip(CircleShape).background(PT.Surface)
        .clickable(interactionSource = src, indication = null, onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, desc, tint = PT.Ink)
        if (badge) Box(Modifier.align(Alignment.TopEnd).padding(11.dp).size(9.dp).clip(CircleShape).background(PT.Red))
    }
}

/** Top bar for the main tabs: round menu button, logo pill in the middle, round bell button. Slides down on entry. */
@Composable
fun AppTopBar(onMenu: (() -> Unit)?, onBell: (() -> Unit)?, hPad: Dp = 16.dp, badge: Boolean = false) {
    val enter = remember { Animatable(0f) }
    LaunchedEffect(Unit) { enter.animateTo(1f, tween(450)) }
    Row(Modifier.fillMaxWidth().padding(horizontal = hPad, vertical = 8.dp)
        .graphicsLayer { alpha = enter.value; translationY = (1f - enter.value) * -24f },
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (onMenu != null) RoundIconButton(Icons.Filled.Menu, "Menu", onMenu)
        }
        LogoPill()
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            if (onBell != null) RoundIconButton(Icons.Filled.Notifications, "Notification settings", onBell, badge)
        }
    }
}
