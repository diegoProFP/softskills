package es.ggm.infor.softskills.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfesorDTO {

    @NotNull(message = "El identificador del profesor es obligatorio.")
    @Positive(message = "El identificador del profesor debe ser positivo.")
    private Long id;

    private boolean administrador;
}
