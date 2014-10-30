
package me.arnaud.procterrain;

public class VoxelColor {
    public float r;
    public float g;
    public float b;
    public float a;
    
    public VoxelColor (float nr, float ng, float nb, float na) {
        
    }
    
    public VoxelColor(int in) {
        r = in==0 ? 1 : 0;
        g = in==1 ? 1 : 0;
        b = in==2 ? 1 : 0;
        a = in==3 ? 1 : 0;
        /*r = 1;
        g = 1;
        b = 1;
        a = 1;*/
    }
    
    public void addLocal (VoxelColor in) {
        r += in.r;
        g += in.g;
        b += in.b;
        a += in.a;
    }
    
    public VoxelColor interpolate (VoxelColor in, float val) {
        /*float nr = r*(1-val) + val*in.r;
        float ng = (g*(1-val)) + (val * in.g);
        float nb = b*(1-val) + val*in.b;
        float na = a*(1-val) + val*in.a;
        return new VoxelColor(nr,ng,nb,na);
        //*/
        r = r*(1-val) + val*in.r;
        g = (g*(1-val)) + (val * in.g);
        b = b*(1-val) + val*in.b;
        a = a*(1-val) + val*in.a;
        return this;
        //*/
    }
    
    public VoxelColor add (VoxelColor in) {
        return new VoxelColor(r+in.r,g+in.g,b+in.b,a+in.a);
    }
    
    public VoxelColor mult (VoxelColor in) {
        return new VoxelColor(r*in.r,g*in.g,b*in.b,a*in.a);
    }
    public VoxelColor mult (float in) {
        return new VoxelColor(r*in,g*in,b*in,a*in);
    }
    
    public float[] GetFloats() {
        return new float[] {r,g,b,a};
    }
    
}
