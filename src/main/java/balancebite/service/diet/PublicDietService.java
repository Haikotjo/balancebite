package balancebite.service.diet;

import balancebite.dto.diet.DietDTO;
import balancebite.errorHandling.DietNotFoundException;
import balancebite.mapper.DietMapper;
import balancebite.model.diet.Diet;
import balancebite.repository.DietRepository;
import balancebite.service.interfaces.diet.IPublicDietService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicDietService implements IPublicDietService {

    private final DietRepository dietRepository;
    private final DietMapper dietMapper;

    public PublicDietService(DietRepository dietRepository, DietMapper dietMapper) {
        this.dietRepository = dietRepository;
        this.dietMapper = dietMapper;
    }

    @Override
    public List<DietDTO> getAllPublicDiets() {
        return dietRepository.findAll().stream()
                .filter(Diet::isTemplate)
                .map(dietMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DietDTO getPublicDietById(Long id) {
        Diet diet = dietRepository.findById(id)
                .filter(Diet::isTemplate)
                .orElseThrow(() -> new DietNotFoundException("Public diet not found with ID: " + id));
        return dietMapper.toDTO(diet);
    }
}
