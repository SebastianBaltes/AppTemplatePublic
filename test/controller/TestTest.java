package controller;

import static play.test.Helpers.*;


/**
 *
 * 
 * for each controller: 
 * 	- assert all negative/positive cases by faking requests.
 * 		- database entries correct ? 
 * 		- check Form constraints ? 
 * 		- check for correct redirects after post 
 * 		- check for correct response code (homogeneous "badRequest"/"forbidden"
 * 			-> valid markup or simple String "forbidden" ? 
 * 				=> check response: content/type
 * 
 * 	- assert no database changes occured e.g. on invalid login ? HOW ? Mock Model ? 
 */

public class TestTest {

}
