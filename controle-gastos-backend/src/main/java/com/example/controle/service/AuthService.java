package com.example.controle.service;

import com.example.controle.exception.BusinessException;
import com.example.controle.model.dto.AuthResponseDTO;
import com.example.controle.model.dto.LoginRequestDTO;
import com.example.controle.model.dto.RegisterRequestDTO;
import com.example.controle.model.entity.Usuario;
import com.example.controle.repository.UsuarioRepository;
import com.example.controle.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder,
                      JwtUtil jwtUtil,
                      AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("Registrando novo usuário: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(Usuario.Role.USER);
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);
        log.info("Usuário registrado com sucesso: {}", usuario.getEmail());

        String token = jwtUtil.generateToken(usuario);
        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getNome());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        log.info("Tentativa de login: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = jwtUtil.generateToken(usuario);

        log.info("Login realizado com sucesso: {}", usuario.getEmail());
        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getNome());
    }
}
