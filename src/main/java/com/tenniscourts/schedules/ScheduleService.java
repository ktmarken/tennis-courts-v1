package com.tenniscourts.schedules;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tenniscourts.tenniscourts.TennisCourt;
import com.tenniscourts.tenniscourts.TennisCourtRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ScheduleService {

	@Autowired
    private final ScheduleRepository scheduleRepository;

	@Autowired
    private final ScheduleMapper scheduleMapper;
	
	@Autowired
	private final TennisCourtRepository tennisCourtRepository;

    public ScheduleDTO addSchedule(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) throws Exception {
    	System.out.println("=========");
        System.out.println(tennisCourtId);
        Optional<TennisCourt> court = tennisCourtRepository.findById(tennisCourtId);
        if (court.isEmpty()) {
            throw new Exception("error");
        }
        System.out.println("&&&&&&&&&");
        Schedule schedule = Schedule.builder()
                .tennisCourt(court.get())
                .startDateTime(createScheduleRequestDTO.getStartDateTime())
                .endDateTime(createScheduleRequestDTO.getStartDateTime().plusHours(1L))
                .build();
    	return scheduleMapper.map(scheduleRepository.save(schedule));
    }

    public Map<String, List<String>> findSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
    	Map<String, List<String>> result = new HashMap<>();
        List<TennisCourt> all = tennisCourtRepository.findAll();
        for (TennisCourt court : all) {
            List<String> list = new ArrayList<>();
            for (LocalDateTime date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
                for (int hour = 14; hour <= 23; hour++) {
                    List<Schedule> byTennisCourt_idAAndStartDateTime = scheduleRepository.mutualAidFlag(court.getId(), date.withHour(hour));
                    if (byTennisCourt_idAAndStartDateTime.size() == 0) list.add(date.withHour(hour).toString());
                }
            }
            result.put(court.getName(), list);
        }
        return result;
    }

    public ScheduleDTO findSchedule(Long scheduleId) throws Exception {
    	return scheduleMapper.map(findScheduleDB(scheduleId));
    }
    
    public Schedule findScheduleDB(Long scheduleId) throws Exception {
        Optional<Schedule> byId = scheduleRepository.findById(scheduleId);
        if (byId.isEmpty()) {
            throw new Exception("error");
        }
        return byId.get();
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }
}
