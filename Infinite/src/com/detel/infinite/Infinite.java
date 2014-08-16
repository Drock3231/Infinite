package com.detel.infinite;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.detel.infinite.screens.ScreenGame;
import com.detel.infinite.screens.ScreenMainMenu;

public class Infinite extends Game {

    @Override
    public void create() {
        setScreen(new ScreenMainMenu(this));
    }
}
