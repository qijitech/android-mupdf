Android-MuPDF
=============

MuPDF usage for magazine reading.

MuPDF developer team: http://mupdf.com/

Original source repository: https://github.com/muennich/mupdf.git

Native libs compiled from original source commit: https://github.com/muennich/mupdf/commit/6bff94b4aa739eabede431320329ff5167e4884e

With small change:
```
diff --git a/platform/android/viewer/jni/mupdf.c b/platform/android/viewer/jni/mupdf.c
index 5e04ff8..71b00ea 100644
--- a/platform/android/viewer/jni/mupdf.c
+++ b/platform/android/viewer/jni/mupdf.c
@@ -15,8 +15,8 @@
 #include "mupdf/fitz.h"
 #include "mupdf/pdf.h"

-#define JNI_FN(A) Java_com_artifex_mupdfdemo_ ## A
-#define PACKAGENAME "com/artifex/mupdfdemo"
+#define JNI_FN(A) Java_tech_qiji_android_mupdf_ ## A
+#define PACKAGENAME "tech/qiji/android/mupdf"

 #define LOG_TAG "libmupdf"
 #define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
```