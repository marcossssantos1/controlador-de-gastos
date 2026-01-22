# GUIA DE IMPLEMENTAÇÃO - Arquivos Restantes

Este guia fornece o código completo para todos os arquivos que faltam implementar.

## Services

### 1. AuthService.java
```java
package com.example.controle.service;

import com.example.controle.exception.BusinessException;
import com.example.controle.model.dto.*;
import com.example.controle.model.entity.Usuario;
import com.example.controle.repository.UsuarioRepository;
import com.example.controle.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

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

        String token = jwtUtil.generateToken(usuario);
        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getNome());
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = jwtUtil.generateToken(usuario);

        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getNome());
    }
}
```

### 2. UserDetailsServiceImpl.java
```java
package com.example.controle.security;

import com.example.controle.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
}
```

### 3. CategoriaService.java
```java
package com.example.controle.service;

import com.example.controle.exception.BusinessException;
import com.example.controle.exception.ResourceNotFoundException;
import com.example.controle.mapper.CategoriaMapper;
import com.example.controle.model.dto.*;
import com.example.controle.model.entity.Categoria;
import com.example.controle.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodas() {
        log.debug("Listando todas as categorias");
        List<Categoria> categorias = categoriaRepository.findAllOrdenadas();
        return categoriaMapper.toResponseDTOList(categorias);
    }

    @Transactional(readOnly = true)
    public CategoriaResponseDTO buscarPorId(Long id) {
        log.debug("Buscando categoria por ID: {}", id);
        Categoria categoria = buscarEntidadePorId(id);
        return categoriaMapper.toResponseDTO(categoria);
    }

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO requestDTO) {
        log.info("Criando nova categoria: {}", requestDTO.getNome());

        if (categoriaRepository.existsByNome(requestDTO.getNome())) {
            throw new BusinessException("Já existe uma categoria com o nome: " + requestDTO.getNome());
        }

        Categoria categoria = categoriaMapper.toEntity(requestDTO);
        Categoria categoriaSalva = categoriaRepository.save(categoria);

        log.info("Categoria criada com ID: {}", categoriaSalva.getId());
        return categoriaMapper.toResponseDTO(categoriaSalva);
    }

    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO requestDTO) {
        log.info("Atualizando categoria ID: {}", id);

        Categoria categoria = buscarEntidadePorId(id);

        if (!categoria.getNome().equals(requestDTO.getNome()) &&
            categoriaRepository.existsByNome(requestDTO.getNome())) {
            throw new BusinessException("Já existe uma categoria com o nome: " + requestDTO.getNome());
        }

        categoriaMapper.updateEntityFromDTO(requestDTO, categoria);
        Categoria categoriaAtualizada = categoriaRepository.save(categoria);

        log.info("Categoria atualizada: {}", id);
        return categoriaMapper.toResponseDTO(categoriaAtualizada);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando categoria ID: {}", id);

        Categoria categoria = buscarEntidadePorId(id);

        if (!categoria.getGastos().isEmpty()) {
            throw new BusinessException("Não é possível deletar categoria com gastos associados");
        }

        categoriaRepository.delete(categoria);
        log.info("Categoria deletada: {}", id);
    }

    public Categoria buscarEntidadePorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));
    }
}
```

### 4. GastoService.java (ARQUIVO GRANDE - VER PRÓXIMO BLOCO)

### 5. DashboardService.java (VER PRÓXIMO BLOCO)

### 6. PdfService.java (VER PRÓXIMO BLOCO)

## Controllers

### 1. AuthController.java
```java
package com.example.controle.controller;

import com.example.controle.model.dto.*;
import com.example.controle.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints de autenticação e registro")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Fazer login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
```

### 2. CategoriaController.java
```java
package com.example.controle.controller;

import com.example.controle.model.dto.*;
import com.example.controle.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Categorias", description = "Gerenciamento de categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as categorias")
    public ResponseEntity<List<CategoriaResponseDTO>> listarTodas() {
        List<CategoriaResponseDTO> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Long id) {
        CategoriaResponseDTO categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar nova categoria (apenas ADMIN)")
    public ResponseEntity<CategoriaResponseDTO> criar(@Valid @RequestBody CategoriaRequestDTO requestDTO) {
        CategoriaResponseDTO categoria = categoriaService.criar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar categoria (apenas ADMIN)")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO requestDTO) {
        CategoriaResponseDTO categoria = categoriaService.atualizar(id, requestDTO);
        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deletar categoria (apenas ADMIN)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
```

CONTINUA NO PRÓXIMO ARQUIVO (GUIA_IMPLEMENTACAO_PARTE2.md)...
