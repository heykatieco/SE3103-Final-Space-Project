package model;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import view.GameBoard;
import view.TextDraw;

import java.awt.Color;

public class EnemyComposite extends GameElement {

    public static final int NROWS = 2;
    public static final int NCOLS = 10;
    public static final int ENEMY_SIZE = 20;
    public static final int UNIT_MOVE = 5;
    public static final int POINTS = 10;

    private ArrayList<ArrayList<GameElement>> rows;
    private ArrayList<GameElement> bombs;
    
    private boolean movingToRight = true;
    private Random random = new Random();
    private int score = 50;

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

        // render bombs
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
                if (e.y >= GameBoard.HEIGHT) {

                    System.exit(0);
                    // the logic works but the canvas isn't clearing yet
                    // here is where we want the game to end and display you lose
                }
            }
        }

        // if y reaches the bottom of the screen where x = 0, then game over

        
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
                        // this is the right place to calc score with hits
                        score += POINTS;
                    System.out.println(score);

                    }
                    
                
                    
                    
                    
                }
            }
            row.removeAll(removeEnemies);
            
        }
        shooter.getWeapons().removeAll(removeBullets);

        // bullets vs bombs
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
    public void setScore(int score) {
        this.score = score;
    }
    public int getScore() {
        return score;
    }
    public String displayScore(){
        String display = "Score: " + score;
        return display;

        
    }
    
}
