package controller;

import java.awt.event.ActionListener;
import java.util.LinkedList;

import model.Bullet;
import model.Shooter;
import view.GameBoard;

import java.awt.event.ActionEvent;

public class TimerListener implements ActionListener {

    public enum EventType {
        KEY_RIGHT, KEY_LEFT, KEY_SPACE
    }

    private GameBoard gameBoard;
    private LinkedList<EventType> eventQueue;
    private final int BOMB_DROP_FREQ = 20;
    private int frameCounter = 0;

    public TimerListener(GameBoard gameBoard){
        this.gameBoard = gameBoard;
        eventQueue = new LinkedList<>();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ++frameCounter;
        update();
        processEventQueue();
        processCollision();
        gameBoard.getCanvas().repaint();
        
    }
    
    private void update(){
        for( var e: gameBoard.getCanvas().getGameElements()){
            e.animate();
        }
    }

    private void processCollision() {
        var shooter = gameBoard.getShooter();
        var enemyComposite = gameBoard.getEnemyComposite();

       shooter.removeBulletsoutOfBound();
       enemyComposite.removeBombsOutOfBound();
       enemyComposite.processCollision(shooter);

    
    }

    private void processEventQueue(){
        while(!eventQueue.isEmpty()){
            var e = eventQueue.getFirst();
            eventQueue.removeFirst();
            Shooter shooter = gameBoard.getShooter();
            if (shooter == null) return; 

            switch(e) {
                case KEY_LEFT:  
                shooter.moveLeft();
                break;
                case KEY_RIGHT:  
                shooter.moveRight();
                break;
                case KEY_SPACE: 
                if (shooter.canFireMoreBullets())
                shooter.getWeapons().add(new Bullet(shooter.x, shooter.y)); 
                break;
            }
        }

        if (frameCounter == BOMB_DROP_FREQ) {
            gameBoard.getEnemyComposite().dropBombs();
            frameCounter = 0;
        }
    }

    public LinkedList<EventType> getEventQueue() {
        return eventQueue;
    }
}
