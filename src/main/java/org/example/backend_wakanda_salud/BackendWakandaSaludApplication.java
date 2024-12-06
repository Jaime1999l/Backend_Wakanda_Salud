package org.example.backend_wakanda_salud;

import org.example.backend_wakanda_salud.domain.centroSalud.CentroSalud;
import org.example.backend_wakanda_salud.repos.CentroSaludRepository;
import org.example.backend_wakanda_salud.service.citas.SistemaCitasService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class BackendWakandaSaludApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendWakandaSaludApplication.class, args);
    }

    @Bean
    CommandLineRunner run(CentroSaludRepository centroSaludRepository, SistemaCitasService sistemaCitasService) {
        return args -> {
            // Create health centers in Wakanda
            CentroSalud centro1 = new CentroSalud();
            centro1.setNombre("Centro de Salud Birnin Zana");
            centro1.setDireccion("Avenida del Rey T'Challa, Birnin Zana, Wakanda");
            centro1.setTelefono("123456789");

            CentroSalud centro2 = new CentroSalud();
            centro2.setNombre("Centro de Salud Distrito de los Minerales");
            centro2.setDireccion("Calle Vibranium, Distrito de los Minerales, Wakanda");
            centro2.setTelefono("987654321");

            CentroSalud centro3 = new CentroSalud();
            centro3.setNombre("Centro de Salud Monte Bashenga");
            centro3.setDireccion("Camino al Monte Bashenga, Wakanda");
            centro3.setTelefono("456789123");

            // Save the centers in the database
            List<CentroSalud> centros = centroSaludRepository.saveAll(List.of(centro1, centro2, centro3));

            // Create an appointment system for each health center
            centros.forEach(centro -> {
                Long sistemaCitasId = sistemaCitasService.crearSistemaCitas(
                        "Sistema de citas para " + centro.getNombre(), centro.getId());
                System.out.println("Appointment system created for " + centro.getNombre() + " with ID: " + sistemaCitasId);
            });
        };
    }
}

