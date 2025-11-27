package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.DespesaRequestDto;
import com.barbearia.application.dto.ReceitaExtraRequestDto;
import com.barbearia.application.dto.RelatorioFinanceiroDto;
import com.barbearia.application.services.ComissaoService;
import com.barbearia.application.services.FinanceiroService;
import com.barbearia.application.services.RelatorioService;
import com.barbearia.application.security.JwtService;
import com.barbearia.domain.enums.CategoriaDespesa;
import com.barbearia.domain.enums.CategoriaReceitaExtra;
import com.barbearia.domain.enums.PeriodoRelatorio;
import com.barbearia.infrastructure.persistence.entities.JpaDespesa;
import com.barbearia.infrastructure.persistence.entities.JpaReceita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceiroControllerTest {

    @Mock
    private FinanceiroService financeiroService;

    @Mock
    private ComissaoService comissaoService;

    @Mock
    private RelatorioService relatorioService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private FinanceiroController financeiroController;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
    }

    @SuppressWarnings("null")
    @Test
    void deveAdicionarReceitaComSucesso() {
        // Arrange
        when(jwtService.extractClaim(anyString(), eq("userId"))).thenReturn(1L);

        ReceitaExtraRequestDto dto = new ReceitaExtraRequestDto(
                BigDecimal.valueOf(100),
                CategoriaReceitaExtra.VENDA_PRODUTO,
                "Venda de produto",
                LocalDate.now());

        JpaReceita receita = new JpaReceita();
        receita.setId(1L);

        when(financeiroService.adicionarReceita(eq(1L), any(ReceitaExtraRequestDto.class))).thenReturn(receita);

        // Act
        ResponseEntity<JpaReceita> response = financeiroController.adicionarReceita(dto, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @SuppressWarnings("null")
    @Test
    void deveAdicionarDespesaComSucesso() {
        // Arrange
        when(jwtService.extractClaim(anyString(), eq("userId"))).thenReturn(1L);

        DespesaRequestDto dto = new DespesaRequestDto(
                BigDecimal.valueOf(50),
                CategoriaDespesa.ALUGUEL,
                "Aluguel",
                LocalDate.now());

        JpaDespesa despesa = new JpaDespesa();
        despesa.setId(1L);

        when(financeiroService.adicionarDespesa(eq(1L), any(DespesaRequestDto.class))).thenReturn(despesa);

        // Act
        ResponseEntity<JpaDespesa> response = financeiroController.adicionarDespesa(dto, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void deveGerarRelatorioFinanceiroComSucesso() {
        // Arrange
        when(jwtService.extractClaim(anyString(), eq("userId"))).thenReturn(1L);

        RelatorioFinanceiroDto relatorio = new RelatorioFinanceiroDto(
                PeriodoRelatorio.MES,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(23, 59, 59),
                BigDecimal.valueOf(1000),
                10L,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(33.33),
                List.of());
        when(financeiroService.gerarRelatorioFinanceiro(eq(1L), any(PeriodoRelatorio.class))).thenReturn(relatorio);

        // Act
        ResponseEntity<?> response = financeiroController.obterRelatorioFinanceiro(PeriodoRelatorio.MES, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
