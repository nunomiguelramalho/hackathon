package org.academiadecodigo.hackathon.echo;

import com.badlogic.gdx.math.Rectangle;

public class GameObjectsFactory {

    public static Rectangle makeObject(float x, float y, float size){
        return new Rectangle(x, y, size, size);
    }
}