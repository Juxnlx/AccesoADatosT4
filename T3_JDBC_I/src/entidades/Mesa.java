package entidades;

public class Mesa {

	// Creamos la variable idMesa como int para almacenar el identificador unico de
	// la mesa.
	private int idMesa;

	// Creamos la variable numComensales como int para almacenar el número de
	// comensales.
	private int numComensales;

	// Creamos la variable reserva como boolean para almacenar si hay una reserva o
	// no.
	private boolean rerserva;

	// Creamos un constructor sin parametros.
	public Mesa() {
	}

	// Creamos un constructor con todos los atributos de la clase Mesa.
	public Mesa(int idMesa, int numComensales, boolean rerserva) {
		this.idMesa = idMesa;
		this.numComensales = numComensales;
		this.rerserva = rerserva;
	}

	/**
	 * Nos devuelve el número de comensales de la mesa.
	 * 
	 * @return El número de comensales.
	 */
	public int getNumComensales() {
		return numComensales;
	}

	/**
	 * Nos permite modificar el número de comensales de cada mesa
	 * 
	 * @param numComensales El nuevo número de comensales.
	 */
	public void setNumComensales(int numComensales) {
		this.numComensales = numComensales;
	}

	/**
	 * Nos devuelve si hay reserva o no.
	 * 
	 * @return True o False dependiendo de si esta reservada o no.
	 */
	public boolean isRerserva() {
		return rerserva;
	}

	/**
	 * Nos permite modificar si la reserva se encuentra activa o no
	 * 
	 * @param rerserva El nuevo estado de la reserva.
	 */
	public void setRerserva(boolean rerserva) {
		this.rerserva = rerserva;
	}

	/**
	 * Nos devuelve el identificador unico de la mesa.
	 * 
	 * @return El identificador de la mesa.
	 */
	public int getIdMesa() {
		return idMesa;
	}

	/**
	 * Metodo que nos devuelve el valor de cada atributo de nuestra mesa.
	 */
	@Override
	public String toString() {
		return "Mesa [idMesa=" + idMesa + ", numComensales=" + numComensales + ", rerserva=" + rerserva + "]";
	}

}
