package model;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import view.GameBoard;

import java.awt.Color;

public class EnemyComposite extends GameElement {

    public static final int NROWS = 2;
    public static final int NCOLS = 10;
    public static final int ENEMY_SIZE = 20;
    public static final int UNIT_MOVE = 5;

    private ArrayList<ArrayList<GameElement>> rows;
    private ArrayList<GameElement> bombs;
    private boolean movingToRight = true;
    private Random random = new Random();

    public EnemyComposite() {
        rows = new ArrayList<>();
        bombs = new ArrayList<>();

        for (int r = 0; r < NROWS; r++){
            var oneRow = new ArrayList<GameElement>();
            rows.add(oneRow);
            for (int c = 0; c < NCOLS; c++){
                oneRow.add(new Enemy(c * ENEMY_SIZE * 2, r  * ENEMY_SIZE * 2, ENEMY_SIZE, Color.yellow, true));
            }
        }
    }

    @Override
    public void render(Graphics2D g2) {

        // render enemy array 
        for (var r: rows){
            for (var e: r){
                e.render(g2);
            }
        }

        // render bomnbs
        for (var b: bombs){
            b.render(g2);
        }
        
    }

    @Override
    public void animate() {
        int dx = UNIT_MOVE;
        int dy = ENEMY_SIZE; // for moving along y axis
        if (movingToRight){
            if(rightEnd() >= GameBoard.WIDTH){
                dx = -dx;
                movingToRight = false;
                dy = -dy;
                yMovement(dy);
            
            }
        } else {
            dx = -dx;
            if (leftEnd() <= 0){
                dx =-dx;
                movingToRight = true;
                dy = -dy;
                yMovement(dy);

            }
        }

        // update x loc
        for (var row: rows){
            for (var e: row) {
                e.x += dx;
                
            }
        }

       
        

        // animate bombs
        for (var b: bombs){
            b.animate();
        }

        
        
    } // end of animate method

    private int rightEnd(){
        int xEnd = -100;
        for (var row: rows){
            if (row.size() == 0) continue;
            int x = row.get(row.size() - 1).x + ENEMY_SIZE;
            if(x >xEnd) xEnd = x;
        }

        return xEnd;
    }

    private int leftEnd(){
        int xEnd = 9000;
        for(var row: rows){
            if(row.size() == 0) continue;
            int x = row.get(0).x;
            if (x < xEnd) xEnd = x;
        }

        return xEnd;
    }

    public void dropBombs() {
        for (var row: rows){
            for (var e: row) {
                if (random.nextFloat() < 0.1F) {
                    bombs.add(new Bomb(e.x, e.y));
                }
            }
        }
    }

    public void yMovement(int dy){
        // update y for rows to lower enemy when it hits a wall
        for (var row: rows){
            for (var e: row) {
                e.y -= dy;
            }
        }
    }

    public void removeBombsOutOfBound() {
        var remove = new ArrayList<GameElement>();
        for ( var b: bombs) {
            if (b.y >= GameBoard.HEIGHT) {
                remove.add(b);
            }
        }

        bombs.removeAll(remove);
    }

    public void processCollision(Shooter shooter){

        var removeBullets = new ArrayList<GameElement>();

        // bullets vs enemies

        for (var row: rows) {
            var removeEnemies = new ArrayList<GameElement>();
            for(var enemy: row){
                for(var bullet: shooter.getWeapons()) {
                    if (enemy.collideWith(bullet)) {
                        removeBullets.add(bullet);
                        removeEnemies.add(enemy);
                    }

                    // i think here is where we will process the collision of shooter and bullets
                    // if (shooter.collideWith(bullet)) we follow similar as to removeBullets
                }
            }
            row.removeAll(removeEnemies);
        }
        shooter.getWeapons().removeAll(removeBullets);

        // bulets vs bombs
        var removeBombs = new ArrayList<GameElement>();
        removeBullets.clear();

        for (var b: bombs){
            for (var bullet: shooter.getWeapons()) {
                if (b.collideWith(bullet)) {
                    removeBombs.add(b);
                    removeBullets.add(bullet);
                }
            }
        }

        shooter.getWeapons().removeAll(removeBullets);
        bombs.removeAll(removeBombs);
    }
    
}
