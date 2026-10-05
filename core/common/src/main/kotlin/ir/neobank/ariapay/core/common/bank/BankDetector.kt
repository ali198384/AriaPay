package ir.neobank.ariapay.core.common.bank


import ir.neobank.ariapay.core.common.util.IranianDigits
import ir.neobank.ariapay.core.model.IranianBank

object BankDetector {

    private val bySheba: Map<String, IranianBank> =
        IranianBankCatalog.banks
            .flatMap { bank -> bank.shebaCodes.map { code -> code to bank } }
            .toMap()

    private val byBin: Map<String, IranianBank> =
        IranianBankCatalog.banks
            .flatMap { bank -> bank.bins.map { bin -> bin to bank } }
            .toMap()

    fun findBySheba(raw: String): IranianBank? {
        val normalized = normalizeSheba(raw) ?: return null
        val code = normalized.substring(4, 7)
        return bySheba[code]?.withLegacy(code)
    }

    fun findByCard(raw: String): IranianBank? {
        val normalized = IranianDigits.toEnglish(raw)
        if (normalized.any { it !in '0'..'9' && !it.isWhitespace() && it != '-' }) return null
        val digits = normalized.filter { it in '0'..'9' }
        if (digits.length < 6) return null
        val bin = digits.take(6)
        return byBin[bin]?.withLegacy(bin)
    }

    private fun IranianBank.withLegacy(key: String): IranianBank =
        copy(legacyPersianName = IranianBankCatalog.legacyNames[key])

    private fun normalizeSheba(raw: String): String? {
        val normalized = IranianDigits.toEnglish(raw.filterNot(Char::isWhitespace)).uppercase()
        if (normalized.length != 26 || !normalized.startsWith("IR")) return null
        if (!normalized.drop(2).all { it in '0'..'9' }) return null
        return normalized
    }
}
