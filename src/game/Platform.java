package game;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

public class Platform extends StaticBody {

    //for the platform texture to fit perfectly
    private static float getSnappedWidth(float requestedWidth, float height) {
        float tileWidth = (height * 2f) * (420f / 306f); // Your exact image aspect ratio

        int tiles = Math.round((requestedWidth * 2f) / tileWidth);
        if (tiles < 1) tiles = 1;

        return (tiles * tileWidth) / 2f;
    }

    public Platform(World world, float requestedWidth, float height, float x, float y) {
        super(world, new BoxShape(getSnappedWidth(requestedWidth, height), height));
        this.setPosition(new Vec2(x, y));

        float perfectHalfWidth = getSnappedWidth(requestedWidth, height);
        float perfectFullWidth = perfectHalfWidth * 2f;
        float fullHeight = height * 2f;
        float tileWidth = fullHeight * (420f / 306f);

        BodyImage image = new BodyImage("data/assets/lvl1/platform-texture.png", fullHeight);

        float startX = -perfectHalfWidth + (tileWidth / 2f);

        for (float i = 0; i < perfectFullWidth; i += tileWidth) {
            AttachedImage tile = this.addImage(image);
            tile.setOffset(new Vec2(startX + i, 0));
        }
    }
}