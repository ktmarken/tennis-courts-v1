package com.tenniscourts.schedules;

import com.tenniscourts.config.BaseRestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/schedule")
public class ScheduleController extends BaseRestController {

	@Autowired
    private final ScheduleService scheduleService;

    @PostMapping
    @ApiOperation("Creates a schedule")
	@ApiResponse(code = 201, message = "Successfully created schedule")
    public ResponseEntity<Void> addScheduleTennisCourt(@RequestBody CreateScheduleRequestDTO createScheduleRequestDTO) throws Exception {
        return ResponseEntity.created(locationByEntity(scheduleService.addSchedule(createScheduleRequestDTO.getTennisCourtId(), createScheduleRequestDTO).getId())).build();
    }

    @GetMapping("/date/{startDate}/{endDate}")
    @ApiOperation("Fetches a schedule based on start date and end date")
	@ApiResponse(code = 200, message = "OK")
    public ResponseEntity<Map<String, List<String>>> findSchedulesByDates(@PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
                                                                  @PathVariable @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {
        return ResponseEntity.ok(scheduleService.findSchedulesByDates(LocalDateTime.of(startDate, LocalTime.of(0, 0)), LocalDateTime.of(endDate, LocalTime.of(23, 59))));
    }

    @GetMapping("/{scheduleId}")
    @ApiOperation("Fetches a schedule based on ID")
	@ApiResponse(code = 200, message = "OK")
    public ResponseEntity<ScheduleDTO> findByScheduleId(@PathVariable Long scheduleId) throws Exception {
        return ResponseEntity.ok(scheduleService.findSchedule(scheduleId));
    }
}
