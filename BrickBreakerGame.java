import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BrickBreakerGame extends JPanel implements ActionListener {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 10;
    private static final int BALL_DIAMETER = 20;
    private static final int BRICK_WIDTH = 60;
    private static final int BRICK_HEIGHT = 20;
    private static final int NUM_BRICKS = 65;
    private static final int DELAY = 10;
    
    private int paddleX;
    private int ballX, ballY, ballXDir, ballYDir;
    private int score;
    private boolean playing;
    private Timer timer;
    private int[] brickX, brickY;
    private boolean[] brickExists;

    public BrickBreakerGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT && paddleX > 0) {
                    paddleX -= 20;
                } else if (key == KeyEvent.VK_RIGHT && paddleX < WIDTH - PADDLE_WIDTH) {
                    paddleX += 20;
                }
                repaint();
            }
        });

        initGame();
    }

    private void initGame() {
        paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
        ballX = WIDTH / 2 - BALL_DIAMETER / 2;
        ballY = HEIGHT - PADDLE_HEIGHT - BALL_DIAMETER;
        ballXDir = 1;
        ballYDir = -1;
        score = 0;
        playing = true;
        
        brickX = new int[NUM_BRICKS];
        brickY = new int[NUM_BRICKS];
        brickExists = new boolean[NUM_BRICKS];
        int brickIndex = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 20; col++) {
                brickX[brickIndex] = col * BRICK_WIDTH + 30;
                brickY[brickIndex] = row * BRICK_HEIGHT + 50;
                brickExists[brickIndex] = true;
                brickIndex++;
            }
        }

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(paddleX, HEIGHT - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);

        g2d.setColor(Color.RED);
        g2d.fillOval(ballX, ballY, BALL_DIAMETER, BALL_DIAMETER);

        g2d.setColor(Color.GREEN);
        for (int i = 0; i < NUM_BRICKS; i++) {
            if (brickExists[i]) {
                g2d.fillRect(brickX[i], brickY[i], BRICK_WIDTH, BRICK_HEIGHT);
            }
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 20, 30);

        if (!playing) {
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            g2d.drawString("Game Over!", WIDTH / 2 - 100, HEIGHT / 2 - 20);
        }
    }

    private void moveBall() {
        ballX += ballXDir;
        ballY += ballYDir;

        if (ballX <= 0 || ballX >= WIDTH - BALL_DIAMETER) {
            ballXDir = -ballXDir;
        }
        if (ballY <= 0) {
            ballYDir = -ballYDir;
        }
        if (ballY >= HEIGHT - BALL_DIAMETER - PADDLE_HEIGHT) {
            if (ballX >= paddleX && ballX <= paddleX + PADDLE_WIDTH) {
                ballYDir = -ballYDir;
                score += 10;
            } else {
                playing = false;
                timer.stop();
            }
        }
        
        for (int i = 0; i < NUM_BRICKS; i++) {
            if (brickExists[i] && ballX + BALL_DIAMETER >= brickX[i] && ballX <= brickX[i] + BRICK_WIDTH 
                    && ballY + BALL_DIAMETER >= brickY[i] && ballY <= brickY[i] + BRICK_HEIGHT) {
                ballYDir = -ballYDir;
                brickExists[i] = false;
                score += 20;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (playing) {
            moveBall();
            repaint();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick Breaker Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new BrickBreakerGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
