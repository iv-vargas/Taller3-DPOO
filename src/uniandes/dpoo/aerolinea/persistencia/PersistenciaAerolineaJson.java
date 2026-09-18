package uniandes.dpoo.aerolinea.persistencia;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uniandes.dpoo.aerolinea.exceptions.InformacionInconsistenteException;
import uniandes.dpoo.aerolinea.modelo.Aerolinea;
import uniandes.dpoo.aerolinea.modelo.Aeropuerto;
import uniandes.dpoo.aerolinea.modelo.Avion;
import uniandes.dpoo.aerolinea.modelo.Ruta;
import uniandes.dpoo.aerolinea.modelo.Vuelo;

/**
 * Persistencia de los aviones, aeropuertos, rutas y vuelos de una aerolínea en un archivo JSON.
 * (Los clientes y los tiquetes se persisten aparte, en PersistenciaTiquetesJson.)
 */
public class PersistenciaAerolineaJson implements IPersistenciaAerolinea {

	// Llaves de primer nivel
	private static final String AVIONES = "aviones";
	private static final String AEROPUERTOS = "aeropuertos";
	private static final String RUTAS = "rutas";
	private static final String VUELOS = "vuelos";

	// Aviones
	private static final String NOMBRE_AVION = "nombre";
	private static final String CAPACIDAD = "capacidad";

	// Aeropuertos
	private static final String NOMBRE_AEROPUERTO = "nombre";
	private static final String CODIGO_AEROPUERTO = "codigo";
	private static final String NOMBRE_CIUDAD = "nombreCiudad";
	private static final String LATITUD = "latitud";
	private static final String LONGITUD = "longitud";

	// Rutas
	private static final String HORA_SALIDA = "horaSalida";
	private static final String HORA_LLEGADA = "horaLlegada";
	private static final String CODIGO_RUTA = "codigoRuta";
	private static final String ORIGEN = "origen";
	private static final String DESTINO = "destino";

	// Vuelos
	private static final String FECHA = "fecha";
	private static final String AVION_DEL_VUELO = "nombreAvion";

	// ------------------------------------------------------------------
	// CARGAR
	// ------------------------------------------------------------------

	@Override
	public void cargarAerolinea(String archivo, Aerolinea aerolinea) throws IOException, InformacionInconsistenteException {
		String jsonCompleto = new String(Files.readAllBytes(new File(archivo).toPath()));
		try {
			JSONObject raiz = new JSONObject(jsonCompleto);

			// El orden importa: las rutas necesitan los aeropuertos y los vuelos necesitan rutas y aviones
			cargarAviones(aerolinea, raiz.getJSONArray(AVIONES));
			Map<String, Aeropuerto> aeropuertos = cargarAeropuertos(raiz.getJSONArray(AEROPUERTOS));
			cargarRutas(aerolinea, raiz.getJSONArray(RUTAS), aeropuertos);
			cargarVuelos(aerolinea, raiz.getJSONArray(VUELOS));
		} catch (JSONException e) {
			throw new InformacionInconsistenteException("El archivo no tiene el formato esperado: " + e.getMessage());
		}
	}

	private void cargarAviones(Aerolinea aerolinea, JSONArray jAviones) {
		for (int i = 0; i < jAviones.length(); i++) {
			JSONObject jAvion = jAviones.getJSONObject(i);
			String nombre = jAvion.getString(NOMBRE_AVION);
			int capacidad = jAvion.getInt(CAPACIDAD);
			aerolinea.agregarAvion(new Avion(nombre, capacidad));
		}
	}

	/**
	 * La aerolínea no guarda los aeropuertos directamente (solo a través de las rutas), por eso se devuelven en un mapa
	 * indexado por código, para poder armar las rutas después.
	 */
	private Map<String, Aeropuerto> cargarAeropuertos(JSONArray jAeropuertos) throws InformacionInconsistenteException {
		Map<String, Aeropuerto> aeropuertos = new LinkedHashMap<>();
		for (int i = 0; i < jAeropuertos.length(); i++) {
			JSONObject jAeropuerto = jAeropuertos.getJSONObject(i);
			String nombre = jAeropuerto.getString(NOMBRE_AEROPUERTO);
			String codigo = jAeropuerto.getString(CODIGO_AEROPUERTO);
			String nombreCiudad = jAeropuerto.getString(NOMBRE_CIUDAD);
			double latitud = jAeropuerto.getDouble(LATITUD);
			double longitud = jAeropuerto.getDouble(LONGITUD);

			if (aeropuertos.containsKey(codigo)) {
				throw new InformacionInconsistenteException("El aeropuerto con código " + codigo + " está repetido");
			}

			Aeropuerto aeropuerto;
			try {
				aeropuerto = new Aeropuerto(nombre, codigo, nombreCiudad, latitud, longitud);
			} catch (Exception e) {
				// Por ejemplo, AeropuertoDuplicadoException si el constructor la declara
				throw new InformacionInconsistenteException(e.getMessage());
			}
			aeropuertos.put(codigo, aeropuerto);
		}
		return aeropuertos;
	}

	private void cargarRutas(Aerolinea aerolinea, JSONArray jRutas, Map<String, Aeropuerto> aeropuertos)
			throws InformacionInconsistenteException {
		for (int i = 0; i < jRutas.length(); i++) {
			JSONObject jRuta = jRutas.getJSONObject(i);
			String codigoRuta = jRuta.getString(CODIGO_RUTA);
			// Las horas se leen como texto aunque en el archivo vengan como número (ej. 715)
			String horaSalida = jRuta.get(HORA_SALIDA).toString();
			String horaLlegada = jRuta.get(HORA_LLEGADA).toString();

			String codigoOrigen = jRuta.getString(ORIGEN);
			Aeropuerto origen = aeropuertos.get(codigoOrigen);
			if (origen == null) {
				throw new InformacionInconsistenteException(
						"La ruta " + codigoRuta + " tiene un aeropuerto de origen que no existe: " + codigoOrigen);
			}

			String codigoDestino = jRuta.getString(DESTINO);
			Aeropuerto destino = aeropuertos.get(codigoDestino);
			if (destino == null) {
				throw new InformacionInconsistenteException(
						"La ruta " + codigoRuta + " tiene un aeropuerto de destino que no existe: " + codigoDestino);
			}

			aerolinea.agregarRuta(new Ruta(origen, destino, horaSalida, horaLlegada, codigoRuta));
		}
	}

	private void cargarVuelos(Aerolinea aerolinea, JSONArray jVuelos) throws InformacionInconsistenteException {
		for (int i = 0; i < jVuelos.length(); i++) {
			JSONObject jVuelo = jVuelos.getJSONObject(i);
			String fecha = jVuelo.getString(FECHA);
			String codigoRuta = jVuelo.getString(CODIGO_RUTA);
			String nombreAvion = jVuelo.getString(AVION_DEL_VUELO);

			if (aerolinea.getRuta(codigoRuta) == null) {
				throw new InformacionInconsistenteException(
						"El vuelo del " + fecha + " usa una ruta que no existe: " + codigoRuta);
			}

			boolean existeAvion = false;
			for (Avion avion : aerolinea.getAviones()) {
				if (avion.getNombre().equals(nombreAvion)) {
					existeAvion = true;
					break;
				}
			}
			if (!existeAvion) {
				throw new InformacionInconsistenteException(
						"El vuelo " + codigoRuta + " del " + fecha + " usa un avión que no existe: " + nombreAvion);
			}

			aerolinea.programarVuelo(fecha, codigoRuta, nombreAvion);
		}
	}

	// ------------------------------------------------------------------
	// SALVAR
	// ------------------------------------------------------------------

	@Override
	public void salvarAerolinea(String archivo, Aerolinea aerolinea) throws IOException {
		JSONObject jobject = new JSONObject();

		salvarAviones(aerolinea, jobject);
		salvarAeropuertos(aerolinea, jobject);
		salvarRutas(aerolinea, jobject);
		salvarVuelos(aerolinea, jobject);

		try (PrintWriter pw = new PrintWriter(archivo)) {
			jobject.write(pw, 2, 0);
		}
	}

	private void salvarAviones(Aerolinea aerolinea, JSONObject jobject) {
		JSONArray jAviones = new JSONArray();
		for (Avion avion : aerolinea.getAviones()) {
			JSONObject jAvion = new JSONObject();
			jAvion.put(NOMBRE_AVION, avion.getNombre());
			jAvion.put(CAPACIDAD, avion.getCapacidad());
			jAviones.put(jAvion);
		}
		jobject.put(AVIONES, jAviones);
	}

	/**
	 * Como la aerolínea solo conoce los aeropuertos a través de sus rutas, se recolectan los aeropuertos de origen y
	 * destino de todas las rutas, sin repetir.
	 */
	private void salvarAeropuertos(Aerolinea aerolinea, JSONObject jobject) {
		Map<String, Aeropuerto> aeropuertos = new LinkedHashMap<>();
		for (Ruta ruta : aerolinea.getRutas()) {
			aeropuertos.putIfAbsent(ruta.getOrigen().getCodigo(), ruta.getOrigen());
			aeropuertos.putIfAbsent(ruta.getDestino().getCodigo(), ruta.getDestino());
		}

		JSONArray jAeropuertos = new JSONArray();
		for (Aeropuerto aeropuerto : aeropuertos.values()) {
			JSONObject jAeropuerto = new JSONObject();
			jAeropuerto.put(NOMBRE_AEROPUERTO, aeropuerto.getNombre());
			jAeropuerto.put(CODIGO_AEROPUERTO, aeropuerto.getCodigo());
			jAeropuerto.put(NOMBRE_CIUDAD, aeropuerto.getNombreCiudad());
			jAeropuerto.put(LATITUD, aeropuerto.getLatitud());
			jAeropuerto.put(LONGITUD, aeropuerto.getLongitud());
			jAeropuertos.put(jAeropuerto);
		}
		jobject.put(AEROPUERTOS, jAeropuertos);
	}

	private void salvarRutas(Aerolinea aerolinea, JSONObject jobject) {
		JSONArray jRutas = new JSONArray();
		for (Ruta ruta : aerolinea.getRutas()) {
			JSONObject jRuta = new JSONObject();
			jRuta.put(CODIGO_RUTA, ruta.getCodigoRuta());
			jRuta.put(HORA_SALIDA, ruta.getHoraSalida());
			jRuta.put(HORA_LLEGADA, ruta.getHoraLlegada());
			jRuta.put(ORIGEN, ruta.getOrigen().getCodigo());
			jRuta.put(DESTINO, ruta.getDestino().getCodigo());
			jRutas.put(jRuta);
		}
		jobject.put(RUTAS, jRutas);
	}

	private void salvarVuelos(Aerolinea aerolinea, JSONObject jobject) {
		JSONArray jVuelos = new JSONArray();
		for (Vuelo vuelo : aerolinea.getVuelos()) {
			JSONObject jVuelo = new JSONObject();
			jVuelo.put(FECHA, vuelo.getFecha());
			jVuelo.put(CODIGO_RUTA, vuelo.getRuta().getCodigoRuta());
			jVuelo.put(AVION_DEL_VUELO, vuelo.getAvion().getNombre());
			jVuelos.put(jVuelo);
		}
		jobject.put(VUELOS, jVuelos);
	}

}