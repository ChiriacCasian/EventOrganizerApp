## Agenda Week 4

---

Date:           05/03/2024\
Main focus:     Progress Updates, Frontend and Backend Development\
Chair:          Nico Hammer\
Note taker:     Franciszek Howard

# Opening **(1 min)**
Is everyone present? **(1 min)**

# Approval of the agenda **(1 min)**
Are there any discussion points not in the agenda that should be? **(1 min)**

# Points of action **(35 min)**
- Use a Sprint Board
  - To-do
  - Doing
  - Done
- Reflection on Last Week's Work **(10 min)**
  - did everyone get 100+ LOC, 3+ commits, 1+ MR, and 1+ MR review?
  - summarize what you did
  - summarize the problems you encountered
  - bring up anything significant you noticed?
  - any suggestions for what to do differently (e.g. for the division of work)?
- Division of Work / Sprint Planning **(15 min)**
  - Front-End
    - which scenes and controls still need to be created and who is doing that?
  - Back-End
    - what still needs to be done for the database and who is doing that?
      - API configuration file with YAML (like we did in WDT)
    - server routing and endpoints need to be designed
      - what else for the server?
    - client utils need to be designed
      - what else for the client?
    - using [Jackson](https://github.com/FasterXML/jackson)
  - Entity Design
    - should Expenses, Participants, and Debts be entities dependent on Event or can they exist in a vacuum?
      - there are no accounts
- Fulfilling the Basic Requirements **(6 min)**
  - connecting to a Splitty server (with multiple clients)
  - switching between English and Dutch
  - [admin Story, end of page 2](https://brightspace.tudelft.nl/d2l/le/content/595286/viewContent/3613575/View)
  - do we have the basis to fulfill the other basic requirements?
- Fulfilling the Additional Requirements **(2 min)**
  - should we focus on this after the midterm?
- Using Issues Milestones **(2 min)**
  - we should make use of issues and milestones more to track our progress

# Action points for next week **(3 min)**
- Start properly using Dependency Injection
  - in testing
  - in JavaFX
  - resources:
    - [YouTube](https://youtu.be/J1f5b4vcxCQ?si=59_gfyAmJGi2VQPy)
    - [Blog Post](https://www.jamesshore.com/v2/blog/2006/dependency-injection-demystified)
- look into Long Polling and WebSockets
- BuddyCheck assignment

# Any other business **(1 min)**
*Is there anything that was not discussed yet that should be discussed in this meeting?*

# Questions for the TA **(1 min)**
*What is the assignment this week?*

# Question round **(3 min)**
*If there are any questions, now is the time to ask them.*

# Closing
Thank you all for your participation. This concludes our official meeting for today. **(2 min)**
