package cinema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cinema")
public class MovieController {

    MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping()
    public List<MovieDTO> getMovies(@RequestParam Optional<String> title) {

        return movieService.getMovies(title);

    }

    @GetMapping("/{id}")
    public MovieDTO getMovieById(@PathVariable Long id) {
        return movieService.findMovieById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieDTO createMovie(@Valid @RequestBody CreateMovieCommand command) {
        return movieService.createMovie(command);
    }

    @PostMapping("/{id}/reserve")
    public MovieDTO bookingById(@PathVariable("id") Long id, @RequestBody CreateReservationCommand command) {
        return movieService.bookingById(id, command);
    }

    @PutMapping("/{id}")
    public MovieDTO updateMovieDateById(@PathVariable Long id, @RequestBody UpdateDateCommand command)  {
        return movieService.updateMovieDateById(id, command);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllMovie() {
        movieService.deleteAllMovie();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Problem> handleNotFound(IllegalArgumentException exception) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/not-found"))
                .withTitle("Not found")
                .withStatus(Status.NOT_FOUND)
                .withDetail(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);

    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Problem> handleNotEnoughSpace(IllegalStateException exception) {
        Problem problem = Problem.builder()
                .withType(URI.create("cinema/bad-reservation"))
                .withTitle("Bad reservation")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(exception.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);

    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handleNotValidException(MethodArgumentNotValidException exception) {
        List<Violation> violations =
                exception.getBindingResult().getFieldErrors().stream()
                        .map(fe -> new Violation(fe.getField(), fe.getDefaultMessage()))
                        .collect(Collectors.toList());

        Problem problem = Problem.builder()
                .withType(URI.create("cinema/validation-error"))
                .withTitle("Cinema validation error")
                .withStatus(Status.BAD_REQUEST)
                .withDetail(exception.getMessage())
                .with("violation", violations)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }



}

// ++  A `MoviController` osztály alapértelmezzetten a `api/cinema` URL-n várja a kéréseket és a következő funkciókat valósítja meg!
//
// ++ Le lehet kérdezni az összes filmet, és opcionálisan a film címére is rá lehet szűrni. Ekkor mindig a címet, az időpontot és szabad helyek számát adjuk vissza!
//
// ++ Lehessen a `/{id}` URL-n id alapján egy filmet elérni. (cím, időpont, szabad_helyek)
//
// ++ Az alapértelmezett URL-n lehessen új filmet felvenni. Ekkor a címet az időpontot, és a max helyet adjuk meg.
//    Természetesen új film létrehozásánál a szabad helyek száma a maximális ülőhellyel egyezik meg
//
// ++  Lehessen egy filmre foglalni a `/{id}/reserve` URL-n kersztül. Ekkor egy számot várunk a törzsben, hogy mennyi ülőhelyet szeretnének foglalni
//
// ++ Lehessen egy film időpontját frissíteni egy új dátumra.
//
//  Lehessen törölni az összes filmet.
//
//* A következő szempontokat vegyük még figyelembe:
//    * Új film címe nem lehet üres, és legalább 20 helynek kell lennie a maximumnak
//    * Ha a megfelelő id-n keresztül nem található a film akkor 404-es státuszkóddal térjünk vissza.
//    * Ha több helyet akarunk foglalni mint ahány szabad hely van, akkor ne történjen meg a foglalás. Térjünk vissza 400 BAD_REQUEST státuszkóddal
