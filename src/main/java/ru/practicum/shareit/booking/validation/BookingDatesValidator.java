package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.BookingDto;

import java.time.LocalDateTime;

public class BookingDatesValidator implements ConstraintValidator<ValidBookingDates, BookingDto> {
    
    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        if (bookingDto == null) {
            return true;
        }
        
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        
        if (start == null || end == null) {
            return true;
        }
        
        if (start.isAfter(end) || start.equals(end)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата начала должна быть раньше даты окончания")
                    .addConstraintViolation();
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (start.isBefore(now)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата начала не может быть в прошлом")
                    .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}

