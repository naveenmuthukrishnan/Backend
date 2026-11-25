
package com.railway.trainservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.List;

import com.railway.trainservice.dto.TrainSearchResponse;
import com.railway.trainservice.entity.Station;
import com.railway.trainservice.entity.Train;
import com.railway.trainservice.entity.TrainStop;
import com.railway.trainservice.exception.ResourceNotFoundException;
import com.railway.trainservice.repository.TrainStopRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainSearchServiceTest {

    @Mock
    private TrainStopRepository trainStopRepository;

    @InjectMocks
    private TrainSearchService trainSearchService;

    private Train train;
    private TrainStop sourceStop;
    private TrainStop destinationStop;
    private Station sourceStation;
    private Station destinationStation;

    @BeforeEach
    void setUp() {

        train = mock(Train.class);
        sourceStop = mock(TrainStop.class);
        destinationStop = mock(TrainStop.class);
        sourceStation = mock(Station.class);
        destinationStation = mock(Station.class);

        when(train.getId()).thenReturn(1L);
        when(train.getTrainNumber()).thenReturn("12632");
        when(train.getTrainName()).thenReturn("Express Train");
        when(train.getTrainType()).thenReturn("EXPRESS");

        when(sourceStation.getId()).thenReturn(10L);
        when(sourceStation.getStationName()).thenReturn("Chennai");

        when(destinationStation.getId()).thenReturn(20L);
        when(destinationStation.getStationName()).thenReturn("Madurai");

        when(sourceStop.getStation()).thenReturn(sourceStation);
        when(destinationStop.getStation()).thenReturn(destinationStation);
    }

    @Test
    void searchTrains_ShouldReturnMatchingTrain() {

        when(trainStopRepository.findTrainsBetweenStations(10L, 20L))
                .thenReturn(List.of(train));

        when(trainStopRepository.findByTrain_IdOrderByStopNumberAsc(1L))
                .thenReturn(List.of(sourceStop, destinationStop));

        List<TrainSearchResponse> result =
                trainSearchService.searchTrains(10L, 20L);

        assertEquals(1, result.size());

        verify(trainStopRepository)
                .findTrainsBetweenStations(10L, 20L);

        verify(trainStopRepository)
                .findByTrain_IdOrderByStopNumberAsc(1L);
    }

    @Test
    void searchTrains_ShouldReturnEmptyList_WhenNoTrainsFound() {

        when(trainStopRepository.findTrainsBetweenStations(10L, 20L))
                .thenReturn(List.of());

        List<TrainSearchResponse> result =
                trainSearchService.searchTrains(10L, 20L);

        assertTrue(result.isEmpty());

        verify(trainStopRepository)
                .findTrainsBetweenStations(10L, 20L);

        verify(trainStopRepository, never())
                .findByTrain_IdOrderByStopNumberAsc(anyLong());
    }

    @Test
    void searchTrains_ShouldThrowException_WhenSourceStationIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> trainSearchService.searchTrains(null, 20L)
        );

        verifyNoInteractions(trainStopRepository);
    }

    @Test
    void searchTrains_ShouldThrowException_WhenDestinationStationIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> trainSearchService.searchTrains(10L, null)
        );

        verifyNoInteractions(trainStopRepository);
    }

    @Test
    void searchTrains_ShouldThrowException_WhenBothStationsAreNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> trainSearchService.searchTrains(null, null)
        );

        verifyNoInteractions(trainStopRepository);
    }

    @Test
    void searchTrains_ShouldThrowException_WhenSourceAndDestinationAreSame() {

        assertThrows(
                IllegalArgumentException.class,
                () -> trainSearchService.searchTrains(10L, 10L)
        );

        verifyNoInteractions(trainStopRepository);
    }

    @Test
    void searchTrains_ShouldThrowException_WhenSourceNotFoundInRoute() {

        when(trainStopRepository.findTrainsBetweenStations(10L, 20L))
                .thenReturn(List.of(train));

        when(trainStopRepository.findByTrain_IdOrderByStopNumberAsc(1L))
                .thenReturn(List.of(destinationStop));

        assertThrows(
                ResourceNotFoundException.class,
                () -> trainSearchService.searchTrains(10L, 20L)
        );
    }

    @Test
    void searchTrains_ShouldThrowException_WhenDestinationNotFoundInRoute() {

        when(trainStopRepository.findTrainsBetweenStations(10L, 20L))
                .thenReturn(List.of(train));

        when(trainStopRepository.findByTrain_IdOrderByStopNumberAsc(1L))
                .thenReturn(List.of(sourceStop));

        assertThrows(
                ResourceNotFoundException.class,
                () -> trainSearchService.searchTrains(10L, 20L)
        );
    }
}
