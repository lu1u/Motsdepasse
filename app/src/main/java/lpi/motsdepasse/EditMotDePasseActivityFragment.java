package lpi.motsdepasse;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditMotDePasseActivityFragment extends Fragment
{

public EditMotDePasseActivityFragment()
{
}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState)
{
	return inflater.inflate(R.layout.fragment_edit_mot_de_passe, container, false);
}
}
