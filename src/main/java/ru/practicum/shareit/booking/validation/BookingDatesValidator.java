package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

public class BookingDatesValidator implements ConstraintValidator<ValidBookingDates, BookingDto> {

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        if (bookingDto == null) {
            return true;
        }

        LocalDateTime startDate = bookingDto.getStartDate();
        LocalDateTime endDate = bookingDto.getEndDate();

        if (startDate == null || endDate == null) {
            return true;
        }

        if (startDate.isAfter(endDate) || startDate.equals(endDate)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата начала должна быть раньше даты окончания")
                    .addConstraintViolation();
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (startDate.isBefore(now)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата начала не может быть в прошлом")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}