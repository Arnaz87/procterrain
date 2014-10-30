package me.arnaud.procterrain;

class VoxelManager {
    
    TerrainManager terrain;
    PerlinNoise noise;

    VoxelManager(TerrainManager in) {
        terrain = in;
        noise = in.noise;
    }
    
    public float GetVoxel(float x, float y, float z) {
        float val;
        float d = .0008f;
        float d2 = .1f;
        float d3 = .5f;
        val = ((noise.Noise3D(x*d, y*d, z*d)+0f)*200)-y+5;
        val += noise.Noise3D(x*d2, y*d2, z*d2)*2f;
        val += noise.Noise3D(x*d3, y*d3, z*d3)*.5f;
        //val = (noise.Noise3D(x*d2,y*d2,z*d2))*3;
        return val;
    }
    public byte GetBVoxel (float x, float y, float z) {
        float val = GetVoxel(x,y,z) * 128;
        val = clamp(val);
        return BigByte.toByte((int)val);
    }
    public int GetColor(float x, float y, float z) {
        float d = .05f;
        int val= (int)(noise.Noise3D(x*d, y*d, z*d)*2+1.5f);
        return val;
    }
    
    public float clamp (float val, float min, float max) {
        return val<min? min : val>max? max : val;
    }
    
    public float clamp (float in) {
        return clamp(in, 0, BigByte.MAX_INT);
    }
    
}
