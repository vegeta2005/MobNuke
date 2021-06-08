package com.yujibolt90.mobnuke;
import org.bukkit.util.Vector;

public class Fallout{
	
	double hDecay;
	double vDecay;
	double x1;
	double x2;
	double y1;
	double y2;
	double z1;
	double z2;
	
	public Fallout(double x, double y, double z, double h, double v) {
		this.x1 = x;
		this.x2 = x;
		
		this.y1 = y;
		this.y2 = y;
		
		this.z1 = z;
		this.z2 = z;
		
		this.hDecay = h;
		this.vDecay = v;
	}
	
	public void increaseFallout() {
		this.x1 += hDecay;
		this.x2 -= hDecay;
		
		this.y1 += vDecay;
		this.y2 -= vDecay;
		
		this.z1 += hDecay;
		this.z2 -= hDecay;
	}
	
	public Vector getFirstVector() {
		Vector v = new Vector(x1,y1,z1);
		return v;
	}
	
	public Vector getSecondVector() {
		Vector v = new Vector(x2,y2,z2);
		return v;
	}
}