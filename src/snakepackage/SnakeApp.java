package snakepackage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import enums.Grid_Size;

/**
 * @author jd-
 *
 */
public class SnakeApp {
	private static SnakeApp app;
	public static final int MAX_THREADS = 8;
	Snake[] snakes = new Snake[MAX_THREADS];
	private static final Cell[] spawn = {
			new Cell(1, (Grid_Size.GRID_HEIGHT / 2) / 2),
			new Cell(Grid_Size.GRID_WIDTH - 2,
					3 * (Grid_Size.GRID_HEIGHT / 2) / 2),
			new Cell(3 * (Grid_Size.GRID_WIDTH / 2) / 2, 1),
			new Cell((Grid_Size.GRID_WIDTH / 2) / 2, Grid_Size.GRID_HEIGHT - 2),
			new Cell(1, 3 * (Grid_Size.GRID_HEIGHT / 2) / 2),
			new Cell(Grid_Size.GRID_WIDTH - 2, (Grid_Size.GRID_HEIGHT / 2) / 2),
			new Cell((Grid_Size.GRID_WIDTH / 2) / 2, 1),
			new Cell(3 * (Grid_Size.GRID_WIDTH / 2) / 2,
					Grid_Size.GRID_HEIGHT - 2) };
	private JFrame frame;
	private static Board board;
	int nr_selected = 0;
	Thread[] thread = new Thread[MAX_THREADS];

	public SnakeApp() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		frame = new JFrame("The Snake Race");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setSize(618, 640);
		frame.setSize(Grid_Size.GRID_WIDTH * Grid_Size.WIDTH_BOX + 17,
				Grid_Size.GRID_HEIGHT * Grid_Size.HEIGH_BOX + 40);
		frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
				dimension.height / 2 - frame.getHeight() / 2);
		board = new Board();
		frame.add(board);

		board.addMouseListener(new MouseListener() {

			@Override
			public void mousePressed(MouseEvent arg0) {

				Cell c = new Cell(arg0.getX() / Grid_Size.WIDTH_BOX, arg0
						.getY() / Grid_Size.HEIGH_BOX);
				System.out.println("click -> " + c.toString());

				if (SwingUtilities.isRightMouseButton(arg0)) {
					for (int i = 0; i != MAX_THREADS; i++) {
						for (int j = 0; j != snakes[i].getBody().size(); j++) {
							if (snakes[i].getBody().get(j).getX() == c.getX()
									&& snakes[i].getBody().get(j).getY() == c
											.getY()) {
								snakes[i].setSelected(true);
								System.out.println("[" + snakes[i].getIdt()
										+ "] was selected!");
								for (int x = 0; x != MAX_THREADS; x++) {
									if (x != i)
										snakes[x].setSelected(false);
								}

							}
						}
					}
				} else if (SwingUtilities.isLeftMouseButton(arg0)) {
					for (int i = 0; i != MAX_THREADS; i++) {
						if (snakes[i].isSelected()) {
							if (thread[i].getState() == Thread.State.WAITING) {
								snakes[i].setObjective(c);
								thread[i].interrupt();
							}
							snakes[i].setObjective(c);
						}
					}
				}

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}
		});

	}

	public static void main(String[] args) {
		app = new SnakeApp();
		app.init();
	}

	private void init() {
		for (int i = 0; i != MAX_THREADS; i++) {
			snakes[i] = new Snake(i + 1, spawn[i], i + 1);
			snakes[i].addObserver(board);
			thread[i] = new Thread(snakes[i]);
			thread[i].start();
		}

		frame.setVisible(true);

		while (true) {
			int x = 0;
			for (int i = 0; i != MAX_THREADS; i++) {
				if (snakes[i].end == true)
					x++;
			}
			if (x == MAX_THREADS)
				break;
		}

		System.out.println("END OF GAME");

		System.out.println("RESULT");
		for (int i = 0; i != MAX_THREADS; i++) {
			System.out.println("#" + (i + 1) + " - " + Board.result[i]);

		}

	}

	public static SnakeApp getApp() {
		return app;
	}

}
