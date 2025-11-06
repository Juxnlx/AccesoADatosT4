package entidades;

public class Factura {

	private int idFactura;
	private int idMesa;
	private String tipoPago;
	private double importe;

	// Constructor con ID (para cuando la lees de la BD)
	public Factura(int idFactura, int idMesa, String tipoPago, double importe) {
		this.idFactura = idFactura;
		this.idMesa = idMesa;
		this.tipoPago = tipoPago;
		this.importe = importe;
	}

	// Constructor sin ID (para cuando la vas a insertar)
	public Factura(int idMesa, String tipoPago, double importe) {
		this.idMesa = idMesa;
		this.tipoPago = tipoPago;
		this.importe = importe;
	}

	// Getters
	public int getIdFactura() {
		return idFactura;
	}

	public int getIdMesa() {
		return idMesa;
	}

	public String getTipoPago() {
		return tipoPago;
	}

	public double getImporte() {
		return importe;
	}

	// Setters
	public void setIdMesa(int idMesa) {
		this.idMesa = idMesa;
	}

	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	@Override
	public String toString() {
		return "Factura [id=" + idFactura + ", idMesa=" + idMesa + ", tipoPago=" + tipoPago + ", importe=" + importe
				+ "]";
	}
}