package com.revature.johnKimAPI.services;

import com.revature.johnKimAPI.pojos.Course;
import com.revature.johnKimAPI.pojos.Faculty;
import com.revature.johnKimAPI.pojos.Student;
import com.revature.johnKimAPI.repositories.SchoolRepository;
import com.revature.johnKimAPI.service.ValidationService;
import com.revature.johnKimAPI.util.exceptions.InvalidRequestException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ValidationServiceTestSuite {
    /*
    @Before
    @After
    @Test
    @BeforeTest
    @AfterTest
     */

    ValidationService sut;
    private SchoolRepository mockSchoolRepo;

    @Before
    public void beforeEachTest() {
        mockSchoolRepo = mock(SchoolRepository.class);
        sut = new ValidationService(mockSchoolRepo);
    }

    @After
    public void afterEachTest() {
        sut = null;
    }
/*
    @Test
    public void isUserValid_returnTrueForGivenValidUser() {

        // Arrange
        Student testStudent = new Student("username", 12562562, "firstname", "lastname", "email@email.net");

        // Act
        boolean actualResult = sut.isUserValid(testStudent);

        // Assert
        Assert.assertTrue("Expected user to be considered valid!", actualResult);
    }
/*
    @Test
    public void isUserValid_returnsFalseForGivenBlankValues() {

        // Arrange
        Student testStudent1 = new Student(null, 12562562, "firstname", "lastname", "email");
        Student testStudent2 = new Student("", 12562562, "firstname", "lastname", "email");
        Student testStudent3 = new Student("      ", 12562562, "firstname", "lastname", "email");

        // Act
        boolean actualResult1 = sut.isUserValid(testStudent1);
        boolean actualResult2 = sut.isUserValid(testStudent2);
        boolean actualResult3 = sut.isUserValid(testStudent3);

        // Assert
        Assert.assertFalse("The first name cannot be a null value!", actualResult1);
        Assert.assertFalse("The first name cannot be empty!", actualResult2);
        Assert.assertFalse("The first name cannot be empty space!", actualResult3);

    }

/*    @Test
    public void register_returnsTrueWhenGivenValidUser() {

        // Arrange
        Student testStudent = new Student("username", 12562562, "firstname", "lastname", "email");
        Student expectedStudent = new Student("username", 12562562, "firstname", "lastname", "email");
        when(mockSchoolRepo.save(any())).thenReturn(expectedStudent);

        // Act
        mockSchoolRepo.save(testStudent);

        // Assert
        Assert.assertEquals(expectedStudent, testStudent);
        verify(mockSchoolRepo, times(1)).save(any());

    }

    @Test(expected = InvalidRequestException.class)
    public void register_throwsException_whenGivenInvalidUser() {

        // Arrange
        Student invalidStudent = new Student(null, 123456, "", "", "");

        // Act
        try {
            sut.register(invalidStudent);
        } finally {
            // Assert
            verify(mockSchoolRepo, times(0)).save(any());
        }
    }
*/
    @Test
    public void returnsTrue_GivenValidCourseInput() {

        // Arrange
        Course testCourse = new Course("ValidCourseName","TST242","This description is a completely valid one.","Valid",true);
        when(mockSchoolRepo.findCourseByID(anyString())).thenReturn(null);

        // Act
        boolean actualResult = sut.isCourseValid(testCourse);

        // Assert
        Assert.assertTrue("This is a valid course, one that would be put into the database.", actualResult);

    }

    @Test
    public void returnsFalse_GivenDuplicateClassID() {

        // Arrange
        Course testCourse = new Course("ValidCourseName","tst242","This description is a completely valid one.","Valid",true);
        when(mockSchoolRepo.findCourseByID(anyString())).thenReturn(null);

        // Act
        boolean actualResult = sut.isCourseValid(testCourse);

        // Assert
        Assert.assertFalse(actualResult);
    }

//    @Test
//    public void deleteAndDeregister_returnsTrue_GivenValidClassID() {
//
//        // Arrange
//        String validID = "CHM242";
//        when(mockSchoolRepo.findCourseByID(anyString())).thenReturn(new Course("TestyPlaceholder","TST222","This is a placeholder","Valid",true));
//        sut.setAuthStudent(new Student("ValidUser", "banana".hashCode(),"Test","Validerson","test@text.com"));
//
//        // Act
//        sut.deregister(validID);
//        sut.deleteCourse(validID);
//
//        // Assert
//        verify(mockSchoolRepo, times(1)).deleteEnrolled(any(), any());
//        verify(mockSchoolRepo, times(1)).deleteCourse(any());
//    }

    @Test
    public void updateCourse_ReturnsTrue_GivenValidCourseAndClassID() {

        // Arrange
        String validID = "CHM242";
        Course validCourse = new Course("Chemistry 2","CHM242","This is a test course, only here because there is need.","Test",true);
        sut.setAuthFac(new Faculty("TestDummy","banana".hashCode(),"Test","Validson","test@text.com"));

        // Act
        sut.updateCourse(validCourse, validID);

        // Assert
        verify(mockSchoolRepo, times(1)).updateCourse(any(), any(), any());
    }

    @Test
    public void getStudentAndFac_ReturnsTrue_GivenValidUsers() {

        // Assert
        sut.setAuthFac(new Faculty("TestDummy","banana".hashCode(),"Test","Validson","test@text.com"));
        sut.setAuthStudent(new Student("TestDummy","banana".hashCode(),"Test","Validson","test@text.com"));

        // Act
        Faculty testFac = sut.getAuthFac();
        Student testStudent = sut.getStudent();

        // Assert
        Assert.assertNotEquals(null, testFac);
        Assert.assertNotEquals(null, testStudent);
    }

    @Test
    public void getStudentAndAuthfac_ReturnsFalse_GivenNullValues() {

        // Arrange
        sut.setAuthStudent(null);
        sut.setAuthFac(null);

        // Act
        Faculty testFac = sut.getAuthFac();
        Student testStudent = sut.getStudent();

        // Assert
        Assert.assertNull(testFac);
        Assert.assertNull(testStudent);
    }

    @Test
    public void facAndStudentLogin_ReturnsTrue_GivenValidCredentials() {

        // Arrange
        String username = "Valid";
        int password = "Valid".hashCode();

        // Act
        sut.facLogin(username, password);
        sut.login(username, password);

        // Assert
        verify(mockSchoolRepo, times(1)).findFacultyByCredentials(anyString(), anyInt());
        verify(mockSchoolRepo, times(1)).findStudentByCredentials(anyString(), anyInt());
    }

    @Test(expected = InvalidRequestException.class)
    public void facAndStudentLogin_ThrowsException_GivenBlankUsername() {

        // Arrange
        String falseUser = "   ";
        int password = "Valid".hashCode();

        try {
            // Act
            sut.facLogin(falseUser, password);
            sut.login(falseUser, password);
        } finally {
            // Assert
            verify(mockSchoolRepo, times(0)).findStudentByCredentials(anyString(), anyInt());
            verify(mockSchoolRepo, times(0)).findFacultyByCredentials(anyString(), anyInt());
        }
    }

    @Test
    public void logoutFunction_SetsVariablesToNull() {

        // Arrange
        sut.setAuthFac(new Faculty("TestDummy","banana".hashCode(),"Test","Validson","test@text.com"));
        sut.setAuthStudent(new Student("TestDummy","banana".hashCode(),"Test","Validson","test@text.com"));

        // Act
        sut.logout();

        // Assert
        Assert.assertNull(sut.getAuthFac());
        Assert.assertNull(sut.getStudent());
    }

//    @Test
//    public void createAndEnrollCourse_ReturnTrue_GivenValidInput() {
//
//        // Arrange
//        Course testCourse = new Course("Testing and you 101","TST101","This is a placeholder for the sake of testing. Do not enroll to this.","Valid",true);
//        sut.setAuthFac(new Faculty("p0tter","banana".hashCode(),"Severus","Snape","SlytherinCyberSecurity@Hogwarts.com"));
//        sut.setAuthStudent(new Student("iemBatman","expelliarmus".hashCode(),"Harry","Potter","theCh0SenOne@Gryffindor.net"));
//
//        // Act
//        sut.enroll(testCourse);
//        sut.createCourse(testCourse);
//
//        // Assert
//        verify(mockSchoolRepo, times(1)).enroll(any());
//        verify(mockSchoolRepo, times(1)).newCourse(any());
//    }

    @Test
    public void deleteCourseAndDeregister_ReturnsFalse_GivenInvalidInput() {

        // Arrange
        when(mockSchoolRepo.findCourseByID(anyString())).thenReturn(null);
        String input = "tst242";
        String input2 = "TST949";

        // Act
//        boolean deregisterActualResult = sut.deregister(input);
//        boolean deregisterActualResult2 = sut.deregister(input2);
        boolean deleteCourseActualResult = sut.deleteCourse(input);
        boolean deleteCourseActualResult2 = sut.deleteCourse(input2);

        // Assert
//        Assert.assertFalse(deregisterActualResult);
//        Assert.assertFalse(deregisterActualResult2);
        Assert.assertFalse(deleteCourseActualResult);
        Assert.assertFalse(deleteCourseActualResult2);
    }
}