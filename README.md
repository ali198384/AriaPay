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

## ساختار پروژه

در درخت زیر، **✅ تکمیل‌شده** یعنی قابلیت در نسخهٔ فعلی پیاده‌سازی شده، **🟡 اسکلت اولیه** یعنی ماژول موجود است اما هنوز کامل نشده و **🧪** نشان‌دهندهٔ فایل‌های تست است. فایل‌های تولیدشدهٔ Gradle، تنظیمات شخصی Android Studio و `local.properties` در این درخت نیامده‌اند.

<details>
<summary>نمایش درخت کامل فایل‌ها و ماژول‌ها</summary>

```text
AriaPay/
├── .github/
│   └── workflows/
│       └── ci.yml
├── app/  # ✅ اجرا و ناوبری
│   ├── src/
│   │   └── main/
│   │       ├── keepRules/
│   │       │   └── rules.keep
│   │       ├── kotlin/
│   │       │   └── ir/
│   │       │       └── neobank/
│   │       │           └── ariapay/
│   │       │               ├── di/
│   │       │               │   └── SessionModule.kt
│   │       │               ├── navigation/
│   │       │               │   ├── AppNavHost.kt
│   │       │               │   ├── AppPinNavHost.kt
│   │       │               │   ├── AppRoute.kt
│   │       │               │   ├── HomeNavHost.kt
│   │       │               │   ├── MobileNumberNavHost.kt
│   │       │               │   ├── OtpNavHost.kt
│   │       │               │   └── ProfileNavHost.kt
│   │       │               ├── AppThemeViewModel.kt
│   │       │               ├── AriaPayApplication.kt
│   │       │               └── MainActivity.kt
│   │       ├── res/
│   │       │   ├── drawable/
│   │       │   │   ├── ic_launcher_background.xml
│   │       │   │   └── ic_launcher_foreground.xml
│   │       │   ├── mipmap-anydpi/
│   │       │   │   ├── ic_launcher_round.xml
│   │       │   │   └── ic_launcher.xml
│   │       │   ├── mipmap-hdpi/
│   │       │   │   ├── ic_launcher_round.webp
│   │       │   │   └── ic_launcher.webp
│   │       │   ├── mipmap-mdpi/
│   │       │   │   ├── ic_launcher_round.webp
│   │       │   │   └── ic_launcher.webp
│   │       │   ├── mipmap-xhdpi/
│   │       │   │   ├── ic_launcher_round.webp
│   │       │   │   └── ic_launcher.webp
│   │       │   ├── mipmap-xxhdpi/
│   │       │   │   ├── ic_launcher_round.webp
│   │       │   │   └── ic_launcher.webp
│   │       │   ├── mipmap-xxxhdpi/
│   │       │   │   ├── ic_launcher_round.webp
│   │       │   │   └── ic_launcher.webp
│   │       │   ├── values/
│   │       │   │   ├── colors.xml
│   │       │   │   ├── strings.xml
│   │       │   │   └── themes.xml
│   │       │   └── xml/
│   │       │       ├── backup_rules.xml
│   │       │       └── data_extraction_rules.xml
│   │       └── AndroidManifest.xml
│   ├── .gitignore
│   └── build.gradle.kts
├── build-logic/  # ✅ convention plugins
│   ├── convention/
│   │   ├── src/
│   │   │   └── main/
│   │   │       └── kotlin/
│   │   │           └── ir/
│   │   │               └── neobank/
│   │   │                   └── ariapay/
│   │   │                       └── convention/
│   │   │                           ├── AndroidApplicationConventionPlugin.kt
│   │   │                           ├── AndroidCompose.kt
│   │   │                           ├── AndroidFeatureConventionPlugin.kt
│   │   │                           ├── AndroidLibraryComposeConventionPlugin.kt
│   │   │                           ├── AndroidLibraryConventionPlugin.kt
│   │   │                           ├── HiltConventionPlugin.kt
│   │   │                           ├── JvmLibraryConventionPlugin.kt
│   │   │                           ├── KotlinAndroid.kt
│   │   │                           └── ProjectExtensions.kt
│   │   ├── .gitignore
│   │   └── build.gradle.kts
│   ├── gradle/
│   │   └── wrapper/
│   │       ├── gradle-wrapper.jar
│   │       └── gradle-wrapper.properties
│   ├── gradlew
│   ├── gradlew.bat
│   └── settings.gradle.kts
├── core/
│   ├── common/  # ✅ مشترک
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   └── kotlin/
│   │   │   │       └── ir/
│   │   │   │           └── neobank/
│   │   │   │               └── ariapay/
│   │   │   │                   └── core/
│   │   │   │                       └── common/
│   │   │   │                           ├── bank/
│   │   │   │                           │   ├── BankDetector.kt
│   │   │   │                           │   └── IranianBankCatalog.kt
│   │   │   │                           ├── dispatchers/
│   │   │   │                           │   └── AriaDispatchers.kt
│   │   │   │                           ├── result/
│   │   │   │                           │   ├── AriaErrorMapper.kt
│   │   │   │                           │   ├── AriaException.kt
│   │   │   │                           │   └── AriaResult.kt
│   │   │   │                           ├── settings/
│   │   │   │                           │   ├── AppThemeMode.kt
│   │   │   │                           │   └── AppThemeSettings.kt
│   │   │   │                           ├── util/
│   │   │   │                           │   ├── CardMasker.kt
│   │   │   │                           │   ├── FinancialInputNormalizer.kt
│   │   │   │                           │   └── MoneyFormatter.kt
│   │   │   │                           └── validation/
│   │   │   │                               ├── LuhnValidator.kt
│   │   │   │                               └── ShebaValidator.kt
│   │   │   └── test/
│   │   │       └── kotlin/
│   │   │           └── ir/
│   │   │               └── neobank/
│   │   │                   └── ariapay/
│   │   │                       └── core/
│   │   │                           └── common/
│   │   │                               ├── bank/
│   │   │                               │   └── BankDetectorTest.kt
│   │   │                               ├── result/
│   │   │                               │   ├── AriaExceptionTest.kt
│   │   │                               │   └── AriaResultTest.kt
│   │   │                               ├── util/
│   │   │                               │   ├── CardMaskerTest.kt
│   │   │                               │   └── MoneyFormatterTest.kt
│   │   │                               └── validation/
│   │   │                                   ├── LuhnValidatorTest.kt
│   │   │                                   └── ShebaValidatorTest.kt
│   │   ├── .gitignore
│   │   └── build.gradle.kts
│   ├── database/  # ✅ Room
│   │   ├── schemas/
│   │   │   └── ir.neobank.ariapay.core.database.AriaDatabase/
│   │   │       └── 1.json
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── keepRules/
│   │   │       │   └── rules.keep
│   │   │       └── kotlin/
│   │   │           └── ir/
│   │   │               └── neobank/
│   │   │                   └── ariapay/
│   │   │                       └── core/
│   │   │                           └── database/
│   │   │                               ├── di/
│   │   │                               │   └── DatabaseModule.kt
│   │   │                               ├── internal/
│   │   │                               │   └── SchemaAnchor.kt
│   │   │                               ├── profile/
│   │   │                               │   ├── UserProfileDao.kt
│   │   │                               │   └── UserProfileEntity.kt
│   │   │                               └── AriaDatabase.kt
│   │   ├── .gitignore
│   │   ├── build.gradle.kts
│   │   └── consumer-rules.keep
│   ├── datastore/  # ✅ DataStore
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── keepRules/
│   │   │   │   │   └── rules.keep
│   │   │   │   ├── kotlin/
│   │   │   │   │   └── ir/
│   │   │   │   │       └── neobank/
│   │   │   │   │           └── ariapay/
│   │   │   │   │               └── core/
│   │   │   │   │                   └── datastore/
│   │   │   │   │                       ├── di/
│   │   │   │   │                       │   └── DataStoreModule.kt
│   │   │   │   │                       ├── AriaPreferenceKeys.kt
│   │   │   │   │                       ├── AriaPreferencesDataSource.kt
│   │   │   │   │                       └── AriaPreferencesDataSourceImpl.kt
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/
│   │   │       └── kotlin/
│   │   │           └── ir/
│   │   │               └── neobank/
│   │   │                   └── ariapay/
│   │   │                       └── core/
│   │   │                           └── datastore/
│   │   │                               └── AriaPreferencesDataSourceImplTest.kt
│   │   ├── .gitignore
│   │   ├── build.gradle.kts
│   │   └── consumer-rules.keep
│   ├── designsystem/  # ✅ UI
│   │   ├── src/
│   │   │   ├── androidTest/
│   │   │   │   └── kotlin/
│   │   │   │       └── ir/
│   │   │   │           └── neobank/
│   │   │   │               └── ariapay/
│   │   │   │                   └── core/
│   │   │   │                       └── designsystem/
│   │   │   │                           └── component/
│   │   │   │                               └── AriaButtonTest.kt
│   │   │   ├── main/
│   │   │   │   ├── keepRules/
│   │   │   │   │   └── rules.keep
│   │   │   │   ├── kotlin/
│   │   │   │   │   └── ir/
│   │   │   │   │       └── neobank/
│   │   │   │   │           └── ariapay/
│   │   │   │   │               └── core/
│   │   │   │   │                   └── designsystem/
│   │   │   │   │                       ├── adaptive/
│   │   │   │   │                       │   └── WindowWidthClass.kt
│   │   │   │   │                       ├── component/
│   │   │   │   │                       │   ├── AriaButton.kt
│   │   │   │   │                       │   ├── AriaFieldInput.kt
│   │   │   │   │                       │   ├── AriaOtpField.kt
│   │   │   │   │                       │   └── AriaTextField.kt
│   │   │   │   │                       └── theme/
│   │   │   │   │                           ├── Color.kt
│   │   │   │   │                           ├── ExtraColors.kt
│   │   │   │   │                           ├── Shape.kt
│   │   │   │   │                           ├── Theme.kt
│   │   │   │   │                           └── Type.kt
│   │   │   │   └── res/
│   │   │   │       └── font/
│   │   │   │           ├── vazirmatn_bold.ttf
│   │   │   │           ├── vazirmatn_medium.ttf
│   │   │   │           ├── vazirmatn_regular.ttf
│   │   │   │           └── vazirmatn_semibold.ttf
│   │   │   └── test/
│   │   │       └── kotlin/
│   │   │           └── ir/
│   │   │               └── neobank/
│   │   │                   └── ariapay/
│   │   │                       └── core/
│   │   │                           └── designsystem/
│   │   │                               └── AriaFieldInputTest.kt
│   │   ├── .gitignore
│   │   ├── build.gradle.kts
│   │   └── consumer-rules.keep
│   ├── model/  # ✅ مدل
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   └── kotlin/
│   │   │   │       └── ir/
│   │   │   │           └── neobank/
│   │   │   │               └── ariapay/
│   │   │   │                   └── core/
│   │   │   │                       └── model/
│   │   │   │                           ├── IranianBank.kt
│   │   │   │                           └── Money.kt
│   │   │   └── test/
│   │   │       └── kotlin/
│   │   │           └── ir/
│   │   │               └── neobank/
│   │   │                   └── ariapay/
│   │   │                       └── model/
│   │   │                           └── MoneyTest.kt
│   │   ├── .gitignore
│   │   └── build.gradle.kts
│   ├── security/  # ✅ امنیت
│   │   ├── src/
│   │   │   ├── androidTest/
│   │   │   │   └── kotlin/
│   │   │   │       └── ir/
│   │   │   │           └── neobank/
│   │   │   │               └── ariapay/
│   │   │   │                   └── core/
│   │   │   │                       └── security/
│   │   │   │                           └── TinkStringCipherTest.kt
│   │   │   └── main/
│   │   │       ├── keepRules/
│   │   │       │   └── rules.keep
│   │   │       └── kotlin/
│   │   │           └── ir/
│   │   │               └── neobank/
│   │   │                   └── ariapay/
│   │   │                       └── core/
│   │   │                           └── security/
│   │   │                               ├── di/
│   │   │                               │   └── SecurityModule.kt
│   │   │                               ├── DeviceSecurityChecker.kt
│   │   │                               ├── DeviceSecurityStatus.kt
│   │   │                               ├── RootDetector.kt
│   │   │                               ├── SecureValueCipher.kt
│   │   │                               ├── SecureWindow.kt
│   │   │                               └── TinkStringCipher.kt
│   │   ├── .gitignore
│   │   ├── build.gradle.kts
│   │   └── consumer-rules.keep
│   ├── session/  # ✅ نشست
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   └── kotlin/
│   │   │   │       └── ir/
│   │   │   │           └── neobank/
│   │   │   │               └── ariapay/
│   │   │   │                   └── core/
│   │   │   │                       └── session/
│   │   │   │                           ├── AppSessionState.kt
│   │   │   │                           └── SessionManager.kt
│   │   │   └── test/
│   │   │       └── kotlin/
│   │   │           └── ir/
│   │   │               └── neobank/
│   │   │                   └── ariapay/
│   │   │                       └── core/
│   │   │                           └── session/
│   │   │                               └── SessionManagerTest.kt
│   │   └── build.gradle.kts
│   └── testing/  # ✅ ابزار تست
│       ├── src/
│       │   └── main/
│       │       └── kotlin/
│       │           └── ir/
│       │               └── neobank/
│       │                   └── ariapay/
│       │                       └── core/
│       │                           └── testing/
│       │                               ├── AriaDispatchersFake.kt
│       │                               └── MainDispatcherExtension.kt
│       ├── .gitignore
│       └── build.gradle.kts
├── docs/
│   ├── adr/
│   │   └── .gitkeep
│   └── OFL.txt
├── feature/
│   ├── applock/  # ✅ قفل برنامه
│   │   ├── data/
│   │   │   ├── src/
│   │   │   │   ├── main/
│   │   │   │   │   └── kotlin/
│   │   │   │   │       └── ir/
│   │   │   │   │           └── neobank/
│   │   │   │   │               └── ariapay/
│   │   │   │   │                   └── feature/
│   │   │   │   │                       └── applock/
│   │   │   │   │                           └── data/
│   │   │   │   │                               ├── di/
│   │   │   │   │                               │   └── AppPinDataModule.kt
│   │   │   │   │                               └── AppPinRepositoryImpl.kt
│   │   │   │   └── test/
│   │   │   │       └── kotlin/
│   │   │   │           └── ir/
│   │   │   │               └── neobank/
│   │   │   │                   └── ariapay/
│   │   │   │                       └── feature/
│   │   │   │                           └── applock/
│   │   │   │                               └── data/
│   │   │   │                                   └── AppPinRepositoryImplTest.kt
│   │   │   └── build.gradle.kts
│   │   ├── domain/
│   │   │   ├── src/
│   │   │   │   └── main/
│   │   │   │       └── kotlin/
│   │   │   │           └── ir/
│   │   │   │               └── neobank/
│   │   │   │                   └── ariapay/
│   │   │   │                       └── feature/
│   │   │   │                           └── applock/
│   │   │   │                               └── domain/
│   │   │   │                                   └── AppPinRepository.kt
│   │   │   └── build.gradle.kts
│   │   └── presentation/
│   │       ├── src/
│   │       │   └── main/
│   │       │       └── kotlin/
│   │       │           └── ir/
│   │       │               └── neobank/
│   │       │                   └── ariapay/
│   │       │                       └── feature/
│   │       │                           └── applock/
│   │       │                               ├── contract/
│   │       │                               │   └── AppPinContract.kt
│   │       │                               ├── AppPinScreen.kt
│   │       │                               └── AppPinViewModel.kt
│   │       └── build.gradle.kts
│   ├── auth/  # ✅ احراز هویت
│   │   ├── data/
│   │   │   ├── src/
│   │   │   │   ├── main/
│   │   │   │   │   ├── keepRules/
│   │   │   │   │   │   └── rules.keep
│   │   │   │   │   └── kotlin/
│   │   │   │   │       └── ir/
│   │   │   │   │           └── neobank/
│   │   │   │   │               └── ariapay/
│   │   │   │   │                   └── feature/
│   │   │   │   │                       └── auth/
│   │   │   │   │                           └── data/
│   │   │   │   │                               ├── di/
│   │   │   │   │                               │   └── AuthDataModule.kt
│   │   │   │   │                               ├── remote/
│   │   │   │   │                               │   ├── AuthRemoteDataSource.kt
│   │   │   │   │                               │   ├── AuthRemoteError.kt
│   │   │   │   │                               │   ├── AuthRemoteResponse.kt
│   │   │   │   │                               │   ├── AuthRemoteSession.kt
│   │   │   │   │                               │   └── FakeAuthRemoteDataSourceImpl.kt
│   │   │   │   │                               └── repository/
│   │   │   │   │                                   ├── AuthRepositoryImpl.kt
│   │   │   │   │                                   └── FakeAuthRepository.kt
│   │   │   │   └── test/
│   │   │   │       └── kotlin/
│   │   │   │           └── ir/
│   │   │   │               └── neobank/
│   │   │   │                   └── ariapay/
│   │   │   │                       └── feature/
│   │   │   │                           └── auth/
│   │   │   │                               └── data/
│   │   │   │                                   ├── remote/
│   │   │   │                                   │   └── FakeAuthRemoteDataSourceImplTest.kt
│   │   │   │                                   └── repository/
│   │   │   │                                       └── FakeAuthRepositoryTest.kt
│   │   │   └── build.gradle.kts
│   │   ├── domain/
│   │   │   ├── src/
│   │   │   │   ├── main/
│   │   │   │   │   └── kotlin/
│   │   │   │   │       └── ir/
│   │   │   │   │           └── neobank/
│   │   │   │   │               └── ariapay/
│   │   │   │   │                   └── feature/
│   │   │   │   │                       └── auth/
│   │   │   │   │                           └── domain/
│   │   │   │   │                               ├── repository/
│   │   │   │   │                               │   └── AuthRepository.kt
│   │   │   │   │                               ├── usecase/
│   │   │   │   │                               │   ├── RequestOtpUseCase.kt
│   │   │   │   │                               │   └── VerifyOtpUseCase.kt
│   │   │   │   │                               ├── AuthError.kt
│   │   │   │   │                               ├── AuthResult.kt
│   │   │   │   │                               ├── AuthSession.kt
│   │   │   │   │                               ├── MobileNumber.kt
│   │   │   │   │                               └── OtpCode.kt
│   │   │   │   └── test/
│   │   │   │       └── kotlin/
│   │   │   │           └── ir/
│   │   │   │               └── neobank/
│   │   │   │                   └── ariapay/
│   │   │   │                       └── feature/
│   │   │   │                           └── auth/
│   │   │   │                               └── domain/
│   │   │   │                                   ├── AuthModelsTest.kt
│   │   │   │                                   └── AuthUseCaseTest.kt
│   │   │   └── build.gradle.kts
│   │   └── presentation/
│   │       ├── src/
│   │       │   ├── main/
│   │       │   │   ├── keepRules/
│   │       │   │   │   └── rules.keep
│   │       │   │   └── kotlin/
│   │       │   │       └── ir/
│   │       │   │           └── neobank/
│   │       │   │               └── ariapay/
│   │       │   │                   └── feature/
│   │       │   │                       └── auth/
│   │       │   │                           ├── contract/
│   │       │   │                           │   ├── AuthEffect.kt
│   │       │   │                           │   ├── AuthEvent.kt
│   │       │   │                           │   ├── AuthOperation.kt
│   │       │   │                           │   ├── AuthStep.kt
│   │       │   │                           │   ├── AuthUiState.kt
│   │       │   │                           │   ├── MobileNumberUiState.kt
│   │       │   │                           │   └── OtpUiState.kt
│   │       │   │                           ├── di/
│   │       │   │                           │   └── AuthUseCaseModule.kt
│   │       │   │                           ├── AuthViewModel.kt
│   │       │   │                           ├── MobileNumberScreen.kt
│   │       │   │                           └── OtpScreen.kt
│   │       │   └── test/
│   │       │       └── kotlin/
│   │       │           └── ir/
│   │       │               └── neobank/
│   │       │                   └── ariapay/
│   │       │                       └── feature/
│   │       │                           └── auth/
│   │       │                               └── AuthViewModelTest.kt
│   │       ├── .gitignore
│   │       ├── build.gradle.kts
│   │       └── consumer-rules.keep
│   ├── home/  # 🟡 اسکلت اولیه
│   │   ├── src/
│   │   │   └── main/
│   │   │       ├── keepRules/
│   │   │       │   └── rules.keep
│   │   │       └── kotlin/
│   │   │           └── ir/
│   │   │               └── neobank/
│   │   │                   └── ariapay/
│   │   │                       └── feature/
│   │   │                           └── home/
│   │   │                               └── HomeScreen.kt
│   │   ├── .gitignore
│   │   ├── build.gradle.kts
│   │   └── consumer-rules.keep
│   └── profile/  # ✅ پروفایل
│       ├── data/
│       │   ├── src/
│       │   │   ├── androidTest/
│       │   │   │   └── kotlin/
│       │   │   │       └── ir/
│       │   │   │           └── neobank/
│       │   │   │               └── ariapay/
│       │   │   │                   └── feature/
│       │   │   │                       └── profile/
│       │   │   │                           └── data/
│       │   │   │                               └── repository/
│       │   │   │                                   └── ProfileRepositoryImplTest.kt
│       │   │   └── main/
│       │   │       └── kotlin/
│       │   │           └── ir/
│       │   │               └── neobank/
│       │   │                   └── ariapay/
│       │   │                       └── feature/
│       │   │                           └── profile/
│       │   │                               └── data/
│       │   │                                   ├── di/
│       │   │                                   │   └── ProfileDataModule.kt
│       │   │                                   ├── repository/
│       │   │                                   │   └── ProfileRepositoryImpl.kt
│       │   │                                   └── storage/
│       │   │                                       └── InternalProfilePhotoStorage.kt
│       │   └── build.gradle.kts
│       ├── domain/
│       │   ├── src/
│       │   │   ├── main/
│       │   │   │   └── kotlin/
│       │   │   │       └── ir/
│       │   │   │           └── neobank/
│       │   │   │               └── ariapay/
│       │   │   │                   └── feature/
│       │   │   │                       └── profile/
│       │   │   │                           └── domain/
│       │   │   │                               ├── model/
│       │   │   │                               │   └── UserProfile.kt
│       │   │   │                               ├── repository/
│       │   │   │                               │   └── ProfileRepository.kt
│       │   │   │                               ├── usecase/
│       │   │   │                               │   ├── GetProfileUseCase.kt
│       │   │   │                               │   ├── LogoutUseCase.kt
│       │   │   │                               │   ├── UpdateNameUseCase.kt
│       │   │   │                               │   └── UpdateProfilePhotoUseCase.kt
│       │   │   │                               └── ProfilePhotoStorage.kt
│       │   │   └── test/
│       │   │       └── kotlin/
│       │   │           └── ir/
│       │   │               └── neobank/
│       │   │                   └── ariapay/
│       │   │                       └── feature/
│       │   │                           └── profile/
│       │   │                               └── domain/
│       │   │                                   └── ProfileUseCasesTest.kt
│       │   └── build.gradle.kts
│       └── presentation/
│           ├── src/
│           │   └── main/
│           │       └── kotlin/
│           │           └── ir/
│           │               └── neobank/
│           │                   └── ariapay/
│           │                       └── feature/
│           │                           └── profile/
│           │                               ├── contract/
│           │                               │   ├── ProfileEffect.kt
│           │                               │   ├── ProfileEvent.kt
│           │                               │   └── ProfileUiState.kt
│           │                               ├── di/
│           │                               │   └── ProfileUseCaseModule.kt
│           │                               ├── ProfileScreen.kt
│           │                               └── ProfileViewModel.kt
│           └── build.gradle.kts
├── gradle/
│   ├── wrapper/
│   │   ├── gradle-wrapper.jar
│   │   └── gradle-wrapper.properties
│   ├── gradle-daemon-jvm.properties
│   └── libs.versions.toml
├── .gitignore
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── README.md
└── settings.gradle.kts
```

</details>

### برنامهٔ ادامهٔ توسعه

<small><i>موارد زیر هنوز در مخزن پیاده‌سازی نشده‌اند و برای مراحل آینده در نظر گرفته شده‌اند.</i></small>

<small><i>◻️ تکمیل Home به داشبورد واقعی‌تر با کارت‌ها، حساب‌ها و میانبرهای قابل استفاده.</i></small>

<small><i>◻️ افزودن backend واقعی و ماژول شبکه، مدیریت خطاهای API، احراز هویت سرور و پیکربندی محیط‌های توسعه و تولید.</i></small>

<small><i>◻️ توسعهٔ featureهای کیف پول، کارت‌ها، انتقال وجه، پرداخت قبض، شارژ سیم‌کارت، تراکنش‌ها و رسیدها.</i></small>

<small><i>◻️ همگام‌سازی آفلاین و عملیات پس‌زمینه با WorkManager، همراه با سیاست retry و ثبت وضعیت تراکنش.</i></small>

<small><i>◻️ گسترش تست‌ها: پوشش بیشتر ViewModel و UI، تست‌های end-to-end، گزارش coverage و بررسی پایداری workflowهای CI.</i></small>

<small><i>◻️ ارزیابی KMP برای اشتراک‌گذاری بخش‌های مناسب domain و data در آینده؛ نسخهٔ فعلی Android و UI آن مبتنی بر Compose است.</i></small>

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
