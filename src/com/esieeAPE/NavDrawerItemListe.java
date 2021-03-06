/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE;

import java.util.ArrayList;

/**
 * Sliding Menu stuff
 * @author Jonathan
 *
 */
public class NavDrawerItemListe {

    private ArrayList<NavDrawerItem> liste = new ArrayList<NavDrawerItem>();
	
    public void add(NavDrawerItem o) {
    	if(o==null) return;
    	liste.add(o);
    }
    
    public NavDrawerItem get(int o) {
    	return liste.get(o);
    }
	
    public int size() {
    	return liste.size();
    }
    
    public ArrayList<NavDrawerItem> getListe() {
    	return liste;
    }
    
    public int getIndice(NavDrawerItem o) {
    	for(int i = 0; i<liste.size(); i++)
    		if(get(i).equals(o))
    			return i;
    	return -1;    	
    }
}
