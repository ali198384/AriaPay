package ir.neobank.ariapay.core.common.validation


object LuhnValidator {
    /**
     * اعتبارسنجی شماره کارت ۱۶ رقمی براساس الگوریتم لاین (Luhn Algorithm)
     * طبق استاندارد بانکی شتاب، ضرب یک‌درمیان در ۲ انجام می‌شود.
     */
    fun isValid(cardNumber: String): Boolean {
        val sanitized = cardNumber.replace("-", "").replace(" ", "").trim()

        if (sanitized.length != 16 || !sanitized.all { it.isDigit() }) {
            return false
        }

        var sum = 0

        for (i in sanitized.indices) {
            var digit = sanitized[i].digitToInt()

            if (i % 2 == 0) { // ضرب ارقام در موقعیت‌های فرد در 2 (با اندیس صفر زوج حساب می‌شود)
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }
            sum += digit
        }

        return sum % 10 == 0
    }
}
