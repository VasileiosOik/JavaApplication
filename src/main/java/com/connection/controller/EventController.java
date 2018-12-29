package com.connection.controller;

import com.connection.dao.CompanyEventDao;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/event")
@SwaggerDefinition(tags = {@Tag(name = "company", description = "Operations pertaining to see events adding units")})
public class EventController {

    private final CompanyEventDao companyEventDao;

    @Autowired
    public EventController(CompanyEventDao companyEventDao) {
        this.companyEventDao = companyEventDao;
    }

    @ApiOperation(value = "View the events", response = ResponseEntity.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 404, message = "No content to display")})
    @GetMapping(value = "/getEvents", produces = "application/json")
    public ResponseEntity<Object> getAllEventsBetweenDates(@RequestParam(name = "fromDate") String fromDate, @RequestParam(name = "toDate") String toDate) {
        LocalDate fromDateParsed = parseDate(fromDate);
        LocalDate toDateParsed = parseDate(toDate);
        return companyEventDao.getEventsBetweenDates(fromDateParsed, toDateParsed);
    }

    private LocalDate parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return date != null ? LocalDate.parse(date, formatter) : null;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }
}
