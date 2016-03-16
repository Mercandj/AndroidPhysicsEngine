/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.physics;

import com.esieeAPE.lib.Predicat;
import com.esieeAPE.lib.myVector3D;

/**
 * Force "ponctuelle" : Physics Force 
 * @author Jonathan
 * 
 */
public class ForcePoint extends Force {
	
	protected myVector3D pointToApply;
	public float intensity;
	
	public ForcePoint(float x, float y, float z, float intensity, boolean dotMass, Predicat isApplyForce) {
		super(x, y, z, intensity, dotMass, isApplyForce);
		// TODO Auto-generated constructor stub
	}
}
