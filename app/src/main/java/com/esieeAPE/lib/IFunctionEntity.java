/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.lib;

import com.esieeAPE.objects.Entity;

public interface IFunctionEntity {
    public boolean condition(Entity entity);

    public void execute(Entity entity);
}
