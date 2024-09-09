import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //Images
    Image backgroundImg;
    Image birdImg;
    Image toppipeImg;
    Image bottompipeImg;

    //Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdheight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdheight;
        Image img;

        Bird(Image img) {
        this.img = img;
        }
    }

    //pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;  //scaled by 1/6
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    //Game Logic
    Bird bird;
    int velocityX = -4; //move pipes to the left speed(simulates bird moving to right )
    int velocityY = -9; // move bird up/down speed
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placepipesTimer;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load Images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        toppipeImg = new ImageIcon(getClass().getResource("toppipe.png")).getImage();
        bottompipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placepipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                placepipes();
            }
        });
        placepipesTimer.start();
       
        //game timer
        gameLoop =new Timer(1000/60, this); //1000/60 = 16.6
        gameLoop.start();

    }

    public void placepipes() {
        //(0-1) * pipeHeight/2 -> (0-256)
        //128
        //(0-128) - (0-256) --> 1/4 of pipeHeight -> 3/4 pipeHeight

        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(toppipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottompipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g) {
         //background
         g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

         //bird
         g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height,null);

         //pipes
         for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
         }

         //score
         g.setColor(Color.white);
         g.setFont(new Font("Arial", Font.PLAIN, 32));
         if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);            
         }
         else {
            g.drawString(String.valueOf((int) score), 10, 35);
         }
    }
    
    public void move() {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX; 

            if(!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; //0.5 because there are 2 pipes ! so 0.5*2 = 1 , 1 for each set of pipes 
            }
            
            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        
        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && //a's top left corner doesn't reach top right corner
               a.x + a.width > b.x && //a's top right corner passes b's top left corner
               a.y < b.y + b.height && //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;   //a's bottom left corner passes b's top left corner
               

    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placepipesTimer.stop();
            gameLoop.stop();
        }
        }

   

    @Override
    public void keyPressed(KeyEvent e) {
         if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) { 
                //restrat the game by resetting the conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placepipesTimer.start();
            }
         }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
} 
