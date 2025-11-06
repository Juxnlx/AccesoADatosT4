package entidades;

public class Pedido {
	
	private int idPedido;
	private int idFactura;
	private int idProducto;
	private int cantidad;

	// Constructor con ID (para leer de la BD)
	public Pedido(int idPedido, int idFactura, int idProducto, int cantidad) {
		this.idPedido = idPedido;
		this.idFactura = idFactura;
		this.idProducto = idProducto;
		this.cantidad = cantidad;
	}

	// Constructor sin ID (para insertar)
	public Pedido(int idFactura, int idProducto, int cantidad) {
		this.idFactura = idFactura;
		this.idProducto = idProducto;
		this.cantidad = cantidad;
	}

	// Getters
	public int getIdPedido() {
		return idPedido;
	}

	public int getIdFactura() {
		return idFactura;
	}

	public int getIdProducto() {
		return idProducto;
	}

	public int getCantidad() {
		return cantidad;
	}

	// Setters
	public void setIdFactura(int idFactura) {
		this.idFactura = idFactura;
	}

	public void setIdProducto(int idProducto) {
		this.idProducto = idProducto;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	@Override
	public String toString() {
		return "Pedido [id=" + idPedido + ", idFactura=" + idFactura + ", idProducto=" + idProducto + ", cantidad="
				+ cantidad + "]";
	}
}
