# AriaPay

<div dir="rtl" align="right">

## معرفی

AriaPay یک نمونه‌کار اندرویدی در حوزهٔ نئوبانک است که با Kotlin و Jetpack Compose ساخته می‌شود. هدف آن نمایش توانایی طراحی معماری قابل نگهداری، ساخت جریان‌های مالی نمایشی، توجه به امنیت و تجربهٔ فارسی، و نوشتن تست برای بخش‌های مختلف برنامه است.

این پروژه برای نمونه‌کار و یادگیری است، نه یک اپلیکیشن بانکی عملیاتی. ورود فعلی به سرویس واقعی وصل نیست: OTP و پاسخ‌های احراز هویت از منبع آزمایشی می‌آیند و هیچ تراکنش مالی واقعی انجام نمی‌شود. اطلاعات کامل کارت، CVV، OTP واقعی یا دادهٔ بانکی مشتری در محدودهٔ پروژه نیست.

## آنچه تاکنون پیاده‌سازی شده

- **ورود آزمایشی با شمارهٔ موبایل و OTP:** نرمال‌سازی ورودی، حالت‌های loading و error، شمارش معکوس ارسال دوبارهٔ کد و حفظ کد و زمان باقی‌مانده هنگام برگشت و ورود دوباره با همان شماره. کد دمو `123456` است.
- **صفحهٔ OTP قابل ویرایش:** شش خانهٔ مستقل، هر کدام فقط برای یک رقم؛ امکان لمس و اصلاح خانه‌های قبلی، حرکت خودکار فوکوس و ارسال خودکار پس از تکمیل شش رقم.
- **قفل محلی برنامه:** ساخت و تأیید PIN شش‌رقمی، ورود با PIN و پشتیبانی از BiometricPrompt در دستگاه سازگار.
- **نگهداری امن PIN:** رمزنگاری با Tink و کلیدی که Android Keystore محافظت می‌کند. PIN به UI فرستاده نمی‌شود و برای نمایش فقط تعداد رقم‌های واردشده نگهداری می‌شود.
- **مدیریت نشست:** وضعیت‌های ورود، قفل و بازبودن نشست؛ قفل هنگام رفتن برنامه به پس‌زمینه و پس از بی‌فعالیتی؛ بازگشایی با PIN یا biometric.
- **پروفایل:** نگهداری پروفایل محلی در Room، از جمله شناسهٔ کاربر، نام، اطلاعات پوشیده‌شده، وضعیت حساب و مسیر تصویر. ویرایش نام و انتخاب تصویر پشتیبانی می‌شوند؛ تصویر در فضای خصوصی خود برنامه کپی می‌شود.
- **تم برنامه:** کاربر می‌تواند حالت **روشن (Light)**، **تیره (Dark)** یا **پیروی از تنظیم دستگاه (System)** را انتخاب کند. انتخاب با Preferences DataStore ذخیره می‌شود و در کل برنامه اعمال می‌شود.
- **ابزارهای مالی مشترک:** نرمال‌سازی شمارهٔ موبایل، OTP، شمارهٔ کارت و شبا؛ اعتبارسنجی Luhn و شبا؛ تشخیص بانک ایرانی و قالب‌بندی مبلغ بر مبنای ریال.
- **طراحی فارسی و adaptive:** RTL، فونت Vazirmatn، اجزای Design System و تغییر چیدمان بر اساس عرض پنجره.
- **زیرساخت پروژه:** ماژول‌بندی Gradle، Version Catalog، Convention Pluginهای محلی، Hilt و Navigation Compose.
- **تست و CI پایه:** تست‌های unit و instrumentation در ماژول‌های مختلف؛ GitHub Actions workflow فعلی روی push و pull request دستور `./gradlew build` را اجرا می‌کند. تست‌ها و CI هنوز در حال گسترش‌اند.

صفحهٔ Home فعلی ساده است و نقش نقطهٔ شروع را دارد؛ هنوز داشبورد موجودی، کارت و فعالیت مالی پیاده‌سازی نشده است.

## معماری

پروژه از **معماری ماژولار Feature-oriented با جداسازی لایه‌ها درون قابلیت‌ها** استفاده می‌کند. هر قابلیت اصلی مالک منطق، قراردادها و داده‌های خودش است. در وضعیت فعلی، Auth، Profile و AppLock به ماژول‌های جداگانهٔ Gradle تقسیم شده‌اند؛ Home به دلیل کوچک‌بودن فعلی یک ماژول ساده دارد و تا وقتی مرز یا نیاز تستی روشنی ایجاد نشود، لایه‌های بیشتری برای آن ساخته نمی‌شود.

- **Presentation:** صفحه‌های Compose، ViewModel و قراردادهای UI مثل State، Event و Effect. این بخش مستقیماً با Room، DataStore یا API کار نمی‌کند.
- **Domain:** مدل‌ها، قوانین کسب‌وکار، قرارداد Repository و UseCaseهای همان قابلیت. تا حد ممکن مستقل از Android و ابزارهای ذخیره‌سازی می‌ماند.
- **Data:** پیاده‌سازی Repository، منبع محلی یا remote/fake و تبدیل مدل‌ها برای همان قابلیت.
- **Core:** فقط زیرساخت و مفاهیم واقعاً مشترک را نگه می‌دارد؛ منطق مختص Auth یا Profile به Core منتقل نشده است. بنابراین در ساختار فعلی ماژول سراسری `:core:domain` یا `:core:data` وجود ندارد.
- **App:** نقطهٔ ترکیب برنامه است؛ Application، Activity، Hilt، ناوبری سراسری و اتصال featureها در این بخش قرار دارند.
- **Session:** وضعیت نشست در `:core:session` مشترک است تا AppLock، Profile و پوستهٔ برنامه یک منبع وضعیت داشته باشند.
- **Database:** `:core:database` مالک ساخت Room و schema مشترک است؛ repository و منطق پروفایل در feature پروفایل می‌مانند.

قاعدهٔ وابستگی این است که UI رویداد را به ViewModel می‌فرستد، ViewModel از قراردادهای Domain استفاده می‌کند و Repository در ماژول Data آن قراردادها را پیاده می‌کند. `:app` ماژول‌های لازم را به هم متصل می‌کند. این ساختار امکان تست منطق را بدون اجرای UI و جدا از سرویس واقعی فراهم می‌کند.

## ساختار ماژول‌ها

درخت زیر فقط ماژول‌های اصلی را نشان می‌دهد؛ تقسیم داخلی featureها به لایه‌ها در بخش «معماری» توضیح داده شده است. **موارد پررنگ با ✅ در پروژه موجودند**، Home فعلی با 🟡 هنوز ساده است و موارد کم‌رنگ با ◻️ در roadmap آمده‌اند.

</div>

<pre dir="ltr" align="left">
AriaPay/
├── <strong>app/</strong> — ✅ اپلیکیشن اصلی، DI، تم و ناوبری
│   └── src/
│       ├── <strong>main/</strong> — ✅ نسخهٔ فعلی
│       ├── <small><i>demoDebug/ — برنامه‌ریزی‌شده برای دموی مستقل</i></small>
│       ├── <small><i>prodDebug/ — برنامه‌ریزی‌شده برای backend واقعی</i></small>
│       └── <small><i>bazaarRelease/ — برنامه‌ریزی‌شده در صورت توزیع در بازار</i></small>
├── <strong>build-logic/convention/</strong> — ✅ تنظیمات و pluginهای مشترک Gradle
├── <strong>core/</strong>
│   ├── <strong>common/</strong> — ✅ خطا، Result، dispatcher و ابزارهای مالی
│   ├── <strong>model/</strong> — ✅ مدل‌های مشترک مانند Money و IranianBank
│   ├── <strong>session/</strong> — ✅ وضعیت و قفل نشست
│   ├── <strong>testing/</strong> — ✅ ابزارهای مشترک تست
│   ├── <strong>designsystem/</strong> — ✅ تم فارسی، RTL و اجزای UI
│   ├── <strong>database/</strong> — ✅ Room و schema پایگاه داده
│   ├── <strong>datastore/</strong> — ✅ تنظیمات و داده‌های سبک محلی
│   ├── <strong>security/</strong> — ✅ Tink، Android Keystore و کنترل‌های امنیتی
│   ├── <small><i>network/ — برنامه‌ریزی‌شده؛ کلاینت API مشترک در صورت نیاز چند feature</i></small>
│   ├── <small><i>ui/ — مشروط به شکل‌گیری اجزای سطح صفحهٔ واقعاً مشترک</i></small>
│   └── <small><i>analytics/ — مشروط به نیاز ثبت رویدادهای مشترک</i></small>
├── <strong>feature/</strong>
│   ├── <strong>auth/</strong> — ✅ ورود آزمایشی با موبایل و OTP
│   ├── <strong>applock/</strong> — ✅ PIN، قفل نشست و biometric
│   ├── <strong>profile/</strong> — ✅ پروفایل، ویرایش، تصویر، تم و خروج
│   ├── <strong>home/</strong> — 🟡 صفحهٔ آغازین ساده
│   ├── <small><i>wallet/ — برنامه‌ریزی‌شده؛ حساب‌ها و موجودی نمونه</i></small>
│   ├── <small><i>cards/ — برنامه‌ریزی‌شده؛ نمایش کارت ماسک‌شده</i></small>
│   ├── <small><i>transfer/ — برنامه‌ریزی‌شده؛ فلو انتقال نمایشی</i></small>
│   ├── <small><i>bills/ — برنامه‌ریزی‌شده؛ پرداخت قبض نمایشی</i></small>
│   ├── <small><i>topup/ — برنامه‌ریزی‌شده؛ خرید شارژ نمایشی</i></small>
│   ├── <small><i>transactions/ — برنامه‌ریزی‌شده؛ تاریخچه و جزئیات</i></small>
│   └── <small><i>receipts/ — برنامه‌ریزی‌شده؛ رسید با اطلاعات پوشیده</i></small>
├── <small><i>sync/ — برنامه‌ریزی‌شده؛ کارهای آفلاین با WorkManager</i></small>
├── <small><i>benchmark/ — برنامه‌ریزی‌شده؛ سنجش عملکرد با Macrobenchmark</i></small>
├── <strong>.github/workflows/ci.yml</strong> — ✅ اجرای build و تست‌ها
├── <small><i>.github/workflows/nightly.yml — برنامه‌ریزی‌شده؛ screenshot و coverage</i></small>
├── <small><i>config/ — برنامه‌ریزی‌شده؛ قواعد Detekt و تنظیمات امنیت شبکه</i></small>
└── <strong>gradle/libs.versions.toml</strong> — ✅ مرجع متمرکز نسخهٔ کتابخانه‌ها
</pre>

<div dir="rtl" align="right">

## تم روشن و تیره

تم سه حالت دارد: **System** از حالت روشن/تیرهٔ خود دستگاه پیروی می‌کند؛ **Light** همیشه روشن است؛ و **Dark** همیشه تیره می‌ماند. انتخاب از صفحهٔ Profile انجام و در DataStore ذخیره می‌شود. پوستهٔ برنامه تغییر را دریافت می‌کند و palette مناسب Material 3 را در سراسر برنامه اعمال می‌کند؛ جهت RTL و تایپوگرافی فارسی نیز در هر دو تم حفظ می‌شوند.

## فناوری‌های فعلی

| بخش | موارد استفاده‌شده |
|---|---|
| زبان و پلتفرم | Kotlin، Android، Gradle |
| UI | Jetpack Compose، Material 3، Navigation Compose |
| معماری و DI | ViewModel، StateFlow، Hilt، KSP، Convention Plugin |
| داده | Room، Preferences DataStore، Kotlin Coroutines |
| امنیت | Tink AEAD، Android Keystore، AndroidX Biometric |
| طراحی | RTL، Vazirmatn، Compose adaptive window sizing |
| تست | JUnit، Truth، MockK، Turbine، AndroidX Test |
| نسخه‌های پروژه | Kotlin 2.4.21، AGP 9.4.1، Gradle 9.8، Compose BOM 2026.09.00 |
| SDK | compile/target 37، min 26؛ JVM target 17 |

## فناوری‌ها و ابزارهای برنامه‌ریزی‌شده

فناوری‌های زیر هنوز بخشی از نسخهٔ فعلی نیستند. مطابق roadmap، فقط وقتی یک قابلیت به آن‌ها نیاز واقعی پیدا کند اضافه می‌شوند.

| فناوری / ابزار | کاربرد برنامه‌ریزی‌شده |
|---|---|
| Retrofit و OkHttp | اتصال featureها به API واقعی، interceptor و نگاشت خطا؛ ماژول شبکهٔ Core فقط اگر client بین چند feature مشترک شود. DTO و endpoint هر قابلیت در feature خودش می‌ماند. |
| WorkManager | اجرای sync یا صف آفلاین در صورت نیاز؛ retry انتقال فقط با idempotency و استعلام وضعیت، نه ارسال کورکورانهٔ درخواست مالی. |
| AndroidX Paging | صفحه‌بندی تاریخچهٔ تراکنش‌ها هنگام اضافه‌شدن حجم داده. |
| TLS و Network Security Config | سخت‌سازی ارتباط با backend؛ certificate pinning فقط پس از وجود endpoint پایدار و برنامهٔ rotation گواهی. |
| Detekt و Android Lint | بررسی ایستای کد و قواعد کیفیت در pipeline. |
| R8 / ProGuard | کوچک‌سازی و obfuscation نسخهٔ release، پس از اضافه‌شدن پیکربندی release. |
| GitHub Actions تکمیلی | اجرای جداگانهٔ تست، lint و assemble، ساخت artifact دمو؛ سپس screenshot test با Roborazzi و گزارش coverage. |
| Android instrumentation / Gradle Managed Device | تست رفتارهای وابسته به Android روی emulator، در مواردی که unit test کافی نیست. |
| ZarinPal Sandbox | فقط در صورت نیاز به نمایش پرداخت درگاه یا افزایش موجودی؛ این مورد با انتقال کارت‌به‌کارت یا شبا یکی نیست. |
| Macrobenchmark | اندازه‌گیری عملکرد پس از کامل‌شدن مسیرهای اصلی برنامه. |

KMP در محدودهٔ نزدیک roadmap نیست؛ پس از کامل‌شدن نمونه‌کار Android می‌توان ضرورت اشتراک‌گذاری بخش‌های Kotlin را جداگانه ارزیابی کرد.

## اجرا و تست

### پیش‌نیازها

- Android Studio سازگار با Android Gradle Plugin پروژه.
- JDK 25 برای اجرای Gradle؛ bytecode پروژه برای JVM 17 تنظیم شده است.
- Android SDK 37؛ حداقل نسخهٔ Android پشتیبانی‌شده API 26 است.
- دسترسی به مخزن‌های وابستگی تعریف‌شده در Gradle و تنظیم SDK در فایل محلی `local.properties` در صورت نیاز.

اجرای تست‌های unit:

</div>

```bash
./gradlew test
```

<div dir="rtl" align="right">

اجرای تست‌های instrumentation به emulator یا دستگاه متصل نیاز دارد:

</div>

```bash
./gradlew connectedDebugAndroidTest
```

<div dir="rtl" align="right">

## اجرای دمو

- شمارهٔ موبایل معتبر وارد کنید.
- OTP آزمایشی: `123456`
- این اطلاعات به backend بانکی ارسال نمی‌شوند و عملیات مالی واقعی انجام نمی‌شود.

## محدودیت‌های آگاهانه

- OTP و session پاسخ دمو هستند؛ authentication سمت سرور هنوز پیاده‌سازی نشده است.
- PAN کامل، CVV و اطلاعات بانکی واقعی ذخیره یا نمایش داده نمی‌شوند.
- PIN و biometric قفل محلی را باز می‌کنند و جایگزین احراز هویت سمت سرور نیستند.
- انتقال وجه، پرداخت درگاه، افزایش موجودی و تراکنش واقعی خارج از محدودهٔ نسخهٔ فعلی‌اند.

</div>
