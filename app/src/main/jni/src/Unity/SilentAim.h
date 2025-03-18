struct SilentAim
{
    union
    {
        struct
        {
            float X;
            float Y;
            float Z;
        };
        float data[3];
    };


    /**
     * Constructors.
     */
    inline SilentAim();
    inline SilentAim(float data[]);
    inline SilentAim(float value);
    inline SilentAim(float x, float y);
    inline SilentAim(float x, float y, float z);


    /**
     * Constants for common vectors.
     */
    static inline SilentAim Zero();
    static inline SilentAim One();
    static inline SilentAim Right();
    static inline SilentAim Left();
    static inline SilentAim Up();
    static inline SilentAim Down();
    static inline SilentAim Forward();
    static inline SilentAim Backward();


    /**
     * Returns the angle between two vectors in radians.
     * @param a: The first vector.
     * @param b: The second vector.
     * @return: A scalar value.
     */
    static inline float Angle(SilentAim a, SilentAim b);

    /**
     * Returns a vector with its magnitude clamped to maxLength.
     * @param vector: The target vector.
     * @param maxLength: The maximum length of the return vector.
     * @return: A new vector.
     */
    static inline SilentAim ClampMagnitude(SilentAim vector, float maxLength);

    /**
     * Returns the component of a in the direction of b (scalar projection).
     * @param a: The target vector.
     * @param b: The vector being compared against.
     * @return: A scalar value.
     */
    static inline float Component(SilentAim a, SilentAim b);

    /**
     * Returns the cross product of two vectors.
     * @param lhs: The left side of the multiplication.
     * @param rhs: The right side of the multiplication.
     * @return: A new vector.
     */
    static inline SilentAim Cross(SilentAim lhs, SilentAim rhs);

    /**
     * Returns the distance between a and b.
     * @param a: The first point.
     * @param b: The second point.
     * @return: A scalar value.
     */
    static inline float Distance(SilentAim a, SilentAim b);

   

    /**
     * Returns the dot product of two vectors.
     * @param lhs: The left side of the multiplication.
     * @param rhs: The right side of the multiplication.
     * @return: A scalar value.
     */
    static inline float Dot(SilentAim lhs, SilentAim rhs);

    /**
     * Converts a spherical representation of a vector into cartesian
     * coordinates.
     * This uses the ISO convention (radius r, inclination theta, azimuth phi).
     * @param rad: The magnitude of the vector.
     * @param theta: The angle in the XY plane from the X axis.
     * @param phi: The angle from the positive Z axis to the vector.
     * @return: A new vector.
     */
    static inline SilentAim FromSpherical(float rad, float theta, float phi);

    /**
     * Returns a vector linearly interpolated between a and b, moving along
     * a straight line. The vector is clamped to never go beyond the end points.
     * @param a: The starting point.
     * @param b: The ending point.
     * @param t: The interpolation value [0-1].
     * @return: A new vector.
     */
    static inline SilentAim Lerp(SilentAim a, SilentAim b, float t);

    /**
     * Returns a vector linearly interpolated between a and b, moving along
     * a straight line.
     * @param a: The starting point.
     * @param b: The ending point.
     * @param t: The interpolation value [0-1] (no actual bounds).
     * @return: A new vector.
     */
    static inline SilentAim LerpUnclamped(SilentAim a, SilentAim b, float t);

    /**
     * Returns the magnitude of a vector.
     * @param v: The vector in question.
     * @return: A scalar value.
     */
    static inline float Magnitude(SilentAim v);

    /**
     * Returns a vector made from the largest components of two other vectors.
     * @param a: The first vector.
     * @param b: The second vector.
     * @return: A new vector.
     */
    static inline SilentAim Max(SilentAim a, SilentAim b);

    /**
     * Returns a vector made from the smallest components of two other vectors.
     * @param a: The first vector.
     * @param b: The second vector.
     * @return: A new vector.
*/
    static inline SilentAim Min(SilentAim a, SilentAim b);

    /**
     * Returns a vector "maxDistanceDelta" units closer to the target. This
     * interpolation is in a straight line, and will not overshoot.
     * @param current: The current position.
     * @param target: The destination position.
     * @param maxDistanceDelta: The maximum distance to move.
     * @return: A new vector.
     */
    static inline SilentAim MoveTowards(SilentAim current, SilentAim target,
                                      float maxDistanceDelta);

    /**
     * Returns a new vector with magnitude of one.
     * @param v: The vector in question.
     * @return: A new vector.
     */
    static inline SilentAim Normalized(SilentAim v);

    /**
     * Returns an arbitrary vector orthogonal to the input.
     * This vector is not normalized.
     * @param v: The input vector.
     * @return: A new vector.
     */
    static inline SilentAim Orthogonal(SilentAim v);

    /**
     * Creates a new coordinate system out of the three vectors.
     * Normalizes "normal", normalizes "tangent" and makes it orthogonal to
     * "normal" and normalizes "binormal" and makes it orthogonal to both
     * "normal" and "tangent".
     * @param normal: A reference to the first axis vector.
     * @param tangent: A reference to the second axis vector.
     * @param binormal: A reference to the third axis vector.
     */
    static inline void OrthoNormalize(SilentAim &normal, SilentAim &tangent,
                                      SilentAim &binormal);

    /**
     * Returns the vector projection of a onto b.
     * @param a: The target vector.
     * @param b: The vector being projected onto.
     * @return: A new vector.
     */
    static inline SilentAim Project(SilentAim a, SilentAim b);

    /**
     * Returns a vector projected onto a plane orthogonal to "planeNormal".
     * This can be visualized as the shadow of the vector onto the plane, if
     * the light source were in the direction of the plane normal.
     * @param vector: The vector to project.
     * @param planeNormal: The normal of the plane onto which to project.
     * @param: A new vector.
     */
    static inline SilentAim ProjectOnPlane(SilentAim vector, SilentAim planeNormal);

    /**
     * Returns a vector reflected off the plane orthogonal to the normal.
     * The input vector is pointed inward, at the plane, and the return vector
     * is pointed outward from the plane, like a beam of light hitting and then
     * reflecting off a mirror.
     * @param vector: The vector traveling inward at the plane.
     * @param planeNormal: The normal of the plane off of which to reflect.
     * @return: A new vector pointing outward from the plane.
     */
    static inline SilentAim Reflect(SilentAim vector, SilentAim planeNormal);

    /**
     * Returns the vector rejection of a on b.
     * @param a: The target vector.
     * @param b: The vector being projected onto.
     * @return: A new vector.
     */
    static inline SilentAim Reject(SilentAim a, SilentAim b);

    /**
     * Rotates vector "current" towards vector "target" by "maxRadiansDelta".
     * This treats the vectors as directions and will linearly interpolate
     * between their magnitudes by "maxMagnitudeDelta". This function does not
     * overshoot. If a negative delta is supplied, it will rotate away from
     * "target" until it is pointing the opposite direction, but will not
     * overshoot that either.
     * @param current: The starting direction.
     * @param target: The destination direction.
     * @param maxRadiansDelta: The maximum number of radians to rotate.
     * @param maxMagnitudeDelta: The maximum delta for magnitude interpolation.
     * @return: A new vector.
     */
    static inline SilentAim RotateTowards(SilentAim current, SilentAim target,
                                        float maxRadiansDelta,
                                        float maxMagnitudeDelta);

    /**
     * Multiplies two vectors element-wise.
     * @param a: The lhs of the multiplication.
     * @param b: The rhs of the multiplication.
* @return: A new vector.
     */
    static inline SilentAim Scale(SilentAim a, SilentAim b);

    /**
     * Returns a vector rotated towards b from a by the percent t.
     * Since interpolation is done spherically, the vector moves at a constant
     * angular velocity. This rotation is clamped to 0 <= t <= 1.
     * @param a: The starting direction.
     * @param b: The ending direction.
     * @param t: The interpolation value [0-1].
     */
    static inline SilentAim Slerp(SilentAim a, SilentAim b, float t);

    /**
     * Returns a vector rotated towards b from a by the percent t.
     * Since interpolation is done spherically, the vector moves at a constant
     * angular velocity. This rotation is unclamped.
     * @param a: The starting direction.
     * @param b: The ending direction.
     * @param t: The interpolation value [0-1].
     */
    static inline SilentAim SlerpUnclamped(SilentAim a, SilentAim b, float t);

    /**
     * Returns the squared magnitude of a vector.
     * This is useful when comparing relative lengths, where the exact length
     * is not important, and much time can be saved by not calculating the
     * square root.
     * @param v: The vector in question.
     * @return: A scalar value.
     */
    static inline float SqrMagnitude(SilentAim v);

    /**
     * Calculates the spherical coordinate space representation of a vector.
     * This uses the ISO convention (radius r, inclination theta, azimuth phi).
     * @param vector: The vector to convert.
     * @param rad: The magnitude of the vector.
     * @param theta: The angle in the XY plane from the X axis.
     * @param phi: The angle from the positive Z axis to the vector.
     */
    static inline void ToSpherical(SilentAim vector, float &rad, float &theta,
                                   float &phi);


    /**
     * Operator overloading.
     */
    inline struct SilentAim& operator+=(const float rhs);
    inline struct SilentAim& operator-=(const float rhs);
    inline struct SilentAim& operator*=(const float rhs);
    inline struct SilentAim& operator/=(const float rhs);
    inline struct SilentAim& operator+=(const SilentAim rhs);
    inline struct SilentAim& operator-=(const SilentAim rhs);
};

inline SilentAim operator-(SilentAim rhs);
inline SilentAim operator+(SilentAim lhs, const float rhs);
inline SilentAim operator-(SilentAim lhs, const float rhs);
inline SilentAim operator*(SilentAim lhs, const float rhs);
inline SilentAim operator/(SilentAim lhs, const float rhs);
inline SilentAim operator+(const float lhs, SilentAim rhs);
inline SilentAim operator-(const float lhs, SilentAim rhs);
inline SilentAim operator*(const float lhs, SilentAim rhs);
inline SilentAim operator/(const float lhs, SilentAim rhs);
inline SilentAim operator+(SilentAim lhs, const SilentAim rhs);
inline SilentAim operator-(SilentAim lhs, const SilentAim rhs);
inline bool operator==(const SilentAim lhs, const SilentAim rhs);
inline bool operator!=(const SilentAim lhs, const SilentAim rhs);



/*******************************************************************************
 * Implementation
 */

SilentAim::SilentAim() : X(0), Y(0), Z(0) {}
SilentAim::SilentAim(float data[]) : X(data[0]), Y(data[1]), Z(data[2]) {}
SilentAim::SilentAim(float value) : X(value), Y(value), Z(value) {}
SilentAim::SilentAim(float x, float y) : X(x), Y(y), Z(0) {}
SilentAim::SilentAim(float x, float y, float z) : X(x), Y(y), Z(z) {}


SilentAim SilentAim::Zero() { return SilentAim(0, 0, 0); }
SilentAim SilentAim::One() { return SilentAim(1, 1, 1); }
SilentAim SilentAim::Right() { return SilentAim(1, 0, 0); }
SilentAim SilentAim::Left() { return SilentAim(-1, 0, 0); }
SilentAim SilentAim::Up() { return SilentAim(0, 1, 0); }
SilentAim SilentAim::Down() { return SilentAim(0, -1, 0); }
SilentAim SilentAim::Forward() { return SilentAim(0, 0, 1); }
SilentAim SilentAim::Backward() { return SilentAim(0, 0, -1); }


float SilentAim::Angle(SilentAim a, SilentAim b)
{
    float v = Dot(a, b) / (Magnitude(a) * Magnitude(b));
    v = fmax(v, -1.0);
    v = fmin(v, 1.0);
    return acos(v);
}

SilentAim SilentAim::ClampMagnitude(SilentAim vector, float maxLength)
{
    float length = Magnitude(vector);
    if (length > maxLength)
		vector *= maxLength / length;
    return vector;
}

float SilentAim::Component(SilentAim a, SilentAim b)
{
    return Dot(a, b) / Magnitude(b);
}

SilentAim SilentAim::Cross(SilentAim lhs, SilentAim rhs)
{
    float x = lhs.Y * rhs.Z - lhs.Z * rhs.Y;
    float y = lhs.Z * rhs.X - lhs.X * rhs.Z;
    float z = lhs.X * rhs.Y - lhs.Y * rhs.X;
    return SilentAim(x, y, z);
}

float SilentAim::Distance(SilentAim a, SilentAim b)
{
    return SilentAim::Magnitude(a - b);
}

float SilentAim::Dot(SilentAim lhs, SilentAim rhs)
{
    return lhs.X * rhs.X + lhs.Y * rhs.Y + lhs.Z * rhs.Z;
}

SilentAim SilentAim::FromSpherical(float rad, float theta, float phi)
{
    SilentAim v;
    v.X = rad * sin(theta) * cos(phi);
    v.Y = rad * sin(theta) * sin(phi);
    v.Z = rad * cos(theta);
    return v;
}

SilentAim SilentAim::Lerp(SilentAim a, SilentAim b, float t)
{
    if (t < 0) return a;
    else if (t > 1) return b;
    return LerpUnclamped(a, b, t);
}

SilentAim SilentAim::LerpUnclamped(SilentAim a, SilentAim b, float t)
{
    return (b - a) * t + a;
}

float SilentAim::Magnitude(SilentAim v)
{
    return sqrt(SqrMagnitude(v));
}

SilentAim SilentAim::Max(SilentAim a, SilentAim b)
{
    float x = a.X > b.X ? a.X : b.X;
    float y = a.Y > b.Y ? a.Y : b.Y;
    float z = a.Z > b.Z ? a.Z : b.Z;
    return SilentAim(x, y, z);
}

SilentAim SilentAim::Min(SilentAim a, SilentAim b)
{
    float x = a.X > b.X ? b.X : a.X;
    float y = a.Y > b.Y ? b.Y : a.Y;
    float z = a.Z > b.Z ? b.Z : a.Z;
    return SilentAim(x, y, z);
}

SilentAim SilentAim::MoveTowards(SilentAim current, SilentAim target,
                             float maxDistanceDelta)
{
    SilentAim d = target - current;
    float m = Magnitude(d);
    if (m < maxDistanceDelta || m == 0)
        return target;
    return current + (d * maxDistanceDelta / m);
}

SilentAim SilentAim::Normalized(SilentAim v)
{
    float mag = Magnitude(v);
    if (mag == 0)
        return SilentAim::Zero();
    return v / mag;
}

SilentAim SilentAim::Orthogonal(SilentAim v)
{
    return v.Z < v.X ? SilentAim(v.Y, -v.X, 0) : SilentAim(0, -v.Z, v.Y);
}

void SilentAim::OrthoNormalize(SilentAim &normal, SilentAim &tangent,
                             SilentAim &binormal)
{
    normal = Normalized(normal);
    tangent = ProjectOnPlane(tangent, normal);
    tangent = Normalized(tangent);
    binormal = ProjectOnPlane(binormal, tangent);
    binormal = ProjectOnPlane(binormal, normal);
    binormal = Normalized(binormal);
}

SilentAim SilentAim::Project(SilentAim a, SilentAim b)
{
    float m = Magnitude(b);
    return Dot(a, b) / (m * m) * b;
}

SilentAim SilentAim::ProjectOnPlane(SilentAim vector, SilentAim planeNormal)
{
    return Reject(vector, planeNormal);
}

SilentAim SilentAim::Reflect(SilentAim vector, SilentAim planeNormal)
{
    return vector - 2 * Project(vector, planeNormal);
}

SilentAim SilentAim::Reject(SilentAim a, SilentAim b)
{
    return a - Project(a, b);
}

SilentAim SilentAim::RotateTowards(SilentAim current, SilentAim target,
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

    SilentAim axis = Cross(current, target);
    float magAxis = Magnitude(axis);
    if (magAxis == 0)
        axis = Normalized(Cross(current, current + SilentAim(3.95, 5.32, -4.24)));
    else
        axis /= magAxis;
    current = Normalized(current);
    SilentAim newVector = current * cos(maxRadiansDelta) +
                        Cross(axis, current) * sin(maxRadiansDelta);
    return newVector * newMag;
}

SilentAim SilentAim::Scale(SilentAim a, SilentAim b)
{
    return SilentAim(a.X * b.X, a.Y * b.Y, a.Z * b.Z);
}

SilentAim SilentAim::Slerp(SilentAim a, SilentAim b, float t)
{
    if (t < 0) return a;
	else if (t > 1) return b;
    return SlerpUnclamped(a, b, t);
}

SilentAim SilentAim::SlerpUnclamped(SilentAim a, SilentAim b, float t)
{
    float magA = Magnitude(a);
    float magB = Magnitude(b);
    a /= magA;
    b /= magB;
    float dot = Dot(a, b);
    dot = fmax(dot, -1.0);
    dot = fmin(dot, 1.0);
    float theta = acos(dot) * t;
    SilentAim relativeVec = Normalized(b - a * dot);
    SilentAim newVec = a * cos(theta) + relativeVec * sin(theta);
    return newVec * (magA + (magB - magA) * t);
}

float SilentAim::SqrMagnitude(SilentAim v)
{
    return v.X * v.X + v.Y * v.Y + v.Z * v.Z;
}

void SilentAim::ToSpherical(SilentAim vector, float &rad, float &theta,
                          float &phi)
{
    rad = Magnitude(vector);
    float v = vector.Z / rad;
    v = fmax(v, -1.0);
    v = fmin(v, 1.0);
    theta = acos(v);
    phi = atan2(vector.Y, vector.X);
}


struct SilentAim& SilentAim::operator+=(const float rhs)
{
    X += rhs;
    Y += rhs;
    Z += rhs;
    return *this;
}

struct SilentAim& SilentAim::operator-=(const float rhs)
{
    X -= rhs;
    Y -= rhs;
    Z -= rhs;
    return *this;
}

struct SilentAim& SilentAim::operator*=(const float rhs)
{
    X *= rhs;
    Y *= rhs;
    Z *= rhs;
    return *this;
}

struct SilentAim& SilentAim::operator/=(const float rhs)
{
    X /= rhs;
    Y /= rhs;
    Z /= rhs;
    return *this;
}

struct SilentAim& SilentAim::operator+=(const SilentAim rhs)
{
    X += rhs.X;
    Y += rhs.Y;
    Z += rhs.Z;
    return *this;
}

struct SilentAim& SilentAim::operator-=(const SilentAim rhs)
{
    X -= rhs.X;
    Y -= rhs.Y;
    Z -= rhs.Z;
    return *this;
}

SilentAim operator-(SilentAim rhs) { return rhs * -1; }
SilentAim operator+(SilentAim lhs, const float rhs) { return lhs += rhs; }
SilentAim operator-(SilentAim lhs, const float rhs) { return lhs -= rhs; }
SilentAim operator*(SilentAim lhs, const float rhs) { return lhs *= rhs; }
SilentAim operator/(SilentAim lhs, const float rhs) { return lhs /= rhs; }
SilentAim operator+(const float lhs, SilentAim rhs) { return rhs += lhs; }
SilentAim operator-(const float lhs, SilentAim rhs) { return rhs -= lhs; }
SilentAim operator*(const float lhs, SilentAim rhs) { return rhs *= lhs; }
SilentAim operator/(const float lhs, SilentAim rhs) { return rhs /= lhs; }
SilentAim operator+(SilentAim lhs, const SilentAim rhs) { return lhs += rhs; }
SilentAim operator-(SilentAim lhs, const SilentAim rhs) { return lhs -= rhs; }

bool operator==(const SilentAim lhs, const SilentAim rhs)
{
    return lhs.X == rhs.X && lhs.Y == rhs.Y && lhs.Z == rhs.Z;
}

bool operator!=(const SilentAim lhs, const SilentAim rhs)
{
    return !(lhs == rhs);
}

