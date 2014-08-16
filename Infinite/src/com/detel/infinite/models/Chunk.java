package com.detel.infinite.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Created by Derick on 2/22/14.
 */
public class Chunk {


    private static final int SIZE_X = 40;
    private static final int SIZE_Y = 40;

    private Block[][] blocks = new Block[SIZE_X][SIZE_Y];

    public Chunk(Block[][] blocks){
        this.blocks = blocks;
    }

    public Block[][] getBlocks() {
        return blocks;
    }

    public static Vector2 getSIZE(){
        return new Vector2(SIZE_X,SIZE_Y);
    }
}
