# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Manter as classes de modelo intactas (evita que o ProGuard renomeie variáveis usadas no banco)
-keep class com.example.govacation.model.** { *; }

# Manter as classes de conexão com o banco de dados (SQLite)
-keep class com.example.govacation.data.** { *; }

# Manter classes utilitárias (como o CriptoUtil, para não quebrar a geração de Hash da senha)
-keep class com.example.govacation.util.** { *; }