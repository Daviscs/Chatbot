/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package awsassertions;

import java.util.Scanner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aaronbuehne
 */
public class AWSAssertionsTest {
    
    public AWSAssertionsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class AWSAssertions.
     */
    @Test
    public void testCase1() {
        System.out.println("Test 1");
        
        String[] arguments = {String.valueOf(1)};
        String[] expected = {"null", "null", "\"2019-03-01\"", "\"13:00\"", "\"Check-up\""};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
        
    }
    
    @Test
    public void testCase2() {
        System.out.println("Test 2");
        
        String[] arguments = {String.valueOf(2)};
        String[] expected = {"null", "null", "\"2019-02-19\"", "null", "null"};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
    }
    
    @Test
    public void testCase3() {
        System.out.println("Test 3");
        
        String[] arguments = {String.valueOf(3)};
        String[] expected = {"null", "null", "null", "null", "\"whitening\""};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
    }
    
    @Test
    public void testCase4() {
        System.out.println("Test 4");
        
        String[] arguments = {String.valueOf(4)};
        String[] expected = {"\"Dentist\"", "null", "\"2019-03-05\"", "null", "null"};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
    }
    
    @Test
    public void testCase5() {
        System.out.println("Test 5");
        
        String[] arguments = {String.valueOf(5)};
        String[] expected = {"\"Dentist\"", "null", "\"1997-01-25\"", "null", "null"};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
    }
    
    @Test
    public void testCase6() {
        System.out.println("Test 6");
        
        String[] arguments = {String.valueOf(6)};
        String[] expected = {"null", "null", "null", "null", "\"Doctor\""};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
    }
    
    @Test
    public void testCase7() {
        System.out.println("Test 7");
        
        String[] arguments = {String.valueOf(7)};
        String[] expected = {"\"Dentist\"", "null", "null", "null", "null"};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
    }
    
    @Test
    public void testCase8() {
        System.out.println("Test 8");
        
        String[] arguments = {String.valueOf(8)};
        String[] expected = {"null", "null", "\"2019-03-11\"", "\"12:00\"", "\"whitening\'"};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
    }
    @Test
    public void testCase9() {
        System.out.println("Test 9");
        
        String[] arguments = {String.valueOf(9)};
        String[] expected = {"null", "null", "\"2019-03-02\"", "\"00:00\"", "\"check-up\'"};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
    }
    
    @Test
    public void testCase10() {
        System.out.println("Test 10");
        
        String[] arguments = {String.valueOf(10)};
        String[] expected = {"null", "null", "\"2019-02-25\"", "\"25:00\"", "\"carpet cleaning\'"};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
    }
    
    @Test
    public void testCase11() {
        System.out.println("Test 11");
        
        String[] arguments = {String.valueOf(11)};
        String[] expected = {"null", "null", "\"2019-10-27\"", "\"15:00\"", "\"check-up\'"};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
    }
    
    @Test
    public void testCase12() {
        System.out.println("Test 12");
        
        String[] arguments = {String.valueOf(12)};
        String[] expected = {"null", "null", "\"2019-03-01\"", "\"14:00\"", "\"whitening\'"};
        AWSAssertions.main(arguments);
        assertTrue(AWSAssertions.completed == 1);
        if(AWSAssertions.completed == 1){
            assertTrue(expected[0].equalsIgnoreCase(AWSAssertions.DoctorType));
            assertTrue(expected[1].equalsIgnoreCase(AWSAssertions.Zip));
            assertTrue(expected[2].equalsIgnoreCase(AWSAssertions.Date));
            assertTrue(expected[3].equalsIgnoreCase(AWSAssertions.Time));
            assertTrue(expected[4].equalsIgnoreCase(AWSAssertions.AppointmentType));
        }
    }
    
}
