package lpi.motsdepasse;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class EditMotDePasseActivity extends AppCompatActivity
{
public static final int RESULT_EDIT_MDP = 1;
public static final String ACTION_EDIT_MDP_FINISHED = "lpi.EDITEMDP";
public static final String EXTRA_OPERATION = "lpi.OPERATION";
public static final String EXTRA_OPERATION_AJOUTE = "AJOUTE";
public static final String EXTRA_OPERATION_MODIFIE = "MODIFIE";

EditText eNom, eUtilisateur, eMotDePasse, eDescription;
ImageButton bCouleur, bCategorie;
ImageView iancienMdp;
String _operation;
MotDePasse _mdp;
boolean _nouveauMotDePasse;
@Override
protected void onCreate(Bundle savedInstanceState)
{
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_edit_mot_de_passe);
	Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	getSupportActionBar().setDisplayShowTitleEnabled(true);
	FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
	fab.setOnClickListener(new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			onOK();
		}
	});
	eNom = (EditText) findViewById(R.id.editTextNom);
	eUtilisateur = (EditText) findViewById(R.id.editTextUtilisateur);
	eMotDePasse = (EditText) findViewById(R.id.editTextMotDePasse);
	eDescription = (EditText) findViewById(R.id.editTextDescription);
	bCategorie = (ImageButton) findViewById(R.id.imageButtonIcone);
	bCouleur = (ImageButton) findViewById(R.id.imageButtonCouleur);
	iancienMdp = (ImageView)findViewById(R.id.imageViewAncien)     ;
	_nouveauMotDePasse = false;

	if (savedInstanceState == null)
		savedInstanceState = this.getIntent().getExtras();

	if (savedInstanceState != null)
	{
		_mdp = new MotDePasse(savedInstanceState);
		_operation = savedInstanceState.getString(EXTRA_OPERATION);
	}
	else
	{
		_mdp = new MotDePasse();
		_operation = EXTRA_OPERATION_AJOUTE;
	}

	eMotDePasse.addTextChangedListener(new TextWatcher()
	{

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count)
		{
			_nouveauMotDePasse = true;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
		                              int after)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable arg0)
		{
			// TODO Auto-generated method stub

		}
	});



	MajUI();

}

private void MajUI()
{
	eNom.setText(_mdp._nom);
	eDescription.setText(_mdp._description);
	eUtilisateur.setText(_mdp._utilisateur);
	bCategorie.setBackgroundColor(_mdp._couleur);
	bCouleur.setBackgroundColor(_mdp._couleur);
	bCategorie.setImageResource(_mdp.getCategoryIconResource());
	if (_mdp._bitmap != null)
	{
		iancienMdp.setImageBitmap(_mdp._bitmap);
		iancienMdp.setVisibility(View.VISIBLE);
	}
	else
		iancienMdp.setVisibility(View.GONE);
}

@Override
protected void onResume()
{
	//IntentFilter filter = new IntentFilter();
	//filter.addAction(Partages.ACTION_RESULT_RECHERCHE_PARTAGE);
	//registerReceiver(receiver, filter);
	super.onResume();
}

@Override
protected void onPause()
{
	//unregisterReceiver(receiver);
	super.onPause();
}

@Override
public boolean onCreateOptionsMenu(Menu menu)
{
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu_dialog_box, menu);
	return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item)
{
	switch (item.getItemId())
	{
		case R.id.buttonOK:
			onOK();
			return true;

		/*case R.id.buttonCancel:
			onAnnuler();
			return true;              */

		default:
			// If we got here, the user's action was not recognized.
			// Invoke the superclass to handle it.
			return super.onOptionsItemSelected(item);

	}
}

/**
 * OK: fermer l'ecran et renvoyer les donnees
 */
public void onOK()
{
	Intent returnIntent = new Intent();
	returnIntent.setAction(ACTION_EDIT_MDP_FINISHED);
	returnIntent.putExtra("result", RESULT_EDIT_MDP);

	_mdp._nom = eNom.getText().toString();
	_mdp._description = eDescription.getText().toString();
	_mdp._utilisateur = eUtilisateur.getText().toString();

	if ( _nouveauMotDePasse)
		// Si le mot de passe a ete modifie
		_mdp.construitImage(this, eMotDePasse.getText().toString());

	boolean erreur = false;

	if (displayError("".equals(_mdp._nom), eNom, "Donnez un nom à votre mot de passe"))
		erreur = true;

	if (erreur)
		return;


	Bundle bundle = new Bundle();
	_mdp.toBundle(bundle);

	bundle.putString(EXTRA_OPERATION, _operation);
	returnIntent.putExtras(bundle);
	setResult(Activity.RESULT_OK, returnIntent);

	finish();
}

private boolean displayError(boolean error, @NonNull View v, @NonNull String message)
{
	if (error)
	{
		if (v instanceof EditText)
		{
			((TextView) v).setError(message);
		}
		else
		{
			final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			MainActivity.MessageNotification(v, message);
		}
	}
	else
	{
		if (v instanceof TextView)
			((TextView) v).setError(null);
	}


	return error;
}

public void onAnnuler()
{
	Intent returnIntent = new Intent();
	setResult(Activity.RESULT_CANCELED, returnIntent);
	finish();
}


public void onClickChangeCouleur(View v)
{
	final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
	helpBuilder.setTitle("Choisissez une couleur");
	//helpBuilder.setMessage("Couleur");

	LayoutInflater inflater = getLayoutInflater();
	View layout = inflater.inflate(R.layout.choix_couleur, null);
	helpBuilder.setView(layout);

	helpBuilder.setNegativeButton("Annuler",
			new DialogInterface.OnClickListener()
			{

				public void onClick(DialogInterface dialog, int which)
				{
					// Do nothing but close the dialog
				}
			});
	// Remember, create doesn't show the dialog
	final AlertDialog helpDialog = helpBuilder.create();

	ImageButton.OnClickListener listener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			ColorDrawable buttonColor = (ColorDrawable) v.getBackground();
			helpDialog.dismiss();
			_mdp._couleur = buttonColor.getColor();
			bCategorie.setBackgroundColor(_mdp._couleur);
			bCouleur.setBackgroundColor(_mdp._couleur);
		}
	};

	layout.findViewById(R.id.button1).setOnClickListener(listener);
	layout.findViewById(R.id.button2).setOnClickListener(listener);
	layout.findViewById(R.id.button3).setOnClickListener(listener);
	layout.findViewById(R.id.button4).setOnClickListener(listener);
	layout.findViewById(R.id.button5).setOnClickListener(listener);
	layout.findViewById(R.id.button6).setOnClickListener(listener);
	layout.findViewById(R.id.button7).setOnClickListener(listener);
	layout.findViewById(R.id.button8).setOnClickListener(listener);
	layout.findViewById(R.id.button9).setOnClickListener(listener);
	layout.findViewById(R.id.button10).setOnClickListener(listener);
	layout.findViewById(R.id.button11).setOnClickListener(listener);
	layout.findViewById(R.id.button12).setOnClickListener(listener);
	layout.findViewById(R.id.button13).setOnClickListener(listener);
	layout.findViewById(R.id.button14).setOnClickListener(listener);
	layout.findViewById(R.id.button15).setOnClickListener(listener);
	layout.findViewById(R.id.button16).setOnClickListener(listener);
	helpDialog.show();
}
public void onClickChangeIcone(View v)
{
	final AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
	helpBuilder.setTitle("Choisissez une icone");
	//helpBuilder.setMessage("Couleur");

	LayoutInflater inflater = getLayoutInflater();
	View layout = inflater.inflate(R.layout.choix_icone, null);
	helpBuilder.setView(layout);

	helpBuilder.setNegativeButton("Annuler",
			new DialogInterface.OnClickListener()
			{

				public void onClick(DialogInterface dialog, int which)
				{
					// Do nothing but close the dialog
				}
			});
	// Remember, create doesn't show the dialog
	final AlertDialog helpDialog = helpBuilder.create();

	ImageButton.OnClickListener listener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			int icone = v.getId();
			helpDialog.dismiss();
			_mdp.setCategorieFromResource( buttonIdToIconResource(icone));
			bCategorie.setImageResource(_mdp.getCategoryIconResource());
		}
	};

	layout.findViewById(R.id.button1).setOnClickListener(listener);
	layout.findViewById(R.id.button2).setOnClickListener(listener);
	layout.findViewById(R.id.button3).setOnClickListener(listener);
	layout.findViewById(R.id.button4).setOnClickListener(listener);
	layout.findViewById(R.id.button5).setOnClickListener(listener);
	layout.findViewById(R.id.button6).setOnClickListener(listener);
	layout.findViewById(R.id.button7).setOnClickListener(listener);
	layout.findViewById(R.id.button8).setOnClickListener(listener);
	layout.findViewById(R.id.button9).setOnClickListener(listener);
	layout.findViewById(R.id.button10).setOnClickListener(listener);
	layout.findViewById(R.id.button11).setOnClickListener(listener);
	layout.findViewById(R.id.button12).setOnClickListener(listener);
	layout.findViewById(R.id.button13).setOnClickListener(listener);
	layout.findViewById(R.id.button14).setOnClickListener(listener);
	layout.findViewById(R.id.button15).setOnClickListener(listener);
	layout.findViewById(R.id.button16).setOnClickListener(listener);
	helpDialog.show();
}

private @DrawableRes  int buttonIdToIconResource(int icone)
{
	switch (icone)
	{
		case R.id.button1: return   R.drawable.key ;
		case R.id.button2: return   R.drawable.web ;
		case R.id.button3: return   R.drawable.windows ;
		case R.id.button4: return   R.drawable.android ;
		case R.id.button5: return   R.drawable.apple ;
		case R.id.button6: return   R.drawable.attachment ;
		case R.id.button7: return   R.drawable.airplane ;
		case R.id.button8: return   R.drawable.bell ;
		case R.id.button9: return   R.drawable.beach ;
		case R.id.button10: return   R.drawable.briefcase ;
		case R.id.button11: return   R.drawable.cart ;
		case R.id.button12: return   R.drawable.facebook ;
		case R.id.button13: return   R.drawable.ferry ;
		case R.id.button14: return   R.drawable.gmail ;
		case R.id.button15: return   R.drawable.hotel ;
		case R.id.button16: return   R.drawable.twitter ;
		default:
			return R.drawable.key ;
	}
}

}
