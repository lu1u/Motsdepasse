package lpi.motsdepasse.preferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import lpi.motsdepasse.MainActivity;
import lpi.motsdepasse.R;
import lpi.motsdepasse.database.PreferencesDatabase;
import lpi.motsdepasse.login.PatternLockView;

public class PreferencesActivity extends AppCompatActivity implements PatternLockView.patternDoneListener
{
public static final String ACTION_PREFERENCES_FINISHED = "lpi.motdepasse.preferencesfinished";
private static final int TYPE_AUCUN = 0;
private static final int TYPE_PATTERN = 1;
private static final int TYPE_NUMERIQUE = 2;

private int _typeDeverouillage;
private String _MdpNumerique;
private String _MdpPrincipal;
private String _MdpPattern;

TextView _tvCodeNumerique;
PatternLockView _lockPattern;
@Override
protected void onCreate(Bundle savedInstanceState)
{
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_preferences);
	Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);

	FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
	fab.setOnClickListener(new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			onOK(view);
		}
	});
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	// Listener pour les radio boutons deverouillage alternatif
	RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupDevAlternatif);
	radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
	                                      {

		                                      @Override
		                                      public void onCheckedChanged(RadioGroup group, int checkedId)
		                                      {
			                                      switch (checkedId)
			                                      {
				                                      case R.id.radioButtonAucun:
					                                      _typeDeverouillage = TYPE_AUCUN;
					                                      Cache(R.id.layoutChiffre);
					                                      Cache(R.id.layoutSchema);
					                                      break;

				                                      case R.id.radioButtonSchema:
					                                      _typeDeverouillage = TYPE_PATTERN;
					                                      Cache(R.id.layoutChiffre);
					                                      Montre(R.id.layoutSchema);
					                                      break;

				                                      case R.id.radioButtonNumerique:
					                                      _typeDeverouillage = TYPE_NUMERIQUE;
					                                      Cache(R.id.layoutSchema);
					                                      Montre(R.id.layoutChiffre);
					                                      break;
			                                      }
		                                      }

	                                      }

	);

	PreferencesDatabase database =PreferencesDatabase.getInstance(this);
	_typeDeverouillage = database.getIntPreference(PreferencesDatabase.PREF_DEVEROUILLAGE_ALTERNATIF);
	_MdpPrincipal = database.getStringPreference(PreferencesDatabase.PREF_DEVEROUILLAGE_PRINCIPAL);
	_MdpNumerique = database.getStringPreference(PreferencesDatabase.PREF_DEVEROUILLAGE_NUMERIQUE);
	_MdpPattern = database.getStringPreference(PreferencesDatabase.PREF_DEVEROUILLAGE_PATTERN);
	switch (_typeDeverouillage)
	{
		case TYPE_AUCUN: 	radioGroup.check(R.id.radioButtonAucun); break;
		case TYPE_PATTERN:   radioGroup.check(R.id.radioButtonSchema); break;
		case TYPE_NUMERIQUE:radioGroup.check(R.id.radioButtonNumerique); break;
	}

	// Initialisation Mot de passe principal
	((EditText)findViewById(R.id.editTextMotDePasse)).setText(_MdpPrincipal);

	// Initialisation LockPattern
	_lockPattern = (PatternLockView)findViewById(R.id.viewLockPattern);
	_lockPattern.setPattern( _MdpPattern);
	_lockPattern.setPatternListener(this);

	// Initialisation Numerique
	_tvCodeNumerique = (TextView)findViewById(R.id.textViewCodeNumerique);
	_tvCodeNumerique.setText(_MdpNumerique);
}

/***
 * Validation
 * @param view
 */
private void onOK(View view)
{
	PreferencesDatabase database =PreferencesDatabase.getInstance(this);

	switch (_typeDeverouillage)
	{
		case TYPE_AUCUN: 	break;
		case TYPE_PATTERN:
		{
			if ( _MdpPattern.length() == 0)
			{
				MainActivity.MessageNotification( view, "Veuillez choisir un schéma de déverouillage");
				return;
			}
			break;
		}
		case TYPE_NUMERIQUE:
		{
			if ( _MdpNumerique.length() == 0)
			{
				MainActivity.MessageNotification( view, "Veuillez choisir un code de déverouillage numérique");
				return;
			}
			break;
		}
	}
	_MdpPrincipal = ((EditText)findViewById(R.id.editTextMotDePasse)).getText().toString();

	database.setStringPreference(PreferencesDatabase.PREF_DEVEROUILLAGE_PRINCIPAL, _MdpPrincipal);
	database.setIntPreference(PreferencesDatabase.PREF_DEVEROUILLAGE_ALTERNATIF, _typeDeverouillage);
	database.setStringPreference(PreferencesDatabase.PREF_DEVEROUILLAGE_PATTERN, _MdpPattern);
	database.setStringPreference(PreferencesDatabase.PREF_DEVEROUILLAGE_NUMERIQUE, _MdpNumerique);

	Intent returnIntent = new Intent();
	returnIntent.setAction(ACTION_PREFERENCES_FINISHED);
	setResult(Activity.RESULT_OK, returnIntent);

	finish();
}

private void Montre(@IdRes int layoutChiffre)
{
	View v = findViewById(layoutChiffre);
	if (v != null)
	{
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.enter);
		v.setAnimation(anim);
		v.setVisibility(View.VISIBLE);
	}
}

private void Cache(int layoutChiffre)
{
	View v = findViewById(layoutChiffre);
	if (v != null)
	{
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.exit);
		v.setAnimation(anim);
		v.setVisibility(View.GONE);
	}
}


public void onClickPaveNumerique(View v)
{
	switch( v.getId())
	{
		case R.id.buttonZero: _MdpNumerique += "0"; break;
		case R.id.buttonUn: _MdpNumerique += "1"; break;
		case R.id.buttonDeux: _MdpNumerique += "2"; break;
		case R.id.buttonTrois: _MdpNumerique += "3"; break;
		case R.id.buttonQuatre: _MdpNumerique += "4"; break;
		case R.id.buttonCinq: _MdpNumerique += "5"; break;
		case R.id.buttonSix: _MdpNumerique += "6"; break;
		case R.id.buttonSept: _MdpNumerique += "7"; break;
		case R.id.buttonHuit: _MdpNumerique += "8"; break;
		case R.id.buttonNeuf: _MdpNumerique += "9"; break;
		case R.id.buttonEfface:
		{
			if ( _MdpNumerique.length()> 0)
				_MdpNumerique = _MdpNumerique.substring(0, _MdpNumerique.length()-1);
			break;
		}
		case R.id.buttonClear:
			_MdpNumerique = "";
			break;
	}

	_tvCodeNumerique.setText(_MdpNumerique);
}

/***
 * Reception d'une nouvelle pattern
 * @param pattern
 */
@Override
public void onNewPattern(String pattern)
{
	Toast.makeText(this, pattern, Toast.LENGTH_SHORT).show();
	_MdpPattern = pattern;
}
}
