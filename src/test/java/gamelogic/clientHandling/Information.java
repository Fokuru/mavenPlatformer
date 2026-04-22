package gamelogic.clientHandling;

import java.util.ArrayList;

import gamelogic.key.Key;

public class Information {
    Float myX;
    Float myY;
    int myHitX;
    int myHitY;
    Key key;
    int[] colors = {255, 255, 0};

    public Information(Float visX, Float visY, int hitX, int hitY, Key key, int[] col) {
        myX = visX;
        myY = visY;
        myHitX = hitX;
        myHitY = hitY;
        this.key=key;
        colors = col;
    }

    public void changeInfo(Float visX, Float visY, int hitX, int hitY, Key key, int[] col) {
        myX = visX;
        myY = visY;
        myHitX = hitX;
        myHitY = hitY;
        this.key = key;
        colors = col;
    }

    public ArrayList<Object> getData() {
        ArrayList<Object> ret = new ArrayList<>();
        ret.add(myX);
        ret.add(myY);
        ret.add(myHitX);
        ret.add(myHitY);
        ret.add(key);
        ret.add(colors);

        return ret;
    }
}
