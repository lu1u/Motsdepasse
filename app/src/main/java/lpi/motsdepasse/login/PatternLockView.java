package lpi.motsdepasse.login;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import lpi.motsdepasse.R;

/**
 * TODO: document your custom view class.
 */
public class PatternLockView extends View
{


public interface patternDoneListener
{
	public void onNewPattern( String pattern );
}


static final private int NB_CASES_LARGEUR = 4;
static final private int NB_CASES_HAUTEUR = 4;
Case[][] _cases;
ArrayList<Case> _pattern = new ArrayList<>();
/// Attributs parametrables par xml
private float _ratioDiametreCase;
private int _couleurFondFocus;
private int _couleurFondCercle;
private int _couleurCercleEteint;
private int _couleurCercleActif;
private Paint _pathPaint;
private int _paddingLeft ;
private int _paddingTop ;
private int _paddingRight;
private int _paddingBottom;
private float _largeurCase;
private float _hauteurCase ;
private float _diametre ;
private Case _caseSelectionnee;
private float _distanceMax;
private boolean _confirmer ;
private Vibrator _myVib;
private patternDoneListener _listener;

public PatternLockView(Context context)
{
	super(context);
	init(null, 0);
}

public PatternLockView(Context context, AttributeSet attrs)
{
	super(context, attrs);
	init(attrs, 0);
}

public void setPatternListener( patternDoneListener listener )
{
	_listener = listener;
}

public PatternLockView(Context context, AttributeSet attrs, int defStyle)
{
	super(context, attrs, defStyle);
	init(attrs, defStyle);
}

/**
 * Adds the children of this View relevant for accessibility to the given list
 * as output. Since some Views are not important for accessibility the added
 * child views are not necessarily direct children of this view, rather they are
 * the first level of descendants important for accessibility.
 *
 * @param outChildren The output list that will receive children for accessibility.
 */
@Override
public void addChildrenForAccessibility(ArrayList<View> outChildren)
{
	super.addChildrenForAccessibility(outChildren);
}

private void init(AttributeSet attrs, int defStyle)
{
	// Load attributes
	final TypedArray a = getContext().obtainStyledAttributes(
			attrs, R.styleable.PatternLockView, defStyle, 0);

	_couleurFondCercle = a.getColor(R.styleable.PatternLockView_fondCercle, ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
	_couleurFondFocus = a.getColor(R.styleable.PatternLockView_fondFocus, ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));
	_couleurCercleEteint = a.getColor(R.styleable.PatternLockView_cercleEteint, ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
	_couleurCercleActif = a.getColor(R.styleable.PatternLockView_cercleActif, ContextCompat.getColor(getContext(), R.color.colorAccent));
	_ratioDiametreCase = a.getFloat(R.styleable.PatternLockView_ratioDiametreCercle, 0.3f);
	_confirmer = a.getBoolean(R.styleable.PatternLockView_confirmer, false);
	_paddingLeft = getPaddingLeft();
	_paddingTop = getPaddingTop();
	_paddingRight = getPaddingRight();
	_paddingBottom = getPaddingBottom();

	int contentWidth = getWidth() - _paddingLeft - _paddingRight;
	int contentHeight = getHeight() - _paddingTop - _paddingBottom;

	float largeurCase = contentWidth / (float) NB_CASES_LARGEUR;
	float hauteurCase = contentHeight / (float) NB_CASES_HAUTEUR;
	float diametre = Math.min(largeurCase, hauteurCase) * _ratioDiametreCase;

	_cases = new Case[NB_CASES_LARGEUR][NB_CASES_HAUTEUR];
	Random r = new Random();
	for (int x = 0; x < NB_CASES_LARGEUR; x++)
		for (int y = 0; y < NB_CASES_HAUTEUR; y++)
		{
			_cases[x][y] = new Case();
			_cases[x][y].caractere = Character.toChars( 65 + x + (y*NB_CASES_LARGEUR))[0];
			_cases[x][y].actif = false;//r.nextBoolean();
			_cases[x][y].focus = false;//r.nextBoolean();
			_cases[x][y].x = _paddingLeft + (largeurCase * x) + diametre * 1.5f;
			_cases[x][y].y = _paddingTop + (hauteurCase * y) + diametre * 1.5f;
			_cases[x][y].diametre = diametre;
		}

	a.recycle();

	_pathPaint = new Paint();
	_pathPaint.setAntiAlias(true);
	_pathPaint.setStrokeWidth(_diametre*0.8f);
	_pathPaint.setColor(Color.argb(200,Color.red(_couleurCercleActif),Color.green(_couleurCercleActif),Color.blue(_couleurCercleActif)));
	_pathPaint.setStyle(Paint.Style.STROKE);
	_pathPaint.setStrokeCap(Paint.Cap.ROUND);

	_myVib = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
}

@Override
protected void onDraw(Canvas canvas)
{
	super.onDraw(canvas);

	/// Dessine les cases
	Paint paint = new Paint();
	paint.setAntiAlias(true);
	for (int x = 0; x < NB_CASES_LARGEUR; x++)
		for (int y = 0; y < NB_CASES_HAUTEUR; y++)
			_cases[x][y].display(canvas, paint);

	// Dessine le pattern
	if ( _pattern.size()>1)
	{
		Case premiere = _pattern.get(0);
		for ( int i = 1; i < _pattern.size(); i++)
		{
			Case deuxieme = _pattern.get(i);
			canvas.drawLine(premiere.x, premiere.y, deuxieme.x, deuxieme.y, _pathPaint);
			premiere = deuxieme;
		}
	}
}



@Override
protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld)
{
	super.onSizeChanged(xNew, yNew, xOld, yOld);

	int paddingLeft = getPaddingLeft();
	int paddingTop = getPaddingTop();
	int paddingRight = getPaddingRight();
	int paddingBottom = getPaddingBottom();

	int contentWidth = getWidth() - paddingLeft - paddingRight;
	int contentHeight = getHeight() - paddingTop - paddingBottom;

	_largeurCase = contentWidth / (float) NB_CASES_LARGEUR;
	_hauteurCase = contentHeight / (float) NB_CASES_HAUTEUR;
	_diametre = Math.min(_largeurCase, _hauteurCase) * _ratioDiametreCase;

	for (int x = 0; x < NB_CASES_LARGEUR; x++)
		for (int y = 0; y < NB_CASES_HAUTEUR; y++)
		{
			_cases[x][y].x = paddingLeft + (_largeurCase * x) + _diametre * 1.5f;
			_cases[x][y].y = paddingTop + (_hauteurCase * y) + _diametre * 1.5f;
			_cases[x][y].diametre = _diametre;
		}

	_distanceMax = _cases[0][0].diametre * 1.1f;
	_pathPaint.setStrokeWidth(_diametre*0.8f);

}




@Override
public boolean onTouchEvent(MotionEvent event)
{
	switch (event.getAction())
	{
		case MotionEvent.ACTION_DOWN:
			return MotionDown(event);

		case MotionEvent.ACTION_MOVE:
			return MotionMove(event);

		case MotionEvent.ACTION_UP:
			return MotionUp(event);

		case MotionEvent.ACTION_CANCEL:
			return MotionCancel(event);
	}
	return super.onHoverEvent(event);
}


private boolean MotionDown(MotionEvent event)
{
	for( Case cas : _pattern)
	{
		cas.actif = false;
		cas.focus = false;
	}

	_pattern.clear();
	invalidate();
	return true;
}


private boolean MotionMove(MotionEvent event)
{
	float eventX = event.getX();
	float eventY = event.getY();

	// Retrouver la case la plus proche
	int xCase = (int) Math.round((eventX - (_paddingLeft + _diametre * 1.5f)) / _largeurCase);
	int yCase = (int) Math.round((eventY - (_paddingTop + _diametre * 1.5f)) / _hauteurCase);
	if ((xCase < 0) || (xCase >= NB_CASES_LARGEUR) || (yCase < 0) || (yCase >= NB_CASES_HAUTEUR))
		return false ;

	if ( distance( eventX, eventY,_cases[xCase][yCase] ) > _distanceMax )
		return false;

	select(_cases[xCase][yCase]);
	if (_pattern.size() == 0)
		addCase(_cases[xCase][yCase]);
	else if (_pattern.get(_pattern.size() - 1) != _cases[xCase][yCase])
		addCase(_cases[xCase][yCase]);

	invalidate();
	return true;
}

private double distance(float eventX, float eventY, Case aCase)
{
	return Math.sqrt( (eventX-aCase.x)*(eventX-aCase.x) + (eventY-aCase.y)*(eventY-aCase.y));
}

private void select( Case cse )
{
	if (_caseSelectionnee!=cse)
	{
		if (_caseSelectionnee != null)
			_caseSelectionnee.focus = false;

		_myVib.vibrate(40);
		cse.focus = true;
		_caseSelectionnee = cse;
	}
}

private void addCase( Case cse )
{
	cse.actif = true;
	if ( _pattern.indexOf(cse) == -1)
		_pattern.add(cse);
}


private boolean MotionUp(MotionEvent event)
{
	// Controler la validite du pattern
	if ( patternOk())
	{
		 if ( _listener !=null)
		 {
			 String pattern =   getPatternString();
			 if ( pattern.length() > 0)
			    _listener.onNewPattern(pattern);
		 }
	}
	return true;
}


/***
 * Retrouve une chaine symbolisant la pattern en cours
 * @return
 */
private String getPatternString()
{
	StringBuilder res = new StringBuilder();
	for( Case aCase : _pattern)
		res.append(aCase.caractere) ;
	return res.toString() ;
}

/***
 * Initialise le controle avec une nouvelle Pattern representee par une chaine de caracteres
 * @param pattern
 */
public void setPattern(@NonNull String pattern)
{
	for( Case cas : _pattern)
	{
		cas.actif = false;
		cas.focus = false;
	}
	_pattern.clear();
	for ( int i = 0; i < pattern.length();i++)
	{
		Case cse =   findCase(pattern.charAt(i));
		if ( cse != null)
		{
			cse.actif = true;
			_pattern.add(cse);
		}
	}
	invalidate();
}

/***
 * Retrouve la case representee par un caractere
 * @param c
 * @return
 */
private @Nullable  Case findCase(char c)
{
	for (int x = 0; x < NB_CASES_LARGEUR;x++)
		for (int y = 0; y < NB_CASES_HAUTEUR;y++)
			if ( c == _cases[x][y].caractere )
				return _cases[x][y];
	return null;
}

private boolean patternOk()
{
	return true;
}


private boolean MotionCancel(MotionEvent event)
{
	return true;
}

/***
 * Represente une des cases du schema
 */
private class Case
{
	boolean actif;
	boolean focus;
	float x, y, diametre;
	char caractere;

	public void display(Canvas canvas, Paint paint)
	{
		// Fond du cercle
		if ( focus )
		{
			paint.setColor(_couleurFondFocus);
			canvas.drawCircle(x, y, diametre*1.5f, paint);
		}
		else
		{
			paint.setColor(_couleurFondCercle);
			canvas.drawCircle(x, y, diametre, paint);
		}
		// Cercle
		paint.setColor(actif ? _couleurCercleActif : _couleurCercleEteint);
		canvas.drawCircle(x, y, diametre * 0.5f, paint);
	}
}

public int getfondCercle() {
	return _couleurFondCercle;
}

public void setfondCercle(int fondCercle) {
	this._couleurFondCercle = fondCercle;
	invalidate();
	}
}
