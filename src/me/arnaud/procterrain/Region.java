
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
    
    private ChunkInfo GetChunk (Coord pos) {
        int index = pos.ToIndex(8);
        ChunkInfo chunk = chunks[index];
        if (chunk == null) {
            chunk = new ChunkInfo();
            chunk.res = 5;
            //GenerateChunk(chunk, pos, 0);
            chunks[index] = chunk;
        }
        return chunk;
    }
    
    public ChunkInfo GetChunkAt (Coord pos) {
        pos = pos.ShiftRight(4);
        return GetChunk(pos);
    }
    
    public void EnsureChunkAt (Coord pos, int res) {
        res--;
        pos = pos.ShiftRight(4).ShiftRightLeft(res);
        Coord cpos;
        ChunkInfo chunk;
        int num = 1 << res;
        for (int a = 0; a < num; a++) {
            for (int b = 0; b < num; b++) {
                for (int c = 0; c < num; c++) {
                    cpos = pos.Add(a, b, c);
                    chunk = GetChunk(cpos);
                    if (chunk.res > res) {
                        chunk.res = res;
                        GenerateChunk(chunk, cpos, res);
                    }
                }
            }
        }
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
        chunk.CreateShortArray(res);
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
