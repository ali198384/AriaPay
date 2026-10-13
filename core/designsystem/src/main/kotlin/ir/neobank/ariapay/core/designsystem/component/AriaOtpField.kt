package ir.neobank.ariapay.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.neobank.ariapay.core.common.util.FinancialInputNormalizer
import ir.neobank.ariapay.core.designsystem.theme.AriaTheme

private const val OtpLength = 6
private val OtpCellShape = RoundedCornerShape(10.dp)

/**
 * Six editable OTP cells.
 *
 * The parent must update [value] immediately in [onValueChange].
 * Internal empty cells are represented by spaces.
 */
@Composable
fun AriaOtpField(
    value: String,
    onValueChange: (String) -> Unit,
    onComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    val focusRequesters = remember {
        List(OtpLength) { FocusRequester() }
    }

    val latestValue by rememberUpdatedState(value)
    val keyboardController = LocalSoftwareKeyboardController.current
    val density = LocalDensity.current
    val colors = MaterialTheme.colorScheme

    var focusedIndex by remember {
        mutableIntStateOf(-1)
    }

    BoxWithConstraints(
        modifier = modifier.widthIn(max = 420.dp),
        contentAlignment = Alignment.Center,
    ) {
        val gap = 6.dp
        val availableWidth = if (constraints.hasBoundedWidth) {
            maxWidth
        } else {
            420.dp
        }

        val cellSize = (
                (availableWidth - gap * (OtpLength - 1)) / OtpLength
                ).coerceIn(1.dp, 56.dp)

        val digitSize = with(density) {
            (cellSize * 0.48f).toSp()
        }

        val textStyle = MaterialTheme.typography.headlineSmall.copy(
            fontSize = digitSize,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            textDirection = TextDirection.Ltr,
            fontFeatureSettings = "tnum",
            color = if (enabled) {
                colors.onSurface
            } else {
                colors.onSurface.copy(alpha = 0.42f)
            },
        )

        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Ltr,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                repeat(OtpLength) { index ->
                    val currentDigit = otpDigitAt(value, index)

                    var fieldValue by remember(index) {
                        mutableStateOf(
                            TextFieldValue(
                                text = currentDigit,
                                selection = TextRange(currentDigit.length),
                            ),
                        )
                    }

                    LaunchedEffect(currentDigit) {
                        if (fieldValue.text != currentDigit) {
                            fieldValue = TextFieldValue(
                                text = currentDigit,
                                selection = TextRange(currentDigit.length),
                            )
                        }
                    }

                    val isFocused = enabled && focusedIndex == index

                    val borderColor = when {
                        isError -> colors.error
                        isFocused -> colors.primary
                        else -> colors.outlineVariant
                    }

                    val containerColor = when {
                        !enabled -> colors.surfaceContainerLow
                        isError -> colors.errorContainer.copy(alpha = 0.24f)
                        isFocused -> colors.primaryContainer.copy(alpha = 0.38f)
                        else -> colors.surfaceContainerLow
                    }

                    BasicTextField(
                        value = fieldValue,
                        onValueChange = { editedValue ->
                            val previousFieldValue = fieldValue

                            /*
                             * A tap or long press can change only selection.
                             * Preserve it without updating the OTP or moving focus.
                             */
                            if (editedValue.text == previousFieldValue.text) {
                                fieldValue = editedValue
                                return@BasicTextField
                            }

                            val edit = calculateOtpEdit(
                                currentValue = latestValue,
                                index = index,
                                previous = previousFieldValue,
                                edited = editedValue,
                            ) ?: return@BasicTextField

                            val nextCellDigit = otpDigitAt(edit.value, index)

                            fieldValue = TextFieldValue(
                                text = nextCellDigit,
                                selection = TextRange(nextCellDigit.length),
                            )

                            if (edit.value != latestValue) {
                                onValueChange(edit.value)

                                val completeCode =
                                    FinancialInputNormalizer.normalizeOtpCode(
                                        edit.value,
                                    )

                                if (completeCode != null) {
                                    keyboardController?.hide()
                                    onComplete(completeCode)
                                    return@BasicTextField
                                }
                            }

                            edit.nextFocusIndex?.let { nextIndex ->
                                focusRequesters[nextIndex].requestFocus()
                            }
                        },
                        modifier = Modifier
                            .size(cellSize)
                            .background(
                                color = containerColor,
                                shape = OtpCellShape,
                            )
                            .border(
                                width = if (isFocused || isError) {
                                    1.5.dp
                                } else {
                                    1.dp
                                },
                                color = borderColor,
                                shape = OtpCellShape,
                            )
                            .focusRequester(focusRequesters[index])
                            .onFocusChanged { state ->
                                if (state.isFocused) {
                                    focusedIndex = index

                                    if (fieldValue.text.isNotEmpty()) {
                                        fieldValue = fieldValue.copy(
                                            selection = TextRange(
                                                start = 0,
                                                end = fieldValue.text.length,
                                            ),
                                        )
                                    }
                                } else if (focusedIndex == index) {
                                    focusedIndex = -1
                                }
                            }
                            .semantics {
                                contentDescription = "رقم ${index + 1} از ۶"

                                if (index == 0) {
                                    contentType = ContentType.SmsOtpCode
                                }

                                if (isError) {
                                    error(
                                        errorMessage
                                            ?: "کد تأیید واردشده معتبر نیست",
                                    )
                                }
                            },
                        enabled = enabled,
                        singleLine = true,
                        textStyle = textStyle,
                        cursorBrush = SolidColor(
                            if (isError) colors.error else colors.primary,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = if (index == OtpLength - 1) {
                                ImeAction.Done
                            } else {
                                ImeAction.Next
                            },
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (index < OtpLength - 1) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            },
                            onDone = {
                                // Completion is handled when the OTP changes.
                                if (
                                    FinancialInputNormalizer.normalizeOtpCode(
                                        latestValue,
                                    ) != null
                                ) {
                                    keyboardController?.hide()
                                }
                            },
                        ),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                innerTextField()
                            }
                        },
                        visualTransformation = OtpPersianDigitTransformation,
                    )
                }
            }
        }
    }
}

private data class OtpEditResult(
    val value: String,
    val nextFocusIndex: Int?,
)

private fun otpDigitAt(
    value: String,
    index: Int,
): String {
    val character = value.getOrNull(index) ?: return ""

    return FinancialInputNormalizer
        .digitsOnly(character.toString())
        .take(1)
}

/**
 * Extracts the inserted text using the previous selection when possible.
 * Falls back to comparing the old and new text for IME edits.
 */
private fun insertedOtpText(
    previous: TextFieldValue,
    edited: TextFieldValue,
): String {
    val selectionStart = previous.selection.min
        .coerceIn(0, previous.text.length)

    val selectionEnd = previous.selection.max
        .coerceIn(selectionStart, previous.text.length)

    val prefix = previous.text.substring(0, selectionStart)
    val suffix = previous.text.substring(selectionEnd)

    if (
        edited.text.length >= prefix.length + suffix.length &&
        edited.text.startsWith(prefix) &&
        edited.text.endsWith(suffix)
    ) {
        return edited.text.substring(
            startIndex = prefix.length,
            endIndex = edited.text.length - suffix.length,
        )
    }

    var commonPrefixLength = 0

    while (
        commonPrefixLength < previous.text.length &&
        commonPrefixLength < edited.text.length &&
        previous.text[commonPrefixLength] ==
        edited.text[commonPrefixLength]
    ) {
        commonPrefixLength++
    }

    var commonSuffixLength = 0

    while (
        commonSuffixLength < previous.text.length - commonPrefixLength &&
        commonSuffixLength < edited.text.length - commonPrefixLength &&
        previous.text[previous.text.lastIndex - commonSuffixLength] ==
        edited.text[edited.text.lastIndex - commonSuffixLength]
    ) {
        commonSuffixLength++
    }

    return edited.text.substring(
        startIndex = commonPrefixLength,
        endIndex = edited.text.length - commonSuffixLength,
    )
}

private fun calculateOtpEdit(
    currentValue: String,
    index: Int,
    previous: TextFieldValue,
    edited: TextFieldValue,
): OtpEditResult? {
    val slots = CharArray(OtpLength) { position ->
        otpDigitAt(currentValue, position).firstOrNull() ?: ' '
    }

    // Deleting a cell preserves the positions of the other digits.
    if (edited.text.isEmpty()) {
        slots[index] = ' '

        return OtpEditResult(
            value = slots.concatToString().trimEnd(),
            nextFocusIndex = null,
        )
    }

    val insertedText = insertedOtpText(
        previous = previous,
        edited = edited,
    )

    val insertedDigits =
        FinancialInputNormalizer.digitsOnly(insertedText)

    // Reject nonnumeric input without clearing the current cell.
    if (insertedDigits.isEmpty()) {
        return null
    }

    // A complete pasted OTP replaces the entire code from the first cell.
    if (insertedDigits.length >= OtpLength) {
        return OtpEditResult(
            value = insertedDigits.take(OtpLength),
            nextFocusIndex = null,
        )
    }

    val digitsToInsert = insertedDigits.take(OtpLength - index)

    digitsToInsert.forEachIndexed { offset, digit ->
        slots[index + offset] = digit
    }

    val nextIndex = index + digitsToInsert.length

    return OtpEditResult(
        value = slots.concatToString().trimEnd(),
        nextFocusIndex = nextIndex.takeIf { it < OtpLength },
    )
}

private object OtpPersianDigitTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformed = buildString(text.length) {
            text.text.forEach { character ->
                append(
                    when (character) {
                        in '0'..'9' -> {
                            (
                                    '۰'.code + character.code - '0'.code
                                    ).toChar()
                        }

                        else -> character
                    },
                )
            }
        }

        return TransformedText(
            text = AnnotatedString(transformed),
            offsetMapping = OffsetMapping.Identity,
        )
    }
}

@Preview(
    showBackground = true,
    locale = "fa",
    widthDp = 320,
)
@Composable
private fun AriaOtpFieldPreview() {
    var code by remember {
        mutableStateOf("1234")
    }

    AriaTheme {
        Column(
            modifier = Modifier.padding(24.dp),
        ) {
            AriaOtpField(
                value = code,
                onValueChange = { code = it },
                onComplete = {},
            )

            AriaOtpField(
                value = "123456",
                onValueChange = {},
                onComplete = {},
                modifier = Modifier.padding(top = 20.dp),
                isError = true,
                errorMessage = "کد تأیید نادرست است",
            )

            AriaOtpField(
                value = "123456",
                onValueChange = {},
                onComplete = {},
                modifier = Modifier.padding(top = 20.dp),
                enabled = false,
            )
        }
    }
}
