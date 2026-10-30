package com.aksh.emicalculator.data

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String
) {
    HINDI("hi", "हिंदी", "Hindi"),
    ENGLISH("en", "English", "English"),
    CHHATTISGARHI("hne", "छत्तीसगढ़ी", "Chhattisgarhi"),
    MARATHI("mr", "मराठी", "Marathi"),
    GUJARATI("gu", "ગુજરાતી", "Gujarati"),
    BENGALI("bn", "বাংলা", "Bengali"),
    PUNJABI("pa", "ਪੰਜਾਬੀ", "Punjabi"),
    TAMIL("ta", "தமிழ்", "Tamil"),
    TELUGU("te", "తెలుగు", "Telugu"),
    KANNADA("kn", "ಕನ್ನಡ", "Kannada"),
    MALAYALAM("ml", "മലയാളം", "Malayalam"),
    ODIA("or", "ଓଡ଼ିଆ", "Odia")
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AccentColor(val colorName: String, val colorValue: Long) {
    ROYAL_BLUE("Royal Blue", 0xFF3B82F6),
    PARROT_GREEN("Parrot Green", 0xFF22C55E),
    CRIMSON_RED("Crimson Red", 0xFFEF4444),
    GOLD("Gold", 0xFFFBBF24)
}

object Strings {
    // Nav & Tabs
    const val TAB_CALCULATOR = "tab_calculator"
    const val TAB_COMPARE = "tab_compare"
    const val TAB_PREPAYMENT = "tab_prepayment"
    const val TAB_SCHEDULE = "tab_schedule"

    // Headers & Labels
    const val APP_TITLE = "app_title"
    const val LOAN_AMOUNT = "loan_amount"
    const val INTEREST_RATE = "interest_rate"
    const val LOAN_TENURE = "loan_tenure"
    const val YEARS = "years"
    const val MONTHS = "months"
    const val MONTHLY_EMI = "monthly_emi"
    const val TOTAL_INTEREST = "total_interest"
    const val TOTAL_PAYMENT = "total_payment"
    const val PRINCIPAL_AMOUNT = "principal_amount"

    // Loan Types
    const val HOME_LOAN = "home_loan"
    const val PERSONAL_LOAN = "personal_loan"
    const val CAR_LOAN = "car_loan"
    const val EDUCATION_LOAN = "education_loan"

    // Action Buttons
    const val DOWNLOAD_PDF = "download_pdf"
    const val SHARE_BTN = "share_btn"

    // Comparison Screen
    const val COMPARE_TITLE = "compare_title"
    const val LOAN_A = "loan_a"
    const val LOAN_B = "loan_b"
    const val BEST_DEAL = "best_deal"
    const val YOU_SAVE = "you_save"
    const val DIFFERENCE_MONTHLY = "difference_monthly"
    const val DIFFERENCE_INTEREST = "difference_interest"
    const val SELECT_BANK_PRESET = "select_bank_preset"
    const val BANK_SBI = "bank_sbi"
    const val BANK_HDFC = "bank_hdfc"
    const val BANK_ICICI = "bank_icici"
    const val BANK_AXIS = "bank_axis"
    const val BANK_PNB = "bank_pnb"
    const val SELECT_BANK = "select_bank"
    const val OTHER_BANK = "other_bank"
    const val ENTER_BANK_NAME = "enter_bank_name"

    // Prepayment Screen
    const val PREPAYMENT_TITLE = "prepayment_title"
    const val ORIGINAL_LOAN_AMOUNT = "original_loan_amount"
    const val ORIGINAL_LOAN_TENURE = "original_loan_tenure"
    const val EMIS_ALREADY_PAID = "emis_already_paid"
    const val REMAINING_BALANCE_BEFORE = "remaining_balance_before"
    const val EXTRA_PAYMENT = "extra_payment"
    const val PREPAYMENT_TYPE = "prepayment_type"
    const val ONE_TIME = "one_time"
    const val MONTHLY_EXTRA = "monthly_extra"
    const val SAVINGS_RESULT = "savings_result"
    const val TENURE_REDUCED_BY = "tenure_reduced_by"
    const val TOTAL_INTEREST_SAVED = "total_interest_saved"
    const val PRINCIPAL_WITHOUT_INTEREST = "principal_without_interest"

    // Amortization Screen
    const val SCHEDULE_TITLE = "schedule_title"
    const val YEARLY_VIEW = "yearly_view"
    const val MONTHLY_VIEW = "monthly_view"
    const val YEAR = "year"
    const val BALANCE = "balance"
    const val SHARE_SCHEDULE = "share_schedule"
    const val COPIED_TO_CLIPBOARD = "copied_to_clipboard"

    // Language & Theme Modals
    const val CHOOSE_LANGUAGE = "choose_language"
    const val CONTINUE_BTN = "continue_btn"
    const val CHOOSE_THEME = "choose_theme"
    const val THEME_SYSTEM = "theme_system"
    const val THEME_DARK = "theme_dark"
    const val THEME_LIGHT = "theme_light"

    // Ad Banner
    const val SPONSORED_AD = "sponsored_ad"
    const val BEST_RATES_BANNER = "best_rates_banner"
    const val CHECK_ELIGIBILITY = "check_eligibility"
}

object TranslationRepository {
    private val translations = mapOf(
        AppLanguage.HINDI to mapOf(
            Strings.APP_TITLE to "ईएमआई कैलकुलेटर",
            Strings.TAB_CALCULATOR to "ईएमआई",
            Strings.TAB_COMPARE to "तुलना करें",
            Strings.TAB_PREPAYMENT to "प्री-पेमेंट",
            Strings.TAB_SCHEDULE to "शेड्यूल",

            Strings.LOAN_AMOUNT to "ऋण राशि (Loan Amount)",
            Strings.INTEREST_RATE to "ब्याज दर (% प्रति वर्ष)",
            Strings.LOAN_TENURE to "अवधि (Tenure)",
            Strings.YEARS to "वर्ष",
            Strings.MONTHS to "महीने",
            Strings.MONTHLY_EMI to "मासिक ईएमआई",
            Strings.TOTAL_INTEREST to "कुल ब्याज",
            Strings.TOTAL_PAYMENT to "कुल भुगतान",
            Strings.PRINCIPAL_AMOUNT to "मूलधन",

            Strings.HOME_LOAN to "होम लोन",
            Strings.PERSONAL_LOAN to "पर्सनल लोन",
            Strings.CAR_LOAN to "कार लोन",
            Strings.EDUCATION_LOAN to "शिक्षा लोन",

            Strings.DOWNLOAD_PDF to "PDF डाउनलोड",
            Strings.SHARE_BTN to "शेयर करें",

            Strings.COMPARE_TITLE to "लोन ऑफर की तुलना करें",
            Strings.LOAN_A to "ऑफर A (बैंक 1)",
            Strings.LOAN_B to "ऑफर B (बैंक 2)",
            Strings.BEST_DEAL to "सर्वोत्तम ऑफर",
            Strings.YOU_SAVE to "आपकी बचत",
            Strings.DIFFERENCE_MONTHLY to "मासिक ईएमआई अंतर",
            Strings.DIFFERENCE_INTEREST to "कुल ब्याज अंतर",
            Strings.SELECT_BANK_PRESET to "बैंक ब्याज दरें चुनें",

            Strings.PREPAYMENT_TITLE to "प्री-पेमेंट लाभ कैलकुलेटर",
            Strings.EXTRA_PAYMENT to "अतिरिक्त भुगतान राशि",
            Strings.PREPAYMENT_TYPE to "भुगतान प्रकार",
            Strings.ONE_TIME to "एकमुश्त भुगतान",
            Strings.MONTHLY_EXTRA to "मासिक अतिरिक्त",
            Strings.SAVINGS_RESULT to "आपकी कुल बचत",
            Strings.TENURE_REDUCED_BY to "कम हुई लोन अवधि",
            Strings.TOTAL_INTEREST_SAVED to "बचाया गया कुल ब्याज",

            Strings.SCHEDULE_TITLE to "लोन भुगतान शेड्यूल",
            Strings.YEARLY_VIEW to "वार्षिक विवरण",
            Strings.MONTHLY_VIEW to "मासिक विवरण",
            Strings.YEAR to "वर्ष",
            Strings.BALANCE to "बकाया राशि",
            Strings.SHARE_SCHEDULE to "शेयर रिपोर्ट",
            Strings.COPIED_TO_CLIPBOARD to "कॉपी हो गया",

            Strings.CHOOSE_LANGUAGE to "भाषा चुनें",
            Strings.CONTINUE_BTN to "आगे बढ़ें",
            Strings.CHOOSE_THEME to "थीम चुनें",
            Strings.THEME_SYSTEM to "सिस्टम डिफॉल्ट",
            Strings.THEME_DARK to "ऑब्सीडियन डार्क",
            Strings.THEME_LIGHT to "स्लेट लाइट",

            Strings.SPONSORED_AD to "स्पॉन्सर्ड पार्टनर",
            Strings.BEST_RATES_BANNER to "होम लोन न्यूनतम ब्याज दर 8.35%* से शुरू",
            Strings.CHECK_ELIGIBILITY to "मुफ्त जांचें"
        ),

        AppLanguage.ENGLISH to mapOf(
            Strings.APP_TITLE to "EMI Calculator",
            Strings.TAB_CALCULATOR to "EMI Calc",
            Strings.TAB_COMPARE to "Compare",
            Strings.TAB_PREPAYMENT to "Prepayment",
            Strings.TAB_SCHEDULE to "Schedule",

            Strings.LOAN_AMOUNT to "Loan Amount",
            Strings.INTEREST_RATE to "Interest Rate (% p.a.)",
            Strings.LOAN_TENURE to "Tenure",
            Strings.YEARS to "Years",
            Strings.MONTHS to "Months",
            Strings.MONTHLY_EMI to "Monthly EMI",
            Strings.TOTAL_INTEREST to "Total Interest",
            Strings.TOTAL_PAYMENT to "Total Payment",
            Strings.PRINCIPAL_AMOUNT to "Principal",

            Strings.HOME_LOAN to "Home Loan",
            Strings.PERSONAL_LOAN to "Personal",
            Strings.CAR_LOAN to "Car Loan",
            Strings.EDUCATION_LOAN to "Education",

            Strings.DOWNLOAD_PDF to "PDF Download",
            Strings.SHARE_BTN to "Share",

            Strings.COMPARE_TITLE to "Compare Loan Offers",
            Strings.LOAN_A to "Offer A",
            Strings.LOAN_B to "Offer B",
            Strings.BEST_DEAL to "Best Offer",
            Strings.YOU_SAVE to "You Save",
            Strings.DIFFERENCE_MONTHLY to "Monthly EMI Difference",
            Strings.DIFFERENCE_INTEREST to "Total Interest Difference",
            Strings.SELECT_BANK_PRESET to "Load Bank Preset Rates",

            Strings.PREPAYMENT_TITLE to "Prepayment Impact",
            Strings.EXTRA_PAYMENT to "Part Payment Amount",
            Strings.PREPAYMENT_TYPE to "Payment Frequency",
            Strings.ONE_TIME to "One-Time Payment",
            Strings.MONTHLY_EXTRA to "Extra Monthly",
            Strings.SAVINGS_RESULT to "Your Potential Savings",
            Strings.TENURE_REDUCED_BY to "Tenure Reduced By",
            Strings.TOTAL_INTEREST_SAVED to "Total Interest Saved",

            Strings.SCHEDULE_TITLE to "Repayment Schedule",
            Strings.YEARLY_VIEW to "Yearly Summary",
            Strings.MONTHLY_VIEW to "Monthly Breakdown",
            Strings.YEAR to "Year",
            Strings.BALANCE to "Remaining Balance",
            Strings.SHARE_SCHEDULE to "Share Summary",
            Strings.COPIED_TO_CLIPBOARD to "Summary copied to clipboard",

            Strings.CHOOSE_LANGUAGE to "Select Language",
            Strings.CONTINUE_BTN to "Continue",
            Strings.CHOOSE_THEME to "Choose Theme",
            Strings.THEME_SYSTEM to "System Default",
            Strings.THEME_DARK to "Obsidian Dark",
            Strings.THEME_LIGHT to "Slate Light",

            Strings.SPONSORED_AD to "Sponsored Partner",
            Strings.BEST_RATES_BANNER to "Lowest Home Loan Interest starting at 8.35%*",
            Strings.CHECK_ELIGIBILITY to "Check Free"
        ),

        AppLanguage.CHHATTISGARHI to mapOf(
            Strings.APP_TITLE to "ईएमआई कैलकुलेटर",
            Strings.TAB_CALCULATOR to "ईएमआई",
            Strings.TAB_COMPARE to "मिलावव",
            Strings.TAB_PREPAYMENT to "पहिले पटावव",
            Strings.TAB_SCHEDULE to "हिसाब-किताब",

            Strings.LOAN_AMOUNT to "करजा के रकम",
            Strings.INTEREST_RATE to "ब्याज दर (% हर साल)",
            Strings.LOAN_TENURE to "समय (दिन-महीना)",
            Strings.YEARS to "बछर",
            Strings.MONTHS to "महीना",
            Strings.MONTHLY_EMI to "महीना के ईएमआई",
            Strings.TOTAL_INTEREST to "कुल ब्याज",
            Strings.TOTAL_PAYMENT to "कुल पटाए रकम",
            Strings.PRINCIPAL_AMOUNT to "मूलधन",

            Strings.HOME_LOAN to "घर लोन",
            Strings.PERSONAL_LOAN to "निजी लोन",
            Strings.CAR_LOAN to "गाड़ी लोन",
            Strings.EDUCATION_LOAN to "पढ़ई लोन",

            Strings.DOWNLOAD_PDF to "PDF डाउनलोड",
            Strings.SHARE_BTN to "शेयर करव",

            Strings.COMPARE_TITLE to "लोन ऑफर ला मिलावव",
            Strings.LOAN_A to "ऑफर A",
            Strings.LOAN_B to "ऑफर B",
            Strings.BEST_DEAL to "सबले बने ऑफर",
            Strings.YOU_SAVE to "तुंहर बचत",
            Strings.DIFFERENCE_MONTHLY to "महीना के बचत अंतर",
            Strings.DIFFERENCE_INTEREST to "कुल ब्याज में अंतर",
            Strings.SELECT_BANK_PRESET to "बैंक ब्याज दर चुनव",

            Strings.PREPAYMENT_TITLE to "पहिले पटाए के फायदा",
            Strings.EXTRA_PAYMENT to "अतिरिक्त पटाए रकम",
            Strings.PREPAYMENT_TYPE to "पटाए के नियम",
            Strings.ONE_TIME to "एक घांव में",
            Strings.MONTHLY_EXTRA to "हर महीना जादा",
            Strings.SAVINGS_RESULT to "तुंहर कुल बचत",
            Strings.TENURE_REDUCED_BY to "कमती होइस समय",
            Strings.TOTAL_INTEREST_SAVED to "बांचे ब्याज रकम",

            Strings.SCHEDULE_TITLE to "लोन पटौती सूची",
            Strings.YEARLY_VIEW to "सालाना हिसाब",
            Strings.MONTHLY_VIEW to "महीना हिसाब",
            Strings.YEAR to "बछर",
            Strings.BALANCE to "बांचे रकम",
            Strings.SHARE_SCHEDULE to "शेयर करव",
            Strings.COPIED_TO_CLIPBOARD to "कॉपी होगे",

            Strings.CHOOSE_LANGUAGE to "अपन भाखा चुनव",
            Strings.CONTINUE_BTN to "आगे बढ़व",
            Strings.CHOOSE_THEME to "रंग-रूप चुनव",
            Strings.THEME_SYSTEM to "सिस्टम जैसन",
            Strings.THEME_DARK to "डार्क मोड",
            Strings.THEME_LIGHT to "लाइट मोड",

            Strings.SPONSORED_AD to "प्रायोजित पार्टनर",
            Strings.BEST_RATES_BANNER to "सस्ता होम लोन 8.35%* ले सुरू",
            Strings.CHECK_ELIGIBILITY to "जांच करव"
        ),

        AppLanguage.MARATHI to mapOf(
            Strings.APP_TITLE to "ईएमआय कॅल्क्युलेटर",
            Strings.TAB_CALCULATOR to "ईएमआय",
            Strings.TAB_COMPARE to "तुलना करा",
            Strings.TAB_PREPAYMENT to "प्री-पेमेंट",
            Strings.TAB_SCHEDULE to "शेड्यूल",

            Strings.LOAN_AMOUNT to "कर्ज रक्कम",
            Strings.INTEREST_RATE to "व्याज दर (% प्रति वर्ष)",
            Strings.LOAN_TENURE to "मुदत",
            Strings.YEARS to "वर्षे",
            Strings.MONTHS to "महिने",
            Strings.MONTHLY_EMI to "मासिक ईएमआय",
            Strings.TOTAL_INTEREST to "एकूण व्याज",
            Strings.TOTAL_PAYMENT to "एकूण परतफेड",
            Strings.PRINCIPAL_AMOUNT to "मुद्दल",

            Strings.HOME_LOAN to "गृह कर्ज",
            Strings.PERSONAL_LOAN to "वैयक्तिक कर्ज",
            Strings.CAR_LOAN to "कार कर्ज",
            Strings.EDUCATION_LOAN to "शिक्षण कर्ज",

            Strings.DOWNLOAD_PDF to "PDF डाउनलोड",
            Strings.SHARE_BTN to "शेअर करा",

            Strings.COMPARE_TITLE to "कर्ज ऑफर्सची तुलना करा",
            Strings.LOAN_A to "ऑफर A",
            Strings.LOAN_B to "ऑफर B",
            Strings.BEST_DEAL to "सर्वोत्कृष्ट ऑफर",
            Strings.YOU_SAVE to "तुमची बचत",
            Strings.DIFFERENCE_MONTHLY to "मासिक फरक",
            Strings.DIFFERENCE_INTEREST to "एकूण व्याज फरक",
            Strings.SELECT_BANK_PRESET to "बँकेचे व्याजदर निवडा",

            Strings.PREPAYMENT_TITLE to "प्री-पेमेंट बचत",
            Strings.EXTRA_PAYMENT to "अतिरिक्त रक्कम",
            Strings.PREPAYMENT_TYPE to "पेमेंट प्रकार",
            Strings.ONE_TIME to "एकदाच",
            Strings.MONTHLY_EXTRA to "मासिक अतिरिक्त",
            Strings.SAVINGS_RESULT to "तुमची बचत",
            Strings.TENURE_REDUCED_BY to "कमी झालेला कालावधी",
            Strings.TOTAL_INTEREST_SAVED to "वाचलेले एकूण व्याज",

            Strings.SCHEDULE_TITLE to "परतफेड तक्ता",
            Strings.YEARLY_VIEW to "वार्षिक सारांश",
            Strings.MONTHLY_VIEW to "मासिक तपशील",
            Strings.YEAR to "वर्ष",
            Strings.BALANCE to "शिल्लक मुद्दल",
            Strings.SHARE_SCHEDULE to "शेअर करा",
            Strings.COPIED_TO_CLIPBOARD to "कॉपी झाले",

            Strings.CHOOSE_LANGUAGE to "भाषा निवडा",
            Strings.CONTINUE_BTN to "पुढे जा",
            Strings.CHOOSE_THEME to "थीम निवडा",
            Strings.THEME_SYSTEM to "सिस्टम प्रमाणे",
            Strings.THEME_DARK to "डार्क मोड",
            Strings.THEME_LIGHT to "लाइट मोड",

            Strings.SPONSORED_AD to "प्रायोजित",
            Strings.BEST_RATES_BANNER to "सर्वात कमी गृहकर्ज दर 8.35%* पासून",
            Strings.CHECK_ELIGIBILITY to "पात्रता तपासा"
        ),

        AppLanguage.GUJARATI to mapOf(
            Strings.APP_TITLE to "ઇએમઆઇ કેલ્ક્યુલેટર",
            Strings.TAB_CALCULATOR to "ઇએમઆઇ",
            Strings.TAB_COMPARE to "સરખામણી",
            Strings.TAB_PREPAYMENT to "પ્રી-પેમેન્ટ",
            Strings.TAB_SCHEDULE to "શેડ્યૂલ",

            Strings.LOAN_AMOUNT to "લોનની રકમ",
            Strings.INTEREST_RATE to "વ્યાજ દર (% વાર્ષિક)",
            Strings.LOAN_TENURE to "મુદ્દત",
            Strings.YEARS to "વર્ષ",
            Strings.MONTHS to "મહિના",
            Strings.MONTHLY_EMI to "માસિક ઇએમઆઇ",
            Strings.TOTAL_INTEREST to "કુલ વ્યાજ",
            Strings.TOTAL_PAYMENT to "કુલ ચૂકવણી",
            Strings.PRINCIPAL_AMOUNT to "મુદ્દલ રકમ",

            Strings.HOME_LOAN to "હોમ લોન",
            Strings.PERSONAL_LOAN to "પર્સનલ લોન",
            Strings.CAR_LOAN to "કાર લોન",
            Strings.EDUCATION_LOAN to "એજ્યુકેશન લોન",

            Strings.DOWNLOAD_PDF to "PDF ડાઉનલોડ",
            Strings.SHARE_BTN to "શેર કરો",

            Strings.COMPARE_TITLE to "લોન ઑફર્સની સરખામણી",
            Strings.LOAN_A to "ઑફર A",
            Strings.LOAN_B to "ઑફર B",
            Strings.BEST_DEAL to "શ્રેષ્ઠ ઑફર",
            Strings.YOU_SAVE to "તમારી બચત",
            Strings.DIFFERENCE_MONTHLY to "માસિક તફાવત",
            Strings.DIFFERENCE_INTEREST to "કુલ વ્યાજ તફાવત",
            Strings.SELECT_BANK_PRESET to "બેંક વ્યાજ દર પસંદ કરો",

            Strings.PREPAYMENT_TITLE to "પ્રી-પેમેન્ટ બચત",
            Strings.EXTRA_PAYMENT to "વધારાની રકમ",
            Strings.PREPAYMENT_TYPE to "ચૂકવણીનો પ્રકાર",
            Strings.ONE_TIME to "એકવાર",
            Strings.MONTHLY_EXTRA to "દર મહિને વધારાનું",
            Strings.SAVINGS_RESULT to "કુલ બચત",
            Strings.TENURE_REDUCED_BY to "ઘટેલી મુદ્દત",
            Strings.TOTAL_INTEREST_SAVED to "બચાવેલ વ્યાજ",

            Strings.SCHEDULE_TITLE to "ચૂકવણી શેડ્યૂલ",
            Strings.YEARLY_VIEW to "વાર્ષિક વિગત",
            Strings.MONTHLY_VIEW to "માસિક વિગત",
            Strings.YEAR to "વર્ષ",
            Strings.BALANCE to "બાકી રકમ",
            Strings.SHARE_SCHEDULE to "શેર કરો",
            Strings.COPIED_TO_CLIPBOARD to "કોપી થયું",

            Strings.CHOOSE_LANGUAGE to "ભાષા પસંદ કરો",
            Strings.CONTINUE_BTN to "આગળ વધો",
            Strings.CHOOSE_THEME to "થીમ પસંદ કરો",
            Strings.THEME_SYSTEM to "સિસ્ટમ મુજબ",
            Strings.THEME_DARK to "ડાર્ક મોડ",
            Strings.THEME_LIGHT to "લાઇટ મોડ",

            Strings.SPONSORED_AD to "સ્પોન્સર્ડ પાર્ટનર",
            Strings.BEST_RATES_BANNER to "હોમ લોન ન્યૂનતમ વ્યાજ દર 8.35%* થી શરૂ",
            Strings.CHECK_ELIGIBILITY to "તપાસો"
        ),

        AppLanguage.BENGALI to mapOf(
            Strings.APP_TITLE to "ইএমআই ক্যালকুলেটর",
            Strings.TAB_CALCULATOR to "ইএমআই",
            Strings.TAB_COMPARE to "তুলনা",
            Strings.TAB_PREPAYMENT to "প্রি-পেমেন্ট",
            Strings.TAB_SCHEDULE to "তালিকা",

            Strings.LOAN_AMOUNT to "ঋণের পরিমাণ",
            Strings.INTEREST_RATE to "সুদের হার (%)",
            Strings.LOAN_TENURE to "সময়কাল",
            Strings.YEARS to "বছর",
            Strings.MONTHS to "মাস",
            Strings.MONTHLY_EMI to "মাসিক ইএমআই",
            Strings.TOTAL_INTEREST to "মোট সুদ",
            Strings.TOTAL_PAYMENT to "মোট পরিশোধ",
            Strings.PRINCIPAL_AMOUNT to "আসল টাকা",

            Strings.HOME_LOAN to "গৃহ ঋণ",
            Strings.PERSONAL_LOAN to "ব্যক্তিগত ঋণ",
            Strings.CAR_LOAN to "গাড়ি ঋণ",
            Strings.EDUCATION_LOAN to "শিক্ষা ঋণ",

            Strings.DOWNLOAD_PDF to "PDF ডাউনলোড",
            Strings.SHARE_BTN to "শেয়ার করুন",

            Strings.COMPARE_TITLE to "লোন অফারের তুলনা",
            Strings.LOAN_A to "অফার A",
            Strings.LOAN_B to "অফার B",
            Strings.BEST_DEAL to "সেরা অফার",
            Strings.YOU_SAVE to "আপনার সঞ্চয়",
            Strings.DIFFERENCE_MONTHLY to "মাসিক তফাত",
            Strings.DIFFERENCE_INTEREST to "মোট সুদের তফাত",
            Strings.SELECT_BANK_PRESET to "ব্যাঙ্ক সুদের হার",

            Strings.PREPAYMENT_TITLE to "প্রি-পেমেন্ট সুবিধা",
            Strings.EXTRA_PAYMENT to "অতিরিক্ত পরিশোধ",
            Strings.PREPAYMENT_TYPE to "পরিশোধের ধরন",
            Strings.ONE_TIME to "এককালীন",
            Strings.MONTHLY_EXTRA to "প্রতি মাসে অতিরিক্ত",
            Strings.SAVINGS_RESULT to "আপনার মোট সাশ্রয়",
            Strings.TENURE_REDUCED_BY to "সময়কাল কমেছে",
            Strings.TOTAL_INTEREST_SAVED to "সাশ্রয় হওয়া মোট সুদ",

            Strings.SCHEDULE_TITLE to "ঋণ পরিশোধের তালিকা",
            Strings.YEARLY_VIEW to "বার্ষিক বিবরণ",
            Strings.MONTHLY_VIEW to "মাসিক বিবরণ",
            Strings.YEAR to "বছর",
            Strings.BALANCE to "বাকি টাকা",
            Strings.SHARE_SCHEDULE to "শেয়ার করুন",
            Strings.COPIED_TO_CLIPBOARD to "কপি করা হয়েছে",

            Strings.CHOOSE_LANGUAGE to "ভাষা নির্বাচন করুন",
            Strings.CONTINUE_BTN to "এগিয়ে যান",
            Strings.CHOOSE_THEME to "থিম নির্বাচন",
            Strings.THEME_SYSTEM to "সিস্টেম ডিফল্ট",
            Strings.THEME_DARK to "ডার্ক মোড",
            Strings.THEME_LIGHT to "লাইট মোড",

            Strings.SPONSORED_AD to "বিজ্ঞাপন",
            Strings.BEST_RATES_BANNER to "সবচেয়ে কম সুদে হোম লোন 8.35%* থেকে",
            Strings.CHECK_ELIGIBILITY to "চেক করুন"
        ),

        AppLanguage.PUNJABI to mapOf(
            Strings.APP_TITLE to "EMI ਕੈਲਕੁਲੇਟਰ",
            Strings.TAB_CALCULATOR to "EMI ਕੈਲਕ",
            Strings.TAB_COMPARE to "ਮੁਕਾਬਲਾ",
            Strings.TAB_PREPAYMENT to "ਪ੍ਰੀ-ਪੇਮੈਂਟ",
            Strings.TAB_SCHEDULE to "ਸੂਚੀ",

            Strings.LOAN_AMOUNT to "ਕਰਜ਼ਾ ਰਕਮ",
            Strings.INTEREST_RATE to "ਵਿਆਜ ਦਰ (% ਸਾਲਾਨਾ)",
            Strings.LOAN_TENURE to "ਮਿਆਦ",
            Strings.YEARS to "ਸਾਲ",
            Strings.MONTHS to "ਮਹੀਨੇ",
            Strings.MONTHLY_EMI to "ਮਹੀਨਾਵਾਰ EMI",
            Strings.TOTAL_INTEREST to "ਕੁੱਲ ਵਿਆਜ",
            Strings.TOTAL_PAYMENT to "ਕੁੱਲ ਭੁਗਤਾਨ",
            Strings.PRINCIPAL_AMOUNT to "ਮੂਲ ਰਕਮ",

            Strings.HOME_LOAN to "ਘਰ ਦਾ ਕਰਜ਼ਾ",
            Strings.PERSONAL_LOAN to "ਨਿੱਜੀ ਕਰਜ਼ਾ",
            Strings.CAR_LOAN to "ਕਾਰ ਕਰਜ਼ਾ",
            Strings.EDUCATION_LOAN to "ਪੜ੍ਹਾਈ ਕਰਜ਼ਾ",

            Strings.DOWNLOAD_PDF to "PDF ਡਾਊਨਲੋਡ",
            Strings.SHARE_BTN to "ਸ਼ੇਅਰ ਕਰੋ",

            Strings.COMPARE_TITLE to "ਕਰਜ਼ਾ ਆਫਰਾਂ ਦੀ ਤੁਲਨਾ ਕਰੋ",
            Strings.LOAN_A to "ਆਫਰ A",
            Strings.LOAN_B to "ਆਫਰ B",
            Strings.BEST_DEAL to "ਸਭ ਤੋਂ ਵਧੀਆ",
            Strings.YOU_SAVE to "ਤੁਹਾਡੀ ਬੱਚਤ",
            Strings.DIFFERENCE_MONTHLY to "ਮਹੀਨਾਵਾਰ ਫ਼ਰਕ",
            Strings.DIFFERENCE_INTEREST to "ਕੁੱਲ ਵਿਆਜ ਫ਼ਰਕ",
            Strings.SELECT_BANK_PRESET to "ਬੈਂਕ ਵਿਆਜ ਦਰਾਂ ਚੁਣੋ",

            Strings.PREPAYMENT_TITLE to "ਪ੍ਰੀ-ਪੇਮੈਂਟ ਬੱਚਤ",
            Strings.EXTRA_PAYMENT to "ਵਾਧੂ ਰਕਮ",
            Strings.PREPAYMENT_TYPE to "ਭੁਗਤਾਨ ਦਾ ਢੰਗ",
            Strings.ONE_TIME to "ਇੱਕ ਵਾਰ",
            Strings.MONTHLY_EXTRA to "ਮਹੀਨਾਵਾਰ ਵਾਧੂ",
            Strings.SAVINGS_RESULT to "ਤੁਹਾਡੀ ਬੱਚਤ",
            Strings.TENURE_REDUCED_BY to "ਘਟੀ ਹੋਈ ਮਿਆਦ",
            Strings.TOTAL_INTEREST_SAVED to "ਬਚਾਇਆ ਵਿਆਜ",

            Strings.SCHEDULE_TITLE to "ਭੁਗਤਾਨ ਵੇਰਵਾ",
            Strings.YEARLY_VIEW to "ਸਾਲਾਨਾ ਵੇਰਵਾ",
            Strings.MONTHLY_VIEW to "ਮਹੀਨਾਵਾਰ ਵੇਰਵਾ",
            Strings.YEAR to "ਸਾਲ",
            Strings.BALANCE to "ਬਾਕੀ ਰਕਮ",
            Strings.SHARE_SCHEDULE to "ਸ਼ੇਅਰ ਕਰੋ",
            Strings.COPIED_TO_CLIPBOARD to "ਕਾਪੀ ਹੋ ਗਿਆ",

            Strings.CHOOSE_LANGUAGE to "ਭਾਸ਼ਾ ਚੁਣੋ",
            Strings.CONTINUE_BTN to "ਅੱਗੇ ਵਧੋ",
            Strings.CHOOSE_THEME to "ਥੀਮ ਚੁਣੋ",
            Strings.THEME_SYSTEM to "ਸਿਸਟਮ ਅਨੁਸਾਰ",
            Strings.THEME_DARK to "ਡਾਰਕ ਮੋਡ",
            Strings.THEME_LIGHT to "ਲਾਈਟ ਮੋਡ",

            Strings.SPONSORED_AD to "ਸਪਾਂਸਰਡ ਪਾਰਟਨਰ",
            Strings.BEST_RATES_BANNER to "ਸਭ ਤੋਂ ਘੱਟ ਵਿਆਜ ਦਰ 8.35%* ਤੋਂ ਸ਼ੁਰੂ",
            Strings.CHECK_ELIGIBILITY to "ਜਾਂਚ ਕਰੋ"
        ),

        AppLanguage.TAMIL to mapOf(
            Strings.APP_TITLE to "இஎம்ஐ கால்குலேட்டர்",
            Strings.TAB_CALCULATOR to "இஎம்ஐ",
            Strings.TAB_COMPARE to "ஒப்பிடுக",
            Strings.TAB_PREPAYMENT to "முன்செலுத்தல்",
            Strings.TAB_SCHEDULE to "அட்டவணை",

            Strings.LOAN_AMOUNT to "கடன் தொகை",
            Strings.INTEREST_RATE to "வட்டி விகிதம் (%)",
            Strings.LOAN_TENURE to "கால அளவு",
            Strings.YEARS to "ஆண்டுகள்",
            Strings.MONTHS to "மாதங்கள்",
            Strings.MONTHLY_EMI to "மாத தவணை (EMI)",
            Strings.TOTAL_INTEREST to "மொத்த வட்டி",
            Strings.TOTAL_PAYMENT to "மொத்த தொகை",
            Strings.PRINCIPAL_AMOUNT to "அசல் தொகை",

            Strings.HOME_LOAN to "வீட்டுக் கடன்",
            Strings.PERSONAL_LOAN to "தனிநபர் கடன்",
            Strings.CAR_LOAN to "வாகன கடன்",
            Strings.EDUCATION_LOAN to "கல்வி கடன்",

            Strings.DOWNLOAD_PDF to "PDF பதிவிறக்கம்",
            Strings.SHARE_BTN to "பகிர்",

            Strings.COMPARE_TITLE to "கடன் ஒப்பிடல்",
            Strings.LOAN_A to "வங்கி A",
            Strings.LOAN_B to "வங்கி B",
            Strings.BEST_DEAL to "சிறந்த சலுகை",
            Strings.YOU_SAVE to "உங்கள் சேமிப்பு",
            Strings.DIFFERENCE_MONTHLY to "மாதாந்திர வித்தியாசம்",
            Strings.DIFFERENCE_INTEREST to "வட்டி வித்தியாசம்",
            Strings.SELECT_BANK_PRESET to "வங்கி வட்டி விகிதங்கள்",

            Strings.PREPAYMENT_TITLE to "முன்செலுத்தல் பலன்",
            Strings.EXTRA_PAYMENT to "கூடுதல் தொகை",
            Strings.PREPAYMENT_TYPE to "செலுத்தும் முறை",
            Strings.ONE_TIME to "ஒரே முறை",
            Strings.MONTHLY_EXTRA to "மாதாந்திர கூடுதல்",
            Strings.SAVINGS_RESULT to "சேமிப்பு கணக்கீடு",
            Strings.TENURE_REDUCED_BY to "குறைந்த கால அளவு",
            Strings.TOTAL_INTEREST_SAVED to "சேமிக்கப்பட்ட வட்டி",

            Strings.SCHEDULE_TITLE to "தவணை விவரம்",
            Strings.YEARLY_VIEW to "வருடாந்திர சுருக்கம்",
            Strings.MONTHLY_VIEW to "மாதாந்திர விவரம்",
            Strings.YEAR to "ஆண்டு",
            Strings.BALANCE to "மீதி தொகை",
            Strings.SHARE_SCHEDULE to "பகிர்",
            Strings.COPIED_TO_CLIPBOARD to "நகலெடுக்கப்பட்டது",

            Strings.CHOOSE_LANGUAGE to "மொழியைத் தேர்வு செய்க",
            Strings.CONTINUE_BTN to "தொடர்க",
            Strings.CHOOSE_THEME to "தீம் தேர்வு",
            Strings.THEME_SYSTEM to "இயல்புநிலை",
            Strings.THEME_DARK to "டார்க் மோட்",
            Strings.THEME_LIGHT to "லைட் மோட்",

            Strings.SPONSORED_AD to "விளம்பரம்",
            Strings.BEST_RATES_BANNER to "குறைந்த வட்டி வீட்டுக் கடன் 8.35%*",
            Strings.CHECK_ELIGIBILITY to "சரிபார்க்க"
        ),

        AppLanguage.TELUGU to mapOf(
            Strings.APP_TITLE to "ఇఎంఐ కాలిక్యులేటర్",
            Strings.TAB_CALCULATOR to "ఇఎంఐ",
            Strings.TAB_COMPARE to "పోల్చండి",
            Strings.TAB_PREPAYMENT to "ప్రీ-పేమెంట్",
            Strings.TAB_SCHEDULE to "షెడ్యూల్",

            Strings.LOAN_AMOUNT to "రుణ మొత్తం",
            Strings.INTEREST_RATE to "వడ్డీ రేటు (%)",
            Strings.LOAN_TENURE to "వ్యవధి",
            Strings.YEARS to "సంవత్సరాలు",
            Strings.MONTHS to "నెలలు",
            Strings.MONTHLY_EMI to "నెలవారీ ఇఎంఐ",
            Strings.TOTAL_INTEREST to "మొత్తం వడ్డీ",
            Strings.TOTAL_PAYMENT to "మొత్తం చెల్లింపు",
            Strings.PRINCIPAL_AMOUNT to "అసలు మొత్తం",

            Strings.HOME_LOAN to "హోమ్ లోన్",
            Strings.PERSONAL_LOAN to "పర్సనల్ లోన్",
            Strings.CAR_LOAN to "కార్ లోన్",
            Strings.EDUCATION_LOAN to "ఎడ్యుకేషన్ లోన్",

            Strings.DOWNLOAD_PDF to "PDF డౌన్‌లోడ్",
            Strings.SHARE_BTN to "షేర్ చేయండి",

            Strings.COMPARE_TITLE to "రుణ ఆఫర్ల పోలిక",
            Strings.LOAN_A to "ఆఫర్ A",
            Strings.LOAN_B to "ఆఫర్ B",
            Strings.BEST_DEAL to "ఉత్తమ ఆఫర్",
            Strings.YOU_SAVE to "మీ ఆదా",
            Strings.DIFFERENCE_MONTHLY to "నెలవారీ తేడా",
            Strings.DIFFERENCE_INTEREST to "వడ్డీ తేడా",
            Strings.SELECT_BANK_PRESET to "బ్యాంకు వడ్డీ రేట్లు",

            Strings.PREPAYMENT_TITLE to "ప్రీ-పేమెంట్ ఆదా",
            Strings.EXTRA_PAYMENT to "అదనపు చెల్లింపు",
            Strings.PREPAYMENT_TYPE to "చెల్లింపు పద్ధతి",
            Strings.ONE_TIME to "ఒకేసారి",
            Strings.MONTHLY_EXTRA to "ప్రతినెలా అదనంగా",
            Strings.SAVINGS_RESULT to "మీ ఆదా ఫలితం",
            Strings.TENURE_REDUCED_BY to "తగ్గిన వ్యవధి",
            Strings.TOTAL_INTEREST_SAVED to "ఆదా అయిన మొత్తం వడ్డీ",

            Strings.SCHEDULE_TITLE to "చెల్లింపుల వివరాలు",
            Strings.YEARLY_VIEW to "వార్షిక వివరాలు",
            Strings.MONTHLY_VIEW to "నెలవారీ వివరాలు",
            Strings.YEAR to "సంవత్సరం",
            Strings.BALANCE to "మిగిలిన మొత్తం",
            Strings.SHARE_SCHEDULE to "షేర్ చేయండి",
            Strings.COPIED_TO_CLIPBOARD to "కాపీ చేయబడింది",

            Strings.CHOOSE_LANGUAGE to "భాషను ఎంచుకోండి",
            Strings.CONTINUE_BTN to "కొనసాగించండి",
            Strings.CHOOSE_THEME to "థీమ్ ఎంపిక",
            Strings.THEME_SYSTEM to "సిస్టమ్ డిఫాల్ట్",
            Strings.THEME_DARK to "డార్క్ మోడ్",
            Strings.THEME_LIGHT to "లైట్ మోడ్",

            Strings.SPONSORED_AD to "ప్రాయోజిత భాగస్వామి",
            Strings.BEST_RATES_BANNER to "తక్కువ వడ్డీ హోమ్ లోన్ 8.35%* నుండి ప్రారంభం",
            Strings.CHECK_ELIGIBILITY to "పరిశీలించండి"
        ),

        AppLanguage.KANNADA to mapOf(
            Strings.APP_TITLE to "ಇಎಂಐ ಕ್ಯಾಲ್ಕುಲೇಟರ್",
            Strings.TAB_CALCULATOR to "ಇಎಂಐ",
            Strings.TAB_COMPARE to "ಹೋಲಿಕೆ",
            Strings.TAB_PREPAYMENT to "ಪೂರ್ವಪಾವತಿ",
            Strings.TAB_SCHEDULE to "ವೇಳಾಪಟ್ಟಿ",

            Strings.LOAN_AMOUNT to "ಸಾಲದ ಮೊತ್ತ",
            Strings.INTEREST_RATE to "ಬಡ್ಡಿ ದರ (%)",
            Strings.LOAN_TENURE to "ಅವಧಿ",
            Strings.YEARS to "ವರ್ಷಗಳು",
            Strings.MONTHS to "ತಿಂಗಳುಗಳು",
            Strings.MONTHLY_EMI to "ಮಾಸಿಕ ಇಎಂಐ",
            Strings.TOTAL_INTEREST to "ಒಟ್ಟು ಬಡ್ಡಿ",
            Strings.TOTAL_PAYMENT to "ಒಟ್ಟು ಪಾವತಿ",
            Strings.PRINCIPAL_AMOUNT to "ಅಸಲು ಮೊತ್ತ",

            Strings.HOME_LOAN to "ಗೃಹ ಸಾಲ",
            Strings.PERSONAL_LOAN to "ವೈಯಕ್ತಿಕ ಸಾಲ",
            Strings.CAR_LOAN to "ಕಾರು ಸಾಲ",
            Strings.EDUCATION_LOAN to "ಶಿಕ್ಷಣ ಸಾಲ",

            Strings.DOWNLOAD_PDF to "PDF ಡೌನ್‌ಲೋಡ್",
            Strings.SHARE_BTN to "ಹಂಚಿಕೊಳ್ಳಿ",

            Strings.COMPARE_TITLE to "ಸಾಲದ ಆಫರ್ ಹೋಲಿಕೆ",
            Strings.LOAN_A to "ಆಫರ್ A",
            Strings.LOAN_B to "ಆಫರ್ B",
            Strings.BEST_DEAL to "ಉತ್ತಮ ಆಫರ್",
            Strings.YOU_SAVE to "ನಿಮ್ಮ ಉಳಿತಾಯ",
            Strings.DIFFERENCE_MONTHLY to "ಮಾಸಿಕ ವ್ಯತ್ಯಾಸ",
            Strings.DIFFERENCE_INTEREST to "ಬಡ್ಡಿ ವ್ಯತ್ಯಾಸ",
            Strings.SELECT_BANK_PRESET to "ಬ್ಯಾಂಕ್ ಬಡ್ಡಿ ದರಗಳು",

            Strings.PREPAYMENT_TITLE to "ಪೂರ್ವಪಾವತಿ ಉಳಿತಾಯ",
            Strings.EXTRA_PAYMENT to "ಹೆಚ್ಚುವರಿ ಮೊತ್ತ",
            Strings.PREPAYMENT_TYPE to "ಪಾವತಿ ವಿಧಾನ",
            Strings.ONE_TIME to "ಒಮ್ಮೆ ಮಾತ್ರ",
            Strings.MONTHLY_EXTRA to "ತಿಂಗಳಿಗೆ ಹೆಚ್ಚುವರಿ",
            Strings.SAVINGS_RESULT to "ಉಳಿತಾಯ ಲೆಕ್ಕಾಚಾರ",
            Strings.TENURE_REDUCED_BY to "ಕಡಿಮೆಯಾದ ಅವಧಿ",
            Strings.TOTAL_INTEREST_SAVED to "ಉಳಿಸಿದ ಒಟ್ಟು ಬಡ್ಡಿ",

            Strings.SCHEDULE_TITLE to "ಮರುಪಾವತಿ ವಿವರ",
            Strings.YEARLY_VIEW to "ವಾರ್ಷಿಕ ವಿವರ",
            Strings.MONTHLY_VIEW to "ಮಾಸಿಕ ವಿವರ",
            Strings.YEAR to "ವರ್ಷ",
            Strings.BALANCE to "ಉಳಿದ ಮೊತ್ತ",
            Strings.SHARE_SCHEDULE to "ಹಂಚಿಕೊಳ್ಳಿ",
            Strings.COPIED_TO_CLIPBOARD to "ಕಾಪಿ ಮಾಡಲಾಗಿದೆ",

            Strings.CHOOSE_LANGUAGE to "ಭಾಷೆ ಆಯ್ಕೆಮಾಡಿ",
            Strings.CONTINUE_BTN to "ಮುಂದುವರಿಯಿರಿ",
            Strings.CHOOSE_THEME to "ಥೀಮ್ ಆಯ್ಕೆಮಾಡಿ",
            Strings.THEME_SYSTEM to "ಸಿಸ್ಟಮ್ ಡೀಫಾಲ್ಟ್",
            Strings.THEME_DARK to "ಡಾರ್ಕ್ ಮೋಡ್",
            Strings.THEME_LIGHT to "ಲೈಟ್ ಮೋಡ್",

            Strings.SPONSORED_AD to "ಪ್ರಾಯೋಜಿತ ಪಾಲುದಾರ",
            Strings.BEST_RATES_BANNER to "ಕಡಿಮೆ ಬಡ್ಡಿದರ ಗೃಹ ಸಾಲ 8.35%* ರಿಂದ",
            Strings.CHECK_ELIGIBILITY to "ಪರಿಶೀಲಿಸಿ"
        ),

        AppLanguage.MALAYALAM to mapOf(
            Strings.APP_TITLE to "ഇഎംഐ കാൽക്കുലേറ്റർ",
            Strings.TAB_CALCULATOR to "ഇഎംഐ",
            Strings.TAB_COMPARE to "താരതമ്യം",
            Strings.TAB_PREPAYMENT to "മുൻകൂർ തിരിച്ചടവ്",
            Strings.TAB_SCHEDULE to "പട്ടിക",

            Strings.LOAN_AMOUNT to "വായ്പ തുക",
            Strings.INTEREST_RATE to "പലിശ നിരക്ക് (%)",
            Strings.LOAN_TENURE to "കാലാവധി",
            Strings.YEARS to "വർഷങ്ങൾ",
            Strings.MONTHS to "മാസങ്ങൾ",
            Strings.MONTHLY_EMI to "പ്രതിമാസ ഇഎംഐ",
            Strings.TOTAL_INTEREST to "ആകെ പലിശ",
            Strings.TOTAL_PAYMENT to "ആകെ തിരിച്ചടവ്",
            Strings.PRINCIPAL_AMOUNT to "യഥാർത്ഥ തുക",

            Strings.HOME_LOAN to "ഭവന വായ്പ",
            Strings.PERSONAL_LOAN to "വ്യക്തിഗത വായ്പ",
            Strings.CAR_LOAN to "വാഹന വായ്പ",
            Strings.EDUCATION_LOAN to "വിദ്യാഭ്യാസ വായ്പ",

            Strings.DOWNLOAD_PDF to "PDF ഡൗൺലോഡ്",
            Strings.SHARE_BTN to "പങ്കിടുക",

            Strings.COMPARE_TITLE to "വായ്പ ഓഫർ താരതമ്യം",
            Strings.LOAN_A to "ഓഫർ A",
            Strings.LOAN_B to "ഓഫർ B",
            Strings.BEST_DEAL to "മികച്ച ഓഫർ",
            Strings.YOU_SAVE to "നിങ്ങളുടെ ലാഭം",
            Strings.DIFFERENCE_MONTHLY to "പ്രതിമാസ വ്യത്യാസം",
            Strings.DIFFERENCE_INTEREST to "പലിശ വ്യത്യാസം",
            Strings.SELECT_BANK_PRESET to "ബാങ്ക് പലിശ നിരക്കുകൾ",

            Strings.PREPAYMENT_TITLE to "മുൻകൂർ തിരിച്ചടവ് ലാഭം",
            Strings.EXTRA_PAYMENT to "അധിക തുക",
            Strings.PREPAYMENT_TYPE to "തിരിച്ചടവ് രീതി",
            Strings.ONE_TIME to "ഒറ്റത്തവണ",
            Strings.MONTHLY_EXTRA to "പ്രതിമാസ അധികം",
            Strings.SAVINGS_RESULT to "ലാഭം കണക്കുകൂട്ടൽ",
            Strings.TENURE_REDUCED_BY to "കുറഞ്ഞ കാലാവധി",
            Strings.TOTAL_INTEREST_SAVED to "ലാഭിച്ച ആകെ പലിശ",

            Strings.SCHEDULE_TITLE to "തിരിച്ചടവ് വിവരങ്ങൾ",
            Strings.YEARLY_VIEW to "വാർഷിക വിവരം",
            Strings.MONTHLY_VIEW to "പ്രതിമാസ വിവരം",
            Strings.YEAR to "വർഷം",
            Strings.BALANCE to "ബാക്കി തുക",
            Strings.SHARE_SCHEDULE to "പങ്കിടുക",
            Strings.COPIED_TO_CLIPBOARD to "പകർത്തി",

            Strings.CHOOSE_LANGUAGE to "ഭാഷ തിരഞ്ഞെടുക്കുക",
            Strings.CONTINUE_BTN to "തുടരുക",
            Strings.CHOOSE_THEME to "തീം തിരഞ്ഞെടുക്കുക",
            Strings.THEME_SYSTEM to "സിസ്റ്റം സ്ഥിരസ്ഥിതി",
            Strings.THEME_DARK to "ഡാർക്ക് മോഡ്",
            Strings.THEME_LIGHT to "ലൈറ്റ് മോഡ്",

            Strings.SPONSORED_AD to "പരസ്യം",
            Strings.BEST_RATES_BANNER to "കുറഞ്ഞ പലിശയിൽ ഭവന വായ്പ 8.35%* മുതൽ",
            Strings.CHECK_ELIGIBILITY to "പരിശോധിക്കുക"
        ),

        AppLanguage.ODIA to mapOf(
            Strings.APP_TITLE to "EMI କାଲକୁଲେଟର",
            Strings.TAB_CALCULATOR to "EMI",
            Strings.TAB_COMPARE to "ତୁଳନା",
            Strings.TAB_PREPAYMENT to "ପ୍ରି-ପେମେଣ୍ଟ",
            Strings.TAB_SCHEDULE to "ତାଲିକା",

            Strings.LOAN_AMOUNT to "ଋଣ ରାଶି",
            Strings.INTEREST_RATE to "ସୁଧ ହାର (%)",
            Strings.LOAN_TENURE to "ଅବଧି",
            Strings.YEARS to "ବର୍ଷ",
            Strings.MONTHS to "ମାସ",
            Strings.MONTHLY_EMI to "ମାସିକ EMI",
            Strings.TOTAL_INTEREST to "ମୋଟ ସୁଧ",
            Strings.TOTAL_PAYMENT to "ମୋଟ ପରିଶୋଧ",
            Strings.PRINCIPAL_AMOUNT to "ମୂଳଧନ",

            Strings.HOME_LOAN to "ଗୃହ ଋଣ",
            Strings.PERSONAL_LOAN to "ବ୍ୟକ୍ତିଗତ ଋଣ",
            Strings.CAR_LOAN to "କାର୍ ଋଣ",
            Strings.EDUCATION_LOAN to "ଶିକ୍ଷା ଋଣ",

            Strings.DOWNLOAD_PDF to "PDF ଡାଉନଲୋଡ୍",
            Strings.SHARE_BTN to "ସେୟାର କରନ୍ତୁ",

            Strings.COMPARE_TITLE to "ଋଣ ଅଫର୍ ତୁଳନା କରନ୍ତୁ",
            Strings.LOAN_A to "ଅଫର୍ A",
            Strings.LOAN_B to "ଅଫର୍ B",
            Strings.BEST_DEAL to "ସର୍ବୋତ୍ତମ ଅଫର୍",
            Strings.YOU_SAVE to "ଆପଣଙ୍କ ସଞ୍ଚୟ",
            Strings.DIFFERENCE_MONTHLY to "ମାସିକ ଅନ୍ତର",
            Strings.DIFFERENCE_INTEREST to "ମୋଟ ସୁଧ ଅନ୍ତର",
            Strings.SELECT_BANK_PRESET to "ବ୍ୟାଙ୍କ ସୁଧ ହାର",

            Strings.PREPAYMENT_TITLE to "ପ୍ରି-ପେମେଣ୍ଟ ଲାଭ",
            Strings.EXTRA_PAYMENT to "ଅତିରିକ୍ତ ରାଶି",
            Strings.PREPAYMENT_TYPE to "ପରିଶୋଧ ପ୍ରକାର",
            Strings.ONE_TIME to "ଏକକାଳୀନ",
            Strings.MONTHLY_EXTRA to "ପ୍ରତି ମାସ ଅତିରିକ୍ତ",
            Strings.SAVINGS_RESULT to "ମୋଟ ସଞ୍ଚୟ",
            Strings.TENURE_REDUCED_BY to "କମିଥିବା ଅବଧି",
            Strings.TOTAL_INTEREST_SAVED to "ବଞ୍ଚିଥିବା ସୁଧ",

            Strings.SCHEDULE_TITLE to "ପରିଶୋଧ ତାଲିକା",
            Strings.YEARLY_VIEW to "ବାର୍ଷିକ ବିବରଣୀ",
            Strings.MONTHLY_VIEW to "ମାସିକ ବିବରଣୀ",
            Strings.YEAR to "ବର୍ଷ",
            Strings.BALANCE to "ବାକି ରାଶି",
            Strings.SHARE_SCHEDULE to "ସେୟାର କରନ୍ତୁ",
            Strings.COPIED_TO_CLIPBOARD to "କପି ହୋଇଗଲା",

            Strings.CHOOSE_LANGUAGE to "ଭାଷା ବାଛନ୍ତୁ",
            Strings.CONTINUE_BTN to "ଆଗକୁ ବଢ଼ନ୍ତୁ",
            Strings.CHOOSE_THEME to "ଥିମ୍ ବାଛନ୍ତୁ",
            Strings.THEME_SYSTEM to "ସିଷ୍ଟମ ଅନୁସାରେ",
            Strings.THEME_DARK to "ଡାର୍କ ମୋଡ୍",
            Strings.THEME_LIGHT to "ଲାଇଟ୍ ମୋଡ୍",

            Strings.SPONSORED_AD to "ବିଜ୍ଞାପନ",
            Strings.BEST_RATES_BANNER to "କମ୍ ସୁଧରେ ଗୃହ ଋଣ 8.35%* ରୁ ଆରମ୍ଭ",
            Strings.CHECK_ELIGIBILITY to "ଯାଞ୍ଚ କରନ୍ତୁ"
        )
    )

    fun getString(language: AppLanguage, key: String): String {
        if (key == Strings.PRINCIPAL_AMOUNT || key == Strings.PRINCIPAL_WITHOUT_INTEREST) {
            return getPrincipalWithoutInterestLabel(language)
        }
        if (key == Strings.ORIGINAL_LOAN_AMOUNT) {
            return getOriginalLoanAmountLabel(language)
        }
        if (key == Strings.ORIGINAL_LOAN_TENURE) {
            return getOriginalLoanTenureLabel(language)
        }
        if (key == Strings.EMIS_ALREADY_PAID) {
            return getEmisAlreadyPaidLabel(language)
        }
        if (key == Strings.REMAINING_BALANCE_BEFORE) {
            return getRemainingBalanceLabel(language)
        }
        return translations[language]?.get(key)
            ?: ExtraStrings.get(language, key)
            ?: ToolStrings.get(language, key)
            ?: translations[AppLanguage.ENGLISH]?.get(key)
            ?: ExtraStrings.english(key)
            ?: ToolStrings.english(key)
            ?: key
    }

    fun getPrincipalWithoutInterestLabel(language: AppLanguage): String {
        return when (language) {
            AppLanguage.HINDI -> "मूलधन (बिना ब्याज के)"
            AppLanguage.CHHATTISGARHI -> "मूलधन (बिना ब्याज के)"
            AppLanguage.MARATHI -> "मुद्दल (व्याजाशिवाय)"
            AppLanguage.GUJARATI -> "મુદ્દલ (વ્યાજ વગર)"
            AppLanguage.BENGALI -> "আসল টাকা (সুদ ছাড়া)"
            AppLanguage.PUNJABI -> "ਮੂਲ ਰਕਮ (ਬਿਨਾਂ ਵਿਆਜ)"
            AppLanguage.TAMIL -> "அசல் (வட்டி இல்லாமல்)"
            AppLanguage.TELUGU -> "అసలు (వడ్డీ లేకుండా)"
            AppLanguage.KANNADA -> "ಅಸಲು (ಬಡ್ಡಿ ರಹಿತ)"
            AppLanguage.MALAYALAM -> "യഥാർത്ഥ തുക (പലിശയില്ലാതെ)"
            AppLanguage.ODIA -> "ମୂଳଧନ (ବିନା ସୁଧ)"
            else -> "Principal (Without Interest)"
        }
    }

    fun getOriginalLoanAmountLabel(language: AppLanguage): String {
        return when (language) {
            AppLanguage.HINDI -> "कुल मूल ऋण राशि (Original Loan)"
            AppLanguage.CHHATTISGARHI -> "कुल असली करजा रकम"
            AppLanguage.MARATHI -> "एकूण मूळ कर्ज रक्कम"
            AppLanguage.GUJARATI -> "કુલ મૂળ લોન રકમ"
            AppLanguage.BENGALI -> "মোট আসল ঋণের পরিমাণ"
            AppLanguage.PUNJABI -> "ਕੁੱਲ ਅਸਲ ਕਰਜ਼ਾ ਰਕਮ"
            AppLanguage.TAMIL -> "மொத்த அசல் கடன் தொகை"
            AppLanguage.TELUGU -> "మొత్తం అసలు రుణ మొత్తం"
            AppLanguage.KANNADA -> "ಒಟ್ಟು ಮೂಲ ಸಾಲದ ಮೊತ್ತ"
            AppLanguage.MALAYALAM -> "യഥാർത്ഥ വായ്പ തുക"
            AppLanguage.ODIA -> "ମୋଟ ମୂଳ ଋଣ ରାଶି"
            else -> "Total Original Loan Amount"
        }
    }

    fun getOriginalLoanTenureLabel(language: AppLanguage): String {
        return when (language) {
            AppLanguage.HINDI -> "मूल ऋण अवधि (Original Tenure)"
            AppLanguage.CHHATTISGARHI -> "असली लोन अवधि"
            AppLanguage.MARATHI -> "मूळ कर्ज कालावधी"
            AppLanguage.GUJARATI -> "મૂળ લોનની મુદત"
            AppLanguage.BENGALI -> "আসল ঋণের মেয়াদ"
            AppLanguage.PUNJABI -> "ਅਸਲ ਕਰਜ਼ਾ ਮਿਆਦ"
            AppLanguage.TAMIL -> "அசல் கடன் காலம்"
            AppLanguage.TELUGU -> "అసలు రుణ కాలపరిమితి"
            AppLanguage.KANNADA -> "ಮೂಲ ಸಾಲದ ಅವಧಿ"
            AppLanguage.MALAYALAM -> "യഥാർത്ഥ വായ്പാ കാലാവധി"
            AppLanguage.ODIA -> "ମୂଳ ଋଣ ଅବଧି"
            else -> "Original Loan Tenure"
        }
    }

    fun getEmisAlreadyPaidLabel(language: AppLanguage): String {
        return when (language) {
            AppLanguage.HINDI -> "पहले से चुकाई गई ईएमआई (Paid EMIs)"
            AppLanguage.CHHATTISGARHI -> "पहिले पटाय ईएमआई (Paid EMIs)"
            AppLanguage.MARATHI -> "आधी भरलेले ईएमआय (Paid EMIs)"
            AppLanguage.GUJARATI -> "પહેલેથી ચૂકવેલ ઇએમઆઇ"
            AppLanguage.BENGALI -> "ইতিমধ্যে পরিশোধিত ইএমআই"
            AppLanguage.PUNJABI -> "ਪਹਿਲਾਂ ਅਦਾ ਕੀਤੀਆਂ EMIs"
            AppLanguage.TAMIL -> "ஏற்கனவே செலுத்திய தவணைகள் (EMIs)"
            AppLanguage.TELUGU -> "ఇప్పటికే చెల్లించిన ఇఎంఐలు"
            AppLanguage.KANNADA -> "ಈಗಾಗಲೇ ಪಾವತಿಸಿದ ಇಎಂಐಗಳು"
            AppLanguage.MALAYALAM -> "ഇതിനകം അടച്ച ഇഎംഐകൾ"
            AppLanguage.ODIA -> "ପୂର୍ବରୁ ପରିଶୋଧିତ EMI"
            else -> "Number of EMIs Already Paid"
        }
    }

    fun getRemainingBalanceLabel(language: AppLanguage): String {
        return when (language) {
            AppLanguage.HINDI -> "प्री-पेमेंट से पहले शेष बकाया"
            AppLanguage.CHHATTISGARHI -> "पहिले पटाए के आगे बांचे रकम"
            AppLanguage.MARATHI -> "प्री-पेमेंट आधी शिल्लक मुद्दल"
            AppLanguage.GUJARATI -> "પ્રી-પેમેન્ટ પહેલાં બાકી રકમ"
            AppLanguage.BENGALI -> "প্রি-পেমেন্টের আগের বাকি টাকা"
            AppLanguage.PUNJABI -> "ਪ੍ਰੀ-ਪੇਮੈਂਟ ਤੋਂ ਪਹਿਲਾਂ ਬਾਕੀ ਰਕਮ"
            AppLanguage.TAMIL -> "முன்செலுத்தலுக்கு முன் மீதி தொகை"
            AppLanguage.TELUGU -> "ప్రీ-పేమెంట్‌కు ముందు మిగిలిన మొత్తం"
            AppLanguage.KANNADA -> "ಪೂರ್ವಪಾವತಿಗೆ ಮುಂಚಿನ ಉಳಿದ ಮೊತ್ತ"
            AppLanguage.MALAYALAM -> "മുൻകൂർ അടവിന് മുമ്പുള്ള ബാക്കി"
            AppLanguage.ODIA -> "ପ୍ରି-ପେମେଣ୍ଟ ପୂର୍ବ ବକେୟା ରାଶି"
            else -> "Balance Before Prepayment"
        }
    }

    fun getSelectBankLabel(language: AppLanguage): String {
        return when (language) {
            AppLanguage.HINDI -> "बैंक चुनें"
            AppLanguage.CHHATTISGARHI -> "बैंक चुनव"
            AppLanguage.MARATHI -> "बँक निवडा"
            AppLanguage.GUJARATI -> "બેંક પસંદ કરો"
            AppLanguage.BENGALI -> "ব্যাঙ্ক নির্বাচন করুন"
            AppLanguage.PUNJABI -> "ਬੈਂਕ ਚੁਣੋ"
            AppLanguage.TAMIL -> "வங்கி தேர்வு"
            AppLanguage.TELUGU -> "బ్యాంకును ఎంచుకోండి"
            AppLanguage.KANNADA -> "ಬ್ಯಾಂಕ್ ಆಯ್ಕೆಮಾಡಿ"
            AppLanguage.MALAYALAM -> "ബാങ്ക് തിരഞ്ഞെടുക്കുക"
            AppLanguage.ODIA -> "ବ୍ୟାଙ୍କ ବାଛନ୍ତୁ"
            else -> "Select Bank"
        }
    }

    fun getOtherBankLabel(language: AppLanguage): String {
        return when (language) {
            AppLanguage.HINDI -> "अन्य (Other)"
            AppLanguage.CHHATTISGARHI -> "दुसर (Other)"
            AppLanguage.MARATHI -> "इतर (Other)"
            AppLanguage.GUJARATI -> "અન્ય (Other)"
            AppLanguage.BENGALI -> "অন্যান্য (Other)"
            AppLanguage.PUNJABI -> "ਹੋਰ (Other)"
            AppLanguage.TAMIL -> "மற்றவை (Other)"
            AppLanguage.TELUGU -> "ఇతర (Other)"
            AppLanguage.KANNADA -> "ಇತರೆ (Other)"
            AppLanguage.MALAYALAM -> "മറ്റുള്ളവ (Other)"
            AppLanguage.ODIA -> "ଅନ୍ୟ (Other)"
            else -> "Other (Custom Bank)"
        }
    }

    fun getEnterBankNameHint(language: AppLanguage): String {
        return when (language) {
            AppLanguage.HINDI -> "बैंक का नाम दर्ज करें"
            AppLanguage.CHHATTISGARHI -> "बैंक के नाव लिखव"
            AppLanguage.MARATHI -> "बँकेचे नाव टाका"
            AppLanguage.GUJARATI -> "બેંકનું નામ દાખલ કરો"
            AppLanguage.BENGALI -> "ব্যাঙ্কের নাম লিখুন"
            AppLanguage.PUNJABI -> "ਬੈਂਕ ਦਾ ਨਾਂ ਦਰਜ ਕਰੋ"
            AppLanguage.TAMIL -> "வங்கியின் பெயரை உள்ளிடவும்"
            AppLanguage.TELUGU -> "బ్యాంకు పేరు నమోదు చేయండి"
            AppLanguage.KANNADA -> "ಬ್ಯಾಂಕ್ ಹೆಸರು ನಮೂದಿಸಿ"
            AppLanguage.MALAYALAM -> "ബാങ്കിന്റെ പേര് നൽകുക"
            AppLanguage.ODIA -> "ବ୍ୟାଙ୍କ ନାମ ଲେଖନ୍ତୁ"
            else -> "Enter Bank Name"
        }
    }
}
