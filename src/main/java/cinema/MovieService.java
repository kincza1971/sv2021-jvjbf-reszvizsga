package cinema;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class MovieService {

    private final List<Movie> movies = new ArrayList<>();

    private AtomicLong idGenerator = new AtomicLong();

    private ModelMapper modelMapper;

    @Autowired
    public MovieService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<MovieDTO> getMovies(Optional<String> title) {
        return movies.stream()
                .filter(m -> title.isEmpty() || m.getTitle().equalsIgnoreCase(title.get()))
                .map(m -> modelMapper.map(m, MovieDTO.class))
                .toList();
    }


    public MovieDTO findMovieById(Long id) {
        final Movie movie = findById(id);
        return modelMapper.map(movie,MovieDTO.class);
    }

    private Movie findById(Long id) {
        return movies.stream()
                .filter(m -> m.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find movie with this id: " + id));
    }

    public MovieDTO createMovie(CreateMovieCommand command) {
        Movie movie = new Movie(idGenerator.incrementAndGet(),
                command.getTitle(),command.getStartTime(),
                command.getNumberOfSeats(),
                command.getNumberOfSeats()
        );
        movies.add(movie);
        return modelMapper.map(movie, MovieDTO.class);
    }

    public MovieDTO bookingById(Long id, CreateReservationCommand command) {
        Movie movie = findById(id);
        movie.booking(command.getNumberOfSeats());
        return modelMapper.map(movie, MovieDTO.class);
    }

    public MovieDTO updateMovieDateById(Long id, UpdateDateCommand command) {
        Movie movie = findById(id);
        movie.setDate(command.getDate());
        return modelMapper.map(movie, MovieDTO.class);
    }

    public void deleteAllMovie() {
        idGenerator = new AtomicLong();
        movies.clear();
    }
}

//A `MovieService` osztály tárolja egy listában a filmeket. Kezdetben a lista üres. Ez az osztály felelős az egyedi azonosítók kiosztásáért is.
