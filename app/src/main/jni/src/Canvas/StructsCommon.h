#ifndef STRUCTSCOMM_H
#define STRUCTSCOMM_H
#pragma once

#include <iostream>
#include <string>
#include <thread>
#include <chrono>
#include <functional>
#include <vector>
#include <list>
#include <src/Unity/Ragdoll3.hpp>
#include <src/Unity/Ragdoll2.hpp>
using namespace std;

#define maxplayerCount 54
#define maxvehicleCount 54
#define maxitemsCount 200

std::string string_to_hex(const std::string& input) {
    static const char hex_digits[] = "0123456789ABCDEF";
    std::string output;
    output.reserve(input.length() * 2);
    for (unsigned char c : input) {
        output.push_back(hex_digits[c >> 4]);
        output.push_back(hex_digits[c & 15]);
    }
    return output;
}

int hex_value(unsigned char hex_digit) {
    static const signed char hex_values[256] = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
         0,  1,  2,  3,  4,  5,  6,  7,  8,  9, -1, -1, -1, -1, -1, -1,
        -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    };
    int value = hex_values[hex_digit];
    if (value == -1) throw std::invalid_argument(std::string(OBFUSCATE("invalid hex digit")));
    return value;
}

std::string hex_to_string(const std::string& input) {
    const auto len = input.length();
    if (len & 1) throw std::invalid_argument(std::string(OBFUSCATE("odd length")));
    std::string output;
    output.reserve(len / 2);
    for (auto it = input.begin(); it != input.end(); ) {
        int hi = hex_value(*it++);
        int lo = hex_value(*it++);
        output.push_back(hi << 4 | lo);
    }
    return output;
}

class Color {
public:
	float r;
	float g;
	float b;
	float a;

	Color() {
		this->r = 0;
		this->g = 0;
		this->b = 0;
		this->a = 0;
	}

	Color(float r, float g, float b, float a) {
		this->r = r;
		this->g = g;
		this->b = b;
		this->a = a;
	}

	Color(float r, float g, float b) {
		this->r = r;
		this->g = g;
		this->b = b;
		this->a = 255;
	}
	
	static Color Black(){
		return Color(0, 0, 0);
	}

	static Color White(){
		return Color(255, 255, 255);
	}

	static Color Green(){
		return Color(0, 255, 0);
	}
	
	static Color Blue(){
		return Color(0, 0, 255);
	}
	
	static Color Cyan(){
		return Color(0, 255, 255);
	}
	
	static Color DarkGolden(){
		return Color(218,165,32);
	}
	
	static Color Indigo(){
		return Color(75,0,130);
	}
	
	static Color Purple(){
		return Color(128,0,128);
	}
	
	static Color Pink() {
		return Color(255,192,203);
	}
	
	static Color Red() {
		return Color(255, 0, 0);
	}
	
	static Color Yellow() {
		return Color(255, 255, 0);
	}
	
	static Color Magenta() {
		return Color(255, 0, 255);
	}
};

class Rect {
public:
	float x;
	float y;
	float width;
	float height;

	Rect() {
		this->x = 0;
		this->y = 0;
		this->width = 0;
		this->height = 0;
	}

	Rect(float x, float y, float width, float height) {
		this->x = x;
		this->y = y;
		this->width = width;
		this->height = height;
	}

	bool operator==(const Rect &src) const {
		return (src.x == this->x && src.y == this->y && src.height == this->height &&
				src.width == this->width);
	}

	bool operator!=(const Rect &src) const {
		return (src.x != this->x && src.y != this->y && src.height != this->height &&
				src.width != this->width);
	}
    
    Rect operator+(const Rect &src) const {
        return Rect(this->x + src.x, this->y + src.y, this->width + src.width, this->height + src.height);
    }
    
    Rect operator-(const Rect &src) const {
        return Rect(this->x - src.x, this->y - src.y, this->width - src.width, this->height - src.height);
    }
};

struct FMatrix {
	float M[4][4];
};

struct Quat {
	float X;
	float Y;
	float Z;
	float W;
};

struct FTransform {
	Quat Rotation;
	Ragdoll3 Translation;
	Ragdoll3 Scale3D;
};

Ragdoll3 MarixToRagdoll(FMatrix matrix) {
	return Ragdoll3(matrix.M[3][0], matrix.M[3][1], matrix.M[3][2]);
}

FMatrix MatrixMulti(FMatrix m1, FMatrix m2) {
	FMatrix matrix = FMatrix();
	for (int i = 0; i < 4; i++) {
		for (int j = 0; j < 4; j++) {
			for (int k = 0; k < 4; k++) {
				matrix.M[i][j] += m1.M[i][k] * m2.M[k][j];
			}
		}
	}
	return matrix;
}

FMatrix TransformToMatrix(FTransform transform) {
	FMatrix matrix;

	matrix.M[3][0] = transform.Translation.X;
	matrix.M[3][1] = transform.Translation.Y;
	matrix.M[3][2] = transform.Translation.Z;

	float x2 = transform.Rotation.X + transform.Rotation.X;
	float y2 = transform.Rotation.Y + transform.Rotation.Y;
	float z2 = transform.Rotation.Z + transform.Rotation.Z;

	float xx2 = transform.Rotation.X * x2;
	float yy2 = transform.Rotation.Y * y2;
	float zz2 = transform.Rotation.Z * z2;

	matrix.M[0][0] = (1.0f - (yy2 + zz2)) * transform.Scale3D.X;
	matrix.M[1][1] = (1.0f - (xx2 + zz2)) * transform.Scale3D.Y;
	matrix.M[2][2] = (1.0f - (xx2 + yy2)) * transform.Scale3D.Z;

	float yz2 = transform.Rotation.Y * z2;
	float wx2 = transform.Rotation.W * x2;
	matrix.M[2][1] = (yz2 - wx2) * transform.Scale3D.Z;
	matrix.M[1][2] = (yz2 + wx2) * transform.Scale3D.Y;

	float xy2 = transform.Rotation.X * y2;
	float wz2 = transform.Rotation.W * z2;
	matrix.M[1][0] = (xy2 - wz2) * transform.Scale3D.Y;
	matrix.M[0][1] = (xy2 + wz2) * transform.Scale3D.X;

	float xz2 = transform.Rotation.X * z2;
	float wy2 = transform.Rotation.W * y2;
	matrix.M[2][0] = (xz2 + wy2) * transform.Scale3D.Z;
	matrix.M[0][2] = (xz2 - wy2) * transform.Scale3D.X;

	matrix.M[0][3] = 0;
	matrix.M[1][3] = 0;
	matrix.M[2][3] = 0;
	matrix.M[3][3] = 1;

	return matrix;
}

struct FRotator {
	float Pitch;
	float Yaw;
	float Roll;
};

FMatrix RotatorToMatrix(FRotator rotation) {
	float radPitch = rotation.Pitch * ((float)M_PI / 180.0f);
	float radYaw = rotation.Yaw * ((float)M_PI / 180.0f);
	float radRoll = rotation.Roll * ((float)M_PI / 180.0f);

	float SP = sinf(radPitch);
	float CP = cosf(radPitch);
	float SY = sinf(radYaw);
	float CY = cosf(radYaw);
	float SR = sinf(radRoll);
	float CR = cosf(radRoll);

	FMatrix matrix;

	matrix.M[0][0] = (CP * CY);
	matrix.M[0][1] = (CP * SY);
	matrix.M[0][2] = (SP);
	matrix.M[0][3] = 0;

	matrix.M[1][0] = (SR * SP * CY - CR * SY);
	matrix.M[1][1] = (SR * SP * SY + CR * CY);
	matrix.M[1][2] = (-SR * CP);
	matrix.M[1][3] = 0;

	matrix.M[2][0] = (-(CR * SP * CY + SR * SY));
	matrix.M[2][1] = (CY * SR - CR * SP * SY);
	matrix.M[2][2] = (CR * CP);
	matrix.M[2][3] = 0;

	matrix.M[3][0] = 0;
	matrix.M[3][1] = 0;
	matrix.M[3][2] = 0;
	matrix.M[3][3] = 1;

	return matrix;
}

struct MinimalViewInfo {
	Ragdoll3 Location;
	FRotator Rotation;
	float FOV;
};

Ragdoll2 WorldToScreen(Ragdoll3 worldLocation, MinimalViewInfo camViewInfo, int width, int height) {
	FMatrix tempMatrix = RotatorToMatrix(camViewInfo.Rotation);

	Ragdoll3 vAxisX(tempMatrix.M[0][0], tempMatrix.M[0][1], tempMatrix.M[0][2]);
	Ragdoll3 vAxisY(tempMatrix.M[1][0], tempMatrix.M[1][1], tempMatrix.M[1][2]);
	Ragdoll3 vAxisZ(tempMatrix.M[2][0], tempMatrix.M[2][1], tempMatrix.M[2][2]);

	Ragdoll3 vDelta = worldLocation - camViewInfo.Location;

	Ragdoll3 vTransformed(Ragdoll3::Dot(vDelta, vAxisY), Ragdoll3::Dot(vDelta, vAxisZ), Ragdoll3::Dot(vDelta, vAxisX));

	if (vTransformed.Z < 1.0f) {
		vTransformed.Z = 1.0f;
	}

	float fov = camViewInfo.FOV;
	float screenCenterX = (width / 2.0f);
	float screenCenterY = (height / 2.0f);

	return Ragdoll2(
		(screenCenterX + vTransformed.X * (screenCenterX / tanf(fov * ((float)M_PI / 360.0f))) / vTransformed.Z),
		(screenCenterY - vTransformed.Y * (screenCenterX / tanf(fov * ((float)M_PI / 360.0f))) / vTransformed.Z)
	);
}

#endif
