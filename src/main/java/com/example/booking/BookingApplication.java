package com.example.booking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.MessageBodyWriter;
import static java.util.Arrays.asList;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.booking.models.Booking;
import com.example.booking.models.BookingInput;
import com.example.booking.models.ShowBookingInput;
import com.example.booking.models.TableCount;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.json.JSONObject;
import io.muserver.openapi.SchemaObject;
import io.muserver.openapi.SchemaObjectBuilder;
import io.muserver.rest.RestHandlerBuilder;
import io.muserver.rest.SchemaObjectCustomizer;
import io.muserver.rest.SchemaObjectCustomizerContext;
import io.muserver.*;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;


@SpringBootApplication
public class BookingApplication {

	/**
     * A JAX-RS message body writer that converts a Product object to JSON
     */
    @Produces("application/json")
    private static class BookingWriter implements MessageBodyWriter<Booking> {
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return Booking.class.isAssignableFrom(type);
        }

        public void writeTo(Booking booking, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            booking.toJSON().write(new OutputStreamWriter(entityStream));
        }
    }
	
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
				.addCustomWriter(new BookingWriter())
			)
			// .addHandler(Method.GET, "/showBookings", (request, response, path) -> {

			// 	JSONObject json = new JSONObject(request.readBodyAsString());
			// 	List<Booking> result = new ArrayList<>();
			// 	//JSONObject result = new JSONObject();
			// 	for(Map.Entry<Integer, Booking> element: bookings.entrySet()) {
			// 		if(element.getValue().getdate().equals(json.get("date"))) { 
			// 			result.add(element.getValue());
			// 			//result.put(element.getKey().toString(), element.getValue());
			// 		}
			// 	}
			// 	response.contentType("text/html;charset=utf-8");
			// 	response.write("Result: " + result);
            // })
            .start();

        System.out.println("API example: " + server.uri().resolve("/api"));
    }

	@Path("/api")
    public static class BookingResource implements SchemaObjectCustomizer{

        private Map<Integer, Booking> _bookings;
		private List<TableCount> _tbCount;

        public BookingResource(Map<Integer, Booking> bookings, List<TableCount> tableCount) {
            this._bookings = bookings;
			this._tbCount = tableCount;
        }

		@Override
        public SchemaObjectBuilder customize(SchemaObjectBuilder builder, SchemaObjectCustomizerContext context) {
            if (context.resource() == this && context.type().equals(Booking.class)) {
                Map<String, SchemaObject> props = new HashMap<>();
                props.put("customerName", SchemaObjectBuilder.schemaObjectFrom(String.class).build());
                props.put("email", SchemaObjectBuilder.schemaObjectFrom(String.class).build());
				props.put("phone", SchemaObjectBuilder.schemaObjectFrom(String.class).build());
                props.put("date", SchemaObjectBuilder.schemaObjectFrom(String.class).build());
                props.put("tableSize", SchemaObjectBuilder.schemaObjectFrom(Integer.class).build());
                props.put("slot", SchemaObjectBuilder.schemaObjectFrom(String.class).build());
                builder.withProperties(props)
                    .withRequired(asList("customerName", "email", "phone", "date", "tableSize", "slot"));
            }
            return builder;
        }

        @GET
        @Path("/showBookings")
		@Produces("application/json")
        public String get(ShowBookingInput input) {
			String date = input.getdate();
			JSONObject result = new JSONObject();
			for(Map.Entry<Integer, Booking> element: this._bookings.entrySet()) {
				if(element.getValue().getdate().equals(date)) { 
					result.put(element.getKey().toString(), element.getValue());
				}
			}
			return result.toString();
        }

		@POST
        @Path("/addBooking")
        @Produces("application/json")
        public String book(BookingInput input) {

			int count = 0;
			String result = "";
			String customerName = input.getCustomerName();
			String email = input.getEmail();
			String phone = input.getPhone();
			String date = input.getdate();
			int tableSize = input.getTableSize();
			String slot = input.getSlot();

			for(TableCount tb: this._tbCount) {
				if(tb.getdate().equals(date) && tb.getSlot().equals(slot) && tb.getTableSize() == tableSize) {
					count = tb.getCount();
					if(count > 0) {
						tb.setCount(count - 1);
						this._bookings.put(this._bookings.size() + 1, new Booking(customerName, email, phone, date, tableSize, slot));
						result = "Successfully booked the table";
						break;
					}
				} else {
					result = "No table available or booking not allowed";
				}
			}
			return result;
        }
    }
}
