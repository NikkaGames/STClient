//
// Created by nikka on 4/3/25.
//

#ifndef STCLIENT_JNISTUFF_H

#include <jni.h>
#include <fstream>
#include "Includes/obfuscaterr.h"

jobject GetActivityContext(JNIEnv* env) {
    jclass uplayer = env->FindClass(OBFUSCATE("ge/nikka/stclient/MainActivity"));
    jfieldID cmeth = env->GetStaticFieldID(uplayer, OBFUSCATE("thiz"), OBFUSCATE("Lge/nikka/stclient/MainActivity;"));
    jobject currt = env->NewGlobalRef(env->GetStaticObjectField(uplayer, cmeth));
    return currt;
}

jobject GetContext(JNIEnv* globalEnv) {
    jclass activityThread = globalEnv->FindClass(OBFUSCATE("android/app/ActivityThread"));
    jmethodID currentActivityThread = globalEnv->GetStaticMethodID(activityThread, OBFUSCATE("currentActivityThread"), OBFUSCATE("()Landroid/app/ActivityThread;"));
    jobject at = globalEnv->CallStaticObjectMethod(activityThread, currentActivityThread);
    jmethodID getApplication = globalEnv->GetMethodID(activityThread, OBFUSCATE("getApplication"), OBFUSCATE("()Landroid/app/Application;"));
    jobject context = globalEnv->CallObjectMethod(at, getApplication);
    return context;
}

void Toast(JNIEnv *env, const char *text, int length) {
    jstring jstr = env->NewStringUTF(text);
    jclass toast = env->FindClass(OBFUSCATE("android/widget/Toast"));
    jmethodID methodMakeText =env->GetStaticMethodID(toast,OBFUSCATE("makeText"),OBFUSCATE("(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;"));
    jobject toastobj = env->CallStaticObjectMethod(toast, methodMakeText, GetContext(env), jstr, length);
    jmethodID methodShow = env->GetMethodID(toast, OBFUSCATE("show"), OBFUSCATE("()V"));
    env->CallVoidMethod(toastobj, methodShow);
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
bool isconnected(JNIEnv* env) {
    jclass cont = env->FindClass(OBFUSCATE("android/content/Context"));
    jclass cn = env->FindClass(OBFUSCATE("android/net/ConnectivityManager"));
    jclass netc = env->FindClass(OBFUSCATE("android/net/NetworkCapabilities"));
    jmethodID getser = env->GetMethodID(cont, OBFUSCATE("getSystemService"), OBFUSCATE("(Ljava/lang/String;)Ljava/lang/Object;"));
    jmethodID getcncaps = env->GetMethodID(cn, OBFUSCATE("getNetworkCapabilities"), OBFUSCATE("(Landroid/net/Network;)Landroid/net/NetworkCapabilities;"));
    jmethodID getact = env->GetMethodID(cn, OBFUSCATE("getActiveNetwork"), OBFUSCATE("()Landroid/net/Network;"));
    jmethodID gethas = env->GetMethodID(netc, OBFUSCATE("hasTransport"), OBFUSCATE("(I)Z"));
    jobject conser = env->CallObjectMethod(GetContext(env), getser, env->NewStringUTF(OBFUSCATE("connectivity")));
    jobject getcnact = env->CallObjectMethod(conser, getact);
    jobject getcnc = env->CallObjectMethod(conser, getcncaps, getcnact);
    bool networkVpn = env->CallBooleanMethod(getcnc, gethas, atoi(OBFUSCATE("4")));
    if (networkVpn) return true;
    std::ifstream netFile(std::string(OBFUSCATE("/proc/self/net/dev")));
    std::string line;
    std::vector<std::string> vpnInterfaces = {std::string(OBFUSCATE("tun0")), std::string(OBFUSCATE("ppp0")), std::string(OBFUSCATE("utun"))};
    while (std::getline(netFile, line)) {
        for (const auto& iface : vpnInterfaces) {
            if (line.find(iface) != std::string::npos) {
                return true;
            }
        }
    }
    std::ifstream routeFile(std::string(OBFUSCATE("/proc/self/net/route")));
    while (std::getline(routeFile, line)) {
        if (line.find(std::string(OBFUSCATE("ppp0"))) != std::string::npos || line.find(std::string(OBFUSCATE("tun0"))) != std::string::npos) {
            return true;
        }
    }
    return false;
}

void displayKeyboard(JNIEnv* env) {
    jclass uplayer = env->FindClass(OBFUSCATE("ge/nikka/stclient/MainActivity"));
    jfieldID cmeth = env->GetStaticFieldID(uplayer, OBFUSCATE("thiz"), OBFUSCATE("Lge/nikka/stclient/MainActivity;"));
    jobject currt = env->NewGlobalRef(env->GetStaticObjectField(uplayer, cmeth));
    jclass aycls = env->FindClass(OBFUSCATE("android/app/Activity"));
    jmethodID gss = env->GetMethodID(aycls, OBFUSCATE("getSystemService"), OBFUSCATE("(Ljava/lang/String;)Ljava/lang/Object;"));
    jobject ss = env->CallObjectMethod(currt, gss, env->NewStringUTF(OBFUSCATE("input_method")));
    jclass imcls = env->FindClass(OBFUSCATE("android/view/inputmethod/InputMethodManager"));
    jmethodID tgsifm = env->GetMethodID(imcls, OBFUSCATE("toggleSoftInput"), OBFUSCATE("(II)V"));
    env->CallVoidMethod(ss, tgsifm, 2,0);
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
void setDialogMD(jobject ctx, JNIEnv *env, const char *title, const char *msg){
    jclass Alert = env->FindClass(OBFUSCATE("android/app/AlertDialog$Builder"));
    jmethodID AlertCons = env->GetMethodID(Alert, OBFUSCATE("<init>"), OBFUSCATE("(Landroid/content/Context;)V"));
    jobject MainAlert = env->NewObject(Alert, AlertCons, ctx);
    jmethodID setTitle = env->GetMethodID(Alert, OBFUSCATE("setTitle"), OBFUSCATE("(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;"));
    env->CallObjectMethod(MainAlert, setTitle, env->NewStringUTF(title));
    jmethodID setMsg = env->GetMethodID(Alert, OBFUSCATE("setMessage"), OBFUSCATE("(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;"));
    env->CallObjectMethod(MainAlert, setMsg, env->NewStringUTF(msg));
    jmethodID setCa = env->GetMethodID(Alert, OBFUSCATE("setCancelable"), OBFUSCATE("(Z)Landroid/app/AlertDialog$Builder;"));
    env->CallObjectMethod(MainAlert, setCa, false);
    jmethodID create = env->GetMethodID(Alert, OBFUSCATE("create"), OBFUSCATE("()Landroid/app/AlertDialog;"));
    jobject creaetob = env->CallObjectMethod(MainAlert, create);
    jclass AlertN = env->FindClass(OBFUSCATE("android/app/AlertDialog"));
    jmethodID show = env->GetMethodID(AlertN, OBFUSCATE("show"), OBFUSCATE("()V"));
    env->CallVoidMethod(creaetob, show);
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
void copyText(JNIEnv* env, std::string &text) {
    jclass aycls = env->FindClass(OBFUSCATE("android/app/Application"));
    jmethodID gss = env->GetMethodID(aycls, OBFUSCATE("getSystemService"), OBFUSCATE("(Ljava/lang/String;)Ljava/lang/Object;"));
    jobject ss = env->CallObjectMethod(GetContext(env), gss, env->NewStringUTF(OBFUSCATE("clipboard")));
    jclass clcls = env->FindClass(OBFUSCATE("android/content/ClipData"));
    jmethodID clmeth = env->GetStaticMethodID(clcls, OBFUSCATE("newPlainText"), OBFUSCATE("(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Landroid/content/ClipData;"));
    jclass imcls = env->FindClass(OBFUSCATE("android/content/ClipboardManager"));
    jmethodID tgsifm = env->GetMethodID(imcls, OBFUSCATE("setPrimaryClip"), OBFUSCATE("(Landroid/content/ClipData;)V"));
    env->CallVoidMethod(ss, tgsifm, env->CallStaticObjectMethod(clcls, clmeth, env->NewStringUTF(OBFUSCATE("unknown")), env->NewStringUTF(text.c_str())));
    text.clear();
    Toast(env, OBFUSCATE("Copied to Clipboard!\nRedirecting..."), 1);
}

const char* readClip(JNIEnv* env, jobject context) {
    jclass uplr = env->FindClass(OBFUSCATE("com/unity3d/player/UnityPlayer"));
    jmethodID uobj = env->GetMethodID(uplr, OBFUSCATE("<init>"), OBFUSCATE("(Landroid/content/Context;)V"));
    jobject umain = env->NewObject(uplr, uobj, context);
    jmethodID lmao = env->GetMethodID(uplr, OBFUSCATE("getClipboardText"), OBFUSCATE("()Ljava/lang/String;"));
    jstring fvalue = static_cast<jstring>(env->CallObjectMethod(umain, lmao));
    return env->GetStringUTFChars(fvalue, 0);
}

std::string getStorage(JNIEnv* globalEnv) {
    jclass activityClass = globalEnv->FindClass(OBFUSCATE("android/os/Environment"));
    jmethodID getFilesDirMethod = globalEnv->GetStaticMethodID(activityClass, OBFUSCATE("getExternalStorageDirectory"), OBFUSCATE("()Ljava/io/File;"));
    jobject filesDirObj = globalEnv->CallStaticObjectMethod(activityClass, getFilesDirMethod);
    jclass fileClass = globalEnv->FindClass(OBFUSCATE("java/io/File"));
    jmethodID getPathMethod = globalEnv->GetMethodID(fileClass, OBFUSCATE("getAbsolutePath"), OBFUSCATE("()Ljava/lang/String;"));
    jstring cacheDir = (jstring) globalEnv->CallObjectMethod(filesDirObj, getPathMethod);
    return std::string(globalEnv->GetStringUTFChars(cacheDir, 0));
}

const char* getCacheDir(JNIEnv *env) {
    jclass activityClass = env->FindClass(OBFUSCATE("com/unity3d/player/UnityPlayerActivity"));
    jmethodID getFilesDirMethod = env->GetMethodID(activityClass, OBFUSCATE("getCacheDir"), OBFUSCATE("()Ljava/io/File;"));
    jobject filesDirObj = env->CallObjectMethod(GetContext(env), getFilesDirMethod);
    jclass fileClass = env->FindClass(OBFUSCATE("java/io/File"));
    jmethodID getPathMethod = env->GetMethodID(fileClass, OBFUSCATE("getAbsolutePath"), OBFUSCATE("()Ljava/lang/String;"));
    jstring pathObj = (jstring) env->CallObjectMethod(filesDirObj, getPathMethod);
    const char* filesDir = env->GetStringUTFChars(pathObj, NULL);
    //env->ReleaseStringUTFChars(pathObj, dir);
    return filesDir;
}

const char* getApkPath(JNIEnv *globalEnv, jobject context) {
    jclass contextClass = globalEnv->GetObjectClass(context);
    jmethodID getApplicationInfo = globalEnv->GetMethodID(contextClass, OBFUSCATE("getApplicationInfo"), OBFUSCATE("()Landroid/content/pm/ApplicationInfo;"));
    jobject ApplicationInfo_obj = globalEnv->CallObjectMethod(context, getApplicationInfo);
    jclass ApplicationInfoClass = globalEnv->GetObjectClass(ApplicationInfo_obj);
    jmethodID getPackageResourcePath = globalEnv->GetMethodID(contextClass, OBFUSCATE("getPackageResourcePath"), OBFUSCATE("()Ljava/lang/String;"));
    jstring mPackageFilePath = static_cast<jstring>(globalEnv->CallObjectMethod(context, getPackageResourcePath));
    return globalEnv->GetStringUTFChars(mPackageFilePath, 0);
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
std::string getApkSign(JNIEnv *env, jobject context) {
    jclass versionClass = env->FindClass(OBFUSCATE("android/os/Build$VERSION"));
    jfieldID sdkIntFieldID = env->GetStaticFieldID(versionClass, OBFUSCATE("SDK_INT"), OBFUSCATE("I"));
    int sdkInt = env->GetStaticIntField(versionClass, sdkIntFieldID);
    jclass contextClass = env->FindClass(OBFUSCATE("android/content/Context"));
    jmethodID pmMethod = env->GetMethodID(contextClass, OBFUSCATE("getPackageManager"), OBFUSCATE("()Landroid/content/pm/PackageManager;"));
    jobject pm = env->CallObjectMethod(context, pmMethod);
    jclass pmClass = env->GetObjectClass(pm);
    jmethodID piMethod = env->GetMethodID(pmClass, OBFUSCATE("getPackageInfo"), OBFUSCATE("(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;"));
    jmethodID pnMethod = env->GetMethodID(contextClass, OBFUSCATE("getPackageName"), OBFUSCATE("()Ljava/lang/String;"));
    jstring packageName = (jstring) env->CallObjectMethod(context, pnMethod);
    int flags;
    if (sdkInt >= atoi(OBFUSCATE("28"))) {
        flags = 0x08000000; // PackageManager.GET_SIGNING_CERTIFICATES
    } else {
        flags = 0x00000040; // PackageManager.GET_SIGNATURES
    }
    jobject packageInfo = env->CallObjectMethod(pm, piMethod, packageName, flags);
    jclass piClass = env->GetObjectClass(packageInfo);
    jobjectArray signatures;
    if (sdkInt >= atoi(OBFUSCATE("28"))) {
        jfieldID signingInfoField = env->GetFieldID(piClass, OBFUSCATE("signingInfo"), OBFUSCATE("Landroid/content/pm/SigningInfo;"));
        jobject signingInfoObject = env->GetObjectField(packageInfo, signingInfoField);
        jclass signingInfoClass = env->GetObjectClass(signingInfoObject);
        jmethodID signaturesMethod = env->GetMethodID(signingInfoClass, OBFUSCATE("getApkContentsSigners"), OBFUSCATE("()[Landroid/content/pm/Signature;"));
        jobject signaturesObject = env->CallObjectMethod(signingInfoObject, signaturesMethod);
        signatures = (jobjectArray) (signaturesObject);
    } else {
        jfieldID signaturesField = env->GetFieldID(piClass, OBFUSCATE("signatures"), OBFUSCATE("[Landroid/content/pm/Signature;"));
        jobject signaturesObject = env->GetObjectField(packageInfo, signaturesField);
        if (env->IsSameObject(signaturesObject, nullptr)) {
            return OBFUSCATE("");
        }
        signatures = (jobjectArray) (signaturesObject);
    }
    jobject firstSignature = env->GetObjectArrayElement(signatures, 0);
    jclass signatureClass = env->GetObjectClass(firstSignature);
    jmethodID signatureByteMethod = env->GetMethodID(signatureClass, OBFUSCATE("toByteArray"), OBFUSCATE("()[B"));
    jobject signatureByteArray = (jobject) env->CallObjectMethod(firstSignature, signatureByteMethod);
    jclass mdClass = env->FindClass((OBFUSCATE("java/security/MessageDigest")));
    jmethodID mdMethod = env->GetStaticMethodID(mdClass, OBFUSCATE("getInstance"), OBFUSCATE("(Ljava/lang/String;)Ljava/security/MessageDigest;"));
    jobject md5Object = env->CallStaticObjectMethod(mdClass, mdMethod, env->NewStringUTF(OBFUSCATE("MD5")));
    jmethodID mdUpdateMethod = env->GetMethodID(mdClass, OBFUSCATE("update"), OBFUSCATE("([B)V"));// The return value of this function is void, write V
    env->CallVoidMethod(md5Object, mdUpdateMethod, signatureByteArray);
    jmethodID mdDigestMethod = env->GetMethodID(mdClass, OBFUSCATE("digest"), OBFUSCATE("()[B"));
    jobject fingerPrintByteArray = env->CallObjectMethod(md5Object, mdDigestMethod);
    jsize byteArrayLength = env->GetArrayLength(static_cast<jarray>(fingerPrintByteArray));
    jbyte *fingerPrintByteArrayElements = env->GetByteArrayElements(static_cast<jbyteArray>(fingerPrintByteArray), JNI_FALSE);
    char *charArray = (char *) fingerPrintByteArrayElements;
    char *md5 = (char *) calloc(2 * byteArrayLength + 1, sizeof(char));
    int k;
    for (k = 0; k < byteArrayLength; k++) {
        sprintf(&md5[2 * k], OBFUSCATE("%02X"), charArray[k]);
    }
    std::string retr(md5);
    free(charArray);
    free(md5);
    return retr;
}

const char* getLocale(JNIEnv *env) {
    jclass activityClass = env->FindClass(OBFUSCATE("java/util/Locale"));
    jmethodID getdef = env->GetStaticMethodID(activityClass, OBFUSCATE("getDefault"), OBFUSCATE("()Ljava/util/Locale;"));
    jobject getdefobj = env->CallStaticObjectMethod(activityClass, getdef);
    jmethodID toStr = env->GetMethodID(activityClass, OBFUSCATE("toLanguageTag"), OBFUSCATE("()Ljava/lang/String;"));
    jstring loc = static_cast<jstring>(env->CallObjectMethod(getdefobj, toStr));
    return env->GetStringUTFChars(loc, 0);
}

static int GetAndroidApiLevel() {
    char prop_value[4];
    __system_property_get(OBFUSCATE("ro.build.version.sdk"), prop_value);
    return atoi(prop_value);
}

#define STCLIENT_JNISTUFF_H

#endif //STCLIENT_JNISTUFF_H
