package projectsai.saibackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projectsai.saibackend.domain.Role;
import projectsai.saibackend.repository.RoleRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service @Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoleService {

    @PersistenceContext EntityManager em;
    private final RoleRepository roleRepository;

    @Transactional
    public Long saveRole(Role role) {
        return roleRepository.addRole(role);
    }

    public Role findByPosition(String position) {
        return roleRepository.findByPosition(position);
    }
}
