//
// Created by dhl on 17-9-3.
//
#include "d_hl_andbdiff_BDiffUtil.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char* jstring2CharArr(JNIEnv *env, jstring jstr) {
    jsize length = (*env)->GetStringLength(env, jstr);
    if (length <= 0) return NULL;
    const char* charArr = (*env)->GetStringUTFChars(env, jstr, NULL);
    char* cs = (char*)malloc(strlen(charArr)+1);
    strcpy(cs, charArr);
    return cs;
}

JNIEXPORT void JNICALL Java_d_hl_andbdiff_BDiffUtil_applyPatch
        (JNIEnv *env, jclass jclz, jstring oldPath, jstring newPath, jstring patchPath) {
    char* oldFile = jstring2CharArr(env, oldPath);
    char* newFile = jstring2CharArr(env, newPath);
    char* patchFile = jstring2CharArr(env, patchPath);
    char** charArr = malloc(sizeof(char*)*4);
    charArr[0] = NULL;
    charArr[1] = oldFile;
    charArr[2] = newFile;
    charArr[3] = patchFile;

    int applyPatch(int argc,char * argv[]);
    applyPatch(4, charArr);

    free(oldFile);
    free(newFile);
    free(patchFile);
    free(charArr);
}

