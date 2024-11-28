package org.example.backend_wakanda_salud.util;

import java.sql.Time;
import java.util.List;

public class HorarioNoDisponibleException extends RuntimeException {
  private final List<Time[]> horariosSugeridos;

  public HorarioNoDisponibleException(String mensaje, List<Time[]> horariosSugeridos) {
    super(mensaje);
    this.horariosSugeridos = horariosSugeridos;
  }

  public List<Time[]> getHorariosSugeridos() {
    return horariosSugeridos;
  }
}
