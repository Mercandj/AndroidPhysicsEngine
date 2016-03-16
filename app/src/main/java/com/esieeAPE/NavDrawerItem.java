/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE;

import android.widget.CompoundButton.OnCheckedChangeListener;

import com.esieeAPE.lib.IFunction;

/**
 * Sliding Menu stuff
 *
 * @author Jonathan
 */
public class NavDrawerItem {
    public String title;
    public int icon;
    public int SLIDING_MENU_TAB;
    public boolean isImage;
    public IFunction listenerClick = null;

    public boolean initChecked = false;
    public OnCheckedChangeListener onCheckedChangeListener = null;

    public NavDrawerItem(String title, int icon, int SLIDING_MENU_TAB) {
        super();
        this.title = title;
        this.icon = icon;
        this.SLIDING_MENU_TAB = SLIDING_MENU_TAB;
        this.isImage = true;
    }

    public NavDrawerItem(String title, int icon, IFunction listenerClick, int SLIDING_MENU_TAB) {
        super();
        this.title = title;
        this.icon = icon;
        this.SLIDING_MENU_TAB = SLIDING_MENU_TAB;
        this.isImage = true;
        this.listenerClick = listenerClick;
    }

    public NavDrawerItem(String title, boolean initChecked, OnCheckedChangeListener onCheckedChangeListener, int SLIDING_MENU_TAB) {
        super();
        this.title = title;
        this.SLIDING_MENU_TAB = SLIDING_MENU_TAB;
        this.initChecked = initChecked;
        this.onCheckedChangeListener = onCheckedChangeListener;
        this.isImage = false;
    }

    public NavDrawerItem(String title, int SLIDING_MENU_TAB) {
        super();
        this.title = title;
        this.SLIDING_MENU_TAB = SLIDING_MENU_TAB;
        this.isImage = false;
    }

    public NavDrawerItem(String title, IFunction listenerClick, int SLIDING_MENU_TAB) {
        super();
        this.title = title;
        this.SLIDING_MENU_TAB = SLIDING_MENU_TAB;
        this.isImage = false;
        this.listenerClick = listenerClick;
    }

    @Override
    public boolean equals(Object o) {
        return this.title.equals(((NavDrawerItem) o).title) && this.icon == ((NavDrawerItem) o).icon;
    }
}
