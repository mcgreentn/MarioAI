package competition.cig.mechanicextractor.extractor;

import competition.cig.mechanicextractor.astar.sprites.Sprite;

public class Entity extends Node {

    private byte type;
    private Sprite sprite;


    public Entity(Sprite sprite, byte type) {
        this.sprite = sprite;
        this.type = type;
    }

    /**
     * Sets the type of this entity
     * @param type
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * Gets the type of this entity
     * @return
     */

    public byte getType() {
        return type;
    }

    /**
     * Sets the sprite of this Entity to the given sprite
     * @param sprite
     */
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    /**
     * Gets this Entity's sprite
     * @return
     */
    public Sprite getSprite() {
        return sprite;
    }

}
