package entidades;

public class Producto {
	private int idProducto;
    private String denominacion;
    private double precio;

    public Producto(int idProducto, String denominacion, double precio) {
        this.idProducto = idProducto;
        this.denominacion = denominacion;
        this.precio = precio;
    }

    public Producto(String denominacion, double precio) {
        this.denominacion = denominacion;
        this.precio = precio;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Producto [id=" + idProducto + ", nombre=" + denominacion + ", precio=" + precio + "]";
    }
}