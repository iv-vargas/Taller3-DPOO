package presentacion;

import uniandes.dpoo.aerolinea.exceptions.AeropuertoDuplicadoException;
import uniandes.dpoo.aerolinea.modelo.Aeropuerto;

public class mainAerolineas {
	
	public static void main(String[] args) {
		
		try {
			Aeropuerto a = new Aeropuerto("Aeropuerto1", "ARP", "Bogota", 10.0, 90.0);
			System.out.println(a.getNombre());
		} catch (AeropuertoDuplicadoException e) {
			System.out.println(e.getMessage());
		}
		try {
			Aeropuerto b = new Aeropuerto("Aeropuerto1", "BP", "Bogota", 10.0, 90.0);
			System.out.println(b.getNombre());
		} catch (AeropuertoDuplicadoException e) {
			System.out.println(e.getMessage());
		}
		
		
		
		
		    
		
		System.out.println("PIPI");
		
	}

}
