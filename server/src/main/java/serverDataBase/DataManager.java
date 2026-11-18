package serverDataBase;
import logic.Configuration;
import logic.enums.*;
import network.ClientHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import serverDataBase.models.*;

import java.time.LocalDateTime;
import java.util.*;

public class DataManager {
   //commit changes both in database and loaded data

    static Logger log = LogManager.getLogger(DataManager.class);

  //  public static ArrayList<Answer> answers;
  //  public static ArrayList<Chat> chats;
  //  public static ArrayList<Course> courses;
  //  public static ArrayList<Department> departments;

  //  public static ArrayList<EducationalContext> educationalContexts;

  //  public static ArrayList<Exercise> exercises;
  //  public static ArrayList<Master> masters;

  //  public static ArrayList<Message> messages;

  //  public static ArrayList<Score> scores;
  //  public static ArrayList<Student> students;
  //  public static ArrayList<UniRequest> uniRequests;

  //  public static  ArrayList<User> users;


    public static Session session;



    public static StandardServiceRegistry registry;
    public static SessionFactory sessionFactory;


    //this class creates session and load on load function
    //this class has save or update function for each
    //? what about the relations?
    //create a thread to check if database is still connected!
    //dont forget to close

    public static void connect(){
        log.info("connecting to DataBase");
        try{
        registry = new StandardServiceRegistryBuilder().configure().build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        log.info("Connected to DataBase");
        } catch (HibernateException e) {
            log.info("connection failed cause:"+e.getMessage());
        }

    }
    public static void load(){  //only in the beginning
        session = sessionFactory.openSession();
        session.beginTransaction();
        //answers = (ArrayList<Answer>) session.createQuery("from Answer", Answer.class).list();
        //chats = (ArrayList<Chat>) session.createQuery("from Chat", Chat.class).list();
        //courses = (ArrayList<Course>) session.createQuery("from Course", Course.class).list();
        //departments = (ArrayList<Department>) session.createQuery("from Department", Department.class).list();
        //educationalContexts = (ArrayList<EducationalContext>) session.createQuery("from EducationalContext", EducationalContext.class).list();
        //exercises = (ArrayList<Exercise>) session.createQuery("from Exercise", Exercise.class).list();
        //masters = (ArrayList<Master>) session.createQuery("from Master", Master.class).list();
        //messages = (ArrayList<Message>) session.createQuery("from Message", Message.class).list();
        //scores = (ArrayList<Score>) session.createQuery("from Score", Score.class).list();
        //students = (ArrayList<Student>) session.createQuery("from Student", Student.class).list();
        //uniRequests = (ArrayList<UniRequest>) session.createQuery("from UniRequest", UniRequest.class).list();
        //users = (ArrayList<User>) session.createQuery("from User", User.class).list();
        log.info("Data loaded from Database");
        session.close();
    }

    //SaveOrUpdate(obj);

    public static void save(Object user){
        session = sessionFactory.openSession();
        session.beginTransaction();
        //session.merge(user);
        session.persist(user);  //saveOr
       // users.removeIf(oldUser -> oldUser.getId() == user.getId());
        //users.add(user);

    }


    public static List<User> getUsers(){
        session = sessionFactory.openSession();
        List<User> users = session.createQuery("from User", User.class).list();
        session.close();
        return users;
    }


    public static User getUserById(int id) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        log.info("Got user By id:"+id);
        User user =  session.find(User.class,id);
        session.close();
        return user;
    }

    public static Chat getChatById(int chatId) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        Chat chat =  session.find(Chat.class,chatId);
        session.close();
        log.info("Got Chat By id:"+chatId);
        return chat;

    }

    public static Department getDepartmentById(int departmentId){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Department department = session.find(Department.class,departmentId);
        session.close();
        log.info("Got Department By id:"+departmentId);
        return department;

    }

    public  static Course getCourseById(int courseId){
        session = sessionFactory.openSession();
        session.beginTransaction();
        Course course = session.find(Course.class,courseId);
        log.info("Got Course By id:"+courseId);
        session.close();
        return course;

    }

    public static EducationalContext getEducationalContextById(int cwEducationalContextId) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        EducationalContext educationalContext = session.find(EducationalContext.class,cwEducationalContextId);
        session.close();
        log.info("Got EducationalContext By id:"+cwEducationalContextId);
        return educationalContext;
    }


    public static Exercise getExerciseById(int exerciseId) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        Exercise exercise = session.find(Exercise.class,exerciseId);
        log.info("Got Exercise By id:"+exerciseId);
        session.close();
        return exercise;
    }

    public static void deleteMaster(String s) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        Master master = session.find(Master.class,s);
        session.remove(master);
        //masters.remove(master);
        session.getTransaction().commit();
        
        session.clear();
    }

    public static void deleteCourse(String s) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        Course course = session.find(Course.class,s);
        session.remove(course);
        //courses.remove(course);
        session.getTransaction().commit();
        
        session.clear();
    }

    public static void addRecommendationRequest(int id, ArrayList<String> arrayList) {
        session = sessionFactory.openSession();
        session.beginTransaction();



        UniRequest uniRequest = new UniRequest();
        uniRequest.setUniRequesterId(id);
        uniRequest.setUniRequestTime(LocalDateTime.now().toString());
        uniRequest.setUniRequestType(UniRequestType.RECOMMENDATION);
        uniRequest.setUniRequestResult(UniRequestResultType.NOT_ANSWERED);
        uniRequest.setUniRequestResponsibleId(Integer.parseInt(arrayList.get(0)));

        User user1 = session.find(User.class,id);
        ArrayList<UniRequest> arrayList1 = (ArrayList<UniRequest>) user1.getOutUniRequests();
        arrayList1.add(uniRequest);
        user1.setOutUniRequests(arrayList1);
        session.merge(user1);
        User user2 = session.find(User.class,uniRequest.getUniRequesterId());
        ArrayList<UniRequest> arrayList2 = (ArrayList<UniRequest>) user2.getInUniRequests();
        arrayList2.add(uniRequest);
        user2.setInUniRequests(arrayList1);
        session.merge(user2);


        session.persist(uniRequest);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addEnrollmentRequest(int id) {
        UniRequest uniRequest = new UniRequest();
        uniRequest.setUniRequesterId(id);
        uniRequest.setUniRequestTime(LocalDateTime.now().toString());
        uniRequest.setUniRequestType(UniRequestType.ENROLLMENT);
        uniRequest.setUniRequestResult(UniRequestResultType.ACCEPTED);
        String context = (String) Configuration.labels.get("Enrollment");
        context = context.replace("@1",getUserById(id).getUserFullName());
        context = context.replace("@3",getDepartmentById(getUserById(id).getStudent().getStudentDepartmentId()).getDepartmentType().name);
        context = context.replace("@2",getUserById(id).getStudent().getStudentshipNumber());
        context =context.replace("@4",LocalDateTime.now().plusYears(1).toString());
        uniRequest.setUniRequestContext(context);

        session = sessionFactory.openSession();
        session.beginTransaction();

        User user1 = session.find(User.class,id);
        ArrayList<UniRequest> arrayList1 = (ArrayList<UniRequest>) user1.getOutUniRequests();
        arrayList1.add(uniRequest);
        user1.setOutUniRequests(arrayList1);
        session.merge(user1);


        session.persist(uniRequest);
        session.getTransaction().commit();
        
        session.clear();
        session.close();

    }

    public static void addMinorRequest(int id, ArrayList<String> arrayList) {
        UniRequest uniRequest = new UniRequest();
        uniRequest.setUniRequesterId(id);
        uniRequest.setUniRequestTime(LocalDateTime.now().toString());
        uniRequest.setUniRequestType(UniRequestType.MINOR);
        uniRequest.setUniRequestResult(UniRequestResultType.NOT_ANSWERED);
        uniRequest.setUniRequestResponsibleId(getDepartmentById(getUserById(id).getStudent().getStudentDepartmentId()).getDepartmentAssistantId());
        String context = (String) Configuration.labels.get("MinorRequest");
        context = context.replace("@1",getUserById(id).getUserFullName());
        context = context.replace("@2",getUserById(id).getStudent().getStudentshipNumber());
        context = context.replace("@3",getDepartmentById(Integer.parseInt(arrayList.get(0))).getDepartmentType().name);
        uniRequest.setUniRequestContext(context);

        //TODO should be answered by both
        session = sessionFactory.openSession();
        session.beginTransaction();

        User user1 = session.find(User.class,id);
        ArrayList<UniRequest> arrayList1 = (ArrayList<UniRequest>) user1.getOutUniRequests();
        arrayList1.add(uniRequest);
        user1.setOutUniRequests(arrayList1);
        session.merge(user1);


        User user2 = session.find(User.class,uniRequest.getUniRequesterId());
        ArrayList<UniRequest> arrayList2 = (ArrayList<UniRequest>) user2.getInUniRequests();
        arrayList2.add(uniRequest);
        user2.setInUniRequests(arrayList1);
        session.merge(user2);



        session.persist(uniRequest);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addWithdrawRequest(int id) {
        UniRequest uniRequest = new UniRequest();
        uniRequest.setUniRequesterId(id);
        uniRequest.setUniRequestTime(LocalDateTime.now().toString());
        uniRequest.setUniRequestType(UniRequestType.WITHDRAWAL);
        uniRequest.setUniRequestResult(UniRequestResultType.NOT_ANSWERED);
        uniRequest.setUniRequestResponsibleId(getDepartmentById(getUserById(id).getStudent().getStudentDepartmentId()).getDepartmentAssistantId());

        session = sessionFactory.openSession();
        session.beginTransaction();

        User user1 = session.find(User.class,id);
        ArrayList<UniRequest> arrayList1 = (ArrayList<UniRequest>) user1.getOutUniRequests();
        arrayList1.add(uniRequest);
        user1.setOutUniRequests(arrayList1);
        session.merge(user1);


        User user2 = session.find(User.class,uniRequest.getUniRequesterId());
        ArrayList<UniRequest> arrayList2 = (ArrayList<UniRequest>) user2.getInUniRequests();
        arrayList2.add(uniRequest);
        user2.setInUniRequests(arrayList1);
        session.merge(user2);


        session.persist(uniRequest);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addDormitoryRequest(int id) {
        UniRequest uniRequest = new UniRequest();
        uniRequest.setUniRequesterId(id);
        uniRequest.setUniRequestTime(LocalDateTime.now().toString());
        uniRequest.setUniRequestType(UniRequestType.DORMITORY);
        Random random = new Random();
        if(random.nextInt()>0){
            uniRequest.setUniRequestResult(UniRequestResultType.ACCEPTED);
        }
        else uniRequest.setUniRequestResult(UniRequestResultType.REJECTED);

        session = sessionFactory.openSession();
        session.beginTransaction();


        User user1 = session.find(User.class,id);
        ArrayList<UniRequest> arrayList1 = (ArrayList<UniRequest>) user1.getOutUniRequests();
        arrayList1.add(uniRequest);
        user1.setOutUniRequests(arrayList1);
        session.merge(user1);


        session.persist(uniRequest);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addDDDRequest(int id) {
        UniRequest uniRequest = new UniRequest();
        uniRequest.setUniRequesterId(id);
        uniRequest.setUniRequestTime(LocalDateTime.now().toString());
        uniRequest.setUniRequestType(UniRequestType.DOCTORAL_DISSERTATION_DEFENSE);
        uniRequest.setUniRequestContext("you can defend on "+LocalDateTime.now().plusDays(1));  //add config.
        uniRequest.setUniRequestResult(UniRequestResultType.ACCEPTED);

        session = sessionFactory.openSession();
        session.beginTransaction();

        User user1 = session.find(User.class,id);
        ArrayList<UniRequest> arrayList1 = (ArrayList<UniRequest>) user1.getOutUniRequests();
        arrayList1.add(uniRequest);
        user1.setOutUniRequests(arrayList1);
        session.merge(user1);


        session.persist(uniRequest);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }


    public static void AddOrEditCourse(String departmentId,ArrayList<String> arrayList) {

        //public void requestAddCourse(String courseName, String courseNumber,
        //        String courseMasterId, ArrayList<String> courseWeaklyPlan, String courseExamTime,
          //      String courseCapacity, String courseType, String courseUnits, String coursePrerequisiteId, String courseCorequisiteId){



        Course course = new Course();
        course.setCourseUnits(Integer.parseInt(arrayList.get(7)));
        course.setCourseType(CourseType.valueOf(arrayList.get(6)));
        course.setCourseNumber(arrayList.get(1));
        course.setCourseDepartmentId(Integer.parseInt(departmentId));
        course.setCourseMasterId(Integer.parseInt(arrayList.get(2)));
        LocalDateTime localDateTime = LocalDateTime.now();
        //course.setTerm(String.valueOf(localDateTime.getYear());
        //course.setCourseTAs();
       // course.setCourseExamTime(LocalDateTime.parse(arrayList.get(4)));
        //course.setCourseCorequisites();
        //course.setCoursePrerequisites();
       // course.setCourseCapacity(Integer.parseInt(arrayList.get(5)));
        //course.setCourseWeaklyPlan();   //todo
        //course.setCourseGroup();
        course.setCourseName(arrayList.get(0));

        session = sessionFactory.openSession();
        session.beginTransaction();


        Department department = getDepartmentById(Integer.parseInt(departmentId));
        if(!department.getDepartmentCourses().contains(course)) {
            List<Course> list = department.getDepartmentCourses();
            list.add(course);
            department.setDepartmentCourses(list);
            session.merge(department);
        }



        session.merge(course);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addOrEditMaster(String departmentId,ArrayList<String> arrayList, byte[] fileInByte) {
        //String masterType, String masterGrade, String masteryNumber, String masterRoomNumber, String masterDepartmentId,
        ////        String username, String userFullName,
        //byte[] userImage, String userEmail, String userPhoneNumber, String userNationalNumber ,String pass
        User masterUser = new User();
        Master master = new Master();
        master.setMasterDepartmentId(Integer.parseInt(departmentId));
        master.setMasterGrade(MasterGrade.valueOf(arrayList.get(1)));
        master.setMasterType(MasterType.valueOf(arrayList.get(0)));
        master.setMasterShipNumber(arrayList.get(2));
        master.setMasterRoomNumber(arrayList.get(3));
        master.setUser(masterUser);

        masterUser.setMaster(master);
        masterUser.setUserType(UserType.MASTER);
        masterUser.setUserEmail(arrayList.get(6));
        masterUser.setUserPassword(arrayList.get(9));
        masterUser.setUsername(arrayList.get(4));
        masterUser.setUserPhoneNumber(arrayList.get(7));
        masterUser.setUserNationalNumber(arrayList.get(8));
        masterUser.setUserLastLoginTime(LocalDateTime.now());
        masterUser.setUserFullName(arrayList.get(5));
        masterUser.setCanLogin(true);
        masterUser.setUserImage(fileInByte);

        session = sessionFactory.openSession();
        session.beginTransaction();

        Department department = getDepartmentById(Integer.parseInt(departmentId));
        if(!department.getDepartmentCourses().contains(master)) {
            List<Master> list = department.getDepartmentMasters();
            list.add(master);
            department.setDepartmentMasters(list);
            session.merge(department);
        }

        session.merge(master);
        session.merge(masterUser);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addMessage(int id, ArrayList<String> arrayList, byte[] fileInByte) {
        session = sessionFactory.openSession();
        session.beginTransaction();

        Message message = new Message();
        message.setMessageContent(arrayList.get(1));
        message.setMessageChatId(Integer.parseInt(arrayList.get(0)));
        message.setMessageTime(LocalDateTime.now());
        message.setMessageSenderId(id);
        if(fileInByte!=null){
            message.setMessageType(MessageType.FILE);
            message.setMessageFile(fileInByte);
        }
        else{
            message.setMessageType(MessageType.TEXT);
        }

        Chat chat = session.find(Chat.class,arrayList.get(0));
        ArrayList<Message> arrayList1 = new ArrayList<>(chat.getMessages());
        arrayList1.add(message);
        chat.setMessages(arrayList1);
        chat.setLastMessage(message.getMessageContent());
        chat.setLastMessageTime(message.getMessageTime());

        session.persist(message);
        session.merge(chat);           //TODO merge it
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addCreateChatRequest(int id, String s) {
        UniRequest uniRequest = new UniRequest();
        uniRequest.setUniRequesterId(id);
        uniRequest.setUniRequestTime(LocalDateTime.now().toString());
        uniRequest.setUniRequestType(UniRequestType.CREATE_CHAT);
        uniRequest.setUniRequestContext("wanna chat?");  //add config.
        uniRequest.setUniRequestResult(UniRequestResultType.NOT_ANSWERED);

        for(Master master:session.createQuery("from Master", Master.class).list()){
            if(Objects.equals(master.getMasterShipNumber(), s)) uniRequest.setUniRequestResponsibleId(master.getId());
        }
        for(Student student:session.createQuery("from Student", Student.class).list()){
            if(Objects.equals(student.getStudentshipNumber(), s)) uniRequest.setUniRequestResponsibleId(student.getId());
        }

        session = sessionFactory.openSession();
        session.beginTransaction();

        User user1 = session.find(User.class,id);
        ArrayList<UniRequest> arrayList1 = (ArrayList<UniRequest>) user1.getOutUniRequests();
        arrayList1.add(uniRequest);
        user1.setOutUniRequests(arrayList1);
        session.merge(user1);


        User user2 = session.find(User.class,uniRequest.getUniRequesterId());
        ArrayList<UniRequest> arrayList2 = (ArrayList<UniRequest>) user2.getInUniRequests();
        arrayList2.add(uniRequest);
        user2.setInUniRequests(arrayList1);
        session.merge(user2);


        session.persist(uniRequest);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void requestUnitSelectionFromAssistant(int id, String s) {
        UniRequest uniRequest = new UniRequest();
        uniRequest.setUniRequesterId(id);
        uniRequest.setUniRequestTime(LocalDateTime.now().toString());
        uniRequest.setUniRequestType(UniRequestType.UNITSELCTION);
        uniRequest.setUniRequestContext(s);  ///todo
        uniRequest.setUniRequestResult(UniRequestResultType.NOT_ANSWERED);
        uniRequest.setUniRequestResponsibleId(getDepartmentById(getUserById(id).getStudent().getStudentDepartmentId()).getDepartmentAssistantId());

        session = sessionFactory.openSession();
        session.beginTransaction();

        User user1 = session.find(User.class,id);
        ArrayList<UniRequest> arrayList1 = (ArrayList<UniRequest>) user1.getOutUniRequests();
        arrayList1.add(uniRequest);
        user1.setOutUniRequests(arrayList1);
        session.merge(user1);


        User user2 = session.find(User.class,uniRequest.getUniRequesterId());
        ArrayList<UniRequest> arrayList2 = (ArrayList<UniRequest>) user2.getInUniRequests();
        arrayList2.add(uniRequest);
        user2.setInUniRequests(arrayList1);
        session.merge(user2);


        session.persist(uniRequest);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addExercise(ArrayList<String> arrayList, byte[] fileInByte) {
        Exercise exercise = new Exercise();
        exercise.setExerciseCourseId(Integer.parseInt(arrayList.get(0)));
        exercise.setExerciseName(arrayList.get(1));
        exercise.setExerciseStartTime(LocalDateTime.parse(arrayList.get(2)));
        exercise.setExerciseEndTime(LocalDateTime.parse(arrayList.get(3)));
        exercise.setExerciseDeadline(LocalDateTime.parse(arrayList.get(4)));
        exercise.setExplanation(arrayList.get(5));
        //exercise.setAnswerType(arrayList.get(6));  //todo
        exercise.setExerciseFile(fileInByte);


        Course course = getCourseById(exercise.getExerciseCourseId());
        List<Exercise> exercises = course.getCourseExercises();
        exercises.add(exercise);
        course.setCourseExercises(exercises);



        session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(exercise);
        session.merge(course);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addAnswer(ArrayList<String> arrayList, byte[] fileInByte) {
        Answer answer = new Answer();
        answer.setAnswerFile(fileInByte);
        answer.setAnswerExerciseId(Integer.parseInt(arrayList.get(0)));
        answer.setAnswerText(arrayList.get(1));


        Exercise exercise = getExerciseById(answer.getAnswerExerciseId());
        List<Answer> answers = exercise.getExerciseAnswers();
        answers.add(answer);
        exercise.setExerciseAnswers(answers);

        session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(answer);
        session.merge(exercise);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addOrEditEducationContext(ArrayList<String> arrayList, byte[] fileInByte) {
        EducationalContext educationalContext = new EducationalContext();
        educationalContext.setEducationalContextName(arrayList.get(1));
        educationalContext.setEducationalContextText(arrayList.get(2));
        educationalContext.setEducationalContextFile(fileInByte);
        educationalContext.setEducationalContextTime(LocalDateTime.now());

        if(fileInByte!=null){
            educationalContext.setEducationalContextType(EducationalContextType.FILE);
        }
        else educationalContext.setEducationalContextType(EducationalContextType.TEXT);
        educationalContext.setEducationalContextCourseId(Integer.parseInt(arrayList.get(0)));


        Course course = getCourseById(educationalContext.getEducationalContextCourseId());
        List<EducationalContext> educationalContexts = course.getCourseEducationalContexts();
        educationalContexts.add(educationalContext);
        course.setCourseEducationalContexts(educationalContexts);

        session = sessionFactory.openSession();
        session.beginTransaction();
        session.persist(educationalContext);
        session.merge(course);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void setLastloginTime(ClientHandler clientHandler) {
        session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.find(User.class,clientHandler.getUser().getId());
        user.setUserLastLoginTime(LocalDateTime.now());

        session.merge(user);
        session.getTransaction().commit();
        session.clear();
        session.close();
    }
    public static void changeEmail(int id, String s) {
        session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.find(User.class,id);
        user.setUserEmail(s);

        session.merge(user);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void changePhoneNumber(int id, String s) {
        session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.find(User.class,id);
        user.setUserPhoneNumber(s);

        session.merge(user);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void changePass(int id, String s) {
        session = sessionFactory.openSession();
        session.beginTransaction();

        User user = session.find(User.class,id);
        user.setUserPassword(s);

        session.merge(user);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void deleteEducationalContext(String s) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        EducationalContext educationalContext = session.find(EducationalContext.class,s);
        session.remove(educationalContext);
        session.createQuery("from EducationalContext", EducationalContext.class).list().remove(educationalContext);
        session.getTransaction().commit();
        
        session.clear();
    }

    public static void addScorePortestation(ArrayList<String> arrayList) {
        session = sessionFactory.openSession();
        session.beginTransaction();

        Score score = session.find(Score.class,arrayList.get(0));
        score.setScoreProtestation(arrayList.get(1));

        session.merge(score);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addScorePortestationAnswer(ArrayList<String> arrayList) {
        session = sessionFactory.openSession();
        session.beginTransaction();

        Score score = session.find(Score.class,arrayList.get(0));
        score.setScoreProtestationAnswer(arrayList.get(1));

        session.merge(score);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }
    public static void setScoreValue(ArrayList<String> arrayList) {
        session = sessionFactory.openSession();
        session.beginTransaction();

        Score score = session.find(Score.class,arrayList.get(0));
        double doube = Double.parseDouble(arrayList.get(1));
        int integer = (int) Math.round(4*doube);
        score.setScoreValue(integer/4);
        score.setScoreType(ScoreType.TEMPORARY);

        session.merge(score);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void verifyScores(ArrayList<String> arrayList) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        Course course = session.find(Course.class,arrayList.get(0));
        for(Score score:course.getScores()){
            score.setScoreType(ScoreType.VERIFIED);
            session.merge(score);

        }
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }
    public static void ScoreAnswer(String s, String s1) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        Answer answer = session.find(Answer.class,s);
        answer.setAnswerScore(Double.parseDouble(s1));

        session.merge(answer);

        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void answerUniRequest(ArrayList<String> arrayList) {
        session = sessionFactory.openSession();
        session.beginTransaction();

        UniRequest uniRequest = session.find(UniRequest.class,arrayList.get(0));
        boolean result = Boolean.parseBoolean(arrayList.get(1));
        if(result) {
            if(uniRequest.getUniRequestType()==UniRequestType.MINOR){
                uniRequest.getUniRequestResponsibleId() ;  //todo
            }
            uniRequest.setUniRequestResult(UniRequestResultType.ACCEPTED);
        }
        else uniRequest.setUniRequestResult(UniRequestResultType.REJECTED);


        session.merge(uniRequest);

        session.getTransaction().commit();
        
        session.clear();
        session.close();

    }
    public static void statOrDestarCourse(Student student, String s) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        ArrayList<Course> courseArrayList = (ArrayList<Course>) student.getStudentSelectedCourse();
        Course course = session.find(Course.class,s);
        if(courseArrayList.contains(course)){
            courseArrayList.remove(course);
        } else if (!courseArrayList.contains(course)) courseArrayList.add(course);

        student.setStudentSelectedCourse(courseArrayList);

        session.merge(student);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void preRegisterCourse(Student student, String s) {
        Course course = getCourseById(Integer.parseInt(s));
        ArrayList<Course> courseArrayList = (ArrayList<Course>) student.getStudentPreRegisteredCourses();
        courseArrayList.add(course);
        student.setStudentPreRegisteredCourses(courseArrayList);

        ArrayList<Student> studentArrayList = (ArrayList<Student>) course.getCoursePreregisteredStudents();
        studentArrayList.add(student);
        course.setCoursePreregisteredStudents(studentArrayList);
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.merge(student);
        session.merge(course);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void undoPreRegisterCourse(Student student, String s) {
        Course course = getCourseById(Integer.parseInt(s));
        ArrayList<Course> courseArrayList = (ArrayList<Course>) student.getStudentPreRegisteredCourses();
        courseArrayList.remove(course);
        student.setStudentPreRegisteredCourses(courseArrayList);

        ArrayList<Student> studentArrayList = (ArrayList<Student>) course.getCoursePreregisteredStudents();
        studentArrayList.remove(student);
        course.setCoursePreregisteredStudents(studentArrayList);
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.merge(student);
        session.merge(course);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addTAToCourse(String s, String s1) {
        Student student = null;
        for(Student student1: session.createQuery("from Student", Student.class).list()){
            if (Objects.equals(student1.getStudentshipNumber(), s1)) {student=student1;}
        }
        Course course = getCourseById(Integer.parseInt(s));
        assert student != null;
        ArrayList<Course> courseArrayList = (ArrayList<Course>) student.getStudentAssistingCourses();
        courseArrayList.add(course);
        student.setStudentAssistingCourses(courseArrayList);

        ArrayList<Student> studentArrayList = (ArrayList<Student>) course.getCourseTAs();
        studentArrayList.add(student);
        course.setCourseTAs(studentArrayList);
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.merge(student);
        session.merge(course);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void addStudentToCourse(String s, String s1) {
        Student student = null;
        for(Student student1:session.createQuery("from Student", Student.class).list()){
            if (Objects.equals(student1.getStudentshipNumber(), s1)) {student=student1;}
        }
        Course course = getCourseById(Integer.parseInt(s));
        assert student != null;
        ArrayList<Course> courseArrayList = (ArrayList<Course>) student.getStudentCourses();
        courseArrayList.add(course);
        student.setStudentCourses(courseArrayList);

        ArrayList<Student> studentArrayList = (ArrayList<Student>) course.getCourseStudents();
        studentArrayList.add(student);
        course.setCourseStudents(studentArrayList);
        session = sessionFactory.openSession();
        session.beginTransaction();
        session.merge(student);
        session.merge(course);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }

    public static void changeGroup(String id,ArrayList<String> arrayList) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        Student student = session.find(Student.class,id);
        Course course1 = session.find(Course.class,arrayList.get(0));
        Course course2 = session.find(Course.class,arrayList.get(1));

        ArrayList<Course> courseArrayList = (ArrayList<Course>) student.getStudentCourses();
        courseArrayList.remove(course1);
        courseArrayList.add(course2);
        student.setStudentCourses(courseArrayList);

        ArrayList<Student> studentArrayList1 = (ArrayList<Student>) course1.getCourseStudents();
        studentArrayList1.remove(student);
        course1.setCourseStudents(studentArrayList1);


        ArrayList<Student> studentArrayList2 = (ArrayList<Student>) course2.getCourseStudents();
        studentArrayList2.add(student);
        course2.setCourseStudents(studentArrayList2);

        session.merge(student);
        session.merge(course1);
        session.merge(course2);
        session.getTransaction().commit();
        
        session.clear();
        session.close();
    }


    public static void setUnitSelectionTime(ArrayList<String> arrayList) {
    }

    public static ArrayList<Course> getCourses() {
        session = sessionFactory.openSession();
        session.beginTransaction();
        List<Course> list = session.createQuery("from Course", Course.class).list();
        session.clear();
        session.close();
        return (ArrayList<Course>) list;
    }


    public static ArrayList<Master> getMasters() {
        session = sessionFactory.openSession();
        session.beginTransaction();
        List<Master> list = session.createQuery("from Master", Master.class).list();
        session.clear();
        session.close();
        return (ArrayList<Master>) list;
    }

    public static ArrayList<Student> getStudents() {
        session = sessionFactory.openSession();
        session.beginTransaction();
        List<Student> list = session.createQuery("from Student", Student.class).list();
        session.clear();
        session.close();
        return (ArrayList<Student>) list;
    }


    public static User getUserByUserName(String username){
        for(User user:getUsers()){
            if(Objects.equals(user.getUsername(), username)){
                return user;
            }
        }
        return null;
    }

    //TODO do log gor save delete and edits
}
