package lpi.motsdepasse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import lpi.motsdepasse.MainActivity;
import lpi.motsdepasse.MotDePasse;


/**
 * Created by lucien on 26/01/2016.
 */
@SuppressWarnings("ALL")
public class MdpDatabase
{
public static final int INVALID_ID = -1;

/**
 * Instance unique non préinitialisée
 */
private static MdpDatabase INSTANCE = null;
private SQLiteDatabase database;
private DatabaseHelper dbHelper;

private MdpDatabase(Context context)
{
	dbHelper = new DatabaseHelper(context);
	database = dbHelper.getWritableDatabase();
}

/**
 * Point d'accès pour l'instance unique du singleton
 */
public static synchronized MdpDatabase getInstance(Context context)
{
	if (INSTANCE == null)
	{
		INSTANCE = new MdpDatabase(context);
	}
	return INSTANCE;
}

@Override
public void finalize()
{
	dbHelper.close();
}

/***
 * Ajoute le motDePasse
 *
 * @param motDePasse
 */
public void Ajoute(MotDePasse motDePasse)
{
	ContentValues initialValues = new ContentValues();
	motDePasse.toContentValues(initialValues, false);

	try
	{
		int id = (int) database.insert(DatabaseHelper.TABLE_MOTS_DE_PASSE, null, initialValues);
	} catch (Exception e)
	{
		MainActivity.SignaleErreur("ajout du motDePasse", e);
	}
}

/***
 * Retourne un motDePasse cree a partir de la base de donnnees
 *
 * @param Id
 * @return motDePasse
 */
public MotDePasse getMotDePasse(int Id)
{
	MotDePasse motDePasse = null;
	Cursor cursor = null;
	try
	{
		String[] colonnes = null;
		String where = DatabaseHelper.COLUMN_MDP_ID + " = " + Id;
		cursor = database.query(DatabaseHelper.TABLE_MOTS_DE_PASSE, colonnes, where, null, null, null, null);
		cursor.moveToFirst();
		motDePasse = new MotDePasse(cursor);
		return motDePasse;
	} catch (SQLException e)
	{
		e.printStackTrace();
	} finally
	{
		if (cursor != null)
			cursor.close();
	}

	return motDePasse;

}

public void ModifieMotDePasse(MotDePasse motDePasse)
{
	try
	{
		ContentValues valeurs = new ContentValues();
		motDePasse.toContentValues(valeurs, true);
		database.update(DatabaseHelper.TABLE_MOTS_DE_PASSE, valeurs, DatabaseHelper.COLUMN_MDP_ID + " = " + motDePasse._Id, null);
	} catch (Exception e)
	{
		MainActivity.SignaleErreur("modification du motDePasse", e);
	}
}

public void SupprimeMotDePasse(MotDePasse motDePasse)
{
	try
	{
		database.delete(DatabaseHelper.TABLE_MOTS_DE_PASSE, DatabaseHelper.COLUMN_MDP_ID + " = " + motDePasse._Id, null);
	} catch (Exception e)
	{
		MainActivity.SignaleErreur("suppression motDePasse", e);
	}
}


public Cursor getCursor()
{
	return database.query(DatabaseHelper.TABLE_MOTS_DE_PASSE, null, null, null, null, null, null);
}

public long nbMotDePasses()
{
	Cursor cursor = database.rawQuery("SELECT COUNT (*) FROM " + DatabaseHelper.TABLE_MOTS_DE_PASSE, null);
	int count = 0;
	if (null != cursor)
		if (cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			count = cursor.getInt(0);
		}
	cursor.close();

	return count;
}


}
