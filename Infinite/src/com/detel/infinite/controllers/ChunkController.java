package com.detel.infinite.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.detel.infinite.models.*;

import java.util.Random;

/**
 * Created by Derick on 3/4/14.
 */
public class ChunkController {

    private World world;

    private final static int possibleChunks = 4;

    private static final int SIZE_X = 40;
    private static final int SIZE_Y = 40;

    public ChunkController(World world){
        this.world = world;
    }

    public Chunk generateChunk(int i){

        String filename;

        Random rand = new Random();
        int randomNumber = rand.nextInt(possibleChunks);

        switch(randomNumber){
            case(0):

               filename = "data/chunk1.txt";
                break;

            case(1):
                //creates a chunk from the data located in chunk2.txt
                filename = "data/chunk2.txt";
                break;
            case(2):
                //creates a chunk from the data located in chunk3.txt
                filename = "data/chunk3.txt";
                break;
            /*case(3):
                //creates a chunk from the data located in chunk4.txt
                filename = "data/chunk4.txt";
                break;*/
            default:
                //creates a chunk from the default chunk if all else fails, chunk data located in chunk1.txt
                filename = "data/chunk1.txt";
                break;
        }

        return generateChunkFromFile(filename, i);

    }

    public Chunk generateFirstChunk(){
        Block[][] blocks = new Block[SIZE_X][SIZE_Y];

        //creates a "full" chunk
        for(int x = 0; x < SIZE_X; x++){
            for(int y = 0; y< SIZE_Y; y++){
                blocks[x][y] = new Block(x,y);
            }
        }

        return new Chunk(blocks);

    }

    public Chunk generateSecondChunk(){
        Block[][] blocks = new Block[SIZE_X][SIZE_Y];

        //creates a flat chunk with ground at 3
        for(int x = 0; x < SIZE_X; x++){
            for(int y = 0; y < SIZE_Y; y++){
                if(y < 3)blocks[x][y] = new Block(x + SIZE_X,y);
                else blocks[x][y] = new BlockAir(x + SIZE_X ,y);
            }
        }

        return new Chunk(blocks);
    }

    public Chunk generateChunkFromFile(String fileName, int i){

        Block[][] blocks = new Block[SIZE_X][SIZE_Y];

        FileHandle handle;
        String text;
        int stringIndex;

        //creates a chunk from the data located in fileName
        handle = Gdx.files.internal(fileName);
        text = handle.readString();
        stringIndex = 0;
        for(int x = 0; x < SIZE_X; x++){
            for(int y = 0; y < SIZE_Y; y++){
                if(text.charAt(stringIndex) == '\r') stringIndex++;
                if(text.charAt(stringIndex) == '\n') stringIndex++;

                if (text.charAt(stringIndex) == '2'){
                    blocks[x][y] = new BlockAir(x + SIZE_X * i,y);
                    world.addEnemy(new Enemy(x + SIZE_X * world.getChunks().size(),y));
                } else if(text.charAt(stringIndex) == '1')blocks[x][y] = new Block(x + SIZE_X * i,y);
                else if(text.charAt(stringIndex) == '0') blocks[x][y] = new BlockAir(x + SIZE_X * i,y);
                stringIndex++;
            }
        }

        return new Chunk(blocks);
    }
}
