package org.academiadecodigo.hackathon.echo.assets;

public enum Levels {

    LEVEL_1("level1/level1.tmx", 2300, 64, 4235, 224, 5182, 32),
    LEVEL_2("level2/level2.tmx", 2765, 128, 3685, 128, 4310, 16);

    public String path;
    public float keyX;
    public float keyY;
    public float closetX;
    public float closetY;
    public float npcX;
    public float npcY;

    Levels(String path, float keyX, float keyY, float closetX, float closetY, float npcX, float npcY) {

        this.path = path;
        this.keyX = keyX;
        this.keyY = keyY;
        this.closetX = closetX;
        this.closetY = closetY;
        this.npcX = npcX;
        this.npcY = npcY;
    }
}