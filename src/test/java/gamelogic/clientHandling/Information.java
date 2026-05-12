package gamelogic.clientHandling;

import java.io.Serializable;
import java.util.ArrayList;

import gamelogic.key.Key;

public class Information implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id = -1;
    Float myX;
    Float myY;
    private transient Key key;
    int[] colors = {255, 255, 0};
    private boolean hasWon = false;
    private int levelIndex = 0;
    private boolean buttonPressed = false;


    public Information(Float visX, Float visY,  Key key, int[] col) {
        myX = visX;
        myY = visY;
        this.key = key;
        colors = col;
    }

    public boolean hasWon() {
        return hasWon;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }
    
    public void setWon(boolean hasWon) {
        this.hasWon = hasWon;
    }

    public boolean isButtonPressed() {
        return buttonPressed;
    }

    public void setButtonPressed(boolean pressed) {
        this.buttonPressed = pressed;
    }


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void changeInfo(Float visX, Float visY, Key key, int[] col) {
        myX = visX;
        myY = visY;
        this.key = key;
        colors = col;
    }

    public Float getMyX() {
        return myX;
    }

    public Float getMyY() {
        return myY;
    }

    public int[] getColors() {
        return colors;
    }

    public Key getKey() {
        return key;
    }

    public ArrayList<Object> getData() {
        ArrayList<Object> ret = new ArrayList<>();
        ret.add(myX);
        ret.add(myY);
        ret.add(key);
        ret.add(colors);

        return ret;
    }
}
