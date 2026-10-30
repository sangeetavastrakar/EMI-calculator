# EMI Calculator v6.0 (versionCode 6 nil edited

## Pro / subscription (Google Play Billing)
Play Console -> Monetize -> Subscriptions:
- Product ID: `premium`
- Base plans: `monthly` (Rs 21) and `yearly` (Rs 151). Add a free-trial offer if you want a trial.
Settings: `billing/ProConfig.kt` (test switch, product ids, support email, terms/privacy links).

## Test switch
`ProConfig.TEST_UNLOCK_PRO = true` unlocks Pro for free in APK builds (for your own testing).
It is forced OFF automatically for AAB (bundle) builds, so it can never reach the Play Store.

## Ads
`ui/components/AdConfig.kt` (USE_REAL_ADS, HIDE_ADS_FOR_PRO) + App ID in AndroidManifest.xml.

## PDF / Share + big ad (v6.2)
- PDF aur Share ab sabke liye free hain. Share = sirf text + Play Store link (PDF attach nahi hota).
- Free users ko PDF/Share dabane par bada (interstitial) ad dikhta hai; Pro users ko nahi.
- `AdConfig.REAL_INTERSTITIAL_AD_UNIT_ID` me AdMob ka real Interstitial ad unit id daalein.
- Debug APK me `TEST_UNLOCK_PRO=true` se Pro on rehta hai, isliye ad dekhne ke liye usse false karke test karein.
