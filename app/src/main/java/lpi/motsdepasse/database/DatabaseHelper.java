package lpi.motsdepasse.database;

/**
 * Utilitaire de gestion de la base de donnees
 */

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper
{
public static final int DATABASE_VERSION = 5;
public static final String DATABASE_NAME = "database.db";

////////////////////////////////////////////////////////////////////////////////////////////////////
// Table mots de passe
public static final String TABLE_MOTS_DE_PASSE = "MDP";
public static final String COLUMN_MDP_ID = "_id";
public static final String COLUMN_MDP_NOM = "NOM";
public static final String COLUMN_MDP_UTILISATEUR = "UTILISATEUR";
public static final String COLUMN_MDP_MOTDEPASSE = "MOTDEPASSE";
public static final String COLUMN_MDP_DESCRIPTION = "DESCRIPTION";
public static final String COLUMN_MDP_CATEGORIE = "CATEGORIE";
public static final String COLUMN_MDP_COULEUR = "COULEUR";
public static final String COLUMN_MDP_IMAGE = "IMAGE";


////////////////////////////////////////////////////////////////////////////////////////////////////
// Table categories
/*public static final String TABLE_CATEGORIES = "CATEGORIES";
public static final String COLUMN_CATEGORIES_ID = "_id";
public static final String COLONNE_CATEGORIES_NOM = "NOM";
           */
////////////////////////////////////////////////////////////////////////////////////////////////////
// Table traces
public static final String TABLE_TRACES = "TRACES";
public static final String COLONNE_TRACES_ID = "_id";
public static final String COLONNE_TRACES_DATE = "DATE";
public static final String COLONNE_TRACES_NIVEAU = "NIVEAU";
public static final String COLONNE_TRACES_LIGNE = "LIGNE";


////////////////////////////////////////////////////////////////////////////////////////////////////
// Table preferences
public static final String TABLE_PREFERENCES = "PREFERENCES";
public static final String COLONNE_PREF_NAME = "NAME";
public static final String COLONNE_PREF_VALEUR = "VALEUR";

private static final String DATABASE_MDP_CREATE = "create table "
		+ TABLE_MOTS_DE_PASSE + "("
		+ COLUMN_MDP_ID + " integer primary key autoincrement, "
		+ COLUMN_MDP_NOM + " text not null,"
		+ COLUMN_MDP_UTILISATEUR+ " text,"
		+ COLUMN_MDP_MOTDEPASSE + " text,"
		+ COLUMN_MDP_DESCRIPTION + " text,"
		+ COLUMN_MDP_CATEGORIE + " integer,"
		+ COLUMN_MDP_COULEUR+ " integer,"
		+ COLUMN_MDP_IMAGE + " blob"
		+ ");";
/*
private static final String DATABASE_CATEGORIES_CREATE = "create table "
		+ TABLE_CATEGORIES + "("
		+ COLUMN_CATEGORIES_ID+ " integer primary key autoincrement, "
		+ COLONNE_CATEGORIES_NOM+ " text UNIQUE ON CONFLICT IGNORE"
		+ ");";     */
private static final String DATABASE_TRACES_CREATE = "create table "
		+ TABLE_TRACES + "("
		+ COLONNE_TRACES_ID + " integer primary key autoincrement, "
		+ COLONNE_TRACES_DATE + " integer,"
		+ COLONNE_TRACES_NIVEAU + " integer,"
		+ COLONNE_TRACES_LIGNE + " text not null"
		+ ");";
private static final String DATABASE_PREF_CREATE = "create table "
		+ TABLE_PREFERENCES + "("
		+ COLONNE_PREF_NAME + " text primary key not null, "
		+ COLONNE_PREF_VALEUR + " text "
		+ ");";

public DatabaseHelper(Context context)
{
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
}

static public int CalendarToSQLiteDate(@Nullable Calendar cal)
{
	if (cal == null)
		cal = Calendar.getInstance();
	return (int) (cal.getTimeInMillis() / 1000L);
}

@NonNull
static public Calendar SQLiteDateToCalendar(int date)
{
	Calendar cal = Calendar.getInstance();
	cal.setTimeInMillis((long) date * 1000L);
	return cal;
}

@NonNull
public static String getStringFromAnyColumn(@NonNull Cursor cursor, int colonne)
{
	Object o = getObjectFromAnyColumn(cursor, colonne);
	if (o != null)
		return o.toString();
	else
		return "Impossible de lire la colonne " + cursor.getColumnName(colonne);
}

@Nullable
public static Object getObjectFromAnyColumn(@NonNull Cursor cursor, int colonne)
{
	try
	{
		return cursor.getInt(colonne);
	} catch (Exception e)
	{
		try
		{
			return cursor.getShort(colonne);
		} catch (Exception e1)
		{
			try
			{
				return cursor.getLong(colonne);
			} catch (Exception e2)
			{
				try
				{
					return cursor.getDouble(colonne);
				} catch (Exception e3)
				{
					try
					{
						return cursor.getFloat(colonne);
					} catch (Exception e4)
					{
						try
						{
							return cursor.getString(colonne);
						} catch (Exception e5)
						{
							Log.e("Dabase", "impossible de lire la colonne " + colonne);
						}
					}
				}
			}
		}
	}

	return null;
}

@Override
public void onCreate(SQLiteDatabase database)
{
	try
	{
		//database.execSQL(DATABASE_CATEGORIES_CREATE);
		database.execSQL(DATABASE_MDP_CREATE);
		database.execSQL(DATABASE_TRACES_CREATE);
		database.execSQL(DATABASE_PREF_CREATE);
	} catch (SQLException e)
	{
		e.printStackTrace();
	}
}

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
{
	try
	{
		Log.w(DatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOTS_DE_PASSE);
		db.execSQL("DROP TABLE IF EXISTS " + "CATEGORIES");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES);
		onCreate(db);
	} catch (SQLException e)
	{
		e.printStackTrace();
	}
}


}
