package com.example.controle.service;

import com.example.controle.exception.ResourceNotFoundException;
import com.example.controle.mapper.GastoMapper;
import com.example.controle.model.dto.GastoRequestDTO;
import com.example.controle.model.dto.GastoResponseDTO;
import com.example.controle.model.entity.Categoria;
import com.example.controle.model.entity.Gasto;
import com.example.controle.model.entity.Usuario;
import com.example.controle.repository.GastoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GastoServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    @Mock
    private GastoMapper gastoMapper;

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private GastoService gastoService;

    private Usuario usuarioTeste;
    private Categoria categoriaTeste;
    private Gasto gastoTeste;
    private GastoRequestDTO gastoRequestDTO;
    private GastoResponseDTO gastoResponseDTO;

    @BeforeEach
    void setUp() {
        usuarioTeste = new Usuario();
        usuarioTeste.setId(1L);
        usuarioTeste.setEmail("teste@email.com");
        usuarioTeste.setNome("Usuário Teste");

        categoriaTeste = new Categoria();
        categoriaTeste.setId(1L);
        categoriaTeste.setNome("Alimentação");

        gastoTeste = new Gasto();
        gastoTeste.setId(1L);
        gastoTeste.setDescricao("Almoço");
        gastoTeste.setValor(new BigDecimal("50.00"));
        gastoTeste.setCategoria(categoriaTeste);
        gastoTeste.setUsuario(usuarioTeste);
        gastoTeste.setDataGasto(LocalDate.now());

        gastoRequestDTO = new GastoRequestDTO();
        gastoRequestDTO.setDescricao("Almoço");
        gastoRequestDTO.setValor(new BigDecimal("50.00"));
        gastoRequestDTO.setCategoriaId(1L);

        gastoResponseDTO = new GastoResponseDTO();
        gastoResponseDTO.setId(1L);
        gastoResponseDTO.setDescricao("Almoço");
        gastoResponseDTO.setValor(new BigDecimal("50.00"));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuarioTeste);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void deveCriarGastoComSucesso() {
        when(categoriaService.buscarEntidadePorId(1L)).thenReturn(categoriaTeste);
        when(gastoMapper.toEntity(gastoRequestDTO)).thenReturn(gastoTeste);
        when(gastoRepository.save(any(Gasto.class))).thenReturn(gastoTeste);
        when(gastoMapper.toResponseDTO(gastoTeste)).thenReturn(gastoResponseDTO);

        GastoResponseDTO resultado = gastoService.criar(gastoRequestDTO);

        assertNotNull(resultado);
        assertEquals("Almoço", resultado.getDescricao());
        verify(gastoRepository, times(1)).save(any(Gasto.class));
    }

    @Test
    void deveBuscarGastoPorIdComSucesso() {
        when(gastoRepository.findById(1L)).thenReturn(Optional.of(gastoTeste));
        when(gastoMapper.toResponseDTO(gastoTeste)).thenReturn(gastoResponseDTO);

        GastoResponseDTO resultado = gastoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void deveLancarExcecaoQuandoGastoNaoEncontrado() {
        when(gastoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            gastoService.buscarPorId(999L);
        });
    }

    @Test
    void deveDeletarGastoComSucesso() {
        when(gastoRepository.findById(1L)).thenReturn(Optional.of(gastoTeste));

        gastoService.deletar(1L);

        verify(gastoRepository, times(1)).delete(gastoTeste);
    }

    @Test
    void naoDevePermitirAcessoAGastoDeOutroUsuario() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        gastoTeste.setUsuario(outroUsuario);

        when(gastoRepository.findById(1L)).thenReturn(Optional.of(gastoTeste));

        assertThrows(ResourceNotFoundException.class, () -> {
            gastoService.buscarPorId(1L);
        });
    }
}
