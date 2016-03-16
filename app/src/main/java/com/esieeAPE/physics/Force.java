/**
 * ESIEE OpenSource Project : OpenGL
 *
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.physics;

import com.esieeAPE.lib.Predicat;
import com.esieeAPE.lib.myVector3D;
import com.esieeAPE.objects.Entity;

/**
 * Force like gravity
 * Physics force : One direction, non specific application point (use ForcePoint if you want)
 * @author Jonathan
 *
 */
public class Force {
	
	// Force vector : contains directions and intensity
	protected myVector3D vector;
	
	// Apply to object with mass
	public boolean dotMass = false;
	
	public Predicat isApplyForce = new Predicat() {
		@Override
		public myVector3D isTrue(Entity entity) {
			return new myVector3D(1.0f,1.0f,1.0f);
		}		
	};
	
	public myVector3D getForceV(Entity entity) {
		return vector.mult(isApplyForce.isTrue(entity));
	}
	
	public Force(float intensity, boolean dotMass) {
		super();
		this.vector = new myVector3D(intensity, intensity, intensity);
		this.dotMass = dotMass;
	}
	
	public Force(float intensity, boolean dotMass, Predicat isApplyForce) {
		super();
		this.vector = new myVector3D(intensity, intensity, intensity);
		this.dotMass = dotMass;
		this.isApplyForce = isApplyForce;
	}
	
	public Force(float x, float y, float z, float intensity, boolean dotMass) {
		super();
		this.vector = new myVector3D(x*intensity, y*intensity, z*intensity);
		this.dotMass = dotMass;
	}
	
	public Force(float x, float y, float z, float intensity, boolean dotMass, Predicat isApplyForce) {
		super();
		this.vector = new myVector3D(x*intensity, y*intensity, z*intensity);
		this.dotMass = dotMass;
		this.isApplyForce = isApplyForce;
	}
}
