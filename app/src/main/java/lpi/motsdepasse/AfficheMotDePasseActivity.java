package lpi.motsdepasse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import lpi.motsdepasse.database.LoginManager;
import lpi.motsdepasse.database.MdpDatabase;
import lpi.motsdepasse.login.LoginActivity;

public class AfficheMotDePasseActivity extends AppCompatActivity
{
public static final String EXTRA_MDP_ID = "lpi.mdp.id";


@Override
protected void onCreate(Bundle savedInstanceState)
{
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_affiche_mot_de_passe);

	LoginManager lm = LoginManager.getInstance(this);
	if (!lm.IsLoginOK())
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent, MainActivity.RESULT_LOGIN);
		return;
	}

	int id = getIntent().getExtras().getInt(EXTRA_MDP_ID);
	MdpDatabase database = MdpDatabase.getInstance(this);
	MotDePasse motDePasse = database.getMotDePasse(id);

	if ( motDePasse !=null)
	{
		ImageView im = (ImageView) findViewById(R.id.imageViewIcone);
		im.setBackgroundColor(motDePasse._couleur);
		im.setImageResource(motDePasse.getCategoryIconResource());

		((TextView) findViewById(R.id.textViewNom)).setText(motDePasse._nom);
		((TextView) findViewById(R.id.textViewDescription)).setText(motDePasse._description);
		((TextView) findViewById(R.id.textViewUtilisateur)).setText(motDePasse._utilisateur);
		if ( motDePasse._bitmap!=null)
		{
			im = (ImageView) findViewById(R.id.imageViewMotDePasse);
			im.setImageBitmap(motDePasse._bitmap);
		}
	}
}

/**
 * Dispatch onPause() to fragments.
 */
@Override
protected void onPause()
{
	super.onPause();
	finish();
}
}
