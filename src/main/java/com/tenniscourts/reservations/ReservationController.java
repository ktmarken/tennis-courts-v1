package com.tenniscourts.reservations;

import com.tenniscourts.config.BaseRestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/reservation")
public class ReservationController extends BaseRestController {

	@Autowired
    private final ReservationService reservationService;

	@PostMapping
	@ApiOperation("Creates a reservation")
	@ApiResponse(code = 201, message = "Successfully created reservation")
    public ResponseEntity<Void> bookReservation(@RequestBody CreateReservationRequestDTO createReservationRequestDTO) throws Exception {
        return ResponseEntity.created(locationByEntity(reservationService.bookReservation(createReservationRequestDTO).getId())).build();
    }

    @GetMapping("/{reservationId}")
    @ApiOperation("Fetches a reservation by ID")
    @ApiResponse(code = 200, message = "OK")
    public ResponseEntity<ReservationDTO> findReservation(@PathVariable Long reservationId) throws Exception {
        return ResponseEntity.ok(reservationService.findReservation(reservationId));
    }
    
    @GetMapping
    @ApiOperation("Fetches all reservations")
    @ApiResponse(code = 200, message = "OK")
    public ResponseEntity<List<ReservationDTO>> findAll() {
    	return ResponseEntity.ok(reservationService.findAll());
    }
    
    @GetMapping("/past")
    @ApiOperation("Fetches all past reservations")
	@ApiResponse(code = 200, message = "OK")
    public ResponseEntity<List<ReservationDTO>> findPastReservation() {
        return ResponseEntity.ok(reservationService.findPastReservation());
    }
    
    @DeleteMapping("/{reservationId}")
    @ApiOperation("Deletes a reservation by ID")
    @ApiResponse(code = 200, message = "Successfully deleted reservation")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long reservationId) throws Exception {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

    @PostMapping("/reschedule/{reservationId}/{scheduleId}")
    @ApiOperation("Reschedules a reservation to a new schedule based on IDs")
	@ApiResponse(code = 200, message = "OK")
    public ResponseEntity<ReservationDTO> rescheduleReservation(@PathVariable Long reservationId, @PathVariable Long scheduleId) throws Exception {
        return ResponseEntity.ok(reservationService.rescheduleReservation(reservationId, scheduleId));
    }
}
