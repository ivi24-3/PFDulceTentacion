// ReniecController.java
package com.pasteleria.controllers.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reniec")
@CrossOrigin(origins = "*")
public class ReniecController {
    
    private final String API_TOKEN = "914dbef8372327d4839a63dd83edebab38233f5ece768ea7c30fe5ecb8d3bae6";
    private final String API_URL = "https://apiperu.dev/api/dni";
    
    // Endpoint de prueba
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "API funcionando correctamente");
        System.out.println("🔥 ENDPOINT DE PRUEBA LLAMADO");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{dni}")
    public ResponseEntity<Map<String, Object>> consultarDni(@PathVariable String dni) {
        Map<String, Object> response = new HashMap<>();
        
        System.out.println("🔥🔥🔥 LLAMADA RECIBIDA - DNI: " + dni);
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // Headers para la API
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_TOKEN);
            headers.set("Content-Type", "application/json");
            headers.set("Accept", "application/json");
            
            // Body para POST con el DNI
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("dni", dni);
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
            
            System.out.println("=== CONSULTANDO DNI ===");
            System.out.println("URL: " + API_URL);
            System.out.println("DNI: " + dni);
            System.out.println("Token: " + API_TOKEN);
            System.out.println("Body: " + requestBody);
            
            // Usar POST en lugar de GET
            ResponseEntity<ReniecResponse> apiResponse = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,  // Cambio importante: POST
                entity,
                ReniecResponse.class
            );
            
            System.out.println("Status Code: " + apiResponse.getStatusCode());
            System.out.println("🔍 RESPUESTA COMPLETA RAW: " + apiResponse.getBody());
            
            if (apiResponse.getBody() != null && apiResponse.getBody().isSuccess()) {
                ReniecResponse.Data data = apiResponse.getBody().getData();
                
                // LOG DETALLADO de cada campo
                System.out.println("📋 ANÁLISIS DETALLADO:");
                System.out.println("  - Nombres: '" + data.getNombres() + "'");
                System.out.println("  - Apellido Paterno: '" + data.getApellidoPaterno() + "'");
                System.out.println("  - Apellido Materno: '" + data.getApellidoMaterno() + "'");
                System.out.println("  - Número: '" + data.getNumero() + "'");
                
                // Intentar también otros posibles nombres de campos
                System.out.println("🔍 INSPECCIONANDO RESPUESTA JSON COMPLETA...");
                
                // Construir nombre completo manejando valores null
                StringBuilder nombreCompleto = new StringBuilder();
                
                if (data.getNombres() != null && !data.getNombres().trim().isEmpty()) {
                    nombreCompleto.append(data.getNombres().trim());
                }
                
                if (data.getApellidoPaterno() != null && !data.getApellidoPaterno().trim().isEmpty()) {
                    if (nombreCompleto.length() > 0) nombreCompleto.append(" ");
                    nombreCompleto.append(data.getApellidoPaterno().trim());
                }
                
                if (data.getApellidoMaterno() != null && !data.getApellidoMaterno().trim().isEmpty()) {
                    if (nombreCompleto.length() > 0) nombreCompleto.append(" ");
                    nombreCompleto.append(data.getApellidoMaterno().trim());
                }
                
                String nombreFinal = nombreCompleto.toString().trim();
                
                response.put("success", true);
                response.put("nombre", nombreFinal);
                response.put("dni", dni);
                
                System.out.println("✅ NOMBRE FINAL PROCESADO: '" + nombreFinal + "'");
            } else {
                response.put("success", false);
                response.put("message", "DNI no encontrado en RENIEC");
                System.out.println("❌ DNI no encontrado");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERROR al consultar DNI: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Error de conexión: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReniecResponse {
        private boolean success;
        private Data data;
        private String message;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public Data getData() { return data; }
        public void setData(Data data) { this.data = data; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        @Override
        public String toString() {
            return "ReniecResponse{success=" + success + ", data=" + data + ", message='" + message + "'}";
        }
        
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Data {
            private String numero;
            private String nombres;
            private String nombre_completo;
            private String apellido_paterno;  // Con guión bajo!
            private String apellido_materno;  // Con guión bajo!
            private String codigo_verificacion;
            
            public String getNumero() { return numero; }
            public void setNumero(String numero) { this.numero = numero; }
            
            public String getNombres() { return nombres; }
            public void setNombres(String nombres) { this.nombres = nombres; }
            
            public String getNombre_completo() { return nombre_completo; }
            public void setNombre_completo(String nombre_completo) { this.nombre_completo = nombre_completo; }
            
            public String getApellidoPaterno() { return apellido_paterno; }
            public void setApellidoPaterno(String apellido_paterno) { this.apellido_paterno = apellido_paterno; }
            
            public String getApellidoMaterno() { return apellido_materno; }
            public void setApellidoMaterno(String apellido_materno) { this.apellido_materno = apellido_materno; }
            
            public String getApellido_paterno() { return apellido_paterno; }
            public void setApellido_paterno(String apellido_paterno) { this.apellido_paterno = apellido_paterno; }
            
            public String getApellido_materno() { return apellido_materno; }
            public void setApellido_materno(String apellido_materno) { this.apellido_materno = apellido_materno; }
            
            public String getCodigo_verificacion() { return codigo_verificacion; }
            public void setCodigo_verificacion(String codigo_verificacion) { this.codigo_verificacion = codigo_verificacion; }
            
            @Override
            public String toString() {
                return "Data{numero='" + numero + "', nombres='" + nombres + 
                       "', nombre_completo='" + nombre_completo +
                       "', apellido_paterno='" + apellido_paterno + 
                       "', apellido_materno='" + apellido_materno + "'}";
            }
        }
    }
}