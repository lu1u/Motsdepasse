package lpi.motsdepasse.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import lpi.motsdepasse.R;
import lpi.motsdepasse.database.LoginManager;

public class LoginActivity extends AppCompatActivity
{
public static final int RESULT_EDIT_MDP = 1;
public static final String ACTION_LOGIN_FINISHED = "lpi.LOGIN_FINISHED";

@Override
protected void onCreate(Bundle savedInstanceState)
{
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login);
	Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);

	FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
	fab.setOnClickListener(new View.OnClickListener()
	{
		@Override
		public void onClick(View view)
		{
			onOk();
		}
	});
	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
}

private void onOk()
{
	String mdp = ((EditText)findViewById(R.id.editTextMdp)).getText().toString();

	LoginManager lm = LoginManager.getInstance(this);
	if ( lm.tryLogin(mdp))
	{

		Intent returnIntent = new Intent();
		returnIntent.setAction(ACTION_LOGIN_FINISHED);
		setResult(Activity.RESULT_OK, returnIntent);

		finish();
	}
	else
		Toast.makeText(this, "Mot de passe incorrect", Toast.LENGTH_LONG).show();
}

}
