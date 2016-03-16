/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Static Methods used to apply Fonts
 *
 * @author Jonathan
 */
public class Font {
    public static void applyFont(Activity activity, TextView tv, String police) {
        if (activity == null)
            return;
        Typeface font = Typeface.createFromAsset(activity.getAssets(), police);
        tv.setTypeface(font);
    }
}
