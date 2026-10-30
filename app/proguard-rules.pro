# ============================================================================
# R8 / ProGuard rules  (isMinifyEnabled = true, isShrinkResources = true)
#
# Goal: release build SHRINK + OBFUSCATE ho, lekin crash BILKUL na ho.
#
# Purani file me blanket rules the:
#   -keep class com.aksh.emicalculator.** { *; }
#   -keep class kotlin.** / androidx.compose.** / androidx.lifecycle.** /
#   com.google.android.gms.** / kotlinx.coroutines.** { *; }
# Inse R8 kuch bhi shrink/obfuscate nahi kar pata tha (APK bada, code khula).
# Yeh sab libraries (Compose, Lifecycle, Coroutines, Play Services/AdMob,
# Play Billing) apne "consumer rules" khud bundle karti hain, isliye unhe
# yahan dobara keep karne ki zarurat NAHI hai.
#
# App me reflection / Gson / Moshi / Room / Retrofit nahi hai (JSON org.json
# se hota hai), isliye sirf neeche ki chhoti safe-list kaafi hai.
# ============================================================================

# 1) ViewModel: `by viewModels()` default factory constructor ko reflection se
#    dhoondhti hai. Constructor rename/remove hua to app start par hi crash.
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class com.aksh.emicalculator.viewmodel.EmiViewModel {
    public <init>(android.app.Application);
}

# 2) Enums: SharedPreferences me .name se save hote hain aur valueOf()/values()
#    se wapas padhe jaate hain (ThemeMode, AccentColor, AppLanguage, LoanCategory).
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 3) Agar kabhi @Keep annotation lagao to R8 use respect karega.
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# 4) Play Console me padhne layayak stack trace (line numbers) chahiye.
#    mapping.txt bundle ke saath Play Console me auto-upload hoti hai.
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod, Exceptions

# 5) SDK providers / init (CRASH FIX: "Unable to get provider ... installProvider").
#    Ads SDK, Play Services, Billing aur androidx.startup app start par ContentProvider
#    ke through reflection se init hote hain. Inki classes rename/strip hui to app
#    launch hote hi crash. Isliye yeh SDKs fully keep hain (app ka apna code phir bhi
#    shrink + obfuscate hota hai).
-keep class * extends android.content.ContentProvider { <init>(); }
-keep class androidx.startup.** { *; }
-keep class * extends androidx.startup.Initializer { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

# 5b) ROOT-CAUSE CRASH FIX: "Failed to create an instance of androidx.work.impl.WorkDatabase".
#     Google Ads SDK apne saath WorkManager (aur uska Room database) laata hai, aur
#     WorkManager app start par androidx.startup se init hota hai. Room apni generated
#     class (WorkDatabase_Impl) ko naam se reflection me dhoondhti hai. R8 usse
#     rename/strip kar deta hai -> app launch par hi crash. App WorkManager direct
#     use nahi karta, phir bhi yeh rules zaroori hain.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep class androidx.work.impl.WorkDatabase_Impl { *; }
-keep class androidx.work.** { *; }
-keep class androidx.room.** { *; }
-dontwarn androidx.work.**
-dontwarn androidx.room.**

# 6) Optional / compile-time-only classes: missing-class warnings se build fail na ho.
-dontwarn com.google.android.play.core.**
-dontwarn org.jetbrains.annotations.**
-dontwarn javax.annotation.**
