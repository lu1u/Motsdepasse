package lpi.motsdepasse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import lpi.motsdepasse.database.DatabaseHelper;
import lpi.motsdepasse.database.LoginManager;
import lpi.motsdepasse.database.MdpAdapter;
import lpi.motsdepasse.database.MdpDatabase;
import lpi.motsdepasse.database.PreferencesDatabase;
import lpi.motsdepasse.login.LoginActivity;
import lpi.motsdepasse.preferences.PreferencesActivity;

public class MainActivity extends AppCompatActivity
{

private static AppCompatActivity _applicationActivity;
static public final int RESULT_EDIT_MDP = 0;
static public final int RESULT_LOGIN = 1;
MdpAdapter _adapter;
int _currentItemSelected = -1;
private BroadcastReceiver receiver = new BroadcastReceiver()
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();

		//if (MdpAdapter.ACTION_LANCE_SAUVEGARDE.equals(action))
		//	lancerSauvegardeProfil(intent);
		//else
		if (EditMotDePasseActivity.ACTION_EDIT_MDP_FINISHED.equals(action))
			onEditMdp(intent);
	}
};

/***
 * Reception du résultat de l'activite d'edition d'un profil
 *
 * @param data
 */
private void onEditMdp(Intent data)
{
	String Operation = data.getExtras().getString(EditMotDePasseActivity.EXTRA_OPERATION);
	MotDePasse motDePasse = new MotDePasse(data.getExtras());

	MdpDatabase database = MdpDatabase.getInstance(this);
	if (EditMotDePasseActivity.EXTRA_OPERATION_AJOUTE.equals(Operation))
	{
		// Ajouter le profil
		database.Ajoute(motDePasse);
		_adapter.changeCursor(database.getCursor());
		_currentItemSelected = -1;
	}
	else if (EditMotDePasseActivity.EXTRA_OPERATION_MODIFIE.equals(Operation))
	{
		// Modifier le profil
		database.ModifieMotDePasse(motDePasse);
		_adapter.changeCursor(database.getCursor());
		//_currentItemSelected = -1;
	}
	TraceDatabase();
}


@Override
protected void onCreate(Bundle savedInstanceState)
{
	_applicationActivity = this;

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);
	getSupportActionBar().setDisplayShowTitleEnabled(true);

	FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
	fab.setOnClickListener(new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			Intent intent = new Intent(MainActivity.this, EditMotDePasseActivity.class);
			startActivityForResult(intent, RESULT_EDIT_MDP);
		}
	});

	ListView listView = (ListView) findViewById(R.id.listView);
	listView.setEmptyView(findViewById(R.id.textViewEmpty));

	_adapter = new MdpAdapter(this, MdpDatabase.getInstance(this).getCursor());
	listView.setAdapter(_adapter);
	registerForContextMenu(listView);

	listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			view.setSelected(true);
			_currentItemSelected = position;
			afficheMotDePasse();
		}
	});

	listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
	{
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
		{
			view.setSelected(true);
			_currentItemSelected = position;
			return false;
		}
	});
	registerForContextMenu(listView);

	IntentFilter filter = new IntentFilter();
	filter.addAction(EditMotDePasseActivity.ACTION_EDIT_MDP_FINISHED);
	registerReceiver(receiver, filter);
	TraceDatabase();
}

@Override
public boolean onCreateOptionsMenu(Menu menu)
{
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.menu_main, menu);
	return true;
}

/***
 * Affiche le mot de passe selectionne
 */
private void afficheMotDePasse()
{
	if (_currentItemSelected == -1)
		return;

	LoginManager lm = LoginManager.getInstance(this);
	if (!lm.IsLoginOK())
	{
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivityForResult(intent, RESULT_LOGIN);
		return;
	}

	MotDePasse motDePasse = _adapter.get(_currentItemSelected);
	if (motDePasse != null)
	{
		Intent intent = new Intent(this, AfficheMotDePasseActivity.class);
		// Pour eviter de passer trop d'informations sur le mot de passe a l'exterieur de l'appli: uniquement l'id
		intent.putExtra(AfficheMotDePasseActivity.EXTRA_MDP_ID, motDePasse._Id);
		startActivity(intent);


		/*LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast_affiche_mdp,
				(ViewGroup) findViewById(R.id.toast_layout_root));

		((TextView) layout.findViewById(R.id.textViewNom)).setText(motDePasse._nom);
		((TextView) layout.findViewById(R.id.textViewDescription)).setText(motDePasse._description);
		((TextView) layout.findViewById(R.id.textViewUtilisateur)).setText(motDePasse._utilisateur);

		ImageView im = (ImageView) layout.findViewById(R.id.imageViewMotDePasse);
		if ( motDePasse._bitmap!=null)
			im.setImageBitmap(motDePasse._bitmap);

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();  */
	}
}

private void TraceDatabase()
{
	MdpDatabase base = MdpDatabase.getInstance(this);

	Log.d("MDP", "Nombre de mots de passe: " + base.nbMotDePasses());

	Cursor c = base.getCursor();
	c.moveToFirst();

	while (c.moveToNext())
	{
		Log.d("MDP", c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_MDP_ID)) + " : " + c.getInt(c.getColumnIndex(DatabaseHelper.COLUMN_MDP_NOM)));
	}
	c.close();
	PreferencesDatabase pbase = PreferencesDatabase.getInstance(this);
	c = pbase.getCursor();
	c.moveToFirst();

	while (c.moveToNext())
	{
		Log.d("MDP", c.getString(c.getColumnIndex(DatabaseHelper.COLONNE_PREF_NAME)) + " : " + c.getString(c.getColumnIndex(DatabaseHelper.COLONNE_PREF_VALEUR)));
	}
	c.close();
}

@Override
public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
{
	super.onCreateContextMenu(menu, v, menuInfo);
	if (v.getId() == R.id.listView)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main_liste, menu);
	}
}


@Override
public boolean onContextItemSelected(MenuItem item)
{
	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	switch (item.getItemId())
	{
		case R.id.action_afficher:
			afficheMotDePasse();
			return true;
		case R.id.action_modifier:
			modifieMotDePasse();
			return true;
		case R.id.action_supprimer:
			supprimeMotDePasse();
			return true;
		default:
			return super.onContextItemSelected(item);
	}
}

private void supprimeMotDePasse()
{
	LoginManager lm = LoginManager.getInstance(this);
	if (!lm.IsLoginOK())
	{
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivityForResult(intent, RESULT_LOGIN);
		return;
	}
	if (_currentItemSelected == -1)
		return;

	final MotDePasse motDePasseASupprimer = _adapter.get(_currentItemSelected);

	if (motDePasseASupprimer != null)
	{
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Supprimer");
		dialog.setMessage("Supprimer le mot de passe " + motDePasseASupprimer._nom + " ?");
		dialog.setCancelable(false);
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(android.R.string.ok),
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int buttonId)
					{
						//if (profilASupprimer != null)
						{
							MdpDatabase database = MdpDatabase.getInstance(MainActivity.this);
							// Supprimer
							database.SupprimeMotDePasse(motDePasseASupprimer);
							_adapter.changeCursor(database.getCursor());
							_currentItemSelected = -1;
						}
					}
				});
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel),
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int buttonId)
					{
						// Ne rien faire
					}
				});
		dialog.setIcon(android.R.drawable.ic_dialog_alert);
		dialog.show();
	}
}

private void modifieMotDePasse()
{
	LoginManager lm = LoginManager.getInstance(this);
	if (!lm.IsLoginOK())
	{
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivityForResult(intent, RESULT_LOGIN);
		return;
	}

	if (_currentItemSelected == -1)
		return;

	MotDePasse motDePasse = _adapter.get(_currentItemSelected);
	if (motDePasse != null)
	{
		Intent intent = new Intent(this, EditMotDePasseActivity.class);
		Bundle b = new Bundle();
		motDePasse.toBundle(b);
		b.putString(EditMotDePasseActivity.EXTRA_OPERATION, EditMotDePasseActivity.EXTRA_OPERATION_MODIFIE);
		intent.putExtras(b);
		startActivityForResult(intent, RESULT_EDIT_MDP);
	}
}

@Override
public boolean onOptionsItemSelected(MenuItem item)
{
	// Handle action bar item clicks here. The action bar will
	// automatically handle clicks on the Home/Up button, so long
	// as you specify a parent activity in AndroidManifest.xml.
	int id = item.getItemId();

	switch( id )
	{
		case R.id.action_settings :
		{
			Intent intent = new Intent(this, PreferencesActivity.class);
			startActivity(intent);
			break;
		}

		default:
			return super.onOptionsItemSelected(item);
	}

	return true;
}

/**
 * Dispatch incoming result to the correct fragment.
 *
 * @param requestCode
 * @param resultCode
 * @param data
 */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data)
{
	super.onActivityResult(requestCode, resultCode, data);
	if (resultCode == RESULT_CANCELED)
		return;

	switch (requestCode)
	{
		case RESULT_EDIT_MDP:
			onEditMdp(data);
			break;
	}
}

/***
 * Signaler une erreur
 *
 * @param message
 * @param e
 */

static public void SignaleErreur(String message, Exception e)
{
/*	LayoutInflater inflater = _applicationActivity.getLayoutInflater();
	View layout = inflater.inflate(R.layout.layout_toast_erreur,
			(ViewGroup) _applicationActivity.findViewById(R.id.layoutRoot));

	TextView tv = (TextView) layout.findViewById(R.id.textViewTextErreur);
	String m = String.format(tv.getText().toString(), message);
	tv.setText(m);

	m = e.getLocalizedMessage();
	int nbMax = 0;
	for (StackTraceElement s : e.getStackTrace())
	{
		m += "\n" + (s.getClassName() + '/' + s.getMethodName() + ':' + s.getLineNumber());
		nbMax++;
		if (nbMax > 2)
			break;
	}

	((TextView) layout.findViewById(R.id.textViewStackTrace)).setText(m);
	Toast toast = new Toast(_applicationActivity.getApplicationContext());
	toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	toast.setDuration(Toast.LENGTH_LONG);
	toast.setView(layout);
	toast.show();      */
	Toast.makeText(_applicationActivity.getApplicationContext(), message, Toast.LENGTH_LONG).show();

}

public static void MessageNotification(View v, String message)
{
	Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
}
}
