package pe.edu.utp.yachaykard.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Activa los servicios RESTful (Jersey) bajo la ruta /api.
 */
@ApplicationPath("api")
public class ApiConfig extends Application {
}