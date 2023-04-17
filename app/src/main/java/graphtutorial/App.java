package graphtutorial;

import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.MessageCollectionPage;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class App {
        static final Properties oAuthProperties = new Properties();
    public static void main(String[] args) throws Exception {
        System.out.println("Java Graph Tutorial");
        System.out.println();


        try {
            oAuthProperties.load(App.class.getResourceAsStream("oAuth.properties"));
        } catch (IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted " +
                    "oAuth.properties file. See README for details.");
            return;
        }

        initializeGraph();

        greetUser();

        Scanner input = new Scanner(System.in);

        int choice = -1;

        while (choice != 0) {
            System.out.println("\nPlease choose one of the following options:");
            System.out.println("0. Exit");
            System.out.println("1. Display access token");
            System.out.println("2. List my inbox");
            System.out.println("3. Send mail");
            System.out.println("4. Check calendar's events");
            System.out.println("5. Create and send event");

            try {
                choice = input.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println(ex.getMessage());
            }

            input.nextLine();

            switch(choice) {
                case 0:
                    System.out.println("Goodbye...");
                    break;
                case 1:
                    displayAccessToken();
                    break;
                case 2:
                    listInbox();
                    break;
                case 3:
                    sendMail();
                    break;
                case 4:
                    checkEvents();
                    break;
                case 5:
                    createAndSendEvent();
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }

        input.close();
    }

    private static void initializeGraph() {
        try {
            Graph.initializeGraphForUserAuth(App.oAuthProperties,
                    challenge -> System.out.println(challenge.getMessage()));
        } catch (Exception e)
        {
            System.out.println("Error initializing Graph for user auth");
            System.out.println(e.getMessage());
        }
    }

    private static void greetUser() {
        try {
            final User user = Graph.getUser();
            // For Work/school accounts, email is in mail property
            // Personal accounts, email is in userPrincipalName
            final String email = user.mail == null ? user.userPrincipalName : user.mail;
            System.out.println("Hello, " + user.displayName + "!");
            System.out.println("Email: " + email);
        } catch (Exception e) {
            System.out.println("Error getting user");
            System.out.println(e.getMessage());
        }
    }

    private static void displayAccessToken() {
        try {
            final String accessToken = Graph.getUserToken();
            System.out.println("Access token: " + accessToken);
        } catch (Exception e) {
            System.out.println("Error getting access token");
            System.out.println(e.getMessage());
        }
    }

    private static void listInbox() {
        try {
            final MessageCollectionPage messages = Graph.getInbox();

            // Output each message's details
            for (Message message: messages.getCurrentPage()) {
                System.out.println("Message: " + message.subject);
                assert message.from != null;
                assert message.from.emailAddress != null;
                System.out.println("  From: " + message.from.emailAddress.name);
                System.out.println("  Status: " + (Boolean.TRUE.equals(message.isRead) ? "Read" : "Unread"));
                assert message.receivedDateTime != null;
                System.out.println("  Received: " + message.receivedDateTime
                        // Values are returned in UTC, convert to local time zone
                        .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
            }

            final boolean moreMessagesAvailable = messages.getNextPage() != null;
            System.out.println("\nMore messages available? " + moreMessagesAvailable);
        } catch (Exception e) {
            System.out.println("Error getting inbox");
            System.out.println(e.getMessage());
        }
    }

    private static void sendMail() {
        try {
            // Send mail to the signed-in user
            // Get the user for their email address

            // Get own user email:
            // final User user = Graph.getUser();
            // final String email = user.mail == null ? user.userPrincipalName : user.mail;

            Scanner scan = new Scanner(System.in);
            System.out.print("E-mail: ");
            final String email = scan.nextLine();

            System.out.print("Subject: ");
            final String emailSubject = scan.nextLine();

            System.out.print("Body: ");
            final String emailBody = scan.nextLine();

            Graph.sendMail(emailSubject, emailBody, email);
            System.out.println("\nMail sent to "+ email);
        } catch (Exception e) {
            System.out.println("Error sending mail");
            System.out.println(e.getMessage());
        }
    }

    private static void createAndSendEvent() throws Exception {
        String subject = "Subject e-mail goes here";
        String body = "Does noon work for you?";
        String startDateTime = "2022-04-27T12:30:00";
        String endDateTime = "2022-04-29T14:00:00";
        String timeZone = "Pacific Standard Time";
        String locationDisplayName = "Location display name goes here";
        List<String> attendeeEmailAddresses = new ArrayList<>();
        attendeeEmailAddresses.add(oAuthProperties.getProperty("app.email"));

        try{
            Graph.createEvent(subject, body, startDateTime, endDateTime, timeZone, locationDisplayName, attendeeEmailAddresses);
            System.out.println("Event successfully created, email sent to: " + attendeeEmailAddresses);
        }catch (Exception e) {
            throw new Exception("Couldn't create new Event");
        }
    }

    private static void checkEvents() {
        try {
            Graph.checkEvents();
        } catch (Exception e) {
            System.out.println("Error making Graph call");
            System.out.println(e.getMessage());
        }
    }
}
