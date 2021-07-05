package cinema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieCommand {

    @NotBlank
    private String title;

    private LocalDateTime startTime;

    @Min(20)
    private Integer numberOfSeats;
}

//Az alapértelmezett URL-n lehessen új filmet felvenni. Ekkor a címet az időpontot, és a max helyet adjuk meg.
// Természetesen új film létrehozásánál a szabad helyek száma a maximális ülőhellyel egyezik meg
////

//    * Új film címe nem lehet üres, és legalább 20 helynek kell lennie a maximumnak