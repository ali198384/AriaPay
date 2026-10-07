package ir.neobank.ariapay.core.designsystem.component

import ir.neobank.ariapay.core.common.util.FinancialInputNormalizer


enum class AriaFieldType {
    Text,
    Phone,
    Amount,
    Card,
    Sheba,
}

internal fun sanitizeFieldInput(type: AriaFieldType, raw: String): String {
    if (type == AriaFieldType.Text) return raw

    val digits = FinancialInputNormalizer.digitsOnly(raw)

    return when (type) {
        AriaFieldType.Text -> raw
        AriaFieldType.Phone -> digits.take(11)
        AriaFieldType.Card -> digits.take(16)
        AriaFieldType.Sheba -> digits.take(24)
        AriaFieldType.Amount -> {
            val limited = digits.take(15)
            limited.trimStart('0').ifEmpty {
                if (limited.isNotEmpty()) "0" else ""
            }
        }
    }
}

internal fun toPersianDigits(text: String): String = text.map { ch ->
    if (ch in '0'..'9') '۰' + (ch - '0') else ch
}.joinToString("")

internal fun groupThousands(digits: String, separator: Char = '٬'): String {
    if (digits.isEmpty()) return ""
    return digits
        .reversed()
        .chunked(3)
        .joinToString(separator.toString())
        .reversed()
}

internal fun thousandSeparatorsBefore(length: Int, offset: Int): Int {
    if (length <= 0 || offset <= 0) return 0
    val safeOffset = offset.coerceIn(0, length)
    val total = (length - 1) / 3
    val toTheRight = (length - safeOffset) / 3
    return (total - toTheRight).coerceAtLeast(0)
}

internal fun groupFromStart(
    digits: String,
    groupSizes: List<Int>,
    separator: Char = ' ',
): String {
    if (digits.isEmpty() || groupSizes.isEmpty()) return digits
    val parts = mutableListOf<String>()
    var index = 0
    var sizeIndex = 0
    while (index < digits.length) {
        val size = groupSizes[sizeIndex.coerceAtMost(groupSizes.lastIndex)]
        val end = (index + size).coerceAtMost(digits.length)
        parts += digits.substring(index, end)
        index = end
        sizeIndex++
    }
    return parts.joinToString(separator.toString())
}

internal fun separatorsBeforeFromStart(offset: Int, groupSizes: List<Int>): Int {
    if (offset <= 0 || groupSizes.isEmpty()) return 0
    var remaining = offset
    var separators = 0
    var index = 0
    while (remaining > 0) {
        val size = groupSizes[index.coerceAtMost(groupSizes.lastIndex)]
        if (remaining <= size) break
        remaining -= size
        separators++
        index++
    }
    return separators
}

internal fun formatGroupedDigits(
    digits: String,
    groupSizes: List<Int>,
    persianDigits: Boolean,
): String {
    val grouped = groupFromStart(digits, groupSizes)
    return if (persianDigits) toPersianDigits(grouped) else grouped
}
