
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
            GenerateChunk(chunk, pos, 0);
            chunks[index] = chunk;
        }
        return chunk;
    }

    private void GenerateChunk(ChunkInfo chunk, Coord pos, int res) {
        int num = 16 >> res;
        Coord npos,
            localPos,
            chunkPos = pos.ShiftLeft(4),
            regionLevel,
            worldLevel;
        byte val;
        for (int a = 0; a < num; a++) {
            for (int b = 0; b < num; b++) {
                for (int c = 0; c < num; c++) {
                    localPos = new Coord(a, b, c).ShiftLeft(res);
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
