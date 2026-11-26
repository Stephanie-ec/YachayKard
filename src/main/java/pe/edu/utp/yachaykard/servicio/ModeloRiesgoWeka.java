package pe.edu.utp.yachaykard.servicio;

import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Modelo de riesgo de deserción con Weka (RF-26): un árbol de decisión J48 entrenado con
 * datos ficticios (src/main/resources/riesgo_entrenamiento.arff).
 * Recibe las tres señales de alerta (faltas, competencias en C e incidencias graves)
 * y devuelve Alto, Medio o Bajo. Se entrena una sola vez, la primera vez que se usa.
 */
public final class ModeloRiesgoWeka implements ClasificadorRiesgo {

    /** Archivo de entrenamiento dentro de src/main/resources. */
    static final String DATOS = "/riesgo_entrenamiento.arff";

    private static ModeloRiesgoWeka instancia;

    private final J48 arbol;
    /** Estructura de los datos (atributos y clase), sin filas. */
    private final Instances estructura;

    /**
     * Entrena el modelo con los datos recibidos.
     *
     * @param datos contenido del archivo ARFF
     * @throws IllegalStateException si no hay datos o el entrenamiento falla
     */
    ModeloRiesgoWeka(InputStream datos) {
        if (datos == null) {
            throw new IllegalStateException("No se encontró el archivo de entrenamiento " + DATOS + ".");
        }
        try (Reader lector = new BufferedReader(new InputStreamReader(datos, StandardCharsets.UTF_8))) {
            Instances entrenamiento = new Instances(lector);
            entrenamiento.setClassIndex(entrenamiento.numAttributes() - 1);
            J48 modelo = new J48();
            modelo.buildClassifier(entrenamiento);
            this.arbol = modelo;
            this.estructura = new Instances(entrenamiento, 0);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo de entrenamiento.", e);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo entrenar el modelo de riesgo.", e);
        }
    }

    /**
     * Devuelve el modelo entrenado con el archivo de la aplicación (lo entrena la primera vez).
     *
     * @return modelo listo para clasificar
     * @throws IllegalStateException si el entrenamiento falla
     */
    public static synchronized ModeloRiesgoWeka obtener() {
        if (instancia == null) {
            instancia = new ModeloRiesgoWeka(ModeloRiesgoWeka.class.getResourceAsStream(DATOS));
        }
        return instancia;
    }

    @Override
    public synchronized String clasificar(int faltas, int competenciasEnC, int incidenciasGraves) {
        DenseInstance fila = new DenseInstance(estructura.numAttributes());
        fila.setDataset(estructura);
        fila.setValue(0, faltas);
        fila.setValue(1, competenciasEnC);
        fila.setValue(2, incidenciasGraves);
        try {
            double indice = arbol.classifyInstance(fila);
            return estructura.classAttribute().value((int) indice);
        } catch (Exception e) {
            throw new IllegalStateException("El modelo no pudo clasificar al estudiante.", e);
        }
    }

    /** @return el árbol aprendido en texto (sirve para el informe) */
    public String descripcionArbol() {
        return arbol.toString();
    }
}