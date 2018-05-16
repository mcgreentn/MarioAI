package competition.cig.mechanicextractor.astar.sprites;

import competition.cig.mechanicextractor.astar.LevelScene;


public class FireFlower extends Sprite
{
    int height = 24;

    private LevelScene world;
    public int facing;

    public boolean avoidCliffs = false;
    private int life;

    public FireFlower(LevelScene world, int x, int y)
    {
        kind = 88;

        this.x = x;
        this.y = y;
        this.world = world;
        height = 12;
        facing = 1;
        life = 0;
    }

    public void collideCheck()
    {
        float xMarioD = world.mario.x - x;
        float yMarioD = world.mario.y - y;
        if (xMarioD > -16 && xMarioD < 16)
        {
            if (yMarioD > -height && yMarioD < world.mario.height)
            {
                world.mario.getFlower();
                spriteContext.removeSprite(this);
            }
        }
    }
    public boolean collideCheck2() {
        float xMarioD = world.mario.x - x;
        float yMarioD = world.mario.y - y;
        float w = 16;
        if (xMarioD > -16 && xMarioD < 16)
        {
            if (yMarioD > -height && yMarioD < world.mario.height)
            {
                return true;
            }
        }
        return false;
    }
    public void move()
    {
        if (life<9)
        {
            layer = 0;
            y--;
            life++;
            return;
        }
    }
}