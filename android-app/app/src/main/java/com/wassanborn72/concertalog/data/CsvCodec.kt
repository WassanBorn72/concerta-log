package com.wassanborn72.concertalog.data

object CsvCodec {
    val headers = listOf(
        "date", "recordTime", "dose", "doseOther", "medTime", "sleep", "sleepQuality", "otherMeds",
        "latency", "food", "overall", "workOutput", "work", "notes", "doctorNote",
        "startEase", "startEffort", "progress", "motivation", "calm", "fog", "focus",
        "anxiety", "irritability", "appetite", "physical", "wearoff"
    )

    fun encode(logs: List<MedicationLog>): String {
        val rows = buildList {
            add(headers)
            logs.sortedBy { it.date }.forEach { log ->
                add(listOf(
                    log.date, log.recordTime, log.dose, log.doseOther, log.medTime,
                    log.sleep?.toString().orEmpty(),
                    log.sleepQuality?.toString().orEmpty(),
                    log.otherMeds?.toString().orEmpty(),
                    log.latency,
                    log.food?.toString().orEmpty(),
                    log.overall?.toString().orEmpty(),
                    log.workOutput?.toString().orEmpty(),
                    log.work, log.notes, log.doctorNote,
                    log.startEase?.toString().orEmpty(),
                    log.startEffort?.toString().orEmpty(),
                    log.progress?.toString().orEmpty(),
                    log.motivation?.toString().orEmpty(),
                    log.calm?.toString().orEmpty(),
                    log.fog?.toString().orEmpty(),
                    log.focus?.toString().orEmpty(),
                    log.anxiety?.toString().orEmpty(),
                    log.irritability?.toString().orEmpty(),
                    log.appetite?.toString().orEmpty(),
                    log.physical?.toString().orEmpty(),
                    log.wearoff?.toString().orEmpty()
                ))
            }
        }
        return "\uFEFF" + rows.joinToString("\r\n") { row ->
            row.joinToString(",") { escape(it) }
        }
    }

    fun decode(csv: String): List<MedicationLog> {
        val rows = parse(csv.removePrefix("\uFEFF"))
        if (rows.isEmpty()) return emptyList()
        val header = rows.first()
        val index = header.withIndex().associate { it.value to it.index }

        fun rowValue(row: List<String>, key: String): String =
            index[key]?.let { row.getOrNull(it) }.orEmpty()

        fun intOrNull(v: String) = v.trim().toIntOrNull()
        fun doubleOrNull(v: String) = v.trim().toDoubleOrNull()

        return rows.drop(1).mapNotNull { row ->
            val date = rowValue(row, "date").trim()
            if (date.isBlank()) return@mapNotNull null

            MedicationLog(
                date = date,
                recordTime = rowValue(row, "recordTime"),
                dose = rowValue(row, "dose"),
                doseOther = rowValue(row, "doseOther"),
                medTime = rowValue(row, "medTime"),
                sleep = doubleOrNull(rowValue(row, "sleep")),
                sleepQuality = intOrNull(rowValue(row, "sleepQuality")),
                otherMeds = intOrNull(rowValue(row, "otherMeds")),
                startEase = intOrNull(rowValue(row, "startEase")),
                startEffort = intOrNull(rowValue(row, "startEffort")),
                progress = intOrNull(rowValue(row, "progress")),
                motivation = intOrNull(rowValue(row, "motivation")),
                calm = intOrNull(rowValue(row, "calm")),
                fog = intOrNull(rowValue(row, "fog")),
                focus = intOrNull(rowValue(row, "focus")),
                anxiety = intOrNull(rowValue(row, "anxiety")),
                irritability = intOrNull(rowValue(row, "irritability")),
                appetite = intOrNull(rowValue(row, "appetite")),
                physical = intOrNull(rowValue(row, "physical")),
                wearoff = intOrNull(rowValue(row, "wearoff")),
                latency = rowValue(row, "latency"),
                food = intOrNull(rowValue(row, "food")),
                overall = intOrNull(rowValue(row, "overall")),
                workOutput = intOrNull(rowValue(row, "workOutput")),
                work = rowValue(row, "work"),
                notes = rowValue(row, "notes"),
                doctorNote = rowValue(row, "doctorNote")
            )
        }
    }

    private fun escape(value: String): String {
        val needsQuotes = value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        val escaped = value.replace("\\\"", "\\\"\\\"")
        return if (needsQuotes) "\\\"$escaped\\\"" else escaped
    }

    private fun parse(text: String): List<List<String>> {
        val rows = mutableListOf<MutableList<String>>()
        var row = mutableListOf<String>()
        val cell = StringBuilder()
        var quoted = false
        var i = 0

        fun pushCell() {
            row.add(cell.toString())
            cell.clear()
        }

        fun pushRow() {
            pushCell()
            rows.add(row)
            row = mutableListOf()
        }

        while (i < text.length) {
            val c = text[i]
            when {
                c == '"' && quoted && i + 1 < text.length && text[i + 1] == '"' -> {
                    cell.append('"')
                    i++
                }
                c == '"' -> quoted = !quoted
                c == ',' && !quoted -> pushCell()
                (c == '\n' || c == '\r') && !quoted -> {
                    if (c == '\r' && i + 1 < text.length && text[i + 1] == '\n') i++
                    pushRow()
                }
                else -> cell.append(c)
            }
            i++
        }

        if (cell.isNotEmpty() || row.isNotEmpty()) pushRow()
        return rows.filterNot { it.size == 1 && it.first().isBlank() }
    }
}
