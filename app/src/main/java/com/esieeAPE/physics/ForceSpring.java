/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.physics;

import com.esieeAPE.lib.myVector3D;
import com.esieeAPE.objects.Entity;

/**
 * Force Spring : Physics Force 
 * @author Jonathan
 * 
 */
public class ForceSpring extends Force {
	
	myVector3D position; /* OR */ Entity entity;

	public ForceSpring(myVector3D position, float intensity, boolean dotMass) {
		super(intensity, dotMass);
		this.position = new myVector3D(position);
	}
	
	public ForceSpring(Entity entity, float intensity, boolean dotMass) {
		super(intensity, dotMass);
		this.entity = entity;
	}
	
	@Override
	public myVector3D getForceV(Entity pentity) {
		myVector3D res = new myVector3D(0,0,0);		
		if(entity!=null) {
			if(entity.position.dY-pentity.position.dY>0)
				res = new myVector3D(0, (float)Math.sqrt(Math.abs( (entity.position.dY+entity.edgeVerticeMin.dY)-(pentity.position.dY+pentity.edgeVerticeMax.dY) )), 0).mult(0, 0.000002f, 0);
			else
				res = new myVector3D(0, -((float)Math.sqrt(Math.abs( (pentity.position.dY+pentity.edgeVerticeMin.dY)-(entity.position.dY+entity.edgeVerticeMax.dY) ))), 0).mult(0, 0.000002f, 0);
		}			
		else if(position!=null) {		
			if(position.dY-pentity.position.dY>0)
				res = new myVector3D(0, ((float)Math.sqrt(Math.sqrt(Math.abs(position.dY-pentity.position.dY)))), 0).mult(0, 0.000001f, 0);
			else
				res = new myVector3D(0, -((float)Math.sqrt(Math.sqrt(Math.abs(position.dY-pentity.position.dY)))), 0).mult(0, 0.000001f, 0);
		}
		return res;
	}
}
