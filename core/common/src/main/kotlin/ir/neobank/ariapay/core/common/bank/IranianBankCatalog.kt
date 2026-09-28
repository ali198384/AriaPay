package ir.neobank.ariapay.core.common.bank

import ir.neobank.ariapay.core.model.IranianBank

internal object IranianBankCatalog {
    val banks: List<IranianBank> = listOf(
        bank("cbi", "بانک مرکزی", "Central Bank", setOf("010"), setOf("636795")),
        bank("sanat_madan", "بانک صنعت و معدن", "Bank of Industry and Mine", setOf("011"), setOf("627961")),
        bank("mellat", "بانک ملت", "Bank Mellat", setOf("012"), setOf("610433", "991975")),
        bank("refah", "بانک رفاه کارگران", "Refah Bank", setOf("013"), setOf("589463")),
        bank("maskan", "بانک مسکن", "Bank Maskan", setOf("014"), setOf("628023")),
        bank(
            id = "sepah",
            fa = "بانک سپه",
            en = "Bank Sepah",
            sheba = setOf("015", "052", "063", "065", "073", "090"),
            bins = setOf("589210", "639599", "627381", "636949", "505801", "639370"),
        ),
        bank("keshavarzi", "بانک کشاورزی", "Bank Keshavarzi", setOf("016"), setOf("603770", "639217")),
        bank(
            id = "melli",
            fa = "بانک ملی",
            en = "Bank Melli",
            sheba = setOf("017", "062", "080"),
            bins = setOf("603799", "170019", "636214", "507677"),
        ),
        bank("tejarat", "بانک تجارت", "Tejarat Bank", setOf("018"), setOf("627353", "585983")),
        bank("saderat", "بانک صادرات", "Saderat Bank", setOf("019"), setOf("603769", "903769")),
        bank("edbi", "بانک توسعه صادرات", "EDBI", setOf("020"), setOf("627648", "207177")),
        bank("post", "پست بانک", "Post Bank", setOf("021"), setOf("627760")),
        bank("taavon", "بانک توسعه تعاون", "Tose'e Ta'avon", setOf("022"), setOf("502908")),
        bank("karafarin", "بانک کارآفرین", "Karafarin Bank", setOf("053"), setOf("627488", "502910")),
        bank("parsian", "بانک پارسیان", "Parsian Bank", setOf("054"), setOf("622106", "627884", "639194")),
        bank("en", "بانک اقتصاد نوین", "EN Bank", setOf("055"), setOf("627412")),
        bank("saman", "بانک سامان", "Saman Bank", setOf("056"), setOf("621986")),
        bank("pasargad", "بانک پاسارگاد", "Pasargad Bank", setOf("057"), setOf("502229", "639347")),
        bank("sarmayeh", "بانک سرمایه", "Sarmayeh Bank", setOf("058"), setOf("639607")),
        bank("sina", "بانک سینا", "Sina Bank", setOf("059"), setOf("639346")),
        bank("mehr_iran", "بانک مهر ایران", "Mehr Iran", setOf("060"), setOf("606373")),
        bank("shahr", "بانک شهر", "Shahr Bank", setOf("061"), setOf("502806", "504706")),
        bank("gardeshgari", "بانک گردشگری", "Tourism Bank", setOf("064"), setOf("505416", "505426")),
        bank("dey", "بانک دی", "Dey Bank", setOf("066"), setOf("502938")),
        bank("iran_zamin", "بانک ایران زمین", "Iran Zamin", setOf("069"), setOf("505785")),
        bank("resalat", "بانک رسالت", "Resalat Bank", setOf("070"), setOf("504172")),
        bank("melal", "موسسه اعتباری ملل", "Melal", setOf("075"), setOf("606256")),
        bank("middle_east", "بانک خاورمیانه", "Middle East Bank", setOf("078"), setOf("585947")),
        bank("venezuela", "بانک ایران و ونزوئلا", "Iran Venezuela", setOf("079"), setOf("581874")),
    )

    val legacyNames: Map<String, String> = mapOf(
        "052" to "قوامین", "639599" to "قوامین",
        "063" to "انصار", "627381" to "انصار",
        "065" to "حکمت ایرانیان", "636949" to "حکمت ایرانیان",
        "073" to "کوثر", "505801" to "کوثر",
        "090" to "مهر اقتصاد", "639370" to "مهر اقتصاد",
        "062" to "آینده", "636214" to "آینده",
        "080" to "نور", "507677" to "نور",
    )

    private fun bank(
        id: String, fa: String, en: String, sheba: Set<String>, bins: Set<String>,
    ) = IranianBank(id, fa, en, sheba, bins)
}
