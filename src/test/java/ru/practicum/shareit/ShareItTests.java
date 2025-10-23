package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

//test
@SpringBootTest
class ShareItTests {

	@Autowired
	private ApplicationContext context;

	@Test
	void contextLoads() {
		assertNotNull(context);
	}

	@Test
	void allServicesAreLoaded() {
		assertNotNull(context.getBean(UserService.class));
		assertNotNull(context.getBean(ItemService.class));
		assertNotNull(context.getBean(BookingService.class));
		assertNotNull(context.getBean(ItemRequestService.class));
	}

	@Test
	void allControllersAreLoaded() {
		assertNotNull(context.getBean(UserController.class));
		assertNotNull(context.getBean(ItemController.class));
		assertNotNull(context.getBean(BookingController.class));
		assertNotNull(context.getBean(ItemRequestController.class));
	}

}