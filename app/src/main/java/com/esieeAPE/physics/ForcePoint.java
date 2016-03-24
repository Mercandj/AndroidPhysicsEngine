/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.physics;

import com.esieeAPE.lib.Predicate;
import com.esieeAPE.lib.Vector3D;

/**
 * Force "ponctuelle" : Physics Force
 *
 * @author Jonathan
 */
public class ForcePoint extends Force {

    public float intensity;
    protected Vector3D pointToApply;

    public ForcePoint(float x, float y, float z, float intensity, boolean dotMass, Predicate isApplyForce) {
        super(x, y, z, intensity, dotMass, isApplyForce);
        // TODO Auto-generated constructor stub
    }
}
