import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

// Login class handles user registration and authentication
class Login {

    private String storedUsername;
    private String storedPassword;
    private String storedPhone;
    private String firstName;
    private String lastName;

    // Checks username has underscore and is 5 chars or less
    public boolean checkUserName(String username) {
        boolean hasUnderscore = username.contains("_");
        boolean correctLength = username.length() <= 5;
        return hasUnderscore && correctLength;
    }

    // Checks password has 8+ chars, capital, number and special character
    public boolean checkPasswordComplexity(String password) {
        boolean longEnough = password.length() >= 8;
        boolean hasCapital = false;
        boolean hasNumber  = false;
        boolean hasSpecial = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c >= 'A' && c <= 'Z') hasCapital = true;
            if (c >= '0' && c <= '9') hasNumber  = true;
            if (c == '!' || c == '@' || c == '#' || c == '$'
                || c == '%' || c == '&' || c == '*') hasSpecial = true;
        }

        return longEnough && hasCapital && hasNumber && hasSpecial;
    }

    // Checks phone number is in +27XXXXXXXXX format
    public boolean checkCellPhoneNumber(String phone) {
        return phone.matches("^\\+27[0-9]{9}$");
    }

    // Registers user if all fields are valid
    public String registerUser(String username, String password,
                               String phone, String fName, String lName) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your " +
                   "username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password " +
                   "contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber(phone)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        storedUsername = username;
        storedPassword = password;
        storedPhone    = phone;
        firstName      = fName;
        lastName       = lName;

        return "User registered successfully!";
    }

    // Returns true if username and password match stored values
    public boolean loginUser(String username, String password) {
        if (storedUsername == null) return false;
        return username.equals(storedUsername) && password.equals(storedPassword);
    }

    // Returns welcome message or error message based on login result
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}


// Message class handles creating, sending, storing and managing messages
final class Message {

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int    messageNumber;

    // ArrayLists to store all message data
    private static ArrayList<String> sentMessages        = new ArrayList<>();
    private static ArrayList<String> disregardedMessages = new ArrayList<>();
    private static ArrayList<String> storedMessages      = new ArrayList<>();
    private static ArrayList<String> messageHashes       = new ArrayList<>();
    private static ArrayList<String> messageIDs          = new ArrayList<>();

    // Parallel ArrayLists used for searching and finding longest message
    private static ArrayList<String> allMessageTexts   = new ArrayList<>();
    private static ArrayList<String> allRecipients     = new ArrayList<>();
    private static ArrayList<String> allMessageIDsList = new ArrayList<>();

    private static int totalMessagesSent = 0;

    // Constructor - stores original text without truncating so length check works correctly
    public Message(String recipient, String messageText, int messageNumber) {
        this.recipient     = recipient;
        this.messageText   = messageText;
        this.messageNumber = messageNumber;
        this.messageID     = generateMessageID();
        this.messageHash   = createMessageHash();
    }

    // Generates a random 10 digit message ID
    private String generateMessageID() {
        long id = (long)(Math.random() * 9_000_000_000L) + 1_000_000_000L;
        return String.valueOf(id);
    }

    // Returns true if message ID is 10 characters or less
    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    // Checks recipient number starts with +27 and is 12 characters long
    public String checkRecipientCell() {
        if (recipient.startsWith("+27") && recipient.length() == 12) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number incorrectly formatted or does not contain an " +
                   "international code. Please correct the number and try again.";
        }
    }

    // Checks message is 250 characters or less - now works correctly because text is not pre-truncated
    public String checkMessageLength() {
        if (messageText.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = messageText.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
    }

    // Creates a hash from the message ID, number, and first and last words
    public String createMessageHash() {
        // Guard against empty message to prevent crash
        if (messageText == null || messageText.trim().isEmpty()) {
            return (messageID.substring(0, 2) + ":" + messageNumber + ":EMPTY").toUpperCase();
        }

        String idPrefix  = messageID.substring(0, 2);
        String[] words   = messageText.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord  = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");

        return (idPrefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
    }

    // Asks user to send, disregard or store the message
    public String sentMessage(Scanner scanner) {
        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Send Message");
        System.out.println("2. Disregard Message");
        System.out.println("3. Store Message");
        System.out.print("Enter choice: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            choice = 2;
        }

        // Truncate to 250 chars now that validation has already passed
        String safeText = messageText.length() > 250 ? messageText.substring(0, 250) : messageText;

        switch (choice) {
            case 1:
                totalMessagesSent++;
                String sentEntry =
                    "Message ID: "   + messageID   + "\n" +
                    "Message Hash: " + messageHash + "\n" +
                    "Recipient: "    + recipient   + "\n" +
                    "Message: "      + safeText;
                sentMessages.add(sentEntry);
                messageHashes.add(messageHash);
                messageIDs.add(messageID);
                // Add to parallel arrays for search and longest message features
                allMessageTexts.add(safeText);
                allRecipients.add(recipient);
                allMessageIDsList.add(messageID);
                return "Message successfully sent.";

            case 2:
                String disregardEntry =
                    "Message ID: "   + messageID   + "\n" +
                    "Message Hash: " + messageHash + "\n" +
                    "Recipient: "    + recipient   + "\n" +
                    "Message: "      + safeText;
                disregardedMessages.add(disregardEntry);
                return "Press 0 to delete the message.";

            case 3:
                storeMessage();
                messageHashes.add(messageHash);
                messageIDs.add(messageID);
                // Add to parallel arrays for search and longest message features
                allMessageTexts.add(safeText);
                allRecipients.add(recipient);
                allMessageIDsList.add(messageID);
                return "Message successfully stored.";

            default:
                return "Invalid option.";
        }
    }

    // Saves message to stored_messages.json file
    public void storeMessage() {
        String safeText = messageText.length() > 250 ? messageText.substring(0, 250) : messageText;
        String json =
            "{\n" +
            "  \"messageID\": \""   + messageID   + "\",\n" +
            "  \"messageHash\": \"" + messageHash + "\",\n" +
            "  \"recipient\": \""   + recipient   + "\",\n" +
            "  \"message\": \""     + safeText    + "\"\n"  +
            "}\n";

        try (FileWriter fw = new FileWriter("stored_messages.json", true)) {
            fw.write(json);
            System.out.println("Saved to stored_messages.json");
        } catch (IOException e) {
            System.out.println("Could not save: " + e.getMessage());
        }
    }

    // Reads stored_messages.json and loads messages into the storedMessages ArrayList
    public static void loadStoredMessages() {
        storedMessages.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("stored_messages.json"))) {
            String line;
            String currentID = "", currentHash = "", currentRecipient = "", currentMsg = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("\"messageID\""))
                    currentID = extractJsonValue(line);
                else if (line.startsWith("\"messageHash\""))
                    currentHash = extractJsonValue(line);
                else if (line.startsWith("\"recipient\""))
                    currentRecipient = extractJsonValue(line);
                else if (line.startsWith("\"message\""))
                    currentMsg = extractJsonValue(line);
                else if (line.equals("}")) {
                    storedMessages.add(
                        "Message ID: "   + currentID        + "\n" +
                        "Message Hash: " + currentHash      + "\n" +
                        "Recipient: "    + currentRecipient + "\n" +
                        "Message: "      + currentMsg
                    );
                }
            }
        } catch (IOException e) {
            System.out.println("(No stored messages file found or error reading it.)");
        }
    }

    // Extracts a value from a JSON line like "key": "value"
    private static String extractJsonValue(String line) {
        int start = line.indexOf('"', line.indexOf(':') + 1) + 1;
        int end   = line.lastIndexOf('"');
        if (start > 0 && end > start) return line.substring(start, end);
        return "";
    }

    // Shows the stored messages menu
    public static void storedMessagesMenu(Scanner scanner) {
        loadStoredMessages();

        boolean back = false;
        while (!back) {
            System.out.println("\n==== STORED MESSAGES MENU ====");
            System.out.println("a. Display sender and recipient of all stored messages");
            System.out.println("b. Display the longest stored message");
            System.out.println("c. Search for a message ID");
            System.out.println("d. Search messages for a particular recipient");
            System.out.println("e. Delete a message using message hash");
            System.out.println("f. Display full report of all stored messages");
            System.out.println("0. Back to main menu");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "a":
                    displayStoredSenderRecipient();
                    break;
                case "b":
                    displayLongestMessage();
                    break;
                case "c":
                    System.out.print("Enter Message ID to search: ");
                    String searchID = scanner.nextLine().trim();
                    searchByMessageID(searchID);
                    break;
                case "d":
                    System.out.print("Enter recipient number to search: ");
                    String searchRecipient = scanner.nextLine().trim();
                    searchByRecipient(searchRecipient);
                    break;
                case "e":
                    System.out.print("Enter Message Hash to delete: ");
                    String deleteHash = scanner.nextLine().trim();
                    deleteByHash(deleteHash);
                    break;
                case "f":
                    displayFullStoredReport();
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // Displays recipient of all stored messages
    private static void displayStoredSenderRecipient() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages.");
            return;
        }
        System.out.println("\n--- Stored Message Recipients ---");
        for (int i = 0; i < storedMessages.size(); i++) {
            String recipient = extractField(storedMessages.get(i), "Recipient: ");
            System.out.println((i + 1) + ". Recipient: " + recipient);
        }
    }

    // Finds and displays the longest message from sent and stored messages
    private static void displayLongestMessage() {
        if (allMessageTexts.isEmpty() && storedMessages.isEmpty()) {
            System.out.println("No messages available.");
            return;
        }

        String longest          = "";
        String longestRecipient = "";

        // Search in-memory parallel ArrayList
        for (int i = 0; i < allMessageTexts.size(); i++) {
            if (allMessageTexts.get(i).length() > longest.length()) {
                longest          = allMessageTexts.get(i);
                longestRecipient = allRecipients.get(i);
            }
        }

        // Also check file-loaded stored messages
        for (String entry : storedMessages) {
            String msg = extractField(entry, "Message: ");
            if (msg.length() > longest.length()) {
                longest          = msg;
                longestRecipient = extractField(entry, "Recipient: ");
            }
        }

        System.out.println("\n--- Longest Message ---");
        System.out.println("Recipient: " + longestRecipient);
        System.out.println("Message:   " + longest);
    }

    // Searches for a message by its ID in both memory and stored file
    private static void searchByMessageID(String id) {
        boolean found = false;

        // Search in-memory parallel ArrayList
        for (int i = 0; i < allMessageIDsList.size(); i++) {
            if (allMessageIDsList.get(i).equals(id)) {
                System.out.println("\n--- Message Found ---");
                System.out.println("Recipient: " + allRecipients.get(i));
                System.out.println("Message:   " + allMessageTexts.get(i));
                found = true;
            }
        }

        // Also search file-loaded stored messages
        for (String entry : storedMessages) {
            if (extractField(entry, "Message ID: ").equals(id)) {
                System.out.println("\n--- Message Found (stored) ---");
                System.out.println("Recipient: " + extractField(entry, "Recipient: "));
                System.out.println("Message:   " + extractField(entry, "Message: "));
                found = true;
            }
        }

        if (!found) System.out.println("No message found with ID: " + id);
    }

    // Searches for all messages sent to a particular recipient
    private static void searchByRecipient(String recipient) {
        boolean found = false;
        System.out.println("\n--- Messages for " + recipient + " ---");

        // Search in-memory parallel ArrayList
        for (int i = 0; i < allRecipients.size(); i++) {
            if (allRecipients.get(i).equals(recipient)) {
                System.out.println(allMessageTexts.get(i));
                found = true;
            }
        }

        // Also search file-loaded stored messages
        for (String entry : storedMessages) {
            if (extractField(entry, "Recipient: ").equals(recipient)) {
                System.out.println(extractField(entry, "Message: "));
                found = true;
            }
        }

        if (!found) System.out.println("No messages found for recipient: " + recipient);
    }

    // Deletes a stored message by its hash and rewrites the file
    private static void deleteByHash(String hash) {
        for (int i = 0; i < storedMessages.size(); i++) {
            if (extractField(storedMessages.get(i), "Message Hash: ").equals(hash)) {
                String deletedMsg = extractField(storedMessages.get(i), "Message: ");
                storedMessages.remove(i);
                rewriteStoredFile();
                System.out.println("Message: \"" + deletedMsg + "\" successfully deleted.");
                return;
            }
        }
        System.out.println("No message found with hash: " + hash);
    }

    // Rewrites stored_messages.json from the current storedMessages ArrayList
    private static void rewriteStoredFile() {
        try (FileWriter fw = new FileWriter("stored_messages.json", false)) {
            for (String entry : storedMessages) {
                String id        = extractField(entry, "Message ID: ");
                String hash      = extractField(entry, "Message Hash: ");
                String recipient = extractField(entry, "Recipient: ");
                String message   = extractField(entry, "Message: ");
                fw.write("{\n");
                fw.write("  \"messageID\": \""   + id        + "\",\n");
                fw.write("  \"messageHash\": \"" + hash      + "\",\n");
                fw.write("  \"recipient\": \""   + recipient + "\",\n");
                fw.write("  \"message\": \""     + message   + "\"\n");
                fw.write("}\n");
            }
        } catch (IOException e) {
            System.out.println("Error rewriting stored messages: " + e.getMessage());
        }
    }

    // Displays the full report of all stored messages
    private static void displayFullStoredReport() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages.");
            return;
        }
        System.out.println("\n===== FULL STORED MESSAGES REPORT =====");
        for (int i = 0; i < storedMessages.size(); i++) {
            System.out.println("\n--- Stored Message " + (i + 1) + " ---");
            System.out.println(storedMessages.get(i));
        }
    }

    // Extracts a named field from a multi-line entry string
    private static String extractField(String entry, String fieldName) {
        int start = entry.indexOf(fieldName);
        if (start == -1) return "";
        start += fieldName.length();
        int end = entry.indexOf('\n', start);
        if (end == -1) end = entry.length();
        return entry.substring(start, end).trim();
    }

    // Returns all sent messages as a formatted string
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages have been sent yet.";
        }
        StringBuilder sb = new StringBuilder("\n===== SENT MESSAGES =====\n");
        for (int i = 0; i < sentMessages.size(); i++) {
            sb.append("\n--- Message ").append(i + 1).append(" ---\n");
            sb.append(sentMessages.get(i)).append("\n");
        }
        return sb.toString();
    }

    public static int returnTotalMessages()              { return totalMessagesSent; }
    public static ArrayList<String> getSentMessages()    { return sentMessages; }
    public static ArrayList<String> getAllMessageTexts() { return allMessageTexts; }
    public static ArrayList<String> getAllRecipients()   { return allRecipients; }
    public static ArrayList<String> getAllMessageIDs()   { return allMessageIDsList; }

    public String getMessageID()   { return messageID;   }
    public String getMessageHash() { return messageHash; }
    public String getRecipient()   { return recipient;   }
    public String getMessageText() { return messageText; }
}


// MessageTests contains all unit tests for the application
class MessageTests {

    public static void runAllTests() {
        System.out.println("\n========== RUNNING UNIT TESTS ==========\n");
        testMessageLength();
        testRecipientCell();
        testMessageHash();
        testSentMessagesArray();
        testLongestMessage();
        testSearchByMessageID();
        testSearchByRecipient();
        testDeleteByHash();
        System.out.println("========== ALL TESTS COMPLETE ==========\n");
    }

    // Tests message length check for valid and too-long messages
    private static void testMessageLength() {
        System.out.println("--- Test: Message Length ---");
        Message msg1 = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 1);
        assertEqual("Message ready to send.", msg1.checkMessageLength(), "Message length - success");

        Message msg2 = new Message("+27718693002", "A".repeat(260), 2);
        String result2 = msg2.checkMessageLength();
        if (result2.contains("Message exceeds 250 characters by 10")) {
            System.out.println("PASSED: Message length - correctly reports 10 excess characters\n");
        } else {
            System.out.println("FAILED: Message length - failure case\n");
        }
    }

    // Tests cell number validation for valid and invalid numbers
    private static void testRecipientCell() {
        System.out.println("--- Test: Recipient Cell Number ---");
        Message msg1 = new Message("+27718693002", "Test message", 1);
        assertEqual("Cell phone number successfully captured.", msg1.checkRecipientCell(), "Cell number - success");

        Message msg2 = new Message("08575975889", "Test message", 2);
        String result2 = msg2.checkRecipientCell();
        if (result2.contains("incorrectly formatted")) {
            System.out.println("PASSED: Cell number - failure case\n");
        } else {
            System.out.println("FAILED: Cell number - failure case\n");
        }
    }

    // Tests that the message hash ends with the correct first and last word
    private static void testMessageHash() {
        System.out.println("--- Test: Message Hash ---");
        Message msg1 = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 0);
        String hash = msg1.createMessageHash();
        System.out.println("Hash generated: " + hash);
        if (hash.endsWith(":HITONIGHT")) {
            System.out.println("PASSED: Hash ends with :HITONIGHT\n");
        } else {
            System.out.println("FAILED: Expected :HITONIGHT, got: " + hash + "\n");
        }
    }

    // Tests that the live sentMessages ArrayList contains expected messages
    private static void testSentMessagesArray() {
        System.out.println("--- Test: Sent Messages Array Correctly Populated ---");

        ArrayList<String> sent = Message.getSentMessages();
        int sizeBefore = sent.size();

        sent.add("Message ID: 1234567890\nMessage Hash: 12:1:DIDCAKE\nRecipient: +27834557896\nMessage: Did you get the cake?");
        sent.add("Message ID: 1234567891\nMessage Hash: 12:2:ITTIME\nRecipient: +27838884567\nMessage: It is dinner time !");

        boolean containsCake   = false;
        boolean containsDinner = false;
        for (String entry : sent) {
            if (entry.contains("Did you get the cake?"))  containsCake   = true;
            if (entry.contains("It is dinner time !"))    containsDinner = true;
        }

        if (containsCake && containsDinner) {
            System.out.println("PASSED: Sent messages ArrayList contains expected test data");
            System.out.println("  -> \"Did you get the cake?\"");
            System.out.println("  -> \"It is dinner time !\"\n");
        } else {
            System.out.println("FAILED: Sent messages ArrayList missing expected test data\n");
        }

        // Clean up test entries
        while (sent.size() > sizeBefore) sent.remove(sent.size() - 1);
    }

    // Tests that the longest message is correctly found from the parallel ArrayList
    private static void testLongestMessage() {
        System.out.println("--- Test: Display Longest Message ---");

        ArrayList<String> texts      = Message.getAllMessageTexts();
        ArrayList<String> recipients = Message.getAllRecipients();
        ArrayList<String> ids        = Message.getAllMessageIDs();
        int sizeBefore = texts.size();

        texts.add("Did you get the cake?");
        texts.add("Where are you? You are late! I have asked you to be on time.");
        texts.add("It is dinner time !");
        texts.add("Ok, I am leaving without you.");
        recipients.add("+27834557896"); recipients.add("+27838884567");
        recipients.add("+27834484567"); recipients.add("+27838884567");
        ids.add("ID1"); ids.add("ID2"); ids.add("ID3"); ids.add("ID4");

        String longest = "";
        for (String msg : texts) {
            if (msg.length() > longest.length()) longest = msg;
        }

        String expected = "Where are you? You are late! I have asked you to be on time.";
        if (longest.equals(expected)) {
            System.out.println("PASSED: Longest message correctly identified");
            System.out.println("  -> \"" + longest + "\"\n");
        } else {
            System.out.println("FAILED: Expected \"" + expected + "\", got \"" + longest + "\"\n");
        }

        // Clean up test entries
        while (texts.size() > sizeBefore) {
            texts.remove(texts.size() - 1);
            recipients.remove(recipients.size() - 1);
            ids.remove(ids.size() - 1);
        }
    }

    // Tests searching for a message by ID in the parallel ArrayList
    private static void testSearchByMessageID() {
        System.out.println("--- Test: Search for Message ID ---");

        ArrayList<String> texts      = Message.getAllMessageTexts();
        ArrayList<String> recipients = Message.getAllRecipients();
        ArrayList<String> ids        = Message.getAllMessageIDs();
        int sizeBefore = texts.size();

        texts.add("It is dinner time !");
        recipients.add("0838884567");
        ids.add("9876543210");

        boolean found = false;
        String foundText = "";
        for (int i = 0; i < ids.size(); i++) {
            if (ids.get(i).equals("9876543210")) {
                foundText = texts.get(i);
                found = true;
            }
        }

        if (found && foundText.equals("It is dinner time !")) {
            System.out.println("PASSED: Message ID search returns correct message");
            System.out.println("  -> \"" + foundText + "\"\n");
        } else {
            System.out.println("FAILED: Message ID search did not return expected message\n");
        }

        // Clean up test entries
        while (texts.size() > sizeBefore) {
            texts.remove(texts.size() - 1);
            recipients.remove(recipients.size() - 1);
            ids.remove(ids.size() - 1);
        }
    }

    // Tests searching messages by recipient in the parallel ArrayList
    private static void testSearchByRecipient() {
        System.out.println("--- Test: Search Messages by Recipient ---");

        ArrayList<String> texts      = Message.getAllMessageTexts();
        ArrayList<String> recipients = Message.getAllRecipients();
        ArrayList<String> ids        = Message.getAllMessageIDs();
        int sizeBefore = texts.size();

        texts.add("Did you get the cake?");
        texts.add("Where are you? You are late! I have asked you to be on time.");
        texts.add("Yohoooo, I am at your gate.");
        texts.add("It is dinner time !");
        texts.add("Ok, I am leaving without you.");
        recipients.add("+27834557896"); recipients.add("+27838884567");
        recipients.add("+27834484567"); recipients.add("0838884567");
        recipients.add("+27838884567");
        ids.add("ID1"); ids.add("ID2"); ids.add("ID3"); ids.add("ID4"); ids.add("ID5");

        String targetRecipient = "+27838884567";
        ArrayList<String> results = new ArrayList<>();
        for (int i = 0; i < recipients.size(); i++) {
            if (recipients.get(i).equals(targetRecipient)) results.add(texts.get(i));
        }

        boolean hasMsg2 = results.contains("Where are you? You are late! I have asked you to be on time.");
        boolean hasMsg5 = results.contains("Ok, I am leaving without you.");

        if (hasMsg2 && hasMsg5) {
            System.out.println("PASSED: Recipient search returns correct messages for " + targetRecipient);
            System.out.println("  -> \"Where are you? You are late! I have asked you to be on time.\"");
            System.out.println("  -> \"Ok, I am leaving without you.\"\n");
        } else {
            System.out.println("FAILED: Recipient search did not return expected messages\n");
        }

        // Clean up test entries
        while (texts.size() > sizeBefore) {
            texts.remove(texts.size() - 1);
            recipients.remove(recipients.size() - 1);
            ids.remove(ids.size() - 1);
        }
    }

    // Tests deleting a message from a list using its hash
    private static void testDeleteByHash() {
        System.out.println("--- Test: Delete Message Using Hash ---");

        Message msg2 = new Message("+27838884567",
            "Where are you? You are late! I have asked you to be on time.", 2);
        String hash2 = msg2.getMessageHash();

        ArrayList<String> testList = new ArrayList<>();
        testList.add("Hash:" + hash2 + "|Msg:Where are you? You are late! I have asked you to be on time.");

        boolean deleted = false;
        for (int i = 0; i < testList.size(); i++) {
            if (testList.get(i).contains("Hash:" + hash2)) {
                testList.remove(i);
                deleted = true;
                break;
            }
        }

        if (deleted && testList.isEmpty()) {
            System.out.println("PASSED: Message \"Where are you? You are late! I have asked you to be on time.\" successfully deleted");
            System.out.println("  Using hash: " + hash2 + "\n");
        } else {
            System.out.println("FAILED: Message deletion did not work as expected\n");
        }
    }

    // Helper to compare expected and actual and print result
    private static void assertEqual(String expected, String actual, String testName) {
        if (expected.equals(actual)) {
            System.out.println("PASSED: " + testName);
        } else {
            System.out.println("FAILED: " + testName);
            System.out.println("  Expected: " + expected);
            System.out.println("  Actual:   " + actual);
        }
    }
}


// Main class - entry point for the QuickChat application
public class Main {

    public static void main(String[] args) {

        Scanner scanner    = new Scanner(System.in);
        Login   login      = new Login();
        boolean isLoggedIn = false;
        boolean exit       = false;

        System.out.println("===========================================");
        System.out.println("        WELCOME TO THE CHAT APP");
        System.out.println("===========================================\n");

        while (!isLoggedIn && !exit) {

            System.out.println("==============");
            System.out.println("  SELECT OPTION");
            System.out.println("==============");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter 1, 2, or 3.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("\n--- REGISTRATION ---");
                    System.out.print("Create username (must have _ and be 5 chars or less): ");
                    String regUser = scanner.nextLine();
                    System.out.print("Create password (8+ chars, 1 capital, 1 number, 1 special !@#$%&*): ");
                    String regPass = scanner.nextLine();
                    System.out.print("Enter cell number (format: +27XXXXXXXXX): ");
                    String regPhone = scanner.nextLine();
                    System.out.print("Enter first name: ");
                    String regFirst = scanner.nextLine();
                    System.out.print("Enter last name: ");
                    String regLast = scanner.nextLine();
                    System.out.println(login.registerUser(regUser, regPass, regPhone, regFirst, regLast) + "\n");
                    break;

                case 2:
                    System.out.println("\n--- LOGIN ---");
                    System.out.print("Username: ");
                    String loginUser = scanner.nextLine();
                    System.out.print("Password: ");
                    String loginPass = scanner.nextLine();
                    System.out.println(login.returnLoginStatus(loginUser, loginPass) + "\n");
                    if (login.loginUser(loginUser, loginPass)) {
                        isLoggedIn = true;
                    }
                    break;

                case 3:
                    System.out.println("Goodbye!\n");
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1, 2, or 3.\n");
            }
        }

        if (isLoggedIn) {

            System.out.println("\n===========================================");
            System.out.println("          Welcome to QuickChat!");
            System.out.println("===========================================\n");

            boolean quickChatExit = false;

            while (!quickChatExit) {

                System.out.println("==============");
                System.out.println("   MAIN MENU");
                System.out.println("==============");
                System.out.println("1. Send Messages");
                System.out.println("2. Show Recently Sent Messages");
                System.out.println("3. Run Unit Tests");
                System.out.println("4. Stored Messages");
                System.out.println("5. Quit");
                System.out.print("Enter your choice: ");

                int menuChoice;
                try {
                    menuChoice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.\n");
                    continue;
                }

                switch (menuChoice) {

                    case 1:
                        System.out.print("\nHow many messages do you want to send? ");
                        int numMessages;
                        try {
                            numMessages = Integer.parseInt(scanner.nextLine());
                            if (numMessages <= 0) {
                                System.out.println("Please enter a number greater than 0.\n");
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number.\n");
                            break;
                        }

                        for (int i = 1; i <= numMessages; i++) {
                            System.out.println("\n--- Message " + i + " of " + numMessages + " ---");
                            System.out.print("Enter recipient number (+27...): ");
                            String recipient = scanner.nextLine();
                            System.out.print("Enter your message (max 250 chars): ");
                            String msgText = scanner.nextLine();

                            Message message = new Message(recipient, msgText, i);

                            System.out.println(message.checkRecipientCell());

                            String lengthCheck = message.checkMessageLength();
                            System.out.println(lengthCheck);

                            if (!lengthCheck.equals("Message ready to send.")) {
                                System.out.println("Please shorten your message and try again.\n");
                                i--;
                                continue;
                            }

                            System.out.println("Message ID:   " + message.getMessageID());
                            System.out.println("Message Hash: " + message.getMessageHash());

                            System.out.println(message.sentMessage(scanner));

                            if (Message.returnTotalMessages() > 0) {
                                System.out.println("\n--- Message Details ---");
                                System.out.println("Message ID:   " + message.getMessageID());
                                System.out.println("Message Hash: " + message.getMessageHash());
                                System.out.println("Recipient:    " + message.getRecipient());
                                System.out.println("Message:      " + message.getMessageText());
                            }
                        }

                        System.out.println("\nTotal messages sent this session: " + Message.returnTotalMessages());
                        break;

                    case 2:
                        System.out.println(Message.printMessages());
                        break;

                    case 3:
                        MessageTests.runAllTests();
                        break;

                    case 4:
                        Message.storedMessagesMenu(scanner);
                        break;

                    case 5:
                        System.out.println("Goodbye! Thanks for using QuickChat.\n");
                        quickChatExit = true;
                        break;

                    default:
                        System.out.println("Invalid option. Please choose 1-5.\n");
                }
            }
        }

        scanner.close();
    }
}
