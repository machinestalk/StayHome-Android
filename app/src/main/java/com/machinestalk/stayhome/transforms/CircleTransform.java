package com.machinestalk.stayhome.transforms;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Created on 8/24/16.
 */

public class CircleTransform implements Transformation {


    private static final String KEY = "circleImageTransformation";

    private int strokeWidth;
    private int strokeColor;

    public CircleTransform() {
        this.strokeWidth = 0;
        this.strokeColor = Color.TRANSPARENT;
    }

    public CircleTransform( int strokeWidth, int strokeColor ) {
        this.strokeWidth = strokeWidth;
        this.strokeColor = strokeColor;
    }

    @Override
    public Bitmap transform( Bitmap source ) {

        int minEdge = Math.min( source.getWidth(), source.getHeight() );
        int dx = ( source.getWidth() - minEdge ) / 2;
        int dy = ( source.getHeight() - minEdge ) / 2;

        // Init shader
        Shader shader = new BitmapShader( source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP );
        Matrix matrix = new Matrix();
        matrix.setTranslate( -dx, -dy );   // Move the target area to center of the source bitmap
        shader.setLocalMatrix( matrix );

        // Init paint
        Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
        paint.setShader( shader );
        Paint paintBorder = new Paint( Paint.ANTI_ALIAS_FLAG );
        paintBorder.setColor( strokeColor );

        // Create and draw circle bitmap
        Bitmap output = Bitmap.createBitmap( minEdge, minEdge, source.getConfig() );
        Canvas canvas = new Canvas( output );
        canvas.drawOval( new RectF( 0, 0, minEdge, minEdge ), paintBorder );
        canvas.drawOval( new RectF( strokeWidth, strokeWidth, Float.intBitsToFloat( minEdge - strokeWidth ), Float.intBitsToFloat( minEdge - strokeWidth ) ), paint );


        source.recycle();

        return output;
    }

    @Override
    public String key() {
        return KEY;
    }
}
