package pl.mehow2k.dataLoader;
//Dodanie danych na samym poczatku

import pl.mehow2k.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.mehow2k.repositories.MachineRepository;
import pl.mehow2k.repositories.RoleRepository;
import pl.mehow2k.repositories.UserRepository;

import java.math.BigDecimal;
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
    private MachineRepository machineRepository;
//    private CzytelnikRepository czytelnikRepository;
//    private  MyLogRecordRepository myLogRecordRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(UserRepository userRepository, RoleRepository roleRepository,MachineRepository machineRepository , PasswordEncoder passwordEncoder)
    {
//        this.wypRepository = wypRepository;
        this.machineRepository = machineRepository;
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
// Maszyny rolnicze
            List<Machine> machines = Arrays.asList(
                    new Machine("John Deere 6R", "Traktory", new BigDecimal("550.00"), true),
                    new Machine("New Holland T7", "Traktory", new BigDecimal("500.00"), true),
                    new Machine("Fendt 930 Vario", "Traktory", new BigDecimal("700.00"), true),
                    new Machine("Claas Axion 850", "Traktory", new BigDecimal("620.00"), true),

                    new Machine("Claas Lexion 8900", "Kombajny", new BigDecimal("1500.00"), true),
                    new Machine("John Deere X9", "Kombajny", new BigDecimal("1700.00"), true),
                    new Machine("New Holland CR10.90", "Kombajny", new BigDecimal("1600.00"), false),

                    new Machine("Amazone ZA-TS", "Rozsiewacze", new BigDecimal("250.00"), true),
                    new Machine("Kuhn Axis", "Rozsiewacze", new BigDecimal("230.00"), true),

                    new Machine("Horsch Pronto 6DC", "Siewniki", new BigDecimal("450.00"), true),
                    new Machine("Vaderstad Rapid A600S", "Siewniki", new BigDecimal("520.00"), false),

                    new Machine("Kverneland EG", "Pługi", new BigDecimal("180.00"), true),
                    new Machine("Lemken Diamant 16", "Pługi", new BigDecimal("220.00"), true),

                    new Machine("Pottinger Novacat", "Kosiarki", new BigDecimal("300.00"), true),
                    new Machine("Krone EasyCut", "Kosiarki", new BigDecimal("320.00"), true),

                    new Machine("Manitou MLT 741", "Ładowarki", new BigDecimal("400.00"), true),
                    new Machine("JCB 541-70", "Ładowarki", new BigDecimal("420.00"), false),

                    new Machine("John Deere 8RX", "Traktory", new BigDecimal("900.00"), true),
                    new Machine("Case IH Magnum", "Traktory", new BigDecimal("850.00"), true),
                    new Machine("Deutz-Fahr 7250 TTV", "Traktory", new BigDecimal("680.00"), true)
            );

            machines.forEach(machine -> machineRepository.saveAndFlush(machine));


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
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);

            Role staffRole = new Role();
            staffRole.setName("ROLE_STAFF");
            roleRepository.save(staffRole);

            Role clientRole = new Role();
            clientRole.setName("ROLE_CLIENT");
            roleRepository.save(clientRole);

            //----------------
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.getRoles().add(adminRole);
                admin.getRoles().add(clientRole);
                userRepository.save(admin);
            }
            if (userRepository.findByUsername("staff").isEmpty()) {
                User staff = new User();
                staff.setUsername("staff");
                staff.setPassword(passwordEncoder.encode("staff123"));
                staff.getRoles().add(staffRole);
                userRepository.save(staff);
            }
            if (userRepository.findByUsername("client").isEmpty()) {
                User client = new User();
                client.setUsername("client");
                client.setPassword(passwordEncoder.encode("client123"));
                client.getRoles().add(clientRole);
                userRepository.save(client);
            }
            if (userRepository.findByUsername("pasieka").isEmpty()) {
                User pasieka = new User();
                pasieka.setUsername("pasieka");
                pasieka.setPassword(passwordEncoder.encode("pasieka123"));
                pasieka.getRoles().add(adminRole);
                pasieka.getRoles().add(clientRole);
                pasieka.getRoles().add(staffRole);
                userRepository.save(pasieka);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
