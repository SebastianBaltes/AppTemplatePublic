package testutil;

import play.mvc.Result;


public class FormTupel<T extends funcy.Page> {
	public Result result; 
	public T page;
	
	public FormTupel() {
	}
	
	public FormTupel(Result _result) {
		result = _result;
	} 
	
	@Override
	public String toString() {
		return "result=" + result + ", page=" + page;
	}
	
}
