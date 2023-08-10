package sobad.code.moviesdiary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sobad.code.moviesdiary.entities.Role;
import sobad.code.moviesdiary.repositories.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        Role role = new Role("ROLE_USER");
        return roleRepository.save(role);
    }
}
