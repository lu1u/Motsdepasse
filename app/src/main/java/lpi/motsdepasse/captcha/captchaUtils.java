package lpi.motsdepasse.captcha;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

import lpi.motsdepasse.MotDePasse;

/**
 * Created by lucien on 25/04/2016.
 */
public class captchaUtils
{
private static Random r = new Random(SystemClock.currentThreadTimeMillis());

private static ArrayList<String> _listeFontes;

static
{
	// Initialisation: lecture de la liste des fontes disponibles
	_listeFontes = FontManager.enumerateFontFiles();

}


static
@Nullable
public Typeface getFont()
{
	Typeface tf = null;
	while (tf == null && !(_listeFontes.isEmpty()))
	{
		int index = r.nextInt(_listeFontes.size());
		try
		{
			tf = Typeface.createFromFile(_listeFontes.get(index));
		} catch (Exception e)
		{
			// Impossible de creer cette fonte, autant la supprimer de la liste des fontes possibles
			_listeFontes.remove(index);
		}
	}

	return tf;
}

// Dessine le texte de façon un peu brouillee
static public void drawText(@NonNull Bitmap bitmap, @NonNull String motDePasse, Typeface typeface)
{
	Canvas canvas = new Canvas(bitmap);
	RectF rBitmap = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
	Paint paint = new Paint();
	paint.setTextSize(MotDePasse.TAILLE_TEXTE);
	paint.setTypeface(typeface);

	switch (r.nextInt(3))
	{
		case 0:
		{
			paint.setShadowLayer(2, 2, 2, Color.WHITE);
			paint.setColor(Color.BLACK);
			break;
		}
		case 1:
		{
			paint.setShadowLayer(2, 2, 2, Color.BLACK);
			paint.setColor(Color.WHITE);
			break;
		}
		default:
			paint.setColor(Color.DKGRAY);

	}
	Rect rBounds = new Rect();
	paint.getTextBounds(motDePasse, 0, motDePasse.length(), rBounds);

	float X = (bitmap.getWidth() - rBounds.width()) / 2;
	float Y = (rBitmap.height() - paint.ascent() - paint.descent()) / 2.0f;
	for (int i = 0; i < motDePasse.length(); i++)
	{
		String c = motDePasse.substring(i, i + 1);
		int DecalY = r.nextInt(11) - 5;

		canvas.drawText(c, X, Y + DecalY, paint);
		X += paint.measureText(c);
	}
}


/***
 * Dessine un fond au hasard
 *
 * @param bitmap
 */
public static void drawRandBackground(Bitmap bitmap)
{
	switch (r.nextInt(6))
	{
		case 0:
			degradeDroiteGauche(bitmap);
			break;

		case 1:
			degradeGaucheDroite(bitmap);
			break;

		case 2:
			degradeBasHaut(bitmap);
			break;

		case 3:
			degradeHautBas(bitmap);
			break;

		case 4:
			degradeNOSE(bitmap);
			break;

		case 5:
			bruitMono(bitmap);
			break;

		default:
			degradeSONE(bitmap);


	}
}

public static final int COLOR_MIN = 0x11;
public static final int COLOR_MAX = 0xFF;

public static void bruitMono(Bitmap source)
{
	// get image size
	int width = source.getWidth();
	int height = source.getHeight();
	int[] pixels = new int[width * height];
	source.getPixels(pixels, 0, width, 0, 0, width, height);

	int index = 0;
	for (int y = 0; y < height; ++y)
	{
		for (int x = 0; x < width; ++x)
		{
			index = y * width + x;
			int col = COLOR_MIN + r.nextInt(COLOR_MAX - COLOR_MIN);
			int randColor = Color.rgb(col, col, col);
			pixels[index] |= randColor;
		}
	}
	source.setPixels(pixels, 0, width, 0, 0, width, height);
}

private static void degradeSONE(Bitmap bitmap)
{
	RectF rBitmap = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

	Paint paint = new Paint();
	paint.setColor(Color.WHITE);
	paint.setStyle(Paint.Style.FILL);
	LinearGradient s = new LinearGradient(rBitmap.left, rBitmap.bottom, rBitmap.right, rBitmap.top,
			Color.WHITE,
			Color.DKGRAY, Shader.TileMode.CLAMP);
	paint.setShader(s);
	Canvas canvas = new Canvas(bitmap);
	canvas.drawRect(rBitmap, paint);
}

private static void degradeNOSE(Bitmap bitmap)
{
	RectF rBitmap = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
	Paint paint = new Paint();
	paint.setColor(Color.WHITE);
	paint.setStyle(Paint.Style.FILL);
	LinearGradient s = new LinearGradient(rBitmap.right, rBitmap.top, rBitmap.left, rBitmap.bottom,
			Color.WHITE,
			Color.DKGRAY, Shader.TileMode.CLAMP);
	paint.setShader(s);
	Canvas canvas = new Canvas(bitmap);
	canvas.drawRect(rBitmap, paint);
}

private static void degradeHautBas(Bitmap bitmap)
{
	RectF rBitmap = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

	Paint paint = new Paint();
	paint.setColor(Color.WHITE);
	paint.setStyle(Paint.Style.FILL);
	LinearGradient s = new LinearGradient(rBitmap.left, rBitmap.top, rBitmap.right, rBitmap.bottom,
			Color.WHITE,
			Color.DKGRAY, Shader.TileMode.CLAMP);
	paint.setShader(s);
	Canvas canvas = new Canvas(bitmap);
	canvas.drawRect(rBitmap, paint);
}


private static void degradeBasHaut(Bitmap bitmap)
{
	RectF rBitmap = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
	Paint paint = new Paint();
	paint.setColor(Color.WHITE);
	paint.setStyle(Paint.Style.FILL);
	LinearGradient s = new LinearGradient(rBitmap.right, rBitmap.bottom, rBitmap.right, rBitmap.top,
			Color.WHITE,
			Color.DKGRAY, Shader.TileMode.CLAMP);
	paint.setShader(s);
	Canvas canvas = new Canvas(bitmap);
	canvas.drawRect(rBitmap, paint);
}

private static void degradeGaucheDroite(Bitmap bitmap)
{
	RectF rBitmap = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
	Paint paint = new Paint();
	paint.setColor(Color.WHITE);
	paint.setStyle(Paint.Style.FILL);
	LinearGradient s = new LinearGradient(rBitmap.right, rBitmap.top, rBitmap.right, rBitmap.bottom,
			Color.WHITE,
			Color.DKGRAY, Shader.TileMode.CLAMP);
	paint.setShader(s);
	Canvas canvas = new Canvas(bitmap);
	canvas.drawRect(rBitmap, paint);
}

private static void degradeDroiteGauche(Bitmap bitmap)
{
	RectF rBitmap = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
	Paint paint = new Paint();
	paint.setColor(Color.WHITE);
	paint.setStyle(Paint.Style.FILL);
	LinearGradient s = new LinearGradient(rBitmap.right, rBitmap.top, rBitmap.left, rBitmap.top,
			Color.WHITE,
			Color.DKGRAY, Shader.TileMode.CLAMP);
	paint.setShader(s);
	Canvas canvas = new Canvas(bitmap);
	canvas.drawRect(rBitmap, paint);
}


}
