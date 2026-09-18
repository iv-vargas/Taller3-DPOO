package uniandes.dpoo.aerolinea.modelo;

import java.util.Objects;

public class Avion {
	private String nombre;
	private int capacidad;
	
	public Avion(String nombre, int capacidad) {
		this.capacidad = capacidad;
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}


	public int getCapacidad() {
		return capacidad;
	}

	@Override
	public int hashCode() {
		return Objects.hash(capacidad, nombre);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Avion other = (Avion) obj;
		return capacidad == other.capacidad && Objects.equals(nombre, other.nombre);
	}
	
	

}
