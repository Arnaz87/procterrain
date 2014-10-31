/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.arnaud.procterrain;

import com.jme3.material.Material;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;

/**
 *
 * @author Arnaud
 */
public class Chunk {
    
    public int[] indexes;
    public Vector3f[] vertices;
    public Vector3f[] normals;
    public float[] colors;
    public float[][][] m_voxels;
    public ByteArray3 b_voxels;
    public ChunkInfo cinfo;
    Vector3f[][][] m_normals;
    int[][][] m_colors;
    public Mesh mesh = new Mesh();
    public Geometry geom;
    public Node parentNode;
    public Vector3f pos;
    public int size = 16;
    public int resolution = 1;
    public int res = 1;
    public int[] index = new int[] {0,0,0};
    public Coord cindex = Coord.ZERO;
    public TerrainManager terrain;
    public Material mat;
    
    public Chunk() {
    }
    
    public void CreateVoxels() {
        int ns = size; //= size+4;
        //m_voxels = new float[ns][ns][ns];
        cinfo = new ChunkInfo();
        cinfo.res = resolution;
        b_voxels = new ByteArray3(ns);
        m_colors = new int[ns][ns][ns];
        
        for (int a = 0; a < ns; a++) {
            for (int b = 0; b < ns; b++) {
                for (int c = 0; c < ns; c++) {
                    float x = (a-2)*res+index[0]*size;
                    float y = (b-2)*res+index[1]*size;
                    float z = (c-2)*res+index[2]*size;
                    //m_voxels[a][b][c] = terrain.GetVoxel(x, y, z)-resolution;
                    Coord coord = new Coord(a,b,c);
                    byte value = terrain.GetBVoxel(x, y, z);
                    //b_voxels.Set(coord, value);
                    cinfo.SetVoxel(coord, value);
                    m_colors[a][b][c] = terrain.GetColor(x, y, z);
                }
            }
        }
        //System.out.println(cinfo.fe);
        //System.out.println(cinfo.fe);
        //cinfo.SetRawBytes(b_voxels);
        //cinfo.CreateFromRaw();
    }
    public void MarchCubes() {
        MarchingCubes.GetMesh(cinfo, this);
    }
    public void SetVertices (ArrayList<Vector3f> in) {
        int s = in.size();
        vertices = new Vector3f[s];
        for (int i = 0; i < s; i++) {
            vertices[i] = in.get(i);
        }
    }
    public void SetIndexes (ArrayList<Integer> in) {
        int s = in.size();
        indexes = new int[s];
        for (int i = 0; i < s; i++) {
            indexes[i] = in.get(i);
        }
    }
    
    public short GetVoxel(Coord pos) {
        Coord gp = pos.Mult(res).Add(cindex.Mult(size));
        //Coord gp = cindex.Mult(size).Add(pos.ShiftLeft(res));
        return terrain.GetDVoxel(gp);
    }
    
    public void SetResolution (int in) {
        resolution = in;
        res = 1 << in-1;
    }
    
    public void CalculateFacetsNormals () {
        int is = indexes.length/3;
        normals = new Vector3f[is*3];
        for (int i = 0; i < is; i++) {
            int j = i*3;
            Triangle tri = new Triangle(vertices[j], vertices[j+1], vertices[j+2]);
            Vector3f norm = tri.getNormal().negateLocal();
            normals[j] = norm;
            normals[j+1] = norm;
            normals[j+2] = norm;
        }
    }
    
    public void CalculateColors () {
        
        int ns = vertices.length;
        colors = new float[ns*3];
        for (int i = 0; i < ns; i++) {
            int ni = i*3;
            float[] ac = TriLinearInterpColor(vertices[i]);
            colors[ni]   = ac[0];
            colors[ni+1] = ac[1];
            colors[ni+2] = ac[2];
        }
    }
    
    public void CalculateNormals () {
        //--- Voxels Normals ------------
        if (m_voxels == null) {
            System.out.println("Not voxels here");
            return;
        }
        int w = m_voxels.length-1;
	int h = m_voxels[0].length-1;
	int l = m_voxels[0][0].length-1;
		
	if(m_normals == null) m_normals = new Vector3f[w+1][h+1][l+1];
	
	for(int x = 1; x < w; x++)
	{
            for(int y = 1; y < h; y++)
            {
		for(int z = 1; z < l; z++)
		{
                    float dx = m_voxels[x+1][y][z] - m_voxels[x-1][y][z];
                    float dy = m_voxels[x][y+1][z] - m_voxels[x][y-1][z];
                    float dz = m_voxels[x][y][z+1] - m_voxels[x][y][z-1];
				
                    m_normals[x][y][z] = new Vector3f(dx,dy,dz).normalizeLocal().negateLocal();
		}
            }
	}
        //Real Normals Calculation
        int ns = vertices.length;
        normals = new Vector3f[ns];
        for (int i = 0; i < ns; i++) {
            normals[i] = TriLinearInterpNormal(vertices[i]);
        }
        
    }
    
    private Vector3f GetNormal (int x, int y, int z) {
        x = Math.min(x, m_normals.length-2);
        y = Math.min(y, m_normals[0].length-2);
        z = Math.min(z, m_normals[0][0].length-2);
        if (m_normals[x][y][z] == null) {
            System.out.println("Null normal at " + x + ", " + y + ", " + z);
        }
        return m_normals[x][y][z];
    }
    private VoxelColor GetColor (int x, int y, int z) {
        x = Math.min(x, m_colors.length-2);
        y = Math.min(y, m_colors[0].length-2);
        z = Math.min(z, m_colors[0][0].length-2);
        return new VoxelColor(m_colors[x][y][z]);
    }
    
    private float[] TriLinearInterpColor (Vector3f in) {
        int x = (int) Math.floor(in.x);
	int y = (int) Math.floor(in.y);
	int z = (int) Math.floor(in.z);
		
	float fx = cl(in.x-x);
	float fy = cl(in.y-y);
	float fz = cl(in.z-z);
		
	VoxelColor x0 = GetColor(x,y,z).interpolate(GetColor(x+1,y,z), fx);
	VoxelColor x1 = GetColor(x,y,z+1).interpolate(GetColor(x+1,y,z+1), fx);
	
	VoxelColor x2 = GetColor(x,y+1,z).interpolate(GetColor(x+1,y+1,z), fx);
	VoxelColor x3 = GetColor(x,y+1,z+1).interpolate(GetColor(x+1,y+1,z+1), fx);
	
	VoxelColor z0 = x0.interpolate(x1, fz);
	VoxelColor z1 = x2.interpolate(x3, fz);
		
	return z0.interpolate(z1, fy).GetFloats();
        //return new float[] {1,0,0,0};
        //return z0.GetFloats();
    }
    
    private float cl(float t) { return (t<0)?0:(t>1)?1:t; }
    
    private Vector3f TriLinearInterpNormal(Vector3f in) {
        int x = (int) Math.floor(in.x);
	int y = (int) Math.floor(in.y);
	int z = (int) Math.floor(in.z);
		
	float fx = in.x-x;
	float fy = in.y-y;
	float fz = in.z-z;
	
        /*/
	Vector3f x0 = GetNormal(x,y,z).mult(1.0f-fx).add(GetNormal(x+1,y,z).mult(fx));
	Vector3f x1 = GetNormal(x,y,z+1).mult(1.0f-fx).add(GetNormal(x+1,y,z+1).mult(fx));
		
	Vector3f x2 = GetNormal(x,y+1,z).mult(1.0f-fx).add(GetNormal(x+1,y+1,z).mult(fx));
	Vector3f x3 = GetNormal(x,y+1,z+1).mult(1.0f-fx).add(GetNormal(x+1,y+1,z+1).mult(fx));
		
	Vector3f z0 = x0.mult(1.0f-fz).add(x1.mult(fz));
	Vector3f z1 = x2.mult(1.0f-fz).add(x3.mult(fz));
	
        
	return z0.mult(1.0f-fy).addLocal(z1.mult(fy));
        /*/
        Vector3f x0 = GetNormal(x,y,z).clone().interpolate(GetNormal(x+1,y,z), fx);
	Vector3f x1 = GetNormal(x,y,z+1).clone().interpolate(GetNormal(x+1,y,z+1), fx);
		
	Vector3f x2 = GetNormal(x,y+1,z).clone().interpolate(GetNormal(x+1,y+1,z), fx);
	Vector3f x3 = GetNormal(x,y+1,z+1).clone().interpolate(GetNormal(x+1,y+1,z+1), fx);
		
	Vector3f z0 = x0.interpolate(x1, fz);
	Vector3f z1 = x2.interpolate(x3, fz);
        
	return z0.interpolate(z1, fy);
        //*/
        
    }
    
    public void CreateMesh() {
        if (vertices.length + indexes.length < 6) return;
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
        if(normals != null) {
            mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        }
        if(colors != null) {
            mesh.setBuffer(Type.Color, 3, BufferUtils.createFloatBuffer(colors));
        }
        mesh.updateBound();
    }
    public void CreateGeometry () {
        if (vertices.length + indexes.length < 6) return;
        geom = new Geometry("Chunk", mesh);
        pos = new Vector3f(index[0], index[1], index[2]).multLocal(size);//.subtractLocal(2*res, 2*res, 2*res);
        geom.setLocalTranslation(pos);
        geom.setLocalScale(res, res, res);
        geom.setMaterial(mat);
        parentNode.attachChild(geom);
    }
    
    public void Clear () {
        if (geom != null) {
            parentNode.detachChild(geom);
            mesh.clearBuffer(Type.Position);
            mesh.clearBuffer(Type.Index);
            mesh.clearBuffer(Type.Normal);
            mesh.updateBound();
            geom = null;
        }
        mesh = null;
        indexes = null;
        vertices = null;
        normals = null;
        colors = null;
        m_normals = null;
        m_voxels = null;
        m_colors = null;
    }
    
    public void DebugBox () {
        //mesh = new Box(16,16,16);
        //mesh.setMode(Mesh.Mode.Lines);
        makebox();
        
        geom = new Geometry("Chunk", mesh);
        pos = new Vector3f(index[0], index[1], index[2]).multLocal(size).subtractLocal(2*res, 2*res, 2*res);
        geom.setLocalTranslation(pos);
        geom.setLocalScale(res, res, res);
        geom.setMaterial(mat);
        parentNode.attachChild(geom);
    }
    
    public Mesh makebox() {
        mesh = new Mesh();
        int x = 16;
        Vector3f[] bv = new Vector3f[] {
            new Vector3f(0,0,0),
            new Vector3f(x,0,0),
            new Vector3f(0,x,0),
            new Vector3f(x,x,0),
            new Vector3f(0,0,x),
            new Vector3f(x,0,x),
            new Vector3f(0,x,x),
            new Vector3f(x,x,x)
        };
        int[] bi = new int[] {
            0,1, 2,3, 0,2, 1,3, 
            4,5, 6,7, 4,6, 5,7, 
            0,4, 1,5, 2,6, 3,7
        };
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(bv));
        mesh.setBuffer(Type.Index, 2, bi);
        
        mesh.setMode(Mesh.Mode.Lines);
        
        mesh.updateBound();
        
        return mesh;
    }
    
}
