package com.minimarket.security.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true")
public class SecurityDataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo-data.admin-password}")
    private String adminPassword;

    @Value("${app.demo-data.cajero-password}")
    private String cajeroPassword;

    @Value("${app.demo-data.cliente-password}")
    private String clientePassword;

    public SecurityDataInitializer(RolRepository rolRepository,
                                   UsuarioRepository usuarioRepository,
                                   PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Rol admin = role("ROLE_ADMIN");
        Rol cajero = role("ROLE_CAJERO");
        Rol cliente = role("ROLE_CLIENTE");

        user("admin", adminPassword, admin);
        user("cajero", cajeroPassword, cajero);
        user("cliente", clientePassword, cliente);
    }

    private Rol role(String name) {
        return rolRepository.findByNombre(name)
                .orElseGet(() -> rolRepository.save(new Rol(name)));
    }

    private void user(String username, String password, Rol role) {
        if (usuarioRepository.findByUsername(username).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setRoles(Set.of(role));
            usuarioRepository.save(usuario);
        }
    }
}
