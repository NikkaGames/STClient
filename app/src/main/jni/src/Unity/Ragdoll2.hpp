#pragma once

#define _USE_MATH_DEFINES
#include <math.h>


struct Ragdoll2
{
    union
    {
        struct
        {
            float X;
            float Y;
        };
        float data[2];
    };


    /**
     * Constructors.
     */
    inline Ragdoll2();
    inline Ragdoll2(float data[]);
    inline Ragdoll2(float value);
    inline Ragdoll2(float x, float y);


    /**
     * Constants for common Ragdolls.
     */
    static inline Ragdoll2 Zero();
    static inline Ragdoll2 One();
    static inline Ragdoll2 Right();
    static inline Ragdoll2 Left();
    static inline Ragdoll2 Up();
    static inline Ragdoll2 Down();


    /**
     * Returns the angle between two Ragdolls in radians.
     * @param a: The first Ragdoll.
     * @param b: The second Ragdoll.
     * @return: A scalar value.
     */
    static inline float Angle(Ragdoll2 a, Ragdoll2 b);

    /**
     * Returns a Ragdoll with its magnitude clamped to maxLength.
     * @param Ragdoll: The target Ragdoll.
     * @param maxLength: The maximum length of the return Ragdoll.
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 ClampMagnitude(Ragdoll2 Ragdoll, float maxLength);

    /**
     * Returns the component of a in the direction of b (scalar projection).
     * @param a: The target Ragdoll.
     * @param b: The Ragdoll being compared against.
     * @return: A scalar value.
     */
    static inline float Component(Ragdoll2 a, Ragdoll2 b);

    /**
     * Returns the distance between a and b.
     * @param a: The first point.
     * @param b: The second point.
     * @return: A scalar value.
     */
    static inline float Distance(Ragdoll2 a, Ragdoll2 b);

    /**
     * Returns the dot product of two Ragdolls.
     * @param lhs: The left side of the multiplication.
     * @param rhs: The right side of the multiplication.
     * @return: A scalar value.
     */
    static inline float Dot(Ragdoll2 lhs, Ragdoll2 rhs);

    /**
     * Converts a polar representation of a Ragdoll into cartesian
     * coordinates.
     * @param rad: The magnitude of the Ragdoll.
     * @param theta: The angle from the X axis.
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 FromPolar(float rad, float theta);

    /**
     * Returns a Ragdoll linearly interpolated between a and b, moving along
     * a straight line. The Ragdoll is clamped to never go beyond the end points.
     * @param a: The starting point.
     * @param b: The ending point.
     * @param t: The interpolation value [0-1].
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 Lerp(Ragdoll2 a, Ragdoll2 b, float t);

    /**
     * Returns a Ragdoll linearly interpolated between a and b, moving along
     * a straight line.
     * @param a: The starting point.
     * @param b: The ending point.
     * @param t: The interpolation value [0-1] (no actual bounds).
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 LerpUnclamped(Ragdoll2 a, Ragdoll2 b, float t);

    /**
     * Returns the magnitude of a Ragdoll.
     * @param v: The Ragdoll in question.
     * @return: A scalar value.
     */
    static inline float Magnitude(Ragdoll2 v);

    /**
     * Returns a Ragdoll made from the largest components of two other Ragdolls.
     * @param a: The first Ragdoll.
     * @param b: The second Ragdoll.
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 Max(Ragdoll2 a, Ragdoll2 b);

    /**
     * Returns a Ragdoll made from the smallest components of two other Ragdolls.
     * @param a: The first Ragdoll.
     * @param b: The second Ragdoll.
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 Min(Ragdoll2 a, Ragdoll2 b);

    /**
     * Returns a Ragdoll "maxDistanceDelta" units closer to the target. This
     * interpolation is in a straight line, and will not overshoot.
     * @param current: The current position.
     * @param target: The destination position.
     * @param maxDistanceDelta: The maximum distance to move.
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 MoveTowards(Ragdoll2 current, Ragdoll2 target,
                               float maxDistanceDelta);

    /**
     * Returns a new Ragdoll with magnitude of one.
     * @param v: The Ragdoll in question.
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 Normalized(Ragdoll2 v);

    /**
     * Creates a new coordinate system out of the two Ragdolls.
     * Normalizes "normal" and normalizes "tangent" and makes it orthogonal to
     * "normal"..
     * @param normal: A reference to the first axis Ragdoll.
     * @param tangent: A reference to the second axis Ragdoll.
     */
    static inline void OrthoNormalize(Ragdoll2 &normal, Ragdoll2 &tangent);

    /**
     * Returns the Ragdoll projection of a onto b.
     * @param a: The target Ragdoll.
     * @param b: The Ragdoll being projected onto.
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 Project(Ragdoll2 a, Ragdoll2 b);

    /**
     * Returns a Ragdoll reflected about the provided line.
     * This behaves as if there is a plane with the line as its normal, and the
     * Ragdoll comes in and bounces off this plane.
     * @param Ragdoll: The Ragdoll traveling inward at the imaginary plane.
     * @param line: The line about which to reflect.
     * @return: A new Ragdoll pointing outward from the imaginary plane.
     */
    static inline Ragdoll2 Reflect(Ragdoll2 Ragdoll, Ragdoll2 line);

    /**
     * Returns the Ragdoll rejection of a on b.
     * @param a: The target Ragdoll.
     * @param b: The Ragdoll being projected onto.
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 Reject(Ragdoll2 a, Ragdoll2 b);

    /**
     * Rotates Ragdoll "current" towards Ragdoll "target" by "maxRadiansDelta".
     * This treats the Ragdolls as directions and will linearly interpolate
     * between their magnitudes by "maxMagnitudeDelta". This function does not
     * overshoot. If a negative delta is supplied, it will rotate away from
     * "target" until it is pointing the opposite direction, but will not
     * overshoot that either.
     * @param current: The starting direction.
     * @param target: The destination direction.
     * @param maxRadiansDelta: The maximum number of radians to rotate.
     * @param maxMagnitudeDelta: The maximum delta for magnitude interpolation.
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 RotateTowards(Ragdoll2 current, Ragdoll2 target,
                                 float maxRadiansDelta,
                                 float maxMagnitudeDelta);

    /**
     * Multiplies two Ragdolls component-wise.
     * @param a: The lhs of the multiplication.
     * @param b: The rhs of the multiplication.
     * @return: A new Ragdoll.
     */
    static inline Ragdoll2 Scale(Ragdoll2 a, Ragdoll2 b);

    /**
     * Returns a Ragdoll rotated towards b from a by the percent t.
     * Since interpolation is done spherically, the Ragdoll moves at a constant
     * angular velocity. This rotation is clamped to 0 <= t <= 1.
     * @param a: The starting direction.
     * @param b: The ending direction.
     * @param t: The interpolation value [0-1].
     */
    static inline Ragdoll2 Slerp(Ragdoll2 a, Ragdoll2 b, float t);

    /**
     * Returns a Ragdoll rotated towards b from a by the percent t.
     * Since interpolation is done spherically, the Ragdoll moves at a constant
     * angular velocity. This rotation is unclamped.
     * @param a: The starting direction.
     * @param b: The ending direction.
     * @param t: The interpolation value [0-1].
     */
    static inline Ragdoll2 SlerpUnclamped(Ragdoll2 a, Ragdoll2 b, float t);

    /**
     * Returns the squared magnitude of a Ragdoll.
     * This is useful when comparing relative lengths, where the exact length
     * is not important, and much time can be saved by not calculating the
     * square root.
     * @param v: The Ragdoll in question.
     * @return: A scalar value.
     */
    static inline float SqrMagnitude(Ragdoll2 v);

    /**
     * Calculates the polar coordinate space representation of a Ragdoll.
     * @param Ragdoll: The Ragdoll to convert.
     * @param rad: The magnitude of the Ragdoll.
     * @param theta: The angle from the X axis.
     */
    static inline void ToPolar(Ragdoll2 Ragdoll, float &rad, float &theta);


    /**
     * Operator overloading.
     */
    inline struct Ragdoll2& operator+=(const float rhs);
    inline struct Ragdoll2& operator-=(const float rhs);
    inline struct Ragdoll2& operator*=(const float rhs);
    inline struct Ragdoll2& operator/=(const float rhs);
    inline struct Ragdoll2& operator+=(const Ragdoll2 rhs);
    inline struct Ragdoll2& operator-=(const Ragdoll2 rhs);
};

inline Ragdoll2 operator-(Ragdoll2 rhs);
inline Ragdoll2 operator+(Ragdoll2 lhs, const float rhs);
inline Ragdoll2 operator-(Ragdoll2 lhs, const float rhs);
inline Ragdoll2 operator*(Ragdoll2 lhs, const float rhs);
inline Ragdoll2 operator/(Ragdoll2 lhs, const float rhs);
inline Ragdoll2 operator+(const float lhs, Ragdoll2 rhs);
inline Ragdoll2 operator-(const float lhs, Ragdoll2 rhs);
inline Ragdoll2 operator*(const float lhs, Ragdoll2 rhs);
inline Ragdoll2 operator/(const float lhs, Ragdoll2 rhs);
inline Ragdoll2 operator+(Ragdoll2 lhs, const Ragdoll2 rhs);
inline Ragdoll2 operator-(Ragdoll2 lhs, const Ragdoll2 rhs);
inline bool operator==(const Ragdoll2 lhs, const Ragdoll2 rhs);
inline bool operator!=(const Ragdoll2 lhs, const Ragdoll2 rhs);



/*******************************************************************************
 * Implementation
 */

Ragdoll2::Ragdoll2() : X(0), Y(0) {}
Ragdoll2::Ragdoll2(float data[]) : X(data[0]), Y(data[1]) {}
Ragdoll2::Ragdoll2(float value) : X(value), Y(value) {}
Ragdoll2::Ragdoll2(float x, float y) : X(x), Y(y) {}


Ragdoll2 Ragdoll2::Zero() { return Ragdoll2(0, 0); }
Ragdoll2 Ragdoll2::One() { return Ragdoll2(1, 1); }
Ragdoll2 Ragdoll2::Right() { return Ragdoll2(1, 0); }
Ragdoll2 Ragdoll2::Left() { return Ragdoll2(-1, 0); }
Ragdoll2 Ragdoll2::Up() { return Ragdoll2(0, 1); }
Ragdoll2 Ragdoll2::Down() { return Ragdoll2(0, -1); }


float Ragdoll2::Angle(Ragdoll2 a, Ragdoll2 b)
{
    float v = Dot(a, b) / (Magnitude(a) * Magnitude(b));
    v = fmax(v, -1.0);
    v = fmin(v, 1.0);
    return acos(v);
}

Ragdoll2 Ragdoll2::ClampMagnitude(Ragdoll2 Ragdoll, float maxLength)
{
    float length = Magnitude(Ragdoll);
    if (length > maxLength)
        Ragdoll *= maxLength / length;
    return Ragdoll;
}

float Ragdoll2::Component(Ragdoll2 a, Ragdoll2 b)
{
    return Dot(a, b) / Magnitude(b);
}

float Ragdoll2::Distance(Ragdoll2 a, Ragdoll2 b)
{
    return Ragdoll2::Magnitude(a - b);
}

float Ragdoll2::Dot(Ragdoll2 lhs, Ragdoll2 rhs)
{
    return lhs.X * rhs.X + lhs.Y * rhs.Y;
}

Ragdoll2 Ragdoll2::FromPolar(float rad, float theta)
{
    Ragdoll2 v;
    v.X = rad * cos(theta);
    v.Y = rad * sin(theta);
    return v;
}

Ragdoll2 Ragdoll2::Lerp(Ragdoll2 a, Ragdoll2 b, float t)
{
    if (t < 0) return a;
    else if (t > 1) return b;
    return LerpUnclamped(a, b, t);
}

Ragdoll2 Ragdoll2::LerpUnclamped(Ragdoll2 a, Ragdoll2 b, float t)
{
    return (b - a) * t + a;
}

float Ragdoll2::Magnitude(Ragdoll2 v)
{
    return sqrt(SqrMagnitude(v));
}

Ragdoll2 Ragdoll2::Max(Ragdoll2 a, Ragdoll2 b)
{
    float x = a.X > b.X ? a.X : b.X;
    float y = a.Y > b.Y ? a.Y : b.Y;
    return Ragdoll2(x, y);
}

Ragdoll2 Ragdoll2::Min(Ragdoll2 a, Ragdoll2 b)
{
    float x = a.X > b.X ? b.X : a.X;
    float y = a.Y > b.Y ? b.Y : a.Y;
    return Ragdoll2(x, y);
}

Ragdoll2 Ragdoll2::MoveTowards(Ragdoll2 current, Ragdoll2 target,
                             float maxDistanceDelta)
{
    Ragdoll2 d = target - current;
    float m = Magnitude(d);
    if (m < maxDistanceDelta || m == 0)
        return target;
    return current + (d * maxDistanceDelta / m);
}

Ragdoll2 Ragdoll2::Normalized(Ragdoll2 v)
{
    float mag = Magnitude(v);
    if (mag == 0)
        return Ragdoll2::Zero();
    return v / mag;
}

void Ragdoll2::OrthoNormalize(Ragdoll2 &normal, Ragdoll2 &tangent)
{
    normal = Normalized(normal);
    tangent = Reject(tangent, normal);
    tangent = Normalized(tangent);
}

Ragdoll2 Ragdoll2::Project(Ragdoll2 a, Ragdoll2 b)
{
    float m = Magnitude(b);
    return Dot(a, b) / (m * m) * b;
}

Ragdoll2 Ragdoll2::Reflect(Ragdoll2 Ragdoll, Ragdoll2 planeNormal)
{
    return Ragdoll - 2 * Project(Ragdoll, planeNormal);
}

Ragdoll2 Ragdoll2::Reject(Ragdoll2 a, Ragdoll2 b)
{
    return a - Project(a, b);
}

Ragdoll2 Ragdoll2::RotateTowards(Ragdoll2 current, Ragdoll2 target,
                               float maxRadiansDelta,
                               float maxMagnitudeDelta)
{
    float magCur = Magnitude(current);
    float magTar = Magnitude(target);
    float newMag = magCur + maxMagnitudeDelta *
        ((magTar > magCur) - (magCur > magTar));
    newMag = fmin(newMag, fmax(magCur, magTar));
    newMag = fmax(newMag, fmin(magCur, magTar));

    float totalAngle = Angle(current, target) - maxRadiansDelta;
    if (totalAngle <= 0)
        return Normalized(target) * newMag;
    else if (totalAngle >= M_PI)
        return Normalized(-target) * newMag;

    float axis = current.X * target.Y - current.Y * target.X;
    axis = axis / fabs(axis);
    if (!(1 - fabs(axis) < 0.00001))
        axis = 1;
    current = Normalized(current);
    Ragdoll2 newRagdoll = current * cos(maxRadiansDelta) +
        Ragdoll2(-current.Y, current.X) * sin(maxRadiansDelta) * axis;
    return newRagdoll * newMag;
}

Ragdoll2 Ragdoll2::Scale(Ragdoll2 a, Ragdoll2 b)
{
    return Ragdoll2(a.X * b.X, a.Y * b.Y);
}

Ragdoll2 Ragdoll2::Slerp(Ragdoll2 a, Ragdoll2 b, float t)
{
    if (t < 0) return a;
    else if (t > 1) return b;
    return SlerpUnclamped(a, b, t);
}

Ragdoll2 Ragdoll2::SlerpUnclamped(Ragdoll2 a, Ragdoll2 b, float t)
{
    float magA = Magnitude(a);
    float magB = Magnitude(b);
    a /= magA;
    b /= magB;
    float dot = Dot(a, b);
    dot = fmax(dot, -1.0);
    dot = fmin(dot, 1.0);
    float theta = acos(dot) * t;
    Ragdoll2 relativeVec = Normalized(b - a * dot);
    Ragdoll2 newVec = a * cos(theta) + relativeVec * sin(theta);
    return newVec * (magA + (magB - magA) * t);
}

float Ragdoll2::SqrMagnitude(Ragdoll2 v)
{
    return v.X * v.X + v.Y * v.Y;
}

void Ragdoll2::ToPolar(Ragdoll2 Ragdoll, float &rad, float &theta)
{
    rad = Magnitude(Ragdoll);
    theta = atan2(Ragdoll.Y, Ragdoll.X);
}


struct Ragdoll2& Ragdoll2::operator+=(const float rhs)
{
    X += rhs;
    Y += rhs;
    return *this;
}

struct Ragdoll2& Ragdoll2::operator-=(const float rhs)
{
    X -= rhs;
    Y -= rhs;
    return *this;
}

struct Ragdoll2& Ragdoll2::operator*=(const float rhs)
{
    X *= rhs;
    Y *= rhs;
    return *this;
}

struct Ragdoll2& Ragdoll2::operator/=(const float rhs)
{
    X /= rhs;
    Y /= rhs;
    return *this;
}

struct Ragdoll2& Ragdoll2::operator+=(const Ragdoll2 rhs)
{
    X += rhs.X;
    Y += rhs.Y;
    return *this;
}

struct Ragdoll2& Ragdoll2::operator-=(const Ragdoll2 rhs)
{
    X -= rhs.X;
    Y -= rhs.Y;
    return *this;
}

Ragdoll2 operator-(Ragdoll2 rhs) { return rhs * -1; }
Ragdoll2 operator+(Ragdoll2 lhs, const float rhs) { return lhs += rhs; }
Ragdoll2 operator-(Ragdoll2 lhs, const float rhs) { return lhs -= rhs; }
Ragdoll2 operator*(Ragdoll2 lhs, const float rhs) { return lhs *= rhs; }
Ragdoll2 operator/(Ragdoll2 lhs, const float rhs) { return lhs /= rhs; }
Ragdoll2 operator+(const float lhs, Ragdoll2 rhs) { return rhs += lhs; }
Ragdoll2 operator-(const float lhs, Ragdoll2 rhs) { return rhs -= lhs; }
Ragdoll2 operator*(const float lhs, Ragdoll2 rhs) { return rhs *= lhs; }
Ragdoll2 operator/(const float lhs, Ragdoll2 rhs) { return rhs /= lhs; }
Ragdoll2 operator+(Ragdoll2 lhs, const Ragdoll2 rhs) { return lhs += rhs; }
Ragdoll2 operator-(Ragdoll2 lhs, const Ragdoll2 rhs) { return lhs -= rhs; }

bool operator==(const Ragdoll2 lhs, const Ragdoll2 rhs)
{
    return lhs.X == rhs.X && lhs.Y == rhs.Y;
}

bool operator!=(const Ragdoll2 lhs, const Ragdoll2 rhs)
{
    return !(lhs == rhs);
}
