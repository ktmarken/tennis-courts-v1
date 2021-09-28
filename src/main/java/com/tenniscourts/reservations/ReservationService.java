package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestService;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleService;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationService {

	@Autowired
    private final ReservationRepository reservationRepository;

	@Autowired
    private final ReservationMapper reservationMapper;
	
	@Autowired
	private final GuestService guestService;
	
	@Autowired
	private final ScheduleService scheduleService;

    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) throws Exception {
    	Schedule schedule = scheduleService.findScheduleDB(createReservationRequestDTO.getScheduleId());
        Guest guest = guestService.findById(createReservationRequestDTO.getGuestId());
        Reservation reservation = reservationMapper.map(createReservationRequestDTO);
        reservation.setSchedule(schedule);
        reservation.setReservationStatus(ReservationStatus.READY_TO_PLAY);
        reservation.setGuest(guest);
        reservation.setValue(BigDecimal.TEN);
        reservation.setRefundValue(getRefundValue(reservation));
        return reservationMapper.map(reservationRepository.save(reservation));
    }

    public ReservationDTO findReservation(Long reservationId) throws Exception {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }
    
    public List<ReservationDTO> findAll(){
    	return reservationMapper.map(reservationRepository.findAll());
    }

    public ReservationDTO cancelReservation(Long reservationId) throws Exception {
        return reservationMapper.map(this.cancel(reservationId));
    }

    private Reservation cancel(Long reservationId) throws Exception {
        return reservationRepository.findById(reservationId).map(reservation -> {

            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

        }).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (hours < 0 ) {
        	return BigDecimal.ZERO;
        }
        if (hours >= 0 && hours < 2) {
        	return reservation.getValue().multiply(BigDecimal.valueOf(3/4));
        }
        if (hours >= 2 && hours < 12) {
        	return reservation.getValue().multiply(BigDecimal.valueOf(1/2));
        }
        if (hours >= 12 && hours < 24) {
        	return reservation.getValue().multiply(BigDecimal.valueOf(3/4));
        }
        if (hours >= 24) {
            return reservation.getValue();
        }

        return BigDecimal.ZERO;
    }

    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) throws Exception {
        Reservation previousReservation = cancel(previousReservationId);
        
        ScheduleDTO schedule = scheduleService.findSchedule(scheduleId);

        // compare start times instead of IDs
        if (schedule.getStartDateTime().equals(previousReservation.getSchedule().getStartDateTime())) {
            throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.save(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId())
                .scheduleId(scheduleId)
                .build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;
    }
    
    public List<ReservationDTO> findPastReservation() {
    	List<Long> pasts = reservationRepository.findPastReservations();
    	List<ReservationDTO> pastReservations = new ArrayList<>();
    	for (Long r: pasts) {
    		Optional<Reservation> byId = reservationRepository.findById(r);
    		Reservation reservation = byId.get();
    		pastReservations.add(reservationMapper.map(reservation));
    	}
    	return pastReservations;
    }
}
