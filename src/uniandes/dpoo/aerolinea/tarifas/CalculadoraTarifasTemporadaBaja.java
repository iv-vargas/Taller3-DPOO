package uniandes.dpoo.aerolinea.tarifas;

import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.cliente.ClienteCorporativo;

public class CalculadoraTarifasTemporadaBaja extends CalculadoraTarifas {
	
	protected static final int COSTO_POR_KM_NATURAL  = 600;
    protected static final int COSTO_POR_KM_CORPORATIVO = 900;
    protected static final double DESCUENTO_PEQ = 0.02;
    protected static final double DESCUENTO_MEDIANAS = 0.1;
    protected static final double DESCUENTO_GRANDES = 0.2;
    
    public int calcularCostoBase(Vuelo vuelo, Cliente cliente) {
    	
    	int costo_por_km = 0;
    	if(cliente.getTipoCliente().equals("Natural")) {
    		costo_por_km = COSTO_POR_KM_NATURAL;
    	}else {
    		costo_por_km = COSTO_POR_KM_CORPORATIVO;
    	}
    	int distanciaVuelo = calcularDistanciaVuelo(vuelo.getRuta());
		return distanciaVuelo * costo_por_km;
    }
    
    public double calcularPorcentajeDescuento(Cliente cliente) {
        
        if (cliente.getTipoCliente().equals("Corporativo")) {
            ClienteCorporativo clienteCorporativo = (ClienteCorporativo) cliente;
            if (clienteCorporativo.getTamanoEmpresa() == ClienteCorporativo.GRANDE) {
                return DESCUENTO_GRANDES;
            } else if (clienteCorporativo.getTamanoEmpresa() == ClienteCorporativo.MEDIANA) {
                return DESCUENTO_MEDIANAS;
            } else if (clienteCorporativo.getTamanoEmpresa() == ClienteCorporativo.PEQUENA) {
                return DESCUENTO_PEQ;
            }
            
        } else {
            return 0.0;
        }
        
        return 0.0;
    }
    
    
}

