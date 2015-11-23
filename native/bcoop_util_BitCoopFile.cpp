#include <bcoop_util_BitCoopFile.h>

#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>

#include <stdio.h>
#include <iconv.h>
#include <string.h>
#include <locale.h>

#include <limits.h>
#include <stdlib.h>
#include <wchar.h>

char* convertToUTF8Path(JNIEnv * env, jbyteArray jAbsolutePathBytes){
	size_t pathNameUTF8Len = (size_t) env->GetArrayLength(jAbsolutePathBytes);
	jbyte *pathNameUTF8 = env->GetByteArrayElements(jAbsolutePathBytes, 0);
	char* filePathUTF8 = new char[pathNameUTF8Len+1];
	memcpy(filePathUTF8, pathNameUTF8, pathNameUTF8Len);
	filePathUTF8[pathNameUTF8Len] = 0x0;
	env->ReleaseByteArrayElements(jAbsolutePathBytes, pathNameUTF8, 0);
	return filePathUTF8;
}

JNIEXPORT void JNICALL Java_bcoop_util_BitCoopFile_getInformation(JNIEnv * env, jobject jthis, jbyteArray jAbsolutePathBytes){
	char* filePathUTF8 = 	convertToUTF8Path(env, jAbsolutePathBytes);

	struct stat fileStats;
	if(lstat(filePathUTF8, &fileStats) != 0){
		printf("lstat failed for %s\n", filePathUTF8);
		delete[] filePathUTF8;
		return;
	}

	jclass bitCoopClass = env->GetObjectClass(jthis);
	jfieldID fileTypeFid = env->GetFieldID(bitCoopClass, "fileType", "I" );

	if(S_ISREG(fileStats.st_mode)){
		env->SetIntField(jthis, fileTypeFid, bcoop_util_BitCoopFile_TYPE_FILE);
	}
	else if(S_ISDIR(fileStats.st_mode)){
		env->SetIntField(jthis, fileTypeFid, bcoop_util_BitCoopFile_TYPE_DIR);
	}
	else if(S_ISLNK(fileStats.st_mode)){
		env->SetIntField(jthis, fileTypeFid, bcoop_util_BitCoopFile_TYPE_LINK);

		char* pathDest = new char[512];
		int destLen = readlink(filePathUTF8, pathDest, 512);
		if(destLen < 0){
			fprintf(stderr, "'%s'\n", filePathUTF8);
			perror("readlink failed for");
		}
		else{
			pathDest[destLen]='\0';
			jfieldID linkDestinationFid = env->GetFieldID(bitCoopClass, "linkDestination", "Ljava/lang/String;" );
			jstring linkDestinationJString = env->NewStringUTF(pathDest);
			env->SetObjectField(jthis, linkDestinationFid, linkDestinationJString);
		}

		delete[] pathDest;
	}
	else if(S_ISBLK(fileStats.st_mode)){
		env->SetIntField(jthis, fileTypeFid, bcoop_util_BitCoopFile_TYPE_BLK);
	}
	else if(S_ISCHR(fileStats.st_mode)){
		env->SetIntField(jthis, fileTypeFid, bcoop_util_BitCoopFile_TYPE_CHR);
	}
	else if(S_ISFIFO(fileStats.st_mode)){
		env->SetIntField(jthis, fileTypeFid, bcoop_util_BitCoopFile_TYPE_FIFO);
	}
	else if(S_ISSOCK(fileStats.st_mode)){
		env->SetIntField(jthis, fileTypeFid, bcoop_util_BitCoopFile_TYPE_SOCK);
	}
	delete[] filePathUTF8;
}

JNIEXPORT void JNICALL Java_bcoop_util_BitCoopFile_setMode(JNIEnv *env, jobject jthis, jbyteArray jAbsolutePathBytes, jint mode){
	char* filePathUTF8 = 	convertToUTF8Path(env, jAbsolutePathBytes);
	
	int rc = chmod(filePathUTF8, mode);
	
	delete[] filePathUTF8;
}

JNIEXPORT void JNICALL Java_bcoop_util_BitCoopFile_createSymlink(JNIEnv *env, jobject jthis, jbyteArray jLinkAbsolutePathBytes, jbyteArray jLinkDestPathBytes){
	char* linkNameUTF8 = 	convertToUTF8Path(env, jLinkAbsolutePathBytes);
	char* linkDestinationUTF8 = 	convertToUTF8Path(env, jLinkDestPathBytes);

	int rc = symlink(linkDestinationUTF8, linkNameUTF8);
	if(rc != 0){
		perror("Cannot create symlink");
	}
	
	delete[] linkNameUTF8;
	delete[] linkDestinationUTF8;
}
