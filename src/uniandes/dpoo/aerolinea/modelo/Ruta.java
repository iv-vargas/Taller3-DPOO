package uniandes.dpoo.aerolinea.modelo;

import java.util.Objects;

/**
 * Esta clase tiene la información de una ruta entre dos aeropuertos que cubre una aerolínea.
 */
public class Ruta
{
    private String horaSalida;
    private String horaLlegada;
    private String codigoRuta;
    private Aeropuerto origen;
    private Aeropuerto destino;
    
    public Ruta (Aeropuerto origen, Aeropuerto destino, String horaSalida, String horaLlegada, String codigoRuta) {
    	this.origen = origen;
    	this.destino = destino;
    	this.horaSalida = horaSalida;
    	this.horaLlegada = horaLlegada;
    	this.codigoRuta = codigoRuta;
    }
    
    
    


    public String getHoraSalida() {
		return horaSalida;
	}





	public String getHoraLlegada() {
		return horaLlegada;
	}





	public String getCodigoRuta() {
		return codigoRuta;
	}





	public Aeropuerto getOrigen() {
		return origen;
	}





	public Aeropuerto getDestino() {
		return destino;
	}
	
	public int getDuracion() {
	    int horasSalida = getHoras(this.horaSalida);
	    int minutosSalida = getMinutos(this.horaSalida);
	    
	    int horasLlegada = getHoras(this.horaLlegada);
	    int minutosLlegada = getMinutos(this.horaLlegada);
	    

	    int salidaTotalMinutos = (horasSalida * 60) + minutosSalida;
	    int llegadaTotalMinutos = (horasLlegada * 60) + minutosLlegada;
	    
	    int duracion = llegadaTotalMinutos - salidaTotalMinutos;
	    
	    if (duracion < 0) {
	        duracion += 24 * 60;
	    }
	    
	    return duracion;
	}


	/**
     * Dada una cadena con una hora y minutos, retorna los minutos.
     * 
     * Por ejemplo, para la cadena '715' retorna 15.
     * @param horaCompleta Una cadena con una hora, donde los minutos siempre ocupan los dos últimos caracteres
     * @return Una cantidad de minutos entre 0 y 59
     */
    public static int getMinutos( String horaCompleta )
    {
        int minutos = Integer.parseInt( horaCompleta ) % 100;
        return minutos;
    }

    /**
     * Dada una cadena con una hora y minutos, retorna las horas.
     * 
     * Por ejemplo, para la cadena '715' retorna 7.
     * @param horaCompleta Una cadena con una hora, donde los minutos siempre ocupan los dos últimos caracteres
     * @return Una cantidad de horas entre 0 y 23
     */
    public static int getHoras( String horaCompleta )
    {
        int horas = Integer.parseInt( horaCompleta ) / 100;
        return horas;
    }





	@Override
	public int hashCode() {
		return Objects.hash(codigoRuta);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ruta other = (Ruta) obj;
		return Objects.equals(codigoRuta, other.codigoRuta);
	}
    
    

    
}
