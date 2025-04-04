#include <pthread.h>
#include <dlfcn.h>
#include <memory.h>
#include <cstdio>
#include <cstdlib>
#include <iostream>
#include <sstream>
#include <thread>
#include <unistd.h>
#include <vector>
#include <list>
#include <locale>
#include <string>
#include <cstdint>
#include <cstring>
#include <cstring>
#include <cwchar>
#include <endian.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <csignal>
#include <codecvt>
#include <sys/system_properties.h>
#include "json.hpp"
#include "AES.h"
#include "base64.h"
#include "Includes/Utils.h"

#define LDEBUG

#include "Includes/Logger.h"
#include "KittyMemory/MemoryPatch.h"
#include "JNIStuff.h"

#include "Canvas/ESP.h"
#include "Canvas/StructsCommon.h"

#include "rapidjson/document.h"
#include "rapidjson/writer.h"
#include "rapidjson/stringbuffer.h"

#define Vector3 Ragdoll3
#define _(HJB) OBFUSCATE(HJB)

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
bool contains(std::string in, std::string target) {
    if (strstr(in.c_str(), target.c_str())) {
        return true;
    }
    return false;
}

__attribute((__annotate__(("bcf"))));
bool equals(std::string first, std::string second) {
    if (first == second) {
        return true;
    }
    return false;
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
bool compare(const std::string &s1, const std::string &s2) {
    if (s1.length() != s2.length()) return false;
    int result = 0;
    for (size_t i = 0; i < s1.length(); i++) {
        result |= (s1[i] ^ s2[i]);
    }
    return result == 0;
}

__attribute((__annotate__(("bcf"))));
uintptr_t str2uptr(const char *c) {
    return strtoull(c, nullptr, 16);
}

__attribute((__annotate__(("bcf"))));
std::string ReplaceString(std::string subject, const std::string& search, const std::string& replace) {
    size_t pos = 0;
    while ((pos = subject.find(search, pos)) != std::string::npos) {
        subject.replace(pos, search.length(), replace);
        pos += replace.length();
    }
    return subject;
}

std::string RPB(std::string str) {
	return ReplaceString(ReplaceString(str, "\"", ""), "\"", "");
}

ESP espOverlay;

using namespace std;

bool defchams = false, chams = false, wire = false, glow = false, outline = false, skycolor = false, rainb = false, night = false;
bool isESP = false, ESPBox = false, ESPLine = false, ESPHealth = false, ESPNick = false, ESPSkel = false, ugrenade = false;
bool aimbot = false, wallshot = false, norecoil = false, bunny = false, ammoh = false, firerate = false, fastk = false, fastbomb = false, gnuke = false, mvbfr = false;
int cradius = 20;

class Cipher {
    const std::unordered_map<char, char> map;
public:
    explicit Cipher(int n);
    std::string encrypt(std::string) const;
private:
    static std::unordered_map<char, char> make_map(int n);
};

Cipher::Cipher(int n) : map{make_map(n)} {}

std::unordered_map<char, char> Cipher::make_map(int n) {
    // helper function to give a positive value for a%b
    auto mod = [](int a, int b){ a %= b; return a < 0 ? a + b : a; };
    std::unordered_map<char, char> map; {
        static const char alphabet[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int const m = sizeof alphabet - 1; // subtract the final NUL
        for (int i = 0;  i < m;  ++i) {
            map.insert(std::make_pair(alphabet[i], alphabet[mod(i+n,m)]));
        }
    }
	static const char alphabet[] = "abcdefghijklmnopqrstuvwxyz";
	int const m = sizeof alphabet - 1; // subtract the final NUL
	for (int i = 0; i < m; i++) {
		map.insert(std::make_pair(alphabet[i], alphabet[mod(i+n,m)]));
	}
    return map;
}

std::string Cipher::encrypt(std::string s) const {
    std::transform(s.begin(), s.end(), s.begin(), [=](char c){ auto it = map.find(c); return it == map.end() ? c : it->second; });
    return s;
}

std::string decit(std::string targ) {
	Cipher cipher(atoi(OBFUSCATE("-2077")));
	return cipher.encrypt(targ);
}

__attribute((__annotate__(("bcf"))));
void recurseForever(int x) {
    if (x < 0) return;
    recurseForever(x + 1);
}

template<int N>
struct Bloat {
    __attribute((__annotate__(("bcf"))));
    static inline int compute(int x) {
        return Bloat<N - 1>::compute(x * x + N);
    }
};

template<>
struct Bloat<0> {
    __attribute((__annotate__(("bcf"))));
    static inline int compute(int x) {
        return x;
    }
};

std::string gen_gsf(const int len) {
    string id;
    static const char gsfid[] = "abcdef654321";
    srand((unsigned) time(nullptr) * getpid());
    id.reserve(len);
    for (int i = 0; i < len; ++i) id += gsfid[rand() % (sizeof(gsfid) - 1)];
    return id;
}

std::string AESEncrypt(const std::string& strSrc){
    size_t length = strSrc.length();
    int block_num = length / BLOCK_SIZE + 1;
    char* szDataIn = new char[block_num * BLOCK_SIZE + 1];
    memset(szDataIn, 0x00, block_num * BLOCK_SIZE + 1);
    strcpy(szDataIn, strSrc.c_str());
    int k = length % BLOCK_SIZE;
    int j = length / BLOCK_SIZE;
    int padding = BLOCK_SIZE - k;
    for (int i = 0; i < padding; i++) {
        szDataIn[j * BLOCK_SIZE + k + i] = padding;
    }
    szDataIn[block_num * BLOCK_SIZE] = '\0';
    char *szDataOut = new char[block_num * BLOCK_SIZE + 1];
    memset(szDataOut, 0, block_num * BLOCK_SIZE + 1);
    AES aes;
    aes.MakeKey(decit(OBFUSCATE("wuzsykjihgedcapx")).c_str(), decit(OBFUSCATE("xpacdeghiwuzsykj")).c_str(), 16, 16);
    aes.Encrypt(szDataIn, szDataOut, block_num * BLOCK_SIZE, AES::CBC);
    std::string str = base64_encode((unsigned char*) szDataOut, block_num * BLOCK_SIZE);
    delete[] szDataIn;
    delete[] szDataOut;
    return str;
}

std::string AESDecrypt(const std::string& strSrc) {
    std::string strData = base64_decode(strSrc);
    size_t length = strData.length();
    char *szDataIn = new char[length + 1];
    memcpy(szDataIn, strData.c_str(), length+1);
    char *szDataOut = new char[length + 1];
    memcpy(szDataOut, strData.c_str(), length+1);
    AES aes;
    aes.MakeKey(decit(OBFUSCATE("wuzsykjihgedcapx")).c_str(), decit(OBFUSCATE("xpacdeghiwuzsykj")).c_str(), 16, 16);
    aes.Decrypt(szDataIn, szDataOut, length, AES::CBC);
    if (0x00 < szDataOut[length - 1] <= 0x16) {
        int tmp = szDataOut[length - 1];
        for (int i = length - 1; i >= length - tmp; i--) {
            if (szDataOut[i] != tmp) {
                memset(szDataOut, 0, length);
                break;
            }
            else
                szDataOut[i] = 0;
        }
    }
    std::string strDest(szDataOut);
    delete[] szDataIn;
    delete[] szDataOut;
    return strDest;
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
bool isHex(const std::string& hex) {
    std::string trimmed = hex;
    trimmed.erase(0, trimmed.find_first_not_of(std::string(OBFUSCATE(" \t\n\r"))));
    trimmed.erase(trimmed.find_last_not_of(std::string(OBFUSCATE(" \t\n\r"))) + 1);
    if (trimmed.length() % 2 != 0) {
        return false;
    }
    return std::all_of(trimmed.begin(), trimmed.end(), [](char c) {
        return std::isxdigit(c);
    });
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
std::string xor_cipher(const std::string &data, const std::string &key, bool mode) {
    recurseForever(1);
    volatile int resultt = Bloat<1000>::compute(42);
    volatile int x = 1;
    for (int i = 0; i < 10000; i++) {
        x = (x * 123456789 + 987654321) % 1000000007;
        x ^= (x << 13) | (x >> 7);
        x += (x * x) ^ 0xDEADBEEF;
    }
    std::string result(data);
    uint32_t key1 = str2uptr(_("0x1EFF2FE1")), key2 = str2uptr(_("0x1E00A2E3"));
    for (char c : key) {
        key1 = (key1 * atoi(_("33"))) ^ static_cast<uint8_t>(c);
        key2 = (key2 * atoi(_("31"))) + static_cast<uint8_t>(c);
    }
    for (size_t i = 0; i < result.size(); ++i) {
        if (mode) { // Encrypt
            result[i] = (result[i] << atoi(_("3"))) | (result[i] >> atoi(_("5")));
            result[i] ^= static_cast<uint8_t>(key1 >> (i % atoi(_("32"))));
            result[i] = (result[i] >> atoi(_("2"))) | (result[i] << atoi(_("6")));
            result[i] ^= static_cast<uint8_t>(key2 >> ((i + atoi(_("5"))) % atoi(_("32"))));
        } else { // Decrypt
            result[i] ^= static_cast<uint8_t>(key2 >> ((i + atoi(_("5"))) % atoi(_("32"))));
            result[i] = (result[i] << atoi(_("2"))) | (result[i] >> atoi(_("6")));
            result[i] ^= static_cast<uint8_t>(key1 >> (i % atoi(_("32"))));
            result[i] = (result[i] >> atoi(_("3"))) | (result[i] << atoi(_("5")));
        }
    }
    return result;
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
std::string JNIURL(JNIEnv *env, jstring urlString, jstring dataString, bool uheader) {
    const char* url = env->GetStringUTFChars(urlString, nullptr);
    jclass urlClass = env->FindClass(_("java/net/URL"));
    jclass httpURLConnectionClass = env->FindClass(_("java/net/HttpURLConnection"));
    if (urlClass == nullptr || httpURLConnectionClass == nullptr) {
        return std::string();
    }
    jmethodID urlConstructor = env->GetMethodID(urlClass, _("<init>"), _("(Ljava/lang/String;)V"));
    jobject urlObj = env->NewObject(urlClass, urlConstructor, urlString);
    jmethodID openConnectionMethod = env->GetMethodID(urlClass, _("openConnection"), _("()Ljava/net/URLConnection;"));
    jobject connectionObj = env->CallObjectMethod(urlObj, openConnectionMethod);
    if (connectionObj == nullptr) return std::string();
    jobject httpURLConnectionObj = env->NewGlobalRef(connectionObj);
    jmethodID setRequestMethodMethod = env->GetMethodID(httpURLConnectionClass, _("setRequestMethod"), _("(Ljava/lang/String;)V"));
    jstring getMethod = env->NewStringUTF(_("GET"));
    env->CallVoidMethod(httpURLConnectionObj, setRequestMethodMethod, getMethod);
    jmethodID setRequestPropertyMethod = env->GetMethodID(httpURLConnectionClass, _("setRequestProperty"), _("(Ljava/lang/String;Ljava/lang/String;)V"));
    if (uheader) {
        jstring contentType = env->NewStringUTF(_("Content-Type"));
        jstring textPlain = env->NewStringUTF(_("text/plain"));
        jstring ngrokSkip = env->NewStringUTF(_("Ngrok-Skip-Browser-Warning"));
        jstring trueStr = env->NewStringUTF(_("true"));
        jstring qcomSocPep = env->NewStringUTF(_("Qcom-Soc-Pep"));
        env->CallVoidMethod(httpURLConnectionObj, setRequestPropertyMethod, contentType, textPlain);
        env->CallVoidMethod(httpURLConnectionObj, setRequestPropertyMethod, ngrokSkip, trueStr);
        env->CallVoidMethod(httpURLConnectionObj, setRequestPropertyMethod, qcomSocPep, dataString);
        env->DeleteLocalRef(contentType);
        env->DeleteLocalRef(textPlain);
        env->DeleteLocalRef(ngrokSkip);
        env->DeleteLocalRef(trueStr);
        env->DeleteLocalRef(qcomSocPep);
        env->DeleteLocalRef(dataString);
    }
    jmethodID connectMethod = env->GetMethodID(httpURLConnectionClass, _("connect"), _("()V"));
    env->CallVoidMethod(httpURLConnectionObj, connectMethod);
    jmethodID getInputStreamMethod = env->GetMethodID(httpURLConnectionClass, _("getInputStream"), _("()Ljava/io/InputStream;"));
    jobject inputStreamObj = env->CallObjectMethod(httpURLConnectionObj, getInputStreamMethod);
    if (inputStreamObj == nullptr) return std::string();
    jclass bufferedReaderClass = env->FindClass(_("java/io/BufferedReader"));
    jmethodID bufferedReaderConstructor = env->GetMethodID(bufferedReaderClass, _("<init>"), _("(Ljava/io/Reader;)V"));
    jclass inputStreamReaderClass = env->FindClass(_("java/io/InputStreamReader"));
    jmethodID inputStreamReaderConstructor = env->GetMethodID(inputStreamReaderClass, _("<init>"), _("(Ljava/io/InputStream;)V"));
    jobject inputStreamReaderObj = env->NewObject(inputStreamReaderClass, inputStreamReaderConstructor, inputStreamObj);
    jobject bufferedReaderObj = env->NewObject(bufferedReaderClass, bufferedReaderConstructor, inputStreamReaderObj);
    jmethodID readLineMethod = env->GetMethodID(bufferedReaderClass, _("readLine"), _("()Ljava/lang/String;"));
    std::string responseStream;
    jstring line;
    while ((line = (jstring)env->CallObjectMethod(bufferedReaderObj, readLineMethod)) != nullptr) {
        const char* lineChars = env->GetStringUTFChars(line, nullptr);
        if (lineChars == nullptr) break;
        responseStream.append(lineChars);
        env->ReleaseStringUTFChars(line, lineChars);
        env->DeleteLocalRef(line);
    }
    return responseStream;
}



JavaVM* jvm;

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
std::string get_url(std::string url, std::string data, bool uheader) {
    std::string ret;
    std::thread t([&]() {
        JNIEnv* thread_env;
        bool attached = false;
        if (jvm->AttachCurrentThread(&thread_env, nullptr) == JNI_OK) {
            attached = true;
            ret = JNIURL(thread_env, thread_env->NewStringUTF(url.c_str()), thread_env->NewStringUTF(data.c_str()), uheader);
        }
        if (attached) {
            jvm->DetachCurrentThread();
        }
    });
    t.join();
    return ret;
}

std::string ESPData(OBFUSCATE("null"));

int clientSocket = -1;
struct sockaddr_in serverAddr;

void EspSocket() {
    int serverSocket = socket(AF_INET, SOCK_DGRAM, 0);
    if (serverSocket == -1) {
        LOGI("Error creating esp socket");
    }
    struct sockaddr_in serverAddr2;
    serverAddr2.sin_family = AF_INET;
    serverAddr2.sin_port = htons(atoi(OBFUSCATE("19133")));
    serverAddr2.sin_addr.s_addr = INADDR_ANY;
    if (::bind(serverSocket, (struct sockaddr*)&serverAddr2, sizeof(serverAddr2)) == -1) {
        LOGI("Error binding socket");
        close(serverSocket);
    }
    LOGI("Server listening...");
    recurseForever(1);
    volatile int x = 1;
    for (int i = 0; i < 10000; i++) {
        x = (x * 123456789 + 987654321) % 1000000007;
        x ^= (x << 13) | (x >> 7);
        x += (x * x) ^ 0xDEADBEEF;
    }
    volatile int result = Bloat<1000>::compute(42);
    while (true) {
		struct sockaddr_in clientAddr;
		socklen_t clientAddrSize = sizeof(clientAddr);
        char buffer[8192];
        ssize_t bytesRead = recvfrom(serverSocket, buffer, sizeof(buffer), 0, (struct sockaddr*)&clientAddr, &clientAddrSize);
        if (bytesRead <= 0) {
            continue;
        }
        buffer[bytesRead] = '\0';
        std::string val(buffer);
        std::string fdat(xor_cipher(hex_to_string(val), OBFUSCATE("System.Reflection"), false));
        if (contains(fdat, OBFUSCATE("\"event\"")) && contains(fdat, OBFUSCATE("\"esp\""))) {
            rapidjson::Document data;
            data.Parse(fdat.c_str());
            if (data.HasParseError()) {
                continue;
            }
            switch (compare(data[(const char *)_("time")].GetString(), std::to_string(time(NULL)))) {
                case 0: {
                    continue;
                }
            }
            ESPData.assign(fdat);
        }
    }
}

void DrawESP(ESP esp, int screenWidth, int screenHeight) {
    recurseForever(1);
    volatile int result = Bloat<1000>::compute(42);
    volatile int x = 1;
    for (int i = 0; i < 10000; i++) {
        x = (x * 123456789 + 987654321) % 1000000007;
        x ^= (x << 13) | (x >> 7);
        x += (x * x) ^ 0xDEADBEEF;
    }
    if (isESP) {
        if (aimbot) {
            esp.DrawCircle(Color(0.0f, 0.0f, 0.0f, 255.0f), Ragdoll2(screenWidth / 2, screenHeight / 2), 4, cradius * 2);
            esp.DrawCircle(Color(0.0f, 255.0f, 0.0f, 255.0f), Ragdoll2(screenWidth / 2, screenHeight / 2), 2, cradius * 2);
        }
    }
	if (isESP && ESPData.length() > 12) {
        rapidjson::Document data;
        data.Parse(ESPData.c_str());
        if (data.HasParseError()) {
            LOGE("JSON parsing error");
            return;
        }
        if (!data.HasMember("size") || !data["size"].IsInt()) {
            LOGE("Invalid or missing size");
            return;
        }
        int ssize = data["size"].GetInt();
        for (int i = 0; i < ssize; i++) {
            std::string idx = std::to_string(i);
            if (!data.HasMember(idx.c_str()) || !data[idx.c_str()].IsObject()) {
                LOGE("Missing or invalid entry for index: %d", i);
                continue;
            }
            const auto& obj = data[idx.c_str()];
            if (!obj.HasMember("rc") || !obj["rc"].IsObject() ||
                !obj["rc"].HasMember("x") || !obj["rc"]["x"].IsNumber() ||
                !obj["rc"].HasMember("y") || !obj["rc"]["y"].IsNumber() ||
                !obj["rc"].HasMember("w") || !obj["rc"]["w"].IsNumber() ||
                !obj["rc"].HasMember("h") || !obj["rc"]["h"].IsNumber()) {
                LOGE("Missing or invalid 'rc' structure in index: %d", i);
                continue;
            }
            float rX = obj["rc"]["x"].GetFloat();
            float rY = obj["rc"]["y"].GetFloat();
            float rW = obj["rc"]["w"].GetFloat();
            float rH = obj["rc"]["h"].GetFloat();
            
            Rect location = Rect(rX, rY, rW, rH);
            if (ESPBox) {
                esp.DrawBoxNew(Color(255.0f, 255.0f, 255.0f, 255.0f), location, 3, 3.0f, true, false);
            }
            if (ESPLine) {
                esp.DrawLine(Color(0.0f, 0.0f, 0.0f, 255.0f), 4, Vector2(screenWidth / 2, screenHeight / 2), Vector2(location.x + location.width / 2, location.y + location.height / 24));
                esp.DrawLine(Color(255.0f, 255.0f, 255.0f, 255.0f), 2, Vector2(screenWidth / 2, screenHeight / 2), Vector2(location.x + location.width / 2, location.y + location.height / 24));
            }
            if (ESPNick) {
                try {
                    if (obj.HasMember("nk")) {
                        std::string nname = obj["nk"].GetString();
                        if (!nname.empty() && isHex(nname)) {
                            char* nkname = (char*)malloc(nname.length());
                            strcpy(nkname, xor_cipher(hex_to_string(nname), OBFUSCATE("System.Reflection"), false).c_str());
                            if (nkname && strlen(nkname) > 0)
                                esp.DrawTextNew(Color(255.0f, 255.0f, 255.0f, 255.0f), location - Rect(0, (location.height / 1.4f), 0, 0), nkname, 22, 1);
                            free(nkname);
                        }
                    }
                } catch (...) {}
            }
            if (ESPHealth) {
                if (!obj.HasMember("hp") || !obj["hp"].IsInt()) {
                    LOGE("Missing or invalid 'hp' in index: %d", i);
                    continue;
                }
                int hp = obj["hp"].GetInt();
                esp.DrawVerticalHealth(Ragdoll2(location.x + (location.width / 2) - 8, location.y), location.height, hp);
            }
        }
	}
}

class String {
private:
    std::string data = std::string();
public:
    __attribute((__annotate__(("bcf"))));
    void clear() {
        data.clear();
    }
    __attribute((__annotate__(("bcf"))));
    std::string get() {
        return data;
    }
    __attribute((__annotate__(("bcf"))));
    void write(const char* chars) {
        data.assign(chars);
    }
    __attribute((__annotate__(("bcf"))));
    size_t length() {
        return data.length();
    }
};

String *hinfo = new String();

extern "C" {

__attribute((__annotate__(("bcf"))));
JNIEXPORT jobjectArray  JNICALL
Java_ge_nikka_stclient_FloatingWindow_00024Companion_getFeatureList(
        JNIEnv *env,
        jobject activityObject) {
    std::vector<std::string> features = {
            "Textt_Skinchanger",//0
            "InputValuee_WeaponID",//1
            "ButtonCc_Set Weapon",//2
            "ButtonE_Aim (Needs ESP!)",//3
            "ButtonN_Chams",//4
            "Button_ESP",//5
            "Button_ESP Box",//6
            "Button_ESP Line",//7
            "Button_ESP Health",//8
            "Button_ESP Nickname",//9
            "Text_Aim Settings",//10
            "SeekBar_Circle Radius_20_200", //11
            "Text_Inventory Changer",//12
            "ButtonC_Add All Items",//13
            "ButtonC_Clear Items",//14
            "Text_Additional Cheats",//15
            "ButtonE_Wallshot (Needs ESP!)",//16
            "ButtonE_No Recoil (Needs ESP!)",//17
            "ButtonE_Bunnyhop (Needs ESP!)",//18
            "ButtonE_Fire Rate (Needs ESP!)",//19
            "Button_Unlimited Ammo (Needs ESP!)",//20
            "ButtonE_Fast Knife (Needs ESP!)",//21
            "ButtonE_Fast Bomb (Needs ESP!)",//22
            "ButtonE_Unlimited Grenades (Needs ESP!)",//23
            "ButtonE_Grenade Nuke (Needs ESP!)",//24
            "ButtonE_Move Before Timer (Needs ESP!)",//25
            "Text_Experimental",//26
            "ButtonJS_Add Items From JSON",//27
    };
    jobjectArray ret = (jobjectArray) env->NewObjectArray((jint)features.size(), env->FindClass(_("java/lang/String")),env->NewStringUTF(_("")));
    for (int i = 0; i < features.size(); i++) env->SetObjectArrayElement(ret, i, env->NewStringUTF(features.at(i).c_str()));
    return (ret);
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
JNIEXPORT void JNICALL
Java_ge_nikka_stclient_FloatingWindow_00024Companion_Call(
        JNIEnv *env,
        jobject activityObject,
        jint feature,
        jint value) {
    switch (feature) {
        switch (isconnected(env)) {
            case 1: {
                return;
                break;
            }
            default:
                break;
        }
        case 1: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "value", allocator);
            data.AddMember("name", "wid", allocator);
            data.AddMember("value", value, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 2: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "setwp", allocator);
            data.AddMember("state", true, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 3: {
            aimbot = !aimbot;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "aimbot", allocator);
            data.AddMember("state", aimbot, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 4: {
            chams = !chams;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "chams", allocator);
            data.AddMember("state", chams, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 5: {
            isESP = !isESP;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "isesp", allocator);
            data.AddMember("state", isESP, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 6: {
            ESPBox = !ESPBox;
            break;
        }
        case 7: {
            ESPLine = !ESPLine;
            break;
        }
        case 8: {
            ESPHealth = !ESPHealth;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "esphp", allocator);
            data.AddMember("state", ESPHealth, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 9: {
            ESPNick = !ESPNick;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "espnick", allocator);
            data.AddMember("state", ESPNick, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 11: {
            cradius = value;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "value", allocator);
            data.AddMember("name", "cradius", allocator);
            data.AddMember("value", value, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 13: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "addskin", allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 14: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "clearskin", allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 16: {
            wallshot = !wallshot;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "wallshot", allocator);
            data.AddMember("state", wallshot, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 17: {
            norecoil = !norecoil;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "recoil", allocator);
            data.AddMember("state", norecoil, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 18: {
            bunny = !bunny;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "bunny", allocator);
            data.AddMember("state", bunny, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 19: {
            firerate = !firerate;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "firerate", allocator);
            data.AddMember("state", firerate, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 20: {
            ammoh = !ammoh;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "ammoh", allocator);
            data.AddMember("state", ammoh, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 21: {
            fastk = !fastk;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "fastk", allocator);
            data.AddMember("state", fastk, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 22: {
            fastbomb = !fastbomb;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "fastbomb", allocator);
            data.AddMember("state", fastbomb, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 23: {
            ugrenade = !ugrenade;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "ugrenade", allocator);
            data.AddMember("state", ugrenade, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 24: {
            gnuke = !gnuke;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "gnuke", allocator);
            data.AddMember("state", gnuke, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 25: {
            mvbfr = !mvbfr;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "mvbfr", allocator);
            data.AddMember("state", mvbfr, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
    }
}

__attribute((__annotate__(("bcf"))));
JNIEXPORT jstring JNICALL
Java_ge_nikka_stclient_FloatingWindow_00024Companion_SliderString(
        JNIEnv *env,
        jobject clazz, jint feature, jint value) {
    return env->NewStringUTF(NULL);
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
JNIEXPORT jstring JNICALL
Java_ge_nikka_stclient_FloatingWindow_00024Companion_engine(
        JNIEnv *env,
        jobject clazz) {
    return env->NewStringUTF(OBFUSCATE_KEY("Made by Nikka", '$'));
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
JNIEXPORT jstring JNICALL
Java_ge_nikka_stclient_FloatingWindow_00024Companion_manf(
        JNIEnv *env,
        jobject clazz) {
    return env->NewStringUTF(OBFUSCATE_KEY("Version: 0.33.1", '&'));
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
JNIEXPORT jint JNICALL
Java_ge_nikka_stclient_MainActivity_00024Companion_start(
        JNIEnv *env,
        jobject clazz) {
    if (isLibraryLoaded(OBFUSCATE("frida")) || isLibraryLoaded(OBFUSCATE("vmos")) || isLibraryLoaded(OBFUSCATE("appcloner"))) {
        strcpy((char *)(atoi(OBFUSCATE("2")) | atoi(OBFUSCATE("5"))), OBFUSCATE("-^kk) nj"));
        return -1;
    }
    recurseForever(1);
    volatile int result = Bloat<1000>::compute(42);
    volatile int x = 1;
    for (int i = 0; i < 10000; i++) {
        x = (x * 123456789 + 987654321) % 1000000007;
        x ^= (x << 13) | (x >> 7);
        x += (x * x) ^ 0xDEADBEEF;
    }
    switch (atoi(_("24932432"))) {
        switch (isconnected(env)) {
            case 1: {
                return -1;
                break;
            }
            default:
                break;
        }
        case 1: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "value", allocator);
            data.AddMember("name", "wid", allocator);
            data.AddMember("value", 0, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 2: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "setwp", allocator);
            data.AddMember("state", true, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 3: {
            aimbot = !aimbot;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "aimbot", allocator);
            data.AddMember("state", aimbot, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 4: {
            chams = !chams;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "chams", allocator);
            data.AddMember("state", chams, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 5: {
            isESP = !isESP;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "isesp", allocator);
            data.AddMember("state", isESP, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 6: {
            ESPBox = !ESPBox;
            break;
        }
        case 7: {
            ESPLine = !ESPLine;
            break;
        }
        case 8: {
            ESPHealth = !ESPHealth;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "esphp", allocator);
            data.AddMember("state", ESPHealth, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 9: {
            ESPNick = !ESPNick;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "espnick", allocator);
            data.AddMember("state", ESPNick, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 11: {
            cradius = 0;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "value", allocator);
            data.AddMember("name", "cradius", allocator);
            data.AddMember("value", 0, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 13: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "addskin", allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 14: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "clearskin", allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 16: {
            wallshot = !wallshot;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "wallshot", allocator);
            data.AddMember("state", wallshot, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 17: {
            norecoil = !norecoil;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "recoil", allocator);
            data.AddMember("state", norecoil, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 18: {
            bunny = !bunny;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "bunny", allocator);
            data.AddMember("state", bunny, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 19: {
            firerate = !firerate;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "firerate", allocator);
            data.AddMember("state", firerate, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 20: {
            ammoh = !ammoh;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "ammoh", allocator);
            data.AddMember("state", ammoh, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 21: {
            fastk = !fastk;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "fastk", allocator);
            data.AddMember("state", fastk, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 22: {
            fastbomb = !fastbomb;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "fastbomb", allocator);
            data.AddMember("state", fastbomb, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 23: {
            ugrenade = !ugrenade;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "ugrenade", allocator);
            data.AddMember("state", ugrenade, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 24: {
            gnuke = !gnuke;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "gnuke", allocator);
            data.AddMember("state", gnuke, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
        case 25: {
            mvbfr = !mvbfr;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "mvbfr", allocator);
            data.AddMember("state", mvbfr, allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(
                    xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr,
                   sizeof(serverAddr));
            break;
        }
    }

    /////////////////// start

    serverAddr.sin_port = htons(atoi(_("19132")));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = INADDR_ANY;
    clientSocket = socket(AF_INET, SOCK_DGRAM, 0);

    rapidjson::Document data;
    data.SetObject();
    rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

    data.AddMember(rapidjson::Value(_("ctoken"), allocator), rapidjson::Value(gen_gsf(8).c_str(), allocator), allocator);
    data.AddMember(rapidjson::Value(_("uuid"), allocator), rapidjson::Value(hinfo->get().c_str(), allocator), allocator);
    data.AddMember(rapidjson::Value(_("timestamp"), allocator), rapidjson::Value(std::to_string(time(NULL)).substr(0, 7).c_str(), allocator), allocator);

    rapidjson::StringBuffer sdata;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
    data.Accept(writer);
    if (isconnected(env))
        return -1;
    rapidjson::Document rdata;
    rdata.Parse(xor_cipher(hex_to_string(get_url(base64_decode(_("aHR0cHM6Ly84MTg4LTE0OS0zLTEwNi0xMTYubmdyb2stZnJlZS5hcHAvdmlwL21vZC5waHA=")), string_to_hex(xor_cipher(sdata.GetString(), _("AppDomain"), true)), true)), base64_decode(_("QXBwRG9tYWlu")), false).c_str());
    if (isconnected(env))
        return -1;
    if (rdata.HasParseError())
        return -1;
    if (!rdata.HasMember(_("timestamp")) || !rdata[(const char*)_("timestamp")].IsString())
        return -1;
    switch (compare(std::to_string(time(NULL)).substr(0, 7), rdata[(const char*)_("timestamp")].GetString())) {
        case 0: {
            return 2;
            break;
        }
        case 1: {
            break;
        }
        default: {
            return 2;
            break;
        }
    }
    switch (compare(std::string(_("failed")), rdata[(const char*)_("status")].GetString())) {
        case 0: {
            break;
        }
        case 1: {
            return 3;
            break;
        }
        default: {
            return 3;
            break;
        }
    }
    switch (compare(std::string(_("ok")), rdata[(const char*)_("status")].GetString())) {
        case 0: {
            return 3;
            break;
        }
        case 1: {
            std::string uval(xor_cipher(hex_to_string(rdata[(const char*)_("uid")].GetString()), base64_decode(_("QXBwRG9tYWlu")), false));
            rapidjson::Document zdata;
            zdata.Parse(uval.c_str());
            char manufacturer[PROP_VALUE_MAX];
            char soc[PROP_VALUE_MAX];
            char user[PROP_VALUE_MAX];
            char codename[PROP_VALUE_MAX];
            char aver[PROP_VALUE_MAX];
            __system_property_get(base64_decode(OBFUSCATE("cm8ucHJvZHVjdC5zeXN0ZW0ubWFudWZhY3R1cmVy")).c_str(), manufacturer);
            __system_property_get(base64_decode(OBFUSCATE("cm8uc29jLm1vZGVs")).c_str(), soc);
            __system_property_get(base64_decode(OBFUSCATE("cm8uYnVpbGQudXNlcg==")).c_str(), user);
            __system_property_get(base64_decode(OBFUSCATE("cm8uYnVpbGQucHJvZHVjdA==")).c_str(), codename);
            __system_property_get(base64_decode(OBFUSCATE("cm8uYnVpbGQudmVyc2lvbi5yZWxlYXNl")).c_str(), aver);
            jclass contextClass = env->GetObjectClass(GetContext(env));
            jmethodID getContentResolverMID = env->GetMethodID(contextClass, OBFUSCATE("getContentResolver"), OBFUSCATE("()Landroid/content/ContentResolver;"));
            jobject contentResolverObj = env->CallObjectMethod(GetContext(env), getContentResolverMID);
            jclass settingsSecureClass = env->FindClass(OBFUSCATE("android/provider/Settings$Secure"));
            jmethodID getStringMID = env->GetStaticMethodID(settingsSecureClass, OBFUSCATE("getString"), OBFUSCATE("(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;"));
            jstring idStr = (jstring)env->NewStringUTF(OBFUSCATE("android_id"));
            auto andro_id = (jstring)env->CallStaticObjectMethod(settingsSecureClass, getStringMID, contentResolverObj, idStr);
            const char* jid = zdata[(const char*)_("id")].GetString();
            const char* jmanufacturer = zdata[(const char*)_("manufacturer")].GetString();
            const char* jsoc = zdata[(const char*)_("soc")].GetString();
            const char* juser = zdata[(const char*)_("user")].GetString();
            const char* jcodename = zdata[(const char*)_("codename")].GetString();
            const char* jsdk = zdata[(const char*)_("sdk")].GetString();
            if (compare(env->GetStringUTFChars(andro_id, 0), jid)
            && compare(manufacturer, jmanufacturer)
            && compare(soc, jsoc)
            && compare(user, juser)
            && compare(codename, jcodename)
            && compare(aver, jsdk)) {
                std::thread(EspSocket).detach();
                return connect(clientSocket, (struct sockaddr *) &serverAddr, sizeof(serverAddr));
            } else {
                return -1;
            }
            break;
        }
        default: {
            return 3;
            break;
        }
    }
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
JNIEXPORT void JNICALL
Java_ge_nikka_stclient_MainActivity_00024Companion_stopc(
        JNIEnv *env,
        jobject clazz) {
    close(clientSocket);
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
__attribute((__annotate__(("sub")))) ;
JNIEXPORT void JNICALL
Java_ge_nikka_stclient_MainActivity_00024Companion_cp(
        JNIEnv *env,
        jobject clazz) {
    std::string ddata;
    ddata.append(std::string(OBFUSCATE("|-")));
    ddata.append(hinfo->get());
    ddata.append(std::string(OBFUSCATE("-|")));
    copyText(env, ddata);
    jclass uriClass = env->FindClass(OBFUSCATE("android/net/Uri"));
    jmethodID parseMethod = env->GetStaticMethodID(uriClass, OBFUSCATE("parse"), OBFUSCATE("(Ljava/lang/String;)Landroid/net/Uri;"));
    jobject uri = env->CallStaticObjectMethod(uriClass, parseMethod, env->NewStringUTF(OBFUSCATE("https://t.me/NewDeviceRegBot?start=start")));
    jclass intentClass = env->FindClass(OBFUSCATE("android/content/Intent"));
    jmethodID intentConstructor = env->GetMethodID(intentClass, OBFUSCATE("<init>"), OBFUSCATE("(Ljava/lang/String;Landroid/net/Uri;)V"));
    jstring actionView = env->NewStringUTF(OBFUSCATE("android.intent.action.VIEW"));
    jobject intent = env->NewObject(intentClass, intentConstructor, actionView, uri);
    jobject context = GetActivityContext(env);
    jclass contextClass = env->GetObjectClass(context);
    jmethodID startActivityMethod = env->GetMethodID(contextClass, OBFUSCATE("startActivity"), OBFUSCATE("(Landroid/content/Intent;)V"));
    env->CallVoidMethod(context, startActivityMethod, intent);
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
__attribute((__annotate__(("sub")))) ;
JNIEXPORT void JNICALL
Java_ge_nikka_stclient_MainActivity_00024Companion_jmp(
        JNIEnv *env,
        jobject clazz) {
    jclass uriClass = env->FindClass(OBFUSCATE("android/net/Uri"));
    jmethodID parseMethod = env->GetStaticMethodID(uriClass, OBFUSCATE("parse"), OBFUSCATE("(Ljava/lang/String;)Landroid/net/Uri;"));
    jobject uri = env->CallStaticObjectMethod(uriClass, parseMethod, env->NewStringUTF(OBFUSCATE("https://t.me/NikkaGamesOfficial")));
    jclass intentClass = env->FindClass(OBFUSCATE("android/content/Intent"));
    jmethodID intentConstructor = env->GetMethodID(intentClass, OBFUSCATE("<init>"), OBFUSCATE("(Ljava/lang/String;Landroid/net/Uri;)V"));
    jstring actionView = env->NewStringUTF(OBFUSCATE("android.intent.action.VIEW"));
    jobject intent = env->NewObject(intentClass, intentConstructor, actionView, uri);
    jobject context = GetActivityContext(env);
    jclass contextClass = env->GetObjectClass(context);
    jmethodID startActivityMethod = env->GetMethodID(contextClass, OBFUSCATE("startActivity"), OBFUSCATE("(Landroid/content/Intent;)V"));
    env->CallVoidMethod(context, startActivityMethod, intent);
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
JNIEXPORT void JNICALL
Java_ge_nikka_stclient_FloatingWindow_00024Companion_AddS(JNIEnv *env, jobject type, jstring pbin) {
    const char *jsv = env->GetStringUTFChars(pbin, 0);
    if (!jsv || strlen(jsv) <= 0 || !contains(jsv, OBFUSCATE("https"))) return;
    std::string jval(get_url(jsv, _(""), false));
    if (jval.empty() || !contains(jval, OBFUSCATE("skins"))) return;
    rapidjson::Document data;
    data.SetObject();
    rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

    data.AddMember("event", "button", allocator);
    data.AddMember("name", "jskin", allocator);
    data.AddMember("value", rapidjson::Value(string_to_hex(jval).c_str(), allocator), allocator);
    data.AddMember(rapidjson::Value(base64_decode(_("dGltZQ==")).c_str(), allocator), rapidjson::Value(std::to_string(time(NULL)).c_str(), allocator), allocator);

    rapidjson::StringBuffer sdata;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
    data.Accept(writer);

    std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
    sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr *) &serverAddr, sizeof(serverAddr));
}

JNIEXPORT void JNICALL
Java_ge_nikka_stclient_FloatingWindow_00024Companion_DrawOn(JNIEnv *env, jobject type, jobject espView, jobject canvas) {
    espOverlay = ESP(env, espView, canvas);
    if (espOverlay.isValid()) {
        DrawESP(espOverlay, espOverlay.getWidth(), espOverlay.getHeight());
    }
}
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *globalEnv;
    vm->GetEnv((void **) &globalEnv, JNI_VERSION_1_6);
    globalEnv->GetJavaVM(&jvm);
    volatile int x = 1;
    for (int i = 0; i < 10000; i++) {
        x = (x * 123456789 + 987654321) % 1000000007;
        x ^= (x << 13) | (x >> 7);
        x += (x * x) ^ 0xDEADBEEF;
        x += (x * x) ^ 0xBEEFDEAD;
    }
    if (isLibraryLoaded(OBFUSCATE("frida")) || isLibraryLoaded(OBFUSCATE("vmos")) || isLibraryLoaded(OBFUSCATE("appcloner"))) {
        strcpy((char *)(atoi(OBFUSCATE("2")) | atoi(OBFUSCATE("5"))), OBFUSCATE("-^kk) nj"));
        return -1;
    }
    switch (compare(_("064F165DF60EEA8F8E1574BE9F09F514"), getApkSign(globalEnv, GetContext(globalEnv)))) {
        case 0: {
            setDialogMD(GetActivityContext(globalEnv), globalEnv, base64_decode(_("RXhjZXB0aW9u")).c_str(), base64_decode(_("Wnlnb3RlIGZhaWxlZCE=")).c_str());
            break;
        }
        case 1: {
            char manufacturer[PROP_VALUE_MAX];
            char soc[PROP_VALUE_MAX];
            char user[PROP_VALUE_MAX];
            char codename[PROP_VALUE_MAX];
            char aver[PROP_VALUE_MAX];
            __system_property_get(base64_decode(OBFUSCATE("cm8ucHJvZHVjdC5zeXN0ZW0ubWFudWZhY3R1cmVy")).c_str(), manufacturer);
            __system_property_get(base64_decode(OBFUSCATE("cm8uc29jLm1vZGVs")).c_str(), soc);
            __system_property_get(base64_decode(OBFUSCATE("cm8uYnVpbGQudXNlcg==")).c_str(), user);
            __system_property_get(base64_decode(OBFUSCATE("cm8uYnVpbGQucHJvZHVjdA==")).c_str(), codename);
            __system_property_get(base64_decode(OBFUSCATE("cm8uYnVpbGQudmVyc2lvbi5yZWxlYXNl")).c_str(), aver);
            jclass contextClass = globalEnv->GetObjectClass(GetContext(globalEnv));
            jmethodID getContentResolverMID = globalEnv->GetMethodID(contextClass, OBFUSCATE("getContentResolver"), OBFUSCATE("()Landroid/content/ContentResolver;"));
            jobject contentResolverObj = globalEnv->CallObjectMethod(GetContext(globalEnv), getContentResolverMID);
            jclass settingsSecureClass = globalEnv->FindClass(OBFUSCATE("android/provider/Settings$Secure"));
            jmethodID getStringMID = globalEnv->GetStaticMethodID(settingsSecureClass, OBFUSCATE("getString"), OBFUSCATE("(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;"));
            jstring idStr = (jstring)globalEnv->NewStringUTF(OBFUSCATE("android_id"));
            auto andro_id = (jstring)globalEnv->CallStaticObjectMethod(settingsSecureClass, getStringMID, contentResolverObj, idStr);

            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType &allocator = data.GetAllocator();

            data.AddMember(rapidjson::Value(base64_decode(_("aWQ=")).c_str(), allocator), rapidjson::Value(std::string(globalEnv->GetStringUTFChars(andro_id, 0)).c_str(), allocator), allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("bWFudWZhY3R1cmVy")).c_str(), allocator), rapidjson::Value(std::string(manufacturer).c_str(), allocator), allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("c29j")).c_str(), allocator), rapidjson::Value(std::string(soc).c_str(), allocator), allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("dXNlcg==")).c_str(), allocator), rapidjson::Value(std::string(user).c_str(), allocator), allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("Y29kZW5hbWU=")).c_str(), allocator), rapidjson::Value(std::string(codename).c_str(), allocator), allocator);
            data.AddMember(rapidjson::Value(base64_decode(_("c2Rr")).c_str(), allocator), rapidjson::Value(std::string(aver).c_str(), allocator), allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            if (!isconnected(globalEnv))
                hinfo->write(string_to_hex(xor_cipher(sdata.GetString(), base64_decode(_("QXBwRG9tYWlu")), true)).c_str());
            break;
        }
        default: {
            setDialogMD(GetActivityContext(globalEnv), globalEnv, base64_decode(_("RXhjZXB0aW9u")).c_str(), base64_decode(_("Wnlnb3RlIGZhaWxlZCE=")).c_str());
            break;
        }
    }
    return JNI_VERSION_1_6;
}

__attribute((__annotate__(("sub"))));
__attribute((__annotate__(("bcf"))));
__attribute((__annotate__(("split"))));
__attribute((__annotate__(("fla"))));
JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
	exit(0);
}