/**
 * ESIEE OpenSource Project : OpenGL
 * <p/>
 * MARTEL Andy
 * MERCANDALLI Jonathan
 */

package com.esieeAPE.lib;

import com.esieeAPE.objects.Entity;

public interface IFunctionEntity {
    boolean condition(Entity entity);

    void execute(Entity entity);
}
