package Services;

import Domain.Data.Attributes;
import Domain.Data.Pygmy;
import Repository.AttributesRepository;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Log4j2
public class AttributesService {
    public static Attributes createAttribute(UUID uuid){
        return AttributesRepository.createAttributes(uuid);
    }

    public static void saveAttributes(Attributes attributes){
        AttributesRepository.saveAttributes(attributes);
    }

    public static void setCurrentRoom(UUID uuid, int roomId){
        AttributesRepository.setCurrentRoom(uuid, roomId);
    }

    public static Attributes getAttributeByUUID(UUID uuid, int roomId) {
        List<Integer> attributesList = AttributesRepository.getAttributeByUUID(uuid);
        log.info("Attributes found: {}.", attributesList);
        return Attributes.builder()
                .uuid(uuid)
                .level(attributesList.get(1))
                .currentAttack(attributesList.get(6))
                .currentDefense(attributesList.get(7))
                .currentAgility(attributesList.get(8))
                .currentLuck(attributesList.get(9))
                .critRate(attributesList.get(10))
                .lifeSteal(attributesList.get(11))
                .armorPen(attributesList.get(12))
                .statusResist(attributesList.get(13))
                .maxHP(attributesList.get(0)*50)
                .currentHP(attributesList.get(0)*50)
                .vigor(attributesList.get(7)/2+attributesList.get(0)*5)
                .currentVigor(attributesList.get(7)/2+attributesList.get(0)*5)
                .currentRoom(roomId)
                .build();
    }

    public static List<Pygmy> getTurnOrder(Map<UUID, Pygmy> pygmyMap, int roomId) {
        List<UUID> uuidList = AttributesRepository.getTurnOrder(roomId);
        List<Pygmy> turnOrder = new ArrayList<>();
        for (UUID uuid:uuidList){
            turnOrder.add(pygmyMap.get(uuid));
            log.info("Added {} to list.", pygmyMap.get(uuid));
        }
        return turnOrder;
    }
}

