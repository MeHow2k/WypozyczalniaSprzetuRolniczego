package pl.mehow2k.dataLoader;
//Dodanie danych na samym poczatku

import pl.mehow2k.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.mehow2k.repositories.RoleRepository;
import pl.mehow2k.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;
/**
 * Komponent ładowania danych początkowych do bazy danych po uruchomieniu aplikacji.
 * Ta klasa wypełnia bazę danych przykładowymi danymi, w tym książkami, czytelnikami, wypożyczeniami,
 * logami oraz użytkownikami z odpowiednimi rolami.
 * @author Michał Pasieka
 * @version 1.0, 22.05.2026
 */
@Component
public class DataLoader implements ApplicationRunner {
//    private WypozyczenieRepository wypRepository;
//    private KsiazkaRepository ksiazkaRepository;
//    private CzytelnikRepository czytelnikRepository;
//    private  MyLogRecordRepository myLogRecordRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder)
    {
//        this.wypRepository = wypRepository;
//        this.ksiazkaRepository = ksiazkaRepository;
//        this.czytelnikRepository = czytelnikRepository;
//        this.myLogRecordRepository=myLogRecordRepository;
        this.userRepository=userRepository;
        this.roleRepository=roleRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public void run(ApplicationArguments args)
    {
        try
        {

//            MyLogRecord myLogRecord = new MyLogRecord();
//            myLogRecord.setMessage("Testowy log");
//            myLogRecord.setTimestamp("1970-01-01 00:00:00");
//            myLogRecord.setLevel("WARNING");
//
//            myLogRecordRepository.saveAndFlush(myLogRecord);
//
//            MyLogRecord myLogRecord2 = new MyLogRecord();
//            myLogRecord2.setMessage("Testowy log2");
//            myLogRecord2.setTimestamp("2000-01-01 00:00:00");
//            myLogRecord2.setLevel("SEVERE");
//
//            myLogRecordRepository.saveAndFlush(myLogRecord2);
//
//            MyLogRecord myLogRecord3 = new MyLogRecord();
//            myLogRecord3.setMessage("Testowy log22");
//            myLogRecord3.setTimestamp("2024-01-01 00:00:00");
//            myLogRecord3.setLevel("INFO");
//
//            myLogRecordRepository.saveAndFlush(myLogRecord3);
//
//// Książki
//            List<Ksiazka> ksiazki = Arrays.asList(
//                    new Ksiazka("Władca Pierścieni", "J.R.R. Tolkien", "978-83-7659-394-4"),
//                    new Ksiazka("Hobbit", "J.R.R. Tolkien", "978-83-7469-613-5"),
//                    new Ksiazka("Harry Potter i Kamień Filozoficzny", "J.K. Rowling", "978-83-7184-755-4"),
//                    new Ksiazka("Gra o Tron", "George R.R. Martin", "978-83-7480-197-4"),
//                    new Ksiazka("Rok 1984", "George Orwell", "978-83-287-1534-0"),
//                    new Ksiazka("Duma i uprzedzenie", "Jane Austen", "978-83-7885-876-8"),
//                    new Ksiazka("Zbrodnia i kara", "Fiodor Dostojewski", "978-83-233-3366-1"),
//                    new Ksiazka("Mistrz i Małgorzata", "Michaił Bułhakow", "978-83-7392-450-1"),
//                    new Ksiazka("Lalka", "Bolesław Prus", "978-83-7392-460-0"),
//                    new Ksiazka("Pan Tadeusz", "Adam Mickiewicz", "978-83-7392-470-9"),
//                    new Ksiazka("Potop", "Henryk Sienkiewicz", "978-83-7392-480-8"),
//                    new Ksiazka("Quo Vadis", "Henryk Sienkiewicz", "978-83-7392-490-7"),
//                    new Ksiazka("Krzyżacy", "Henryk Sienkiewicz", "978-83-7392-500-3"),
//                    new Ksiazka("W pustyni i w puszczy", "Henryk Sienkiewicz", "978-83-7392-510-2"),
//                    new Ksiazka("Chłopi", "Władysław Reymont", "978-83-7392-520-1"),
//                    new Ksiazka("Ferdydurke", "Witold Gombrowicz", "978-83-7392-530-0"),
//                    new Ksiazka("Solaris", "Stanisław Lem", "978-83-7392-540-9"),
//                    new Ksiazka("Wiedźmin", "Andrzej Sapkowski", "978-83-7392-550-8"),
//                    new Ksiazka("Mały Książę", "Antoine de Saint-Exupéry", "978-83-7392-560-7"),
//                    new Ksiazka("Bracia Karamazow", "Fiodor Dostojewski", "978-83-7392-570-6")
//            );
//
//            ksiazki.forEach(ksiazka -> ksiazkaRepository.saveAndFlush(ksiazka));
//
//// Czytelnicy
//            List<Czytelnik> czytelnicy = Arrays.asList(
//                    new Czytelnik("Adam", "Nowak"),
//                    new Czytelnik("Anna", "Kowal"),
//                    new Czytelnik("Ewa", "Wójcik"),
//                    new Czytelnik("Jan", "Kowalski"),
//                    new Czytelnik("Piotr", "Wiśniewski"),
//                    new Czytelnik("Paweł", "Wróbel"),
//                    new Czytelnik("Marek", "Kozłowski"),
//                    new Czytelnik("Tomasz", "Jankowski"),
//                    new Czytelnik("Krzysztof", "Mazur"),
//                    new Czytelnik("Andrzej", "Lewandowski"),
//                    new Czytelnik("Michał", "Zieliński"),
//                    new Czytelnik("Rafał", "Szymański"),
//                    new Czytelnik("Dariusz", "Wojciechowski"),
//                    new Czytelnik("Łukasz", "Kwiatkowski"),
//                    new Czytelnik("Grzegorz", "Krawczyk"),
//                    new Czytelnik("Marcin", "Kaczmarek"),
//                    new Czytelnik("Kamil", "Piotrowski"),
//                    new Czytelnik("Jarosław", "Grabowski"),
//                    new Czytelnik("Patryk", "Pawlak"),
//                    new Czytelnik("Dominik", "Dąbrowski")
//            );
//
//            czytelnicy.forEach(czytelnik -> czytelnikRepository.saveAndFlush(czytelnik));
//
//// Wypożyczenia
//            List<Wypozyczenie> wypozyczenia = Arrays.asList(
//                    new Wypozyczenie("01-01-2024"),
//                    new Wypozyczenie("02-02-2024"),
//                    new Wypozyczenie("03-03-2024","22-03-2024"),
//                    new Wypozyczenie("04-04-2024"),
//                    new Wypozyczenie("05-05-2024"),
//                    new Wypozyczenie("06-06-2024","22-03-2024"),
//                    new Wypozyczenie("07-07-2024"),
//                    new Wypozyczenie("08-08-2024"),
//                    new Wypozyczenie("09-09-2023", "22-02-2024"),
//                    new Wypozyczenie("01-01-2024"),
//                    new Wypozyczenie("02-02-2024"),
//                    new Wypozyczenie("03-03-2024"),
//                    new Wypozyczenie("04-04-2024"),
//                    new Wypozyczenie("05-05-2024"),
//                    new Wypozyczenie("06-06-2024"),
//                    new Wypozyczenie("07-07-2024"),
//                    new Wypozyczenie("08-08-2024"),
//                    new Wypozyczenie("10-10-2023", "22-03-2024"),
//                    new Wypozyczenie("07-07-2024"),
//                    new Wypozyczenie("08-08-2024")
//            );
//
//            wypozyczenia.get(0).setKsiazka(ksiazki.get(0)); wypozyczenia.get(0).setCzytelnik(czytelnicy.get(9));
//            wypozyczenia.get(1).setKsiazka(ksiazki.get(1)); wypozyczenia.get(1).setCzytelnik(czytelnicy.get(8));
//            wypozyczenia.get(2).setKsiazka(ksiazki.get(2)); wypozyczenia.get(2).setCzytelnik(czytelnicy.get(7));
//            wypozyczenia.get(3).setKsiazka(ksiazki.get(3)); wypozyczenia.get(3).setCzytelnik(czytelnicy.get(6));
//            wypozyczenia.get(4).setKsiazka(ksiazki.get(4)); wypozyczenia.get(4).setCzytelnik(czytelnicy.get(5));
//            wypozyczenia.get(5).setKsiazka(ksiazki.get(5)); wypozyczenia.get(5).setCzytelnik(czytelnicy.get(4));
//            wypozyczenia.get(6).setKsiazka(ksiazki.get(6)); wypozyczenia.get(6).setCzytelnik(czytelnicy.get(3));
//            wypozyczenia.get(7).setKsiazka(ksiazki.get(7)); wypozyczenia.get(7).setCzytelnik(czytelnicy.get(2));
//            wypozyczenia.get(8).setKsiazka(ksiazki.get(8)); wypozyczenia.get(8).setCzytelnik(czytelnicy.get(4));
//            wypozyczenia.get(9).setKsiazka(ksiazki.get(9)); wypozyczenia.get(9).setCzytelnik(czytelnicy.get(2));
//            wypozyczenia.get(10).setKsiazka(ksiazki.get(5)); wypozyczenia.get(10).setCzytelnik(czytelnicy.get(13));
//            wypozyczenia.get(11).setKsiazka(ksiazki.get(11)); wypozyczenia.get(11).setCzytelnik(czytelnicy.get(19));
//            wypozyczenia.get(12).setKsiazka(ksiazki.get(14)); wypozyczenia.get(12).setCzytelnik(czytelnicy.get(7));
//            wypozyczenia.get(13).setKsiazka(ksiazki.get(2)); wypozyczenia.get(13).setCzytelnik(czytelnicy.get(0));
//            wypozyczenia.get(14).setKsiazka(ksiazki.get(18)); wypozyczenia.get(14).setCzytelnik(czytelnicy.get(3));
//            wypozyczenia.get(15).setKsiazka(ksiazki.get(6)); wypozyczenia.get(15).setCzytelnik(czytelnicy.get(9));
//            wypozyczenia.get(16).setKsiazka(ksiazki.get(12)); wypozyczenia.get(16).setCzytelnik(czytelnicy.get(11));
//            wypozyczenia.get(17).setKsiazka(ksiazki.get(1)); wypozyczenia.get(17).setCzytelnik(czytelnicy.get(2));
//            wypozyczenia.get(18).setKsiazka(ksiazki.get(8)); wypozyczenia.get(18).setCzytelnik(czytelnicy.get(15));
//            wypozyczenia.get(19).setKsiazka(ksiazki.get(4)); wypozyczenia.get(19).setCzytelnik(czytelnicy.get(5));
//
//
//            wypozyczenia.forEach(wypozyczenie -> wypRepository.saveAndFlush(wypozyczenie));

            ///USERS' initialize
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);

            Role staffRole = new Role();
            staffRole.setName("STAFF");
            roleRepository.save(staffRole);

            Role clientRole = new Role();
            clientRole.setName("CLIENT");
            roleRepository.save(clientRole);

            //----------------

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.getRoles().add(adminRole);
            admin.getRoles().add(clientRole);
            userRepository.save(admin);

            User staff = new User();
            staff.setUsername("staff");
            staff.setPassword(passwordEncoder.encode("staff123"));
            staff.getRoles().add(staffRole);
            userRepository.save(staff);

            User client = new User();
            client.setUsername("client");
            client.setPassword(passwordEncoder.encode("client123"));
            client.getRoles().add(clientRole);
            userRepository.save(client);

            User pasieka = new User();
            pasieka.setUsername("pasieka");
            pasieka.setPassword(passwordEncoder.encode("pasieka123"));
            pasieka.getRoles().add(adminRole);
            pasieka.getRoles().add(clientRole);
            pasieka.getRoles().add(staffRole);
            userRepository.save(pasieka);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
