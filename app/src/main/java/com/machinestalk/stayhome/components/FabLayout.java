package com.machinestalk.stayhome.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.machinestalk.stayhome.ConnectedCar;
import com.machinestalk.stayhome.R;
import com.machinestalk.stayhome.constants.KeyConstants;
import com.machinestalk.stayhome.utils.Util;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created on 3/30/2017.
 */

public class FabLayout extends RelativeLayout {

    private View    view;
    private Context context;

    FloatingActionMenu                 fabMenu;
    FabLayout.onFabButtonClickListener listener;
    private ArrayList< FloatingActionButton > actionButtons;

    public FabLayout( Context context ) {
        super( context );
        init( context, null );
    }

    public FabLayout( Context context, AttributeSet attrs ) {
        super( context, attrs );
        init( context, attrs );
    }

    public FabLayout( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        init( context, attrs );
    }

    @RequiresApi( api = Build.VERSION_CODES.LOLLIPOP )
    public FabLayout( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ) {
        super( context, attrs, defStyleAttr, defStyleRes );
        init( context, attrs );
    }

    @RequiresApi( api = Build.VERSION_CODES.JELLY_BEAN_MR1 )
    private void init( Context context, AttributeSet attrs ) {
        this.context = context;
        view = inflateView( context );
        setupAttributes( attrs );
        setupView( view );
    }

    protected View inflateView( Context context ) {
        LayoutInflater inflater = LayoutInflater.from( context );
        view = inflater.inflate( R.layout.layout_fab, this, true );
        return view;
    }

    protected void setupAttributes( AttributeSet attrs ) {
        //TypedArray a = getContext().getTheme().obtainStyledAttributes( attrs, R.styleable.StyledInput, 0, 0 );
    }

    @RequiresApi( api = Build.VERSION_CODES.JELLY_BEAN_MR1 )
    protected void setupView( View view ) {
        fabMenu = ( FloatingActionMenu ) view.findViewById( R.id.fabMenu );
        fabMenu.setIconToggleAnimatorSet( getCustomIconAnimation() );
    }

    private void setActionButtonListener() {
        for ( int i = 0 ; i < actionButtons.size() ; i++ ) {
            final FloatingActionButton button = actionButtons.get( i );
            fabMenu.addMenuButton( button );
            button.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    if ( listener != null ) {
                        if ( button.isSelected() ) {
                            button.setSelected( false );
                        } else {
                            button.setSelected( true );
                        }
                        listener.onFabButtonClicked( button, String.valueOf( button.getTag( R.string.fabTagKey ) ), button.isSelected() );
                    }
                }
            } );
        }
    }

    public void setOnFabButtonClickListener( FabLayout.onFabButtonClickListener listener ) {
        this.listener = listener;
    }

    public void setItems( ArrayList< FloatingActionButton > floatingActionButtons ) {
        this.actionButtons = floatingActionButtons;
        if ( !actionButtons.isEmpty() ) {
            setActionButtonListener();
        }
    }

    public void initWithDefault() {
        ArrayList< FloatingActionButton > floatingActionButtons = new ArrayList<>();
        floatingActionButtons.add( Util.getFloatingActionButton( context, KeyConstants.KEY_BUTTON_TRAFFIC, R.string.LMT_Gen_lbl_fab_traffic, R.drawable.selector_toggle_traffic ) );
        floatingActionButtons.add( Util.getFloatingActionButton( context, KeyConstants.KEY_BUTTON_SATELLITE, R.string.LMT_Gen_lbl_fab_satellite, R.drawable.selector_toggle_satellite ) );
        setItems( floatingActionButtons );
    }

    public AnimatorSet getCustomIconAnimation() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat( fabMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f );
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat( fabMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f );
        ObjectAnimator scaleInX  = ObjectAnimator.ofFloat( fabMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f );
        ObjectAnimator scaleInY  = ObjectAnimator.ofFloat( fabMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f );

        scaleOutX.setDuration( 50 );
        scaleOutY.setDuration( 50 );
        scaleInX.setDuration( 150 );
        scaleInY.setDuration( 150 );

        scaleInX.addListener( new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart( Animator animation ) {
                fabMenu.getMenuIconView().setImageResource(R.drawable.ic_layer);
            }
        } );

        set.play( scaleOutX ).with( scaleOutY );
        set.play( scaleInX ).with( scaleInY ).after( scaleOutX );
        set.setInterpolator( new OvershootInterpolator( 2 ) );

        return set;
    }

    public void setPreSelectedState() {
        for ( Map.Entry< String, Boolean > entry : ConnectedCar.getInstance().getFabStates().entrySet() ) {
            String               key             = entry.getKey();
            boolean              selected        = entry.getValue();
            FloatingActionButton refrencedButton = getButtonByTag( key );
            if ( refrencedButton != null ) {
                refrencedButton.setSelected( selected );
                listener.onFabButtonClicked( refrencedButton, key, selected );
            }
        }
    }

    private FloatingActionButton getButtonByTag( String tag ) {

        if ( actionButtons == null) {
            return null;
        }
        for ( FloatingActionButton button : actionButtons ) {
            if ( button.getTag( R.string.fabTagKey ).equals( tag ) ) {
                return button;
            }
        }
        return null;
    }

    public void close() {
        if ( this.fabMenu == null ) {
            return;
        }

        fabMenu.close( true );
    }

    public void hide() {
        if ( view != null ) {
            this.setVisibility( GONE );
        }
    }

    public interface onFabButtonClickListener {
        void onFabButtonClicked( FloatingActionButton buttonId, String tag, boolean selected );
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public Context getCTX() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
