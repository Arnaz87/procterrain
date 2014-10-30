
package me.arnaud.procterrain;

import com.jme3.material.Material;
import com.jme3.scene.Node;

public class TerrainManager {
    
    VoxelManager voxels;
    ChunkManager chunks;
    public Node node;
    public Material mat;
    public PerlinNoise noise;
    public int maxRes = 4;
    public int farcount = 5;
    public boolean debugb = false;
    
    public TerrainManager () {
        noise = new PerlinNoise(123);
        voxels = new VoxelManager(this);
        chunks = new ChunkManager(this);
    }
    
    public Chunk GetChunk (int x, int y, int z) {
        return chunks.GetChunk(new Coord(x,y,z));
    }
    
    public boolean ChunkExists(int x, int y, int z) {
        return chunks.ChunkExists(new Coord(x,y,z));
    }
    
    public float GetVoxel(float x, float y, float z) {
        return voxels.GetVoxel(x, y, z);
    }
    public byte GetBVoxel (float x, float y, float z) {
        return voxels.GetBVoxel(x, y, z);
    }
    public int GetColor(float x, float y, float z) {
        return voxels.GetColor(x, y, z);
    }
    
    public void ActualizePosition(Coord in) {
        chunks.ActualizePosition(in);
    }
    
    public void print (Object in) {
        //System.out.println(in);
    }
    
}
