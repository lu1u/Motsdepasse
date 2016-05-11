package lpi.motsdepasse.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lucien on 23/04/2016.
 */
public class LoginManager
{
private static LoginManager INSTANCE = null;
private boolean _loginOK;
private LoginManager(Context context)
{
	      _loginOK = true;
}

/**
 * Point d'accès pour l'instance unique du singleton
 */
public static synchronized LoginManager getInstance(Context context)
{
	if (INSTANCE == null)
	{
		INSTANCE = new LoginManager(context);
	}
	return INSTANCE;
}


public boolean IsLoginOK()
{
	return _loginOK;
}

public boolean tryLogin(String mdp)
{
	_loginOK = ( "OK".equals(mdp));
	return _loginOK;
}
}
