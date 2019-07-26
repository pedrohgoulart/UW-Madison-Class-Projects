/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2017 
// PROJECT:          p2
// FILE:             Test_JobList.java
//
// Authors: Jiang, Kyle, Pedro Henrique, Tushar, and Zachary.
// Author 1: Jiang Huiyu, hjiang94@wisc.edu, hjiang94, 001
// Author 2: Kyle Malinowski, kmalinowski2@wisc.edu, kmalinowski2, 001
// Author 3: Pedro Henrique Koeler Goulart, koelergoular@wisc.edu, koelergoular, 001
// Author 4: Tushar Narang, tnarang@wisc.edu, tnarang, 001
// Author 5: Zachary Lesavich, zlesavich@wisc.edu, zlesavich, 001
//
// ---------------- OTHER ASSISTANCE CREDITS 
// Persons: NA
//
// Online sources: NA
//////////////////////////// 80 columns wide //////////////////////////////////

import java.util.Iterator;

public class Test_JobList {

	//Counters to track how many tests are passed or failed.
	private static int tests_passed = 0;
	private static int tests_failed = 0;
	
	public static void main(String[] args)
	{
	
		//Test to see if a JobList can be created.
		test01_Create_JobList();
		
		//Test to see if you are able to create job list, and add items to the 
		//list.
		test02_Add_Items();
		
		//Test to see if removing items works. Also tests the isEmpty() method.
		test03_Add_Remove_Items();
		
		//Test if the iterator works correctly
		test04_Iterator();
		
		//Test if the contains() method works correctly.
		test05_Contains();
		
		System.out.println(tests_passed + " tests passed");
		System.out.println(tests_failed + " tests failed");
	}

	/**
	 * Test if a job list can be created, and verify it is empty upon creation.
	 * 
	 */
	private static void test01_Create_JobList() 
	{
		try 
		{
			
			//See if we are able to create a new JobList.
			JobList test = new JobList();
			
			tests_passed++;
			
			//Test to make sure the job list is empty upon creation.
			if(test.isEmpty())
			{
				tests_passed++;
			}
			else
			{
				tests_failed++;
				testFailOutput("JobList is not returning empty directly after creation.");
			}
			
			test = null;
		}
		catch (Exception e)
		{
			tests_failed -= 2;
			testFailOutput("Unable to create a new JobList.");
		}
	}
	
	/**
	 * Test the add method of the JobList.  Verify that items cannot be added
	 * to an index that is out of range, and that null items cannot be added.
	 */
	private static void test02_Add_Items()
	{
		try{
			//Create a new JobList, add a new job, attempt to add a job that is
			//outside the rance, and then try to add a null job.
			JobList listOfJobs = new JobList();
			
			listOfJobs.add(new Job("Create all of the things", 10, 50000));
			tests_passed++;
			
			try{
				listOfJobs.add(10000, new Job("hahah does this add?", 10, 1000));
				tests_failed++;
				testFailOutput("Add an out of bounds job.");
			}
			catch (IndexOutOfBoundsException e)
			{
				tests_passed++;
			}
			
			try{
				listOfJobs.add(0, null);
				tests_failed++;
				testFailOutput("Add a null job");
			}
			catch (IllegalArgumentException e)
			{
				tests_passed++;
			}
			
			
		}
		catch (Exception e)
		{
			tests_failed++;
			testFailOutput("Add items test");
		}
	}
	
	/**
	 * Verify the functionality of the Add and Remove methods of JobList.
	 * Verifies that these two functions work as intended.
	 */
	private static void test03_Add_Remove_Items()
	{
		try
		{
			//Add and remove items from a JobList.
			JobList listOfJobs = new JobList();
			listOfJobs.add(new Job("hi", 0, 1));
			listOfJobs.add(new Job("hello", 10, 50));
			listOfJobs.add(new Job("this is a test", 30, 10));
			listOfJobs.add(0, new Job("this should be first", 10, 30));
			if(listOfJobs.isEmpty()) throw new RuntimeException();
			Job tempJob = listOfJobs.remove(2);
			
			//Test that a job with a job name of "hello" is at position 2.
			if(tempJob.getJobName().equals("hello"))
			{
				tests_passed++;
			}
			else{
				tests_failed++;
				testFailOutput("Add and remove jobs");
			}
			
			//Test that a job with a job name of "this should be first" is at
			//position 0.
			tempJob = listOfJobs.remove(0);
			
			if(tempJob.getJobName().equals("this should be first"))
			{
				tests_passed++;
			}
			else
			{
				tests_failed++;
				testFailOutput("Add and remove Jobs");
			}
			
		}
		catch (Exception e)
		{
			tests_failed++;
			testFailOutput("Add and remove Jobs");

		}
	}
	
	/**
	 * Tests the functionality of the iterator, to verify that it works correctly.
	 */
	private static void test04_Iterator()
	{
		try {
			JobList listOfJobs = new JobList();
			listOfJobs.add(new Job("hi", 0, 1));
			listOfJobs.add(new Job("hello", 10, 50));
			listOfJobs.add(new Job("this is a test", 30, 10));
			listOfJobs.add(0, new Job("this should be first", 10, 30));
			
			//Get iterator, and verify that the first and last item returned by the iterator are correct.
			Iterator<Job> itr = listOfJobs.iterator();
			
			if(itr.next().getJobName().equals("this should be first"))
			{
				
				if(itr.next().getJobName().equals("hi"))
				{
					if(itr.next().getJobName().equals("hello"))
					{
						if(itr.next().getJobName().equals("this is a test"))
						{
							tests_passed++;
						}
						else
						{
							tests_failed++;
							testFailOutput("Iterator test, last item.");
						}
					}
					else
					{
						tests_failed++;
						testFailOutput("Iterator test, third item.");
					}
				}
				else
				{
					tests_failed++;
					testFailOutput("Iterator test, second item.");
				}
			
			}
			else
			{
				tests_failed++;
				testFailOutput("Iterator test, first item.");
			}
			
			itr = listOfJobs.iterator();
			
			//Verify that the hasNext function works correctly.
			while(itr.hasNext())
			{
				itr.next();
			}
			
		}
		catch (Exception e)
		{
			tests_failed++;
			testFailOutput("Iterator test, generic failure.");

		}
	}
	
	/**
	 * Test and verify the functionality of the contains method of JobList.
	 */
	private static void test05_Contains()
	{
		try
		{
			JobList list = new JobList();
			Job tempJ = new Job("Third job", 4, 2);
			Job temm = new Job("Third job", 4, 2);
			Job altJob = new Job("Third job", 4, 3);
			list.add(new Job("First job", 10, 3));
			list.add(new Job("Second job", 2, 4));
			list.add(tempJ);
			list.add(new Job("Fourth job", 1, 3));
			
			//Verify that the contains method returns true, when there is a job
			//passed with the same data, but a different address that it is referencing.
			if (list.contains(temm))
			{
				tests_passed++;
			}
			else
			{
				tests_failed++;
				testFailOutput("Contains test, could not find job that is in the list");
			}
			
			//Verifies that the contains method does not return true just if 
			//the names are equal.
			if (!list.contains(altJob))
			{
				tests_passed++;
			}
			else
			{
				tests_failed++;
				testFailOutput("Contains test, found job with the same name, but did not have the same work values");
			}
		}
		catch (Exception e)
		{
			tests_failed++;
			testFailOutput("Contains test, generic failure.");
		}
	}
	
	/**
	 * Method to make it easier to provide a consistent format for failure
	 * outputs, and to shorten the amount needed to type to output text for
	 * failures.
	 * @param test String containing text describing the failure that occurred.
	 */
	private static void testFailOutput(String test)
	{
		System.out.println("FAILED: " + test);
	}
	
}
