package ir.neobank.ariapay.core.model

@JvmInline
value class Money private constructor(val amountInRials: Long) {
    init {
        require(amountInRials >= 0L) { "مبلغ نمی‌تواند منفی باشد" }
    }

    fun toTomans(): Long = amountInRials / 10L

    operator fun plus(other: Money): Money = Money(amountInRials + other.amountInRials)

    operator fun minus(other: Money): Money {
        require(amountInRials >= other.amountInRials) { "موجودی کافی نیست" }
        return Money(amountInRials - other.amountInRials)
    }

    companion object {
        val ZERO = Money(0L)
        fun rials(rial: Long): Money = Money(rial)
        fun tomans(toman: Long): Money = Money(toman * 10L)
    }
}