package lpi.motsdepasse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import lpi.motsdepasse.MainActivity;

/**
 * Created by lucien on 01/05/2016.
 */
public class PreferencesDatabase
{
static public final String PREF_DEVEROUILLAGE_ALTERNATIF = "DevAlternatif";
public static final String PREF_DEVEROUILLAGE_PRINCIPAL = "Principal";
static public final String PREF_DEVEROUILLAGE_NUMERIQUE = "Numerique";
static public final String PREF_DEVEROUILLAGE_PATTERN = "Pattern";

public static final int INVALID_ID = -1;

/**
 * Instance unique non préinitialisée
 */
private static PreferencesDatabase INSTANCE = null;
private SQLiteDatabase database;
private DatabaseHelper dbHelper;

private PreferencesDatabase(Context context)
{
	dbHelper = new DatabaseHelper(context);
	database = dbHelper.getWritableDatabase();
}

/**
 * Point d'accès pour l'instance unique du singleton
 */
public static synchronized PreferencesDatabase getInstance(Context context)
{
	if (INSTANCE == null)
	{
		INSTANCE = new PreferencesDatabase(context);
	}
	return INSTANCE;
}

@Override
public void finalize()
{
	dbHelper.close();
}

/***
 * Retourne un motDePasse cree a partir de la base de donnnees
 *
 * @param name
 * @return valeur
 */
public
@NonNull
String getStringPreference(@NonNull String name)
{
	String valeur = null;
	Cursor cursor = null;
	try
	{
		String[] colonnes = null;
		String where = DatabaseHelper.COLONNE_PREF_NAME + " = '" + name + "'";
		cursor = database.query(DatabaseHelper.TABLE_PREFERENCES, colonnes, where, null, null, null, null);
		if (cursor.moveToFirst())
			valeur = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLONNE_PREF_VALEUR));
		else
			valeur = "";
		return valeur;
	} catch (Exception e)
	{
		valeur = "";
	} finally
	{
		if (cursor != null)
			cursor.close();
	}

	return valeur;

}

public int getIntPreference(@NonNull String name)
{
	String res = getStringPreference(name);
	if (res == null)
		return 0;

	try
	{
		return Integer.valueOf(res);
	} catch (Exception e)
	{
		return 0;
	}
}

public Cursor getCursor()
{
	return database.query(DatabaseHelper.TABLE_PREFERENCES, null, null, null, null, null, null);
}


public void setIntPreference(String name, int val)
{
	setStringPreference(name, Integer.toString(val));
}

public void setStringPreference(String name, String val)
{
	try
	{
		ContentValues valeurs = new ContentValues();
		valeurs.put(DatabaseHelper.COLONNE_PREF_NAME, name);
		valeurs.put(DatabaseHelper.COLONNE_PREF_VALEUR, val);
		int id = (int) database.insertWithOnConflict(DatabaseHelper.TABLE_PREFERENCES, null, valeurs, SQLiteDatabase.CONFLICT_IGNORE);
		if (id == -1)
		{
			String where = DatabaseHelper.COLONNE_PREF_NAME + " = '" + name + "'";
			database.update(DatabaseHelper.TABLE_PREFERENCES, valeurs, where, null);  // number 1 is the _id here, update to variable for your code
		}
	} catch (Exception e)
	{
		MainActivity.SignaleErreur("modification de preference", e);
	}
}
}
