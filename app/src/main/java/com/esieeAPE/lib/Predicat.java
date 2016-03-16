package com.esieeAPE.lib;

import java.util.ArrayList;
import java.util.List;

import com.esieeAPE.objects.Entity;

public abstract class Predicat {
	public List<Integer> list_int = new ArrayList<Integer>();
	public Entity entity = null;
	
	public abstract myVector3D isTrue(Entity entity);
}
