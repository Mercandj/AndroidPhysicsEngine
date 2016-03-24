package com.esieeAPE.lib;

import java.util.ArrayList;
import java.util.List;

public class WayPosition {

    public List<Vector3D> way = new ArrayList<Vector3D>();
    public boolean reverse = false;
    private int currentID = 0;

    public WayPosition() {

    }

    public WayPosition(List<Vector3D> way) {
        this.way = way;
    }

    public void add(Vector3D v) {
        way.add(v);
    }

    public int size() {
        return way.size();
    }

    public Vector3D get(int i) {
        return way.get(i);
    }

    public Vector3D getCurrentPosition() {
        int res = currentID;
        if (!reverse) {
            if (currentID + 1 >= size())
                currentID = 0;
            else
                currentID++;
        } else {
            if (currentID - 1 < 0)
                currentID = size() - 1;
            else
                currentID--;
        }
        return get(res);
    }

    public void initCubeWabHorizontal(float centerX, float centerY, float centerZ, float size, float foot, boolean right) {
        way = new ArrayList<Vector3D>();
        float divcote = size / 2;

        for (float i = centerX - divcote; i <= centerX + divcote; i += foot)
            add(new Vector3D(i, centerY, centerZ - divcote));

        for (float i = centerZ - divcote; i <= centerZ + divcote; i += foot)
            add(new Vector3D(centerX + divcote, centerY, i));

        for (float i = centerX + divcote; i >= centerX - divcote; i -= foot)
            add(new Vector3D(i, centerY, centerZ + divcote));

        for (float i = centerZ + divcote; i >= centerZ - divcote; i -= foot)
            add(new Vector3D(centerX - divcote, centerY, i));

        reverse = !right;
    }
}
