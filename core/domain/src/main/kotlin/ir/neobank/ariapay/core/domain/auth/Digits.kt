package ir.neobank.ariapay.core.domain.auth

import ir.neobank.ariapay.core.common.util.IranianDigits

internal fun String.toEnglishDigits(): String = IranianDigits.toEnglish(this)
