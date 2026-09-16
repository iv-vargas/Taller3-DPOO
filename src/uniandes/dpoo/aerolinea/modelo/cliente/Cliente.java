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
		if (tiquete.esUsado()) {
			this.tiquetesUsados.add(tiquete);
		}else {
			this.tiquetesSinUsar.add(tiquete);
		}
	}
	
	public int calcularValorTotalTiquetes() {
		int valorTotal = 0;
		for (Tiquete tiquete : this.tiquetesSinUsar) {
			valorTotal += tiquete.getTarifa();
		}
		
		return valorTotal;
	}
	
	public void usarTiquetes(Vuelo vuelo) {
		for (Tiquete tiquete : vuelo.getTiquetes()) {
			tiquete.marcarComoUsado();;
		}
	}
	
	
	

}
