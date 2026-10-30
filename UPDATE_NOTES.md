# Calculator UI, reports and reward update

- Shared header constrained to 94–124 dp, leaving content visible on calculator detail screens.
- Shared pale-blue fields, rounded cards and Clear / PDF / Share actions. EMI inputs now come before results, and clearing hides the result.
- Compare, Prepayment and Schedule tabs also use the compact blue header.
- EMI, eligibility and tool exports use the blue branded report family. Loan reports include the schedule; non-loan reports show their actual inputs/results.
- All report types share the existing per-install sequence. The counter advances after PDF output is written, survives restarts, and filenames include the number so exports do not overwrite each other. Clearing app data resets the counter.
- Long repayment schedules include every month on continuation pages. The first page retains the compact overview.
- Reward lasts 10 minutes; only full-screen ads pause. Banners remain for free users. Countdown state is remembered across recompositions and updates each second.
- Pro removes all advertising and lists VIP support. Debug auto-Pro is disabled so reward and banner behavior can be tested.
- Monthly/yearly Billing plan identifiers and prices remain unchanged.

## Required configuration
`billing/ProConfig.kt`: replace `SUPPORT_EMAIL = "your-email@example.com"` with your real support address. The support button deliberately refuses to launch the placeholder address. Existing blank legal URLs also need your published URLs before release.

## Verification
Source structure and integration checks were performed here. Android SDK, Gradle executable and wrapper JAR/scripts were not present in the supplied environment, so no APK build or emulator execution was possible. Open in Android Studio with the project's configured SDK/Gradle versions.

Added `ReportAndLayoutTest` instrumentation coverage for header visibility, shared PDF numbering, unique output files, full-schedule pagination and long generic reports. Run it on a device with `connectedDebugAndroidTest` after syncing. These device tests have NOT been run here.

Manual device checks: each calculator at 360 dp width and large font; clear/re-enter decimal values; PDF Hindi/English/currency display; reward completion vs dismissal; countdown across background/reopen and expiry; banner remaining during reward; purchased Pro hiding all ads; both Billing base plans; configured support email.
