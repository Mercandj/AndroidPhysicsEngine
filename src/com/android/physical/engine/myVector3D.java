package com.android.physical.engine;

public class myVector3D {
	public float dX;
	public float dY;
	public float dZ;
	
	public myVector3D()
	{
	}
	
	public myVector3D(float dx, float dy, float dz)
	{
	    dX = dx;
	    dY = dy;
	    dZ = dz;
	}

	float dot(myVector3D v1)
	{
	    return (v1.dX*dX + v1.dY*dY + v1.dZ*dZ);
	}

	myVector3D plus(myVector3D v1)
	{
		return new myVector3D(dX+v1.dX, dY+v1.dY, dZ+v1.dZ);
	}

	myVector3D minus()
	{
		return new myVector3D(-dX, -dY, -dZ);
	}

	myVector3D mult(float s)
	{
		return new myVector3D(dX*s, dY*s, dZ*s);
	}

	void cross(myVector3D v1, myVector3D v2)
	{
		dX = v1.dY * v2.dZ - v1.dZ * v2.dY;
		dY = v1.dZ * v2.dX - v1.dX * v2.dZ;
		dZ = v1.dX * v2.dY - v1.dY * v2.dX;
	}

	myVector3D cross(myVector3D v1)
	{
		myVector3D result = new myVector3D();
		result.cross(this, v1);
		return result;
	}

	float length( )
	{
	    return (float) Math.sqrt( dX*dX + dY*dY + dZ*dZ ); 
	}

	void normalize( )
	{
	    float l = length();
	    dX = dX/l;
	    dY = dY/l;
	    dZ = dZ/l;
	}

	void rotate(myVector3D lp, float theta)
	{
		//rotate vector this around the line defined by lp through the origin by theta degrees.
		float cos_theta = (float) Math.cos(theta);
		float dot = this.dot(lp);
		myVector3D cross = this.cross(lp);
		dX *= cos_theta; dY *= cos_theta; dZ *= cos_theta; 
		dX += lp.dX * dot * (1.0-cos_theta); dY += lp.dY * dot * (1.0-cos_theta); dZ += lp.dZ * dot * (1.0-cos_theta);
		dX -= cross.dX*Math.sin(theta); 
		dY -= cross.dY*Math.sin(theta); 
		dZ -= cross.dZ*Math.sin(theta);
	}
}