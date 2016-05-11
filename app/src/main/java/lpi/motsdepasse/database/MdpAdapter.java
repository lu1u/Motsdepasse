package lpi.motsdepasse.database;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import lpi.motsdepasse.MotDePasse;
import lpi.motsdepasse.R;


/**
 * Adapter pour afficher les profils
 */
public class MdpAdapter extends CursorAdapter
{
public MdpAdapter(Context context, Cursor cursor)
{
	super(context, cursor, 0);
}

// The newView method is used to inflate a new view and return it,
// you don't bind any data to the view at this point.
@Override
public View newView(Context context, Cursor cursor, ViewGroup parent)
{
	return LayoutInflater.from(context).inflate(R.layout.element_liste_mdp, parent, false);
}

// The bindView method is used to bind all data to a given view
// such as setting the text on a TextView.
@Override
public void bindView(View view, final Context context, Cursor cursor)
{
	MotDePasse motDePasse = new MotDePasse(cursor);
	((TextView) view.findViewById(R.id.textViewNom)).setText(motDePasse._nom);


	ImageView im = (ImageView) view.findViewById(R.id.imageViewCategorie);
	im.setBackgroundColor(motDePasse._couleur);
	im.setImageResource(motDePasse.getCategoryIconResource());
	// TODO: supprimer l'affichage des infos confidentielles!
	//((TextView) view.findViewById(R.id.textViewDescription)).setText(motDePasse._description);
	//((TextView) view.findViewById(R.id.textViewUtilisateur)).setText(motDePasse._utilisateur);
/*if (motDePasse._bitmap != null)
	{
		im = (ImageView) view.findViewById(R.id.imageViewMotDePasse);
		im.setImageBitmap(motDePasse._bitmap);
	} */
}

@Nullable
public MotDePasse get(int position)
{
	Cursor cursor = getCursor();

	if (cursor.moveToPosition(position))
		return new MotDePasse(cursor);
	return null;
}

}