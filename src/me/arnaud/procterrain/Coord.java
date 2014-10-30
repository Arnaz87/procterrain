
package me.arnaud.procterrain;

import com.jme3.math.Vector3f;

public class Coord {
    public final int x;
    public final int y;
    public final int z;
    
    public static final Coord ZERO = new Coord(0,0,0);
    public static final Coord UNIT_Y = new Coord(0,1,0);
    
    public Coord (int nx, int ny, int nz) {
        this.x = nx;
        this.y = ny;
        this.z = nz;
    }
    public Coord (Vector3f in) {
        this.x = (int)Math.floor(in.x);
        this.y = (int)Math.floor(in.y);
        this.z = (int)Math.floor(in.z);
    }
    public static Coord FromIndex(int index, int res) {
        int r2 = res * res;
        int ny = index / r2;
        int nz = (index - ny*r2) / res;
        int nx = index - ny*r2 - nz*res;
        return new Coord(nx, ny, nz);
    }
    
    public Coord Add (int nx, int ny, int nz) {
        return new Coord(x+nx,y+ny,z+nz);
    }
    public Coord Add (Coord in) {
        return new Coord(x+in.x,y+in.y,z+in.z);
    }
    public Coord Substract (int nx, int ny, int nz) {
        return new Coord(x-nx,y-ny,z-nz);
    }
    public Coord Substract (Coord in) {
        return new Coord(x-in.x,y-in.y,z-in.z);
    }
    
    public Coord MultR (int r) {
        r--;
        return new Coord(x<<r,y<<r,z<<r);
    }
    public Coord Mult (int in) {
        return new Coord(x*in,y*in,z*in);
    }
    public Coord Mult (int nx, int ny, int nz) {
        return new Coord(x+nx,y+ny,z+nz);
    }
    public Coord Mult (Coord in) {
        return new Coord(x*in.x,y*in.y,z*in.z);
    }
    
    public Coord Floor (int r) {
        if (r < 2) return this;
        r--;
        return new Coord((x>>r)<<r,(y>>r)<<r,(z>>r)<<r);
    }
    public Coord ModR (int r) {
        r--;
        int nx = x - ((x>>r)<<r);
        int ny = y - ((y>>r)<<r);
        int nz = z - ((z>>r)<<r);
        return new Coord(nx,ny,nz);
    }
    public Coord ShiftRight (int t) {
        return new Coord(x>>t,y>>t,z>>t);
    }
    public Coord ShiftLeft (int t) {
        return new Coord(x<<t,y<<t,z<<t);
    }
    public Coord ShiftRightLeft (int t) {
        return new Coord((x>>t)<<t, (y>>t)<<t, (z>>t)<<t);
    }
    public Coord Negate () {
        return new Coord(-x,-y,-z);
    }
    public Coord And (int i) {
        return new Coord(x & i, y & i, z & i);
    }
    
    public int Max () {
        int xz = x>z?x:z;
        return xz>y?xz:y;
    }
    public int Min () {
        int xz = x<z?x:z;
        return xz<y?xz:y;
    }
    public int Mean () {
        return (x+y+z)/3;
    }
    public float Length () {
        return (float)Math.sqrt(x*x + y*y + z*z);
    }
    public int Sum () {
        return x+y+z;
    }
    public int ToIndex (int res) {
        return x + z*res + y*res*res;
    }
    
    public Coord FlipYZ () {
        return new Coord( 15 - x, 15 - z, y);
    }
    
    public boolean InRange(int min, int max) {
        return (Max() <= max) && (Min() >= min);
        //return !(y>max && y<min && x>max && x<min && z>max && z<min);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Coord) {
            Coord c = (Coord)o;
            return c.x == this.x & c.z == this.z & c.y == this.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.x;
        hash = 71 * hash + this.y;
        hash = 71 * hash + this.z;
        return hash;
    }

    @Override
    public String toString() {
        String c = ",";
        String val = String.valueOf(x)+c+String.valueOf(y)+c+String.valueOf(z);
        return val;
    }
    
    public static final Coord[] ARRAY01 = new Coord[] {
        new Coord(0,0,0), new Coord(0,0,1),
        new Coord(0,1,0), new Coord(0,1,1),
        new Coord(1,0,0), new Coord(1,0,1),
        new Coord(1,1,0), new Coord(1,1,1)
    };
    
}
