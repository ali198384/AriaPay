package ir.neobank.ariapay.core.designsystem.component


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.neobank.ariapay.core.designsystem.theme.AriaTheme

@Composable
fun AriaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    type: AriaFieldType = AriaFieldType.Text,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val transformation = remember(type) { type.transformation() }
    /*val textStyle = if (type == AriaFieldType.Amount) {
        MaterialTheme.typography.titleLarge
    } else {
        MaterialTheme.typography.bodyLarge
    }.copy(
        fontFeatureSettings = "tnum",
        textDirection = type.textDirection(),
        color = MaterialTheme.colorScheme.onSurface,
    )*/


    val textStyle = if (type == AriaFieldType.Amount) {
        MaterialTheme.typography.titleLarge
    } else {
        MaterialTheme.typography.bodyLarge
    }.copy(
        fontFeatureSettings = "tnum",
        textDirection = type.textDirection(),
        textAlign = type.textAlign(),
        color = MaterialTheme.colorScheme.onSurface,
    )


    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(sanitizeFieldInput(type, it)) },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = { Text(label) },
        placeholder = placeholder?.let { hint -> { Text(hint) } },
        supportingText = supportingText?.let { message -> { Text(message) } },
        isError = isError,
        singleLine = true,
        visualTransformation = transformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = type.keyboardType(),
            imeAction = imeAction,
        ),
        keyboardActions = keyboardActions,
        suffix = if (type == AriaFieldType.Amount) {
            { Text("ریال", style = MaterialTheme.typography.labelLarge) }
        } else {
            null
        },
        shape = MaterialTheme.shapes.small,
    )
}

private fun AriaFieldType.keyboardType(): KeyboardType = when (this) {
    AriaFieldType.Text -> KeyboardType.Text
    AriaFieldType.Phone -> KeyboardType.Phone
    AriaFieldType.Amount,
    AriaFieldType.Card,
    AriaFieldType.Sheba,
        -> KeyboardType.Number
}

private fun AriaFieldType.textDirection(): TextDirection = when (this) {
    AriaFieldType.Phone, AriaFieldType.Card, AriaFieldType.Sheba -> TextDirection.Ltr
    else -> TextDirection.Content
}

private fun AriaFieldType.textAlign(): TextAlign = when (this) {
    AriaFieldType.Phone,
    AriaFieldType.Card,
    AriaFieldType.Sheba,
        -> TextAlign.Right
    else -> TextAlign.Start
}

private fun AriaFieldType.transformation(): VisualTransformation = when (this) {
    AriaFieldType.Amount -> AmountVisualTransformation()
    AriaFieldType.Phone -> GroupedDigitsVisualTransformation(listOf(4, 3, 4), persianDigits = true)
    AriaFieldType.Card -> GroupedDigitsVisualTransformation(listOf(4, 4, 4, 4), persianDigits = true)
    AriaFieldType.Sheba -> ShebaVisualTransformation()
    AriaFieldType.Text -> VisualTransformation.None
}

internal class AmountVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) return TransformedText(AnnotatedString(""), OffsetMapping.Identity)

        val formatted = toPersianDigits(groupThousands(raw))
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, raw.length)
                return (clamped + thousandSeparatorsBefore(raw.length, clamped))
                    .coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, formatted.length)
                val separators = formatted.take(clamped).count { it == '٬' }
                return (clamped - separators).coerceIn(0, raw.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}

internal class ShebaVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val body = groupFromStart(raw, listOf(4, 4, 4, 4, 4, 4))
        val formatted = if (body.isEmpty()) "IR" else "IR $body"
        val prefixLength = if (body.isEmpty()) 2 else 3

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, raw.length)
                val separators = separatorsBeforeFromStart(clamped, listOf(4, 4, 4, 4, 4, 4))
                return (prefixLength + clamped + separators).coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, formatted.length)
                if (clamped <= prefixLength) return 0
                val inBody = clamped - prefixLength
                val separators = body.take(inBody).count { it == ' ' }
                return (inBody - separators).coerceIn(0, raw.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}


internal class GroupedDigitsVisualTransformation(
    private val groupSizes: List<Int>,
    private val persianDigits: Boolean,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) return TransformedText(AnnotatedString(""), OffsetMapping.Identity)

        val formatted = formatGroupedDigits(raw, groupSizes, persianDigits)
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, raw.length)
                return (clamped + separatorsBeforeFromStart(clamped, groupSizes))
                    .coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, formatted.length)
                val separators = formatted.take(clamped).count { it == ' ' }
                return (clamped - separators).coerceIn(0, raw.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}

@Preview(showBackground = true, locale = "fa")
@Composable
private fun AriaTextFieldPreview() {
    var phone by remember { mutableStateOf("09123456789") }
    var amount by remember { mutableStateOf("12500000") }
    var card by remember { mutableStateOf("5022291508680350") }
    var sheba by remember { mutableStateOf("120170000000123456789012") }

    AriaTheme {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AriaTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "شماره موبایل",
                type = AriaFieldType.Phone,
                placeholder = "۰۹۱۲۳۴۵۶۷۸۹",
            )
            AriaTextField(
                value = amount,
                onValueChange = { amount = it },
                label = "مبلغ",
                type = AriaFieldType.Amount,
            )
            AriaTextField(
                value = "",
                onValueChange = {},
                label = "شماره کارت",
                type = AriaFieldType.Card,
                isError = true,
                supportingText = "شماره کارت نامعتبر است",
            )
            AriaTextField(
                value = card,
                onValueChange = { sheba = it },
                label = "شماره کارت",
                type = AriaFieldType.Card,
            )
            AriaTextField(
                value = sheba,
                onValueChange = { sheba = it },
                label = "شبا",
                type = AriaFieldType.Sheba,
            )
        }
    }
}
