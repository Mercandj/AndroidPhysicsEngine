/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.physics;

import com.esieeAPE.lib.Predicate;
import com.esieeAPE.lib.Vector3D;
import com.esieeAPE.objects.Entity;

/**
 * Force like gravity
 * Physics force : One direction, non specific application point (use ForcePoint if you want)
 *
 * @author Jonathan
 */
public class Force {

    // Apply to object with mass
    public boolean dotMass = false;
    public Predicate isApplyForce = new Predicate() {
        @Override
        public Vector3D isTrue(Entity entity) {
            return new Vector3D(1.0f, 1.0f, 1.0f);
        }
    };
    // Force vector : contains directions and intensity
    protected Vector3D vector;

    public Force(float intensity, boolean dotMass) {
        super();
        this.vector = new Vector3D(intensity, intensity, intensity);
        this.dotMass = dotMass;
    }

    public Force(float intensity, boolean dotMass, Predicate isApplyForce) {
        super();
        this.vector = new Vector3D(intensity, intensity, intensity);
        this.dotMass = dotMass;
        this.isApplyForce = isApplyForce;
    }

    public Force(float x, float y, float z, float intensity, boolean dotMass) {
        super();
        this.vector = new Vector3D(x * intensity, y * intensity, z * intensity);
        this.dotMass = dotMass;
    }

    public Force(float x, float y, float z, float intensity, boolean dotMass, Predicate isApplyForce) {
        super();
        this.vector = new Vector3D(x * intensity, y * intensity, z * intensity);
        this.dotMass = dotMass;
        this.isApplyForce = isApplyForce;
    }

    public Vector3D getForceV(Entity entity) {
        return vector.mult(isApplyForce.isTrue(entity));
    }
}
