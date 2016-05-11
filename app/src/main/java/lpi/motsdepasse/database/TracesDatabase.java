package lpi.motsdepasse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;


/**
 * Base des traces (log)
 */
public class TracesDatabase
{
/**
 * Instance unique non préinitialisée
 */
private static TracesDatabase INSTANCE = null;
private SQLiteDatabase database;
private DatabaseHelper dbHelper;

private TracesDatabase(Context context)
{
	dbHelper = new DatabaseHelper(context);
	database = dbHelper.getWritableDatabase();
}

/**
 * Point d'accès pour l'instance unique du singleton
 */
public static synchronized TracesDatabase getInstance(Context context)
{
	if (INSTANCE == null)
	{
		INSTANCE = new TracesDatabase(context);
	}
	return INSTANCE;
}

@Override
public void finalize()
{
	try
	{
		super.finalize();
	} catch (Throwable throwable)
	{
		throwable.printStackTrace();
	}
	dbHelper.close();
}


public void Ajoute(int Date, int niveau, String ligne)
{
	try
	{
		if (DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE_TRACES) > 200)
		{
			// Supprimer les 10 premieres pour eviter que la table des traces ne grandisse trop
			database.execSQL("DELETE FROM " + DatabaseHelper.TABLE_TRACES + " WHERE " + DatabaseHelper.COLONNE_TRACES_ID
					+ " IN (SELECT " + DatabaseHelper.COLONNE_TRACES_ID + " FROM " + DatabaseHelper.TABLE_TRACES + " ORDER BY " + DatabaseHelper.COLONNE_TRACES_ID + " LIMIT 5)");
		}

		ContentValues initialValues = new ContentValues();
		initialValues.put(DatabaseHelper.COLONNE_TRACES_DATE, Date);
		initialValues.put(DatabaseHelper.COLONNE_TRACES_NIVEAU, niveau);
		initialValues.put(DatabaseHelper.COLONNE_TRACES_LIGNE, ligne);

		database.insert(DatabaseHelper.TABLE_TRACES, null, initialValues);
	} catch (Exception e)
	{
		//MainActivity.SignaleErreur("ajout d'une ligne de trace", e);
	}
}

public Cursor getCursor(int niveau)
{
	return database.query(DatabaseHelper.TABLE_TRACES, null, DatabaseHelper.COLONNE_TRACES_NIVEAU + " >= " + niveau, null, null, null, DatabaseHelper.COLONNE_TRACES_ID + " ASC");
}

public void Vide()
{
	database.delete(DatabaseHelper.TABLE_TRACES, null, null);
}
}
