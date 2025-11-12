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
		
	}
}