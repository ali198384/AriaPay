package ir.neobank.ariapay.core.model


data class IranianBank(
    val id: String,
    val persianName: String,
    val englishName: String,
    val shebaCodes: Set<String>,
    val bins: Set<String>,
    val legacyPersianName: String? = null,
)
