//package balancebite.mapper;
//
//import balancebite.dto.VitaminsAndMineralsDTO;
//import balancebite.model.VitaminsAndMinerals;
//import org.springframework.stereotype.Component;
//
//@Component
//public class VitaminsAndMineralsMapper {
//
//    public VitaminsAndMineralsDTO toDTO(VitaminsAndMinerals entity) {
//        if (entity == null) {
//            return null;
//        }
//
//        VitaminsAndMineralsDTO dto = new VitaminsAndMineralsDTO();
//        dto.setVitaminA(entity.getVitaminA());
//        dto.setVitaminC(entity.getVitaminC());
//        dto.setVitaminD(entity.getVitaminD());
//        dto.setVitaminE(entity.getVitaminE());
//        dto.setVitaminK(entity.getVitaminK());
//        dto.setThiamin(entity.getThiamin());
//        dto.setRiboflavin(entity.getRiboflavin());
//        dto.setNiacin(entity.getNiacin());
//        dto.setVitaminB6(entity.getVitaminB6());
//        dto.setFolate(entity.getFolate());
//        dto.setVitaminB12(entity.getVitaminB12());
//        dto.setPantothenicAcid(entity.getPantothenicAcid());
//        dto.setBiotin(entity.getBiotin());
//        dto.setCholine(entity.getCholine());
//        dto.setCalcium(entity.getCalcium());
//        dto.setIron(entity.getIron());
//        dto.setMagnesium(entity.getMagnesium());
//        dto.setPhosphorus(entity.getPhosphorus());
//        dto.setPotassium(entity.getPotassium());
//        dto.setSodium(entity.getSodium());
//        dto.setZinc(entity.getZinc());
//        dto.setCopper(entity.getCopper());
//        dto.setManganese(entity.getManganese());
//        dto.setSelenium(entity.getSelenium());
//        dto.setFluoride(entity.getFluoride());
//        dto.setChromium(entity.getChromium());
//        dto.setIodine(entity.getIodine());
//        dto.setMolybdenum(entity.getMolybdenum());
//
//        return dto;
//    }
//}
