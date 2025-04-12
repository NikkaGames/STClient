#ifndef ESP_H
#define ESP_H

#include <jni.h>
#include "StructsCommon.h"

#define Vector2 Ragdoll2

class ESP {
private:
    JNIEnv *_env;
    jobject _cvsView;
    jobject _cvs;

public:
    ESP() {
        _env = nullptr;
        _cvsView = nullptr;
        _cvs = nullptr;
    }

    ESP(JNIEnv *env, jobject cvsView, jobject cvs) {
        this->_env = env;
        this->_cvsView = cvsView;
        this->_cvs = cvs;
    }

    bool isValid() const {
        return (_env != nullptr && _cvsView != nullptr && _cvs != nullptr);
    }

    __attribute((__annotate__(("bcf"))));
    int getWidth() const {
        if (isValid()) {
            jclass canvas = _env->GetObjectClass(_cvs);
            jmethodID width = _env->GetMethodID(canvas, "getWidth", "()I");
            return _env->CallIntMethod(_cvs, width);
        }
        return 0;
    }

    __attribute((__annotate__(("bcf"))));
    int getHeight() const {
        if (isValid()) {
            jclass canvas = _env->GetObjectClass(_cvs);
            jmethodID width = _env->GetMethodID(canvas, "getHeight", "()I");
            return _env->CallIntMethod(_cvs, width);
        }
        return 0;
    }

    __attribute((__annotate__(("bcf"))));
    void DrawFilledBox(Color color, Rect rect) {
         if (isValid()) {
            jclass canvasView = _env->GetObjectClass(_cvsView);
            jmethodID gayp = _env->GetMethodID(canvasView, "DrawFilledRect",
                                                           "(Landroid/graphics/Canvas;IIIIFFFF)V");
            _env->CallVoidMethod(_cvsView, gayp, _cvs, (int) color.a, (int) color.r,
                                 (int) color.g, (int) color.b, rect.x, rect.y, rect.width, rect.height);
        }
    }

    __attribute((__annotate__(("bcf"))));
	void DrawBox(Color color, Rect rect, float stroke) {
         if (isValid()) {
            jclass canvasView = _env->GetObjectClass(_cvsView);
            jmethodID gayp = _env->GetMethodID(canvasView, "DrawRect",
                                                           "(Landroid/graphics/Canvas;IIIIFFFFF)V");
            _env->CallVoidMethod(_cvsView, gayp, _cvs, (int) color.a, (int) color.r,
                                 (int) color.g, (int) color.b, stroke, rect.x, rect.y, rect.width, rect.height);
        }
    }

    __attribute((__annotate__(("bcf"))));
	void DrawLine(Color color, float lineWidth, float fromX, float fromY, float toX, float toY) {
    if (isValid()) {
        jclass canvasViewClass = _env->GetObjectClass(_cvsView);
        jmethodID drawLineMethod = _env->GetMethodID(
            canvasViewClass,
            "DrawLine",
            "(Landroid/graphics/Canvas;IIIIFFFF)V"
        );
            _env->CallVoidMethod(
                _cvsView,
                drawLineMethod,
                _cvs,
                color.a, color.r, color.g, color.b,
                lineWidth, fromX, fromY, toX, toY
            );
    }
}

    __attribute((__annotate__(("bcf"))));
    void DrawLine(Color color, float thickness, Vector2 start, Vector2 end) {
        if (isValid()) {
            jclass canvasView = _env->GetObjectClass(_cvsView);
            jmethodID drawline = _env->GetMethodID(canvasView, "DrawLine",
                                                   "(Landroid/graphics/Canvas;IIIIFFFFF)V");
            _env->CallVoidMethod(_cvsView, drawline, _cvs, (int) color.a, (int) color.r,
                                 (int) color.g, (int) color.b,
                                 thickness,
                                 start.X, start.Y, end.X, end.Y);
        }
    }
    __attribute((__annotate__(("bcf"))));
void DrawBox(Color color, float stroke, Rect rect) {
        Vector2 v1 = Vector2(rect.x, rect.y);
        Vector2 v2 = Vector2(rect.x + rect.width, rect.y);
        Vector2 v3 = Vector2(rect.x + rect.width, rect.y + rect.height);
        Vector2 v4 = Vector2(rect.x, rect.y + rect.height);
        DrawLine(color, stroke, v1, v2);
        DrawLine(color, stroke, v2, v3);
        DrawLine(color, stroke, v3, v4);
        DrawLine(color, stroke, v4, v1);
}
    __attribute((__annotate__(("bcf"))));
void DrawVerticalHealth(Vector2 end, float h, float health) {
        float x = end.X;
        float y = end.Y;
        h = -h;

        Color clr = Color(0, 150, 0, 255);

        float hpwidth = h-h*health/100;

        if (health <= (100 * 0.6)) {
            clr = Color(150, 150, 0, 255);
        }
        if (health < (100 * 0.3)) {
            clr = Color(150, 0, 0, 255);
        }

        Rect hpbar((x + h / 4) - 8, y, 4.0f, -h);
        Rect hp((x + h / 4)-8,y-hpwidth, 2.0f, -h + hpwidth);

        DrawBox(Color(0, 0, 0, 255), 3, hpbar);

        DrawBox(clr, 3, hp);
		
		Rect hpt = hp;
		hpt.height = hpt.height - hpt.height;
		DrawTextNew(Color(255.0f, 255.0f, 255.0f, 255.0f), hpt, std::to_string((int)health), 21);

    }
    __attribute((__annotate__(("bcf"))));
void DrawHorizontalHealth(Vector2 start, float w, float health, float maxValue) {
    float x = start.X;
    float y = start.Y;

    // Calculate health percentage based on maxValue
    float healthPercentage = (health / maxValue) * 100;

    // Determine the health color
    Color clr = Color(0, 150, 0, 255); // Green for healthy state
    if (healthPercentage <= 60) {
        clr = Color(150, 150, 0, 255); // Yellow for medium health
    }
    if (healthPercentage < 30) {
        clr = Color(150, 0, 0, 255); // Red for low health
    }

    // Calculate the health width based on the percentage
    float hpWidth = w * healthPercentage / 100;

    // Draw the background (full bar)
    Rect hpBar(x, y, w, 4.0f); // Full width of the bar
    DrawBox(Color(0, 0, 0, 255), 3, hpBar); // Black outline for the bar

    // Draw the health (filled portion)
    Rect hp(x, y, hpWidth, 2.0f); // Filled portion based on health
    DrawBox(clr, 3, hp); // Draw the health bar in respective color

    // Draw the health text (current health/max value)
    std::string healthText = std::to_string((int)health) + "/" + std::to_string((int)maxValue);
    Rect hpText(hp.x + hp.width + 5, hp.y - 10, 0, 20); // Position text to the right
    DrawTextNew(Color(255.0f, 255.0f, 255.0f, 255.0f), hpText, healthText, 21);
}
    __attribute((__annotate__(("bcf"))));
	void DrawBoxNew(Color color, Rect rect, int stroke = 3, float rounding = 0.f, bool outline = true, bool glow = false) {
		if (isValid()) {
				 jclass canvasView = _env->GetObjectClass(_cvsView);
				 jmethodID drawRectMethod = _env->GetMethodID(canvasView, "DrawRectNew", 
				                                                           "(Landroid/graphics/Canvas;IIIIFFFFZFIZIF)V");
				_env->CallVoidMethod(_cvsView, drawRectMethod, _cvs, (int) color.r,
				(int) color.g, (int) color.b, (int) color.a,  rect.x, rect.y, rect.width, rect.height,
				outline, rounding, stroke, glow, 5, 255.0f);
		}
	}
    __attribute((__annotate__(("nobcf"))));
    __attribute((__annotate__(("nosub"))));
    __attribute((__annotate__(("nofla"))));
    __attribute((__annotate__(("nosplit"))));
	void DrawTextNew(Color color, Rect rect, basic_string<char, char_traits<char>, allocator<char>> text, float size = 14, int type = 0,
                     bool shadow = false, bool outline = true, bool glow = false, float glowAlpha = 255.0f, bool gradient = false) {
    if (isValid()) {
        jclass canvasViewClass = _env->GetObjectClass(_cvsView);
        jmethodID drawTextMethod = _env->GetMethodID(
            canvasViewClass, 
            "DrawTextNew", 
            "(Landroid/graphics/Canvas;IIIIFFFFLjava/lang/String;FZZZFZI)V"
        );
        jstring jText;
        char* txts = (char*)malloc(text.length());
        strcpy(txts, text.c_str());
        text.clear();
        if (txts && strlen(txts) > 0) {
            jText = _env->NewStringUTF(txts);
            free(txts);
        } else {
            jText = _env->NewStringUTF("Player");
        }
        _env->CallVoidMethod(
            _cvsView, 
            drawTextMethod, 
            _cvs,
            (int)color.r,
            (int)color.g,
            (int)color.b,
            (int)color.a,
            rect.x,
            rect.y, 
            rect.width,
            rect.height,
            jText,
            size,
            shadow ? JNI_TRUE : JNI_FALSE,
            outline ? JNI_TRUE : JNI_FALSE,
            glow ? JNI_TRUE : JNI_FALSE,
            glowAlpha,
            gradient ? JNI_TRUE : JNI_FALSE,
			type
        );
        _env->DeleteLocalRef(jText);
        _env->DeleteLocalRef(canvasViewClass);
    }
}
    __attribute((__annotate__(("bcf"))));
	void DrawCircle(Color color, Ragdoll2 pos, float stroke, float radius) {
        if (isValid()) {
            jclass canvasView = _env->GetObjectClass(_cvsView);
            jmethodID drawtext = _env->GetMethodID(canvasView, "DrawCircle",
                                                   "(Landroid/graphics/Canvas;IIIIFFFF)V");
            _env->CallVoidMethod(_cvsView, drawtext, _cvs, (int) color.a, (int) color.r,
                                 (int) color.g, (int) color.b, stroke, pos.X, pos.Y, radius);
        }
    }
    __attribute((__annotate__(("bcf"))));
    void DrawText(Color color, const char *txt, Ragdoll2 pos, float size) {
        if (isValid()) {
            jclass canvasView = _env->GetObjectClass(_cvsView);
            jmethodID drawtext = _env->GetMethodID(canvasView, "DrawText",
                                                   "(Landroid/graphics/Canvas;IIIILjava/lang/String;FFF)V");
            _env->CallVoidMethod(_cvsView, drawtext, _cvs, (int) color.a, (int) color.r,
                                 (int) color.g, (int) color.b,
                                 _env->NewStringUTF(txt), pos.X, pos.Y, size);
        }
    }
};

#endif
