package com.wassanborn72.concertalog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wassanborn72.concertalog.data.CsvCodec
import com.wassanborn72.concertalog.data.MedicationLog
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ConcertaLogApp() }
    }
}

private enum class Tab(val label: String, val mark: String) {
    Record("記録", "✎"),
    Trends("推移", "↗"),
    History("履歴", "≡")
}

private data class LogForm(
    val date: String = LocalDate.now().toString(),
    val recordTime: String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
    val dose: String = "",
    val doseOther: String = "",
    val medTime: String = "",
    val sleep: String = "",
    val sleepQuality: Int? = null,
    val otherMeds: Int? = null,
    val startEase: Int? = null,
    val startEffort: Int? = null,
    val progress: Int? = null,
    val motivation: Int? = null,
    val calm: Int? = null,
    val fog: Int? = null,
    val focus: Int? = null,
    val anxiety: Int? = null,
    val irritability: Int? = null,
    val appetite: Int? = null,
    val physical: Int? = null,
    val wearoff: Int? = null,
    val latency: String = "",
    val food: Int? = null,
    val overall: Int? = null,
    val workOutput: Int? = null,
    val work: String = "",
    val notes: String = "",
    val doctorNote: String = ""
) {
    fun toEntity() = MedicationLog(
        date = date,
        recordTime = recordTime,
        dose = dose,
        doseOther = doseOther,
        medTime = medTime,
        sleep = sleep.toDoubleOrNull(),
        sleepQuality = sleepQuality,
        otherMeds = otherMeds,
        startEase = startEase,
        startEffort = startEffort,
        progress = progress,
        motivation = motivation,
        calm = calm,
        fog = fog,
        focus = focus,
        anxiety = anxiety,
        irritability = irritability,
        appetite = appetite,
        physical = physical,
        wearoff = wearoff,
        latency = latency,
        food = food,
        overall = overall,
        workOutput = workOutput,
        work = work,
        notes = notes,
        doctorNote = doctorNote
    )

    companion object {
        fun from(log: MedicationLog) = LogForm(
            date = log.date,
            recordTime = log.recordTime,
            dose = log.dose,
            doseOther = log.doseOther,
            medTime = log.medTime,
            sleep = log.sleep?.toString().orEmpty(),
            sleepQuality = log.sleepQuality,
            otherMeds = log.otherMeds,
            startEase = log.startEase,
            startEffort = log.startEffort,
            progress = log.progress,
            motivation = log.motivation,
            calm = log.calm,
            fog = log.fog,
            focus = log.focus,
            anxiety = log.anxiety,
            irritability = log.irritability,
            appetite = log.appetite,
            physical = log.physical,
            wearoff = log.wearoff,
            latency = log.latency,
            food = log.food,
            overall = log.overall,
            workOutput = log.workOutput,
            work = log.work,
            notes = log.notes,
            doctorNote = log.doctorNote
        )
    }
}

private val appColors = lightColorScheme(
    primary = Color(0xFF28536B),
    secondary = Color(0xFF2D7D6B),
    background = Color(0xFFF4F7F8),
    surface = Color.White,
    error = Color(0xFF9C3E2E)
)

@Composable
private fun ConcertaLogApp(vm: LogViewModel = viewModel()) {
    val context = LocalContext.current
    val logs by vm.logs.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf(Tab.Record) }
    var editing by remember { mutableStateOf<MedicationLog?>(null) }
    var status by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter(Charsets.UTF_8).use { writer ->
                    requireNotNull(writer)
                    writer.write(CsvCodec.encode(logs))
                }
            }.onSuccess { status = "CSVを書き出しました。" }
                .onFailure { status = "CSVの書き出しに失敗しました。" }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8).use { reader ->
                    CsvCodec.decode(requireNotNull(reader).readText())
                }
            }.onSuccess {
                vm.import(it)
                status = "${it.size}件の記録を読み込みました。"
            }.onFailure {
                status = "CSVを読み込めませんでした。形式を確認してください。"
            }
        }
    }

    MaterialTheme(colorScheme = appColors) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Surface(shadowElevation = 2.dp) {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp)) {
                        Text("コンサータ服薬ログ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("端末内に保存するオフライン服薬記録", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            bottomBar = {
                NavigationBar {
                    Tab.entries.forEach { item ->
                        NavigationBarItem(
                            selected = tab == item,
                            onClick = { tab = item },
                            icon = { Text(item.mark) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (tab) {
                    Tab.Record -> RecordScreen(
                        editing = editing,
                        onSaved = {
                            vm.save(it)
                            editing = null
                            status = "${it.date} の記録を保存しました。"
                        },
                        onCancelEdit = { editing = null }
                    )
                    Tab.Trends -> TrendsScreen(logs)
                    Tab.History -> HistoryScreen(
                        logs = logs,
                        status = status,
                        onEdit = {
                            editing = it
                            tab = Tab.Record
                        },
                        onExport = {
                            exportLauncher.launch("コンサータ服薬ログ_${LocalDate.now()}.csv")
                        },
                        onImport = {
                            importLauncher.launch(arrayOf("text/csv", "text/*"))
                        },
                        onDeleteAll = {
                            vm.deleteAll()
                            status = "全記録を削除しました。"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordScreen(
    editing: MedicationLog?,
    onSaved: (MedicationLog) -> Unit,
    onCancelEdit: () -> Unit
) {
    val context = LocalContext.current
    var form by remember { mutableStateOf(LogForm()) }
    var savedMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(editing) {
        form = editing?.let(LogForm::from) ?: LogForm()
        savedMessage = null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionCard(if (editing == null) "今日の記録" else "記録を編集") {
                DateButton(
                    label = "記録日",
                    value = form.date,
                    onClick = { showDatePicker(context, form.date) { form = form.copy(date = it) } }
                )
                TimeButton(
                    label = "記録時刻",
                    value = form.recordTime,
                    onClick = { showTimePicker(context, form.recordTime) { form = form.copy(recordTime = it) } }
                )
                Text("コンサータ用量（mg）", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("18", "27", "36", "54", "other").forEach { dose ->
                        FilterChip(
                            selected = form.dose == dose,
                            onClick = { form = form.copy(dose = dose) },
                            label = { Text(if (dose == "other") "その他" else dose) }
                        )
                    }
                }
                if (form.dose == "other") {
                    OutlinedTextField(
                        value = form.doseOther,
                        onValueChange = { form = form.copy(doseOther = it) },
                        label = { Text("その他の用量") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                TimeButton(
                    label = "服薬時刻",
                    value = form.medTime.ifBlank { "未記録" },
                    onClick = { showTimePicker(context, form.medTime) { form = form.copy(medTime = it) } }
                )
                OutlinedTextField(
                    value = form.sleep,
                    onValueChange = { form = form.copy(sleep = it) },
                    label = { Text("睡眠時間（時間）") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                RatingField("睡眠の質", "0 とても悪い / 4 とても良い", form.sleepQuality) {
                    form = form.copy(sleepQuality = it)
                }
                BinaryField("他の薬の変更・飲み忘れ", form.otherMeds) {
                    form = form.copy(otherMeds = it)
                }
            }
        }

        item { RatingCard("着手のしやすさ", "0 難しい / 4 とても容易", form.startEase) { form = form.copy(startEase = it) } }
        item { RatingCard("着手に必要な努力感", "0 少ない / 4 とても大きい", form.startEffort) { form = form.copy(startEffort = it) } }
        item { RatingCard("作業の進みやすさ", "0 進まない / 4 とても進む", form.progress) { form = form.copy(progress = it) } }
        item { RatingCard("意欲", "0 低い / 4 高い", form.motivation) { form = form.copy(motivation = it) } }
        item { RatingCard("頭・気持ちの落ち着き", "0 落ち着かない / 4 落ち着いている", form.calm) { form = form.copy(calm = it) } }
        item { RatingCard("ぼんやり感", "0 なし / 4 とても強い", form.fog) { form = form.copy(fog = it) } }
        item { RatingCard("集中の保ちやすさ", "0 難しい / 4 とても保ちやすい", form.focus) { form = form.copy(focus = it) } }
        item { RatingCard("不安・緊張", "0 なし / 4 とても強い", form.anxiety) { form = form.copy(anxiety = it) } }
        item { RatingCard("いらだち", "0 なし / 4 とても強い", form.irritability) { form = form.copy(irritability = it) } }
        item { RatingCard("食欲低下", "0 なし / 4 とても強い", form.appetite) { form = form.copy(appetite = it) } }
        item { RatingCard("身体の副作用（動悸など）", "0 なし / 4 とても強い", form.physical) { form = form.copy(physical = it) } }
        item { RatingCard("効果が切れる時の不調", "0 なし / 4 とても強い", form.wearoff) { form = form.copy(wearoff = it) } }

        item {
            SectionCard("その日の実績・メモ") {
                SelectMenu(
                    label = "着手までの時間",
                    value = form.latency,
                    options = listOf("0〜5分", "6〜15分", "16〜30分", "31分以上", "着手できず")
                ) { form = form.copy(latency = it) }

                RatingField("食事・水分", "0 いつもどおり / 1 少なめ / 2 かなり少ない", form.food, 0..2) {
                    form = form.copy(food = it)
                }
                RatingField("全体的な評価", "0 悪化 / 2 変化なし / 4 明確に改善", form.overall) {
                    form = form.copy(overall = it)
                }
                RatingField("作業実績", "0 できず / 4 予定どおり以上", form.workOutput) {
                    form = form.copy(workOutput = it)
                }

                OutlinedTextField(
                    value = form.work,
                    onValueChange = { form = form.copy(work = it) },
                    label = { Text("取り組んだ作業") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = form.notes,
                    onValueChange = { form = form.copy(notes = it) },
                    label = { Text("今日いつもと違ったこと") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = form.doctorNote,
                    onValueChange = { form = form.copy(doctorNote = it) },
                    label = { Text("医師に伝えたいこと") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Button(
                    onClick = {
                        if (form.date.isNotBlank()) {
                            onSaved(form.toEntity())
                            savedMessage = "端末内に保存しました。"
                            form = LogForm()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (editing == null) "記録を保存" else "変更を保存")
                }

                if (editing != null) {
                    OutlinedButton(
                        onClick = {
                            onCancelEdit()
                            form = LogForm()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("編集をやめる")
                    }
                }

                savedMessage?.let { Text(it, color = MaterialTheme.colorScheme.secondary) }
            }
        }

        item {
            Surface(
                color = Color(0xFFFFF8E8),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "このアプリは記録・振り返り用で、診断や薬の効果判定を行うものではありません。服用方法・用量は処方に従ってください。",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TrendsScreen(logs: List<MedicationLog>) {
    var days by remember { mutableIntStateOf(7) }
    var metricA by remember { mutableStateOf(metrics[0]) }
    var metricB by remember { mutableStateOf<Metric?>(metrics[1]) }

    val cutoff = LocalDate.now().minusDays((days - 1).toLong())
    val inRange = logs.filter {
        runCatching { LocalDate.parse(it.date) }.getOrNull()?.let { d -> !d.isBefore(cutoff) } == true
    }.sortedBy { it.date }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionCard("日ごとの推移") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = days == 7, onClick = { days = 7 }, label = { Text("7日間") })
                    FilterChip(selected = days == 30, onClick = { days = 30 }, label = { Text("30日間") })
                }
                MetricMenu("グラフ項目1", metricA, metrics) { metricA = it }
                OptionalMetricMenu("比較項目2", metricB, metrics) { metricB = it }

                TrendChart(
                    logs = inRange,
                    days = days,
                    metricA = metricA,
                    metricB = metricB
                )
                Text(
                    "未記録の日は線でつなぎません。項目によって「高いほど良い／負担が強い」の意味が異なります。",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            SectionCard("期間内の記録") {
                SummaryRow("記録日数", "${inRange.size} / ${days}日")
                SummaryRow("着手のしやすさ平均", average(inRange) { it.startEase })
                SummaryRow("着手の努力感平均", average(inRange) { it.startEffort })
                SummaryRow("ぼんやり感平均", average(inRange) { it.fog })
            }
        }
    }
}

private data class Metric(
    val key: String,
    val label: String,
    val value: (MedicationLog) -> Int?
)

private val metrics = listOf(
    Metric("startEase", "着手のしやすさ") { it.startEase },
    Metric("startEffort", "着手に必要な努力感") { it.startEffort },
    Metric("progress", "作業の進みやすさ") { it.progress },
    Metric("motivation", "意欲") { it.motivation },
    Metric("calm", "頭・気持ちの落ち着き") { it.calm },
    Metric("fog", "ぼんやり感") { it.fog },
    Metric("focus", "集中の保ちやすさ") { it.focus },
    Metric("anxiety", "不安・緊張") { it.anxiety },
    Metric("irritability", "いらだち") { it.irritability },
    Metric("appetite", "食欲低下") { it.appetite },
    Metric("physical", "身体の副作用") { it.physical },
    Metric("wearoff", "効果が切れる時の不調") { it.wearoff },
    Metric("overall", "全体的な評価") { it.overall },
    Metric("workOutput", "作業実績") { it.workOutput },
    Metric("sleepQuality", "睡眠の質") { it.sleepQuality }
)

@Composable
private fun TrendChart(
    logs: List<MedicationLog>,
    days: Int,
    metricA: Metric,
    metricB: Metric?
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val start = LocalDate.now().minusDays((days - 1).toLong())
    val byDate = logs.associateBy { it.date }

    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            LegendDot(primary)
            Text(metricA.label, style = MaterialTheme.typography.bodySmall)
            if (metricB != null) {
                LegendDot(secondary)
                Text(metricB.label, style = MaterialTheme.typography.bodySmall)
            }
        }
        Canvas(
            Modifier.fillMaxWidth().height(230.dp).padding(top = 12.dp)
        ) {
            val left = 10.dp.toPx()
            val right = 10.dp.toPx()
            val top = 8.dp.toPx()
            val bottom = 18.dp.toPx()
            val usableW = size.width - left - right
            val usableH = size.height - top - bottom

            for (v in 0..4) {
                val y = top + usableH * (4 - v) / 4f
                drawLine(Color(0xFFDCE5E9), Offset(left, y), Offset(size.width - right, y), strokeWidth = 1.dp.toPx())
            }

            fun drawMetric(metric: Metric, color: Color) {
                var previous: Offset? = null
                var previousIndex = -2
                for (i in 0 until days) {
                    val date = start.plusDays(i.toLong()).toString()
                    val value = byDate[date]?.let(metric.value)
                    if (value == null) {
                        previous = null
                        previousIndex = -2
                        continue
                    }
                    val x = if (days == 1) size.width / 2 else left + usableW * i / (days - 1).toFloat()
                    val y = top + usableH * (4 - value.coerceIn(0, 4)) / 4f
                    val point = Offset(x, y)
                    if (previous != null && previousIndex == i - 1) {
                        drawLine(color, previous!!, point, strokeWidth = 2.5.dp.toPx())
                    }
                    drawCircle(color, radius = 3.5.dp.toPx(), center = point)
                    previous = point
                    previousIndex = i
                }
            }

            drawMetric(metricA, primary)
            metricB?.let { drawMetric(it, secondary) }
        }
    }
}

@Composable
private fun LegendDot(color: Color) {
    Canvas(Modifier.size(10.dp)) { drawCircle(color) }
}

@Composable
private fun HistoryScreen(
    logs: List<MedicationLog>,
    status: String?,
    onEdit: (MedicationLog) -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onDeleteAll: () -> Unit
) {
    var confirmDelete by remember { mutableStateOf(false) }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("全記録を削除") },
            text = { Text("この操作は取り消せません。必要なら先にCSVを書き出してください。") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    onDeleteAll()
                }) { Text("削除") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("キャンセル") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SectionCard("履歴・バックアップ") {
                Text(
                    "データベースはアプリ専用領域に保存されます。端末故障やアンインストールに備えて、必要に応じてCSVを書き出してください。",
                    style = MaterialTheme.typography.bodySmall
                )
                Button(onClick = onExport, modifier = Modifier.fillMaxWidth(), enabled = logs.isNotEmpty()) {
                    Text("CSVを書き出す")
                }
                OutlinedButton(onClick = onImport, modifier = Modifier.fillMaxWidth()) {
                    Text("CSVを読み込む")
                }
                OutlinedButton(
                    onClick = { confirmDelete = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = logs.isNotEmpty()
                ) {
                    Text("全記録を削除")
                }
                status?.let { Text(it, color = MaterialTheme.colorScheme.secondary) }
            }
        }

        if (logs.isEmpty()) {
            item { Text("まだ記録がありません。", modifier = Modifier.padding(12.dp)) }
        } else {
            items(logs, key = { it.date }) { log ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(log.date, fontWeight = FontWeight.Bold)
                                val dose = if (log.dose == "other") log.doseOther.ifBlank { "その他" } else log.dose
                                Text(
                                    "服薬 ${log.medTime.ifBlank { "未記録" }} ・ ${if (dose.isBlank()) "用量未記録" else "${dose} mg"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            OutlinedButton(onClick = { onEdit(log) }) { Text("編集") }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ChipText("着手", log.startEase)
                            ChipText("努力感", log.startEffort)
                            ChipText("進み", log.progress)
                            ChipText("意欲", log.motivation)
                            ChipText("ぼんやり", log.fog)
                        }

                        if (log.work.isNotBlank()) Text("作業：${log.work}", style = MaterialTheme.typography.bodySmall)
                        if (log.notes.isNotBlank()) Text(log.notes)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipText(label: String, value: Int?) {
    if (value != null) {
        AssistChip(onClick = {}, label = { Text("$label $value") })
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun RatingCard(label: String, hint: String, value: Int?, onChange: (Int?) -> Unit) {
    SectionCard(label) {
        RatingField(null, hint, value, onChange = onChange)
    }
}

@Composable
private fun RatingField(
    label: String?,
    hint: String,
    value: Int?,
    range: IntRange = 0..4,
    onChange: (Int?) -> Unit
) {
    if (label != null) Text(label, fontWeight = FontWeight.SemiBold)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        range.forEach { n ->
            FilterChip(
                selected = value == n,
                onClick = { onChange(if (value == n) null else n) },
                label = { Text(n.toString()) },
                modifier = Modifier.weight(1f)
            )
        }
    }
    Text(hint, style = MaterialTheme.typography.bodySmall, color = Color(0xFF61727D))
}

@Composable
private fun BinaryField(label: String, value: Int?, onChange: (Int?) -> Unit) {
    Text(label, fontWeight = FontWeight.SemiBold)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = value == 0, onClick = { onChange(if (value == 0) null else 0) }, label = { Text("なし") })
        FilterChip(selected = value == 1, onClick = { onChange(if (value == 1) null else 1) }, label = { Text("あり") })
    }
}

@Composable
private fun DateButton(label: String, value: String, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(value) }
    }
}

@Composable
private fun TimeButton(label: String, value: String, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(value) }
    }
}

@Composable
private fun SelectMenu(
    label: String,
    value: String,
    options: List<String>,
    onChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(value.ifBlank { "未記録" })
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("未記録") },
                    onClick = {
                        onChange("")
                        expanded = false
                    }
                )
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricMenu(
    label: String,
    value: Metric,
    options: List<Metric>,
    onChange: (Metric) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(value.label)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            onChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionalMetricMenu(
    label: String,
    value: Metric?,
    options: List<Metric>,
    onChange: (Metric?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(value?.label ?: "比較項目なし")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("比較項目なし") },
                    onClick = {
                        onChange(null)
                        expanded = false
                    }
                )
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            onChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

private fun average(logs: List<MedicationLog>, getter: (MedicationLog) -> Int?): String {
    val values = logs.mapNotNull(getter)
    return if (values.isEmpty()) "—" else "%.1f".format(values.average())
}

private fun showDatePicker(
    context: android.content.Context,
    initial: String,
    onSelected: (String) -> Unit
) {
    val date = runCatching { LocalDate.parse(initial) }.getOrDefault(LocalDate.now())
    DatePickerDialog(
        context,
        { _, year, month, day ->
            onSelected(LocalDate.of(year, month + 1, day).toString())
        },
        date.year,
        date.monthValue - 1,
        date.dayOfMonth
    ).show()
}

private fun showTimePicker(
    context: android.content.Context,
    initial: String,
    onSelected: (String) -> Unit
) {
    val time = runCatching {
        if (initial.isBlank()) LocalTime.now() else LocalTime.parse(initial)
    }.getOrDefault(LocalTime.now())

    TimePickerDialog(
        context,
        { _, hour, minute ->
            onSelected(String.format("%02d:%02d", hour, minute))
        },
        time.hour,
        time.minute,
        true
    ).show()
}
