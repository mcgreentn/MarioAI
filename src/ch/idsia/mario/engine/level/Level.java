package ch.idsia.mario.engine.level;

import java.io.*;
import java.util.Random;


public class Level
{
    public static final String[] BIT_DESCRIPTIONS = {//
    "BLOCK UPPER", //
            "BLOCK ALL", //
            "BLOCK LOWER", //
            "SPECIAL", //
            "BUMPABLE", //
            "BREAKABLE", //
            "PICKUPABLE", //
            "ANIMATED",//
    };

    public static byte[] TILE_BEHAVIORS = new byte[256];

    public static final int BIT_BLOCK_UPPER = 1 << 0;
    public static final int BIT_BLOCK_ALL = 1 << 1;
    public static final int BIT_BLOCK_LOWER = 1 << 2;
    public static final int BIT_SPECIAL = 1 << 3;
    public static final int BIT_BUMPABLE = 1 << 4;
    public static final int BIT_BREAKABLE = 1 << 5;
    public static final int BIT_PICKUPABLE = 1 << 6;
    public static final int BIT_ANIMATED = 1 << 7;

    private static final int FILE_HEADER = 0x271c4178;
    public int width;
    public int height;

    public byte[][] map;
    public byte[][] data;
    public byte[][] observation;

    public SpriteTemplate[][] spriteTemplates;

    public int xExit;
    public int yExit;


    public Level(int width, int height)
    {
        this.width = width;
        this.height = height;

        xExit = 10;
        yExit = 10;
        map = new byte[width][height];
        data = new byte[width][height];
        spriteTemplates = new SpriteTemplate[width][height];
        observation = new byte[width][height];
    }
    
    private static int[] getCorrectIndex(String[] lines, int x, int y) {
	int[][] table = new int[][] {
	    new int[] {}, 
	    new int[] {}, 
	    new int[] {},
	    new int[] {},
	    new int[] {},
	    new int[] {2, 0},
	    new int[] {0, 0},
	    new int[] {1, 0},
	    new int[] {},
	    new int[] {2, 2},
	    new int[] {0, 2},
	    new int[] {1, 2},
	    new int[] {},
	    new int[] {2, 1},
	    new int[] {0, 1},
	    new int[] {1, 1}
	    };
	int index = 0;
	if(x == 0 || lines[y].charAt(x - 1) == 'X') {
	    index += 1;
	}
	if(x == lines[y].length() - 1 || lines[y].charAt(x + 1) == 'X') {
	    index += 2;
	}
	if(y == lines.length - 1 || lines[y + 1].charAt(x) == 'X') {
	    index += 4;
	}
	if(y == 0 || lines[y - 1].charAt(x) == 'X') {
	    index += 8;
	}
	
	return table[index];
    }
    
    private static boolean isGrounded(String[] lines, int x, int y) {
	if(y == 0) {
	    return true;
	}
	Character c = lines[y-1].charAt(x);
	return !(c == 'X' || c == 'S'|| c == '?' || c == 'Q' || c == '<' || c == '>' || c == '[' || c == ']');
    }
    
    private static int findFirstFloor(String[] lines, int x) {
	boolean groundFound = false;
	for(int i=lines.length - 1; i>= 0; i--) {
	    Character c = lines[i].charAt(x);
	    if(!groundFound) {
		if(c == '-') {
		    return i;
		}
	    }
	    else {
		if(c == 'X' || c == 'S'|| c == '?' || c == 'Q' || c == '<' || c == '>' || c == '[' || c == ']') {
		    groundFound = true;
		}
	    }
	}
	
	return -1;
    }
    
    public static Level initializeLevel(Random rnd, String level) {
	String[] lines = level.split("\n");
	for(int i=0; i<lines.length; i++) {
	    if(i == lines.length - 1) {
		lines[i] = "XXX" + lines[i] + "XXX";
	    }
	    else {
		lines[i] = "---" + lines[i] + "---";
	    }
	}
	Level lvl = new Level(lines[0].length() + 6, lines.length);
	for(int y=0; y<lines.length; y++) {
	    for(int x=0; x<lines[y].length(); x++) {
		Character c = lines[y].charAt(x);
		switch(c) {
		case 'E':
		    lvl.setSpriteTemplate(x, y, new SpriteTemplate(rnd.nextInt(4), !isGrounded(lines, x, y)));
		    break;
		case 'X':
		    int[] indeces = getCorrectIndex(lines, x, y);
		    if(indeces.length == 2) {
			lvl.setBlock(x, y, (byte)(4 + indeces[0] + (8 + indeces[1]) * 16));
		    }
		    else {
			lvl.setBlock(x, y, (byte)(9));
		    }
		    break;
		case '?':
		    lvl.setBlock(x, y, (byte)(20 + rnd.nextInt(4)));
		    break;
		case 'Q':
		    lvl.setBlock(x, y, (byte)(4 + rnd.nextInt(4)));
		    break;
		case 'S':
		    lvl.setBlock(x, y, (byte)(16 + rnd.nextInt(4)));
		    break;
		case 'o':
		    lvl.setBlock(x, y, (byte)(32));
		    break;
		case '<':
		    lvl.setBlock(x, y, (byte)(10));
		    break;
		case '>':
		    lvl.setBlock(x, y, (byte)(11));
		    break;
		case '[':
		    lvl.setBlock(x, y, (byte)(10 + 16));
		    break;
		case ']':
		    lvl.setBlock(x, y, (byte)(11 + 16));
		    break;
		}
	    }
	}
	lvl.xExit = lines[0].length() - 3;
	lvl.yExit = lines.length - 2;
	if(lvl.yExit == -1){
	    lvl.yExit = rnd.nextInt(lines.length);
    }
	return lvl;
    }

//    public void ASCIIToOutputStream(OutputStream os) throws IOException {
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
//        bw.write("\nwidth = " + width);
//        bw.write("\nheight = " + height);
//        bw.write("\nMap:\n");
//        for (int y = 0; y < height; y++)
//
//        {
//                for (int x = 0; x < width; x++)
//
//            {
//                bw.write(map[x][y] + "\t");
//            }
//            bw.newLine();
//        }
//        bw.write("\nData: \n");
//
//        for (int y = 0; y < height; y++)
//
//        {
//                for (int x = 0; x < width; x++)
//
//            {
//                bw.write(data[x][y] + "\t");
//            }
//            bw.newLine();
//        }
//
//        bw.write("\nspriteTemplates: \n");
//        for (int y = 0; y < height; y++)
//
//        {
//                for (int x = 0; x < width; x++)
//
//            {
//                if                  (spriteTemplates[x][y] != null)
//                    bw.write(spriteTemplates[x][y].getType() + "\t");
//                else
//                    bw.write("_\t");
//
//            }
//            bw.newLine();
//        }
//
//        bw.write("\n==================\nAll objects: (Map[x,y], Data[x,y], Sprite[x,y])\n");
//        for (int y = 0; y < height; y++)
//        {
//                for (int x = 0; x < width; x++)
//
//            {
//                bw.write("(" + map[x][y] + "," + data[x][y] + ", " + ((spriteTemplates[x][y] == null) ? "_" : spriteTemplates[x][y].getType()) + ")\t");
//            }
//            bw.newLine();
//        }
//
////        bw.close();
//    }

    public static void loadBehaviors(DataInputStream dis) throws IOException
    {
        dis.readFully(Level.TILE_BEHAVIORS);
    }

    public static void saveBehaviors(DataOutputStream dos) throws IOException
    {
        dos.write(Level.TILE_BEHAVIORS);
    }

    public static Level load(DataInputStream dis) throws IOException
    {
        long header = dis.readLong();
        if (header != Level.FILE_HEADER) throw new IOException("Bad level header");
        int version = dis.read() & 0xff;

        int width = dis.readShort() & 0xffff;
        int height = dis.readShort() & 0xffff;
        Level level = new Level(width, height);
        level.map = new byte[width][height];
        level.data = new byte[width][height];
        for (int i = 0; i < width; i++)
        {
            dis.readFully(level.map[i]);
            dis.readFully(level.data[i]);
        }
        return level;
    }

    public void save(DataOutputStream dos) throws IOException
    {
        dos.writeLong(Level.FILE_HEADER);
        dos.write((byte) 0);

        dos.writeShort((short) width);
        dos.writeShort((short) height);

        for (int i = 0; i < width; i++)
        {
            dos.write(map[i]);
            dos.write(data[i]);
        }
    }

    public void tick()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (data[x][y] > 0) data[x][y]--;
            }
        }
    }

    public byte getBlockCapped(int x, int y)
    {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x >= width) x = width - 1;
        if (y >= height) y = height - 1;
        return map[x][y];
    }

    public byte getBlock(int x, int y)
    {
        if (x < 0) x = 0;
        if (y < 0) return 0;
        if (x >= width) x = width - 1;
        if (y >= height) y = height - 1;
        return map[x][y];
    }

    public void setBlock(int x, int y, byte b)
    {
        if (x < 0) return;
        if (y < 0) return;
        if (x >= width) return;
        if (y >= height) return;
        map[x][y] = b;
    }

    public void setBlockData(int x, int y, byte b)
    {
        if (x < 0) return;
        if (y < 0) return;
        if (x >= width) return;
        if (y >= height) return;
        data[x][y] = b;
    }

    public boolean isBlocking(int x, int y, float xa, float ya)
    {
        byte block = getBlock(x, y);
        boolean blocking = ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_ALL) > 0;
        blocking |= (ya > 0) && ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_UPPER) > 0;
        blocking |= (ya < 0) && ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_LOWER) > 0;

        return blocking;
    }

    public SpriteTemplate getSpriteTemplate(int x, int y)
    {
        if (x < 0) return null;
        if (y < 0) return null;
        if (x >= width) return null;
        if (y >= height) return null;
        return spriteTemplates[x][y];
    }

    public void setSpriteTemplate(int x, int y, SpriteTemplate spriteTemplate)
    {
        if (x < 0) return;
        if (y < 0) return;
        if (x >= width) return;
        if (y >= height) return;
        spriteTemplates[x][y] = spriteTemplate;
    }

    public int getWidthCells() {         return width;    }

    public double getWidthPhys() {         return width * 16;    }
}