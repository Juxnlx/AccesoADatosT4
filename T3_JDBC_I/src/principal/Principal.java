package principal;

import java.sql.SQLException;
import java.util.Scanner;

import crud.RestauranteCRUD;

public class Principal {

	private static final Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {

		// Creamos la variable opc como int para almacenar la opción seleccionada por el
		// usuario.
		int opc;

		do {
			// Llamamos a la función menú.
			Menu();
			// Le pedimos al usuario que introduzca la opción que quiere seleccionar en el
			// menú y la leemos.
			System.out.println("Introduce una opción --> ");
			opc = sc.nextInt();
			// Limpiamos el buffer
			sc.nextLine();

			switch (opc) {
			// Se encarga de la creación de todas las tablas o de alguna especifica.
			case 1 -> gestionCreacionTablas();
			case 2 -> gestionInsentarMesa();
			case 3 -> gestionInsertarProducto();
			case 4 -> gestionInsertarFactura();
			case 5 -> gestionInsertarPedido();
			case 0 -> System.out.println("Saliendo del programa...");
		    default -> System.out.println("Opción no válida.");
			}

		} while (opc != 0);

		// Cerramos el Scanner
		sc.close();
	}

	public static void Menu() {
		System.out.println("----------MENÚ----------");
		System.out.println("1. Crear Tabla");
		System.out.println("2. Insertar Mesa");
		System.out.println("3. Insertar Producto");
		System.out.println("4. Insertar Factura");
		System.out.println("5. Insertar Pedido");
		System.out.println("6. Listar");
		System.out.println("7. Modificar");
		System.out.println("8. Borrar");
		System.out.println("9. Eliminar Tabla");
		System.out.println("0. Salir");
	}

	public static void gestionCreacionTablas() {
		int opc;
		String nombreTabla = "";

		System.out.println("¿Quieres crear todas las tablas o una tabla concreta?");
		System.out.println("1. Todas las tablas");
		System.out.println("2. Tabla concreta");
		opc = sc.nextInt();
		sc.nextLine();

		if (opc == 1) {
			RestauranteCRUD.crearTodasLasTablas();
		} else if (opc == 2) {
			System.out.println("Introduce el nombre de la tabla (mesa, producto, factura, pedido):");
			nombreTabla = sc.nextLine().toLowerCase();

			try {
				if (RestauranteCRUD.puedeCrearTabla(nombreTabla)) {
					switch (nombreTabla) {
					case "mesa" -> RestauranteCRUD.crearTablaMesa();
					case "producto" -> RestauranteCRUD.crearTablaProducto();
					case "factura" -> RestauranteCRUD.crearTablaFactura();
					case "pedido" -> RestauranteCRUD.crearTablaPedido();
					default -> System.out.println("Tabla no válida.");
					}
					System.out.println("Tabla " + nombreTabla + " creada correctamente.");
				} else {
					System.out.println("No se puede crear la tabla " + nombreTabla
							+ " porque depende de otras tablas que no existen.");
				}

			} catch (SQLException e) {
				System.out.println("Error al crear la tabla: " + e.getMessage());
			}
		} else {
			System.out.println("Opción no válida");
		}
	}

	public static void gestionInsentarMesa() {
		int numComensales;
		
		System.out.println("----- INSERTAR MESA -----");
		System.out.print("Número de comensales: ");
		numComensales = sc.nextInt();
		sc.nextLine();
		
		System.out.print("¿Tiene reserva? (1 = Sí, 0 = No): ");
		int reserva = sc.nextInt();
		sc.nextLine();

		if (RestauranteCRUD.insertarMesa(numComensales, reserva)) {
			System.out.println("Mesa insertada correctamente.");
		} else {
			System.out.println("Error al insertar mesa.");
		}
	}
	
	public static void gestionInsertarProducto() {
		String nombre = "";
		double precio;
		
		System.out.println("---- INSERTAR PRODUCTO -----");
		System.out.println("Nombre del producto: ");
		nombre = sc.nextLine();
		
		System.out.println("Precio del producto: ");
		precio = sc.nextDouble();
		sc.nextLine();
		
		if (RestauranteCRUD.insertarProducto(nombre, precio)) {
			System.out.println("Producto insertado correctamente");
		} else {
			System.out.println("Error al insertar producto.");
		}
	}
	
	public static void gestionInsertarFactura() {
		int idMesa;
		String tipoPago = "";
		double importe;
		
		System.out.println("----- INSERTAR FACTURA -----");
		System.out.println("ID de mesa: ");
		idMesa = sc.nextInt();
		sc.nextLine();
		
		System.out.println("Tipo de pago: ");
		tipoPago = sc.nextLine();
		
		System.out.println("Importe de factura: ");
		importe = sc.nextDouble();
		sc.nextLine();
		
		if (RestauranteCRUD.insertarFactura(idMesa, tipoPago, importe)) {
			System.out.println("Factura insertada correctamente");
		} else {
			System.out.println("Error al insertar factura.");
		}
	}
	
	public static void gestionInsertarPedido() {
		int idFactura;
		int idProducto;
		int cantidad;
		
		System.out.println("----- INSERTAR PEDIDO -----");
		System.out.println("ID de Factura: ");
		idFactura = sc.nextInt();
		sc.nextLine();
		
		System.out.println("ID de Producto: ");
		idProducto = sc.nextInt();
		sc.nextLine();
		
		System.out.println("Cantidad de pedido: ");
		cantidad = sc.nextInt();
		sc.nextLine();
		
		if (RestauranteCRUD.insertarPedido(idFactura, idProducto, cantidad)) {
			System.out.println("Pedido insertado correctamente");
		} else {
			System.out.println("Error al insertar pedido.");
		}
	}

}