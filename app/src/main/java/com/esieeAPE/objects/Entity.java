/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.objects;

import com.esieeAPE.lib.Vector3D;
import com.esieeAPE.lib.WayPosition;
import com.esieeAPE.physics.Force;
import com.esieeAPE.physics.PhysicsObjStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Define the object's attributes
 *
 * @author Jonathan
 */
public abstract class Entity {

    public int mId;                    // identify entity in an EntityGroup

    public PhysicsObjStats mPhysic = new PhysicsObjStats();

    public Vector3D mEdgePositionMin;
    public Vector3D mEdgeVectorPositionMax;

    public final Vector3D mPosition = new Vector3D(0, 0, 0);
    public final Vector3D mVelocity = new Vector3D(0, 0, 0);
    protected final Vector3D mAcceleration = new Vector3D(0, 0, 0);

    // Move
    public WayPosition repetedWayPosition;

    // Physics
    public boolean contactEnable = true;
    protected final Vector3D mSumForces = new Vector3D(0, 0, 0);
    protected final List<Force> mForces = new ArrayList<>();
    protected List<Entity> entitiesContact;

    public abstract void teleport(float x, float y, float z);

    public abstract void translate(float x, float y, float z);

    public abstract void rotate(float a, float x, float y, float z);

    public abstract void scale(float rate);

    public abstract Entity isInside(Entity object);

    public abstract void draw(float[] _mpMatrix, float[] _mvMatrix);

    public abstract void translateRepetedWayPosition();

    public abstract void computeForces(EntityGroup contacts);

    public abstract void applyForces(EntityGroup contacts);

    public abstract void addForce(Force force);
}
