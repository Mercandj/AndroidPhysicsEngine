/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE;

import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Static Methods used to apply Fonts
 *
 * @author Jonathan
 */
public class Font {
    public static void applyFont(TextView tv, String police) {
        final Typeface font = Typeface.createFromAsset(tv.getContext().getAssets(), police);
        tv.setTypeface(font);
    }
}
