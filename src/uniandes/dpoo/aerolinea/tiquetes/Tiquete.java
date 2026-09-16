package uniandes.dpoo.aerolinea.tiquetes;

import uniandes.dpoo.aerolinea.modelo.Vuelo;

public class Tiquete {
	
	private String codigo;
	private int tarifa;
	private boolean usado;
	private Vuelo vueloAsociado;
	private Cliente cliente;
	
	
	public Tiquete(String codigo, Vuelo vuelo, Cliente clienteComprador, int tarifa) {
		this.codigo = codigo;
		this.vueloAsociado = vuelo;
		this.cliente = clienteComprador;
		this.tarifa = tarifa;
		this.usado = false;
		
	}
	
	public String getCodigo() {
		return codigo;
	}
	public int getTarifa() {
		return tarifa;
	}
	public boolean esUsado() {
		return usado;
	}
	public Vuelo getVueloAsociado() {
		return vueloAsociado;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void marcarComoUsado() {
		this.usado = true;
	}
	
	
	

	
}
