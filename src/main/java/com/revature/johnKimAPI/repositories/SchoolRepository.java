package com.revature.johnKimAPI.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.*;
import com.revature.johnKimAPI.pojos.Course;
import com.revature.johnKimAPI.pojos.Enrolled;
import com.revature.johnKimAPI.pojos.Faculty;
import com.revature.johnKimAPI.pojos.Student;
import com.revature.johnKimAPI.util.GetMongoClient;
import com.revature.johnKimAPI.util.exceptions.InvalidRequestException;
import com.revature.johnKimAPI.util.exceptions.ResourcePersistenceException;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
    Create, Read, Update, and Delete using this script. It also performs certain queries to get data
    from the database, extensions of the feat of reading. This is the closest thing I have to a "god class"
    inside of my application.
 */

public class SchoolRepository {
    private final MongoClient mongoClient;

    public SchoolRepository(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public SchoolRepository() {
        this.mongoClient = GetMongoClient.generate().getConnection();
    }


    // These are used quite often in the CRUD methods.
    Logger logger = LoggerFactory.getLogger(SchoolRepository.class);
    private final ObjectMapper mapper = new ObjectMapper();
    CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
    CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
    Student student;

    // ====CREATE====
    // This method persists users to the database.
    public Student save(Student newStudent) {

        try {
            MongoDatabase database = mongoClient.getDatabase("Project0School").withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Document> collection = database.getCollection("StudentCredentials");

            Document newUserDoc = new Document ("firstName", newStudent.getFirstName())
                                        .append("lastName", newStudent.getLastName())
                                        .append("email", newStudent.getEmail())
                                        .append("username", newStudent.getUsername())
                                        .append("hashPass", newStudent.getHashPass());

            // this inserts the instance into the "StudentCredentials" database.
            collection.insertOne(newUserDoc);

            newStudent.setStudentID(newUserDoc.get("_id").toString());
            System.out.println(newStudent);

        } catch (Exception e) {
            logger.error("Threw an exception at SchoolRepository::save(), full StackTrace follows: " + e);
        }
        return newStudent;
    }

    public void enroll(Enrolled course) {
        try {
            MongoDatabase database = mongoClient.getDatabase("Project0School").withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Document> collection = database.getCollection("enrolled");

            Document newDoc = new Document ("classID", course.getClassID())
                                        .append("name", course.getName())
                                        .append("teacher", course.getTeacher())
                                        .append("username", course.getUsername())
                                        .append("open", true);
            // this inserts the instance into the "StudentCredentials" database.
            collection.insertOne(newDoc);

        } catch (Exception e) {
            logger.error("Threw an exception at SchoolRepository::register(), full StackTrace follows: " + e);
            throw new ResourcePersistenceException("We could not enroll you in that course!");
        }
    }

    public void newCourse(Course course) {
        try {
            MongoDatabase database = mongoClient.getDatabase("Project0School").withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Course> collection = database.getCollection("classes", Course.class);

            // this inserts the instance into the "StudentCredentials" database.
            collection.insertOne(course);

        } catch (Exception e) {
            logger.error("Threw an exception at SchoolRepository::newCourse(), full StackTrace follows: " + e);
            throw new ResourcePersistenceException("We're sorry, but we could not register your class!");
        }
    }


    // ====READ====
    // This is used for the login function in the service layer.
    public Student findStudentByCredentials(String username, int hashPass) {

        try {
            MongoDatabase p0school = mongoClient.getDatabase("Project0School").withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Document> usersCollection = p0school.getCollection("StudentCredentials");
            Document queryDoc = new Document("username", username).append("hashPass", hashPass);
            Document userCredentials = usersCollection.find(queryDoc).first();

            if (userCredentials == null) {
                return null;
            }

            Student student = mapper.readValue(userCredentials.toJson(), Student.class);
            student.setStudentID(userCredentials.get("_id").toString());

            return student;

        } catch (JsonProcessingException jpe) {
            logger.error("Threw an exception at SchoolRepository::findStudentByCredentials(), full StackTrace follows: " + jpe);
            throw new InvalidRequestException("An error has occurred while processing your request!");
        }
    }

    // This method is primarily for students to find classes that are accepting new entries.
    public List<Course> findCourseByOpen() {
        try {
            MongoDatabase p0school = mongoClient.getDatabase("Project0School").withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Course> usersCollection = p0school.getCollection("classes", Course.class);
            Document queryDoc = new Document("open", true);
            List<Course> courses = new ArrayList<>();
            usersCollection.find(queryDoc).into(courses);

            return courses;
        } catch (Exception e) {
            System.out.println("An exception has occurred!" + e.getMessage());
            logger.error("Problem occurred when parsing the data from Mongo. Full Stack Trace follows:: " + e);
        }
        return null;
    }

    public Course findCourseByID(String id) {
        MongoDatabase p0school = mongoClient.getDatabase("Project0School").withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Course> usersCollection = p0school.getCollection("classes", Course.class);
        Document queryDoc = new Document("classID", id);
        return usersCollection.find(queryDoc).first();
    }


    // This is used so that students can see their courses.
    public List<Enrolled> findEnrolledByUsername(String username) {

        List<Enrolled> enrolled = new ArrayList<>();
        MongoDatabase p0school = mongoClient.getDatabase("Project0School").withCodecRegistry(pojoCodecRegistry);
        MongoCollection<Enrolled> usersCollection = p0school.getCollection("enrolled", Enrolled.class);
        Document queryDoc = new Document("username", username);
        return usersCollection.find(queryDoc).into(enrolled);

    }

    // This method is primarily used by Teachers to find classes that they put onto the database, for deletion and updates.
    public List<Course> findCourseByTeacher(String lastName) {

        try {
            MongoDatabase p0school = mongoClient.getDatabase("Project0School").withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Course> usersCollection = p0school.getCollection("classes", Course.class);
            Document queryDoc = new Document("teacher", lastName);
            List<Course> courses = new ArrayList<>();

            usersCollection.find(queryDoc).into(courses);

            return courses;
        } catch (Exception e) {
            System.out.println("An exception has occurred!" + e.getMessage());
            logger.error("Problem occurred when parsing the data from Mongo. Full Stack Trace follows:: " + e);
        }
        return null;
    }

    // Primarily used to ensure that a Student's input username is unique.
    public Student findStudentByUsername(String username) {
        MongoDatabase p0school = mongoClient.getDatabase("Project0School");
        MongoCollection<Document> usersCollection = p0school.getCollection("StudentCredentials");
        Document queryDoc = new Document("username", username);
        Document isUsernameTaken = usersCollection.find(queryDoc).first();

        try {
            if (isUsernameTaken != null) {
                String json = isUsernameTaken.toJson();
                Student student = mapper.readValue(json, Student.class);
                student.setStudentID(isUsernameTaken.get("_id").toString());
                this.student = student;

            } else {
                return null;
            }

        } catch (JsonProcessingException jse) {
            logger.error("Threw a JsonProcessingException at SchoolRepository::isUsernameTaken, full StackTrace follows: " + jse);
        }
        return student;
    }

    // This checks to verify if a Student input a unique Email from all others in the system.
    public Student findStudentByEmail(String email) {
        MongoDatabase p0school = mongoClient.getDatabase("Project0School");
        MongoCollection<Document> usersCollection = p0school.getCollection("StudentCredentials");
        Document queryDoc = new Document("email", email);
        Document isEmailTaken = usersCollection.find(queryDoc).first();


        try {
            if (isEmailTaken != null) {
                String json = isEmailTaken.toJson();
                Student student = mapper.readValue(json, Student.class);
                student.setStudentID(isEmailTaken.get("_id").toString());
                this.student = student;

            } else {
                return null;
            }

        } catch (JsonProcessingException jse) {
            logger.error(jse.getMessage());
            logger.error("Threw a JsonProcessingException at SchoolRepository::isEmailTaken, full StackTrace follows: " + jse);
        }

        return student;
    }

    // This is used in the login function to determine if the user is valid.
    public Faculty findFacultyByCredentials(String username, int hashPass) {

        try {
            MongoDatabase p0school = mongoClient.getDatabase("Project0School");
            MongoCollection<Document> usersCollection = p0school.getCollection("FacultyCredentials");
            Document queryDoc = new Document("username", username).append("hashPass", hashPass);
            Document userCredentials = usersCollection.find(queryDoc).first();

            if (userCredentials == null) {
                return null;
            }

            Faculty authFac = mapper.readValue(userCredentials.toJson(), Faculty.class);
            authFac.setTeacherID(userCredentials.get("_id").toString());

            return authFac;

        } catch (Exception e) {
            logger.error("Threw an exception at SchoolRepository::findStudentByCredentials(), full StackTrace follows: " + e);
        }

        return null;
    }

    // ====UPDATE====
    // This class allows teachers to update the information related to their courses. It works by first
    // deleting the old course, then adding a new course with the same details in its place.
    public void updateCourse(Course course, String id, String teacher) {

        try {
            MongoDatabase p0school = mongoClient.getDatabase("Project0School").withCodecRegistry(pojoCodecRegistry);
            // Grab Bson collection
            MongoCollection<Document> facCollection = p0school.getCollection("classes");
            MongoCollection<Document> enrollCollection = p0school.getCollection("enrolled");

            // Grab POJO objects from database
            MongoCollection<Course> updatedCourse = p0school.getCollection("classes", Course.class);
            MongoCollection<Enrolled> updatedEnrolled = p0school.getCollection("enrolled", Enrolled.class);

            // Query
            Document queryDoc = new Document("classID", id).append("teacher", teacher);
            Document oldCourse = facCollection.find(queryDoc).first();
            List<Document> bsonEnrolled = new ArrayList<>();
            List<Enrolled> oldEnrolled = new ArrayList<>();
            List<Enrolled> newEnrolled = new ArrayList<>();
            updatedEnrolled.find(queryDoc).into(oldEnrolled);
            enrollCollection.find(queryDoc).into(bsonEnrolled);

            if (oldCourse != null) {
                //Compile the new Enrolled with the details of the teacher-uploaded course
                for (Enrolled enroll : oldEnrolled) {

                    enroll.setName(course.getName());
                    enroll.setClassID(course.getClassID());
                    enroll.setDesc(course.getDesc());
                    enroll.setOpen(course.isOpen());

                    // Add it to the newEnrolled list
                    newEnrolled.add(enroll);
                }

                // Replace all old enrolled with the new enrolled
                for (int i = 0; i < newEnrolled.size(); i++) {
                    updatedEnrolled.replaceOne(bsonEnrolled.get(i), newEnrolled.get(i));
                }

                // Replace the old Course with the new one.
                updatedCourse.replaceOne(oldCourse, course);
            }

        } catch (Exception e) {
            logger.error("Threw an exception at SchoolRepository::updateCourse(), full StackTrace follows: " + e);
            throw new ResourcePersistenceException("We're sorry, but that could not be updated!");
        }
    }

    // ====DELETE====
    // This method is used by teachers to get rid of a course.
    public void deleteCourse(String courseID) {

        MongoDatabase database = mongoClient.getDatabase("Project0School");
        MongoCollection<Document> collection = database.getCollection("classes");
        MongoCollection<Document> enrolledCollection = database.getCollection("enrolled");
        Document queryDoc = new Document("classID", courseID);
        Document deletedCourse = collection.find(queryDoc).first();
        List<Document> enrolledCourses = new ArrayList<>();
        enrolledCollection.find(queryDoc).into(enrolledCourses);

        // this deletes all instances of the course which students might be enrolled to.
        if (deletedCourse != null) {
            for (Document enrolledCourse : enrolledCourses) {
                enrolledCollection.deleteMany(enrolledCourse);
            }

            // this deletes the instance from the "classes" database.
            collection.deleteOne(deletedCourse);
        } else {
            throw new InvalidRequestException("The course turned up no results!");
        }
    }

    public void deleteEnrolled(String courseID, String username) {

        MongoDatabase database = mongoClient.getDatabase("Project0School");
        MongoCollection<Document> collection = database.getCollection("enrolled");
        Document queryDoc = new Document("classID", courseID).append("username", username);
        Document deleted = collection.find(queryDoc).first();

        System.out.println(deleted);

        // this deletes the instance from the "classes" database.
        if (deleted != null) {
            collection.deleteOne(deleted);
            System.out.println("You have successfully dropped the course!");
        } else {
            throw new InvalidRequestException("We could not find a class with that ID!");
        }
    }
}