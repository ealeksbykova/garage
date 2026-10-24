package com.lab;
import com.dto.TransmissionRequest;
import com.entity.Transmission;
import com.mapper.TransmissionMapper;
import com.service.TransmissionService;
import com.repository.TransmissionRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class TransmissionServiceTest {
    @Mock TransmissionRepository repository;

    final TransmissionMapper mapper = Mappers.getMapper(TransmissionMapper.class);
    TransmissionService service;
    TransmissionRequest request;
    @BeforeEach void setup() {
        service = new TransmissionService(repository, mapper);
        request = new ObjectMapper().convertValue(ApiTestBase.payload("transmissions"), TransmissionRequest.class);
    }
    @Test @DisplayName("M02-01") void create() {
        when(repository.save(any())).thenAnswer(call -> { Transmission entity=call.getArgument(0); entity.setId(77L); return entity; });
        var response = service.create(request);
        assertEquals(77L,response.getId());
        assertEquals(request.getTransmissionType(),response.getTransmissionType());
        var expected = new ObjectMapper().valueToTree(request);
        var actual = new ObjectMapper().findAndRegisterModules().valueToTree(response);
        expected.fields().forEachRemaining(e -> assertEquals(e.getValue(),actual.get(e.getKey()),e.getKey()));
        verify(repository).save(any());
    }
    @Test @DisplayName("M02-02") void missing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,()->service.findById(999L));
        verify(repository,never()).save(any());
    }
    @Test @DisplayName("M02-03") void update() {
        var entity=mapper.toEntity(request); entity.setId(77L);
        when(repository.findById(77L)).thenReturn(Optional.of(entity));
        request.setTransmissionType("AUTOMATIC");
        var response=service.update(77L,request);
        assertEquals(77L,response.getId());
        assertEquals("AUTOMATIC",response.getTransmissionType());
        assertEquals("AUTOMATIC",mapper.toResponse(entity).getTransmissionType());
        verify(repository,never()).save(any());
    }
    @Test @DisplayName("M02-04") void deleteMissing() {
        when(repository.existsById(999L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,()->service.delete(999L));
        verify(repository,never()).deleteById(any());
    }
}
