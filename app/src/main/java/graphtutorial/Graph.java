package graphtutorial;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.models.*;
import com.microsoft.graph.requests.ContactCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import okhttp3.Request;

import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class Graph {
    private static Properties _properties;
    private static DeviceCodeCredential _deviceCodeCredential;
    private static GraphServiceClient<Request> _userClient;



    public static void initializeGraphForUserAuth(Properties properties, Consumer<DeviceCodeInfo> challenge) throws Exception {
        // Ensure properties isn't null
        if (properties == null) {
            throw new Exception("Properties cannot be null");
        }

        _properties = properties;

        final String clientId = properties.getProperty("app.clientId");
        final String tenantId = properties.getProperty("app.tenantId");
        final List<String> graphUserScopes = Arrays
                .asList(properties.getProperty("app.graphUserScopes").split(","));

        _deviceCodeCredential = new DeviceCodeCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .challengeConsumer(challenge)
                .build();

        final TokenCredentialAuthProvider authProvider =
                new TokenCredentialAuthProvider(graphUserScopes, _deviceCodeCredential);

        _userClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();
    }

    public static String getUserToken() throws Exception {
        // Ensure credential isn't null
        if (_deviceCodeCredential == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        final String[] graphUserScopes = _properties.getProperty("app.graphUserScopes").split(",");

        final TokenRequestContext context = new TokenRequestContext();
        context.addScopes(graphUserScopes);

        final AccessToken token = _deviceCodeCredential.getToken(context).block();
        assert token != null;
        return token.getToken();
    }

    public static User getUser() throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        return _userClient.me()
                .buildRequest()
                .select("displayName,mail,userPrincipalName")
                .get();
    }

    public static MessageCollectionPage getInbox() throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        return _userClient.me()
                .mailFolders("inbox")
                .messages()
                .buildRequest()
                .select("from,isRead,receivedDateTime,subject")
                .top(25)
                .orderBy("receivedDateTime DESC")
                .get();
    }

    public static void sendMail(String subject, String body, String recipient) throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        // Create a new message
        final Message message = new Message();
        message.subject = subject;
        message.body = new ItemBody();
        message.body.content = body;
        message.body.contentType = BodyType.TEXT;

        final Recipient toRecipient = new Recipient();
        toRecipient.emailAddress = new EmailAddress();
        toRecipient.emailAddress.address = recipient;
        message.toRecipients = List.of(toRecipient);

        // Send the message
        _userClient.me()
                .sendMail(UserSendMailParameterSet.newBuilder()
                        .withMessage(message)
                        .build())
                .buildRequest()
                .post();
    }

    public static void createEvent(String subject, String body, String startDateTime, String endDateTime,
                                   String timeZone, String locationDisplayName, List<String> attendeeEmailAddresses) throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        // Create a new event
        final Event event = new Event();
        event.subject = subject;
        event.body = new ItemBody();
        event.body.content = body;
        event.body.contentType = BodyType.TEXT;

        // Set start and end times
        final DateTimeTimeZone start = new DateTimeTimeZone();
        start.dateTime = startDateTime;
        start.timeZone = timeZone;
        event.start = start;

        final DateTimeTimeZone end = new DateTimeTimeZone();
        end.dateTime = endDateTime;
        end.timeZone = timeZone;
        event.end = end;

        // Set location
        final Location location = new Location();
        location.displayName = locationDisplayName;
        event.location = location;

        // Set attendees
        final List<Attendee> attendees = new ArrayList<>();
        for (String attendeeEmailAddress : attendeeEmailAddresses) {
            final Attendee attendee = new Attendee();
            final EmailAddress attendeeEmailAddressObj = new EmailAddress();
            attendeeEmailAddressObj.address = attendeeEmailAddress;
            attendeeEmailAddressObj.name = attendeeEmailAddress;
            attendee.emailAddress = attendeeEmailAddressObj;
            attendee.type = AttendeeType.REQUIRED;
            attendees.add(attendee);
        }
        event.attendees = attendees;

        // Create the event
        _userClient.me().events()
                .buildRequest()
                .post(event);
    }

    public static void checkEvents() {
        List<Event> events = Objects.requireNonNull(
                        _userClient.me().calendar().events()
                                .buildRequest()
                                .select("subject,start,end")
                                .get())
                .getCurrentPage();

        System.out.println("Displaying list of calendar's events of "
                + Objects.requireNonNull(_userClient.me()
                .buildRequest()
                .select("mail")
                .get()).mail + "\n");

        for (Event event : events) {
            assert event.start != null;
            assert event.end != null;
            System.out.println(event.subject + " (" + event.start.dateTime + " - " + event.end.dateTime + ")");
        }
    }

//    public static List<ContactCollectionPage> getContacts() {
//        // Get an instance of TokenCredential using DefaultAzureCredential
//        TokenCredential tokenCredential = new DefaultAzureCredentialBuilder().build();
//
//        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(tokenCredential);
//        GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
//                .authenticationProvider(authProvider)
//                .buildClient();
//
//        ContactCollectionPage contacts = graphClient.me().contacts()
//                .buildRequest()
//                .get();
//
//        return Collections.singletonList(contacts);
//    }

    public static void getContacts() {
        List<Contact> contacts = Objects.requireNonNull(
                        _userClient.me().contacts()
                                .buildRequest()
                                .select("displayName,emailAddresses")
                                .get())
                .getCurrentPage();

        System.out.println("Displaying list of contacts of "
                + Objects.requireNonNull(_userClient.me()
                .buildRequest()
                .select("mail")
                .get()).mail + "\n");

        for (Contact contact : contacts) {
            assert contact.emailAddresses != null;
            System.out.println(contact.displayName + " (" + contact.emailAddresses.get(0).address + ")");
        }
    }

    public static void createContact(String givenName, String surName, String email) {
        Contact newContact = new Contact();
        newContact.givenName = givenName;
        newContact.surname = surName;
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address = email;
        newContact.emailAddresses = List.of(new EmailAddress[]{emailAddress});

        try {
            Contact contact = _userClient.me().contacts()
                    .buildRequest()
                    .post(newContact);

            System.out.println("Contact created successfully. Id: " + contact.id);
        } catch (GraphServiceException e) {
            System.out.println("Error creating contact: " + e.getMessage());
        }
    }

}
