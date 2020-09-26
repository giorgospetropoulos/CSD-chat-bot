# CSD chat-bot
 Course Data Delivery System implemented with Voice Controls

# Description
CSD chat-bot is an application implemented for the Computer Science Department, University of Crete.
CSD chat-bot is an application that implements a system, which provides the users with information about the department's courses. The application supports partial voice controls such as navigating through the application's UI and querying the system's search engine
    
# Components
## User
The System separates the Users in three categories:
    (1) User
    (2) Teacher
    (3) Admin
    
### User    
The User has the ability to:
(i)   Enroll and disenroll from any lesson in the database
(ii)  Create, edit and remove new reminders
(iii) Add and remove course's reminders to/from his reminder's list
(iv)  Add and remove Users to/from his contacts list
(v)   Update his profile's details (username & profile picture)

    - String UID: The user's unique ID
    - String name: The user's username
    - String email: The user's email
    - ArrayList<Reminder> user_reminders: The user's reminders list
    - ArrayList<Course> user_courses: The user's undergraduate course list
    - ArrayList<PostGraduateCourse> user_postgraduate_courses: The user's postgraduate course list
    - ArrayList<User> user_friendlist: The user's contact list
    - boolean isAdmin: Boolean about the user's system role
    
    * void addToUserFriendList(User user):Add user to user's contact list
    * boolean isFriend(String UID): Check if user is in user's contacts list, given his/her UID
    * boolean isEnrolledTo(String course): Check if user is enrolled to course
    * void removeCourse(Course course): Remove course from user's course list
    * boolean hasReminderByID(int ID): Check if user has reminder in his reminders list, given the reminder's ID
    * Reminder getReminderByID(int ID): Get reminder from user's reminders list, given his ID
    * int getReminderPosition(String name): Get the reminder's position in the user's list
    * getReminderPosition(String name, String description, int day, int month, int year, int hour, int min): Get the reminder's position in the user's list
    * ArrayList<Reminder> searchInReminders(String query): Search in user's reminders for query
    
