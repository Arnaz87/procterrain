/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.arnaud.procterrain;

/**
 *
 * @author Arnaud
 */
public class ByteArray3 {
    private int size;
    private byte[] values;
    
    public ByteArray3 (int ns) {
        size = ns;
        values = new byte[ns*ns*ns];
    }
    
    public int size() { return size; }
    
    public byte[] GetArray () {
        byte[] val = new byte[size];
        System.arraycopy(values, 0, val, 0, size);
        return val;
    }
    
    public byte[][][] Get3DArray () {
        byte[][][] val = new byte[size][size][size];
        for (int a = 0; a < size; a++) {
            for (int b = 0; b < size; b++) {
                for (int c = 0; c < size; c++) {
                   val[a][b][c] = values[GetIndex(a,b,c)]; 
                }
            }
        }
        return val;
    }
    
    public int GetIndex (Coord in) {
        if (in.Max() > size || in.Min() < 0)
            throw new IndexOutOfBoundsException(in.toString());
        return GetIndex(in.x,in.y,in.z);
    }
    public int GetIndex(int x, int y, int z) {
        /*int val = 0;
        
        val += y<16?y:y+4096;
        val += x*size;
        val += z*size*size;
        return val;*/
        return x + z*size + y*size*size;
    }
    
    public void Set (Coord in, byte val) {
        values[GetIndex(in)] = val;
    }
    
    public byte Get (Coord in) {
        return values[GetIndex(in)];
    }
    public byte Get (int x, int y, int z) {
        return Get(new Coord(x,y,z));
    }
    
}
