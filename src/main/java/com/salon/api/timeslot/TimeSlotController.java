package com.salon.api.timeslot;

import com.salon.api.timeslot.dto.TimeSlotResponse;
import com.salon.domain.enums.SlotStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stores/{storeId}/time-slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @GetMapping
    public ResponseEntity<List<TimeSlotResponse>> getSlots(
            @PathVariable Long storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long staffId,
            @RequestParam(required = false) SlotStatus status) {
        return ResponseEntity.ok(timeSlotService.getSlots(storeId, date, staffId, status));
    }
}
