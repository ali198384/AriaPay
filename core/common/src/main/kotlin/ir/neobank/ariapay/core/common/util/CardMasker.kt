package ir.neobank.ariapay.core.common.util

object CardMasker {
    fun mask(raw: String): String {
        val d = raw.filter { it.isDigit() }
        require(d.length == 16) { "PAN must be 16 digits" }
        return "${d.take(4)}-${d.substring(4, 6)}**-****-${d.takeLast(4)}"
    }
}