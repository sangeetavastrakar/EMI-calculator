# Dark mode and banner layout update

Dark, Light and System now resolve the actual app theme. Calculator cards, fields, text, result panels and savings panels use theme-aware colors. PDFs retain the printable blue/light branding.

Home has one in-list banner after categories 2 and 4 (never after the final category). Reward windows pause full-screen ads only; banners remain. Pro users receive neither inline nor anchored banners.

Bottom banner uses the available width and an anchored adaptive size. It has an Advertisement label and 8 dp separation above/below; no overlay, rounded clipping of ad creative, or action buttons attached to the ad. Keyboard behavior still hides the bottom area. Failed loads collapse the slot without an automatic retry loop. Ad views pause/resume with lifecycle and are destroyed on disposal. AdMob controls refresh, not app timers.

No revenue/retention guarantee is implied. Test with test ad units before release. Source checks passed. Android SDK/Gradle are unavailable in this environment: build and emulator/device tests have NOT been run. Added ThemeModeTest for light/dark transitions and calculator color changes. Device QA: Light/Dark/System and restart, keyboard, rotation, ad failure, category list, completed reward and Pro purchase.

Version name remains 6.
