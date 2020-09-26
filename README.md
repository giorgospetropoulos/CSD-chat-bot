# CSD chat-bot
 Course Data Delivery System implemented with Voice Controls

# Description
CSD chat-bot is an application implemented for the Computer Science Department, University of Crete.
CSD chat-bot is an application that implements a system, which provides the users with information about the department's courses. The application supports partial voice controls such as navigating through the application's UI and querying the system's search engine
    
# Components
## DatabaseHelper Class
The DatabaseHelper class is a class for handling the system's database. It contains three lists: one User list, one Course list and one PostgraduateCourse list.

    - ArrayList<User> userList: The database's users list
    - ArrayList<Course> courseList: The database's undergraduate courses list
    - ArrayList<PostGraduateCourse> postGraduateCourseList: The database's postgraduate courses list
    
    * boolean userExists(String name): Check if the user exists in the database
    * void addToUserList(User user): Add user to the database's user list
    * User getUserByUID(String UID): Get user from the database's users list, given his/her UID
    * int getUserPosition (User user): Get the user's position in the database's users list
    * Course getCourseByName(String name): Get the undergraduate course from the database, given its name
    * PostGraduateCourse getPostGraduateCourseByName(String name): Get the postgraduate course from the database, given its name
    * boolean hasCourse(Course course): Check if the database has undergraduate course
    * boolean hasCourse(PostGraduateCourse course): Check if the database has postgraduate course
    * int getCoursePosition (Course course): Get the course's position in the database's courses list
    * String getCourseNameByCode(String code): Get the course's name, given its code
    * ArrayList<Course> searchInCourses(String query): Search for query in undergraduate and postgraduate courses lists
    * ArrayList<User> searchInUsers(String query): Search for query in Users list
    * Getters and Setters
    
## User Class
The System separates the Users in three categories:
    <br>(1) User
    <br>(2) Teacher
    <br>(3) Admin
    
### User
The User has the ability to:
<br>(i)   Enroll and disenroll from any lesson in the database
<br>(ii)  Create, edit and remove new reminders
<br>(iii) Add and remove course's reminders to/from his reminder's list
<br>(iv)  Add and remove Users to/from his contacts list
<br>(v)   Update his profile's details (username & profile picture)

### Teacher
The Teacher has all of the User's abilities plus the ability to:
<br>(i) Create, edit and remove reminders for the courses he has been assigned to as a teacher

### Admin
The Admin has all of the above mentioned abilities plus the ability to:
<br>(i)   Update the undergraduate and postgraduate courses lists in the database
<br>(ii)  Create, edit and remove courses from the database
<br>(iii) Give/Take the role of Teacher to other users
<br>(iv)  Give/Take the role of Admin to other users

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
    * Getters and Setters
   
    
## Course Class
The undergraduate course of the department.
    
    - String code_en: The course's code in english
    - String code_gr: The course's code in greek
    - String name_en: The course's name in english
    - String name_gr: The course's name in greek
    - String description_en:  The course's description in english
    - String description_gr: The course's description in greek
    - String area_code_en: The course's area code in english
    - String area_code_gr: The course's area code in greek
    - String area_name_en: The course's area name in english
    - String area_name_gr: The course's area name in greek
    - String teacher: The course's teacher name
    - String teacherUID: The teacher's UID
    - String url: The course's url
    - String ECTS: The course's ECTS
    - ArrayList<Reminder> courseReminders: The course's reminders
    - ArrayList<String> userList: The users enrolled in the course
    
    * int getCourseReminderPosition(String name): Get the reminder's position in the list given its name
    * void removeUserFromCourseUserList(String UID): Remove user from enrolled users list
    * Getters and Setters
    
## PostgraduateCourse Class
The postgraduate course of the department. The PostgraduateCourse class inherits the Course class. 

    - ArrayList<String> area_codes_gr: The course's greek area codes list
    - ArrayList<String> area_codes_en: The course's english area codes list
    - ArrayList<String> area_names_gr: The course's greek area name list
    - ArrayList<String> area_names_en: The course's english area names list
    
    * Getters and Setters
    
## Reminder Class
The reminders of the application. A reminder can be set directly to a user or to a course.

    - String name: The reminder's name
    - String description: The reminder's description
    - int day: The reminder's day
    - int month: The reminder's month
    - int year: The reminder's year
    - int hour: The reminder's hour
    - int min: The reminder's minute
    - int id: The reminder's ID
    - priority reminder_priority: The reminder's priority
    - enum priority {
        low,
        mid,
        high
    }
    
    * Getters and Setters
    
## SpeechRecorder Class
The SpeechRecorder class is designed to implement the voice controls for the application. The class is using a SpeechRecognizer, that implements Audio to Text features in English and Greek. The String provided by the recording is iterated recursivelly in order to produce the desired command.

    - SpeechRecognizer speechRecognizer: The class used for Audio to Text functions
    - Intent speechRecognizerIntent: Intent to start recording when the user presses the button
    - String recording: The String that is going to be iterated recursively 
    - String first_rec: The String of the sentence that was recorded
    - ArrayList<String> recording_table: The list used to check whether the recording has any words left for iteration
    - boolean my: Used to separate cases like "Go to my courses" from "Go to the department's courses"
    - boolean add, new_, set, nea, neas, orismos, orise, dimiourgise, dimiourgia: Used to separate cases like "New reminder" from "Go to reminder"
    - boolean mathimata_, mathimata: Used to separate cases like "Πήγαινέ με στα μαθήματά μου" from "Πήγαινέ με στις υπενθυμίσεις μου"
    - boolean ypenthimiseis: Used to separate cases like "Πήγαινέ με στα μαθήματά μου" from "Πήγαινέ με στις υπενθυμίσεις μου"
    - boolean search: Used to notify the system that what follows need to be queried to the database
    - boolean go: Used to specify id the user want to be redirected somewhere
    
    * void initializeSpeechRecognizer(): Initialize the SpeechRecognizer and implement requested methods
    * void startRecording(): Start recording the user
    * void stopRecording(): Stop recording the user
    * void checkVoiceRecodrPerimission(): Check if user has given voice record permission to the app
    * void callAction(String recording): Recursive function to determine the action that needs to be taken
    * Getters and Setters
