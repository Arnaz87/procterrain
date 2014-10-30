
package me.arnaud.procterrain;

/**
 *
 * @author Arnaud
 */
class Region {
    
    byte[] rawData;
    ChunkInfo[] chunks = new ChunkInfo[8 * 8 * 8];
    Coord position;
    
    public Region (Coord pos) {
        position = pos;
    }
    
    public ChunkInfo GetChunkAt (Coord pos) {
        pos = pos.ShiftRight(4);
        int index = pos.ToIndex(8);
        ChunkInfo chunk = chunks[index];
        if (chunk == null) {
            chunk = new ChunkInfo();
            chunks[index] = chunk;
        }
        return chunk;
    }
    
}
