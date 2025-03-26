package ge.nikka.edk;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import java.text.*;
import java.util.*;

public class MenuCanvas extends View implements Runnable {
    Paint mStrokePaint;
    Paint mFilledPaint;
    Paint mTextPaint;
    Thread mThread;
    int FPS = 60;
    long sleepTime;
   // Date time;
    SimpleDateFormat timeForm, dateForm;
	Context ctx;
    public static Display display = null;
    //public native void SetTime(String value);
	//public native void SetDate(String value);


    public MenuCanvas(Context context) {
        super(context);
		ctx = context;
        InitializePaints();
        setFocusableInTouchMode(false);
        setBackgroundColor(Color.TRANSPARENT);
        //time = new Date();
        timeForm = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        dateForm = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        if (display != null)
            FPS = (int)display.getRefreshRate();
        sleepTime = 1000 / FPS;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas != null && getVisibility() == VISIBLE) {
            ClearCanvas(canvas);
            //time.setTime(System.currentTimeMillis());
            //SetTime(timeForm.format(time));
			//SetDate(dateForm.format(time));
            FloatingWindow.DrawOn(this, canvas);
        }
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        while (mThread.isAlive() && !mThread.isInterrupted()) {
            try {
                long t1 = System.currentTimeMillis();
                postInvalidate();
                long td = System.currentTimeMillis() - t1;
                Thread.sleep(Math.max(Math.min(0, sleepTime - td), sleepTime));
            } catch (InterruptedException it) {
                Log.e("OverlayThread", it.getMessage());
            }
        }
    }

    public void InitializePaints() {
        mStrokePaint = new Paint();
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(Color.rgb(0, 0, 0));

        mFilledPaint = new Paint();
        mFilledPaint.setStyle(Paint.Style.FILL);
        mFilledPaint.setAntiAlias(true);
        mFilledPaint.setColor(Color.rgb(0, 0, 0));

        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.rgb(0, 0, 0));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStrokeWidth(1.1f);
    }

    public void ClearCanvas(Canvas cvs) {
        cvs.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    public void DrawLine(Canvas cvs, int a, int r, int g, int b, float lineWidth, float fromX, float fromY, float toX, float toY) {
        mStrokePaint.setColor(Color.rgb(r, g, b));
        mStrokePaint.setAlpha(a);
        mStrokePaint.setStrokeWidth(lineWidth);
        cvs.drawLine(fromX, fromY, toX, toY, mStrokePaint);
    }

    public void DrawText(Canvas cvs, int a, int r, int g, int b, String txt, float posX, float posY, float size) {
        mTextPaint.setColor(Color.rgb(r, g, b));
        mTextPaint.setAlpha(a);

        if (getRight() > 1920 || getBottom() > 1920) {
            mTextPaint.setShadowLayer(12.0f, 0.0f, 0.0f, Color.rgb(r, g, b));
            mTextPaint.setTextSize(4 + size);
		}
        else if (getRight() == 1920 || getBottom() == 1920) {
            mTextPaint.setTextSize(2 + size);
            mTextPaint.setShadowLayer(12.0f, 0.0f, 0.0f, Color.rgb(r, g, b));
		}
        else
            mTextPaint.setTextSize(size);
        mTextPaint.setShadowLayer(12.0f, 0.0f, 0.0f, Color.rgb(r, g, b));
        cvs.drawText(txt, posX, posY, mTextPaint);
    }

    public void DrawCircle(Canvas cvs, int a, int r, int g, int b, float stroke, float posX, float posY, float radius) {
        mStrokePaint.setColor(Color.rgb(r, g, b));
        mStrokePaint.setAlpha(a);
        mStrokePaint.setStrokeWidth(stroke);
        cvs.drawCircle(posX, posY, radius, mStrokePaint);
    }

    public void DrawFilledCircle(Canvas cvs, int a, int r, int g, int b, float posX, float posY, float radius) {
        mFilledPaint.setColor(Color.rgb(r, g, b));
        mFilledPaint.setAlpha(a);
        cvs.drawCircle(posX, posY, radius, mFilledPaint);
    }
	
	public void DrawTextNew(Canvas canvas, int r, int g, int b, int a, 
							float x, float y, float width, float height, 
							String textt, float size, boolean shadow, 
							boolean outline, boolean glow, float glowAlpha, boolean gradient, int type) {
        try {                      
	    	Paint paint = new Paint();
	    	paint.setAntiAlias(true);
	    	paint.setTextSize(size);
	    	paint.setColor(Color.argb(a, r, g, b));
	    	paint.setTextAlign(Paint.Align.LEFT);
	    	Typeface font =null;
	    	switch (type) {
		    	case 0:
		    		font = Typeface.createFromAsset(ctx.getAssets(), "font.ttf");
			    	break;
	    		case 1:
			    	font = Typeface.createFromAsset(ctx.getAssets(), "fontfunc.ttf");
			    	break;
		    	case 2:
			    	font = Typeface.createFromAsset(ctx.getAssets(), "ficons.ttf");
			    	break;
	    	}
	    	paint.setTypeface(font);
	    	Rect textBounds = new Rect();
            String text = textt;
            if (!paint.hasGlyph(text)) {
                text = text.replaceAll("[^\\x20-\\x7E]", "?");
            }
	    	paint.getTextBounds(text, 0, text.length(), textBounds);
	    	float textX = x + (width - textBounds.width()) / 2f;
	    	float textY = y + (height + textBounds.height()) / 2f;
	    	if (shadow) {
	    		Paint shadowPaint = new Paint(paint);
		    	shadowPaint.setColor(Color.argb((int) (a * 0.7f), 0, 0, 0));
		    	canvas.drawText(text, textX + 2, textY + 2, shadowPaint);
	    	}
	    	if (outline) {
		    	Paint outlinePaint = new Paint(paint);
		    	outlinePaint.setStyle(Paint.Style.STROKE);
		    	outlinePaint.setStrokeWidth(2);  // Adjust stroke width as needed
		    	outlinePaint.setColor(Color.argb(a, 0, 0, 0));
		    	canvas.drawText(text, textX, textY, outlinePaint);
	    	}
	    	if (glow) {
		    	Paint glowPaint = new Paint(paint);
		    	glowPaint.setMaskFilter(new BlurMaskFilter(glowAlpha, BlurMaskFilter.Blur.OUTER));
		    	glowPaint.setColor(Color.argb(a, r, g, b));
		    	canvas.drawText(text, textX, textY, glowPaint);
	    	}
	    	canvas.drawText(text, textX, textY, paint);
        } catch (Exception ex) {}
	}

	public void DrawRectNew(Canvas canvas, int r, int g, int b, int a,
							float x, float y, float width, float height,
							boolean outline, float rounding, int stroke,
							boolean glow, int glowSize, float glowAlpha) {
		float startX = x;
		float startY = y;
		float endX = x + width;
		float endY = y + height;

		Paint paint = new Paint();
		paint.setAntiAlias(true);

		if (glow) {
			paint.setStyle(Paint.Style.STROKE);
			for (int i = 0; i < glowSize; i++) {
				float alpha = glowAlpha * (1.0f / glowSize) * ((float) (glowSize - i) / glowSize);
				paint.setColor(Color.argb((int) (alpha * 255), r, g, b));
				paint.setStrokeWidth(stroke + i);
				canvas.drawRoundRect(startX - i, startY - i, endX + i, endY + i, rounding, rounding, paint);
			}
		}

		if (outline) {
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.argb((int) (a * 0.8f), 0, 0, 0));
			paint.setStrokeWidth(stroke * 2);
			canvas.drawRoundRect(startX, startY, endX, endY, rounding, rounding, paint);
		}

		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.argb(a, r, g, b));
		paint.setStrokeWidth(stroke);
		canvas.drawRoundRect(startX, startY, endX, endY, rounding, rounding, paint);
	}

	public void DrawRect(Canvas cvs, int a, int r, int g, int b, float stroke, float x, float y, float width, float height) {
        mStrokePaint.setStrokeWidth(stroke);
        mStrokePaint.setColor(Color.rgb(r, g, b));
        mStrokePaint.setAlpha(a);
        cvs.drawRect(x, y, x + width, y + height, mStrokePaint);
    }
	
    public void DrawFilledRect(Canvas cvs, int a, int r, int g, int b, float x, float y, float width, float height) {
        mFilledPaint.setColor(Color.rgb(r, g, b));
        mFilledPaint.setAlpha(a);
        cvs.drawRect(x, y, x + width, y + height, mFilledPaint);
    }
}
