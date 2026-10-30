package com.aksh.emicalculator.data

/**
 * Strings for the new home dashboard + the extra calculators.
 *
 * English and Hindi are complete; every other language automatically falls back to English
 * (same behaviour as [ExtraStrings]).
 */
object ToolStrings {

    // ---------------------------------------------------------------- tool names
    const val TOOL_EMI = "t_tool_emi"
    const val TOOL_HOME_CAR = "t_tool_home_car"
    const val TOOL_SCHEDULE = "t_tool_schedule"
    const val TOOL_PREPAY = "t_tool_prepay"
    const val TOOL_ADVANCE = "t_tool_advance"
    const val TOOL_YEARLY = "t_tool_yearly"
    const val TOOL_ELIG = "t_tool_elig"
    const val TOOL_SIP = "t_tool_sip"
    const val TOOL_LUMPSUM = "t_tool_lumpsum"
    const val TOOL_PPF = "t_tool_ppf"
    const val TOOL_FD = "t_tool_fd"
    const val TOOL_RD = "t_tool_rd"
    const val TOOL_SSY = "t_tool_ssy"
    const val TOOL_SIMPLE = "t_tool_simple"
    const val TOOL_COMPOUND = "t_tool_compound"
    const val TOOL_GST = "t_tool_gst"
    const val TOOL_MARGIN = "t_tool_margin"
    const val TOOL_CAGR = "t_tool_cagr"
    const val TOOL_VAT = "t_tool_vat"
    const val TOOL_BROKERAGE = "t_tool_brokerage"
    const val TOOL_INFLATION = "t_tool_inflation"
    const val TOOL_SALARY = "t_tool_salary"
    const val TOOL_BREAKEVEN = "t_tool_breakeven"
    const val TOOL_DISCOUNT_MARKUP = "t_tool_discount_markup"
    const val TOOL_DISCOUNT = "t_tool_discount"
    const val TOOL_PERCENT = "t_tool_percent"
    const val TOOL_FUEL = "t_tool_fuel"
    const val TOOL_GRATUITY = "t_tool_gratuity"
    const val TOOL_AGE = "t_tool_age"
    const val TOOL_SWP = "t_tool_swp"
    const val TOOL_EPF = "t_tool_epf"
    const val TOOL_BMI = "t_tool_bmi"
    const val TOOL_CURRENCY = "t_tool_currency"
    const val TOOL_TARGET = "t_tool_target"
    const val TOOL_TIP = "t_tool_tip"
    const val TOOL_UNIT = "t_tool_unit"
    const val TOOL_GOLD = "t_tool_gold"
    const val TOOL_CAR = "t_tool_car"
    const val TOOL_HEALTH = "t_tool_health"
    const val TOOL_SKIP = "t_tool_skip"

    // ---------------------------------------------------------------- categories
    const val CAT_LOAN = "t_cat_loan"
    const val CAT_BANKING = "t_cat_banking"
    const val CAT_BUSINESS = "t_cat_business"
    const val CAT_DAILY = "t_cat_daily"
    const val CAT_EXTRA = "t_cat_extra"

    // ---------------------------------------------------------------- home
    const val HOME_TAGLINE = "t_home_tagline"
    const val VIEW_ALL = "t_view_all"
    const val ALL_TOOLS = "t_all_tools"
    const val HOME_NOTE = "t_home_note"
    const val HOME = "t_home"

    // ---------------------------------------------------------------- common labels
    const val AMOUNT = "t_amount"
    const val PRINCIPAL_AMT = "t_principal_amt"
    const val RATE_PER_YEAR = "t_rate_per_year"
    const val TIME_YEARS = "t_time_years"
    const val TOTAL_INTEREST_LBL = "t_total_interest_lbl"
    const val TOTAL_AMOUNT = "t_total_amount"
    const val MATURITY_AMT = "t_maturity_amt"
    const val INTEREST_EARNED_LBL = "t_interest_earned_lbl"
    const val TOTAL_DEPOSITED = "t_total_deposited"
    const val COMPOUNDING = "t_compounding"
    const val YEARLY_DEPOSIT = "t_yearly_deposit"
    const val INITIAL_VALUE = "t_initial_value"
    const val FINAL_VALUE = "t_final_value"
    const val DURATION_YEARS = "t_duration_years"
    const val CAGR_RESULT = "t_cagr_result"
    const val ABS_GROWTH = "t_abs_growth"
    const val VAT_RATE = "t_vat_rate"
    const val ADD_TAX = "t_add_tax"
    const val REMOVE_TAX = "t_remove_tax"
    const val TAX_AMOUNT = "t_tax_amount"
    const val TOTAL_WITH_TAX = "t_total_with_tax"
    const val BUY_PRICE = "t_buy_price"
    const val SELL_PRICE = "t_sell_price"
    const val QUANTITY = "t_quantity"
    const val BROKERAGE_PCT = "t_brokerage_pct"
    const val TOTAL_CHARGES = "t_total_charges"
    const val NET_PROFIT = "t_net_profit"
    const val TURNOVER = "t_turnover"
    const val TODAY_AMOUNT = "t_today_amount"
    const val INFLATION_RATE = "t_inflation_rate"
    const val FUTURE_COST = "t_future_cost"
    const val VALUE_LOST = "t_value_lost"
    const val CTC_YEARLY = "t_ctc_yearly"
    const val BASIC_PCT = "t_basic_pct"
    const val PF_PCT = "t_pf_pct"
    const val TAX_PCT = "t_tax_pct"
    const val INHAND_MONTHLY = "t_inhand_monthly"
    const val INHAND_YEARLY = "t_inhand_yearly"
    const val TOTAL_DEDUCTIONS = "t_total_deductions"
    const val FIXED_COST = "t_fixed_cost"
    const val PRICE_PER_UNIT = "t_price_per_unit"
    const val VAR_COST = "t_var_cost"
    const val BREAKEVEN_UNITS = "t_breakeven_units"
    const val BREAKEVEN_REVENUE = "t_breakeven_revenue"
    const val MARKUP_PCT = "t_markup_pct"
    const val DISC_RATE = "t_disc_rate"
    const val FINAL_PRICE = "t_final_price"
    const val VALUE_A = "t_value_a"
    const val VALUE_B = "t_value_b"
    const val PERCENT_RESULT = "t_percent_result"
    const val DISTANCE_KM = "t_distance_km"
    const val MILEAGE = "t_mileage"
    const val FUEL_PRICE = "t_fuel_price"
    const val FUEL_NEEDED = "t_fuel_needed"
    const val FUEL_COST = "t_fuel_cost"
    const val BASIC_DA = "t_basic_da"
    const val SERVICE_YEARS = "t_service_years"
    const val GRATUITY_AMT = "t_gratuity_amt"
    const val DOB = "t_dob"
    const val AGE_RESULT = "t_age_result"
    const val NEXT_BIRTHDAY = "t_next_birthday"
    const val CORPUS = "t_corpus"
    const val MONTHLY_WITHDRAWAL = "t_monthly_withdrawal"
    const val EXPECTED_RETURN_LBL = "t_expected_return_lbl"
    const val FINAL_BALANCE = "t_final_balance"
    const val TOTAL_WITHDRAWN = "t_total_withdrawn"
    const val MONTHS_LASTED = "t_months_lasted"
    const val BASIC_SALARY = "t_basic_salary"
    const val EMP_PCT = "t_emp_pct"
    const val EMPLOYER_PCT = "t_employer_pct"
    const val CURRENT_BALANCE = "t_current_balance"
    const val EPF_YEARS = "t_epf_years"
    const val EPF_RATE = "t_epf_rate"
    const val EPF_MATURITY = "t_epf_maturity"
    const val HEIGHT_CM = "t_height_cm"
    const val WEIGHT_KG = "t_weight_kg"
    const val BMI_RESULT = "t_bmi_result"
    const val BMI_CATEGORY = "t_bmi_category"
    const val BMI_UNDER = "t_bmi_under"
    const val BMI_NORMAL = "t_bmi_normal"
    const val BMI_OVER = "t_bmi_over"
    const val BMI_OBESE = "t_bmi_obese"
    const val FROM = "t_from"
    const val TO = "t_to"
    const val CONVERTED = "t_converted"
    const val TARGET_AMOUNT = "t_target_amount"
    const val MONTHLY_SAVING = "t_monthly_saving"
    const val YEARLY_SAVING = "t_yearly_saving"
    const val BILL_AMOUNT = "t_bill_amount"
    const val TIP_PCT = "t_tip_pct"
    const val SPLIT_BETWEEN = "t_split_between"
    const val TIP_AMOUNT = "t_tip_amount"
    const val TOTAL_BILL = "t_total_bill"
    const val PER_PERSON = "t_per_person"
    const val UNIT_VALUE = "t_unit_value"
    const val LENGTH = "t_length"
    const val WEIGHT = "t_weight"
    const val TEMPERATURE = "t_temperature"
    const val AREA = "t_area"
    const val UNIT_RESULT = "t_unit_result"
    const val DAY = "t_day"
    const val MONTH = "t_month"
    const val YEAR = "t_year"
    const val YEARS_SHORT = "t_years_short"
    const val MONTHS_SHORT = "t_months_short"
    const val DAYS_SHORT = "t_days_short"

    // ---------------------------------------------------------------- prepayment / schedule screen
    const val LOAN_DETAILS = "t_loan_details"
    const val ENTER_LOAN_INFO = "t_enter_loan_info"
    const val EXTRA_PREPAYMENT = "t_extra_prepayment"
    const val CALCULATE = "t_calculate"
    const val SHOW_MORE = "t_show_more"
    const val SHOW_LESS = "t_show_less"
    const val YOUR_SAVINGS = "t_your_savings"
    const val SAVINGS_SUB = "t_savings_sub"
    const val TOTAL_PAYMENT_LBL = "t_total_payment_lbl"
    const val NEW_TENURE_LBL = "t_new_tenure_lbl"
    const val MONTH_SHORT = "t_month_short"
    const val ADVANCED_OPTIONS = "t_advanced_options"
    const val EXTRA_TYPE = "t_extra_type"
    const val YEARS_LBL = "t_years_lbl"
    const val MONTHS_LBL = "t_months_lbl"
    const val MONTHLY = "t_monthly"
    const val YEARLY = "t_yearly"

    // ---------------------------------------------------------------- pdf
    const val PDF_REPORT_DATE = "t_pdf_report_date"
    const val PDF_YOUR_PARTNER = "t_pdf_your_partner"
    const val PDF_SCHEDULE_TITLE = "t_pdf_schedule_title"
    const val PDF_SCHEDULE_SUB = "t_pdf_schedule_sub"
    const val PDF_MONTHLY_BREAKDOWN = "t_pdf_monthly_breakdown"
    const val PDF_EXTRA_BENEFIT = "t_pdf_extra_benefit"
    const val PDF_INCLUDES = "t_pdf_includes"
    const val PDF_TAGLINE = "t_pdf_tagline"
    const val PDF_EXTRA_PAYMENT = "t_pdf_extra_payment"
    const val PDF_TOTAL_PAID = "t_pdf_total_paid"
    const val PDF_NEXT_MONTHS = "t_pdf_next_months"
    const val PDF_INC_1 = "t_pdf_inc_1"
    const val PDF_INC_2 = "t_pdf_inc_2"
    const val PDF_INC_3 = "t_pdf_inc_3"
    const val PDF_INC_4 = "t_pdf_inc_4"
    const val PDF_EASY = "t_pdf_easy"
    const val PDF_SECURE = "t_pdf_secure"
    const val PDF_ALL_IN_ONE = "t_pdf_all_in_one"
    const val PDF_SAVED = "t_pdf_saved"
    const val PDF_NO = "t_pdf_no"
    const val PDF_SAVED_MONTHS = "t_pdf_saved_months"
    const val PDF_TOTAL = "t_pdf_total"
    const val PDF_MONTHLY_EXTRA = "t_pdf_monthly_extra"
    const val LOAN_AMOUNT_LBL = "t_loan_amount_lbl"
    const val LOAN_TENURE_LBL = "t_loan_tenure_lbl"
    const val REPAYMENT_SCHEDULE = "t_repayment_schedule"
    const val PRINCIPAL_LBL = "t_principal_lbl"
    const val EMI_LBL = "t_emi_lbl"
    const val BALANCE_LBL = "t_balance_lbl"
    const val MONTHLY_EMI = "t_monthly_emi"

    // ---------------------------------------------------------------- shared design / report
    const val BACK = "t_back"
    const val REPORT = "t_report"
    const val PDF_REPORT_SUB = "t_pdf_report_sub"
    const val PDF_HIGHLIGHTS = "t_pdf_highlights"
    const val PDF_GEN_INC_1 = "t_pdf_gen_inc_1"
    const val PDF_GEN_INC_2 = "t_pdf_gen_inc_2"
    const val PDF_GEN_INC_3 = "t_pdf_gen_inc_3"
    const val PDF_GEN_INC_4 = "t_pdf_gen_inc_4"
    const val PDF_GEN_NOTE = "t_pdf_gen_note"
    const val PDF_PARAMETER = "t_pdf_parameter"
    const val PDF_VALUE = "t_pdf_value"
    const val PDF_SUCCESS = "t_pdf_success"
    const val PDF_FAILED = "t_pdf_failed"
    const val PDF_OPEN_CHOOSER = "t_pdf_open_chooser"
    const val PDF_SHARE_CHOOSER = "t_pdf_share_chooser"
    const val PDF_SHARE_SUBJECT = "t_pdf_share_subject"
    const val PDF_GET_APP = "t_pdf_get_app"

    private val en: Map<String, String> by lazy {
        mapOf(
            TOOL_EMI to "EMI Calculator",
            TOOL_HOME_CAR to "Home / Car Loan\nEMI",
            TOOL_SCHEDULE to "Amortization\nSchedule",
            TOOL_PREPAY to "Prepayment\nCalculator",
            TOOL_ADVANCE to "Advance /\nPrepayment",
            TOOL_YEARLY to "Yearly\nBreakup",
            TOOL_ELIG to "Loan Eligibility\nChecker",
            TOOL_SIP to "SIP Calculator",
            TOOL_LUMPSUM to "Lumpsum Calculator",
            TOOL_PPF to "PPF Calculator",
            TOOL_FD to "FD Calculator",
            TOOL_RD to "RD Calculator",
            TOOL_SSY to "Sukanya Samriddhi\n(SSY)",
            TOOL_SIMPLE to "Simple Interest\nCalculator",
            TOOL_COMPOUND to "Compound Interest\nCalculator",
            TOOL_GST to "GST Calculator",
            TOOL_MARGIN to "Profit Margin\nCalculator",
            TOOL_CAGR to "CAGR Calculator",
            TOOL_VAT to "VAT / Sales Tax\nCalculator",
            TOOL_BROKERAGE to "Brokerage\nCalculator",
            TOOL_INFLATION to "Inflation Calculator",
            TOOL_SALARY to "Salary Calculator",
            TOOL_BREAKEVEN to "Break-Even Point\nCalculator",
            TOOL_DISCOUNT_MARKUP to "Discount & Markup\nCalculator",
            TOOL_DISCOUNT to "Discount Calculator",
            TOOL_PERCENT to "Percentage\nCalculator",
            TOOL_FUEL to "Fuel Cost Calculator",
            TOOL_GRATUITY to "Gratuity Calculator",
            TOOL_AGE to "Age Calculator",
            TOOL_SWP to "SWP Calculator",
            TOOL_EPF to "EPF Calculator",
            TOOL_BMI to "BMI Calculator",
            TOOL_CURRENCY to "Currency Converter",
            TOOL_TARGET to "FD/RD Target Goal\nCalculator",
            TOOL_TIP to "Tip Calculator",
            TOOL_UNIT to "Unit Converter",
            TOOL_GOLD to "Gold Loan\nCalculator",
            TOOL_CAR to "Car Loan\nCalculator",
            TOOL_HEALTH to "Health Insurance\nCalculator",
            TOOL_SKIP to "Skip EMI\nCalculator",

            CAT_LOAN to "EMI & Loan Calculators",
            CAT_BANKING to "Banking & Investment",
            CAT_BUSINESS to "Business & Financial Tools",
            CAT_DAILY to "Daily Utility & Shopping",
            CAT_EXTRA to "Additional Recommended Tools",

            HOME_TAGLINE to "Smart Tools for Your Financial Life",
            VIEW_ALL to "View All",
            ALL_TOOLS to "All Tools",
            HOME_NOTE to "All calculators are free to use. Your data never leaves your phone.",
            HOME to "Home",

            AMOUNT to "Amount",
            PRINCIPAL_AMT to "Principal amount",
            RATE_PER_YEAR to "Rate (% per year)",
            TIME_YEARS to "Time (years)",
            TOTAL_INTEREST_LBL to "Total interest",
            TOTAL_AMOUNT to "Total amount",
            MATURITY_AMT to "Maturity amount",
            INTEREST_EARNED_LBL to "Interest earned",
            TOTAL_DEPOSITED to "Total deposited",
            COMPOUNDING to "Interest compounding",
            YEARLY_DEPOSIT to "Yearly deposit",
            INITIAL_VALUE to "Initial value",
            FINAL_VALUE to "Final value",
            DURATION_YEARS to "Duration (years)",
            CAGR_RESULT to "CAGR (yearly growth)",
            ABS_GROWTH to "Absolute growth",
            VAT_RATE to "VAT / Sales tax rate (%)",
            ADD_TAX to "Add tax",
            REMOVE_TAX to "Remove tax",
            TAX_AMOUNT to "Tax amount",
            TOTAL_WITH_TAX to "Total (with tax)",
            BUY_PRICE to "Buy price",
            SELL_PRICE to "Sell price",
            QUANTITY to "Quantity",
            BROKERAGE_PCT to "Brokerage (%)",
            TOTAL_CHARGES to "Total charges & taxes",
            NET_PROFIT to "Net profit",
            TURNOVER to "Turnover",
            TODAY_AMOUNT to "Today's amount",
            INFLATION_RATE to "Inflation rate (%)",
            FUTURE_COST to "Future cost",
            VALUE_LOST to "Value of money lost",
            CTC_YEARLY to "CTC (yearly)",
            BASIC_PCT to "Basic salary (% of CTC)",
            PF_PCT to "PF contribution (%)",
            TAX_PCT to "Income tax (%)",
            INHAND_MONTHLY to "In-hand salary (monthly)",
            INHAND_YEARLY to "In-hand salary (yearly)",
            TOTAL_DEDUCTIONS to "Total deductions",
            FIXED_COST to "Fixed costs",
            PRICE_PER_UNIT to "Selling price per unit",
            VAR_COST to "Variable cost per unit",
            BREAKEVEN_UNITS to "Break-even units",
            BREAKEVEN_REVENUE to "Break-even revenue",
            MARKUP_PCT to "Markup (%)",
            DISC_RATE to "Discount (%)",
            FINAL_PRICE to "Final price",
            VALUE_A to "Value A",
            VALUE_B to "Value B",
            PERCENT_RESULT to "Result",
            DISTANCE_KM to "Distance (km)",
            MILEAGE to "Mileage (km per litre)",
            FUEL_PRICE to "Fuel price (per litre)",
            FUEL_NEEDED to "Fuel needed",
            FUEL_COST to "Total fuel cost",
            BASIC_DA to "Basic + DA (monthly)",
            SERVICE_YEARS to "Years of service",
            GRATUITY_AMT to "Gratuity amount",
            DOB to "Date of birth",
            AGE_RESULT to "Age",
            NEXT_BIRTHDAY to "Next birthday in",
            CORPUS to "Investment corpus",
            MONTHLY_WITHDRAWAL to "Monthly withdrawal",
            EXPECTED_RETURN_LBL to "Expected return (%)",
            FINAL_BALANCE to "Final balance",
            TOTAL_WITHDRAWN to "Total withdrawn",
            MONTHS_LASTED to "Money will last (months)",
            BASIC_SALARY to "Basic salary (monthly)",
            EMP_PCT to "Your contribution (%)",
            EMPLOYER_PCT to "Employer contribution (%)",
            CURRENT_BALANCE to "Current EPF balance",
            EPF_YEARS to "Years to retirement",
            EPF_RATE to "EPF interest rate (%)",
            EPF_MATURITY to "EPF maturity amount",
            HEIGHT_CM to "Height (cm)",
            WEIGHT_KG to "Weight (kg)",
            BMI_RESULT to "Your BMI",
            BMI_CATEGORY to "Category",
            BMI_UNDER to "Underweight",
            BMI_NORMAL to "Normal",
            BMI_OVER to "Overweight",
            BMI_OBESE to "Obese",
            FROM to "From",
            TO to "To",
            CONVERTED to "Converted amount",
            TARGET_AMOUNT to "Target amount",
            MONTHLY_SAVING to "Monthly saving needed",
            YEARLY_SAVING to "Yearly saving needed",
            BILL_AMOUNT to "Bill amount",
            TIP_PCT to "Tip (%)",
            SPLIT_BETWEEN to "Split between (people)",
            TIP_AMOUNT to "Tip amount",
            TOTAL_BILL to "Total bill",
            PER_PERSON to "Per person",
            UNIT_VALUE to "Value",
            LENGTH to "Length",
            WEIGHT to "Weight",
            TEMPERATURE to "Temperature",
            AREA to "Area",
            UNIT_RESULT to "Converted value",
            DAY to "Day",
            MONTH to "Month",
            YEAR to "Year",
            YEARS_SHORT to "Yrs",
            MONTHS_SHORT to "Mo",
            DAYS_SHORT to "Days",

            LOAN_DETAILS to "Loan Details",
            ENTER_LOAN_INFO to "Enter your loan information",
            EXTRA_PREPAYMENT to "Extra Prepayment",
            CALCULATE to "Calculate",
            SHOW_MORE to "Show More",
            SHOW_LESS to "Show Less",
            YOUR_SAVINGS to "Your Savings",
            SAVINGS_SUB to "See how much you can save with prepayment",
            TOTAL_PAYMENT_LBL to "Total Payment",
            NEW_TENURE_LBL to "New Tenure",
            MONTH_SHORT to "Month",
            ADVANCED_OPTIONS to "Advanced options",
            EXTRA_TYPE to "Extra payment type",
            YEARS_LBL to "Years",
            MONTHS_LBL to "Months",
            MONTHLY to "Monthly",
            YEARLY to "Yearly",

            PDF_REPORT_DATE to "Report Date",
            PDF_YOUR_PARTNER to "Your Financial\nPartner",
            PDF_SCHEDULE_TITLE to "Schedule",
            PDF_SCHEDULE_SUB to "Your Complete Repayment Schedule",
            PDF_MONTHLY_BREAKDOWN to "Monthly Breakdown",
            PDF_EXTRA_BENEFIT to "Extra Payment Benefit",
            PDF_INCLUDES to "This Report Includes",
            PDF_TAGLINE to "Plan Today for a Better Tomorrow",
            PDF_EXTRA_PAYMENT to "Extra Payment",
            PDF_TOTAL_PAID to "Total Paid",
            PDF_NEXT_MONTHS to "Next %d Months",
            PDF_INC_1 to "Loan Summary & EMI Details",
            PDF_INC_2 to "Complete Repayment Schedule",
            PDF_INC_3 to "Interest Savings",
            PDF_INC_4 to "Final Payoff Information",
            PDF_EASY to "Easy to Use",
            PDF_SECURE to "Secure & Reliable",
            PDF_ALL_IN_ONE to "All in One App",
            PDF_SAVED to "You will save %s on interest and reduce %d months in your loan tenure.",
            PDF_NO to "No.",
            PDF_SAVED_MONTHS to "Saved %d months",
            PDF_TOTAL to "Total",
            PDF_MONTHLY_EXTRA to "Extra Monthly Payment",
            LOAN_AMOUNT_LBL to "Loan Amount",
            LOAN_TENURE_LBL to "Loan Tenure",
            REPAYMENT_SCHEDULE to "Repayment Schedule",
            PRINCIPAL_LBL to "Principal",
            EMI_LBL to "EMI",
            BALANCE_LBL to "Balance",
            MONTHLY_EMI to "Monthly EMI",

            BACK to "Back",
            REPORT to "Report",
            PDF_REPORT_SUB to "Detailed calculation report",
            PDF_HIGHLIGHTS to "Report Highlights",
            PDF_GEN_INC_1 to "Accurate results",
            PDF_GEN_INC_2 to "Complete breakdown",
            PDF_GEN_INC_3 to "Easy to understand",
            PDF_GEN_INC_4 to "Share with anyone",
            PDF_GEN_NOTE to "All calculations are estimates for planning purposes.",
            PDF_PARAMETER to "Parameter",
            PDF_VALUE to "Value",
            PDF_SUCCESS to "PDF report generated",
            PDF_FAILED to "Could not generate PDF. Please try again.",
            PDF_OPEN_CHOOSER to "Open report",
            PDF_SHARE_CHOOSER to "Share report",
            PDF_SHARE_SUBJECT to "EMI Calculator Report",
            PDF_GET_APP to "Get the app"
        )
    }

    private val hi: Map<String, String> by lazy {
        mapOf(
            TOOL_EMI to "ईएमआई कैलकुलेटर",
            TOOL_HOME_CAR to "होम / कार लोन\nईएमआई",
            TOOL_SCHEDULE to "अमोर्टाइजेशन\nशेड्यूल",
            TOOL_PREPAY to "प्रीपेमेंट\nकैलकुलेटर",
            TOOL_ADVANCE to "एडवांस /\nप्रीपेमेंट",
            TOOL_YEARLY to "वार्षिक\nब्रेकअप",
            TOOL_ELIG to "लोन पात्रता\nजांच",
            TOOL_SIP to "एसआईपी कैलकुलेटर",
            TOOL_LUMPSUM to "लम्पसम कैलकुलेटर",
            TOOL_PPF to "पीपीएफ कैलकुलेटर",
            TOOL_FD to "एफडी कैलकुलेटर",
            TOOL_RD to "आरडी कैलकुलेटर",
            TOOL_SSY to "सुकन्या समृद्धि\n(SSY)",
            TOOL_SIMPLE to "साधारण ब्याज\nकैलकुलेटर",
            TOOL_COMPOUND to "चक्रवृद्धि ब्याज\nकैलकुलेटर",
            TOOL_GST to "जीएसटी कैलकुलेटर",
            TOOL_MARGIN to "प्रॉफिट मार्जिन\nकैलकुलेटर",
            TOOL_CAGR to "सीएजीआर कैलकुलेटर",
            TOOL_VAT to "वैट / सेल्स टैक्स\nकैलकुलेटर",
            TOOL_BROKERAGE to "ब्रोकरेज\nकैलकुलेटर",
            TOOL_INFLATION to "महंगाई कैलकुलेटर",
            TOOL_SALARY to "सैलरी कैलकुलेटर",
            TOOL_BREAKEVEN to "ब्रेक-ईवन पॉइंट\nकैलकुलेटर",
            TOOL_DISCOUNT_MARKUP to "डिस्काउंट और मार्कअप\nकैलकुलेटर",
            TOOL_DISCOUNT to "डिस्काउंट कैलकुलेटर",
            TOOL_PERCENT to "प्रतिशत\nकैलकुलेटर",
            TOOL_FUEL to "फ्यूल कॉस्ट कैलकुलेटर",
            TOOL_GRATUITY to "ग्रेच्युटी कैलकुलेटर",
            TOOL_AGE to "उम्र कैलकुलेटर",
            TOOL_SWP to "एसडब्ल्यूपी कैलकुलेटर",
            TOOL_EPF to "ईपीएफ कैलकुलेटर",
            TOOL_BMI to "बीएमआई कैलकुलेटर",
            TOOL_CURRENCY to "करेंसी कन्वर्टर",
            TOOL_TARGET to "एफडी/आरडी लक्ष्य\nकैलकुलेटर",
            TOOL_TIP to "टिप कैलकुलेटर",
            TOOL_UNIT to "यूनिट कन्वर्टर",
            TOOL_GOLD to "गोल्ड लोन\nकैलकुलेटर",
            TOOL_CAR to "कार लोन\nकैलकुलेटर",
            TOOL_HEALTH to "हेल्थ इंश्योरेंस\nकैलकुलेटर",
            TOOL_SKIP to "ईएमआई स्किप\nकैलकुलेटर",

            CAT_LOAN to "ईएमआई और लोन कैलकुलेटर",
            CAT_BANKING to "बैंकिंग और निवेश",
            CAT_BUSINESS to "बिजनेस और वित्तीय टूल्स",
            CAT_DAILY to "रोज़मर्रा और शॉपिंग टूल्स",
            CAT_EXTRA to "अतिरिक्त ज़रूरी टूल्स",

            HOME_TAGLINE to "आपकी वित्तीय ज़िंदगी के स्मार्ट टूल्स",
            VIEW_ALL to "सभी देखें",
            ALL_TOOLS to "सभी टूल्स",
            HOME_NOTE to "सभी कैलकुलेटर मुफ़्त हैं। आपका डेटा आपके फोन में ही रहता है।",
            HOME to "होम",

            AMOUNT to "राशि",
            PRINCIPAL_AMT to "मूलधन",
            RATE_PER_YEAR to "ब्याज दर (% प्रति वर्ष)",
            TIME_YEARS to "समय (वर्ष)",
            TOTAL_INTEREST_LBL to "कुल ब्याज",
            TOTAL_AMOUNT to "कुल राशि",
            MATURITY_AMT to "मैच्योरिटी राशि",
            INTEREST_EARNED_LBL to "कमाया गया ब्याज",
            TOTAL_DEPOSITED to "कुल जमा राशि",
            COMPOUNDING to "ब्याज जोड़ने का तरीका",
            YEARLY_DEPOSIT to "सालाना जमा राशि",
            INITIAL_VALUE to "शुरुआती राशि",
            FINAL_VALUE to "अंतिम राशि",
            DURATION_YEARS to "अवधि (वर्ष)",
            CAGR_RESULT to "सीएजीआर (सालाना वृद्धि)",
            ABS_GROWTH to "कुल वृद्धि",
            VAT_RATE to "वैट / सेल्स टैक्स दर (%)",
            ADD_TAX to "टैक्स जोड़ें",
            REMOVE_TAX to "टैक्स हटाएं",
            TAX_AMOUNT to "टैक्स राशि",
            TOTAL_WITH_TAX to "कुल (टैक्स के साथ)",
            BUY_PRICE to "खरीद भाव",
            SELL_PRICE to "बिक्री भाव",
            QUANTITY to "मात्रा",
            BROKERAGE_PCT to "ब्रोकरेज (%)",
            TOTAL_CHARGES to "कुल चार्ज और टैक्स",
            NET_PROFIT to "शुद्ध लाभ",
            TURNOVER to "टर्नओवर",
            TODAY_AMOUNT to "आज की राशि",
            INFLATION_RATE to "महंगाई दर (%)",
            FUTURE_COST to "भविष्य की लागत",
            VALUE_LOST to "पैसे की कम हुई वैल्यू",
            CTC_YEARLY to "सीटीसी (सालाना)",
            BASIC_PCT to "बेसिक सैलरी (सीटीसी का %)",
            PF_PCT to "पीएफ योगदान (%)",
            TAX_PCT to "इनकम टैक्स (%)",
            INHAND_MONTHLY to "इन-हैंड सैलरी (महीना)",
            INHAND_YEARLY to "इन-हैंड सैलरी (सालाना)",
            TOTAL_DEDUCTIONS to "कुल कटौती",
            FIXED_COST to "फिक्स्ड खर्च",
            PRICE_PER_UNIT to "प्रति यूनिट बिक्री भाव",
            VAR_COST to "प्रति यूनिट वेरिएबल लागत",
            BREAKEVEN_UNITS to "ब्रेक-ईवन यूनिट",
            BREAKEVEN_REVENUE to "ब्रेक-ईवन रेवेन्यू",
            MARKUP_PCT to "मार्कअप (%)",
            DISC_RATE to "डिस्काउंट (%)",
            FINAL_PRICE to "अंतिम कीमत",
            VALUE_A to "मान A",
            VALUE_B to "मान B",
            PERCENT_RESULT to "परिणाम",
            DISTANCE_KM to "दूरी (किमी)",
            MILEAGE to "माइलेज (किमी प्रति लीटर)",
            FUEL_PRICE to "ईंधन की कीमत (प्रति लीटर)",
            FUEL_NEEDED to "ईंधन की ज़रूरत",
            FUEL_COST to "कुल ईंधन खर्च",
            BASIC_DA to "बेसिक + डीए (महीना)",
            SERVICE_YEARS to "सर्विस के साल",
            GRATUITY_AMT to "ग्रेच्युटी राशि",
            DOB to "जन्म तिथि",
            AGE_RESULT to "उम्र",
            NEXT_BIRTHDAY to "अगला जन्मदिन",
            CORPUS to "निवेश राशि",
            MONTHLY_WITHDRAWAL to "महीने की निकासी",
            EXPECTED_RETURN_LBL to "अनुमानित रिटर्न (%)",
            FINAL_BALANCE to "अंतिम बैलेंस",
            TOTAL_WITHDRAWN to "कुल निकाली राशि",
            MONTHS_LASTED to "पैसा कितने महीने चलेगा",
            BASIC_SALARY to "बेसिक सैलरी (महीना)",
            EMP_PCT to "आपका योगदान (%)",
            EMPLOYER_PCT to "कंपनी का योगदान (%)",
            CURRENT_BALANCE to "मौजूदा ईपीएफ बैलेंस",
            EPF_YEARS to "रिटायरमेंट तक साल",
            EPF_RATE to "ईपीएफ ब्याज दर (%)",
            EPF_MATURITY to "ईपीएफ मैच्योरिटी राशि",
            HEIGHT_CM to "लंबाई (सेमी)",
            WEIGHT_KG to "वज़न (किलो)",
            BMI_RESULT to "आपका बीएमआई",
            BMI_CATEGORY to "श्रेणी",
            BMI_UNDER to "कम वज़न",
            BMI_NORMAL to "सामान्य",
            BMI_OVER to "ज़्यादा वज़न",
            BMI_OBESE to "मोटापा",
            FROM to "से",
            TO to "में",
            CONVERTED to "परिवर्तित राशि",
            TARGET_AMOUNT to "लक्ष्य राशि",
            MONTHLY_SAVING to "महीने की ज़रूरी बचत",
            YEARLY_SAVING to "सालाना ज़रूरी बचत",
            BILL_AMOUNT to "बिल राशि",
            TIP_PCT to "टिप (%)",
            SPLIT_BETWEEN to "कितने लोगों में बांटें",
            TIP_AMOUNT to "टिप राशि",
            TOTAL_BILL to "कुल बिल",
            PER_PERSON to "प्रति व्यक्ति",
            UNIT_VALUE to "मान",
            LENGTH to "लंबाई",
            WEIGHT to "वज़न",
            TEMPERATURE to "तापमान",
            AREA to "क्षेत्रफल",
            UNIT_RESULT to "बदला हुआ मान",
            DAY to "दिन",
            MONTH to "महीना",
            YEAR to "साल",
            YEARS_SHORT to "वर्ष",
            MONTHS_SHORT to "माह",
            DAYS_SHORT to "दिन",

            LOAN_DETAILS to "लोन की जानकारी",
            ENTER_LOAN_INFO to "अपने लोन की जानकारी भरें",
            EXTRA_PREPAYMENT to "अतिरिक्त प्रीपेमेंट",
            CALCULATE to "कैलकुलेट करें",
            SHOW_MORE to "और देखें",
            SHOW_LESS to "कम देखें",
            YOUR_SAVINGS to "आपकी बचत",
            SAVINGS_SUB to "देखें प्रीपेमेंट से कितनी बचत होगी",
            TOTAL_PAYMENT_LBL to "कुल भुगतान",
            NEW_TENURE_LBL to "नई अवधि",
            MONTH_SHORT to "महीना",
            ADVANCED_OPTIONS to "अतिरिक्त विकल्प",
            EXTRA_TYPE to "अतिरिक्त भुगतान का प्रकार",
            YEARS_LBL to "वर्ष",
            MONTHS_LBL to "महीने",
            MONTHLY to "महीना",
            YEARLY to "सालाना",

            PDF_REPORT_DATE to "रिपोर्ट की तारीख",
            PDF_YOUR_PARTNER to "आपका वित्तीय\nसाथी",
            PDF_SCHEDULE_TITLE to "शेड्यूल",
            PDF_SCHEDULE_SUB to "आपका पूरा भुगतान शेड्यूल",
            PDF_MONTHLY_BREAKDOWN to "महीने का ब्रेकडाउन",
            PDF_EXTRA_BENEFIT to "अतिरिक्त भुगतान का लाभ",
            PDF_INCLUDES to "इस रिपोर्ट में शामिल है",
            PDF_TAGLINE to "आज प्लान करें, बेहतर कल के लिए",
            PDF_EXTRA_PAYMENT to "अतिरिक्त भुगतान",
            PDF_TOTAL_PAID to "कुल भुगतान",
            PDF_NEXT_MONTHS to "अगले %d महीने",
            PDF_INC_1 to "लोन सारांश और ईएमआई जानकारी",
            PDF_INC_2 to "पूरा भुगतान शेड्यूल",
            PDF_INC_3 to "ब्याज में बचत",
            PDF_INC_4 to "अंतिम भुगतान की जानकारी",
            PDF_EASY to "इस्तेमाल में आसान",
            PDF_SECURE to "सुरक्षित और भरोसेमंद",
            PDF_ALL_IN_ONE to "सब कुछ एक ऐप में",
            PDF_SAVED to "आप ब्याज में %s बचाएंगे और लोन की अवधि %d महीने कम होगी।",
            PDF_NO to "क्र.",
            PDF_SAVED_MONTHS to "%d महीने बचे",
            PDF_TOTAL to "कुल",
            PDF_MONTHLY_EXTRA to "अतिरिक्त मासिक भुगतान",
            LOAN_AMOUNT_LBL to "लोन राशि",
            LOAN_TENURE_LBL to "लोन अवधि",
            REPAYMENT_SCHEDULE to "भुगतान शेड्यूल",
            PRINCIPAL_LBL to "मूलधन",
            EMI_LBL to "ईएमआई",
            BALANCE_LBL to "बकाया",
            MONTHLY_EMI to "मासिक ईएमआई",

            BACK to "वापस",
            REPORT to "रिपोर्ट",
            PDF_REPORT_SUB to "विस्तृत गणना रिपोर्ट",
            PDF_HIGHLIGHTS to "रिपोर्ट की मुख्य बातें",
            PDF_GEN_INC_1 to "सटीक परिणाम",
            PDF_GEN_INC_2 to "पूरा विवरण",
            PDF_GEN_INC_3 to "आसानी से समझें",
            PDF_GEN_INC_4 to "किसी के साथ भी साझा करें",
            PDF_GEN_NOTE to "सभी गणनाएँ योजना के लिए अनुमान हैं।",
            PDF_PARAMETER to "विवरण",
            PDF_VALUE to "राशि",
            PDF_SUCCESS to "PDF रिपोर्ट बन गई",
            PDF_FAILED to "PDF नहीं बन सकी। कृपया फिर कोशिश करें।",
            PDF_OPEN_CHOOSER to "रिपोर्ट खोलें",
            PDF_SHARE_CHOOSER to "रिपोर्ट साझा करें",
            PDF_SHARE_SUBJECT to "ईएमआई कैलकुलेटर रिपोर्ट",
            PDF_GET_APP to "ऐप प्राप्त करें"
        )
    }

    fun get(language: AppLanguage, key: String): String? = when (language) {
        AppLanguage.HINDI -> hi[key] ?: en[key]
        AppLanguage.CHHATTISGARHI -> hi[key] ?: en[key]
        else -> ToolStringsI18n.get(language, key) ?: en[key]
    }

    fun english(key: String): String? = en[key]
}
