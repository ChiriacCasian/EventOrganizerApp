# Splitty

This repository contains the application Splitty, 
developed by Group 18 for the OOPP project.

## Running the Project

To run the project, you need to have JDK 21 installed. 
You can download it from the [OpenJDK website](https://jdk.java.net/21/).

If you are running the project in IntelliJ IDEA, follow these steps:
- create a run configuration for server.Main that includes oopp-team-18\server as your working directory
- create a run configuration for client.Main that includes oopp-team-18\client as your working directory
- add the following VM options to your client configuration:
  - --module-path="[your path to javafx for JDK 21]" --add-modules=javafx.controls,javafx.fxml

If you are running the project from the command line, follow these steps:
- build the project using `./gradlew build`
- run the server using `./gradlew bootRun`
- run the client using `./gradlew run`

## Accessing Certain Features
- upon generating a password, the password will be printed to the server console.
- editing a participant can be done by clicking on the participant in the participants table,
or by typing the participant's name and email into the text fields and changing their IBAN and BIC
- expenses can be edited and removed by clicking on the expense in the list of expenses found in the event overview
- expenses tags can be edited by clicking on the tag in the expense overview
  - note that this can only be done once the expense has been saved with this tag
- editing an event title can be done by clicking on the pen icon next to the event title in the event overview
- payment instructions and related buttons can be found by clicking on the arrow next to a debt
- the API is modeled in the openapi.yaml file found in the server package
- the language of the application, the server to connect to, and the email to send reminders and invitations from can be configured in the [client config file](client/src/main/resources/config.properties)

## Long Polling
The propagation of data is primarily implemented using WebSockets,
but long polling is used in the Admin Overview Controller to update the list of events.
Relevant classes include:
  - [AdminOverviewCtrl](client/src/main/java/client/scenes/AdminOverviewCtrl.java)
  - [ServerUtils](client/src/main/java/client/utils/ServerUtils.java)
  - [EventController](server/src/main/java/server/api/EventController.java)

## Extensions and HCI features
The following extensions have been implemented:
- Live Language Switch
- Detailed Expenses
- Open Debts
- Statistics
- Email Notification

The following HCI features have been implemented in some if not most pages
but may not be realized everywhere:
- Keyboard Shortcuts/Navigation: 
  - ENTER to confirm/save
  - ESCAPE to go back to the event overview/start screen
  - TAB to navigate between fields
- Multi-Modal Visualization:
  - Statistics Extension: 
    - labeled pie chart
  - Live Language Switch:
    - flag indicators with language names
  - Participant Overview:
    - color-coded info based on actions (e.g green for success, red for error)
  - and more...
- Logical Navigation
- Supporting Undo Actions
  - the creation, updating, and deletion of a participant can be undone
- Error Messages
  - upon invalid entries (e.g. an invalid IBAN, negative expense amount, etc.)
  informational error alerts will be displayed to the user
  - server errors are displayed in alerts as well
- Informative Feedback
  - upon adding, deleting, or updating a participant, or undoing one of these actions, the user will be informed of the success of the action
  - upon sending an email, the user will be informed of the success of the action
- Confirmation for Key Actions
  - before deleting an event in the Admin Overview, admins must confirm their decision in a confirmation dialog alert
  - before deleting a participant, users must confirm their decision, although deletion can be undone
## Contributors
- Casian Chiriac
- Simeon Nedelkov
- Lachezar Topalov
- Franciszek Howard
- Pelayo Fernandez Luengo
- Nico Hammer
