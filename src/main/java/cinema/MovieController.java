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

// ++  A `MoviController` oszt??ly alap??rtelmezzetten a `api/cinema` URL-n v??rja a k??r??seket ??s a k??vetkez?? funkci??kat val??s??tja meg!
//
// ++ Le lehet k??rdezni az ??sszes filmet, ??s opcion??lisan a film c??m??re is r?? lehet sz??rni. Ekkor mindig a c??met, az id??pontot ??s szabad helyek sz??m??t adjuk vissza!
//
// ++ Lehessen a `/{id}` URL-n id alapj??n egy filmet el??rni. (c??m, id??pont, szabad_helyek)
//
// ++ Az alap??rtelmezett URL-n lehessen ??j filmet felvenni. Ekkor a c??met az id??pontot, ??s a max helyet adjuk meg.
//    Term??szetesen ??j film l??trehoz??s??n??l a szabad helyek sz??ma a maxim??lis ??l??hellyel egyezik meg
//
// ++  Lehessen egy filmre foglalni a `/{id}/reserve` URL-n kerszt??l. Ekkor egy sz??mot v??runk a t??rzsben, hogy mennyi ??l??helyet szeretn??nek foglalni
//
// ++ Lehessen egy film id??pontj??t friss??teni egy ??j d??tumra.
//
//  Lehessen t??r??lni az ??sszes filmet.
//
//* A k??vetkez?? szempontokat vegy??k m??g figyelembe:
//    * ??j film c??me nem lehet ??res, ??s legal??bb 20 helynek kell lennie a maximumnak
//    * Ha a megfelel?? id-n kereszt??l nem tal??lhat?? a film akkor 404-es st??tuszk??ddal t??rj??nk vissza.
//    * Ha t??bb helyet akarunk foglalni mint ah??ny szabad hely van, akkor ne t??rt??njen meg a foglal??s. T??rj??nk vissza 400 BAD_REQUEST st??tuszk??ddal
