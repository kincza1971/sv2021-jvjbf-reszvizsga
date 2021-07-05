package cinema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private Long id;
    private String title;
    private LocalDateTime date;
    private Integer numberOfSeats;
    private Integer freeSpaces;

    public void booking(int placesToBook) {
        if (freeSpaces >= placesToBook) {
            freeSpaces -= placesToBook;
        } else {
            throw new IllegalStateException("Not enough free place");
        }
    }
}

//Az alap entitás a film. Minden filmnek van egy azonosítója egy címe, egy pontos időpont, hogy mikor játszák,
// a férőhelyek maximális száma és a szabad helyek száma.
//
//A `Movie` osztályban továbbá szerepeljen egy metódus ami paraméterül vár egy egész számot, ami a beérkezett foglalás szám.
// Ha van elég szabad hely akkor paraméter értékét vonjuk le a szabad helyek számából, különben dobjunk IllegalStateExceptiont!
