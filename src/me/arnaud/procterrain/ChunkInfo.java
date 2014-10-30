
package me.arnaud.procterrain;

public class ChunkInfo {
    private short[] bytes;
    public int res;
    int fe;     //First Empty byte in the array
    private ByteArray3 rawBytes;
    ModifiableArray ma;
    String topr = null;
    
    public ChunkInfo() {
        bytes = new short[4680];
        for (int i = 0; i < 8; i++) {
            bytes[i] = -1;
        }
        fe = 8;
        ma = new ModifiableArray(4680);
        ma.SetFirstEmpty(8);
    }
    
    public byte Get (Coord pos) {
        if (!pos.InRange(0, 15)) throw new IndexOutOfBoundsException("Coord values must be between 0 and 15: " + pos);
        int local = ToFullIndex(pos);
        short val = ReadAt(0, local);
        if (val < 0) throw new RuntimeException("Negative value: " + val + ", at: " + pos);
        return (byte)val;
    }
    public int ToFullIndex (Coord in) {
        int val = 0;
        val |= in.ShiftRight(3).ToIndex(2);
        val |= in.ShiftRight(2).And(1).ToIndex(2) << 4;
        val |= in.ShiftRight(1).And(1).ToIndex(2) << 8;
        val |= in.And(1).ToIndex(2) << 12;
        return val;
    }
    public short ReadAt (int global, int biglocal) {
        int local = biglocal & 15;
        int pos = global + local;
        short val = bytes[pos];
        if (val < 0) {
            return ReadAt(-val, biglocal >> 4);
        }
        return val;
    }
    
    public byte Get(int x, int y, int z) {
        return Get(new Coord(x,y,z));
    }
    
    public void SetRawBytes(ByteArray3 in) {
        if (in == null) throw new NullPointerException("Raw Bytes can not be null!");
        if (in.size() < 16) throw new RuntimeException("Raw Bytes must have at least a size of 16!");
        rawBytes = in;
    }
    
    public byte GetAverage (int pos, int index, int res) {
        
        
        
        return 0;
    }
    
    public void SetVoxel (Coord pos, byte in) {
        short value = (short)(in & 0xff);
        if (!pos.InRange(0, 15)) throw new IndexOutOfBoundsException("Coord must be between 0 and 15: " + pos);
        int local = ToFullIndex(pos);
        WriteAt(0, local, value, 3);
    }
    
    public void WriteAt (int global, int biglocal, short value, int res) {
        value = value < 0 ? (short)-value : value;
        int local = biglocal & 7;
        int pos = global + local;
        short val = bytes[pos];
        if (val == value) return;
        if (res == 0 || val == -1) {
            bytes[pos] = value;
            return;
        }
        if (val >= 0) {
            bytes[pos] = (short)-fe;
            for (int i = 0; i < 8; i++) {
                bytes[fe+i] = val;
            }
            int np = fe;
            fe += 8;
            WriteAt(np, biglocal >> 4, value, res-1);
        } else {
            WriteAt(-val, biglocal >> 4, value, res-1);
        }
    }
    
    
    public void CreateFromRaw () {
        if (rawBytes.size() < 16) {
            throw new RuntimeException("Raw Bytes must be at least 16 sized!!");
        }
        ma = new ModifiableArray(4680);
        ma.SetFirstEmpty(8);
        for (int i = 0; i < 8; i++) {
            short val = WriteNode(Coord.ARRAY01[i].ShiftLeft(3), 4);
            ma.writeAt(i, val);
        }
        bytes = ma.build();
        System.out.println(bytes.length);
    }
    
    private short WriteNode (Coord pos, int res) {
        if (res < 2) {
            return rawBytes.Get(pos);   //Return the real value at this pos...
        }
        short[] val = new short[8];     //Values
        for (int i = 0; i < 8; i++) {
            Coord newpos = Coord.ARRAY01[i];    //Create a Coord from the Index
            newpos = newpos.ShiftLeft(res-2);   //Since it is just an index, Multiply the coord to create a relative pos.
            newpos = pos.Add(newpos);           //Add the relative newpos to the real position
            val[i] = WriteNode(newpos, res-1);  //Write the node at the real position
        }
        if (EqualArray(val)) {      //If all the values at the arrays are the same:
            return val[0];          //Return that single value and write nothing
        }                                   //else:
        ma.write(val);                          //Write the values and
        return (short)-(ma.FirstEmpty() - 8);   //Return the negative position of the node
    }
    
    private boolean EqualArray(short[] in) {
        boolean val = true;
        for (short s : in) {
            val &= (s == in[0]);
        }
        return val;
    }
    
    public void WriteAll() {
        String st = "";
        for (int i = 0; i < 512; i++) {
            st += bytes[i] + ", ";
        }
        System.out.println(st);
    }
    
    public class ModifiableArray {
        private short[] info;
        private int fe; //First Empty
        public ModifiableArray (int size) {
            info = new short[size];
            fe = 0;
        }
        public boolean write (short in) {
            if (fe >= info.length) return false;
            info[fe] = in;
            fe++;
            return true;
        }
        public boolean write (short[] in) {
            if (fe+in.length >= info.length || in.length < 1) return false;
            for (short b : in) {
                info[fe] = b;
                fe++;
            }
            return true;
        }
        public boolean writeAt (int pos, short in) {
            if (pos >= info.length) return false;
            info[pos] = in;
            return true;
        }
        public short[] build () {
            short[] val = new short[fe];
            System.arraycopy(info, 0, val, 0, fe);
            return val;
        }
        public int FirstEmpty() {
            return fe;
        }
        public void reset () {
            info = new short[info.length];
        }
        
        public int GetFirstEmpty() {
            return 0;
        }
        
        public class Queue {
            private short[] data;
            private int last;
            public Queue (int size) {
                data = new short[size];
                last= 0;
            }
            public void Add (short in) {
                if (in < Get()) {
                    data[last + 1] = in;
                    last++;
                } else {
                    int pos = 0;
                    for (int i = 0; i < last; i++) {
                        
                    }
                }
            }
            private void Move (int pos) {
                for (int i = last; i >= pos; i--) {
                    data[i+1] = data[i];
                }
            }
            public short Get () {
                return data[last];
            }
            public void Remove () {
                data[last] = 0;
                last--;
            }
            public int Count () {
                return last + 1;
            }
        }

        public boolean SetFirstEmpty(int in) {
            if (in >= info.length) return false;
            fe = in;
            return true;
        }
    }
    
}
