/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tron_game.main;

/**
 *
 * @author ASUS
 */
import javax.swing.JFrame;
public class Main{
    
    public static void main(String[]args){
        
        JFrame window=new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //close window properly
        window.setResizable(false);
        window.setTitle("JAVA TRON GAME");
        
        GamePanel gamepanel=new GamePanel();
        window.add(gamepanel);
        
        window.pack();
        
        window.setLocationRelativeTo(null);//window at centre location
        window.setVisible(true);
    
        
    }
    
}
