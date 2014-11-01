
package me.arnaud.procterrain;

/**
 *
 * @author Arnaud
 */
class Region {
    
    TerrainManager terrain;
    byte[] rawData;
    ChunkInfo[] chunks = new ChunkInfo[8 * 8 * 8];
    Coord position;
    Coord worldPosition;
    
    public Region (Coord pos, TerrainManager nter) {
        position = pos;
        worldPosition = pos.ShiftLeft(4 + 3);
        terrain = nter;
    }
    
    public ChunkInfo GetChunkAt (Coord pos) {
        pos = pos.ShiftRight(4);
        int index = pos.ToIndex(8);
        ChunkInfo chunk = chunks[index];
        if (chunk == null) {
            chunk = new ChunkInfo();
            chunk.res = 0;
            GenerateChunk(chunk, pos, 0);
            chunks[index] = chunk;
        }
        return chunk;
    }

    private void GenerateChunk(ChunkInfo chunk, Coord pos, int res) {
        int num = 1 << res;
        //System.out.println(num);
        Coord npos,
            localPos,
            chunkPos = pos.ShiftLeft(4),
            regionLevel,
            worldLevel;
        byte val;
        for (int a = 0; a < 16; a += num) {
            for (int b = 0; b < 16; b += num) {
                for (int c = 0; c < 16; c += num) {
                    localPos = new Coord(a, b, c);
                    regionLevel = chunkPos.Add(localPos);
                    worldLevel = worldPosition.Add(regionLevel);
                    val = terrain.GetBVoxel(worldLevel);
                    //System.out.println(pos + " -- " + val);
                    chunk.SetVoxel(localPos, val);
                }
            }
        }
    }
    
}
