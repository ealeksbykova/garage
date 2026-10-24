package com.lab;
import com.dto.WheelRequest;
import com.entity.Wheel;
import com.mapper.WheelMapper;
import com.service.WheelService;
import com.repository.WheelRepository;

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
class WheelServiceTest {
    @Mock WheelRepository repository;

    final WheelMapper mapper = Mappers.getMapper(WheelMapper.class);
    WheelService service;
    WheelRequest request;
    @BeforeEach void setup() {
        service = new WheelService(repository, mapper);
        request = new ObjectMapper().convertValue(ApiTestBase.payload("wheels"), WheelRequest.class);
    }
    @Test @DisplayName("M04-01") void create() {
        when(repository.save(any())).thenAnswer(call -> { Wheel entity=call.getArgument(0); entity.setId(77L); return entity; });
        var response = service.create(request);
        assertEquals(77L,response.getId());
        assertEquals(request.getTireBrand(),response.getTireBrand());
        var expected = new ObjectMapper().valueToTree(request);
        var actual = new ObjectMapper().findAndRegisterModules().valueToTree(response);
        expected.fields().forEachRemaining(e -> assertEquals(e.getValue(),actual.get(e.getKey()),e.getKey()));
        verify(repository).save(any());
    }
    @Test @DisplayName("M04-02") void missing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,()->service.findById(999L));
        verify(repository,never()).save(any());
    }
    @Test @DisplayName("M04-03") void update() {
        var entity=mapper.toEntity(request); entity.setId(77L);
        when(repository.findById(77L)).thenReturn(Optional.of(entity));
        request.setTireBrand("Pirelli");
        var response=service.update(77L,request);
        assertEquals(77L,response.getId());
        assertEquals("Pirelli",response.getTireBrand());
        assertEquals("Pirelli",mapper.toResponse(entity).getTireBrand());
        verify(repository,never()).save(any());
    }
    @Test @DisplayName("M04-04") void deleteMissing() {
        when(repository.existsById(999L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,()->service.delete(999L));
        verify(repository,never()).deleteById(any());
    }
}
