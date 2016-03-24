/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.objects;

import com.esieeAPE.physics.Force;
import com.esieeAPE.physics.PhysicsConst;

import java.util.ArrayList;
import java.util.List;

/**
 * Use to apply transformations : all in one
 *
 * @author Jonathan
 */
public class EntityGroup extends Entity {

    public final List<Entity> mEntities = new ArrayList<>();

    public EntityGroup() {
    }

    public void init() {
    }

    public void addEntity(Entity entity) {
        entity.mId = mEntities.size();
        mEntities.add(entity);
    }

    public Entity getEntity(int id) {
        if (id < mEntities.size()) {
            return mEntities.get(id);
        }
        return null;
    }

    @Override
    public Entity isInside(Entity object) {
        for (Entity entity : mEntities) {
            if (entity.isInside(object) != null) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public void teleport(float x, float y, float z) {
        for (Entity entity : mEntities) {
            entity.teleport(x, y, z);
        }
    }

    @Override
    public void translate(float x, float y, float z) {
        for (Entity entity : mEntities) {
            entity.translate(x, y, z);
        }
    }

    @Override
    public void rotate(float a, float x, float y, float z) {
        // TODO Auto-generated method stub
    }

    @Override
    public void draw(float[] _mpMatrix, float[] _mvMatrix) {
        for (Entity entity : mEntities) {
            entity.draw(_mpMatrix, _mvMatrix);
        }
    }

    @Override
    public void scale(float rate) {
        for (Entity entity : mEntities) {
            entity.scale(rate);
        }
    }

    @Override
    public void translateRepeatedWayPosition() {
        for (Entity entity : mEntities) {
            entity.translateRepeatedWayPosition();
        }
    }

    @Override
    public void computeForces(EntityGroup contacts) {
        for (Entity entity : this.mEntities) {
            entity.computeForces(contacts);
        }
    }

    @Override
    public void applyForces(EntityGroup contacts) {
        for (Entity entity : mEntities) {
            entity.applyForces(contacts);
        }
    }

    @Override
    public void addForce(Force force) {
        for (Entity entity : mEntities) {
            entity.mForces.add(force);
        }
    }

    public void separeObject() {
        for (Entity ent : mEntities) {
            Entity entityContact = this.isInside(ent);
            if (entityContact != null) {
                if (PhysicsConst.REAL_LOOP_TIME * (this.mVelocity.dY + PhysicsConst.REAL_LOOP_TIME * this.mAcceleration.dY / 2) > 0) {
                    translate(0,
                            Math.abs(entityContact.mPosition.dY - this.mPosition.dY),
                            0);
                } else {
                    translate(0,
                            -Math.abs(entityContact.mPosition.dY - this.mPosition.dY),
                            0);
                }
            }
        }
    }
}
