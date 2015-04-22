package snakepackage;

/**
 * Classe Cell
 * 
 * contem informacao sobre o estado da celula, 
 * se contem elementos,
 *  quais, 
 *  e trata da Sincronizacao e coordenacao no acesso � mesma
 * 
 * @author Joao Andrade 28445
 * @author Diana Pereira 38074
 */
public class Cell {

	/** X - Coordenada X da celula */
	private int x;
	
	/** Y - Coordenada Y da celula */
	private int y;
	
	/** full - Se Celula preenchida por uma cobra ou barreira */
	private boolean full;
	
	
	/** food - Se Celula preenchida por uma comida */
	private boolean food;
	
	/** jump_pad - Se Celula preenchida por um salto ao eixo*/
	private boolean jump_pad;
	
	/** turbo_boost - Se Celula preenchida por um turbo-boost */
	private boolean turbo_boost;
	
	private boolean barrier;

	
	/**
	 * Verifica se Celula tem turbo_boost.
	 *
	 * @return true, se tem turbo_boost
	 */
	public boolean isTurbo_boost() {
		return turbo_boost;
	}

	/**
	 * Poe turbo-boost na celula.
	 *
	 * @param turbo_boost � novo turbo-boost
	 */
	public void setTurbo_boost(boolean turbo_boost) {
		this.turbo_boost = turbo_boost;
	}

	/**
	 * Verifica se Celula tem comida.
	 *
	 * @return true, se tem comida
	 */
	public boolean isFood() {
		return food;
	}

	/**
	 * Poe comida na celula
	 *
	 * @param food � a nova comida
	 */
	public void setFood(boolean food) {
		this.food = food;
	}

	/**
	 * Verifica se Celula tem salto-ao-eixo.
	 *
	 * @return true, se tem salto-ao-eixo
	 */
	public boolean isJump_pad() {
		return jump_pad;
	}

	/**
	 * Poe salto-ao-eixo na celula
	 *
	 * @param jump_pad � o novo salto-ao-eixo
	 */
	public void setJump_pad(boolean jump_pad) {
		this.jump_pad = jump_pad;
	}

	/**
	 * Instancia a nova celula.
	 *
	 * @param x - x
	 * @param y - y
	 */
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Vai buscar o X da celula
	 *
	 * @return X
	 */
	public int getX() {
		return x;
	}

	/**
	 * Redefine o X
	 *
	 * @param x - x
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Vai buscar o Y da celula.
	 *
	 * @return y
	 */
	public int getY() {
		return y;
	}

	/**
	 * Redefine o Y.
	 *
	 * @param y - y
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Verifica se Celula est� cheia (com cobras ou barreiras)
	 *
	 * @return true, se estiver full
	 */
	public boolean isFull() {
		return full;
	}

	/**
	 * Poe a celula no estado Full
	 *
	 * @param full
	 */
	public void setFull(boolean full) {
		this.full = full;
	}

	/* 
	 * o toString da celula para imprimir na consola
	 */
	public String toString() {
		return "x: " + this.x + " y: " + this.y;

	}

	/**
	 * Reserva celula.
	 * 
	 * Verifica se cobra tem saltos, se tiver saltos ultrapassa ignora barreiras/cobras e atravessa
	 * Verifica se foi contra uma outra cobra/barreira - se for verdade fica em wait() at� essa celula ser libertada por outra thread.
	 * 
	 *
	 * @param numero de salto-ao-eixo
	 * @param idt - Id da cobra
	 * @return o numero de salto-ao-eixo restante.
	 */
	public synchronized int reserveCell(int jumps, int idSnake) {
		boolean usedJump = false;
		if (this.full == true)
			usedJump = true;
			if (jumps == 0) {
				try {
					while (this.full == true) {

						System.out.println("[" + idSnake + "] "
								+ "I'm going to wait - FULL");

						wait();

					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

		this.full = true;
		notifyAll();

		if (usedJump == true && jumps > 0)
			jumps--;
		return jumps;

	}

	/**
	 * Liberta celula ( ultimo elemento da cobra liberta esta celula para poder ser ocupada por outras )
	 */
	public synchronized void freeCell() {
		full = false;
		notifyAll();
	}

	/**
	 * Verifica se Celula tem elements ( comida, salto-ao-eixo, cobras, barreiras ).
	 *
	 * @return true, 
	 */
	public boolean hasElements() {
		if (this.full == true ||  this.food == true
				|| this.jump_pad == true) {
			return true;
		}
		return false;
	}

	public boolean isBarrier() {
		return barrier;
	}

	public void setBarrier(boolean barrier) {
		this.barrier = barrier;
	}

}