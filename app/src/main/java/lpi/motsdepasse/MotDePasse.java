package lpi.motsdepasse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;

import lpi.motsdepasse.captcha.captchaUtils;
import lpi.motsdepasse.database.DatabaseHelper;

/**
 * Created by lucien on 21/04/2016.
 */
public class MotDePasse
{


public int _Id;
public String _nom = "";
public String _description = "";
public String _utilisateur = "";
private String _motDePasse = "";
private int _categorie = 0;
public int _couleur = Color.GRAY;
public static final int TAILLE_TEXTE = 56;
public static final int BORDURE = 16;
public Bitmap _bitmap;

// Tableau des icones representant les types de mot de passe
public final static Integer[] ICONES = {
		R.drawable.key,
		R.drawable.web,
		R.drawable.windows,
		R.drawable.android,
		R.drawable.apple,
		R.drawable.attachment,
		R.drawable.airplane,
		R.drawable.bell,
		R.drawable.beach,
		R.drawable.briefcase,
		R.drawable.cart,
		R.drawable.facebook,
		R.drawable.ferry,
		R.drawable.gmail,
		R.drawable.hotel,
		R.drawable.twitter};


public MotDePasse()
{

}

public MotDePasse(Cursor cursor)
{
	if (cursor != null)
	{
		_Id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MDP_ID));
		_nom = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MDP_NOM));
		_utilisateur = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MDP_UTILISATEUR));
		_motDePasse = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MDP_MOTDEPASSE));
		_categorie = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MDP_CATEGORIE));
		_couleur = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MDP_COULEUR));
		_description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MDP_DESCRIPTION));
		byte[] ba = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_MDP_IMAGE));
		if (ba != null)
			if (ba.length > 0)
				_bitmap = getImage(ba);
	}
}


public MotDePasse(Bundle bundle)
{
	_Id = bundle.getInt(DatabaseHelper.COLUMN_MDP_ID, _Id);
	_nom = bundle.getString(DatabaseHelper.COLUMN_MDP_NOM, _nom);
	_description = bundle.getString(DatabaseHelper.COLUMN_MDP_DESCRIPTION, _description);
	_utilisateur = bundle.getString(DatabaseHelper.COLUMN_MDP_UTILISATEUR, _utilisateur);
	_motDePasse = bundle.getString(DatabaseHelper.COLUMN_MDP_MOTDEPASSE, _motDePasse);
	_categorie = bundle.getInt(DatabaseHelper.COLUMN_MDP_CATEGORIE, _categorie);
	_couleur = bundle.getInt(DatabaseHelper.COLUMN_MDP_COULEUR, _couleur);
	byte[] ba = bundle.getByteArray(DatabaseHelper.COLUMN_MDP_IMAGE);
	if (ba != null)
		if (ba.length > 0)
			_bitmap = getImage(ba);
}


public void toBundle(@NonNull Bundle bundle)
{
	bundle.putInt(DatabaseHelper.COLUMN_MDP_ID, _Id);
	bundle.putString(DatabaseHelper.COLUMN_MDP_NOM, _nom);
	bundle.putString(DatabaseHelper.COLUMN_MDP_UTILISATEUR, _utilisateur);
	bundle.putString(DatabaseHelper.COLUMN_MDP_MOTDEPASSE, _motDePasse);
	bundle.putString(DatabaseHelper.COLUMN_MDP_DESCRIPTION, _description);
	bundle.putInt(DatabaseHelper.COLUMN_MDP_CATEGORIE, _categorie);
	bundle.putInt(DatabaseHelper.COLUMN_MDP_COULEUR, _couleur);
	byte[] ba = getBytes(_bitmap);
	bundle.putByteArray(DatabaseHelper.COLUMN_MDP_IMAGE, ba);
}

public void toContentValues(@NonNull ContentValues content, boolean putId)
{
	if (putId)
		content.put(DatabaseHelper.COLUMN_MDP_ID, _Id);
	content.put(DatabaseHelper.COLUMN_MDP_NOM, _nom);
	content.put(DatabaseHelper.COLUMN_MDP_UTILISATEUR, _utilisateur);
	content.put(DatabaseHelper.COLUMN_MDP_MOTDEPASSE, _motDePasse);
	content.put(DatabaseHelper.COLUMN_MDP_DESCRIPTION, _description);
	content.put(DatabaseHelper.COLUMN_MDP_CATEGORIE, _categorie);
	content.put(DatabaseHelper.COLUMN_MDP_COULEUR, _couleur);
	byte[] ba = getBytes(_bitmap);
	content.put(DatabaseHelper.COLUMN_MDP_IMAGE, ba);
}

public void construitImage(Context context, String motDePasse)
{
	Paint paint = new Paint();
	paint.setTextSize(TAILLE_TEXTE);
	Typeface tf = captchaUtils.getFont();
	paint.setTypeface(tf);
	final int longueur = motDePasse.length();
	Rect rBounds = new Rect();
	paint.getTextBounds(motDePasse, 0, longueur, rBounds);

	RectF rBitmap = new RectF(rBounds.left, rBounds.top, rBounds.right * 1.2f + BORDURE * 2, rBounds.bottom * 1.2f + BORDURE * 2);
	_bitmap = Bitmap.createBitmap((int) rBitmap.width(), (int) rBitmap.height(), Bitmap.Config.RGB_565);

	rBounds.right = (int) paint.measureText(motDePasse, 0, motDePasse.length());
	rBounds.bottom = (int) (paint.descent() - paint.ascent());
	rBounds.left += (rBitmap.width() - rBounds.right) / 2.0f;
	rBounds.top += (rBitmap.height() - rBounds.bottom) / 2.0f;

	captchaUtils.drawRandBackground(_bitmap);
	captchaUtils.drawText(_bitmap, motDePasse, tf);
}

// convert from bitmap to byte array
public static byte[] getBytes(Bitmap bitmap)
{
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
	return stream.toByteArray();
}

// convert from byte array to bitmap
public static Bitmap getImage(byte[] image)
{
	return BitmapFactory.decodeByteArray(image, 0, image.length);
}

public int getCategoryIconResource()
{
	if ((_categorie >= 0) && (_categorie < ICONES.length))
		return ICONES[_categorie];
	else
		return ICONES[0];
}

public void setCategorieFromResource(int categorieResource)
{
	for ( int i =0;i < ICONES.length;i++)
		if ( ICONES[i]== categorieResource)
		{
			_categorie = i;
			return;
		}

	_categorie = 0;
}
}
