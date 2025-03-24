#include <pthread.h>
#include <dlfcn.h>
#include <jni.h>
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
#include <stdint.h>
#include <cstring>
#include <string.h>
#include <wchar.h>
#include <endian.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <csignal>
#include <codecvt>
#include "json.hpp"
#include "AES.h"
#include "base64.h"
#include "Includes/Utils.h"

#define LDEBUG

#include "Includes/Logger.h"
#include "Includes/obfuscaterr.h"
#include "KittyMemory/MemoryPatch.h"

#include "Canvas/ESP.h"
#include "Canvas/StructsCommon.h"

#include "rapidjson/document.h"
#include "rapidjson/writer.h"
#include "rapidjson/stringbuffer.h"

#include <openssl/evp.h>
#include <openssl/pem.h>
#include <openssl/rsa.h>
#include <openssl/err.h>
#include <openssl/md5.h>

#include "curl/curl.h"

#define Vector3 Ragdoll3

bool contains(std::string in, std::string target) {
    if (strstr(in.c_str(), target.c_str())) {
        return true;
    }
    return false;
}

bool equals(std::string first, std::string second) {
    if (first == second) {
        return true;
    }
    return false;
}

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
bool aimbot = false, wallshot = false, norecoil = false, bunny = false, ammoh = false, firerate = false, fastk = false, fastbomb = false, gnuke = false;
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

//std::string g_key = "zxcvbnmlkjhgfdsa";
//std::string g_iv = "asdfghjklzxcvbnm";

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

struct MemoryStruct {
    char *memory;
    size_t size;
};

static size_t WriteMemoryCallback(void *contents, size_t size, size_t nmemb, void *userp) {
    size_t realsize = size * nmemb;
    auto *mem = (struct MemoryStruct *) userp;
    mem->memory = (char *) realloc(mem->memory, mem->size + realsize + 1);
    if (mem->memory == nullptr) {
        return 0;
    }
    memcpy(&(mem->memory[mem->size]), contents, realsize);
    mem->size += realsize;
    mem->memory[mem->size] = 0;

    return realsize;
}

std::string CalcMD5(std::string s) {
    std::string result;

    unsigned char hash[MD5_DIGEST_LENGTH];
    char tmp[4];

    MD5_CTX md5;
    MD5_Init(&md5);
    MD5_Update(&md5, s.c_str(), s.length());
    MD5_Final(hash, &md5);
    for (unsigned char i : hash) {
        sprintf(tmp, OBFUSCATE("%02x"), i);
        result += tmp;
    }
    return result;
}

std::string CalcSHA256(std::string s) {
    std::string result;
    unsigned char hash[SHA256_DIGEST_LENGTH];
    char tmp[4];
    SHA256_CTX sha256;
    SHA256_Init(&sha256);
    SHA256_Update(&sha256, s.c_str(), s.length());
    SHA256_Final(hash, &sha256);
    for (unsigned char i : hash) {
        sprintf(tmp, OBFUSCATE("%02x"), i);
        result += tmp;
    }
    return result;
}

std::string g_Token, g_Auth, encryption_key;
bool bValid = false;

std::string Login(const char *user_key) {
    using json = nlohmann::ordered_json;
    std::string userkey_in_string(user_key);
    char build_id[64] = {0};
    __system_property_get(OBFUSCATE("ro.build.display.id"), build_id);
    char build_hardware[64] = {0};
    __system_property_get(OBFUSCATE("ro.hardware"), build_hardware);
    std::string bKeyID;
    bKeyID.reserve(128);
    bKeyID += build_id;
    bKeyID += build_hardware;

    if (!bKeyID.empty()) {
        size_t pos = bKeyID.find(' ');
        while (pos != std::string::npos) {
            bKeyID.replace(pos, 1, "");
            pos = bKeyID.find(' ', pos);
        }
    }
    std::string UUID = bKeyID;
    std::string errMsg;
    struct MemoryStruct chunk {};
    chunk.memory = (char *)malloc(1);
    chunk.size = 0;

    CURL *curl = curl_easy_init();
    if (!curl) {
        return "CURL initialization failed";
    }
    
    CURLcode res;
    curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "POST");
    std::string sRedLink = OBFUSCATE("https://modkey.space/223/connect");
    curl_easy_setopt(curl, CURLOPT_URL, sRedLink.c_str());
    curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L);

    struct curl_slist *headers = NULL;
    headers = curl_slist_append(headers, OBFUSCATE("Content-Type: application/x-www-form-urlencoded"));
    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

    char data[4096];
    sprintf(data, OBFUSCATE("game=Standoff2&user_key=%s&serial=%s"), user_key, UUID.c_str());
    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, data);

    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteMemoryCallback);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, (void *)&chunk);
    curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L);
    curl_easy_setopt(curl, CURLOPT_SSL_VERIFYHOST, 0L);

    res = curl_easy_perform(curl);
    if (res == CURLE_OK) {
        return std::string(chunk.memory);
    } else {
        return std::string("error");
    }
    curl_easy_cleanup(curl);
}

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

std::string xor_cipher(const std::string &data, const std::string &key, bool mode) {
    std::string result = data;
    uint32_t key1 = 0x1EFF2FE1, key2 = 0x1E00A2E3;
    for (char c : key) {
        key1 = (key1 * 33) ^ static_cast<uint8_t>(c);
        key2 = (key2 * 31) + static_cast<uint8_t>(c);
    }
    for (size_t i = 0; i < result.size(); ++i) {
        if (mode) { // Encrypt
            result[i] = (result[i] << 3) | (result[i] >> 5);
            result[i] ^= static_cast<uint8_t>(key1 >> (i % 32));
            result[i] = (result[i] >> 2) | (result[i] << 6);
            result[i] ^= static_cast<uint8_t>(key2 >> ((i + 5) % 32));
        } else { // Decrypt
            result[i] ^= static_cast<uint8_t>(key2 >> ((i + 5) % 32));
            result[i] = (result[i] << 2) | (result[i] >> 6);
            result[i] ^= static_cast<uint8_t>(key1 >> (i % 32));
            result[i] = (result[i] >> 3) | (result[i] << 5);
        }
    }
    return result;
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
            ESPData = fdat;
        }
    }
}

void DrawESP(ESP esp, int screenWidth, int screenHeight) {
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
                    if (!obj.HasMember("nk") || !obj["nk"].IsString()) {
                        LOGE("Missing or invalid 'nk'");
                        continue;
                    }
                    std::string nname = obj["nk"].GetString();
                    if ((nname.length() > 0) && isHex(nname)) {
                        const char* nkname = xor_cipher(hex_to_string(nname), OBFUSCATE("System.Reflection"), false).c_str();
                        if (nkname && strlen(nkname) > 0)
                            esp.DrawTextNew(Color(255.0f, 255.0f, 255.0f, 255.0f), location - Rect(0, (location.height / 1.4f), 0, 0), nkname, 22, 1);
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

extern "C" {

JNIEXPORT jobjectArray  JNICALL
Java_ge_nikka_edk_FloatingWindow_getFeatureList(
        JNIEnv *env,
        jobject activityObject) {
    jobjectArray ret;
    const char *features[] = {
        "Textt_Skinchanger",//0
        "InputValuee_WeaponID",//1
		"ButtonCc_Set Weapon",//2
        "Button_Aim (Needs ESP!)",//3
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
        "Button_Wallshot (Needs ESP!)",//16
		"Button_No Recoil (Needs ESP!)",//17
        "Button_Bunnyhop (Needs ESP!)",//18
        "Button_Fire Rate (Needs ESP!)",//19
        "Button_Unlimited Ammo (Needs ESP!)",//20
        "Button_Fast Knife (Needs ESP!)",//21
        "Button_Fast Bomb (Needs ESP!)",//22
        "Button_Unlimited Grenades (Needs ESP!)",//23
        "Button_Grenade Nuke (Needs ESP!)",//24
        "Text_Experimental",//25
        "ButtonC_Add Items From JSON",//26
        "ButtonHide_Hide Icon"
    };
    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)env->NewObjectArray(Total_Feature, env->FindClass("java/lang/String"), env->NewStringUTF(""));
    for (int i = 0; i < Total_Feature; i++) env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));
    return (ret);
}

JNIEXPORT void JNICALL
Java_ge_nikka_edk_FloatingWindow_Call(
        JNIEnv *env,
        jobject activityObject,
        jint feature,
        jint value) {
    switch (feature) {
        case 1: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();
            
            data.AddMember("event", "value", allocator);
            data.AddMember("name", "wid", allocator);
            data.AddMember("value", value, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 2: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "setwp", allocator);
            data.AddMember("state", true, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 3: {
            aimbot = !aimbot;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "aimbot", allocator);
            data.AddMember("state", aimbot, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 4: {
            chams = !chams;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "chams", allocator);
            data.AddMember("state", chams, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 5: {
            isESP = !isESP;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "isesp", allocator);
            data.AddMember("state", isESP, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
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
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "esphp", allocator);
            data.AddMember("state", ESPHealth, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 9: {
            ESPNick = !ESPNick;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "espnick", allocator);
            data.AddMember("state", ESPNick, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 11: {
            cradius = value;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "value", allocator);
            data.AddMember("name", "cradius", allocator);
            data.AddMember("value", value, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 13: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "addskin", allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 14: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "clearskin", allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 16: {
            wallshot = !wallshot;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "wallshot", allocator);
            data.AddMember("state", wallshot, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 17: {
            norecoil = !norecoil;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "recoil", allocator);
            data.AddMember("state", norecoil, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 18: {
            bunny = !bunny;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "bunny", allocator);
            data.AddMember("state", bunny, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 19: {
            firerate = !firerate;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "firerate", allocator);
            data.AddMember("state", firerate, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 20: {
            ammoh = !ammoh;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "ammoh", allocator);
            data.AddMember("state", ammoh, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 21: {
            fastk = !fastk;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "fastk", allocator);
            data.AddMember("state", fastk, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 22: {
            fastbomb = !fastbomb;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "fastbomb", allocator);
            data.AddMember("state", fastbomb, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 23: {
            ugrenade = !ugrenade;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "ugrenade", allocator);
            data.AddMember("state", ugrenade, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 24: {
            gnuke = !gnuke;
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "gnuke", allocator);
            data.AddMember("state", gnuke, allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
        case 26: {
            rapidjson::Document data;
            data.SetObject();
            rapidjson::Document::AllocatorType& allocator = data.GetAllocator();

            data.AddMember("event", "button", allocator);
            data.AddMember("name", "jskin", allocator);

            rapidjson::StringBuffer sdata;
            rapidjson::Writer<rapidjson::StringBuffer> writer(sdata);
            data.Accept(writer);

            std::string eval(string_to_hex(xor_cipher(sdata.GetString(), OBFUSCATE("System.Reflection"), true)));
            sendto(clientSocket, eval.c_str(), eval.length(), 0, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
            break;
        }
    }
}


JNIEXPORT jstring JNICALL
Java_ge_nikka_edk_FloatingWindow_SliderString(
        JNIEnv *env,
        jobject clazz, jint feature, jint value) {
    return env->NewStringUTF(NULL);
}

JNIEXPORT jstring JNICALL
Java_ge_nikka_edk_FloatingWindow_engine(
        JNIEnv *env,
        jobject clazz) {
    return env->NewStringUTF(OBFUSCATE_KEY("Made by Nikka", '$'));
}

JNIEXPORT jint JNICALL
Java_ge_nikka_stclient_MainActivity_start(
        JNIEnv *env,
        jobject clazz) {
    serverAddr.sin_port = htons(atoi(OBFUSCATE("19132")));
    serverAddr.sin_family = AF_INET;
	serverAddr.sin_addr.s_addr = INADDR_ANY;
    clientSocket = socket(AF_INET, SOCK_DGRAM, 0);
    if (connect(clientSocket, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) != -1) {
        std::thread(EspSocket).detach();
        return 0;
    } else {
        return -1;
    }
}

JNIEXPORT void JNICALL
Java_ge_nikka_stclient_MainActivity_stopc(
        JNIEnv *env,
        jobject clazz) {
    close(clientSocket);
}

JNIEXPORT void JNICALL
Java_ge_nikka_edk_FloatingWindow_DrawOn(JNIEnv *env, jclass type, jobject espView, jobject canvas) {
		espOverlay = ESP(env, espView, canvas);
		if (espOverlay.isValid()) {
			DrawESP(espOverlay, espOverlay.getWidth(), espOverlay.getHeight());
		}
	}
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *globalEnv;
    vm->GetEnv((void **) &globalEnv, JNI_VERSION_1_6);
    //LOGI("LOGIN: %s", Login("NIKA5567").c_str());
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
	exit(0);
}