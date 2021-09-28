package com.tenniscourts.guests;


import com.tenniscourts.config.BaseRestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@Controller
@RequestMapping("/guest")
public class GuestController extends BaseRestController {

	@Autowired
    private final GuestService guestService;

    @PostMapping
    @ApiOperation("Creates a guest")
	@ApiResponse(code = 201, message = "Successfully created guest")
    public ResponseEntity<Guest> createGuest(@RequestBody CreateGuestRequestDTO createGuestRequestDTO) {
        return ResponseEntity.created(locationByEntity(guestService.createGuest(createGuestRequestDTO).getId())).build();
    }

    @PutMapping
    @ApiOperation("Updates a guest")
    @ApiResponse(code = 200, message = "OK")
    public Guest updateGuest(@RequestBody UpdateGuestDTO updateGuestDTO) throws Exception {
        return guestService.updateGuest(updateGuestDTO);
    }

    @GetMapping
    @ApiOperation("Fetches all guests")
    @ApiResponse(code = 200, message = "OK")
    public ResponseEntity<List<Guest>> findAllGuest() throws Exception {
        return ResponseEntity.ok(guestService.findAll());
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Deletes a guest by ID")
    @ApiResponse(code = 200, message = "OK")
    public void deleteGuest(@PathVariable("id") Long id) throws Exception {
        guestService.deleteGuest(id);
    }

    @GetMapping("/{id}")
    @ApiOperation("Fetches a guest by ID")
    @ApiResponse(code = 200, message = "OK")
    public ResponseEntity<Guest> findGuest(@PathVariable("id") Long id) throws Exception {
        return ResponseEntity.ok(guestService.findById(id));
    }

    @GetMapping("/name/{name}")
    @ApiOperation("Fetches a guest by name")
    @ApiResponse(code = 200, message = "OK")
    public ResponseEntity<Guest> findGuest(@PathVariable("name") String id) throws Exception {
        return ResponseEntity.ok(guestService.findByName(id));
    }
}