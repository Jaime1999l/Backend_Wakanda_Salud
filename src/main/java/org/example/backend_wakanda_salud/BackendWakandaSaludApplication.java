package org.example.backend_wakanda_salud;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.example.backend_wakanda_salud.domain.centroSalud.CentroSalud;
import org.example.backend_wakanda_salud.service.citas.SistemaCitasService;
import org.example.backend_wakanda_salud.repos.CentroSaludRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.File;
import java.util.Collections;
import java.util.List;


@SpringBootApplication(scanBasePackages = "org.example.backend_wakanda_salud")
@EnableDiscoveryClient
public class BackendWakandaSaludApplication {

    public static void main(String[] args) {
        runAllTasks();
        SpringApplication.run(BackendWakandaSaludApplication.class, args);
    }

    @Bean
    CommandLineRunner run(CentroSaludRepository centroSaludRepository, SistemaCitasService sistemaCitasService) {


        return args -> {
            // Crear centros de salud en Wakanda
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

            // Guardar los centros en la base de datos
            List<CentroSalud> centros = centroSaludRepository.saveAll(List.of(centro1, centro2, centro3));

            // Crear un sistema de citas para cada centro de salud
            centros.forEach(centro -> {
                Long sistemaCitasId = sistemaCitasService.crearSistemaCitas(
                        "Sistema de citas para " + centro.getNombre(), centro.getId());
                System.out.println("Sistema de citas creado para " + centro.getNombre() + " con ID: " + sistemaCitasId);
            });
        };
    }

    private static void runAllTasks() {
        runMavenCleanInstall();
        if (!isDockerImageExists("backend-wakanda-salud")) {
            runDockerComposeBuild();
        }
        if (!isDockerContainerRunning("backend-wakanda-salud")) {
            runDockerComposeUp();
        }
    }

    private static void runMavenCleanInstall() {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File("pom.xml"));
        request.setGoals(Collections.singletonList("clean install"));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenExecutable(new File("mvnw")); // Use the Maven Wrapper script

        try {
            invoker.execute(request);
            System.out.println("Maven build completed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during Maven build.");
        }
    }

    private static void runDockerComposeBuild() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("docker-compose", "-f", "C:/Users/Marcosss/Documents/GitHub/Backend_Wakanda_Salud/docker-compose.yml", "build");
        processBuilder.inheritIO();

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error: docker-compose build failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runDockerComposeUp() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("docker-compose", "-f", "C:/Users/Marcosss/Documents/GitHub/Backend_Wakanda_Salud/docker-compose.yml", "up", "-d");
        processBuilder.inheritIO();

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error: docker-compose up failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isDockerImageExists(String imageName) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("docker", "images", "-q", imageName);
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0 && process.getInputStream().read() != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isDockerContainerRunning(String containerName) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("docker", "ps", "-q", "--filter", "name=" + containerName);
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0 && process.getInputStream().read() != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
