package uniandes.dpoo.aerolinea.modelo;



import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import uniandes.dpoo.aerolinea.exceptions.VueloSobrevendidoException;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.tarifas.CalculadoraTarifas;
import uniandes.dpoo.aerolinea.tiquetes.GeneradorTiquetes;
import uniandes.dpoo.aerolinea.tiquetes.Tiquete;

public class Vuelo {
	private String fecha;
	private Ruta ruta;
	private Avion avion;
	private Map<String, Tiquete> tiquetes; //Tiquetes ya vendidos para el vuelo
	
	public Vuelo(Ruta ruta, String fecha, Avion avion) {
		this.fecha = fecha;
		this.ruta = ruta;
		this.avion = avion;
		this.tiquetes = new HashMap<>();
	}

	public String getFecha() {
		return fecha;
	}

	public Ruta getRuta() {
		return ruta;
	}

	public Avion getAvion() {
		return avion;
	}

	public Collection<Tiquete> getTiquetes() {
		return tiquetes.values();
	}
	
	public int venderTiquetes(Cliente cliente, CalculadoraTarifas calculadora, int cantidad) throws VueloSobrevendidoException{
		// 1. Generar Tarifa
		//2. Generar Tiquete
		//3.Registrar Tiquete
		//4. Registrar tiquete en mapa vuelo
		if (this.tiquetes.size() + cantidad > this.avion.getCapacidad()) {
	        throw new VueloSobrevendidoException(this);
	    }
	    int tarifa = calculadora.calcularTarifa(this, cliente);
	    int precioTotal = cantidad * tarifa;
	    for (int i = 0; i < cantidad; i++) {
	        Tiquete tiquete = GeneradorTiquetes.generarTiquete(this, cliente, tarifa);
	        GeneradorTiquetes.registrarTiquete(tiquete);
	        this.tiquetes.put(tiquete.getCodigo(), tiquete);
	    }
	    return precioTotal;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vuelo other = (Vuelo) obj;
		return Objects.equals(avion, other.avion) && Objects.equals(fecha, other.fecha)
				&& Objects.equals(ruta, other.ruta);
	}
	
	
	
	

}
