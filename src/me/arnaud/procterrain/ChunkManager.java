
package me.arnaud.procterrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class ChunkManager {
    HashMap<Coord, Chunk> chunks = new HashMap<Coord, Chunk>();
    HashMap<Coord, Integer> resolutions = new HashMap<Coord, Integer>();
    TerrainManager terrain;
    int minRes = 0;
    int maxRes = 3;
    public int farcount = 0;
    boolean debugb = false;
    
    public ChunkManager(TerrainManager in) {
        terrain = in;
    }
    
    public Chunk GetChunk (Coord in) {
        return chunks.get(in);
    }
    public boolean RemoveChunk (Coord in) {
        Chunk chunk = chunks.get(in);
        if (chunk != null) {
            //System.out.println("removing " + in.toString());
            chunk.Clear();
            chunks.remove(in);
            return true;
        }
        return false;
    }
    
    public void CleanChunks () {
        Coord[] rs = new Coord[0];
        for (Map.Entry<Coord, Chunk> entry : chunks.entrySet()) {
            Coord key = entry.getKey();
            Chunk val = entry.getValue();
            Integer res = resolutions.get(key);
            if (res == null || res != val.resolution ) {
                int p = rs.length;
                rs = Arrays.copyOf(rs, p+1);
                rs[p] = key;
            }
        }
        for (Coord key : rs) {
            RemoveChunk(key);
        }
        
    }
    
    public Chunk CreateChunk (int x, int y, int z, int res) {
        return CreateChunk(new Coord(x,y,z),res);
    }
    public Chunk CreateChunk (Coord in, int res) {
        Chunk chunk = new Chunk();
        chunks.put(in, chunk);
        chunk.terrain = terrain;
        chunk.size = 16;
        chunk.SetResolution(res);
        chunk.index = new int[] {in.x,in.y,in.z};
        chunk.cindex = in;
        chunk.mat = terrain.mat;
        chunk.parentNode = terrain.node;
        if (debugb) {
            chunk.DebugBox();
            return chunk;
        }
        terrain.data.EnsureChunkAt(chunk.cindex.Mult(chunk.size), res);
        //chunk.CreateVoxels();
  //      chunk.MarchCubes();
  //      //chunk.CalculateNormals();
  //      chunk.CalculateFacetsNormals();
  //      //chunk.CalculateColors();
  //      chunk.CreateMesh();
  //      chunk.CreateGeometry();
        return chunk;
    }
    
    public void CreateMesh(Chunk chunk) {
        chunk.MarchCubes();
        //chunk.CalculateNormals();
        chunk.CalculateFacetsNormals();
        //chunk.CalculateColors();
        chunk.CreateMesh();
        chunk.CreateGeometry();
    }
    
    public boolean ChunkExists(Coord in) {
        return chunks.containsKey(in);
    }
    
    public void RemoveUsedChunk(Coord in, int r) {
        Coord nc;
        nc = in.Floor(r);
        int nr = 1<<r-1;
        int a, b, c;
        for (a=0;a<nr;a++){
            for (b=0;b<nr;b++){
                for (c=0;c<nr;c++){
                    RemoveChunk(nc.Add(a,b,c));
                }
            }
        }
        for (int i = maxRes; i>r; i--) {
            nc = in.Floor(i);
            Chunk ch = chunks.get(nc);
            if (ch != null && ch.res == i+1) {
                ch.Clear();
                chunks.remove(nc);
            }
        }
    }
    public void SetRes (int x, int y, int z, int r) { SetRes(new Coord(x,y,z),r); }
    public void SetRes (Coord in, int r) {
        int nr = 1<<r-1;
        Coord c = in.Floor(r+1);
        
        for (Coord i : cords01) {
            resolutions.put(c.Add(i.Mult(nr)), r);
        }
        //resolutions.put(in, r);
    }
    
    public void SetLRes (Coord in, int r) {
        resolutions.put(in.Floor(r), r);
    }
    
    public void SetResolutions () {
        ArrayList<Chunk> created = new ArrayList<Chunk>();
        for (Map.Entry<Coord, Integer> entry : resolutions.entrySet()) {
            Coord c = entry.getKey();
            int r = entry.getValue();
            Chunk ec = chunks.get(c);
            if (ec == null || ec.resolution != r) {
                RemoveUsedChunk(c,r);
                created.add(CreateChunk(c,r));
            }
        }
        for (Chunk ch : created) {
            CreateMesh(ch);
        }
    }
    
    public void ActualizePosition(Coord in) {
        terrain.print("Actualize " + in);
        resolutions.clear();
        int r;
        r = maxRes+1;
        int np = 1<<r-1;
        int fc = farcount<2 ? 1 : farcount/2;
        int i, j, k;
        for (i = 0; i < farcount; i++) {
            for (j = 0; j < farcount; j++) {
                for (k = 0; k < fc; k++) {
                    Coord nc = new Coord(i,k,j);
                    for (Coord l : cords11) {
                        SetLRes(in.Add(l.Mult(nc).Mult(np)) , r);
                        
                        //SetLRes(in.Add(l.Mult(np)),r);
                    }
                }
            }
        }
        for (i = maxRes; i > minRes; i--) {
            r = 1 << i;
            for (Coord l : cords111) {
                SetRes(in.Add(l.Mult(r)), i);
            }
        }
        //SetRes(in,1);
        CleanChunks();
        
        SetResolutions();
        
    }

    public short GetVoxel (Coord pos) {
        Coord ch;
        Coord vo;
        Chunk chu;
        for (int i = 0; i < 3; i++) {
            ch = pos.ShiftRight(4).ShiftRight(i).ShiftLeft(i);
            vo = pos.ShiftRight(i).And(15); 
            //vo = pos.Substract(ch);
            //System.out.println(pos + ", " + ch + ", " + vo);
            chu = GetChunk(ch);
            if (chu != null) {
                return chu.GetVoxel(vo);
            }
        }
        return terrain.voxels.GetBVoxel(pos.x, pos.y, pos.z);
    }
    
    //Coords going from 0 to 1 in all three axes
    //size: 8
    static final Coord[] cords01 = new Coord[] {
        new Coord(0,0,0), new Coord(0,0,1),
        new Coord(0,1,0), new Coord(0,1,1),
        new Coord(1,0,0), new Coord(1,0,1),
        new Coord(1,1,0), new Coord(1,1,1)
    };
    
    //Coords going from -1 to 1 in all three axes
    //size: 8
    static final Coord[] cords11 = new Coord[] {
        new Coord(-1,-1,-1), new Coord(-1,-1,1),
        new Coord(-1,1,-1), new Coord(-1,1,1),
        new Coord(1,-1,-1), new Coord(1,-1,1),
        new Coord(1,1,-1), new Coord(1,1,1)
    };
    
    //Coords going from -1 to 1 in all three axes
    //size: 27
    static final Coord[] cords111 = new Coord[] {
        new Coord(0,0,0), new Coord(0,0,-1), new Coord(0,0,1),
        new Coord(0,-1,0), new Coord(0,-1,-1), new Coord(0,-1,1),
        new Coord(0,1,0), new Coord(0,1,-1), new Coord(0,1,1),
        
        new Coord(1,0,0), new Coord(1,0,-1), new Coord(1,0,1),
        new Coord(1,-1,0), new Coord(1,-1,-1), new Coord(1,-1,1),
        new Coord(1,1,0), new Coord(1,1,-1), new Coord(1,1,1),
        
        new Coord(-1,0,0), new Coord(-1,0,-1), new Coord(-1,0,1),
        new Coord(-1,-1,0), new Coord(-1,-1,-1), new Coord(-1,-1,1),
        new Coord(-1,1,0), new Coord(-1,1,-1), new Coord(-1,1,1),
    };
}
