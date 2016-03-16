/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.implementation;

import android.content.Context;

import com.esieeAPE.GLFragment;
import com.esieeAPE.R;
import com.esieeAPE.lib.IndicesVertices;
import com.esieeAPE.lib.lib;

/**
 * Define the non trivial objects you want (maybe) use (only World.class  apply to the scene the object you use)
 * @author Jonathan
 *
 */
public enum ENUM_Obj {

	CUBE		(R.raw.obj_cube),
	SPHERE		(R.raw.obj_sphere),
	APPLE		(R.raw.obj_apple),
	MONKEY		(R.raw.obj_monkey),
	BOTTLE		(R.raw.obj_bottle),
	HOUSE		(R.raw.obj_house),
	SHUTTLE		(R.raw.obj_shuttle),
	LAMP		(R.raw.obj_lamp),
	CAR_CAMARO	(R.raw.obj_car_camaro),
	CAR_AUDI	(R.raw.obj_car_audi),
	;
	
	private int id;
	private IndicesVertices object;
	
	private ENUM_Obj(int id) {
		this.id = id;
	}
	
	public IndicesVertices getIndicesVertices(Context context) {
		if(object==null) {
			object = lib.readMeshLocalNomalizedOpti(context, id);
			GLFragment.progressBar.setProgress(GLFragment.progressBar.getProgress()+1);
		}
		return new IndicesVertices(object);
	}
}
