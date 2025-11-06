/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tron_game.main;

/**
 *
 * @author ASUS
 */
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
public class GamePanel extends JPanel implements Runnable{
    
    //screen
    final int originalTileSize=16;//character n npc size
    final int scale=3;//in screen
    
    final int tileSize=originalTileSize*scale;
    
    final int maxScreenCol=16;
    final int maxScreenRow=12;
    final int screenWidth=tileSize*maxScreenCol;
    final int screenHeight=tileSize*maxScreenRow;
    
    Thread gameThread;//repeat program
    
    public GamePanel(){
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
    }
    
    public void startGameThread(){
        gameThread=new Thread(this);
        gameThread.start();
    }
    
    //game loop
    public void run(){
        
    }

}
