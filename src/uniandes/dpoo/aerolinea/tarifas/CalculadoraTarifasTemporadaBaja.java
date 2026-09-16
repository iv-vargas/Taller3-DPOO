package uniandes.dpoo.aerolinea.tarifas;

import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.cliente.ClienteCorporativo;

public class CalculadoraTarifasTemporadaBaja {
	
	protected static final int COSTO_POR_KM_NATURAL  = 600;
    protected static final int COSTO_POR_KM_CORPORATIVO = 900;
    protected static final double DESCUENTO_PEQ = 0.02;
    protected static final double DESCUENTO_MEDIANAS = 0.1;
    protected static final double DESCUENTO_GRANDES = 0.2;
    
    public int calcularCostoBase(Vuelo vuelo, Cliente cliente) {
    	return 0;
    }
    
    public double calcularPorcentajeDescuento(Cliente cliente) {
    	if (cliente.getTipoCliente().equals("Corporativo")) {
    		ClienteCorporativo clienteCorporativo = (ClienteCorporativo) cliente;
    		if (clienteCorporativo.getTamanoEmpresa() == 1) {
    			return DESCUENTO_GRANDES;
    		}else if (clienteCorporativo.getTamanoEmpresa() == 2) {
    			return DESCUENTO_MEDIANAS;
    		}else if (clienteCorporativo.getTamanoEmpresa() == 3) {
    			return DESCUENTO_PEQ;
    	}
    }
}
