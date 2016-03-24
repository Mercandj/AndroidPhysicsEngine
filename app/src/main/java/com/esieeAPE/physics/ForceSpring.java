/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.physics;

import com.esieeAPE.lib.Vector3D;
import com.esieeAPE.objects.Entity;

/**
 * Force Spring : Physics Force
 *
 * @author Jonathan
 */
public class ForceSpring extends Force {

    Vector3D position; /* OR */
    Entity entity;

    public ForceSpring(Vector3D position, float intensity, boolean dotMass) {
        super(intensity, dotMass);
        this.position = new Vector3D(position);
    }

    public ForceSpring(Entity entity, float intensity, boolean dotMass) {
        super(intensity, dotMass);
        this.entity = entity;
    }

    @Override
    public Vector3D getForceV(Entity pentity) {
        Vector3D res = new Vector3D(0, 0, 0);
        if (entity != null) {
            if (entity.mPosition.dY - pentity.mPosition.dY > 0)
                res = new Vector3D(0, (float) Math.sqrt(Math.abs((entity.mPosition.dY + entity.mEdgePositionMin.dY) - (pentity.mPosition.dY + pentity.mEdgeVectorPositionMax.dY))), 0).mult(0, 0.000002f, 0);
            else
                res = new Vector3D(0, -((float) Math.sqrt(Math.abs((pentity.mPosition.dY + pentity.mEdgePositionMin.dY) - (entity.mPosition.dY + entity.mEdgeVectorPositionMax.dY)))), 0).mult(0, 0.000002f, 0);
        } else if (position != null) {
            if (position.dY - pentity.mPosition.dY > 0)
                res = new Vector3D(0, ((float) Math.sqrt(Math.sqrt(Math.abs(position.dY - pentity.mPosition.dY)))), 0).mult(0, 0.000001f, 0);
            else
                res = new Vector3D(0, -((float) Math.sqrt(Math.sqrt(Math.abs(position.dY - pentity.mPosition.dY)))), 0).mult(0, 0.000001f, 0);
        }
        return res;
    }
}
