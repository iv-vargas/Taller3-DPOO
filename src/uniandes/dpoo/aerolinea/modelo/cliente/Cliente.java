package uniandes.dpoo.aerolinea.modelo.cliente;

import java.util.ArrayList;
import java.util.List;

import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.tiquetes.Tiquete;

public abstract class Cliente {
	private List<Tiquete> tiquetesUsados;
	private List<Tiquete> tiquetesSinUsar;
	
	public Cliente() {
		this.tiquetesSinUsar = new ArrayList<>();
		this.tiquetesUsados = new ArrayList<>();
	}
	
	public abstract String getTipoCliente();
	public abstract String getIdentificador();
	
	public void agregarTiquete(Tiquete tiquete) {
		if (! tiquete.esUsado()) {
			this.tiquetesSinUsar.add(tiquete);
		}
	}
	
	public int calcularValorTotalTiquetes() {
		int valorTotal = 0;
		for (Tiquete tiquete : this.tiquetesSinUsar) {
		    if (!tiquete.esUsado()) {
		        valorTotal += tiquete.getTarifa();
		    }
		}
		
		return valorTotal;
	}
	
	public void usarTiquetes(Vuelo vuelo) {
		java.util.Iterator<Tiquete> iterator = this.tiquetesSinUsar.iterator();
	    while (iterator.hasNext()) {
	        Tiquete tiquete = iterator.next();
	        if (tiquete.getVuelo().equals(vuelo)) {
	            tiquete.marcarComoUsado();
	            this.tiquetesUsados.add(tiquete);
	            iterator.remove(); // Elimina de forma segura de tiquetesSinUsar
	        }
	    }
	}
	
	
	

}
