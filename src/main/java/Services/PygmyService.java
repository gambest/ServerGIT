package Services;

import Domain.Data.Pygmy;
import Repository.AttributesRepository;
import Repository.PygmyRepository;
import Repository.StatisticsRepository;

import java.util.List;
import java.util.UUID;

public class PygmyService {
    public static Pygmy createPygmy(UUID uuid,String name){
        return PygmyRepository.createPygmy(uuid, name);
    }

    public static void savePygmy(Pygmy pygmy) {
        PygmyRepository.save(pygmy);
    }

    public static void deletePygmy(Pygmy pygmy) {
        PygmyRepository.delete(pygmy);
    }

    public static void updatePygmy(Pygmy pygmy) {
        PygmyRepository.update(pygmy);
    }

    public static List<Pygmy> findAll() {
        return PygmyRepository.findAll();
    }

    public static UUID findByName(String name) {
        return PygmyRepository.findByName(name);
    }
}
