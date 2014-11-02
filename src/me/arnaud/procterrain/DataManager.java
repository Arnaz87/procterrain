
package me.arnaud.procterrain;

import java.util.HashMap;

/**
 *
 * @author Arnaud
 */
class DataManager {
    
    TerrainManager terrain;
    HashMap<Coord, Region> regions = new HashMap<Coord, Region>();
    
    //short[] chunkInfoBuffer = new short[16 * 16 * 16];

    public DataManager(TerrainManager aThis) {
        terrain = aThis;
    }
    
    public Region GetRegionAt (Coord pos) {
        Coord localPos = pos.ShiftRight(4 + 3);
        Region region = regions.get(localPos);
        if (region == null) {
            region = new Region(localPos, terrain);
            regions.put(localPos, region);
        }
        return region;
    }
    
    public void EnsureChunkAt (Coord pos, int res) {
        Region region = GetRegionAt(pos);
        region.EnsureChunkAt(pos.And(0x7f), res);
    }
    
    public ChunkInfo GetChunkAt (Coord pos) {
        Region region = GetRegionAt(pos);
        return region.GetChunkAt(pos.And(0x7f));
    }
    
    public byte GetVoxelAt (Coord pos) {
        ChunkInfo chunk = GetChunkAt(pos);
        return chunk.Get(pos.And(15));
    }
    
}
