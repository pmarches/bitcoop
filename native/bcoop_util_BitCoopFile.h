/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class bcoop_util_BitCoopFile */

#ifndef _Included_bcoop_util_BitCoopFile
#define _Included_bcoop_util_BitCoopFile
#ifdef __cplusplus
extern "C" {
#endif
#undef bcoop_util_BitCoopFile_serialVersionUID
#define bcoop_util_BitCoopFile_serialVersionUID 301077366599181567LL
#undef bcoop_util_BitCoopFile_serialVersionUID
#define bcoop_util_BitCoopFile_serialVersionUID -3776367595828759505LL
#undef bcoop_util_BitCoopFile_TYPE_FILE
#define bcoop_util_BitCoopFile_TYPE_FILE 0L
#undef bcoop_util_BitCoopFile_TYPE_LINK
#define bcoop_util_BitCoopFile_TYPE_LINK 1L
#undef bcoop_util_BitCoopFile_TYPE_DIR
#define bcoop_util_BitCoopFile_TYPE_DIR 2L
#undef bcoop_util_BitCoopFile_TYPE_BLK
#define bcoop_util_BitCoopFile_TYPE_BLK 3L
#undef bcoop_util_BitCoopFile_TYPE_CHR
#define bcoop_util_BitCoopFile_TYPE_CHR 4L
#undef bcoop_util_BitCoopFile_TYPE_SOCK
#define bcoop_util_BitCoopFile_TYPE_SOCK 5L
#undef bcoop_util_BitCoopFile_TYPE_FIFO
#define bcoop_util_BitCoopFile_TYPE_FIFO 6L
/*
 * Class:     bcoop_util_BitCoopFile
 * Method:    getInformation
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_bcoop_util_BitCoopFile_getInformation
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     bcoop_util_BitCoopFile
 * Method:    setMode
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL Java_bcoop_util_BitCoopFile_setMode
  (JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     bcoop_util_BitCoopFile
 * Method:    createSymlink
 * Signature: ([B[B)V
 */
JNIEXPORT void JNICALL Java_bcoop_util_BitCoopFile_createSymlink
  (JNIEnv *, jobject, jbyteArray, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif