package com.machinestalk.stayhome.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.widget.TextView;

import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/30/2016.
 */
public class SpannableStringUtils {

    public static SpannableString getStringCustomFont( Context context, String text, @StringRes int font ) {
        SpannableString spannableString = new SpannableString( text );
        spannableString.setSpan( new CustomTypefaceSpan( context, context.getString( font ) ),
                0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

        return spannableString;
    }

    public static SpannableString[] getStringsCustomFont( Context context, String[] texts, @StringRes int font ) {
        List< SpannableString > spannableStrings = new ArrayList<>();

        for ( String text : texts ) {
            SpannableString spannableString = new SpannableString( text );
            spannableString.setSpan( new CustomTypefaceSpan( context, context.getString( font ) ),
                    0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
            spannableStrings.add( spannableString );
        }

        SpannableString[] spanStrings = new SpannableString[ texts.length ];
        spannableStrings.toArray( spanStrings );

        return spanStrings;
    }

    public static SpannableString[] getStringsCustomFont( Context context, List texts, @StringRes int font ) {
        List< SpannableString > spannableStrings = new ArrayList<>();

        for ( Object obj : texts ) {
            SpannableString spannableString = new SpannableString( obj.toString() );
            spannableString.setSpan( new CustomTypefaceSpan( context, context.getString( font ) ),
                    0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
            spannableStrings.add( spannableString );
        }

        SpannableString[] spanStrings = new SpannableString[ texts.size() ];
        spannableStrings.toArray( spanStrings );

        return spanStrings;
    }



    public static void makeTextClickable(Context context, TextView view, final String clickableText, final ClickSpan.OnClickListener listener)
    {
        CharSequence text = view.getText();
        String string = text.toString();
        ClickSpan span = new ClickSpan(context, listener);

        int start = string.indexOf(clickableText);
        int end = start + clickableText.length();
        if (start == -1) return;

        if (text instanceof Spannable) {
            ((Spannable)text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            SpannableString s = SpannableString.valueOf(text);
            s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.setText(s);
        }

        MovementMethod m = view.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
