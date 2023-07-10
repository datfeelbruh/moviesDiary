package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.entities.Role;
import sobad.code.movies_diary.repositories.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        Role role = new Role("ROLE_USER");
        return roleRepository.save(role);
    }
}
