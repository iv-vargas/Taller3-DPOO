package uniandes.dpoo.aerolinea.tarifas;



import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;

public class CalculadoraTarifasTemporadaAlta extends CalculadoraTarifas {
	protected final int COSTO_POR_KM = 1000;
	
    public int calcularCostoBase(Vuelo vuelo, Cliente cliente) {
		int distanciaVuelo = calcularDistanciaVuelo(vuelo.getRuta());
		return distanciaVuelo * COSTO_POR_KM;
		
	}
	
	public double calcularPorcentajeDescuento(Cliente cliente) {
		return 0;

	}

}
