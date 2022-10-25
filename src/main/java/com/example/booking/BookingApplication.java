package com.example.booking;

import io.muserver.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.booking.models.Booking;
import com.example.booking.models.BookingInput;
import com.example.booking.models.ShowBookingInput;
import com.example.booking.models.TableCount;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.json.JSONObject;
import io.muserver.rest.RestHandlerBuilder;


@SpringBootApplication
public class BookingApplication {
	
	public static void main(String[] args) {

		Map<Integer, Booking> bookings = new HashMap<>();
        bookings.put(1, new Booking("Simran", "test@test.com", "123456", "2022-10-30", 4, "2-4"));
        bookings.put(2, new Booking("John", "test@test.com", "123456", "2022-10-25", 6, "2-4"));
        bookings.put(3, new Booking("Jack", "test@test.com", "123456", "2022-10-30", 8, "2-4"));
		
		List<TableCount> tbCount = new ArrayList<>();
		tbCount.add(new TableCount("2022-10-30", "2-4", 4, 4));
		tbCount.add(new TableCount("2022-10-30", "12-2", 2, 1));
		tbCount.add(new TableCount("2022-10-30", "2-4", 8, 2));
		tbCount.add(new TableCount("2022-10-30", "4-6", 4, 5));

		BookingResource bookingResource = new BookingResource(bookings, tbCount);

		MuServer server = MuServerBuilder.httpServer()
			.withHttpPort(13000)
            .addHandler(
				RestHandlerBuilder.restHandler(bookingResource)
				.addCustomReader(new JacksonJaxbJsonProvider())
			)
            .start();

        System.out.println("API example: " + server.uri().resolve("/api"));
    }

	@Path("/api")
    public static class BookingResource {

        private Map<Integer, Booking> _bookings;
		private List<TableCount> _tbCount;

        public BookingResource(Map<Integer, Booking> bookings, List<TableCount> tableCount) {
            this._bookings = bookings;
			this._tbCount = tableCount;
        }

        @GET
        @Path("/showBookings")
		@Produces("application/json")
        public String get(ShowBookingInput input) {
			String date = input.getdate();
			JSONObject result = new JSONObject();
			for(Map.Entry<Integer, Booking> element: this._bookings.entrySet()) {
				if(element.getValue().getdate().equals(date)) {
					JSONObject filteredBooking = new JSONObject(); 
					filteredBooking.put("customerName", element.getValue().getCustomerName());
					filteredBooking.put("email", element.getValue().getEmail());
					filteredBooking.put("phone", element.getValue().getPhone());
					filteredBooking.put("date", element.getValue().getdate());
					filteredBooking.put("tableSize", element.getValue().getTableSize());
					filteredBooking.put("slot", element.getValue().getSlot());
					result.put(element.getKey().toString(), filteredBooking);
				}
			}
			if(result.isEmpty()) {
				result.put("code", "200");
				result.put("message", "No bookings found");
			}
			return result.toString();
        }

		@POST
        @Path("/addBooking")
        @Produces("application/json")
        public String book(BookingInput input) {

			int count = 0;
			String customerName = input.getCustomerName();
			String email = input.getEmail();
			String phone = input.getPhone();
			String date = input.getdate();
			int tableSize = input.getTableSize();
			String slot = input.getSlot();
			JSONObject result = new JSONObject();

			for(TableCount tb: this._tbCount) {
				if(tb.getdate().equals(date) && tb.getSlot().equals(slot) && tb.getTableSize() == tableSize) {
					count = tb.getCount();
					if(count > 0) {
						tb.setCount(count - 1);
						this._bookings.put(this._bookings.size() + 1, new Booking(customerName, email, phone, date, tableSize, slot));
						result.put("code", "200");
						result.put("message", "Successfully booked the table");
						break;
					}
				} else {
					result.put("code", "200");
					result.put("message", "No table available or booking not allowed");
				}
			}
			return result.toString();
        }
    }
}
