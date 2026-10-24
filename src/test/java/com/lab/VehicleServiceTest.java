package com.lab;
import com.dto.VehicleRequest;
import com.entity.Vehicle;
import com.mapper.VehicleMapper;
import com.mapper.EngineMapper;
import com.mapper.TransmissionMapper;
import com.mapper.OnBoardComputerMapper;
import com.mapper.WheelMapper;
import org.springframework.test.util.ReflectionTestUtils;
import com.service.VehicleService;
import com.repository.VehicleRepository;
import com.repository.EngineRepository;
import com.repository.TransmissionRepository;
import com.repository.OnBoardComputerRepository;
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
class VehicleServiceTest {
    @Mock VehicleRepository repository;
    @Mock EngineRepository engineRepository;
    @Mock TransmissionRepository transmissionRepository;
    @Mock OnBoardComputerRepository onBoardComputerRepository;
    @Mock WheelRepository wheelRepository;
    final VehicleMapper mapper = Mappers.getMapper(VehicleMapper.class);
    VehicleService service;
    VehicleRequest request;
    @BeforeEach void setup() {
        ReflectionTestUtils.setField(mapper, "engineMapper", Mappers.getMapper(EngineMapper.class));
        ReflectionTestUtils.setField(mapper, "transmissionMapper", Mappers.getMapper(TransmissionMapper.class));
        ReflectionTestUtils.setField(mapper, "onBoardComputerMapper", Mappers.getMapper(OnBoardComputerMapper.class));
        ReflectionTestUtils.setField(mapper, "wheelMapper", Mappers.getMapper(WheelMapper.class));
        service = new VehicleService(repository, engineRepository, transmissionRepository, onBoardComputerRepository, wheelRepository, mapper);
        request = new ObjectMapper().convertValue(ApiTestBase.payload("vehicles"), VehicleRequest.class);
    }
    @Test @DisplayName("M05-01") void create() {
        when(repository.save(any())).thenAnswer(call -> { Vehicle entity=call.getArgument(0); entity.setId(77L); return entity; });
        var response = service.create(request);
        assertEquals(77L,response.getId());
        assertEquals(request.getBrand(),response.getBrand());
        var expected = new ObjectMapper().valueToTree(request);
        var actual = new ObjectMapper().findAndRegisterModules().valueToTree(response);
        expected.fields().forEachRemaining(e -> { if (!e.getValue().isNull()) assertEquals(e.getValue(),actual.get(e.getKey()),e.getKey()); });
        verify(repository).save(any());
    }
    @Test @DisplayName("M05-02") void missing() {
        when(repository.findByIdWithDetails(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,()->service.findById(999L));
        verify(repository,never()).save(any());
    }
    @Test @DisplayName("M05-03") void update() {
        var entity=mapper.toEntity(request); entity.setId(77L);
        when(repository.findByIdWithDetails(77L)).thenReturn(Optional.of(entity));
        request.setBrand("Toyota");
        var response=service.update(77L,request);
        assertEquals(77L,response.getId());
        assertEquals("Toyota",response.getBrand());
        assertEquals("Toyota",mapper.toResponse(entity).getBrand());
        verify(repository,never()).save(any());
    }
    @Test @DisplayName("M05-04") void deleteMissing() {
        when(repository.existsById(999L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,()->service.delete(999L));
        verify(repository,never()).deleteById(any());
    }
}
