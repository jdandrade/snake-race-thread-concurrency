package snakepackage;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Random;

import enums.Direction;
import enums.Grid_Size;

public class Snake extends Observable implements Runnable {

	private int idt;
	private Cell head;
	private Cell newCell;
	private LinkedList<Cell> snakeBody = new LinkedList<Cell>();
	private Cell objective = null;
	private Cell start = null;

	private int direction = Direction.NO_DIRECTION;
	private final int INIT_SIZE = 3;

	private boolean hasTurbo = false;
	private int jumps = 0;
	private boolean isSelected = false;
	private int growing = 0;
	public boolean goal = false;
	public boolean end = false;

	public Snake(int idt, Cell head, int direction) {
		this.idt = idt;
		this.direction = direction;
		generateSnake(head);

	}

	private void generateSnake(Cell head) {
		start = head;
		Board.gameboard[head.getX()][head.getY()].reserveCell(jumps, idt);
		snakeBody.add(head);
		growing = INIT_SIZE - 1;
	}

	@Override
	public void run() {
		try {
			try {
				while (!Thread.interrupted()) {
					try {
						snakeCalc();

						setChanged();
						notifyObservers();

						if (hasTurbo == true)
							Thread.sleep(500 / 3);
						else
							Thread.sleep(500);
					} catch (InterruptedException e) {
						if (goal == true)
							throw new IllegalArgumentException();
						fixDirection(head);
					}

				}
			} catch (IllegalArgumentException e) {
				for (Cell c : snakeBody) {
					c.freeCell();
				}
				snakeBody.clear();

				setChanged();
				notifyObservers();
				System.out.println("[" + this.idt + "] "
						+ "HAS FINISHED THE RACE!");
				for (int i = 0; i != SnakeApp.MAX_THREADS; i++) {
					if (Board.result[i] == 0) {
						Board.result[i] = this.idt;
						break;
					}
				}
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			// out of bounds
			for (Cell c : snakeBody) {
				c.freeCell();
			}
			snakeBody.clear();

			setChanged();
			notifyObservers();
		}

		end = true;
	}

	private void snakeCalc() {
		head = snakeBody.peekFirst();

		newCell = head;

		newCell = changeDirection(newCell);

		newCell = avoidOwnBody(newCell);
		System.out.println("[" + this.idt + "] -> " + head.toString());

		jumps = Board.gameboard[newCell.getX()][newCell.getY()].reserveCell(
				jumps, idt);

		if (objective != null)
			searchObjective(objective);
		else
			randomMovement(newCell);

		checkIfFood(newCell);
		checkIfJumpPad(newCell);
		checkIfTurboBoost(newCell);
		checkIfBarrier(newCell);
		if (objective != null)
			checkIfObjective(newCell);
		checkIfGoal(newCell);

		// movement
		snakeBody.push(newCell);

		if (growing <= 0) {
			newCell = snakeBody.peekLast();
			snakeBody.remove(snakeBody.peekLast());
			Board.gameboard[newCell.getX()][newCell.getY()].freeCell();
		} else if (growing != 0)
			growing--;

	}

	private void checkIfBarrier(Cell newCell) {
		if (Board.gameboard[newCell.getX()][newCell.getY()].isBarrier()) {
			// crash
			System.out.println("[" + idt + "] " + "CRASHED AGAINST BARRIER "
					+ newCell.toString());
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	private void checkIfGoal(Cell newCell) {
		if (start.getX() == 1 && head.getX() == Grid_Size.GRID_WIDTH - 1) {
			goal = true;
			Thread.currentThread().interrupt();
		} else if (start.getY() == 1
				&& head.getY() == Grid_Size.GRID_HEIGHT - 1) {
			goal = true;
			Thread.currentThread().interrupt();
		} else if (start.getX() == Grid_Size.GRID_WIDTH - 2 && head.getX() == 0) {
			goal = true;
			Thread.currentThread().interrupt();
		} else if (start.getY() == Grid_Size.GRID_HEIGHT - 2
				&& head.getY() == 0) {
			goal = true;
			Thread.currentThread().interrupt();
		}
	}

	private Cell avoidOwnBody(Cell newCell) {
		while (checkIfOwnBody(newCell) == true) {
			newCell = fixDirection(newCell);
		}
		return newCell;
	}

	private Cell fixDirection(Cell newCell) {

		// revert movement
		if (direction == Direction.LEFT
				&& head.getX() + 1 < Grid_Size.GRID_WIDTH) {
			newCell = Board.gameboard[head.getX() + 1][head.getY()];
		} else if (direction == Direction.RIGHT && head.getX() - 1 >= 0) {
			newCell = Board.gameboard[head.getX() - 1][head.getY()];
		} else if (direction == Direction.UP
				&& head.getY() + 1 < Grid_Size.GRID_HEIGHT) {
			newCell = Board.gameboard[head.getX()][head.getY() + 1];
		} else if (direction == Direction.DOWN && head.getY() - 1 >= 0) {
			newCell = Board.gameboard[head.getX()][head.getY() - 1];
		}

		randomMovement(newCell);
		return newCell;
	}

	private boolean checkIfOwnBody(Cell newCell) {
		for (Cell c : snakeBody) {
			if (newCell.getX() == c.getX() && newCell.getY() == c.getY()) {
				return true;
			}
		}
		return false;

	}

	private void checkIfObjective(Cell newCell) {
		if (objective.getX() == newCell.getX()
				&& objective.getY() == newCell.getY()) {
			System.out.println("Objective reached");
			objective = null;
		}
	}

	private void randomMovement(Cell newCell) {
		Random random = new Random();
		int tmp = random.nextInt(4) + 1;
		if (tmp == Direction.LEFT && !(direction == Direction.RIGHT)) {
			direction = tmp;
		} else if (tmp == Direction.UP && !(direction == Direction.DOWN)) {
			direction = tmp;
		} else if (tmp == Direction.DOWN && !(direction == Direction.UP)) {
			direction = tmp;
		} else if (tmp == Direction.RIGHT && !(direction == Direction.LEFT)) {
			direction = tmp;
		}
	}

	private void checkIfTurboBoost(Cell newCell) {
		if (Board.gameboard[newCell.getX()][newCell.getY()].isTurbo_boost()) {
			// get turbo_boost
			for (int i = 0; i != Board.NR_TURBO_BOOSTS; i++) {
				if (Board.turbo_boosts[i] == newCell) {
					Board.turbo_boosts[i].setTurbo_boost(false);
					Board.turbo_boosts[i] = new Cell(-5, -5);
					hasTurbo = true;
				}

			}
			System.out.println("[" + idt + "] " + "GETTING TURBO BOOST "
					+ newCell.toString());
		}
	}

	private void checkIfJumpPad(Cell newCell) {

		if (Board.gameboard[newCell.getX()][newCell.getY()].isJump_pad()) {
			// get jump_pad
			for (int i = 0; i != Board.NR_JUMP_PADS; i++) {
				if (Board.jump_pads[i] == newCell) {
					Board.jump_pads[i].setJump_pad(false);
					Board.jump_pads[i] = new Cell(-5, -5);
					this.jumps++;
				}

			}
			System.out.println("[" + idt + "] " + "GETTING JUMP PAD "
					+ newCell.toString());
		}
	}

	private void checkIfFood(Cell newCell) {
		Random random = new Random();

		if (Board.gameboard[newCell.getX()][newCell.getY()].isFood()) {
			// eat food
			growing += 3;
			int x = random.nextInt(Grid_Size.GRID_HEIGHT);
			int y = random.nextInt(Grid_Size.GRID_WIDTH);

			System.out.println("[" + idt + "] " + "EATING "
					+ newCell.toString());

			for (int i = 0; i != Board.NR_FOOD; i++) {
				if (Board.food[i].getX() == newCell.getX()
						&& Board.food[i].getY() == newCell.getY()) {
					Board.gameboard[Board.food[i].getX()][Board.food[i].getY()]
							.setFood(false);

					while (Board.gameboard[x][y].hasElements()) {
						x = random.nextInt(Grid_Size.GRID_HEIGHT);
						y = random.nextInt(Grid_Size.GRID_WIDTH);
					}
					Board.food[i] = new Cell(x, y);
					Board.gameboard[x][y].setFood(true);
				}
			}
		}

	}

	private Cell changeDirection(Cell newCell) {
		// Avoid out of bounds

		while (direction == Direction.UP && (newCell.getY() - 1) < 0) {
			if ((head.getX() - 1) < 0)
				this.direction = Direction.RIGHT;
			else if ((head.getX() + 1) == Grid_Size.GRID_WIDTH)
				this.direction = Direction.LEFT;
			else
				randomMovement(newCell);
		}
		while (direction == Direction.DOWN
				&& (head.getY() + 1) == Grid_Size.GRID_HEIGHT) {
			if ((head.getX() - 1) < 0)
				this.direction = Direction.RIGHT;
			else if ((head.getX() + 1) == Grid_Size.GRID_WIDTH)
				this.direction = Direction.LEFT;
			else
				randomMovement(newCell);
		}
		while (direction == Direction.LEFT && (head.getX() - 1) < 0) {
			if ((newCell.getY() - 1) < 0)
				this.direction = Direction.DOWN;
			else if ((head.getY() + 1) == Grid_Size.GRID_HEIGHT)
				this.direction = Direction.UP;
			else
				randomMovement(newCell);
		}
		while (direction == Direction.RIGHT
				&& (head.getX() + 1) == Grid_Size.GRID_WIDTH) {
			if ((newCell.getY() - 1) < 0)
				this.direction = Direction.DOWN;
			else if ((head.getY() + 1) == Grid_Size.GRID_HEIGHT)
				this.direction = Direction.UP;
			else
				randomMovement(newCell);
		}

		switch (direction) {
		case Direction.UP:
			newCell = Board.gameboard[head.getX()][head.getY() - 1];
			break;
		case Direction.DOWN:
			newCell = Board.gameboard[head.getX()][head.getY() + 1];
			break;
		case Direction.LEFT:
			newCell = Board.gameboard[head.getX() - 1][head.getY()];
			break;
		case Direction.RIGHT:
			newCell = Board.gameboard[head.getX() + 1][head.getY()];
			break;
		}
		return newCell;
	}

	public void searchObjective(Cell objective) {

		Random random = new Random();

		// MOVE DIRECTLY TO OBJECTIVE
		if (direction == Direction.LEFT || direction == Direction.RIGHT) {
			if (direction == Direction.LEFT) {
				if (head.getY() > objective.getY())
					direction = Direction.UP;
				else if (head.getY() < objective.getY())
					direction = Direction.DOWN;
				else if (head.getY() == objective.getY()
						&& head.getX() < objective.getX())
					direction = random.nextInt(2) + 3;
			} else if (direction == Direction.RIGHT) {
				if (head.getY() > objective.getY())
					direction = Direction.UP;
				else if (head.getY() < objective.getY())
					direction = Direction.DOWN;
				else if (head.getY() == objective.getY()
						&& head.getX() > objective.getX())
					direction = random.nextInt(2) + 3;
			}
		} else if (direction == Direction.UP || direction == Direction.DOWN) {
			if (direction == Direction.UP) {
				if (head.getX() > objective.getX())
					direction = Direction.LEFT;
				else if (head.getX() < objective.getX())
					direction = Direction.RIGHT;
				else if (head.getX() == objective.getX()
						&& head.getY() < objective.getY())
					direction = random.nextInt(2) + 1;
			} else if (direction == Direction.DOWN) {
				if (head.getX() > objective.getX())
					direction = Direction.LEFT;
				else if (head.getX() < objective.getX())
					direction = Direction.RIGHT;
				else if (head.getX() == objective.getX()
						&& head.getY() > objective.getY())
					direction = random.nextInt(2) + 1;
			}
		}
	}

	public void setObjective(Cell c) {
		System.out.println("Setting objective - " + c.getX() + ":" + c.getY()
				+ " for Snake" + this.idt);
		this.objective = c;
	}

	public LinkedList<Cell> getBody() {
		return this.snakeBody;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getIdt() {
		return idt;
	}

}
