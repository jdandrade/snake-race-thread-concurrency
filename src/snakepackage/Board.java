package snakepackage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import enums.Grid_Size;

public class Board extends JLabel implements Observer {

	private static final long serialVersionUID = 1L;
	public static final int NR_BARRIERS = 5;
	public static final int NR_JUMP_PADS = 2;
	public static final int NR_TURBO_BOOSTS = 2;
	public static final int NR_FOOD = 5;
	static Cell[] food = new Cell[NR_FOOD];
	static Cell[] barriers = new Cell[NR_BARRIERS];
	static Cell[] jump_pads = new Cell[NR_JUMP_PADS];
	static Cell[] turbo_boosts = new Cell[NR_TURBO_BOOSTS];
	static int[] result = new int[SnakeApp.MAX_THREADS];
	Random random = new Random();
	static Cell[][] gameboard = new Cell[Grid_Size.GRID_WIDTH][Grid_Size.GRID_HEIGHT];

	@SuppressWarnings("unused")
	public Board() {
		if ((NR_BARRIERS + NR_JUMP_PADS + NR_FOOD + NR_TURBO_BOOSTS) > Grid_Size.GRID_HEIGHT
				* Grid_Size.GRID_WIDTH)
			throw new IllegalArgumentException(); 
		GenerateBoard();
		GenerateFood();
		GenerateBarriers();
		GenerateJumpPads();
		GenerateTurboBoosts();
	}

	private void GenerateTurboBoosts() {
		for (int i = 0; i != NR_TURBO_BOOSTS; i++) {
			Cell tmp = gameboard[random.nextInt(Grid_Size.GRID_WIDTH)][random
					.nextInt(Grid_Size.GRID_HEIGHT)];
			if (!tmp.hasElements()) {
				turbo_boosts[i] = tmp;
				turbo_boosts[i].setTurbo_boost(true);
			} else {
				i--;
			}
		}
	}

	private void GenerateJumpPads() {
		for (int i = 0; i != NR_JUMP_PADS; i++) {
			Cell tmp = gameboard[random.nextInt(Grid_Size.GRID_WIDTH)][random
					.nextInt(Grid_Size.GRID_HEIGHT)];
			if (!tmp.hasElements()) {
				jump_pads[i] = tmp;
				jump_pads[i].setJump_pad(true);
			} else {
				i--;
			}
		}
	}

	private void GenerateBoard() {
		for (int i = 0; i != Grid_Size.GRID_WIDTH; i++) {
			for (int j = 0; j != Grid_Size.GRID_HEIGHT; j++) {
				gameboard[i][j] = new Cell(i, j);
				System.out.println(" ins " + gameboard[i][j]);
			}
		}

	}

	private void GenerateBarriers() {
		for (int i = 0; i != NR_BARRIERS; i++) {
			Cell tmp = gameboard[random.nextInt(Grid_Size.GRID_WIDTH)][random
					.nextInt(Grid_Size.GRID_HEIGHT)];
			if (!tmp.hasElements()) {
				barriers[i] = tmp;
				barriers[i].setBarrier(true);
			} else {
				i--;
			}
		}
	}

	private void GenerateFood() {
		for (int i = 0; i != NR_FOOD; i++) {
			Cell tmp = gameboard[random.nextInt(Grid_Size.GRID_WIDTH)][random
					.nextInt(Grid_Size.GRID_HEIGHT)];
			if (!tmp.hasElements()) {
				food[i] = tmp;
				food[i].setFood(true);
			} else {
				i--;
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawGrid(g);
		drawSnake(g);
		drawFood(g);
		drawBarriers(g);
		drawJumpPads(g);
		drawTurboBoosts(g);
	}

	private void drawTurboBoosts(Graphics g) {
		Image light = null;
		URL resource = getClass().getResource("../Img/lightning.png");
		try {
			light = ImageIO.read(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Cell c : turbo_boosts) {
			g.drawImage(light, c.getX() * Grid_Size.WIDTH_BOX, c.getY()
					* Grid_Size.HEIGH_BOX, this);
		}
	}

	private void drawJumpPads(Graphics g) {
		Image jump = null;
		URL resource = getClass().getResource("../Img/up.png");
		try {
			jump = ImageIO.read(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Cell c : jump_pads) {
			g.drawImage(jump, c.getX() * Grid_Size.WIDTH_BOX, c.getY()
					* Grid_Size.HEIGH_BOX, this);
		}
		// g.setColor(Color.ORANGE);
		// for (Cell c : jump_pads) {
		// g.fillOval(c.getX() * Grid_Size.WIDTH_BOX, c.getY()
		// * Grid_Size.HEIGH_BOX, Grid_Size.WIDTH_BOX,
		// Grid_Size.HEIGH_BOX);
		// }
	}

	private void drawBarriers(Graphics g) {

		Image firewall = null;
		URL resource = getClass().getResource("../Img/firewall.png");
		try {
			firewall = ImageIO.read(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Cell c : barriers) {
			g.drawImage(firewall, c.getX() * Grid_Size.WIDTH_BOX, c.getY()
					* Grid_Size.HEIGH_BOX, this);
			// g.fillRect(c.getX() * Grid_Size.WIDTH_BOX, c.getY()
			// * Grid_Size.HEIGH_BOX, Grid_Size.WIDTH_BOX,
			// Grid_Size.HEIGH_BOX);
		}
	}

	private void drawFood(Graphics g) {
		Image mouse = null;
		URL resource = getClass().getResource("../Img/mouse.png");
		try {
			mouse = ImageIO.read(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Cell c : food){
		g.drawImage(mouse, c.getX() * Grid_Size.WIDTH_BOX, c.getY()
				* Grid_Size.HEIGH_BOX, this);
		}
	}

	private void drawSnake(Graphics g) {
		for (int i = 0; i != SnakeApp.MAX_THREADS; i++) {
			for (Cell p : SnakeApp.getApp().snakes[i].getBody()) {
				if (p.equals(SnakeApp.getApp().snakes[i].getBody().peekFirst())) {
					g.setColor(new Color(050, 205, 050));
					g.fillRect(p.getX() * Grid_Size.WIDTH_BOX, p.getY()
							* Grid_Size.HEIGH_BOX, Grid_Size.WIDTH_BOX,
							Grid_Size.HEIGH_BOX);
				} else {
					if (SnakeApp.getApp().snakes[i].isSelected()) {
						g.setColor(new Color(032, 178, 170));
					} else
						g.setColor(new Color(034, 139, 034));
					g.fillRect(p.getX() * Grid_Size.WIDTH_BOX, p.getY()
							* Grid_Size.HEIGH_BOX, Grid_Size.WIDTH_BOX,
							Grid_Size.HEIGH_BOX);
				}
			}
		}

	}

	private void drawGrid(Graphics g) {
		g.setColor(new Color(255, 250, 250));
		g.fillRect(0, 0, Grid_Size.GRID_WIDTH * Grid_Size.WIDTH_BOX,
				Grid_Size.GRID_HEIGHT * Grid_Size.HEIGH_BOX);
		g.setColor(new Color(135, 135, 135));
		g.drawRect(0, 0, Grid_Size.GRID_WIDTH * Grid_Size.WIDTH_BOX,
				Grid_Size.GRID_HEIGHT * Grid_Size.HEIGH_BOX);
		for (int i = Grid_Size.WIDTH_BOX; i < Grid_Size.GRID_WIDTH
				* Grid_Size.WIDTH_BOX; i += Grid_Size.WIDTH_BOX) {
			g.drawLine(i, 0, i, Grid_Size.GRID_HEIGHT * Grid_Size.HEIGH_BOX);
		}
		for (int i = Grid_Size.HEIGH_BOX; i < Grid_Size.GRID_HEIGHT
				* Grid_Size.HEIGH_BOX; i += Grid_Size.HEIGH_BOX) {
			g.drawLine(0, i, Grid_Size.GRID_WIDTH * Grid_Size.WIDTH_BOX, i);
		}

	}

	@Override
	public void update(Observable arg0, Object arg1) {
		repaint();
	}
}
