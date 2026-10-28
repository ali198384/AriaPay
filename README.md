# AriaPay

**AriaPay** یک پروژهٔ نمونهٔ نئوبانک اندرویدی با Kotlin و Jetpack Compose است. هدف پروژه تمرین و نمایش مهارت‌های ساخت یک اپلیکیشن مالی با معماری ماژولار، طراحی فارسی و راست‌چین، ورود امن، مدیریت نشست، ذخیره‌سازی محلی و تست‌پذیری است.

> این پروژه یک نمونهٔ آموزشی و پورتفولیو است؛ به بانک یا سرویس مالی واقعی متصل نیست و در حال حاضر انتقال وجه، پرداخت یا موجودی واقعی ارائه نمی‌دهد. پیاده‌سازی ورود از backend آزمایشی استفاده می‌کند.

## وضعیت فعلی

**✅ انجام‌شده — فاز ۴، احراز هویت:** ورود با شمارهٔ موبایل و OTP، مدیریت وضعیت‌ها و خطاها، شمارش معکوس ارسال دوبارهٔ کد و حفظ وضعیت درخواست هنگام جابه‌جایی بین صفحه‌ها. backend فعلی شبیه‌سازی‌شده است؛ کد آزمایشی OTP برابر `123456` است.

**✅ انجام‌شده — فاز ۵، امنیت و قفل برنامه:** ساخت و تأیید PIN شش‌رقمی، ورود با PIN یا biometrics، نگهداری PIN به شکل رمز‌شده با Tink و Android Keystore و قفل نشست در پس‌زمینه یا پس از بی‌فعالیتی.

**✅ انجام‌شده — فاز ۶، بخش‌های 6-1 تا 6-8؛ پروفایل و تنظیمات:** پروفایل محلی با Room، ویرایش نام و تصویر پروفایل، ذخیرهٔ تصویر در فضای خصوصی برنامه، انتخاب و ذخیرهٔ تم روشن/تیره و خروج همراه با پاک‌سازی داده‌های نشست.

**✅ انجام‌شده — زیرساخت مشترک:** ساختار feature-oriented، تزریق وابستگی با Hilt، مسیرهای Navigation Compose، Design System فارسی و راست‌چین، اجزای adaptive و تنظیمات مشترک با DataStore.

**✅ انجام‌شده — کیفیت و اتوماسیون پایه:** تست‌های unit و instrumentation در چند ماژول و یک GitHub Actions workflow که هنگام push و pull request دستور `./gradlew build` را اجرا می‌کند. تست‌ها و پوشش آن‌ها همچنان در حال گسترش‌اند.

**🟡 در حال تکمیل — صفحهٔ Home:** ماژول Home و مسیر ناوبری وجود دارد، اما صفحه فعلاً یک نقطهٔ شروع ساده است و هنوز داشبورد بانکی یا اطلاعات مالی واقعی نمایش نمی‌دهد.

## قابلیت‌ها و مهارت‌های به‌کاررفته

- **Kotlin و معماری چندماژوله:** ماژول‌های `presentation`، `domain` و `data` برای featureهای اصلی جدا شده‌اند تا مسئولیت‌ها و وابستگی‌ها روشن بمانند.
- **Jetpack Compose:** رابط‌های declarative، مدیریت state با `StateFlow`، جمع‌آوری lifecycle-aware، طراحی reusable و توجه به recomposition.
- **رابط فارسی:** پشتیبانی از RTL، فونت Vazirmatn، اجزای Design System و چیدمان adaptive بر اساس عرض پنجره.
- **مدیریت وابستگی:** Hilt و Convention Pluginهای محلی در `build-logic`؛ نسخهٔ کتابخانه‌ها در Version Catalog متمرکز شده است.
- **ذخیره‌سازی:** Room برای پروفایل محلی و Preferences DataStore برای تنظیمات و داده‌های سبک برنامه.
- **امنیت:** Tink AEAD با کلید محافظت‌شده توسط Android Keystore، تفکیک کاربرد دادهٔ رمز‌شده با associated data، biometric prompt و مدیریت وضعیت نشست.
- **منطق مالی پایه:** نرمال‌سازی ورودی موبایل، OTP، کارت و شبا؛ اعتبارسنجی Luhn و شبا؛ تشخیص بانک ایرانی و قالب‌بندی مبلغ.
- **تست:** تست unit برای منطق دامنه، ViewModel و کلاس‌های مشترک؛ تست instrumentation برای بخش‌هایی از Room و اجزای Compose. کتابخانه‌هایی مانند JUnit، MockK، Truth و Turbine در پروژه استفاده شده‌اند.
- **CI پایه:** اجرای build و تست‌های Gradle در GitHub Actions برای push و pull request.

## ساختار ماژول‌ها

در این درخت، **شاخه‌های پررنگ با ✅** ماژول‌ها و قابلیت‌های موجود را نشان می‌دهند. شاخه‌های **کم‌رنگ با ◻️** بخش‌های برنامه‌ریزی‌شده‌اند و هنوز در پروژه وجود ندارند. Home موجود است اما فعلاً فقط یک صفحهٔ آغازین ساده دارد.

<pre>
AriaPay/
├── <strong>app/</strong> — ✅ برنامهٔ اصلی، Hilt، تم و Navigation Compose
├── <strong>build-logic/convention/</strong> — ✅ Convention Pluginهای مشترک Gradle
├── <strong>core/</strong>
│   ├── <strong>common/</strong> — ✅ Result، dispatcher، نرمال‌سازی و اعتبارسنجی مالی
│   ├── <strong>model/</strong> — ✅ مدل‌های پایه مانند Money و IranianBank
│   ├── <strong>session/</strong> — ✅ مدیریت نشست، timeout و وضعیت قفل
│   ├── <strong>testing/</strong> — ✅ ابزارهای مشترک تست
│   ├── <strong>designsystem/</strong> — ✅ تم فارسی، RTL، اجزای Compose و UI تطبیق‌پذیر
│   ├── <strong>database/</strong> — ✅ Room و مدل محلی پروفایل
│   ├── <strong>datastore/</strong> — ✅ تنظیمات و داده‌های محلی با Preferences DataStore
│   ├── <strong>security/</strong> — ✅ Tink، Android Keystore و ابزارهای امنیتی
│   ├── <small><i>◻️ network/ — کلاینت API، مدیریت خطا و ارتباط امن با backend واقعی</i></small>
│   └── <small><i>◻️ analytics/ — قرارداد ثبت رویدادها و پیاده‌سازی اولیه</i></small>
├── <strong>feature/</strong>
│   ├── <strong>auth/</strong> — ✅ احراز هویت آزمایشی با شمارهٔ موبایل و OTP
│   │   ├── <strong>domain/</strong> — ✅ مدل‌ها، قرارداد repository و use caseها
│   │   ├── <strong>data/</strong> — ✅ repository و fake remote data source
│   │   └── <strong>presentation/</strong> — ✅ صفحه‌های موبایل و OTP، ViewModel و state
│   ├── <strong>applock/</strong> — ✅ ایجاد PIN، بازکردن قفل و biometric
│   │   ├── <strong>domain/</strong> — ✅ قرارداد repository قفل برنامه
│   │   ├── <strong>data/</strong> — ✅ ذخیره و بررسی PIN رمز‌شده
│   │   └── <strong>presentation/</strong> — ✅ صفحهٔ PIN و جریان‌های UI
│   ├── <strong>profile/</strong> — ✅ پروفایل، ویرایش و تنظیمات کاربر
│   │   ├── <strong>domain/</strong> — ✅ مدل، قراردادها و use caseها
│   │   ├── <strong>data/</strong> — ✅ Room repository و ذخیرهٔ تصویر در فضای خصوصی برنامه
│   │   └── <strong>presentation/</strong> — ✅ صفحهٔ پروفایل، ویرایش، تم و خروج
│   ├── <strong>home/</strong> — 🟡 ماژول فعلی با صفحهٔ آغازین ساده
│   ├── <small><i>◻️ wallet/ — نمایش حساب‌ها، کیف پول و موجودی از backend</i></small>
│   ├── <small><i>◻️ cards/ — مدیریت و نمایش کارت‌های بانکی</i></small>
│   ├── <small><i>◻️ transfer/ — انتقال وجه و تأیید عملیات</i></small>
│   ├── <small><i>◻️ bills/ — پرداخت قبض</i></small>
│   ├── <small><i>◻️ topup/ — خرید شارژ سیم‌کارت</i></small>
│   ├── <small><i>◻️ transactions/ — تاریخچه و جزئیات تراکنش‌ها</i></small>
│   └── <small><i>◻️ receipts/ — رسید قابل مشاهده و اشتراک‌گذاری</i></small>
├── <small><i>◻️ sync/ — همگام‌سازی آفلاین با WorkManager</i></small>
├── <small><i>◻️ benchmark/ — سنجش عملکرد و Macrobenchmark</i></small>
├── <strong>.github/workflows/ci.yml</strong> — ✅ اجرای build و تست‌ها در push و pull request
├── <small><i>◻️ .github/workflows/nightly.yml — تست‌های زمان‌بندی‌شده، screenshot و coverage</i></small>
├── <small><i>◻️ config/ — تنظیمات تحلیل ایستا و network security برای محیط‌های build</i></small>
└── <strong>gradle/libs.versions.toml</strong> — ✅ مدیریت متمرکز نسخهٔ کتابخانه‌ها
</pre>

### موارد برنامه‌ریزی‌شده

<small><i>◻️ اتصال احراز هویت و پروفایل به backend واقعی، همراه با مدیریت خطاهای API و محیط‌های توسعه و تولید.</i></small>

<small><i>◻️ تکمیل Home و توسعهٔ تدریجی ماژول‌های wallet، cards، transfer، bills، topup، transactions و receipts.</i></small>

<small><i>◻️ افزودن همگام‌سازی آفلاین، تست‌های UI و end-to-end، گزارش coverage و بررسی‌های بیشتر در CI.</i></small>

<small><i>◻️ ارزیابی KMP برای اشتراک‌گذاری بخش‌های مناسب domain و data؛ نسخهٔ فعلی فقط Android است.</i></small>

## فناوری‌ها

| بخش | فناوری‌های استفاده‌شده |
|---|---|
| زبان و رابط کاربری | Kotlin، Jetpack Compose، Material 3 |
| نسخه‌های build | Kotlin 2.4.21، AGP 9.4.1، Gradle 9.8، Compose BOM 2026.09.00 |
| معماری و ناوبری | ماژولار، feature-oriented، ViewModel، StateFlow، Navigation Compose |
| تزریق وابستگی | Hilt و KSP |
| ذخیره‌سازی | Room، Preferences DataStore |
| امنیت | Tink، Android Keystore، AndroidX Biometric |
| تست | JUnit، MockK، Truth، Turbine، AndroidX Test |
| build و CI | Gradle، Version Catalog، Convention Plugin، GitHub Actions |
| طراحی فارسی | RTL، Vazirmatn، Compose adaptive window sizing |

## اجرای پروژه

### پیش‌نیازها

- Android Studio سازگار با Android Gradle Plugin پروژه.
- JDK 25؛ تنظیم فعلی CI نیز از JDK 25 استفاده می‌کند.
- Android SDK با `compileSdk` و `targetSdk` برابر 37؛ `minSdk` برابر 26 است.
- دسترسی به مخزن‌های وابستگی تعریف‌شده در Gradle. در صورت نیاز، مسیر SDK را در فایل محلی `local.properties` تنظیم کنید؛ این فایل commit نمی‌شود.

### ساخت نسخهٔ Debug

در Windows:

```powershell
.\gradlew.bat :app:assembleDebug
```

در Linux یا macOS:

```bash
./gradlew :app:assembleDebug
```

### اجرای تست‌ها و CI

```bash
./gradlew test
```

برای اجرای build کامل مطابق workflow فعلی:

```bash
./gradlew build
```

تست‌های instrumentation به emulator یا دستگاه متصل نیاز دارند:

```bash
./gradlew connectedDebugAndroidTest
```

## ورود آزمایشی

- شمارهٔ موبایل معتبر وارد کنید.
- کد OTP آزمایشی: `123456`
- اطلاعات ورود و نشست به سرویس واقعی ارسال نمی‌شوند؛ منبع دادهٔ فعلی برای نمایش جریان برنامه fake است.

## یادداشت‌های توسعه

- نسخهٔ فعلی برای نمایش معماری و جریان‌های اصلی اپلیکیشن است و هنوز برای استفادهٔ بانکی یا پردازش تراکنش واقعی طراحی نشده است.
- تست‌ها در کنار هر ماژول قرار گرفته‌اند تا منطق هر feature مستقل‌تر قابل بررسی باشد.
- workflow موجود، build را در push و pull request اجرا می‌کند. گسترش pipeline با تحلیل ایستا، گزارش پوشش و تست‌های UI در برنامهٔ آینده است.
