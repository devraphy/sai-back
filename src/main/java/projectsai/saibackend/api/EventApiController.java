package projectsai.saibackend.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import projectsai.saibackend.dto.event.requestDto.AddEventRequest;
import projectsai.saibackend.dto.event.requestDto.DeleteEventRequest;
import projectsai.saibackend.dto.event.requestDto.SearchEventRequest;
import projectsai.saibackend.dto.event.requestDto.UpdateEventRequest;
import projectsai.saibackend.dto.event.responseDto.AddEventResponse;
import projectsai.saibackend.dto.event.responseDto.DeleteEventResponse;
import projectsai.saibackend.dto.event.responseDto.SearchEventResponse;
import projectsai.saibackend.dto.event.responseDto.UpdateEventResponse;
import projectsai.saibackend.service.EventService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.util.List;

@RestController @Slf4j
@RequiredArgsConstructor
public class EventApiController {

    @PersistenceContext EntityManager em;
    private final EventService eventService;

    @PostMapping("/event/add")
    public AddEventResponse addEvent(@RequestBody @Valid AddEventRequest request) {
    }

    @PostMapping("/event")
    public List<SearchEventResponse> searchEvents(@RequestBody @Valid SearchEventRequest request) {
    }

    @PutMapping("/event")
    public UpdateEventResponse updateEvent(@RequestBody @Valid UpdateEventRequest request) {
    }

    @DeleteMapping("/event")
    public DeleteEventResponse deleteEvent(@RequestBody @Valid DeleteEventRequest request) {
    }

}